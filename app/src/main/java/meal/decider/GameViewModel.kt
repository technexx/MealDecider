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
    private val _boardUiState = MutableStateFlow(BoardValues())
    val boardUiState : StateFlow<BoardValues> = _boardUiState.asStateFlow()

    private val _addMode = MutableStateFlow(false)
    val addMode : StateFlow<Boolean> = _addMode.asStateFlow()

    private val _editMode = MutableStateFlow(false)
    val editMode : StateFlow<Boolean> = _editMode.asStateFlow()

    private val _activeEdit = MutableStateFlow(false)
    val activeEdit : StateFlow<Boolean> = _activeEdit.asStateFlow()

    private val _squareToEdit = MutableStateFlow(0)
    val squareToEdit : StateFlow<Int> = _squareToEdit.asStateFlow()

    private val handler = Handler(Looper.getMainLooper())
    private var colorListRunnable = Runnable {}

    fun updateSquareList(list: SnapshotStateList<SquareValues>) {
        _boardUiState.update { currentState ->
            currentState.copy(squareList = list)
        }
    }

    private fun updateColorList(list: SnapshotStateList<Int>) {
        _boardUiState.update { currentState ->
            currentState.copy(colorList = list)
        }
    }

    private fun updateSelectedSquare(squareValues: SquareValues) {
        _boardUiState.update { currentState ->
            currentState.copy(selectedSquare = squareValues)
        }
    }

    fun updateRollEngaged(engaged: Boolean) {
        _boardUiState.update { currentState ->
            currentState.copy(rollEngaged = engaged)
        }
    }

    fun updateRollFinished(finished: Boolean) {
        _boardUiState.update { currentState ->
            currentState.copy(rollFinished = finished)
        }
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

    fun addSquareToList(name: String) {
        val squareList = getSquareList
        squareList.add(SquareValues(name))

        updateSquareList(squareList)
    }

    fun updateSelectedSquareName(index: Int, name: String) {
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
        for (i in SquareDataObject.squareValuesList) {
            list.add(i)
        }
        return list
    }

    fun createColorList() {
        _boardUiState.update { currentState ->
            currentState.copy(colorList = starterColorList())
        }
    }

    private fun starterColorList() : SnapshotStateList<Int> {
        val list = mutableStateListOf<Int>()
        for (i in SquareDataObject.squareValuesList) {
            list.add(R.color.grey_300)
        }

        list[0] = R.color.red_200
        return list
    }

    fun updateColorListWithinLooper() {
        var delay: Long = 500

        handler.removeCallbacks(colorListRunnable)
        updateRollEngaged(true)

        colorListRunnable = Runnable {
            val indexRoll = Random.nextInt(0, getSquareList.size)
            val newColorList = colorListWithRandomIndexChanged(R.color.red_200, indexRoll)
            updateColorList(newColorList)

            handler.postDelayed(colorListRunnable, delay)
            delay -= 20

            if (delay < 20) {
                updateSelectedSquare(SquareDataObject.squareValuesList[indexRoll])
                updateRollEngaged(false)
                updateRollFinished(true)

                handler.removeCallbacks(colorListRunnable)
            }
        }

        handler.post((colorListRunnable))
    }

    private fun colorListWithRandomIndexChanged(color: Int, index: Int): SnapshotStateList<Int> {
        val list = SnapshotStateList<Int>()
        for (i in getSquareList) {
            list.add(R.color.grey_300)
        }
        list[index] = color

        return list
    }

    fun rollRandomSquare(numberOfSquares: Int): Int {
        return Random.nextInt(0, numberOfSquares)
    }

    val getSelectedSquare get() = boardUiState.value.selectedSquare
    val getSquareList get() = boardUiState.value.squareList
    val getColorList get() = boardUiState.value.colorList
    val getRollEngaged get() = boardUiState.value.rollEngaged
    val getRollFinished get() = boardUiState.value.rollFinished

    val getAddMode get() = addMode.value
    val getEditMode get() = editMode.value
    val getActiveEdit get() = activeEdit.value
    val getSquareToEdit get() = squareToEdit.value
}