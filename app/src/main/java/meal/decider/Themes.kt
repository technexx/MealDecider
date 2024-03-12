package meal.decider

object ThemeObject {
    var appBar: Int = R.color.blue_600
    var iconButtons: Int = R.color.white
    var restrictionRow: Int = R.color.grey_100
    var cuisineBoard: Int = R.color.grey_50

    var restaurantsIconButtons: Int = R.color.black
    var restaurantSquares: Int = R.color.white

    var interactionButtonsRow: Int = R.color.grey_700
    var interactionButtons: Int = R.color.blue_400
    var interactionSquares: Int = R.color.white

    var cancelDialogButton: Int = android.R.color.holo_red_light
    var confirmDialogButton: Int = android.R.color.holo_green_light
}

fun updateThemeObject(theme: String) {
    if (theme == "light") {
        ThemeObject.appBar = R.color.blue_600
        ThemeObject.iconButtons = R.color.white
        ThemeObject.restrictionRow = R.color.grey_100
        ThemeObject.cuisineBoard = R.color.grey_50

        ThemeObject.restaurantsIconButtons = R.color.black
        ThemeObject.restaurantSquares = R.color.white

        ThemeObject.interactionButtonsRow = R.color.grey_700
        ThemeObject.interactionButtons = R.color.blue_400
        ThemeObject.interactionSquares = R.color.white

        ThemeObject.cancelDialogButton = android.R.color.holo_red_light
        ThemeObject.confirmDialogButton = android.R.color.holo_green_light
    }

    if (theme == "dark") {
        ThemeObject.appBar = R.color.blue_grey_900
        ThemeObject.iconButtons = R.color.blue_grey_900
        ThemeObject.restrictionRow = R.color.grey_700
        ThemeObject.cuisineBoard = R.color.grey_800

        ThemeObject.restaurantsIconButtons = R.color.white
        ThemeObject.restaurantSquares = R.color.black

        ThemeObject.interactionButtonsRow = R.color.grey_700
        ThemeObject.interactionButtons = R.color.purple_700
        ThemeObject.interactionSquares = R.color.black

        ThemeObject.cancelDialogButton = android.R.color.holo_red_light
        ThemeObject.confirmDialogButton = android.R.color.holo_green_light
    }
}