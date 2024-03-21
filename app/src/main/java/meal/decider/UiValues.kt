package meal.decider

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class ColorTheme(
    val appBar: Int,
    val iconButtons: Int,
    val restrictionRow: Int,
    val cuisineBoard: Int,

    val restaurantsIconButtons: Int,
    val restaurantSquares: Int,

    val interactionButtonsRow: Int,
    val interactionButtons: Int,
    val interactionSquares: Int,
)

val defaultSquareColor = R.color.white
val chosenSquareColor = R.color.red_200
val editSquareColor = R.color.light_blue_100

val defaultCuisineBorderStroke = BorderStroke(1.dp, Color.Black)
val lightCuisineSelectionBorderStroke = BorderStroke(1.dp, Color.Red)
val heavyCuisineSelectionBorderStroke = BorderStroke(3.dp, Color.Red)
val cuisineEditModeBorderStroke = BorderStroke(3.dp, Color.Black)

val defaultRestaurantColor = R.color.grey_300
val chosenRestaurantColor = R.color.red_200
val defaultRestaurantBorderStroke = BorderStroke(1.dp, Color.Black)
val lightRestaurantSelectionBorderStroke = BorderStroke(1.dp, Color.Red)
val heavyRestaurantSelectionBorderStroke = BorderStroke(3.dp, Color.Red)