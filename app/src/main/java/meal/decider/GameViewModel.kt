package meal.decider

import android.content.Context
import android.os.Handler
import android.os.Looper
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlin.random.Random

class GameViewModel (context: Context) : ViewModel() {
    private val genericSquareColor = R.color.grey_300
    private val chosenSquareColor = R.color.red_200

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

    private val _squareToEdit = MutableStateFlow(0)
    val squareToEdit : StateFlow<Int> = _squareToEdit.asStateFlow()

    private val _searchText = MutableStateFlow("")
    val searchText : StateFlow<String> = _searchText.asStateFlow()

    private val handler = Handler(Looper.getMainLooper())
    private var squareColorChangeRunnable = Runnable {}

    fun updateSquareList(list: SnapshotStateList<SquareValues>) {
        _boardUiState.update { currentState ->
            currentState.copy(squareList = list)
        }
    }

    private fun updateSelectedSquare(squareValues: SquareValues) {
        _boardUiState.update { currentState ->
            currentState.copy(selectedSquare = squareValues)
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

    fun updateSquareToEdit(square: Int) {
        _squareToEdit.value = square
    }

    fun updateSearchText(text: String) {
        _searchText.value = text
    }

    fun addSquareToList(name: String) {
        val squareList = getSquareList
        squareList.add(SquareValues(name, genericSquareColor))

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
            list.add(SquareValues(i, genericSquareColor))
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
            val newSquareList = SquareListWithRandomColorChanged(indexRoll)

            updateSquareList(newSquareList)

            handler.postDelayed(squareColorChangeRunnable, delay)
            delay -= 20

            if (delay < 20) {
                updateSelectedSquare(getSquareList[indexRoll])
                updateRollEngaged(false)
                updateRollFinished(true)

                handler.removeCallbacks(squareColorChangeRunnable)
            }
        }

        handler.post((squareColorChangeRunnable))
    }

    private fun SquareListWithRandomColorChanged(index: Int): SnapshotStateList<SquareValues> {
        val currentList = getSquareList
        val newList = SnapshotStateList<SquareValues>()

        for (i in currentList) {
            newList.add(SquareValues(i.name, genericSquareColor))
        }

        newList[index].color = chosenSquareColor

        return newList
    }

    val getSelectedSquare get() = boardUiState.value.selectedSquare
    val getSquareList get() = boardUiState.value.squareList

    val getRollEngaged get() = rollEngaged.value
    val getRollFinished get() = rollFinished.value

    val getAddMode get() = addMode.value
    val getEditMode get() = editMode.value
    val getActiveEdit get() = activeEdit.value
    val getSquareToEdit get() = squareToEdit.value
    val getSearchText get() = searchText.value
}