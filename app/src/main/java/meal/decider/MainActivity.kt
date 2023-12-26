package meal.decider



import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import meal.decider.ui.theme.MealDeciderTheme
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.window.Dialog
import java.util.stream.Collectors.toList

//TODO: Edit should not allow changing cuisine.
//TODO: Categories (Vegan, etc.)

@SuppressLint("StaticFieldLeak")
private lateinit var gameViewModel : GameViewModel
@SuppressLint("StaticFieldLeak")
private lateinit var activityContext : Context

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        gameViewModel = GameViewModel(application)
        activityContext = this@MainActivity

        gameViewModel.createSquareList()
        gameViewModel.updateSelectedSquare(gameViewModel.getSquareList[0])

        setContent {
            MealDeciderTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column {
                        TopBar()
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
    val listOfSquareIndicesToEdit = gameViewModel.listOfSquareIndicesToEdit.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var expanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.smallTopAppBarColors(
                    containerColor = colorResource(id = R.color.blue_600),
                    titleContentColor = Color.White,
                ),
                title = {
                    Text("Meal Decider")
                },
                actions = {
                    if (listOfSquareIndicesToEdit.value.isNotEmpty()) {
                        IconButton(onClick = {
                            deleteSelectedCuisines()
                            gameViewModel.updateEditMode(false)
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Delete"
                            )
                        }
                    }
                    Box(
                        modifier = Modifier
                            .wrapContentSize(Alignment.TopEnd)
                    ) {
                        IconButton(onClick = { expanded = !expanded }) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "More"
                            )
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Add Cuisine") },
                                onClick = {
                                    gameViewModel.updateAddMode(true)
                                    gameViewModel.updateEditMode(false)
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Edit Cuisines") },
                                onClick = {
                                    gameViewModel.updateEditMode(!gameViewModel.getEditMode)
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Sort Alphabetically") },
                                onClick = {
                                    sortAndUpdateCuisineList("alphabetical")
                                    gameViewModel.updateEditMode(false)
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Sort Randomly") },
                                onClick = {
                                    sortAndUpdateCuisineList("random")
                                    gameViewModel.updateEditMode(false)
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Restore Defaults") },
                                onClick = {
                                    gameViewModel.updateEditMode(false)

                                    expanded = false
                                }
                            )
                        }
                    }
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
        ) {
            Board()
        }
    }
}

fun sortAndUpdateCuisineList(typeOfSort: String) {
    var squareNames = gameViewModel.getSquaresNameList()
    val currentSquareList = gameViewModel.getSquareList
    val newSquareList: SnapshotStateList<SquareValues> = SnapshotStateList()
    val selectedSquareName = gameViewModel.getSelectedSquare.name

    if (typeOfSort == "alphabetical") squareNames = squareNames.sorted().toMutableList()
    if (typeOfSort == "random") squareNames = squareNames.shuffled().toMutableList()

    for (i in squareNames.indices) {
        newSquareList.add(SquareValues(squareNames[i], currentSquareList[i].color))
    }

    for (i in 0 until newSquareList.size) {
        if (!newSquareList[i].name.equals(selectedSquareName, true)) {
            newSquareList[i] = SquareValues(newSquareList[i].name, defaultSquareColor)
        } else {
            newSquareList[i] = SquareValues(newSquareList[i].name, chosenSquareColor)
        }
    }

    gameViewModel.updateSquareList(newSquareList)
}

fun toggleEditCuisineHighlight(index: Int) {
    val tempSquareList = gameViewModel.getSquareList

    if (index == gameViewModel.getSelectedSquareIndex) {
        if (tempSquareList[index].color == chosenSquareColor) {
            tempSquareList[index] = SquareValues(tempSquareList[index].name, editSquareColor)
            addSquareToListOfSquareIndicesToUpdate(index)
        } else {
            tempSquareList[index] = SquareValues(tempSquareList[index].name, chosenSquareColor)
            removeSquareFromListOfSquareIndicesToUpdate(index)
        }
    } else {
        if (tempSquareList[index].color == defaultSquareColor) {
            tempSquareList[index] = SquareValues(tempSquareList[index].name, editSquareColor)
            addSquareToListOfSquareIndicesToUpdate(index)
        } else {
            tempSquareList[index] = SquareValues(tempSquareList[index].name, defaultSquareColor)
            removeSquareFromListOfSquareIndicesToUpdate(index)
        }
    }

    gameViewModel.updateSquareList(tempSquareList)

    println("index list (as added) is ${gameViewModel.getListOfSquareIndicesToEdit}")

}

fun addSquareToListOfSquareIndicesToUpdate(index: Int) {
    val tempList = gameViewModel.getListOfSquareIndicesToEdit.toMutableList()
    tempList.add(index)
    gameViewModel.updateListOfSquareIndicesToEdit(tempList)
}

fun removeSquareFromListOfSquareIndicesToUpdate(index: Int) {
    val tempList = gameViewModel.getListOfSquareIndicesToEdit.toMutableList()
    tempList.remove(index)
    gameViewModel.updateListOfSquareIndicesToEdit(tempList)
}

//TODO: Some wrong deletions. Also deletes one less entry if last one is selected.
fun deleteSelectedCuisines() {
    val listOfIndices = gameViewModel.getListOfSquareIndicesToEdit
    val tempList = gameViewModel.getSquareList

    println("index list (deleting) is $listOfIndices")
    println("square list pre delete is ${tempList.toList()}")

    for (i in listOfIndices) {
        if (i <= tempList.size-1) {
         tempList.removeAt(i)
        }
    }

    println("square list post delete is ${tempList.toList()}")

    gameViewModel.updateListOfSquareIndicesToEdit(listOf())
    gameViewModel.updateSquareList(tempList)
    resetSquareColors()
}

fun resetSquareColors() {
    val squareList = gameViewModel.getSquareList
    val selectedSquare = gameViewModel.getSelectedSquare

    for (i in squareList) {
        i.color = defaultSquareColor
        if (i.name.equals(selectedSquare.name, true)) {
            i.color = chosenSquareColor
        }
    }

    //Set first square index to selected if previous one no longer exists.
//    if (!doesSelectedSquareExist()) {
//        squareList[0].color = chosenSquareColor
//        gameViewModel.updateSelectedSquare(squareList[0])
//        gameViewModel.updateSelectedSquareIndex(0)
//    }
}

fun doesSelectedSquareExist() : Boolean {
    val squareList = gameViewModel.getSquareList
    val selectedSquare = gameViewModel.getSelectedSquare

    for (i in squareList) {
        if (i.name.equals(selectedSquare.name)) return true
    }
    return false
}

@Composable
fun Board() {
    Column (modifier = Modifier
        .fillMaxWidth()
        .height((screenHeight() * 1).dp)
        .background(colorResource(id = R.color.grey_50))
    ) {
        SelectionGridLayout()
        Spacer(modifier = Modifier.height(16.dp))
        InteractionLayout()
    }
}

@Composable
fun SelectionGridLayout() {
    val boardUiState = gameViewModel.boardUiState.collectAsStateWithLifecycle()
    val addMode = gameViewModel.addMode.collectAsStateWithLifecycle()
    val editState = gameViewModel.editMode.collectAsStateWithLifecycle()
    val activeEdit = gameViewModel.activeEdit.collectAsStateWithLifecycle()

    var borderStroke: BorderStroke

    borderStroke = BorderStroke(3.dp,Color.Black)

    if (editState.value) {
        borderStroke = BorderStroke(3.dp,Color.Black)
    } else {
        borderStroke = BorderStroke(1.dp,Color.Black)
    }

    if (addMode.value) {
        AddDialogBox()
    }

    if (activeEdit.value) {
        EditDialogBox()
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        contentPadding = PaddingValues(
            start = 12.dp,
            top = 16.dp,
            end = 12.dp,
            bottom = 16.dp
        ),
        content = {
            items(boardUiState.value.squareList.size) { index ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = colorResource(id = gameViewModel.getSquareList[index].color),
                    ),
                    border = borderStroke,
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    modifier = Modifier
                        .padding(4.dp)
                        .selectable(
                            selected = true,
                            onClick = {
                                if (gameViewModel.getEditMode) {
                                    toggleEditCuisineHighlight(index)
                                }
                            }
                        ),
                ) {
                    Text(
                        text = boardUiState.value.squareList[index].name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    )
}

@SuppressLint("MissingPermission")
@Composable
fun InteractionLayout() {
    val rollFinished = gameViewModel.rollFinished.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var foodUri by remember { mutableStateOf("") }

    Column (
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        foodUri = "geo:0,0?q=" + gameViewModel.getSelectedSquare.name + " Food"

        if (rollFinished.value) {
            Text(text = context.getString(R.string.meal_decided, gameViewModel.getSelectedSquare.name), color = Color.Black, fontSize = 22.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    if (!gameViewModel.getRollEngaged && !gameViewModel.getEditMode) {
                        gameViewModel.updateColorOfSquareValuesList()
                    }
                },
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.blue_400)),

            ) {
                ButtonText(text = "Decide")
            }

            Spacer(modifier = Modifier.width(24.dp))

            Button(
                onClick = {
                    if (!gameViewModel.getRollEngaged && !gameViewModel.getEditMode) {
                        mapIntent(Uri.parse(foodUri))
                    }
                },
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.blue_400)),
            ) {
                ButtonText(text = "Open Maps")
            }
        }
    }
}

//List (or any object) in State<Object> is accessed w/ (Var).value.
@Composable
fun FullCuisineList(listToDisplay: State<List<String>>) {
    LazyColumn (
        modifier = Modifier
            .height(200.dp)
            .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        items (listToDisplay.value.size) { index ->
            CuisineListUi(list = listToDisplay.value, index, text = listToDisplay.value[index])
        }
    }
}

@Composable
fun CuisineListUi(list: List<String>, index: Int, text: String) {
    Column (modifier = Modifier
        .padding(4.dp)
        .selectable(
            selected = true,
            onClick = {
                if (!doesCuisineExistsOnBoard(
                        list[index],
                        gameViewModel.getSquaresNameList()
                    )
                ) {
                    gameViewModel.addSquareToList(list[index])
                    gameViewModel.updateAddMode(false)
                } else {
                    Toast
                        .makeText(activityContext, "Cuisine already exists!", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        )) {
        Text(modifier = Modifier
            .padding(4.dp),
            fontSize = 20.sp,
            color = Color.Black,
            text = text )
    }
}

fun filterList(list: List<String>, searchString: String) : List<String> {
    //If search string equals the first X characters typed, filter list with just those matching entries. If search string is empty, display full list.
    return if (searchString != "") {
        list.filter { a -> a.substring(0, searchString.length).equals(searchString, true) }
    } else {
        list
    }
}

fun doesCuisineExistsOnBoard(cuisineToAdd: String, listOfCuisines: List<String>): Boolean {
    for (i in listOfCuisines) {
        if (cuisineToAdd.equals(i, true)) return true
    }
    return false
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddDialogBox() {
    var txtField by remember { mutableStateOf("") }
    val displayedList = gameViewModel.displayedCuisineList.collectAsStateWithLifecycle()
    var searchTerms : List<String>

    //Search box will begin with full list shown.
    gameViewModel.updateDisplayedCuisineList(fullCuisineList)

    Dialog(onDismissRequest = {
        gameViewModel.updateAddMode(false)
    })
    {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = colorResource(id = R.color.grey_300)
        ) {
            Box(modifier = Modifier
                .size(height = 400.dp, width = 300.dp),
            ) {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly)
                {
                    Spacer(modifier = Modifier.height(10.dp))
                    TextField(
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        value = txtField,
                        onValueChange = {
                            txtField = it
                            searchTerms = filterList(fullCuisineList, txtField)
                            gameViewModel.updateDisplayedCuisineList(searchTerms)},
                        singleLine = true,
                        textStyle = TextStyle(color = Color.Black, fontSize = 22.sp, fontWeight = FontWeight.Bold),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = colorResource(id = R.color.grey_50),
                        ),
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    FullCuisineList(displayedList)

                    Row (modifier = Modifier
                        .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        IconButton(onClick = {
                            gameViewModel.updateAddMode(false)
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "",
                                tint = colorResource(android.R.color.holo_red_light),
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(50.dp)
                            )
                        }
                        IconButton(onClick = {
                            if (!doesCuisineExistsOnBoard(txtField, gameViewModel.getSquaresNameList())) {
                                gameViewModel.addSquareToList(txtField)
                                gameViewModel.updateAddMode(false)
                            } else {
                                Toast.makeText(activityContext, "Cuisine already exists!", Toast.LENGTH_SHORT).show()
                            }

                        }) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "",
                                tint = colorResource(android.R.color.holo_green_light),
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(50.dp)
                            )
                        }
                    }
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDialogBox() {
    var txtField by remember { mutableStateOf("") }

    txtField = gameViewModel.getSquareList[gameViewModel.getSquareToEdit].name

    Dialog(onDismissRequest = {
        gameViewModel.updateActiveEdit(false)
    })
    {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = colorResource(id = R.color.grey_300)
        ) {
            Box(modifier = Modifier
                .size(height = 200.dp, width = 300.dp),
            ) {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly)
                {
                    Spacer(modifier = Modifier.height(10.dp))
                    TextField(
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                        value = txtField,
                        onValueChange = { txtField = it },
                        singleLine = true,
                        textStyle = TextStyle(color = Color.Black, fontSize = 22.sp, fontWeight = FontWeight.Bold),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = colorResource(id = R.color.grey_50),
                        ),
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Row (modifier = Modifier
                        .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        IconButton(onClick = {
                            gameViewModel.updateActiveEdit(false)
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "",
                                tint = colorResource(android.R.color.holo_red_light),
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(50.dp)
                            )
                        }
                        IconButton(onClick = {
                            gameViewModel.updateSquareName(gameViewModel.getSquareToEdit, txtField)
                            gameViewModel.updateActiveEdit(false)
                        }) {
                            Icon(
                                imageVector = Icons.Filled.Check,
                                contentDescription = "",
                                tint = colorResource(android.R.color.holo_green_light),
                                modifier = Modifier
                                    .width(50.dp)
                                    .height(50.dp)
                            )
                        }
                    }
                }
            }
        }

    }
}

fun mapIntent(uri: Uri) {
    val intent = Intent(Intent.ACTION_VIEW, uri)
    intent.setPackage("com.google.android.apps.maps")

    activityContext.startActivity(intent)
}


@Composable
fun ButtonText(text: String) {
    Text(text = text, color = Color.Black, fontSize = 20.sp)
}

@Composable
private fun screenWidth() : Double {
    val configuration = LocalConfiguration.current
    return configuration.screenWidthDp.toDouble()
}

@Composable
private fun screenHeight() : Double {
    val configuration = LocalConfiguration.current
    return configuration.screenHeightDp.toDouble()
}

fun showLog(name: String, text: String) {
    Log.i(name, text)
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MealDeciderTheme {
    }
}