package meal.decider

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.net.Uri
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
import java.math.BigDecimal
import java.math.RoundingMode

private lateinit var fusedLocationClient: FusedLocationProviderClient
private var currentLocation: Location = Location("")

//TODO: Should limit the amount of info returned for billing purposes, i.e. just what we want to use.
class MapInteractions(private val activity: Activity, private val activityContext: Context, val viewModel: AppViewModel) {

    var cuisineType = ""

    suspend fun makeApiCall() {
        withContext(Dispatchers.IO) {
//            val uri = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${currentLocation.latitude},${currentLocation.longitude}&radius=2000&type=restaurant&key=AIzaSyBi5VSm6f2mKgNgxaPLfUwV92uPtkYdvVI"

            val uri = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${currentLocation.latitude},${currentLocation.longitude}&radius=2000&name=$cuisineType&key=AIzaSyBi5VSm6f2mKgNgxaPLfUwV92uPtkYdvVI"

            val request = Request.Builder()
                .url(uri)
                .build()

            val response = OkHttpClient().newCall(request).execute().body().string()
            val gson = GsonBuilder().setPrettyPrinting().create()
            val prettyJson = gson.toJson(JsonParser.parseString(response))

            val json = Json { ignoreUnknownKeys = true }
            val jsonSerialized = json.decodeFromString<Root>(prettyJson)

//            showLog("test", "json is $prettyJson")
            showLog("test", "serializable is $jsonSerialized")

            val restaurantList = restaurantResultListFromSerializedJson(jsonSerialized)
            showLog("test", "restaurant list is $restaurantList")

            for (i in jsonSerialized.results!!) {
//                println("name is ${i.name}")
//                println("location is ${i.vicinity}")
//                println("price level is ${i.price_level}")
            }
        }
    }

    private fun distanceOfRestaurantFromCurrentLocation(oldLat: Double?, oldLong: Double?, newLat: Double?, newLong: Double?): FloatArray {
        val results: FloatArray = floatArrayOf(1f)
        if (oldLat != null && oldLong != null && newLat != null && newLong != null)  {
            Location.distanceBetween(oldLat, oldLong, newLat, newLong, results)
        }

        return results
    }

    private fun restaurantResultListFromSerializedJson(result: Root): List<RestaurantValues>{
        val restaurantList = mutableListOf<RestaurantValues>()
        for (i in result.results!!.indices) {
            val distance = metersToMiles(distanceOfRestaurantFromCurrentLocation(currentLocation.latitude, currentLocation.longitude,
                result.results[i].geometry?.location?.lat, result.results[i].geometry?.location?.lng))
            restaurantList.add(RestaurantValues(result.results[i].name, result.results[i].vicinity, distance,
                result.results[i].price_level, result.results[i].rating)
            )
        }
        return restaurantList
    }

    private fun metersToMiles(meters: FloatArray): Double {
        val miles = (meters[0] * .00062137)
        val roundedMiles = BigDecimal(miles).setScale(1, RoundingMode.DOWN)
        return roundedMiles.toDouble()
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
                println("nope!")
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

    fun mapIntent(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")

        activityContext.startActivity(intent)
    }
}