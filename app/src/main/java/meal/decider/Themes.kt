package meal.decider

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class ColorTheme(
    val appBar: Int,
    val dialogBackground: Int,
    val cuisineIconButtons: Int,
    val restrictionRow: Int,
    val cuisineBoard: Int,
    val cuisineSquares: Int,
    val cuisineSquaresText: Int,
    val selectedCuisineSquare: Int,
    val selectedCuisineIcon: Int,

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

    var cancelDialogButton: Int,
    var confirmDialogButton: Int,
)

object Theme {
    val themeColorsList: SnapshotStateList<ColorTheme> = mutableStateListOf(
        //Light
        ColorTheme(
            appBar = R.color.blue_400,
            dialogBackground = R.color.grey_50,
            cuisineIconButtons = R.color.white,
            restrictionRow = R.color.grey_100,
            cuisineBoard = R.color.grey_50,
            cuisineSquares = R.color.grey_200,
            cuisineSquaresText = R.color.black,
            selectedCuisineSquare = R.color.red_200,
            selectedCuisineIcon = R.color.red_a700,

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

            cancelDialogButton = android.R.color.holo_red_light,
            confirmDialogButton = android.R.color.holo_green_light
        ),

        //Dark
        ColorTheme(
            appBar = R.color.blue_grey_900,
            dialogBackground = R.color.grey_700,
            cuisineIconButtons = R.color.white,
            restrictionRow = R.color.grey_700,
            cuisineBoard = R.color.black,
            cuisineSquares = R.color.grey_800,
            cuisineSquaresText = R.color.white,
            selectedCuisineSquare = R.color.red_200,
            selectedCuisineIcon = R.color.red_a700,

            restaurantTopRow = R.color.grey_700,
            restaurantBoard = R.color.grey_800,
            restaurantsIconButtons = R.color.white,
            restaurantSquares = R.color.black,
            restaurantSquaresText = R.color.white,
            selectedRestaurantSquare = R.color.red_200,
            selectedRestaurantIcon = R.color.red_a700,

            cuisineInteractionButtonsRow = R.color.grey_700,
            restaurantInteractionButtonsRow = R.color.grey_700,
            interactionButtons = R.color.grey_200,
            interactionIcons = R.color.white,

            cancelDialogButton = android.R.color.holo_red_light,
            confirmDialogButton = android.R.color.holo_green_light
        )
    )
}