package meal.decider

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlin.random.Random

class Runnables (val appViewModel: AppViewModel) {
    var rolledSquareIndex = 0
    var rolledRestaurantIndex = 0
    var rollCountdown: Long = 1000

    private val handler = Handler(Looper.getMainLooper())
    private var cuisineRollRunnable = Runnable {}
    private var restaurantRollRunnable = Runnable {}
    private var cuisineBorderStrokeToggleRunnable = Runnable {}
    private var restaurantBorderStrokeToggleRunnable = Runnable {}

    fun rollCuisine() {
        var delay: Long = 100
        rollCountdown = 100

        appViewModel.updateRollEngaged(true)
        handler.removeCallbacks(cuisineRollRunnable)

        cuisineRollRunnable = Runnable {
            rolledSquareIndex = Random.nextInt(0, appViewModel.getSquareList.size)
            val newSquareList = squareListWithRandomColorChanged(rolledSquareIndex)
            appViewModel.updateSquareList(newSquareList)

            handler.postDelayed(cuisineRollRunnable, delay)
            if (delay > 100) delay -= 10
            rollCountdown -= 20

            if (rollCountdown < 20) {
                appViewModel.updateSelectedCuisineSquare(appViewModel.getSquareList[rolledSquareIndex])
                appViewModel.updateCuisineRollFinished(true)
                appViewModel.updateRollEngaged(false)
                appViewModel.cuisineStringUri = appViewModel.selectedCuisineSquare.value.name + " Food " + foodRestrictionsString(appViewModel.getRestrictionsList)
                handler.removeCallbacks(cuisineRollRunnable)
            }
        }

        handler.post((cuisineRollRunnable))
    }

    private fun squareListWithRandomColorChanged(index: Int): SnapshotStateList<SquareValues> {
        val currentList = appViewModel.getSquareList
        val newList = SnapshotStateList<SquareValues>()

        for (i in currentList) {
            newList.add(SquareValues(i.name, defaultSquareColor))
        }
        newList[index].color = chosenSquareColor

        return newList
    }

    fun rollRestaurant() {
        var delay: Long = 100
        rollCountdown = 100
        handler.removeCallbacks(restaurantRollRunnable)
        appViewModel.updateRollEngaged(true)

        restaurantRollRunnable = Runnable {
            rolledRestaurantIndex = Random.nextInt(0, appViewModel.getRestaurantList.size)
            val newRestaurantList = restaurantListWithRandomColorChanged(rolledRestaurantIndex)
            appViewModel.updateRestaurantsList(newRestaurantList)

            handler.postDelayed(restaurantRollRunnable, delay)
            if (delay > 100) delay -= 10
            rollCountdown -= 20

            if (rollCountdown < 20) {
                appViewModel.updateSelectedRestaurantSquare(appViewModel.getRestaurantList[rolledRestaurantIndex])
                appViewModel.updateRestaurantRollFinished(true)
                appViewModel.updateRollEngaged(false)
                handler.removeCallbacks(restaurantRollRunnable)
            }
        }

        handler.post(restaurantRollRunnable)
    }

    private fun restaurantListWithRandomColorChanged(index: Int): SnapshotStateList<RestaurantValues> {
        val currentList = appViewModel.getRestaurantList
        val newList = SnapshotStateList<RestaurantValues>()

        for (i in currentList) {
            newList.add(RestaurantValues(i.name, i.address, i.distance, i.priceLevel, i.rating, defaultSquareColor))
        }
        newList[index].color = chosenSquareColor

        return newList
    }

    fun cuisineBorderStrokeToggleAnimation() {
        handler.removeCallbacks(cuisineBorderStrokeToggleRunnable)
        appViewModel.updateCuisineSelectionBorderStroke(lightCuisineSelectionBorderStroke)

        cuisineBorderStrokeToggleRunnable = Runnable {
            if (appViewModel.getCuisineSelectionBorderStroke == lightCuisineSelectionBorderStroke) {
                appViewModel.updateCuisineSelectionBorderStroke(heavyCuisineSelectionBorderStroke)
            } else {
                appViewModel.updateCuisineSelectionBorderStroke(lightCuisineSelectionBorderStroke)
            }
            handler.postDelayed(cuisineBorderStrokeToggleRunnable, 200)
        }
        handler.post(cuisineBorderStrokeToggleRunnable)
    }

    fun cancelCuisineBorderStrokeToggleRunnable() { handler.removeCallbacks(cuisineBorderStrokeToggleRunnable) }

    fun resetCuisineSelectionBorderStroke() { appViewModel.updateCuisineSelectionBorderStroke(defaultCuisineSelectionBorderStroke) }

    fun restaurantBorderStrokeToggleAnimation() {
        handler.removeCallbacks(restaurantBorderStrokeToggleRunnable)
        appViewModel.updateCuisineSelectionBorderStroke(lightRestaurantSelectionBorderStroke)

        restaurantBorderStrokeToggleRunnable = Runnable {
            if (appViewModel.getRestaurantSelectionBorderStroke == lightRestaurantSelectionBorderStroke) {
                appViewModel.updateRestaurantSelectionBorderStroke(heavyRestaurantSelectionBorderStroke)
            } else {
                appViewModel.updateRestaurantSelectionBorderStroke(lightRestaurantSelectionBorderStroke)
            }
            handler.postDelayed(restaurantBorderStrokeToggleRunnable, 200)
        }

        handler.post(restaurantBorderStrokeToggleRunnable)
    }

    fun cancelRestaurantBorderStrokeToggleRunnable() { handler.removeCallbacks(restaurantBorderStrokeToggleRunnable) }

    fun resetRestaurantSelectionBorderStroke() { appViewModel.updateRestaurantSelectionBorderStroke(defaultRestaurantSelectionBorderStroke) }
}