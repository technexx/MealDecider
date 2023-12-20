package meal.decider

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color

object CuisineDataObject {
    var cuisineList = mutableStateListOf(
        "American", "Angolan", "Arab", "Argentine", "Australian", "Austrian", "Belgian", "Bosnian", "Brazilian", "Cambodian", "Canadian", "Chilean", "Chinese", "Columbian", "Congolese", "Croatian", "Czech", "Dutch", "Egyptian", "Ethiopian", "Filipino", "Finnish", "French", "German", "Greek", "Hungarian", "Indian", "Indonesian", "Irish", "Israeli", "Italian", "Jamaican", "Japanese", "Malaysian", "Mongolian", "New Zealand", "Nigerian", "Norwegian", "Pakistani", "Peruvian", "Polish", "Portuguese", "Russian", "Serbian", "Slovak", "Slovenian", "Spanish", "Swedish", "Tanzanian", "Thai", "Turkish", "Ukrainian", "Venezuelan", "Vietnamese"
    )
}

data class BoardValues(
    var selectedSquare: SquareValues = SquareDataObject.squareValuesList[0],
    var squareList: SnapshotStateList<SquareValues> = mutableStateListOf(),
    var colorList: SnapshotStateList<Int> = mutableStateListOf(),
    var rollEngaged: Boolean = false,
    var rollFinished: Boolean = false,
)

data class SquareValues(
    var name: String = "",
    var color: Int = 0
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