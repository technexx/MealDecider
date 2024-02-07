package meal.decider

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

@Stable
class AppViewModel : ViewModel() {
    var singleSquareIndexToEdit = 0
    var rolledSquareIndex = 0
    var rolledRestaurantIndex = 0
    var rollCountdown: Long = 1000

    var restaurantSearchCuisineType = ""
    var cuisineStringUri = ""
    var restaurantStringUri = ""
    var originalRestaurantList: SnapshotStateList<RestaurantValues> = mutableStateListOf()

    var maxRestaurantDistance = 0
    var minRestaurantRating = 3.0
    var maxRestaurantPrice = 1

    private val handler = Handler(Looper.getMainLooper())
    private var cuisineRollRunnable = Runnable {}
    private var restaurantRollRunnable = Runnable {}
    private var pressYourLuckRunnable = Runnable {}
    private var cuisineBorderStrokeToggleRunnable = Runnable {}
    private var restaurantBorderStrokeToggleRunnable = Runnable {}

    private val _boardUiState = MutableStateFlow(BoardValues())
    val boardUiState : StateFlow<BoardValues> = _boardUiState.asStateFlow()

    private val _rollEngaged = MutableStateFlow(false)
    val rollEngaged : StateFlow<Boolean> = _rollEngaged.asStateFlow()

    private val _cuisineRollFinished = MutableStateFlow(false)
    val cuisineRollFinished : StateFlow<Boolean> = _cuisineRollFinished.asStateFlow()

    private val _addMode = MutableStateFlow(false)
    val addMode : StateFlow<Boolean> = _addMode.asStateFlow()

    private val _editMode = MutableStateFlow(false)
    val editMode : StateFlow<Boolean> = _editMode.asStateFlow()

    private val _activeEdit = MutableStateFlow(false)
    val activeEdit : StateFlow<Boolean> = _activeEdit.asStateFlow()

    private val _restoreDefaults = MutableStateFlow(false)
    val restoreDefaults: StateFlow<Boolean> = _restoreDefaults.asStateFlow()

    private val _optionsMode = MutableStateFlow(false)
    val optionsMode : StateFlow<Boolean> = _optionsMode

    private val _selectedCuisineSquare = MutableStateFlow(SquareValues())
    val selectedCuisineSquare: StateFlow<SquareValues> = _selectedCuisineSquare.asStateFlow()

    private val _listOfCuisinesToAdd = MutableStateFlow(emptyList<String>())
    val listOfCuisinesToAdd: StateFlow<List<String>> = _listOfCuisinesToAdd.asStateFlow()

    private val _listOfCuisineSquaresToEdit = MutableStateFlow(emptyList<SquareValues>())
    val listOfCuisineSquaresToEdit : StateFlow<List<SquareValues>> = _listOfCuisineSquaresToEdit

    private val _displayedCuisineList = MutableStateFlow(emptyList<String>())
    val displayedCuisineList: StateFlow<List<String>> = _displayedCuisineList.asStateFlow()

    private val _restrictionsList = MutableStateFlow(RestrictionsObject.RestrictionsList)
    val restrictionsList: StateFlow<SnapshotStateList<RestrictionsValues>> = _restrictionsList.asStateFlow()

    private val _restaurantRollFinished = MutableStateFlow(false)
    val restaurantRollFinished: StateFlow<Boolean> = _restaurantRollFinished.asStateFlow()

    private val _selectedRestaurantSquare = MutableStateFlow(RestaurantValues())
    val selectedRestaurantSquare: StateFlow<RestaurantValues> = _selectedRestaurantSquare.asStateFlow()

    private val _showRestaurants = MutableStateFlow(false)
    val showRestaurants: StateFlow<Boolean> = _showRestaurants.asStateFlow()

    private val _restaurantList = MutableStateFlow(RestaurantsObject.RestaurantList)
    val restaurantList: StateFlow<SnapshotStateList<RestaurantValues>> = _restaurantList.asStateFlow()

    private val _showRestaurantSettings = MutableStateFlow(false)
    val showRestaurantSettings: StateFlow<Boolean> = _showRestaurantSettings.asStateFlow()

    private val _cuisineSelectionBorderStroke = MutableStateFlow(BorderStroke(1.dp, Color.Black))
    val cuisineSelectionBorderStroke: StateFlow<BorderStroke> = _cuisineSelectionBorderStroke.asStateFlow()

    private val _restaurantSelectionBorderStroke = MutableStateFlow(BorderStroke(1.dp, Color.Black))
    val restaurantSelectionBorderStroke: StateFlow<BorderStroke> = _restaurantSelectionBorderStroke.asStateFlow()

    fun updateSquareList(list: SnapshotStateList<SquareValues>) {
        _boardUiState.update { currentState ->
            currentState.copy(squareList = list)
        }
    }

    fun updateRollEngaged(engaged: Boolean) {
        _rollEngaged.value = engaged
    }

    fun updateCuisineRollFinished(finished: Boolean) {
        _cuisineRollFinished.value = finished
    }

    fun updateAddMode(addMode: Boolean) {
        _addMode.value = addMode
    }

    fun updateEditMode(editMode: Boolean) {
        _editMode.value = editMode
    }

    fun updateActiveEdit(activeEdit: Boolean) {
        _activeEdit.value = activeEdit
    }

    fun updateRestoreDefaults(restore: Boolean) {
        _restoreDefaults.value = restore
    }

    fun updateOptionsMode(optionsMode: Boolean) {
        _optionsMode.value = optionsMode
    }

    fun updateSelectedCuisineSquare(selectedCuisineSquare: SquareValues) {
        _selectedCuisineSquare.value = selectedCuisineSquare
    }

    fun updateListOfCuisinesToAdd(list: List<String>) {
        _listOfCuisinesToAdd.value = list
    }

    fun updatelistOfCuisineSquaresToEdit(list: List<SquareValues>) {
        _listOfCuisineSquaresToEdit.value = list
    }

    fun updateDisplayedCuisineList(list: List<String>) {
        _displayedCuisineList.value = list
    }

    fun updateRestrictionsList(list: SnapshotStateList<RestrictionsValues>) {
        _restrictionsList.value = list
    }

    fun updateRestaurantRollFinished(finished: Boolean) {
        _restaurantRollFinished.value = finished
    }

    fun updateRestaurantsList(list: SnapshotStateList<RestaurantValues>) {
        _restaurantList.value = list
    }

    fun updateShowRestaurants(show: Boolean) {
        _showRestaurants.value = show
    }

    fun updateShowRestaurantSettings(show: Boolean) {
        _showRestaurantSettings.value = show
    }

    fun updateSelectedRestaurantSquare(selectedSquare: RestaurantValues) {
        _selectedRestaurantSquare.value = selectedSquare
    }

    fun updateCuisineSelectionBorderStroke(borderStroke: BorderStroke) {
        _cuisineSelectionBorderStroke.value = borderStroke
    }

    fun updateRestaurantSelectionBorderStroke(borderStroke: BorderStroke) {
        _restaurantSelectionBorderStroke.value = borderStroke
    }

    fun addMultipleSquaresToList(squares: List<String>) {
        val squareList = getSquareList
        for (i in squares) {
            squareList.add(SquareValues(i, defaultSquareColor))
        }
        updateSquareList(squareList)
    }

    fun updateSquareName(index: Int, name: String) {
        val list = getSquareList
        list[index].name = name

        _boardUiState.update { currentState ->
            currentState.copy(squareList = list)
        }
    }

    fun starterSquareList(): SnapshotStateList<SquareValues> {
        val list = mutableStateListOf<SquareValues>()
        for (i in starterCuisineList) {
            list.add(SquareValues(i, defaultSquareColor))
        }
        list[0] = SquareValues(list[0].name, chosenSquareColor)
        return list
    }

    fun sortAndUpdateCuisineList(typeOfSort: String) {
        var squareNames = squareNamesList()
        val currentSquareList = getSquareList
        val newSquareList: SnapshotStateList<SquareValues> = SnapshotStateList()
        val selectedCuisineSquareName = getselectedCuisineSquare.name

        if (typeOfSort == "alphabetical") {
            squareNames = squareNames.sorted().toMutableList()
        }
        if (typeOfSort == "random") {
            squareNames = squareNames.shuffled().toMutableList()
        }

        for (i in squareNames.indices) {
            newSquareList.add(SquareValues(squareNames[i], currentSquareList[i].color))
        }

        for (i in 0 until newSquareList.size) {
            if (!newSquareList[i].name.equals(selectedCuisineSquareName, true)) {
                newSquareList[i] = SquareValues(newSquareList[i].name, defaultSquareColor)
            } else {
                newSquareList[i] = SquareValues(newSquareList[i].name, chosenSquareColor)
            }
        }

        updateSquareList(newSquareList)
    }

    fun sortAndUpdateRestaurantList(typeOfSort: String) {
        var sortedList = getRestaurantList.toList()
        val newSnapList = SnapshotStateList<RestaurantValues>()

        if (typeOfSort == "name") {
            sortedList = getRestaurantList.sortedWith(compareBy { it.name })
        }
        if (typeOfSort == "distance"){
            sortedList = getRestaurantList.sortedWith(compareBy { it.distance })
        }
        if (typeOfSort == "rating"){
            sortedList = getRestaurantList.sortedWith(compareBy { it.rating })
        }
        if (typeOfSort == "random") {
            var newNamesList = mutableListOf<String?>()
            for (i in sortedList.indices) {
                newNamesList.add(sortedList[i].name)
            }
            newNamesList = newNamesList.shuffled().toMutableList()

            for (i in sortedList.indices) {
                sortedList[i].name = newNamesList[i]
            }
        }

        newSnapList.addAll(sortedList)
        updateRestaurantsList(newSnapList)
    }

    private fun squareNamesList(): List<String> {
        val listToReturn = mutableListOf<String>()
        for (i in getSquareList) {
            listToReturn.add(i.name)
        }
        return listToReturn
    }

    fun getListOfSquareNames(): List<String> {
        val listToReturn = mutableListOf<String>()
        for (i in getSquareList) {
            listToReturn.add(i.name)
        }
        return listToReturn
    }

    fun adjustDisplayedCuisineListFromDisplayedSquares() {
        val listToDisplay = fullCuisineList.toMutableList()
        val squareNameList = getListOfSquareNames().toMutableList()

        for (i in squareNameList) {
            if (fullCuisineList.contains(i)) {
                listToDisplay.remove(i)
            }
        }
        updateDisplayedCuisineList(listToDisplay)
    }

    fun toggleAddCuisineSelections(cuisine: String) {
        val listToAdd = getListOfCuisinesToAdd.toMutableList()

        if (listToAdd.contains(cuisine)) {
            listToAdd.remove(cuisine)
        } else {
            listToAdd.add(cuisine)
        }
        updateListOfCuisinesToAdd(listToAdd)
    }

    fun toggleEditCuisineHighlight(index: Int) {
        val tempSquareList = getSquareList

        if (tempSquareList[index].color == chosenSquareColor || tempSquareList[index].color == defaultSquareColor) {
            tempSquareList[index] = SquareValues(tempSquareList[index].name, editSquareColor)
            addSquareToListOfCuisineSquaresToEdit(index)
        } else {
            if (tempSquareList[index].name == selectedCuisineSquare.value.name) {
                tempSquareList[index] = SquareValues(tempSquareList[index].name, chosenSquareColor)
            } else {
                tempSquareList[index] = SquareValues(tempSquareList[index].name, defaultSquareColor)
            }
            removeSquareFromListOfSquareIndicesToUpdate()
        }
        updateSquareList(tempSquareList)

    }

    private fun addSquareToListOfCuisineSquaresToEdit(index: Int) {
        val tempList = getlistOfCuisineSquaresToEdit.toMutableList()
        val currentList = getSquareList
        tempList.add(currentList[index])
        updatelistOfCuisineSquaresToEdit(tempList)
    }

    private fun removeSquareFromListOfSquareIndicesToUpdate() {
        val tempList = getlistOfCuisineSquaresToEdit.toMutableList()
        tempList.removeLast()
        updatelistOfCuisineSquaresToEdit(tempList)
    }

    //With SnapShotStateLists, our contains() conditional is true, but not with regular Lists.
    fun deleteSelectedCuisines() {
        val listOfCuisineSquaresToEdit = getlistOfCuisineSquaresToEdit
        val currentSquaresList = getSquareList

        for (i in listOfCuisineSquaresToEdit) {
            if (currentSquaresList.contains(i)) {
                currentSquaresList.remove(i)
            }
        }

        updateSquareList(currentSquaresList)
        resetSquareColors()
        updateEditMode(false)
    }

    private fun resetSquareColors() {
        val squareList = getSquareList
        val selectedCuisineSquare = getselectedCuisineSquare

        for (i in squareList) {
            i.color = defaultSquareColor
            if (i.name.equals(selectedCuisineSquare.name, true)) {
                i.color = chosenSquareColor
            }
        }

        //Set first square index to selected if previous one no longer exists.
        if (!doesSelectedCuisineSquareExist()) {
            squareList[0].color = chosenSquareColor
            updateSelectedCuisineSquare(squareList[0])
        }

        updateSquareList(squareList)
    }

    fun setFirstSquareToDefaultColor() {
        val squareList = getSquareList
        squareList[0].color = chosenSquareColor
        updateSquareList(squareList)
    }

    private fun doesSelectedCuisineSquareExist() : Boolean {
        val squareList = getSquareList
        val selectedCuisineSquare = getselectedCuisineSquare

        for (i in squareList) {
            if (i.name == selectedCuisineSquare.name) return true
        }
        return false
    }

    fun rollCuisine() {
        var delay: Long = 100
        rollCountdown = 100

        updateRollEngaged(true)
        handler.removeCallbacks(cuisineRollRunnable)

        cuisineRollRunnable = Runnable {
            rolledSquareIndex = Random.nextInt(0, getSquareList.size)
            val newSquareList = squareListWithRandomColorChanged(rolledSquareIndex)
            updateSquareList(newSquareList)

            handler.postDelayed(cuisineRollRunnable, delay)
            if (delay > 100) delay -= 10
            rollCountdown -= 20

            if (rollCountdown < 20) {
                updateSelectedCuisineSquare(getSquareList[rolledSquareIndex])
                updateCuisineRollFinished(true)
                updateRollEngaged(false)
                cuisineStringUri = selectedCuisineSquare.value.name + " Food " + foodRestrictionsString(getRestrictionsList)
                handler.removeCallbacks(cuisineRollRunnable)
            }
        }

        handler.post((cuisineRollRunnable))
    }

    private fun squareListWithRandomColorChanged(index: Int): SnapshotStateList<SquareValues> {
        val currentList = getSquareList
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
        updateRollEngaged(true)

        restaurantRollRunnable = Runnable {
            rolledRestaurantIndex = Random.nextInt(0, getRestaurantList.size)
            val newRestaurantList = restaurantListWithRandomColorChanged(rolledRestaurantIndex)
            updateRestaurantsList(newRestaurantList)

            handler.postDelayed(restaurantRollRunnable, delay)
            if (delay > 100) delay -= 10
            rollCountdown -= 20

            if (rollCountdown < 20) {
                updateSelectedRestaurantSquare(getRestaurantList[rolledRestaurantIndex])
                updateRestaurantRollFinished(true)
                updateRollEngaged(false)
                handler.removeCallbacks(restaurantRollRunnable)
            }
        }

        handler.post(restaurantRollRunnable)
    }

    private fun restaurantListWithRandomColorChanged(index: Int): SnapshotStateList<RestaurantValues> {
        val currentList = getRestaurantList
        val newList = SnapshotStateList<RestaurantValues>()

        for (i in currentList) {
            newList.add(RestaurantValues(i.name, i.address, i.distance, i.priceLevel, i.rating, defaultSquareColor))
        }
        newList[index].color = chosenSquareColor

        return newList
    }

    fun cuisineBorderStrokeToggleAnimation() {
        handler.removeCallbacks(cuisineBorderStrokeToggleRunnable)
        updateCuisineSelectionBorderStroke(lightCuisineSelectionBorderStroke)

        cuisineBorderStrokeToggleRunnable = Runnable {
            if (getCuisineSelectionBorderStroke == lightCuisineSelectionBorderStroke) {
                updateCuisineSelectionBorderStroke(heavyCuisineSelectionBorderStroke)
            } else {
                updateCuisineSelectionBorderStroke(lightCuisineSelectionBorderStroke)
            }
            handler.postDelayed(cuisineBorderStrokeToggleRunnable, 200)
        }
        handler.post(cuisineBorderStrokeToggleRunnable)
    }

    fun cancelCuisineBorderStrokeToggleRunnable() { handler.removeCallbacks(cuisineBorderStrokeToggleRunnable) }
    
    fun resetCuisineSelectionBorderStroke() { updateCuisineSelectionBorderStroke(defaultCuisineSelectionBorderStroke) }

    fun restaurantBorderStrokeToggleAnimation() {
        handler.removeCallbacks(restaurantBorderStrokeToggleRunnable)
        updateCuisineSelectionBorderStroke(lightRestaurantSelectionBorderStroke)

        restaurantBorderStrokeToggleRunnable = Runnable {
            if (getRestaurantSelectionBorderStroke == lightRestaurantSelectionBorderStroke) {
                updateRestaurantSelectionBorderStroke(heavyRestaurantSelectionBorderStroke)
            } else {
                updateRestaurantSelectionBorderStroke(lightRestaurantSelectionBorderStroke)
            }
            handler.postDelayed(restaurantBorderStrokeToggleRunnable, 200)
        }

        handler.post(restaurantBorderStrokeToggleRunnable)
    }

    fun cancelRestaurantBorderStrokeToggleRunnable() { handler.removeCallbacks(restaurantBorderStrokeToggleRunnable) }

    fun resetRestaurantSelectionBorderStroke() { updateRestaurantSelectionBorderStroke(defaultRestaurantSelectionBorderStroke) }

    fun setLocalRestaurantFilterValues(distance: Int, rating: Double, price: Int) {
        maxRestaurantDistance = distance
        minRestaurantRating = rating
        maxRestaurantPrice = price
    }

    fun haveRestaurantFiltersChanged(distance: Int, rating: Double, price: Int): Boolean {
        return maxRestaurantDistance !=distance || minRestaurantRating != rating || maxRestaurantPrice != price
    }

    val getSquareList get() = boardUiState.value.squareList
    val getselectedCuisineSquare get() = selectedCuisineSquare.value
    val getDisplayedCuisineList get() = displayedCuisineList.value
    val getlistOfCuisineSquaresToEdit get() = listOfCuisineSquaresToEdit.value
    val getListOfCuisinesToAdd get() = listOfCuisinesToAdd.value
    val getRestaurantList get() = _restaurantList.value
    val getShowRestaurants get() = _showRestaurants.value
    val getShowRestaurantSettings get() = _showRestaurantSettings.value
    val getselectedRestaurantSquare get() = selectedRestaurantSquare.value
    val getCuisineSelectionBorderStroke get() = _cuisineSelectionBorderStroke.value
    val getRestaurantSelectionBorderStroke get() = _restaurantSelectionBorderStroke.value

    val getRollEngaged get() = rollEngaged.value
    val getCuisineRollFinished get() = _cuisineRollFinished.value
    val getRestaurantRollFinished get() = _restaurantRollFinished.value

    val getAddMode get() = addMode.value
    val getEditMode get() = editMode.value
    val getActiveEdit get() = activeEdit.value
    val getOptionsMode get() = optionsMode.value
    val getRestoreDefaults get() = restoreDefaults.value

    val getRestrictionsList get() = restrictionsList.value
}