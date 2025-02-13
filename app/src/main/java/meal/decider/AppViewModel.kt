package meal.decider

import androidx.compose.foundation.BorderStroke
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Stable
class AppViewModel : ViewModel() {
    var cuisineStringUri = ""
    var restaurantStringUri = ""
    var hasCuisineStringUriChanged = false

    var singleSquareIndexToEdit = 0
    var rolledSquareIndex = 0
    var rolledRestaurantIndex = 0

    var cuisineSortIndex = 0
    var restaurantSortIndex = 0

    var currentRestaurantList = SnapshotStateList<RestaurantValues>()

    var maxRestaurantDistance = 0.0
    var minRestaurantRating = 3.0
    var maxRestaurantPrice = 1
    var isOpen: Boolean = false

    var cuisineRollSpeedSetting : Long = 0
    var cuisineRollDurationSetting : Long = 0
    var restaurantRollSpeedSetting : Long = 0
    var restaurantRollDurationSetting : Long = 0

    var restaurantAutoScroll: Boolean = false

    var NO_DIALOG = 0
    var CUISINE_SORT = 1
    var RESTAURANT_FILTERS = 2
    var RESTAURANT_SORT = 3
    var DISCLAIMER = 4

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

    private val _showDialog = MutableStateFlow(0)
    val showDialog: StateFlow<Int> = _showDialog.asStateFlow()

    private val _restaurantList = MutableStateFlow(RestaurantsObject.RestaurantList)
    val restaurantList: StateFlow<SnapshotStateList<RestaurantValues>> = _restaurantList.asStateFlow()

    private val _restaurantVisibility = MutableStateFlow(0)
    val restaurantVisibility: StateFlow<Int> = _restaurantVisibility.asStateFlow()

    private val _restaurantQueryFinished = MutableStateFlow(true)
    val restaurantQueryFinished: StateFlow<Boolean> = _restaurantQueryFinished.asStateFlow()

    private val _restaurantSelectionMode = MutableStateFlow(false)
    val restaurantSelectionMode: StateFlow<Boolean> = _restaurantSelectionMode.asStateFlow()

    private val _optionsMode = MutableStateFlow(false)
    val optionsMode: StateFlow<Boolean> = _optionsMode

    private val _settingsDialogVisibility = MutableStateFlow(SettingsDialogVisibility())
    val settingsDialogVisibility: StateFlow<SettingsDialogVisibility> = _settingsDialogVisibility.asStateFlow()

    private val _optionsMenuVisibility = MutableStateFlow(false)
    val optionsMenuVisibility: StateFlow<Boolean> = _optionsMenuVisibility.asStateFlow()

    private val _colorSettingsSelectionList = MutableStateFlow(ColorSettingsToggleObject.colorSettingsToggleList)
    val colorSettingsSelectionList: StateFlow<SnapshotStateList<SettingsToggle>> = _colorSettingsSelectionList.asStateFlow()

    private val _colorTheme = MutableStateFlow(Theme.themeColorsList[0])
    val colorTheme: StateFlow<ColorTheme> = _colorTheme.asStateFlow()

    private val _mapQueryInProgress = MutableStateFlow(false)
    val mapQueryInProgress: StateFlow<Boolean> = _mapQueryInProgress.asStateFlow()

    private val _cuisineListIsEmpty = MutableStateFlow(false)
    val cuisineListIsEmpty: StateFlow<Boolean> = _cuisineListIsEmpty.asStateFlow()

    private val _restaurantListIsEmpty = MutableStateFlow(false)
    val restaurantListIsEmpty: StateFlow<Boolean> = _restaurantListIsEmpty.asStateFlow()

    fun updateSquareList(list: SnapshotStateList<SquareValues>) {
        _boardUiState.update { currentState ->
            currentState.copy(squareList = list)
        }
    }

    fun updateSettingsDialogVisibility(speeds: Boolean, colors: Boolean, sounds: Boolean) {
        _settingsDialogVisibility.update { currentState ->
            currentState.copy(speeds = speeds, colors = colors, sounds = sounds)
        }
    }

    fun updateOptionsMenuVisibility(visible: Boolean) {
        _optionsMenuVisibility.value = visible
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

    fun updateSelectedCuisineSquare(selectedCuisineSquare: SquareValues) {
        _selectedCuisineSquare.value = selectedCuisineSquare
    }

    fun updateListOfCuisinesToAdd(list: List<String>) {
        _listOfCuisinesToAdd.value = list
    }

    fun updateListOfCuisineSquaresToEdit(list: List<SquareValues>) {
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

    fun updateShowDialog(dialog: Int) {
        _showDialog.value = dialog
    }

    fun updateRestaurantVisibility(value: Int) {
        _restaurantVisibility.value = value
    }

    fun updateSelectedRestaurantSquare(selectedSquare: RestaurantValues) {
        _selectedRestaurantSquare.value = selectedSquare
    }

    fun updateRestaurantQueryFinished(finished: Boolean) {
        _restaurantQueryFinished.value = finished
    }

    fun updateColorSettingsToggleList(list: SnapshotStateList<SettingsToggle>) {
        _colorSettingsSelectionList.value = list
    }

    fun updateColorTheme(theme: ColorTheme) {
        _colorTheme.value = theme
    }

    fun updateMapQueryInProgress(inProgress: Boolean) {
        _mapQueryInProgress.value = inProgress
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
            list.add(SquareValues(i, getColorTheme.cuisineSquares))
        }
        list[0] = SquareValues(list[0].name, getColorTheme.selectedCuisineSquare, heavyCuisineSelectionBorderStroke)
        return list
    }

    fun sortAndUpdateCuisineList(typeOfSort: String) {
        var squareNames = squareNamesList()
        val currentSquareList = getSquareList
        val newSquareList: SnapshotStateList<SquareValues> = SnapshotStateList()
        val selectedCuisineSquareName = getSelectedCuisineSquare.name

        if (typeOfSort == "A-Z") {
            squareNames = squareNames.sorted().toMutableList()
        }
        if (typeOfSort == "Random") {
            squareNames = squareNames.shuffled().toMutableList()
        }

        for (i in squareNames.indices) {
            newSquareList.add(SquareValues(squareNames[i], currentSquareList[i].color))
        }

        for (i in 0 until newSquareList.size) {
            if (!newSquareList[i].name.equals(selectedCuisineSquareName, true)) {
                newSquareList[i] = SquareValues(newSquareList[i].name, getColorTheme.cuisineSquares)
            } else {
                newSquareList[i] = SquareValues(newSquareList[i].name, getColorTheme.selectedCuisineSquare, heavyCuisineSelectionBorderStroke)
                rolledSquareIndex = i
            }
        }

        updateSquareList(newSquareList)
    }

    fun sortAndUpdateRestaurantList(typeOfSort: String) {
        var sortedList = getRestaurantList.toList()
        val newSnapList = SnapshotStateList<RestaurantValues>()

        if (typeOfSort == "A-Z") {
            sortedList = getRestaurantList.sortedWith(compareBy { it.name })
        }
        if (typeOfSort == "Distance"){
            sortedList = getRestaurantList.sortedWith(compareBy { it.distance })
        }
        if (typeOfSort == "Rating"){
            sortedList = getRestaurantList.sortedWith(compareByDescending { it.rating })
        }
        if (typeOfSort == "Price") {
            sortedList = getRestaurantList.sortedWith(compareBy { it.priceLevel })
        }
        if (typeOfSort == "Random") {
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

    fun toggleAddCuisineSelections(cuisine: String) {
        val listToAdd = getListOfCuisinesToAdd.toMutableList()

        if (listToAdd.contains(cuisine)) {
            listToAdd.remove(cuisine)
        } else {
            listToAdd.add(cuisine)
        }
        updateListOfCuisinesToAdd(listToAdd)
    }

    fun toggleEditCuisineHighlightAndAddHighlightedCuisinesToEditList(index: Int) {
        val tempSquareList = getSquareList

        if (!tempSquareList[index].isHighlighted) {
            tempSquareList[index] = SquareValues(tempSquareList[index].name, getColorTheme.selectedEditSquareColor, getColorTheme.cuisineEditModeBorderStroke, true)
            showLog("test", "adding")
        } else {
            //If selected square name matches square selected, apply a border stroke. Otherwise do not.
            if (tempSquareList[index].name == getSelectedCuisineSquare.name) {
                tempSquareList[index] = SquareValues(tempSquareList[index].name, getColorTheme.selectedCuisineSquare, getColorTheme.cuisineEditModeBorderStroke, false)
            } else {
                tempSquareList[index] = SquareValues(tempSquareList[index].name, getColorTheme.cuisineSquares, getColorTheme.cuisineEditModeBorderStroke, false)
            }
            showLog("test", "removing")
        }
        updateSquareList(tempSquareList)
    }

    fun areAnyCuisinesHighlighted(): Boolean {
        for (i in getSquareList) {
            if (i.isHighlighted) return true
        }
        return false
    }

    fun updateSingleCuisineSquareColorAndBorder(index: Int, color: Int, border: BorderStroke) {
        val tempSquareList = getSquareList
        val newList = SnapshotStateList<SquareValues>()

        for (i in tempSquareList) {
            newList.add(SquareValues(i.name, getColorTheme.cuisineSquares, defaultCuisineBorderStroke))
        }

        newList[index].color = color
        newList[index].border = border

        updateSquareList(newList)
    }

    fun updateAllCuisineBorders(border: BorderStroke) {
        val tempSquareList = getSquareList
        val newList = SnapshotStateList<SquareValues>()

        for (i in tempSquareList) {
            newList.add(SquareValues(i.name, i.color, border))
        }
        updateSquareList(newList)
    }

    fun addMultipleSquaresToList(squares: List<String>, listIsEmpty: Boolean) {
        val squareList = getSquareList
        for (i in squares) {
            squareList.add(SquareValues(i, getColorTheme.cuisineSquares))
        }

        if (listIsEmpty) {
            emptyCuisineListIsBeingAddedTo(squareList)
        }
    }

    private fun emptyCuisineListIsBeingAddedTo(squareList: SnapshotStateList<SquareValues>) {
        rolledSquareIndex = 0
        cuisineStringUri = squareList[0].name
        squareList[0].color = getColorTheme.selectedCuisineSquare
        squareList[0].border = getColorTheme.selectedCuisineBorderStroke

        updateSquareList(squareList)
        updateSelectedCuisineSquare(getSquareList[0])
    }

    //With SnapShotStateLists, our contains() conditional is true, but not with regular Lists.
    fun deleteSelectedCuisinesFromLocalList() {
        val currentSquaresList = getSquareList
        val newList: SnapshotStateList<SquareValues> = mutableStateListOf()

        for (i in currentSquaresList) {
            newList.add(i)
            if (i.isHighlighted) {
                newList.remove(i)
            }
        }

        updateSquareList(newList)

        if (!getSquareList.isEmpty()) {
            resetSquareColors()
        }
    }

    private fun resetSquareColors() {
        val squareList = getSquareList
        val selectedCuisineSquare = getSelectedCuisineSquare

        for (i in squareList) {
            i.color = getColorTheme.cuisineSquares
            if (i.name.equals(selectedCuisineSquare.name, true)) {
                i.color = getColorTheme.selectedCuisineSquare
            }
        }

        //Set first square index to selected if previous one no longer exists.
        if (!doesSelectedCuisineSquareExist()) {
            squareList[0].color = getColorTheme.selectedCuisineSquare
            updateSelectedCuisineSquare(squareList[0])
        }

        updateSquareList(squareList)
    }

    fun setFirstSquareToDefaultColorAndBorder() {
        val squareList = getSquareList
        squareList[0].color = getColorTheme.selectedCuisineSquare
        squareList[0].border = heavyCuisineSelectionBorderStroke
        updateSquareList(squareList)
    }

    private fun doesSelectedCuisineSquareExist() : Boolean {
        val squareList = getSquareList
        val selectedCuisineSquare = getSelectedCuisineSquare

        for (i in squareList) {
            if (i.name == selectedCuisineSquare.name) return true
        }
        return false
    }

    fun modifiedAddCuisineList(currentCuisineList: List<SquareValues>): List<String> {
        val modifiedList = mutableListOf<String>()
        modifiedList.addAll(fullCuisineList)

        for (i in currentCuisineList.indices) {
            modifiedList.remove(currentCuisineList[i].name)
        }

        return modifiedList
    }

    fun updateSingleRestaurantColorAndBorder(list: SnapshotStateList<RestaurantValues>, index: Int, color: Int, border: BorderStroke) {
        val tempRestaurantList = list
        val newList = SnapshotStateList<RestaurantValues>()
        newList.addAll(tempRestaurantList)

        newList[index].color = color
        newList[index].border = border

        updateRestaurantsList(newList)
    }

    fun setLocalRestaurantFilterValues(distance: Double, rating: Double, price: Int, openNow: Boolean) {
        maxRestaurantDistance = distance
        minRestaurantRating = rating
        maxRestaurantPrice = price
        isOpen = openNow
    }

    fun hasRestaurantListChanged(currentList: List<RestaurantValues>, newList: List<RestaurantValues>): Boolean {
        if (currentList.isEmpty() || currentList.size != newList.size) return true else {
            for (i in currentList.indices) {
                if (currentList[i].name != newList[i].name) return true
            }
        }
        return false
    }

    fun haveRestaurantFiltersChanged(distance: Double, rating: Double, price: Int, openNow: Boolean): Boolean {
        return maxRestaurantDistance !=distance || minRestaurantRating != rating || maxRestaurantPrice != price || isOpen != openNow
    }

    fun updateCuisineStringUriAndHasChangedBoolean(cuisineSelected: String) {
        hasCuisineStringUriChanged = (cuisineStringUri != cuisineSelected)
        cuisineStringUri = cuisineSelected
    }

    fun updateRollOptions(cuisineDuration: Long, cuisineDelay: Long, restaurantDuration: Long, restaurantDelay: Long) {
        cuisineRollDurationSetting = cuisineDuration; cuisineRollSpeedSetting = cuisineDelay; restaurantRollDurationSetting = restaurantDuration; restaurantRollSpeedSetting = restaurantDelay
    }

    fun toggleRestrictionListItems(index: Int) {
        val list = getRestrictionsList
        list[index].selected = !list[index].selected
        val updatedList = mutableStateListOf<RestrictionsValues>()
        updatedList.addAll(list)

        updateRestrictionsList(updatedList)
    }

    private fun colorSettingsList(index: Int): SnapshotStateList<SettingsToggle> {
        val list = getColorSettingsSelectionList
        for (i in list) { i.selected = false }
        list[index].selected = true
        val updatedList = mutableStateListOf<SettingsToggle>()
        updatedList.addAll(list)

        return updatedList
    }

    fun switchColorSettingsUi(index: Int) {
        updateColorSettingsToggleList(colorSettingsList(index))
    }

    val getSquareList get() = boardUiState.value.squareList
    val getSelectedCuisineSquare get() = selectedCuisineSquare.value
    val getListOfCuisineSquaresToEdit get() = listOfCuisineSquaresToEdit.value
    val getListOfCuisinesToAdd get() = listOfCuisinesToAdd.value
    val getRestaurantList get() = _restaurantList.value
    val getRestaurantVisibility get() = restaurantVisibility.value
    val getRollEngaged get() = rollEngaged.value

    val getEditMode get() = editMode.value

    val getRestrictionsList get() = restrictionsList.value
    val getRestaurantQueryFinished get() = restaurantQueryFinished.value

    val getRestaurantSelectionMode get() = restaurantSelectionMode.value

    val getColorSettingsSelectionList get() = colorSettingsSelectionList.value
    val getColorTheme get() = colorTheme.value

    val getShowDialog get() = showDialog.value

    val getDisplayedCuisineList get() = displayedCuisineList.value
}