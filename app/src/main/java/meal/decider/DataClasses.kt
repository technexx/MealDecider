package meal.decider

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color

data class BoardValues(
    var selectedSquare: SquareValues = SquareDataObject.squareValuesList[0],
    var squareList: SnapshotStateList<SquareValues> = mutableStateListOf(),
    var colorList: SnapshotStateList<Color> = mutableStateListOf(),
    var rollFinished: Boolean = false,
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
        ),
        SquareValues(
            "Greek"
        ),
        SquareValues(
            "Indian"
        ),
        SquareValues(
            "Korean"
        ),
        SquareValues(
            "Thai"
        ),
        SquareValues(
            "Vietnamese"
        ),
        SquareValues(
            "Cuban"
        ),
        SquareValues(
            "Ethiopian"
        ),
        SquareValues(
            "French"
        ),
    )
}