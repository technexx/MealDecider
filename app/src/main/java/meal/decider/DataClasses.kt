package meal.decider

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.serialization.Serializable

data class SettingsDialogVisibility(
    var speeds: Boolean = false,
    var colors: Boolean = false,
    var sounds: Boolean = false
)

data class SettingsToggle(
    var name: String = "",
    var selected: Boolean = false,
)

object ColorSettingsToggleObject {
    var colorSettingsToggleList: SnapshotStateList<SettingsToggle> = mutableStateListOf(
        SettingsToggle("Light", true),
        SettingsToggle("Dark", false)
    )
}

data class BoardValues(
    var squareList: SnapshotStateList<SquareValues> = mutableStateListOf(),
)

data class SquareValues(
    var name: String = "",
    var color: Int = 0,
    var border: BorderStroke = defaultCuisineBorderStroke
)

data class RestrictionsValues(
    var name: String = "",
    var selected: Boolean = false,
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
    var name: String? = "",
    var address: String? = "",
    var distance: Double? = 0.0,
    var priceLevel: Int? = 0,
    var rating: Double? = 0.0,
    var color: Int? = 0,
    var border: BorderStroke = defaultRestaurantBorderStroke
): Comparable<RestaurantValues> {
    override fun compareTo(other: RestaurantValues): Int {
        return compareValuesBy(this, other, { it.name }, { it.distance }, {it.rating })
    }
}

object RestaurantsObject {
    var RestaurantList: SnapshotStateList<RestaurantValues> = mutableStateListOf()
}

//Note how after the initial List<Result> we pass in the classes, not a list of classes.
@Serializable
data class Root(
    val results: List<Result>? = null,
)

@Serializable
data class Result(
    val geometry: Geometry? = null,
    val name: String? = null,
    val vicinity: String? = null,
    val price_level: Int? = null,
    val rating: Double? = null,
)

@Serializable
data class Geometry(
    val location: Location? = null,
)

@Serializable
data class Location(
    val lat: Double? = null,
    val lng: Double? = null,
)