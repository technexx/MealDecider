package meal.decider

import android.content.Context
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
        showLog("test", "list size is ${list.size}")
        return list
    }

    fun createColorList() {
        _boardUiState.update { currentState ->
            currentState.copy(colorList = starterColorList())
        }
    }

    private fun starterColorList() : SnapshotStateList<Color> {
        val list = mutableStateListOf<Color>()
        for (i in SquareDataObject.squareValuesList) {
            list.add(Color.Gray)
        }
        return list
    }

    fun updateColorListItem(index: Int, color: Color) {
        val list = SnapshotStateList<Color>()
        for (i in SquareDataObject.squareValuesList) {
            list.add(Color.Gray)
        }
        list[index] = color

        _boardUiState.update { currentState ->
            currentState.copy(colorList = list)
        }
    }

    fun updateSelectedSquare(square: Int) {
        _boardUiState.update { currentState ->
            currentState.copy(selectedSquare = square)
        }
    }

    fun rollRandomSquare(numberOfSquares: Int): Int {
        return Random.nextInt(0, numberOfSquares)
    }

    val selectedSquare get() = boardUiState.value.selectedSquare
    val numberList get() = boardUiState.value.numberList
    val squareList get() = boardUiState.value.squareList
    val colorList get() = boardUiState.value.colorList
}