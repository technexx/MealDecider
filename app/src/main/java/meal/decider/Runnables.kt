package meal.decider

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlin.random.Random

class Runnables (private val appViewModel: AppViewModel) {
    private val handler = Handler(Looper.getMainLooper())
    private var cuisineRollRunnable = Runnable {}
    private var restaurantRollRunnable = Runnable {}
    private var cuisineBorderStrokeToggleRunnable = Runnable {}
    private var restaurantBorderStrokeToggleRunnable = Runnable {}

    private var timeRunnable = Runnable {}
    private var elapsedTime: Long = 0

    fun timer() {
        val startingTime = System.currentTimeMillis()

        timeRunnable = Runnable {
            val currentTime = System.currentTimeMillis()
            elapsedTime = currentTime - startingTime
            handler.postDelayed(timeRunnable, 200)

            if (elapsedTime >= appViewModel.cuisineRollDurationSetting * 1000) {
                handler.removeCallbacks(timeRunnable)
            }
        }
        handler.post(timeRunnable)
    }

    fun rollCuisine() {
        appViewModel.updateRollEngaged(true)
        appViewModel.updateSingleCuisineSquareColorAndBorder(appViewModel.rolledSquareIndex, appViewModel.getColorTheme.cuisineSquares, lightRestaurantSelectionBorderStroke)
        handler.removeCallbacks(cuisineRollRunnable)

        var delay: Long
        val durationSetting = appViewModel.cuisineRollDurationSetting
        val speedSetting = appViewModel.cuisineRollSpeedSetting
        var duration = rollDurationSettingToMillis(durationSetting)

        cuisineRollRunnable = Runnable {
            appViewModel.rolledSquareIndex = randomNonRepeatingRoll(appViewModel.rolledSquareIndex, appViewModel.getSquareList.size)

            val newSquareList = squareListWithRandomColorChanged(appViewModel.rolledSquareIndex)
            appViewModel.updateSquareList(newSquareList)

            delay = modifierDelay(duration, speedSetting)
            duration -= delay
            showLog("test", "duration is $duration")

            handler.postDelayed(cuisineRollRunnable, delay)

            if (duration < 50 || delay < 35) {
                val index = appViewModel.rolledSquareIndex

                appViewModel.updateSingleCuisineSquareColorAndBorder(index, appViewModel.getSquareList[index].color, heavyCuisineSelectionBorderStroke)

                appViewModel.updateCuisineRollFinished(true)
                appViewModel.updateRollEngaged(false)
                appViewModel.updateCuisineStringUriAndHasChangedBoolean(appViewModel.selectedCuisineSquare.value.name + " Food " + foodRestrictionsString(appViewModel.getRestrictionsList))

                appViewModel.updateSelectedCuisineSquare(appViewModel.getSquareList[appViewModel.rolledSquareIndex])

                handler.removeCallbacks(cuisineRollRunnable)
            }
        }
        handler.post((cuisineRollRunnable))
    }

    private fun squareListWithRandomColorChanged(index: Int): SnapshotStateList<SquareValues> {
        val currentList = appViewModel.getSquareList
        val newList = SnapshotStateList<SquareValues>()

        for (i in currentList) {
            newList.add(SquareValues(i.name, appViewModel.getColorTheme.cuisineSquares))
        }
        newList[index].color = appViewModel.getColorTheme.selectedCuisineSquare
        newList[index].border = heavyCuisineSelectionBorderStroke

        return newList
    }

    fun rollRestaurant() {
        appViewModel.updateRollEngaged(true)
        appViewModel.updateSingleRestaurantColorAndBorder(appViewModel.getRestaurantList, appViewModel.rolledRestaurantIndex, appViewModel.getColorTheme.selectedRestaurantSquare, lightRestaurantSelectionBorderStroke)
        handler.removeCallbacks(restaurantRollRunnable)

//        var delay: Long
//        val durationSetting = appViewModel.restaurantRollDurationSetting
//        var speedSetting = appViewModel.restaurantRollSpeedSetting
//        var duration = rollDurationSettingToMillis(durationSetting)

        //Using same roll for cuisines as restaurants here, to simplify.
        var delay: Long
        val durationSetting = appViewModel.cuisineRollDurationSetting
        val speedSetting = appViewModel.cuisineRollSpeedSetting
        var duration = rollDurationSettingToMillis(durationSetting)

        restaurantRollRunnable = Runnable {
            appViewModel.rolledRestaurantIndex = randomNonRepeatingRoll(appViewModel.rolledRestaurantIndex, appViewModel.getRestaurantList.size)

            val newRestaurantList = restaurantListWithRandomColorChanged(appViewModel.rolledRestaurantIndex)
            appViewModel.updateRestaurantsList(newRestaurantList)

            delay = modifierDelay(duration, speedSetting)
            duration -= delay

            handler.postDelayed(restaurantRollRunnable, delay)

            if (duration < 50 || delay < 50) {
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
            newList.add(RestaurantValues(i.name, i.address, i.distance, i.priceLevel, i.rating, i.isOpen, appViewModel.getColorTheme.restaurantSquares))
        }
        newList[index].color = appViewModel.getColorTheme.selectedRestaurantSquare

        return newList
    }

    //New lists always have to be created when dealing w/ live data. Edited lists (even of snapshot lists) will not trigger recomposition.
    fun cuisineBorderStrokeToggleAnimation(duration: Long, delay: Long) {
        handler.removeCallbacks(cuisineBorderStrokeToggleRunnable)
        var iterations = duration / delay

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
                iterations -= 1

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

    private fun randomNonRepeatingRoll(lastRoll: Int, numberCap: Int): Int {
        var newRoll = Random.nextInt(0, numberCap)
        var selectedRoll = newRoll
        while (newRoll == lastRoll) {
            newRoll = Random.nextInt(0, numberCap)
            selectedRoll = newRoll
        }
        return selectedRoll
    }

}