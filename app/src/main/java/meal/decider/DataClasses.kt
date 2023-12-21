package meal.decider

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color

val starterCuisineList = mutableStateListOf(
    "American", "Chinese", "Mexican", "Italian", "Greek", "Indian", "Korean", "Thai", "Vietnamese", "Cuban", "Ethiopian", "French"
)

var fullCuisineList = mutableStateListOf(
    "American", "Angolan", "Arab", "Argentine", "Australian", "Austrian", "Belgian", "Bosnian", "Brazilian", "Cambodian", "Canadian", "Chilean", "Chinese", "Columbian", "Congolese", "Croatian", "Czech", "Dutch", "Egyptian", "Ethiopian", "Filipino", "Finnish", "French", "German", "Greek", "Hungarian", "Indian", "Indonesian", "Irish", "Israeli", "Italian", "Jamaican", "Japanese", "Malaysian", "Mongolian", "New Zealand", "Nigerian", "Norwegian", "Pakistani", "Peruvian", "Polish", "Portuguese", "Russian", "Serbian", "Slovak", "Slovenian", "Spanish", "Swedish", "Tanzanian", "Thai", "Turkish", "Ukrainian", "Venezuelan", "Vietnamese"
)
data class BoardValues(
    var selectedSquare: SquareValues = SquareValues("", 0),
    var squareList: SnapshotStateList<SquareValues> = mutableStateListOf(),
)

data class SquareValues(
    var name: String = "",
    var color: Int = 0
)