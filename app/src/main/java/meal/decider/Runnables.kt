package meal.decider

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlin.random.Random

class Runnables (val appViewModel: AppViewModel) {
    private val handler = Handler(Looper.getMainLooper())
    private var cuisineRollRunnable = Runnable {}
    private var restaurantRollRunnable = Runnable {}
    private var cuisineBorderStrokeToggleRunnable = Runnable {}
    private var restaurantBorderStrokeToggleRunnable = Runnable {}
    var rollCountdown: Long = 1000

    fun rollCuisine() {
        var delay: Long = 100
        rollCountdown = 100

        appViewModel.updateRollEngaged(true)
        handler.removeCallbacks(cuisineRollRunnable)

        cuisineRollRunnable = Runnable {
            appViewModel.rolledSquareIndex = Random.nextInt(0, appViewModel.getSquareList.size)
            val newSquareList = squareListWithRandomColorChanged(appViewModel.rolledSquareIndex)
            appViewModel.updateSquareList(newSquareList)

            handler.postDelayed(cuisineRollRunnable, delay)
            if (delay > 100) delay -= 10
            rollCountdown -= 20

            if (rollCountdown < 20) {
                appViewModel.updateSelectedCuisineSquare(appViewModel.getSquareList[appViewModel.rolledSquareIndex])

                appViewModel.updateCuisineRollFinished(true)
                appViewModel.updateRollEngaged(false)
                appViewModel.updateCuisineStringUriAndHasChangedBoolean(appViewModel.selectedCuisineSquare.value.name + " Food " + foodRestrictionsString(appViewModel.getRestrictionsList))
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
            appViewModel.rolledRestaurantIndex = Random.nextInt(0, appViewModel.getRestaurantList.size)
            val newRestaurantList = restaurantListWithRandomColorChanged(appViewModel.rolledRestaurantIndex)
            appViewModel.updateRestaurantsList(newRestaurantList)

            handler.postDelayed(restaurantRollRunnable, delay)
            if (delay > 100) delay -= 10
            rollCountdown -= 20

            if (rollCountdown < 20) {
                appViewModel.updateSelectedRestaurantSquare(appViewModel.getRestaurantList[appViewModel.rolledRestaurantIndex])
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

    //New lists always have to be created when dealing w/ live data. Edited lists (even of snapshot lists) will not trigger recomposition.
    fun cuisineBorderStrokeToggleAnimation(duration: Long, delay: Long) {
        handler.removeCallbacks(cuisineBorderStrokeToggleRunnable)
        //Odd number of iterations so we end on heavy border.
        var iterations = duration / delay + 1

        cuisineBorderStrokeToggleRunnable = Runnable {
            if (iterations > 0) {
                val squareList = appViewModel.getSquareList
                val newSquareList: SnapshotStateList<SquareValues> = mutableStateListOf()
                newSquareList.addAll(squareList)
                val selectedSquare = newSquareList[appViewModel.rolledSquareIndex]

                if (selectedSquare.border == defaultCuisineBorderStroke) {
                    selectedSquare.border = heavyCuisineSelectionBorderStroke
                } else {
                    selectedSquare.border = defaultCuisineBorderStroke
                }

                newSquareList[appViewModel.rolledSquareIndex] = selectedSquare
                appViewModel.updateSquareList(newSquareList)
                iterations -=1

                handler.postDelayed(cuisineBorderStrokeToggleRunnable, delay)
            }
        }
        handler.post(cuisineBorderStrokeToggleRunnable)
    }

    fun restaurantBorderStrokeToggleAnimation(duration: Long, delay: Long) {
        handler.removeCallbacks(restaurantBorderStrokeToggleRunnable)
        var iterations = duration / delay + 1

        restaurantBorderStrokeToggleRunnable = Runnable {
            if (iterations > 0) {
                val restaurantList = appViewModel.getRestaurantList
                val newRestaurantList: SnapshotStateList<RestaurantValues> = mutableStateListOf()
                newRestaurantList.addAll(restaurantList)
                val selectedRestaurant = newRestaurantList[appViewModel.rolledRestaurantIndex]

                if (selectedRestaurant.border == defaultRestaurantBorderStroke) {
                    selectedRestaurant.border = heavyRestaurantSelectionBorderStroke
                } else {
                    selectedRestaurant.border = defaultRestaurantBorderStroke
                }

                newRestaurantList[appViewModel.rolledRestaurantIndex] = selectedRestaurant
                appViewModel.updateRestaurantsList(newRestaurantList)
                iterations -= 1

                handler.postDelayed(restaurantBorderStrokeToggleRunnable, delay)
            }
        }
        handler.post(restaurantBorderStrokeToggleRunnable)
    }

    fun cancelRestaurantBorderStrokeToggleRunnable() { handler.removeCallbacks(restaurantBorderStrokeToggleRunnable) }
}