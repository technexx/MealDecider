package meal.decider

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
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
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.room.Room
import kotlinx.coroutines.*
import meal.decider.Database.CuisineDatabase
import meal.decider.Database.RoomInteractions
import meal.decider.ui.theme.MealDeciderTheme
import java.io.*

@SuppressLint("StaticFieldLeak")
private lateinit var activity: Activity
@SuppressLint("StaticFieldLeak")
private lateinit var activityContext : Context
@SuppressLint("StaticFieldLeak")
private lateinit var appContext : Context
@SuppressLint("StaticFieldLeak")
private lateinit var appViewModel : AppViewModel
private lateinit var cuisineDatabase: CuisineDatabase.AppDatabase
@SuppressLint("StaticFieldLeak")
private lateinit var dialogComposables : DialogComposables
private lateinit var roomInteractions: RoomInteractions
@SuppressLint("StaticFieldLeak")
private lateinit var mapInteractions: MapInteractions
val scope = CoroutineScope(Job() + Dispatchers.IO)

//TODO: Randomization speed/duration options.
//TODO: Keep statistics (how many rolls, how many re-rolls, how many maps opened, etc.)
//TODO: Selection between restaurants within category.

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity = this@MainActivity
        activityContext = this@MainActivity
        appContext = applicationContext

        appViewModel = AppViewModel()
        cuisineDatabase = Room.databaseBuilder(appContext, CuisineDatabase.AppDatabase::class.java, "cuisine-database").build()
        roomInteractions = RoomInteractions(cuisineDatabase, appViewModel)
        dialogComposables = DialogComposables(activityContext, appViewModel, cuisineDatabase)

        mapInteractions = MapInteractions(activity, activityContext)
        mapInteractions.fusedLocationListener()

        //Populates SquareValues and DB with default only if empty (i.e. app launched for first time).
        scope.launch {
            if (roomInteractions.cuisineDao.getAllCuisines().isEmpty()) {
                roomInteractions.setSquareValuesAndDatabaseToDefaultStartingValues()
            }
        }

        //Populates SquareValues with DB values and set first cuisine as default selection.
        scope.launch {
            roomInteractions.populateSquareValuesWithDatabaseValues()
            appViewModel.updateSelectedSquare(appViewModel.getSquareList[0])
        }

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
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
    var expanded by remember { mutableStateOf(false) }
    val editMode = appViewModel.editMode.collectAsStateWithLifecycle()
    val listOfSquaresToEdit = appViewModel.listOfSquaresToEdit.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
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
                    if (listOfSquaresToEdit.value.isNotEmpty() && editMode.value) {
                        IconButton(onClick = {
                            coroutineScope.launch {
                                roomInteractions.deleteMultipleCuisines()
                                appViewModel.deleteSelectedCuisines()
                            }

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
//                            DropDownMenuItemUi(text = "Options") {
//                                appViewModel.updateOptionsMode(true)
//                                appViewModel.updateEditMode(false)
//                                expanded = false
//                            }
                            DropDownMenuItemUi(text = "Add Cuisine") {
                                appViewModel.updateAddMode(true)
                                appViewModel.updateEditMode(false)
                                expanded = false
                            }
                            DropDownMenuItemUi(text = "Edit Cuisines") {
                                //Resets list of squares to edit.
                                appViewModel.updateListOfSquaresToEdit(listOf())

                                if (!appViewModel.getEditMode) {
                                    appViewModel.updateEditMode(true)
                                } else {
                                    appViewModel.updateEditMode(false)
                                }
                                expanded = false
                            }
                            DropDownMenuItemUi(text = "Sort Alphabetically") {
                                appViewModel.sortAndUpdateCuisineList("alphabetical")
                                coroutineScope.launch {
                                    roomInteractions.deleteAllCuisines()
                                    roomInteractions.insertMultipleCuisines(appViewModel.getListOfSquareNames())
                                }
                                appViewModel.updateEditMode(false)
                                expanded = false
                            }
                            DropDownMenuItemUi(text = "Sort Randomly") {
                                appViewModel.sortAndUpdateCuisineList("random")
                                coroutineScope.launch {
                                    roomInteractions.deleteAllCuisines()
                                    roomInteractions.insertMultipleCuisines(appViewModel.getListOfSquareNames())
                                }
                                appViewModel.updateEditMode(false)
                                expanded = false
                            }
                            DropDownMenuItemUi(text = "Restore Defaults") {
                                appViewModel.updateRestoreDefaults(true)
                                expanded = false
                            }
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
fun DropDownMenuItemUi(text: String, function: () -> Unit) {
    DropdownMenuItem(
        text = { Text(text) },
        onClick = {
            function()
        }
    )
}

@Composable
fun Board() {
    Column (modifier = Modifier
        .fillMaxWidth()
        .background(colorResource(id = R.color.grey_50))
    ) {
        OptionsBarLayout((screenHeight() * 0.1))
        SelectionGridLayout(screenHeight() * 0.65)
        InteractionLayout(screenHeight() * 0.15)
    }
}

@Composable
fun OptionsBarLayout(height: Double) {
    val restrictionsUi = appViewModel.restrictionsList.collectAsStateWithLifecycle()
    var cardColor: Color

    Column (modifier = Modifier
        .fillMaxWidth()
        .height(height.dp)
        .background(colorResource(id = R.color.grey_50))
    ) {
        LazyHorizontalGrid(rows = GridCells.Adaptive(minSize = 32.dp),
            contentPadding = PaddingValues(
                start = 12.dp,
                top = 16.dp,
                end = 12.dp,
                bottom = 16.dp),
            content = {
                items(restrictionsUi.value.size) { index ->
                    if (appViewModel.getRestrictionsList[index].selected) {
                        cardColor = colorResource(id = R.color.blue_grey_100)
                    } else  {
                        cardColor = Color.White
                    }
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = cardColor,
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        ),
                        modifier = Modifier
                            .padding(4.dp)
                            .selectable(
                                selected = true,
                                onClick = {
                                    val list = appViewModel.getRestrictionsList
                                    list[index].selected = !list[index].selected
                                    val updatedList = mutableStateListOf<RestrictionsValues>()
                                    updatedList.addAll(list)

                                    appViewModel.updateRestrictionsList(updatedList)
                                }
                            ),
                    ) {
                        Text(
                            text = restrictionsUi.value[index].name,
                            fontWeight = FontWeight.Medium,
                            fontSize = 14.sp,
                            color = Color.Black,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
            }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SelectionGridLayout(height: Double) {
    val boardUiState = appViewModel.boardUiState.collectAsStateWithLifecycle()
    val sectionGridState = rememberLazyGridState()
    val addMode = appViewModel.addMode.collectAsStateWithLifecycle()
    val editMode = appViewModel.editMode.collectAsStateWithLifecycle()
    val activeEdit = appViewModel.activeEdit.collectAsStateWithLifecycle()
    val restoreDefaults = appViewModel.restoreDefaults.collectAsStateWithLifecycle()
    val optionsMode = appViewModel.optionsMode.collectAsStateWithLifecycle()

    val borderStroke: BorderStroke

    if (editMode.value) {
        borderStroke = BorderStroke(3.dp,Color.Black)
    } else {
        borderStroke = BorderStroke(1.dp,Color.Black)
        appViewModel.resetSquareColors()
    }

    if (addMode.value) {
        dialogComposables.AddDialogBox()
    }

    if (restoreDefaults.value) {
        dialogComposables.ConfirmRestoreDefaultsDialog()
    }

//    if (activeEdit.value) {
//        dialogComposables.EditDialogBox()
//    }

    if (optionsMode.value) {
        dialogComposables.OptionsDialog()
    }

    LazyVerticalGrid(state = sectionGridState,
        modifier = Modifier
            .height(height.dp),
        columns = GridCells.Adaptive(minSize = 128.dp),
        contentPadding = PaddingValues(
            start = 12.dp,
            top = 16.dp,
            end = 12.dp,
            bottom = 16.dp
        ),
        content = {
            items(boardUiState.value.squareList.size) { index ->
                if (appViewModel.getRollFinished) {
                    LaunchedEffect(key1 = Unit) {
                        sectionGridState.animateScrollToItem(appViewModel.rolledSquareIndex)
                        appViewModel.updateRollFinished(false)
                    }
                }

                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = colorResource(id = appViewModel.getSquareList[index].color),
                    ),
                    border = borderStroke,
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    modifier = Modifier
                        .padding(6.dp)
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
fun InteractionLayout(height: Double) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val rollFinished = appViewModel.rollFinished.collectAsStateWithLifecycle()
    val selectedSquare = appViewModel.selectedSquare.collectAsStateWithLifecycle()
    val restrictionsUi = appViewModel.restrictionsList.collectAsStateWithLifecycle()

    val restrictionsString = appViewModel.foodRestrictionsString(restrictionsUi.value)
    val foodUri = selectedSquare.value.name + "+" + "Food" + "+" + restrictionsString
//    val foodUri = "geo:0,0?q=" + selectedSquare.value.name + " Food " + restrictionsString

    Column (
        modifier = Modifier
            .wrapContentHeight()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Button(
                onClick = {
                    if (!appViewModel.getRollEngaged && !appViewModel.getEditMode) {
                        appViewModel.rollCuisine()
//                        appViewModel.pressYourLuck()
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
                        coroutineScope.launch {
                            mapInteractions.cuisineType = foodUri
                            mapInteractions.makeApiCall()
                        }
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