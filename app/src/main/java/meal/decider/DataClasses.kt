package meal.decider

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList

data class BoardValues(
    var squareList: SnapshotStateList<SquareValues> = mutableStateListOf(),
)

data class SquareValues(
    var name: String = "",
    var color: Int = 0
)

data class RestrictionsValues(
    var name: String = "",
    var selected: Boolean = false
)