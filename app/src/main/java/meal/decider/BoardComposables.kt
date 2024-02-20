package meal.decider

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import meal.decider.Database.CuisineDatabase
import meal.decider.Database.RoomInteractions

//TODO: Restaurant selection should default to index 0 when launching Dialog (blank at start and uses old uri when exiting and re-opening, e.g. if we re-roll cuisine and launch new Dialog, old uri will remain).
//TODO: Restaurant selection may want to reset to index 0 when closing Dialog.
//TODO: Include categories as a parent of cuisines: e.g. fast food, fine dining, etc.

class BoardComposables (private val appViewModel: AppViewModel, private val appDatabase: CuisineDatabase.AppDatabase, private val roomInteractions: RoomInteractions, private val mapInteractions: MapInteractions, private val runnables: Runnables) {

    private val buttons = Buttons(appViewModel, mapInteractions, runnables)
    private val dialogComposables = DialogComposables(appViewModel, appDatabase, mapInteractions, runnables)

    @Composable
    fun BoardUi() {
        TopBar {
            Board()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    //"Void" Composable input.
    fun TopBar(content: @Composable (() -> Unit)) {
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        var expanded by remember { mutableStateOf(false) }
        val editMode = appViewModel.editMode.collectAsStateWithLifecycle()
        val listOfCuisineSquaresToEdit = appViewModel.listOfCuisineSquaresToEdit.collectAsStateWithLifecycle()
        val optionsMode = appViewModel.optionsMode.collectAsStateWithLifecycle()
        val cuisineSelectionMode = appViewModel.cuisineSelectionMode.collectAsStateWithLifecycle()
        val coroutineScope = rememberCoroutineScope()

        val tint: Color = if (cuisineSelectionMode.value) Color.Red; else Color.White

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
                        if (listOfCuisineSquaresToEdit.value.isNotEmpty() && editMode.value) {
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
                        IconButton(onClick = {
                            appViewModel.updateCuisineSelectionMode(!appViewModel.getCuisineSelectionMode)
                        }) {
                            Icon(modifier = Modifier,
                                imageVector = Icons.Filled.Create,
                                contentDescription = "Select",
                                tint = tint
                            )
                        }
                        Box(
                            modifier = Modifier
                                .wrapContentSize(Alignment.TopEnd)
                        ) {
                            Row() {
                                IconButton(onClick = {
                                    appViewModel.updateOptionsMode(true)
                                }) {
                                    Icon(
                                        imageVector = Icons.Filled.Settings,
                                        contentDescription = "Options",
                                        tint = Color.White
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(onClick = { expanded = !expanded }) {
                                    Icon(
                                        imageVector = Icons.Filled.Menu,
                                        contentDescription = "More",
                                        tint = Color.White
                                    )
                                }
                            }

                            DropdownMenu(modifier = Modifier
                                .background(colorResource(id = R.color.white)),
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                DropDownMenuItemUi(text = "Add Cuisine") {
                                    appViewModel.updateAddMode(true)
                                    appViewModel.updateEditMode(false)
                                    expanded = false
                                }
                                DropDownMenuItemUi(text = "Edit Cuisines") {
                                    //Resets list of squares to edit.
                                    appViewModel.updatelistOfCuisineSquaresToEdit(listOf())
                                    if (!appViewModel.getEditMode) {
                                        appViewModel.updateEditMode(true)
                                        appViewModel.updateAllCuisineBorders(cuisineEditModeBorderStroke)
                                    } else {
                                        appViewModel.updateEditMode(false)
                                        appViewModel.updateAllCuisineBorders(defaultCuisineBorderStroke)
                                    }
                                    expanded = false
                                }
                                DropDownMenuItemUi(text = "Sort Alphabetically") {
                                    appViewModel.sortAndUpdateCuisineList("alphabetical")
                                    coroutineScope.launch {
//                                    roomInteractions.updateCuisines(appViewModel.getSquareList)
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
                content()
            }
        }
    }

    @Composable
    fun DropDownMenuItemUi(text: String, function: () -> Unit) {
        DropdownMenuItem(
            text = { Text(text = text, color = Color.Black, fontSize = 14.sp) },
            onClick = {
                function()
            }
        )
    }

    @Composable
    fun Board() {
        Column (modifier = Modifier
            .fillMaxWidth()
            .height(screenHeightPct(0.1).dp)
            .background(colorResource(id = R.color.grey_50))
        ) {
            OptionsBarLayout()
            DialogCompositions()
        }
        Surface(
            color = colorResource(id = R.color.grey_100),
        ) {
            Column(
            ) {
                Column(modifier = Modifier
                    .height(screenHeightPct(0.7).dp)) {
                    CuisineSelectionGrid()
                }
                Column(modifier = Modifier
                    .height(screenHeightPct(0.2).dp)
                    .background(colorResource(id = R.color.grey_50))
                )
                {
                    buttons.InteractionButtons()
                }
            }
        }
    }


    @Composable
    fun OptionsBarLayout() {
        val restrictionsUi = appViewModel.restrictionsList.collectAsStateWithLifecycle()
        var cardColor: Color

        Column (modifier = Modifier
            .fillMaxWidth()
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

    @Composable
    fun DialogCompositions() {
        val addMode = appViewModel.addMode.collectAsStateWithLifecycle()
        val showRestaurants = appViewModel.showRestaurants.collectAsStateWithLifecycle()
        val restoreDefaults = appViewModel.restoreDefaults.collectAsStateWithLifecycle()
        val optionsMode = appViewModel.optionsMode.collectAsStateWithLifecycle()

        if (addMode.value) {
            dialogComposables.AddDialogBox()
        }

        if (restoreDefaults.value) {
            dialogComposables.ConfirmRestoreDefaultsDialog()
        }

        if (optionsMode.value) {
            dialogComposables.OptionsDialog()
        }

        if (showRestaurants.value) {
            dialogComposables.RestaurantDialog()
        }
    }

    @Composable
    fun CuisineSelectionGrid() {
        val coroutineScope = rememberCoroutineScope()
        val boardUiState = appViewModel.boardUiState.collectAsStateWithLifecycle()
        val cuisineRollFinished = appViewModel.cuisineRollFinished.collectAsStateWithLifecycle()
        val sectionGridState = rememberLazyGridState()

        val restrictionsUi = appViewModel.restrictionsList.collectAsStateWithLifecycle()
        val selectedCuisineSquare = appViewModel.selectedCuisineSquare.collectAsStateWithLifecycle()
        val restrictionsString = foodRestrictionsString(restrictionsUi.value)

        val rolledCuisineString = selectedCuisineSquare.value.name + " Food " + restrictionsString

        val editMode = appViewModel.editMode.collectAsStateWithLifecycle()
        var borderStroke: BorderStroke

        if (cuisineRollFinished.value) {
            LaunchedEffect(Unit) {
                coroutineScope.launch {
                    sectionGridState.animateScrollToItem(appViewModel.rolledSquareIndex)
                    runnables.cuisineBorderStrokeToggleAnimation(2000, 200)
                    //mapInteractions.testRestaurants()
                    appViewModel.restaurantSearchCuisineType = rolledCuisineString

                    delay(2000)
                    appViewModel.updateCuisineRollFinished(false)
                }
            }
        }

        LazyVerticalGrid(state = sectionGridState,
            modifier = Modifier,
            columns = GridCells.Adaptive(minSize = 128.dp),
            contentPadding = PaddingValues(
                start = 12.dp,
                top = 16.dp,
                end = 12.dp,
                bottom = 16.dp
            ),
            content = {
                items(boardUiState.value.squareList.size) { index ->
                    borderStroke = appViewModel.getSquareList[index].border
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
                                        appViewModel.toggleEditCuisineHighlightAndAddHighlightedCuisinesToEditList(index)
                                    }
                                    if (appViewModel.getCuisineSelectionMode) {
                                        appViewModel.updateSelectedCuisineSquare(appViewModel.getSquareList[index])
                                        appViewModel.cuisineStringUri = appViewModel.selectedCuisineSquare.value.name + " Food " + foodRestrictionsString(appViewModel.getRestrictionsList)
                                        appViewModel.updateSingleCuisineSquareColorAndBorder(index, chosenSquareColor, heavyCuisineSelectionBorderStroke)
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
}