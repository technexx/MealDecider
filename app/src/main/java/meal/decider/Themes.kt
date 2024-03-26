package meal.decider

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class ColorTheme(
    val appBar: Int,
    val cuisineIconButtons: Int,
    val restrictionRow: Int,
    val cuisineBoard: Int,
    val cuisineSquares: Int,
    val selectedCuisineSquare: Int,

    val restaurantTopRow: Int,
    val restaurantBoard: Int,
    val restaurantsIconButtons: Int,
    val restaurantSquares: Int,
    val selectedRestaurantSquare: Int,

    val cuisineInteractionButtonsRow: Int,
    val restaurantInteractionButtonsRow: Int,
    val interactionButtons: Int,
    val interactionSquares: Int,
    val interactionIcons: Int,

    var cancelDialogButton: Int,
    var confirmDialogButton: Int,
)

object Theme {
    val themeColorsList: SnapshotStateList<ColorTheme> = mutableStateListOf(
        //Light
        ColorTheme(
            appBar = R.color.blue_400,
            cuisineIconButtons = R.color.white,
            restrictionRow = R.color.grey_100,
            cuisineBoard = R.color.grey_50,
            cuisineSquares = R.color.white,
            selectedCuisineSquare = R.color.red_200,

            restaurantTopRow = R.color.grey_200,
            restaurantBoard = R.color.grey_50,
            restaurantsIconButtons = R.color.blue_400,
            restaurantSquares = R.color.white,
            selectedRestaurantSquare = R.color.red_200,

            cuisineInteractionButtonsRow = R.color.grey_200,
            restaurantInteractionButtonsRow = R.color.grey_200,
            interactionButtons = R.color.blue_400,
            interactionSquares = R.color.white,
            interactionIcons = R.color.blue_400,

            cancelDialogButton = android.R.color.holo_red_light,
            confirmDialogButton = android.R.color.holo_green_light
        ),

        //Dark
        ColorTheme(
            appBar = R.color.blue_grey_900,
            cuisineIconButtons = R.color.blue_grey_900,
            restrictionRow = R.color.grey_700,
            cuisineBoard = R.color.black,
            cuisineSquares = R.color.grey_800,
            selectedCuisineSquare = R.color.red_200,

            restaurantTopRow = R.color.grey_200,
            restaurantBoard = R.color.grey_800,
            restaurantsIconButtons = R.color.white,
            restaurantSquares = R.color.black,
            selectedRestaurantSquare = R.color.red_200,

            cuisineInteractionButtonsRow = R.color.grey_700,
            restaurantInteractionButtonsRow = R.color.grey_200,
            interactionButtons = R.color.grey_200,
            interactionSquares = R.color.black,
            interactionIcons = R.color.black,

            cancelDialogButton = android.R.color.holo_red_light,
            confirmDialogButton = android.R.color.holo_green_light
        )
    )
}