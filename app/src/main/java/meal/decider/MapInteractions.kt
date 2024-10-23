package meal.decider

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import meal.decider.Database.RoomInteractions

private lateinit var fusedLocationClient: FusedLocationProviderClient
private var currentLocation: Location = Location("")

class MapInteractions(private val activity: Activity, private val activityContext: Context, private val appViewModel: AppViewModel, private val roomInteractions: RoomInteractions) {

    val mainScope = CoroutineScope(Dispatchers.Main)

    data class PlacesResponse(
        val results: MutableList<Place>,
        val nextPageToken: String?,
        // ... other fields
    )

    data class Place(
        val name: String,
        val rating: Double,
        val price_level: Int
        // ... other fields
    )

    fun getJsonFromMapQuery(usingNextPageToken: Boolean) {
    }

    //While this works, we are using the web service method. We may want to switch over to Places object.
    suspend fun mapsApiCall() {
        withContext(Dispatchers.IO) {
            //Used in uri to filter results.
            val cuisineString = appViewModel.selectedCuisineSquare.value.name + " " + foodRestrictionsString(appViewModel.getRestrictionsList)
            val price = appViewModel.maxRestaurantPrice
            //Values filtered once retrieved from json result.
            val distance = appViewModel.maxRestaurantDistance
            val rating = appViewModel.minRestaurantRating
            val isOpen = appViewModel.isOpen

            var uri = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${currentLocation.latitude},${currentLocation.longitude}&fields=geometry, name, vicinity, price_level, opennow, rating&name=$cuisineString&maxprice=$price&rankby=distance&key=AIzaSyBi5VSm6f2mKgNgxaPLfUwV92uPtkYdvVI"

            val request = Request.Builder()
                .url(uri)
                .build()

            val response = OkHttpClient().newCall(request).execute().body().string()

            val gson = GsonBuilder().setPrettyPrinting().create()
            val prettyJson = gson.toJson(JsonParser.parseString(response))

            val json = Json { ignoreUnknownKeys = true }
            val jsonSerialized = json.decodeFromString<Root>(prettyJson)

            var restaurantList = restaurantResultListFromSerializedJson(jsonSerialized)
            restaurantList = filteredRestaurantList(restaurantList, distance, rating, price, isOpen)

            if (appViewModel.hasRestaurantListChanged(appViewModel.currentRestaurantList, restaurantList)) {
                appViewModel.currentRestaurantList = restaurantList

                if (!restaurantList.isEmpty()) {
                    appViewModel.updateSelectedRestaurantSquare(restaurantList[0])
                    appViewModel.updateSingleRestaurantColorAndBorder(restaurantList, 0, appViewModel.getColorTheme.selectedRestaurantSquare, heavyCuisineSelectionBorderStroke)
                    appViewModel.restaurantStringUri = restaurantList[0].name.toString() + " " + restaurantList[0].address
                }

//                for (i in restaurantList) {
//                    showLog("test", "restaurants are ${i.name}")
//                }
                appViewModel.updateRestaurantsList(restaurantList)
            }

            appViewModel.updateRestaurantQueryFinished(true)
        }
    }

    private fun filteredRestaurantList(list: SnapshotStateList<RestaurantValues>, distanceLimit: Double, minimumRating: Double, maxPrice: Int, isOpen: Boolean): SnapshotStateList<RestaurantValues> {
        val newList = SnapshotStateList<RestaurantValues>()

        for (i in list) {
            if (i.distance!! <= distanceLimit && i.rating!! >= minimumRating && i.priceLevel!! <= maxPrice) {
                newList.add(i)
                if (isOpen) {
                    if (i.isOpen != true) {
                        newList.remove(i)
                    }
                }
            }
        }

        return newList
    }

    private fun distanceOfRestaurantFromCurrentLocation(oldLat: Double?, oldLong: Double?, newLat: Double?, newLong: Double?): FloatArray {
        val results: FloatArray = floatArrayOf(1f)
        if (oldLat != null && oldLong != null && newLat != null && newLong != null)  {
            Location.distanceBetween(oldLat, oldLong, newLat, newLong, results)
        }
        return results
    }

    private fun restaurantResultListFromSerializedJson(result: Root): SnapshotStateList<RestaurantValues>{
        val restaurantList = mutableStateListOf<RestaurantValues>()
        for (i in result.results!!.indices) {
            val distance = floatArrayToDouble(distanceOfRestaurantFromCurrentLocation(currentLocation.latitude, currentLocation.longitude,
                result.results[i].geometry?.location?.lat, result.results[i].geometry?.location?.lng))
            restaurantList.add(RestaurantValues(result.results[i].name, result.results[i].vicinity, distance, result.results[i].price_level, result.results[i].rating,
                result.results[i].opening_hours?.open_now, appViewModel.getColorTheme.restaurantSquares)
            )
        }
        return restaurantList
    }

    fun dummyRestaurantList(): SnapshotStateList<RestaurantValues> {
        val listToReturn = mutableStateListOf<RestaurantValues>()
        var distance = 2000.0
        var rating = 3.0
        var price = 1
        for (i in 1..20) {
            distance += 1000; rating += 0.1; if (i%5==0 && price <4) price += 1
            listToReturn.add(RestaurantValues("So Good Restaurant With Way More Text Here It Is", "123 Bird Brain Lane", doubleMetersToMiles(distance), price, rating, true, appViewModel.getColorTheme.restaurantBoard))
        }
        return listToReturn
    }

    fun fusedLocationListener() {
        checkForLocationPermission()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location : Location? ->
                currentLocation = location!!
                println("location is $location")
            }
            .addOnFailureListener {
                println("location listener failed!")
            }
    }

    private fun checkForLocationPermission() {
        if (activityContext.checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
            PackageManager.PERMISSION_GRANTED) {
            return
        } else {
            ActivityCompat.requestPermissions(activity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                1)
            return
        }
    }

    fun mapIntent(string: String) {
        val uri = Uri.parse("geo:0,0?q=$string")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")

        activityContext.startActivity(intent)
    }
}

///NextPage for multiple sets of 20 results - currently getting null.

//////////////////
//val places = Gson().fromJson(response, MapInteractions.PlacesResponse::class.java)
//var nextPageToken = places.nextPageToken
//showLog("test", "nextPageToken is $nextPageToken")
//
//while (nextPageToken != null) {
//    val nextUri = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${currentLocation.latitude},${currentLocation.longitude}&fields=geometry, name, vicinity, price_level, rating&name=$cuisineString&maxprice=$price&rankby=distance&key=AIzaSyBi5VSm6f2mKgNgxaPLfUwV92uPtkYdvVI&pagetoken=$nextPageToken"
//
//    val nextRequestBuilder = Request.Builder()
//        .url(nextUri)
//        .get()
//    val nextResponse = OkHttpClient().newCall(nextRequestBuilder.build()).execute()
//    val nextResponseBody = nextResponse.body().string()
//    val nextPlaces = Gson().fromJson(nextResponseBody, MapInteractions.PlacesResponse::class.java)
//
//    places.results.addAll(nextPlaces.results)
//    nextPageToken = nextPlaces.nextPageToken
//
//    places.results.forEach { place ->
//        showLog("test", place.name)
//    }
//}
///////////////

///////////////////////////
//            showLog("test", "json is $prettyJson")
//            showLog("test", "serialized is ${jsonSerialized.results}")
//            showLog("test","token returned is ${jsonSerialized.next_page_token}")
//
//            /////////////
//            var token = jsonSerialized.next_page_token
//
//            while (token != null) {
//                val newUri = uri + "&pagetoken=$token"
//
//                Thread.sleep(2000)
//
//                val req = Request.Builder()
//                    .url(newUri)
//                    .build()
//
//                val resp = OkHttpClient().newCall(req).execute().body().string()
//
//                val gson2 = GsonBuilder().setPrettyPrinting().create()
//                val prettyJson2 = gson2.toJson(JsonParser.parseString(resp))
//                val jsonSerialized2 = json.decodeFromString<Root>(prettyJson2)
//
//                token = jsonSerialized2.next_page_token
//
//                showLog("test", "new json is $prettyJson2")
//                showLog("test", "$token")
//            }
/////////////////////