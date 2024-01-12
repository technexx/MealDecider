package meal.decider

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

data class BoardValues(
    var squareList: SnapshotStateList<SquareValues> = mutableStateListOf(),
)

data class SquareValues(
    var name: String = "",
    var color: Int = 0
)

data class RestrictionsValues(
    var name: String = "",
    var selected: Boolean = false
)

object RestrictionsObject {
    var RestrictionsList : SnapshotStateList<RestrictionsValues> = mutableStateListOf(
        RestrictionsValues ("Vegan", false),
        RestrictionsValues ("Vegetarian", false),
        RestrictionsValues ("Gluten Free", false),
        RestrictionsValues ("Kosher", false),
        RestrictionsValues ("Halal", false),
    )
}

data class RestaurantValues(
    var name: String,
    var address: String,
    var distance: String,
    var price: Int
)

object RestaurantsObject {
    var RestaurantList: SnapshotStateList<RestaurantValues> = mutableStateListOf()
}

@Serializable
data class CuisineStuff(
    //We return a list of different object types in our Results data class. We were formerly just trying to pass in a List<String> rather than List<Results>.
    @SerializedName("results") var results : List<CuisineResults>? = null,
//    @SerializedName("location") var location : List<CuisineLocations>? = null,
//    @SerializedName("geometry") var location : List<CuisineLocations>? = null,
    )

@Serializable
data class CuisineResults (
    val name: String? = null,
    val vicinity: String? = null,
    val price_level: Int? = null,
)

@Serializable
data class CuisineLocations (
    val lat: Double? = null,
    val lng: Double? = null,
)