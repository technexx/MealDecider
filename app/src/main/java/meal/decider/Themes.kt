package meal.decider

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

data class ColorTheme(
    val appBar: Int,
    val cuisineIconButtons: Int,
    val restrictionRow: Int,
    val cuisineBoard: Int,
    val cuisineSquares: Int,
    val cuisineSquaresText: Int,
    val selectedCuisineSquare: Int,
    val selectedCuisineIcon: Int,
    val selectedEditSquareColor: Int,
    val defaultCuisineBorderStroke: BorderStroke,
    val cuisineEditModeBorderStroke: BorderStroke,

    val restaurantTopRow: Int,
    val restaurantBoard: Int,
    val restaurantsIconButtons: Int,
    val restaurantSquares: Int,
    val restaurantSquaresText: Int,
    val selectedRestaurantSquare: Int,
    val selectedRestaurantIcon: Int,

    val cuisineInteractionButtonsRow: Int,
    val restaurantInteractionButtonsRow: Int,
    val interactionButtons: Int,
    val interactionIcons: Int,

    val dialogBackground: Int,
    val dropDownMenuBackground: Int,
    val dialogTextColor: Int,
    val dialogTextHighlight: Int,
    val textBoxBackground: Int,
    var cancelDialogButton: Int,
    var confirmDialogButton: Int,
    var circleSelectionColor: Int,

    var settingsText: Int
)

object Theme {
    val themeColorsList: SnapshotStateList<ColorTheme> = mutableStateListOf(
        //Light
        ColorTheme(
            appBar = R.color.blue_400,
            cuisineIconButtons = R.color.white,
            restrictionRow = R.color.grey_100,
            cuisineBoard = R.color.grey_50,
            cuisineSquares = R.color.grey_200,
            cuisineSquaresText = R.color.black,
            selectedCuisineSquare = R.color.red_200,
            selectedCuisineIcon = R.color.red_a700,
            selectedEditSquareColor = R.color.light_blue_100,
            defaultCuisineBorderStroke = BorderStroke(1.dp, Color.Black),
            cuisineEditModeBorderStroke = BorderStroke(3.dp, Color.Black),

            restaurantTopRow = R.color.blue_400,
            restaurantBoard = R.color.grey_50,
            restaurantsIconButtons = R.color.white,
            restaurantSquares = R.color.grey_100,
            restaurantSquaresText = R.color.black,
            selectedRestaurantSquare = R.color.red_200,
            selectedRestaurantIcon = R.color.red_a700,

            cuisineInteractionButtonsRow = R.color.grey_200,
            restaurantInteractionButtonsRow = R.color.grey_200,
            interactionButtons = R.color.blue_400,
            interactionIcons = R.color.blue_400,

            dialogBackground = R.color.grey_50,
            dropDownMenuBackground = R.color.grey_50,
            dialogTextColor = R.color.black,
            dialogTextHighlight = R.color.grey_400,
            textBoxBackground = R.color.grey_300,
            cancelDialogButton = android.R.color.holo_red_light,
            confirmDialogButton = android.R.color.holo_green_light,
            circleSelectionColor = R.color.black,

            settingsText = R.color.black
        ),

        //Dark
        ColorTheme(
            appBar = R.color.blue_grey_900,
            cuisineIconButtons = R.color.white,
            restrictionRow = R.color.grey_700,
            cuisineBoard = R.color.black,
            cuisineSquares = R.color.grey_100,
            cuisineSquaresText = R.color.black,
            selectedCuisineSquare = R.color.red_200,
            selectedCuisineIcon = R.color.red_a700,
            selectedEditSquareColor = R.color.light_blue_100,
            defaultCuisineBorderStroke = BorderStroke(1.dp, Color.White),
            cuisineEditModeBorderStroke = BorderStroke(3.dp, Color(R.color.red_200)),

            restaurantTopRow = R.color.grey_700,
            restaurantBoard = R.color.grey_800,
            restaurantsIconButtons = R.color.white,
            restaurantSquares = R.color.grey_100,
            restaurantSquaresText = R.color.black,
            selectedRestaurantSquare = R.color.red_200,
            selectedRestaurantIcon = R.color.red_a700,

            cuisineInteractionButtonsRow = R.color.grey_700,
            restaurantInteractionButtonsRow = R.color.grey_700,
            interactionButtons = R.color.grey_200,
            interactionIcons = R.color.white,

            dialogBackground = R.color.grey_800,
            dropDownMenuBackground = R.color.grey_700,
            dialogTextColor = R.color.white,
            dialogTextHighlight = R.color.grey_500,
            textBoxBackground = R.color.grey_50,
            cancelDialogButton = android.R.color.holo_red_light,
            confirmDialogButton = android.R.color.holo_green_light,
            circleSelectionColor = R.color.white,

            settingsText = R.color.white
        )
    )
}