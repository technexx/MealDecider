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

            showLog("test", "json is $prettyJson")
            showLog("test", "serializable is $jsonSerialized")

//            println("return size is ${jsonSerialized.results?.size}")

            for (i in jsonSerialized.results!!) {
//                println("name is ${i.name}")
//                println("location is ${i.vicinity}")
//                println("price level is ${i.price_level}")
            }
        }
    }

    fun distanceOfRestaurantFromCurrentLocations(oldLat: Double, oldLong: Double, newLat: Double, newLong: Double) {
        val results: FloatArray = floatArrayOf(1f)
        Location.distanceBetween(oldLat, oldLong, newLat, newLong, results)
    }

    //TODO: Get distance based on long/lat return from json.
    fun sendSerializedJsonToRestaurantList(serializedResults: List<Any>) {
        val listToSend = mutableListOf<RestaurantValues>()
        for (i in serializedResults) {
//            listToSend.add(serializedResults)
        }
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