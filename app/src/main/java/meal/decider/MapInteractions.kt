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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json

private lateinit var fusedLocationClient: FusedLocationProviderClient
private var currentLocation: Location = Location("")

class MapInteractions(private val activity: Activity, private val activityContext: Context, private val appViewModel: AppViewModel) {

    suspend fun mapsApiCall() {
        withContext(Dispatchers.IO) {
            //Used in uri to filter results.
            val cuisineType = appViewModel.cuisineStringUri
            val price = appViewModel.maxRestaurantPrice
            //Values filtered once retrieved from json result.
            val distance = appViewModel.maxRestaurantDistance
            val rating = appViewModel.minRestaurantRating

            //Per docs, we want to use "findplacefromtext" instead of "nearbysearch" in order to filter results and minimize billing. We are getting unnecessary data right now, but also getting null exceptions when using other query.
            val uri = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${currentLocation.latitude},${currentLocation.longitude}&fields=geometry, name, vicinity, price_level, rating&name=$cuisineType&maxprice=$price&rankby=distance&key=AIzaSyBi5VSm6f2mKgNgxaPLfUwV92uPtkYdvVI"

            val request = Request.Builder()
                .url(uri)
                .build()

            val response = OkHttpClient().newCall(request).execute().body().string()
            val gson = GsonBuilder().setPrettyPrinting().create()
            val prettyJson = gson.toJson(JsonParser.parseString(response))

            val json = Json { ignoreUnknownKeys = true }
            val jsonSerialized = json.decodeFromString<Root>(prettyJson)

            var restaurantList = restaurantResultListFromSerializedJson(jsonSerialized)
            restaurantList = filteredDistanceAndRatingRestaurantList(restaurantList, distance, rating)

            appViewModel.updateRestaurantsList(restaurantList)
            appViewModel.updateSingleRestaurantColorAndBorder(0, chosenRestaurantColor, lightRestaurantSelectionBorderStroke)
            appViewModel.updateRestaurantQueryFinished(true)
        }
    }

    private fun filteredDistanceAndRatingRestaurantList(list: SnapshotStateList<RestaurantValues>, distanceLimit: Double, minimumRating: Double): SnapshotStateList<RestaurantValues> {
        val newList = SnapshotStateList<RestaurantValues>()
        for (i in list) {
            if (i.distance!! <= distanceLimit && i.rating!! >= minimumRating) newList.add(i)
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
            restaurantList.add(RestaurantValues(result.results[i].name, result.results[i].vicinity, distance,
                result.results[i].price_level, result.results[i].rating, R.color.grey_300)
            )
        }
        return restaurantList
    }

    fun testRestaurants() {
//        appViewModel.originalRestaurantList = dummyRestaurantList()
        appViewModel.updateRestaurantsList(dummyRestaurantList())
    }

    fun dummyRestaurantList(): SnapshotStateList<RestaurantValues> {
        val listToReturn = mutableStateListOf<RestaurantValues>()
        var distance = 2000.0
        var rating = 3.0
        var price = 1
        for (i in 1..20) {
            distance += 1000; rating += 0.1; if (i%5==0 && price <4) price += 1
            listToReturn.add(RestaurantValues("So Good Restaurant With Way More Text Here It Is", "123 Bird Brain Lane", doubleMetersToMiles(distance), price, rating, defaultRestaurantColor))
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