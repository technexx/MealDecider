package meal.decider.Extra

import android.util.Log
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.model.PlaceTypes
import com.google.android.libraries.places.api.model.RectangularBounds
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse
import java.util.Locale

//class ExtraFunctions {
//    //TODO: Seems limited to 5 places, likely due to billing.
//    fun mapPlaces() {
//        Places.initialize(appContext, "AIzaSyBi5VSm6f2mKgNgxaPLfUwV92uPtkYdvVI", Locale.US)
//
//        //TODO: Get Place IDs of autocompleted searches.
//
//        val placeId = ""
//        val placeFields = listOf(Place.Field.ID, Place.Field.NAME)
//        val req = FetchPlaceRequest.newInstance(placeId, placeFields)
//
//        val placesClient = Places.createClient(activityContext)
//        placesClient.fetchPlace(req)
//
//        val token = AutocompleteSessionToken.newInstance()
//        val bounds = RectangularBounds.newInstance(
//            LatLng(-33.880490, 151.184363),
//            LatLng(-33.858754, 151.229596)
//        )
//
//        val request =
//            FindAutocompletePredictionsRequest.builder()
//                // Call either setLocationBias() OR setLocationRestriction().
////            .setLocationBias(bounds)
////            .setLocationRestriction(bounds)
//                .setOrigin(LatLng(-34.079190, 118.336552))
//                .setTypesFilter(listOf(PlaceTypes.RESTAURANT))
//                .setSessionToken(token)
//                .setQuery("thai")
//                .build()
//
//        placesClient.findAutocompletePredictions(request)
//            .addOnSuccessListener { response: FindAutocompletePredictionsResponse ->
//                for (prediction in response.autocompletePredictions) {
////                Log.i("test", prediction.placeId)
//                    Log.i("test", prediction.getPrimaryText(null).toString())
//                    Log.i("test", prediction.distanceMeters!!.toString())
//                }
//            }.addOnFailureListener { exception: Exception? ->
//                if (exception is ApiException) {
//                    Log.i("test", "Place not found: ${exception.statusCode}")
//                }
//            }
//    }