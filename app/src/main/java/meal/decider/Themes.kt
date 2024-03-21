package meal.decider

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

object Theme {
    val themeColorsList: SnapshotStateList<ColorTheme> = mutableStateListOf(
        //Light
        ColorTheme(
            appBar = R.color.blue_400,
            iconButtons = R.color.white,
            restrictionRow = R.color.grey_100,
            cuisineBoard = R.color.grey_50,

            restaurantsIconButtons = R.color.blue_400,
            restaurantSquares = R.color.white,

            interactionButtonsRow = R.color.grey_700,
            interactionButtons = R.color.blue_400,
            interactionSquares = R.color.white,

            cancelDialogButton = android.R.color.holo_red_light,
            confirmDialogButton = android.R.color.holo_green_light
        ),

        //Dark
        ColorTheme(
            appBar = R.color.blue_grey_900,
            iconButtons = R.color.blue_grey_900,
            restrictionRow = R.color.grey_700,
            cuisineBoard = R.color.grey_800,

            restaurantsIconButtons = R.color.white,
            restaurantSquares = R.color.black,

            interactionButtonsRow = R.color.grey_700,
            interactionButtons = R.color.purple_700,
            interactionSquares = R.color.black,

            cancelDialogButton = android.R.color.holo_red_light,
            confirmDialogButton = android.R.color.holo_green_light
        )
    )
}