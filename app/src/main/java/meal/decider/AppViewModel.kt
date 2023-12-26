package meal.decider

import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

class AppViewModel () : ViewModel() {
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

    private val _selectedSquare = MutableStateFlow(SquareValues())
    var selectedSquare: StateFlow<SquareValues> = _selectedSquare.asStateFlow()

    private val _squareToEdit = MutableStateFlow(0)
    val squareToEdit : StateFlow<Int> = _squareToEdit.asStateFlow()

    private val _listOfSquaresToEdit = MutableStateFlow(emptyList<SquareValues>())
    val listOfSquareIndicesToEdit : StateFlow<List<SquareValues>> = _listOfSquaresToEdit

    private val _selectedSquareIndex = MutableStateFlow(0)
    val selectedSquareIndex : StateFlow<Int> = _selectedSquareIndex.asStateFlow()

    private val _displayedCuisineList = MutableStateFlow(emptyList<String>().toList())
    val displayedCuisineList: StateFlow<List<String>> = _displayedCuisineList.asStateFlow()

    private val handler = Handler(Looper.getMainLooper())
    private var squareColorChangeRunnable = Runnable {}

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
    }

    fun updateEditMode(editMode: Boolean) {
        _editMode.value = editMode
    }

    fun updateActiveEdit(activeEdit: Boolean) {
        _activeEdit.value = activeEdit
    }

    fun updateSelectedSquare(selectedSquare: SquareValues) {
        _selectedSquare.value = selectedSquare
    }

    fun updateSquareToEdit(square: Int) {
        _squareToEdit.value = square
    }

    fun updateListOfSquaresToEdit(list: List<SquareValues>) {
        _listOfSquaresToEdit.value = list
    }

    fun updateSelectedSquareIndex(index: Int) {
        _selectedSquareIndex.value = index
    }

    fun updateDisplayedCuisineList(list: List<String>) {
        _displayedCuisineList.value = list
    }

    fun addSquareToList(name: String) {
        val squareList = getSquareList
        squareList.add(SquareValues(name, defaultSquareColor))

        updateSquareList(squareList)
    }

    fun updateSquareName(index: Int, name: String) {
        val list = getSquareList
        list[index].name = name

        _boardUiState.update { currentState ->
            currentState.copy(squareList = list)
        }
    }

    fun createSquareList() {
        _boardUiState.update { currentState ->
            currentState.copy(squareList = starterSquareList())
        }
    }

    private fun starterSquareList(): SnapshotStateList<SquareValues> {
        val list = mutableStateListOf<SquareValues>()
        for (i in starterCuisineList) {
            list.add(SquareValues(i, defaultSquareColor))
        }
        list[0] = SquareValues(list[0].name, chosenSquareColor)
        return list
    }

    fun updateColorOfSquareValuesList() {
        var delay: Long = 500

        handler.removeCallbacks(squareColorChangeRunnable)
        updateRollEngaged(true)

        squareColorChangeRunnable = Runnable {
            val indexRoll = Random.nextInt(0, getSquareList.size)
            val newSquareList = squareListWithRandomColorChanged(indexRoll)

            updateSquareList(newSquareList)

            handler.postDelayed(squareColorChangeRunnable, delay)
            delay -= 20

            if (delay < 20) {
                updateSelectedSquare(getSquareList[indexRoll])
                updateSelectedSquareIndex(indexRoll)
                updateRollEngaged(false)
                updateRollFinished(true)

                handler.removeCallbacks(squareColorChangeRunnable)
            }
        }

        handler.post((squareColorChangeRunnable))
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

    fun squareNamesList(): List<String> {
        val listToReturn = mutableListOf<String>()
        for (i in getSquareList) {
            listToReturn.add(i.name)
        }
        return listToReturn
    }

    val getSquareList get() = boardUiState.value.squareList
    val getSelectedSquare get() = selectedSquare.value
    val getSelectedSquareIndex get() = selectedSquareIndex.value
    val getListOfSquareIndicesToEdit get() = listOfSquareIndicesToEdit.value

    val getRollEngaged get() = rollEngaged.value
    val getRollFinished get() = rollFinished.value

    val getAddMode get() = addMode.value
    val getEditMode get() = editMode.value
    val getActiveEdit get() = activeEdit.value
    val getSquareToEdit get() = squareToEdit.value
}