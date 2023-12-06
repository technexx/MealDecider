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

    private val handler = Handler(Looper.getMainLooper())
    private var colorListRunnable = Runnable {}

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

    private fun starterColorList() : SnapshotStateList<Color> {
        val list = mutableStateListOf<Color>()
        for (i in SquareDataObject.squareValuesList) {
            list.add(Color.Gray)
        }
        return list
    }

    fun updateColorListLooper() {
        var delay: Long = 500

        colorListRunnable = Runnable {
            val indexRoll = Random.nextInt(0, squareList.size)

            val newColorList = colorListWithRandomIndexChanged(Color.Red, indexRoll)
            updateColorList(newColorList)

            handler.postDelayed(colorListRunnable, delay)
            delay -= 20

            if (delay < 100) {
                showLog("test", "final roll is $indexRoll")
                updateSelectedSquare(SquareDataObject.squareValuesList[indexRoll])
                showLog("test", "selected square is $selectedSquare")

                handler.removeCallbacks(colorListRunnable)
            }
        }

        handler.post((colorListRunnable))
    }

    private fun colorListWithRandomIndexChanged(color: Color, index: Int): SnapshotStateList<Color> {
        val list = SnapshotStateList<Color>()
        for (i in squareList) {
            list.add(Color.Gray)
        }
        list[index] = color

        return list
    }

    private fun updateColorList(list: SnapshotStateList<Color>) {
        _boardUiState.update { currentState ->
            currentState.copy(colorList = list)
        }
    }

    private fun updateSelectedSquare(squareValues: SquareValues) {
        _boardUiState.update { currentState ->
            currentState.copy(selectedSquare = squareValues)
        }
    }

    fun rollRandomSquare(numberOfSquares: Int): Int {
        return Random.nextInt(0, numberOfSquares)
    }

    val selectedSquare get() = boardUiState.value.selectedSquare
    val squareList get() = boardUiState.value.squareList
    val colorList get() = boardUiState.value.colorList
}