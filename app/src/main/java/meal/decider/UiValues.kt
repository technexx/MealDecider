package meal.decider

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

//TODO: Return this to a state view list in view model.
fun changeColorTheme(theme: String): ColorTheme {
    var themeToReturn = Theme.themeColorsList[0]
    if (theme == "light") {
        themeToReturn = Theme.themeColorsList[0]
    }
    if (theme == "dark") {
        themeToReturn = Theme.themeColorsList[1]
    }
    return themeToReturn
}

object Theme {
    val themeColorsList: SnapshotStateList<ColorTheme> = mutableStateListOf(
        ColorTheme(
            appBar = R.color.blue_400,
            iconButtons = R.color.white,
            restrictionRow = R.color.grey_100,
            cuisineBoard = R.color.grey_50,

            restaurantsIconButtons = R.color.blue_400,
            restaurantSquares = R.color.white,

            interactionButtonsRow = R.color.grey_700,
            interactionButtons = R.color.blue_400,
            interactionSquares = R.color.white
        ),

        ColorTheme(
            appBar = R.color.blue_grey_900,
            iconButtons = R.color.blue_grey_900,
            restrictionRow = R.color.grey_700,
            cuisineBoard = R.color.grey_800,

            restaurantsIconButtons = R.color.white,
            restaurantSquares = R.color.black,

            interactionButtonsRow = R.color.grey_700,
            interactionButtons = R.color.purple_700,
            interactionSquares = R.color.black
        )
    )
}

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