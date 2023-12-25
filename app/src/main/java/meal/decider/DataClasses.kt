package meal.decider

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color

data class BoardValues(
    var selectedSquare: SquareValues = SquareValues("", 0),
    var squareList: SnapshotStateList<SquareValues> = mutableStateListOf(),
)

data class SquareValues(
    var name: String = "",
    var color: Int = 0
)