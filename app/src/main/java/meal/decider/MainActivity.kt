package meal.decider

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import meal.decider.ui.theme.MealDeciderTheme

//TODO: Categories (Vegan, etc.)
//TODO: Regions/continents

@SuppressLint("StaticFieldLeak")
private lateinit var appViewModel : AppViewModel
@SuppressLint("StaticFieldLeak")
private lateinit var activityContext : Context
@SuppressLint("StaticFieldLeak")
private lateinit var dialogComposables : DialogComposables

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appViewModel = AppViewModel()
        activityContext = this@MainActivity

        appViewModel.createSquareList()
        appViewModel.updateSelectedSquare(appViewModel.getSquareList[0])

        dialogComposables = DialogComposables(activityContext, appViewModel)

        setContent {
            MealDeciderTheme {
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
    val listOfSquaresToEdit = appViewModel.listOfSquaresToEdit.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var expanded by remember { mutableStateOf(false) }
    var filterMenu by remember { mutableStateOf(false) }

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
                    if (listOfSquaresToEdit.value.isNotEmpty()) {
                        IconButton(onClick = {
                            appViewModel.deleteSelectedCuisines()
                            appViewModel.updateEditMode(false)
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
                                text = { Text("Options") },
                                onClick = {
                                    appViewModel.updateOptionsMode(true)
                                    appViewModel.updateEditMode(false)
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Add Cuisine") },
                                onClick = {
                                    appViewModel.updateAddMode(true)
                                    appViewModel.updateEditMode(false)
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Edit Cuisines") },
                                onClick = {
                                    appViewModel.updateEditMode(!appViewModel.getEditMode)
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Sort Alphabetically") },
                                onClick = {
                                    appViewModel.sortAndUpdateCuisineList("alphabetical")
                                    appViewModel.updateEditMode(false)
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Sort Randomly") },
                                onClick = {
                                    appViewModel.sortAndUpdateCuisineList("random")
                                    appViewModel.updateEditMode(false)
                                    expanded = false
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Restore Defaults") },
                                onClick = {
                                    appViewModel.createSquareList()
                                    appViewModel.updateSelectedSquare(appViewModel.getSquareList[0])
                                    appViewModel.updateSelectedSquareIndex(0)
                                    appViewModel.updateEditMode(false)
                                    appViewModel.updateRollFinished(false)
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
    val boardUiState = appViewModel.boardUiState.collectAsStateWithLifecycle()
    val addMode = appViewModel.addMode.collectAsStateWithLifecycle()
    val editMode = appViewModel.editMode.collectAsStateWithLifecycle()
    val activeEdit = appViewModel.activeEdit.collectAsStateWithLifecycle()
    val optionsMode = appViewModel.optionsMode.collectAsStateWithLifecycle()

    val borderStroke: BorderStroke = if (editMode.value) {
        BorderStroke(3.dp,Color.Black)
    } else {
        BorderStroke(1.dp,Color.Black)
    }

    if (addMode.value) {
        dialogComposables.AddDialogBox()
    }

    if (activeEdit.value) {
        dialogComposables.EditDialogBox()
    }

    if (optionsMode.value) {
        dialogComposables.OptionsDialog()
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
                        containerColor = colorResource(id = appViewModel.getSquareList[index].color),
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
                                if (appViewModel.getEditMode) {
                                    appViewModel.toggleEditCuisineHighlight(index)
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
    val rollFinished = appViewModel.rollFinished.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val selectedSquare = appViewModel.selectedSquare.collectAsStateWithLifecycle()
    val foodUri = "geo:0,0?q=" + selectedSquare.value.name + " Food"

    Column (
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (rollFinished.value) {
            Text(text = context.getString(R.string.meal_decided, appViewModel.getSelectedSquare.name), color = Color.Black, fontSize = 22.sp)
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
                    if (!appViewModel.getRollEngaged && !appViewModel.getEditMode) {
                        appViewModel.updateColorOfSquareValuesList()
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
                    if (!appViewModel.getRollEngaged && !appViewModel.getEditMode) {
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
                if (!appViewModel.doesCuisineExistsOnBoard(
                        list[index],
                        appViewModel.squareNamesList()
                    )
                ) {
                    appViewModel.addSquareToList(list[index])
                    appViewModel.updateAddMode(false)
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