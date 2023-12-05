package meal.decider

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class BoardValues(
    var selectedSquare: Int = 0,
    var numberList: SnapshotStateList<Int> = mutableStateListOf(),
    var squareList:SnapshotStateList<SquareValues> = mutableStateListOf()
)

data class SquareValues(
    var name: String = "",
)

object SquareDataObject {
    var squareValuesList = mutableListOf(
        SquareValues(
            "American",
        ),
        SquareValues(
            "Chinese"
        ),
        SquareValues(
            "Mexican"
        ),
        SquareValues(
            "Italian"
        )
    )
}