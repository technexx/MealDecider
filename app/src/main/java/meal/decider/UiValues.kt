package meal.decider

import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object ColorTheme {
}

data class ThemeColors(
    val appBar: Int = R.color.blue_600,
    val iconButtons: Int = R.color.white,
    val restrictionRow: Int = R.color.grey_100,
    val cuisineBoard: Int = R.color.grey_50,

    val restaurantsIconButtons: Int = R.color.black,
    val restaurantSquares: Int = R.color.white,

    val interactionButtonsRow: Int = R.color.grey_700,
    val interactionButtons: Int = R.color.blue_400,
    val interactionSquares: Int = R.color.white,
)


data class DarkThemeColors(
    val appBar: Int = R.color.blue_grey_900,
    val iconButtons: Int = R.color.blue_grey_900,
    val restrictionRow: Int = R.color.grey_700,
    val cuisineBoard: Int = R.color.grey_800,

    val restaurantsIconButtons: Int = R.color.white,
    val restaurantSquares: Int = R.color.black,

    val interactionButtonsRow: Int = R.color.grey_700,
    val interactionButtons: Int = R.color.purple_700,
    val interactionSquares: Int = R.color.black,
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