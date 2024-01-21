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

    private val handler = Handler(Looper.getMainLooper())
    private var cuisineRollRunnable = Runnable {}
    private var restaurantRollRunnable = Runnable {}
    private var pressYourLuckRunnable = Runnable {}
    private var cuisineBorderStrokeToggleRunnable = Runnable {}

    private val _boardUiState = MutableStateFlow(BoardValues())
    val boardUiState : StateFlow<BoardValues> = _boardUiState.asStateFlow()

    private val _rollEngaged = MutableStateFlow(false)
    val rollEngaged : StateFlow<Boolean> = _rollEngaged.asStateFlow()

    private val _rollFinished = MutableStateFlow(false)
    val rollFinished : StateFlow<Boolean> = _rollFinished.asStateFlow()

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

    private val _selectedRestaurantSquare = MutableStateFlow(RestaurantValues())
    val selectedRestaurantSquare: StateFlow<RestaurantValues> = _selectedRestaurantSquare.asStateFlow()

    private val _showRestaurants = MutableStateFlow(false)
    val showRestaurants: StateFlow<Boolean> = _showRestaurants.asStateFlow()

    private val _restaurantList = MutableStateFlow(RestaurantsObject.RestaurantList)
    val restaurantList: StateFlow<SnapshotStateList<RestaurantValues>> = _restaurantList.asStateFlow()

    private val _cuisinerSelectionBorderStroke = MutableStateFlow(BorderStroke(1.dp, Color.Black))
    val cuisinerSelectionBorderStroke: StateFlow<BorderStroke> = _cuisinerSelectionBorderStroke.asStateFlow()

    fun updateSquareList(list: SnapshotStateList<SquareValues>) {
        _boardUiState.update { currentState ->
            currentState.copy(squareList = list)
        }
    }

    fun updateRollEngaged(engaged: Boolean) {
        _rollEngaged.value = engaged
    }

    fun updateRollFinished(finished: Boolean) {
        _rollFinished.value = finished
    }

    fun updateAddMode(addMode: Boolean) {
        _addMode.value = addMode
        //If disabling add mode and its dialog box, empty our list of cuisines to add.
        if (!_addMode.value) updateListOfSquaresToAdd(emptyList())
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

    fun updateselectedCuisineSquare(selectedCuisineSquare: SquareValues) {
        _selectedCuisineSquare.value = selectedCuisineSquare
    }

    fun updateListOfSquaresToAdd(list: List<String>) {
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

    fun updateRestaurantsList(list: SnapshotStateList<RestaurantValues>) {
        _restaurantList.value = list
    }

    fun updateShowRestaurants(show: Boolean) {
        _showRestaurants.value = show
    }

    fun updateCuisineSelectionBorderStroke(borderStroke: BorderStroke) {
        _cuisinerSelectionBorderStroke.value = borderStroke
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

    fun squareNamesList(): List<String> {
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

    //Val declares first element in our data object (i.e. "name", thought it can be named anything).
    fun adjustDisplayedCuisineListFromDisplayedSquares() {
        val displayedCuisineList = getDisplayedCuisineList.toMutableList()
        for (i in getSquareList) {
            val (name) = i
            if (displayedCuisineList.contains(name)) {
                displayedCuisineList.remove(name)
            }
        }
        updateDisplayedCuisineList(displayedCuisineList)
    }

    fun toggleAddCuisineSelections(cuisine: String) {
        val listToAdd = getListOfCuisinesToAdd.toMutableList()
        if (listToAdd.contains(cuisine)) {
            listToAdd.remove(cuisine)
        } else {
            listToAdd.add(cuisine)
        }
        updateListOfSquaresToAdd(listToAdd)
    }

    fun toggleEditCuisineHighlight(index: Int) {
        val tempSquareList = getSquareList

        if (tempSquareList[index].color == chosenSquareColor || tempSquareList[index].color == defaultSquareColor) {
            tempSquareList[index] = SquareValues(tempSquareList[index].name, editSquareColor)
            addSquareTolistOfCuisineSquaresToEdit(index)
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

    fun addSquareTolistOfCuisineSquaresToEdit(index: Int) {
        val tempList = getlistOfCuisineSquaresToEdit.toMutableList()
        val currentList = getSquareList
        tempList.add(currentList[index])
        updatelistOfCuisineSquaresToEdit(tempList)
    }

    fun removeSquareFromListOfSquareIndicesToUpdate() {
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

    fun resetSquareColors() {
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
            updateselectedCuisineSquare(squareList[0])
        }

        updateSquareList(squareList)
    }

    private fun doesSelectedCuisineSquareExist() : Boolean {
        val squareList = getSquareList
        val selectedCuisineSquare = getselectedCuisineSquare

        for (i in squareList) {
            if (i.name.equals(selectedCuisineSquare.name)) return true
        }
        return false
    }

    fun filterList(list: List<String>, searchString: String) : List<String> {
        //If search string equals the first X characters typed, filter list with just those matching entries. If search string is empty, display full list.
        return if (searchString != "") {
            list.filter { a -> a.substring(0, searchString.length).equals(searchString, true) }
        } else {
            list
        }
    }

    fun foodRestrictionsString(list: SnapshotStateList<RestrictionsValues>): String {
        var stringList = ""
        for (i in list) {
            if (i.selected) {
                stringList = stringList+ "+" + (i.name)
            }
        }
        return stringList
    }

    //Cuisine and Press Your Luck run at different intervals but both stop when rollCountDown hits 20.
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
                updateselectedCuisineSquare(getSquareList[rolledSquareIndex])
                updateRollEngaged(false)
                updateRollFinished(true)

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
        rollCountdown = 1000
        handler.removeCallbacks(restaurantRollRunnable)

        restaurantRollRunnable = Runnable {
            rolledRestaurantIndex = Random.nextInt(0, getRestaurantList.size)
            val newRestaurantList = restaurantListWithRandomColorChanged(rolledRestaurantIndex)
            updateRestaurantsList(newRestaurantList)

            handler.postDelayed(restaurantRollRunnable, delay)
            if (delay > 100) delay -= 10
            rollCountdown -= 20

            if (rollCountdown < 20) {
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

    fun pressYourLuck() {
        var delay: Long = 800
        handler.removeCallbacks(pressYourLuckRunnable)

        pressYourLuckRunnable = Runnable {
            sortAndUpdateCuisineList("random")

            handler.postDelayed(pressYourLuckRunnable, delay)
            if (delay > 200) delay -= 20
            rollCountdown -= 20

            if (rollCountdown < 60) {
                handler.removeCallbacks(pressYourLuckRunnable)
            }
        }

        handler.post(pressYourLuckRunnable)
    }

    fun cuisineBorderStrokeToggle() {
        handler.removeCallbacks(cuisineBorderStrokeToggleRunnable)
        updateCuisineSelectionBorderStroke(defaultCuisineSelectionBorderStroke)

        cuisineBorderStrokeToggleRunnable = Runnable {
            if (getcuisinerSelectionBorderStroke == defaultCuisineSelectionBorderStroke) updateCuisineSelectionBorderStroke(
                animatedCuisineSelectionBorderStroke) else updateCuisineSelectionBorderStroke(
                defaultCuisineSelectionBorderStroke)

            handler.postDelayed(cuisineBorderStrokeToggleRunnable, 200)
        }

        handler.post(cuisineBorderStrokeToggleRunnable)
    }

    fun cancelCuisineBorderStrokeToggle() { handler.removeCallbacks(cuisineBorderStrokeToggleRunnable) }
    
    fun resetCuisineSelectionBorderStroke() { updateCuisineSelectionBorderStroke(
        defaultCuisineSelectionBorderStroke) }

    fun dummyRestaurantList(): SnapshotStateList<RestaurantValues> {
        val listToReturn = mutableStateListOf<RestaurantValues>()
        for (i in 1..20) {
            listToReturn.add(RestaurantValues("So Good Restaurant With Way More Text Here It Is", "123 Bird Brain Lane", 2000.0, 2, 4.0, defaultSquareColor))
        }
        return listToReturn
    }

    val getSquareList get() = boardUiState.value.squareList
    val getselectedCuisineSquare get() = selectedCuisineSquare.value
    val getDisplayedCuisineList get() = displayedCuisineList.value
    val getlistOfCuisineSquaresToEdit get() = listOfCuisineSquaresToEdit.value
    val getListOfCuisinesToAdd get() = listOfCuisinesToAdd.value
    val getRestaurantList get() = _restaurantList.value
    val getShowRestaurants get() = _showRestaurants.value
    val getcuisinerSelectionBorderStroke get() = _cuisinerSelectionBorderStroke.value

    val getRollEngaged get() = rollEngaged.value
    val getRollFinished get() = rollFinished.value

    val getAddMode get() = addMode.value
    val getEditMode get() = editMode.value
    val getActiveEdit get() = activeEdit.value
    val getOptionsMode get() = optionsMode.value
    val getRestoreDefaults get() = restoreDefaults.value

    val getRestrictionsList get() = restrictionsList.value
}