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
import com.google.gson.annotations.SerializedName
import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private lateinit var fusedLocationClient: FusedLocationProviderClient
private var currentLocation: Location = Location("")

//TODO: Should limit the amount of info returned for billing purposes, i.e. just what we want to use.
class MapInteractions(private val activity: Activity, private val activityContext: Context) {
    suspend fun makeApiCall() {
        withContext(Dispatchers.IO) {
            println("lat is ${currentLocation.latitude}")
            println("long is ${currentLocation.longitude}")

            val uri = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=${currentLocation.latitude},${currentLocation.longitude}&radius=2000&type=restaurant&key=AIzaSyBi5VSm6f2mKgNgxaPLfUwV92uPtkYdvVI"

            val request = Request.Builder()
                .url(uri)
                .build()

            val response = OkHttpClient().newCall(request).execute().body().string()
            val gson = GsonBuilder().setPrettyPrinting().create()
            val prettyJson = gson.toJson(JsonParser.parseString(response))

            val json = Json { ignoreUnknownKeys = true }
            val jsonSerialized = json.decodeFromString<CuisineStuff>(prettyJson)

            println("json is $prettyJson")
            println("serializable is $jsonSerialized")

//            showLog("test", prettyJson)
//            for (i in jsonSerialized.results!!) {
//                println("name is ${i.name}")
//                println("location is ${i.vicinity}")
//                println("price level is ${i.price_level}")
//            }
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

    @Serializable
    data class CuisineStuff(
        //We return a list of different object types in our Results data class. We were formerly just trying to pass in a List<String> rather than List<Results>.
        @SerializedName("results") var results : List<Results>? = null,
    )

    @Serializable
    data class Results (
        val name: String? = null,
        val vicinity: String? = null,
        val price_level: Int? = null,
    )

    fun mapIntent(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW, uri)
        intent.setPackage("com.google.android.apps.maps")

        activityContext.startActivity(intent)
    }
}