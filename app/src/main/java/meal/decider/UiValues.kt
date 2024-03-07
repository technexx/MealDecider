package meal.decider

import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class LightThemeColors(
    val appBarLight: Int = R.color.blue_600,
    val iconButtonsLight: Int = R.color.white
    val restrictionRowLight: Int = R.color.grey_100,
    val cuisineBoardLight: Int = R.color.grey_50,
    val cuisineButtonsRowLight: Int = R.color.grey_200,

    val restaurantsIconButtonsLight: Int = R.color.black,
    val restaurantSquaresLight: Int = R.color.white,

    val interactionButtonsLight: Int = R.color.blue_400,
    val interactionsSquaresLight: Int = R.color.white,
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