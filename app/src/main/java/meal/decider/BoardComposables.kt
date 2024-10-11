package meal.decider

import android.app.Activity
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import meal.decider.Database.CuisineDatabase
import meal.decider.Database.RoomInteractions

class BoardComposables (private val appViewModel: AppViewModel, private val appDatabase: CuisineDatabase.AppDatabase, activity: Activity, private val roomInteractions: RoomInteractions, mapInteractions: MapInteractions, private val runnables: Runnables) {

    private val buttons = Buttons(appViewModel, mapInteractions, runnables)
    private val dialogComposables = DialogComposables(appViewModel, appDatabase, activity, mapInteractions, runnables)
    private val settings = Settings(appViewModel, roomInteractions)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun GlobalUi() {
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()
        val coroutineScope = rememberCoroutineScope()
        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(rememberTopAppBarState())
        val editMode = appViewModel.editMode.collectAsStateWithLifecycle()
        val listOfCuisineSquaresToEdit = appViewModel.listOfCuisineSquaresToEdit.collectAsStateWithLifecycle()
        val selectMode = appViewModel.cuisineSelectionMode.collectAsStateWithLifecycle()
        val rollEngaged = appViewModel.rollEngaged.collectAsStateWithLifecycle()
        val restaurantVisibility = appViewModel.restaurantVisibility.collectAsStateWithLifecycle()

        var selectionColor: Int
        val buttonsEnabled = !rollEngaged.value
        var expanded by remember { mutableStateOf(false) }
        if (rollEngaged.value) expanded = false

        Scaffold(
            modifier = Modifier
                .fillMaxSize(),
            topBar = {
                TopAppBar(
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = colorResource(id = colorTheme.value.appBar),
                        titleContentColor = Color.White,
                    ),
                    title = {
                        Text("Meal Decider")
                    },
                    actions = {
                        if (listOfCuisineSquaresToEdit.value.isNotEmpty() && editMode.value) {
                            MaterialIconButton(
                                icon = Icons.Filled.Delete,
                                description = "delete",
                                tint = colorTheme.value.cuisineIconButtons) {
                                coroutineScope.launch {
                                    roomInteractions.deleteMultipleCuisines()
                                    appViewModel.deleteSelectedCuisines()
                                }
                            }
                        }
//                        if (selectMode.value) {
//                            selectionColor = appViewModel.getColorTheme.selectedCuisineIcon
//                        } else {
//                            selectionColor = appViewModel.getColorTheme.cuisineIconButtons
//                        }
//                        MaterialIconButton(
//                            icon = Icons.Filled.Create,
//                            description = "select",
//                            tint = selectionColor,
//                            enabled = buttonsEnabled) {
//                            appViewModel.updateCuisineSelectionMode(!appViewModel.getCuisineSelectionMode)
//                        }
                        Box(
                            modifier = Modifier
                                .wrapContentSize(Alignment.TopEnd)
                        ) {
                            Row {
                                DropdownMenuSelections()

                                MaterialIconButton(
                                    icon = Icons.Filled.Settings,
                                    description = "settings",
                                    tint = colorTheme.value.cuisineIconButtons,
                                    enabled = buttonsEnabled) {
                                    appViewModel.updateOptionsMenuVisibility(true)
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
    fun DropdownMenuSelections() {
        val coroutineScope = rememberCoroutineScope()
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()
        val restaurantVisibility = appViewModel.restaurantVisibility.collectAsStateWithLifecycle()
        val rollEngaged = appViewModel.rollEngaged.collectAsStateWithLifecycle()
        val buttonsEnabled = !rollEngaged.value
        var expanded by remember { mutableStateOf(false) }

        MaterialIconButton(
            icon = Icons.Filled.Menu,
            description = "menu",
            tint = colorTheme.value.cuisineIconButtons,
            enabled = buttonsEnabled) {
            expanded = !expanded
        }

        //All edit mode changes occur in this composable, so if it recomps and edit mode is turned off, return all cuisine squares to original colors and apply border to selected cuisine square.
        if (!appViewModel.getEditMode) {
            appViewModel.updateAllCuisineBorders(colorTheme.value.defaultCuisineBorderStroke)
            appViewModel.updateSingleCuisineSquareColorAndBorder(appViewModel.rolledSquareIndex, appViewModel.getSquareList[appViewModel.rolledSquareIndex].color, heavyCuisineSelectionBorderStroke)
        }

        DropdownMenu(modifier = Modifier
            .background(colorResource(colorTheme.value.dropDownMenuBackground)),
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            if (restaurantVisibility.value == 0) {
                DropDownItemUi(
                    text = "Add Cuisine",
                    fontSize = 18,
                    color = colorResource(id = colorTheme.value.dialogTextColor)) {
                    appViewModel.updateAddMode(true)
                    appViewModel.updateEditMode(false)
                    expanded = false
                }
                DropDownItemUi(
                    text = "Edit Cuisines",
                    fontSize = 18,
                    color = colorResource(id = colorTheme.value.dialogTextColor)) {
                    //Resets list of squares to edit.
                    appViewModel.updateListOfCuisineSquaresToEdit(listOf())
                    if (!appViewModel.getEditMode) {
                        appViewModel.updateEditMode(true)
                        appViewModel.updateAllCuisineBorders(colorTheme.value.cuisineEditModeBorderStroke)
                    } else {
                        appViewModel.updateEditMode(false)
                    }
                    expanded = false
                }
                DropDownItemUi(
                    text = "Sort",
                    fontSize = 18,
                    color = colorResource(id = colorTheme.value.dialogTextColor)) {
                    appViewModel.updateShowDialog(appViewModel.CUISINE_SORT)
                    appViewModel.updateEditMode(false)
                    expanded = false
                }
                DropDownItemUi(
                    text = "Restore Defaults",
                    fontSize = 18,
                    color = colorResource(id = colorTheme.value.dialogTextColor)) {
                    appViewModel.updateRestoreDefaults(true)
                    expanded = false
                }
            }

            if (restaurantVisibility.value == 1) {
                DropDownItemUi(
                    text = "Filters",
                    fontSize = 18,
                    color = colorResource(id = colorTheme.value.dialogTextColor)) {
                    appViewModel.updateShowDialog(appViewModel.RESTAURANT_FILTERS)
                    expanded = false
                }

                DropDownItemUi(
                    text = "Sort",
                    fontSize = 18,
                    color = colorResource(id = colorTheme.value.dialogTextColor)) {
                    appViewModel.updateShowDialog(appViewModel.RESTAURANT_SORT)
                    expanded = false
                }
            }
        }
    }

    @Composable
    fun Board() {
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()
        val restaurantVisibility = appViewModel.restaurantVisibility.collectAsStateWithLifecycle()
        val showDialog = appViewModel.showDialog.collectAsStateWithLifecycle()

        Box(modifier = Modifier.fillMaxSize()) {
            Surface(
                color = colorResource(id = colorTheme.value.cuisineBoard),
            ) {
                Column {
                    Column (modifier = Modifier
                        .fillMaxWidth()
                        .height(screenHeightPct(0.1).dp)
                    ) {
                        RestrictionsBarLayout()
                        DialogCompositions()
                    }
                    Column(modifier = Modifier
                        .height(screenHeightPct(0.7).dp)) {
                        CuisineSelectionGrid()
                    }
                    Column(modifier = Modifier
                        .height(screenHeightPct(0.1).dp)
                        .background(colorResource(id = colorTheme.value.cuisineInteractionButtonsRow))
                    ) {
                        buttons.InteractionButtons(0)
                    }
                }
            }

            if (restaurantVisibility.value == 1) {
                AnimatedComposable(
                    modifier = Modifier.fillMaxSize(),
                    backHandler = {
                        appViewModel.updateRestaurantVisibility(0)
                    }) {
                    dialogComposables.RestaurantListContent()
                }
            }

            if (showDialog.value == appViewModel.CUISINE_SORT) {
                dialogComposables.SortDialog()
            }

            if (showDialog.value == appViewModel.RESTAURANT_SORT) {
                dialogComposables.SortDialog()
            }

            if (showDialog.value == appViewModel.RESTAURANT_FILTERS) {
                dialogComposables.RestaurantFiltersDialog()
            }
        }
    }

    @Composable
    fun RestrictionsBarLayout() {
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()
        val restrictionsUi = appViewModel.restrictionsList.collectAsStateWithLifecycle()

        Column (modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = colorTheme.value.restrictionRow))
        ) {
            LazyHorizontalGrid(rows = GridCells.Adaptive(minSize = 32.dp),
                contentPadding = PaddingValues(
                    start = 12.dp,
                    top = 16.dp,
                    end = 12.dp,
                    bottom = 16.dp),
                content = {
                    items(restrictionsUi.value.size) { index ->
                        val cardColor = if (appViewModel.getRestrictionsList[index].selected) colorResource(id = R.color.grey_500) else Color.White

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
                                        appViewModel.toggleRestrictionListItems(index)
                                    }
                                ),
                        ) {
                            RegText(text = appViewModel.getRestrictionsList[index].name,
                                fontSize = 14,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(6.dp))
                        }
                    }
                }
            )
        }
    }

    @Composable
    fun DialogCompositions() {
        val addMode = appViewModel.addMode.collectAsStateWithLifecycle()
        val restoreDefaults = appViewModel.restoreDefaults.collectAsStateWithLifecycle()
        val optionsMode = appViewModel.optionsMode.collectAsStateWithLifecycle()
        val settingsDialogVisibility = appViewModel.settingsDialogVisibility.collectAsStateWithLifecycle()

        if (addMode.value) {
            dialogComposables.AddDialogBox()
        }

        if (restoreDefaults.value) {
            dialogComposables.ConfirmRestoreDefaultsDialog()
        }

        if (optionsMode.value) {
            settings.OptionsDialogUi()
        }

        if (settingsDialogVisibility.value.speeds) {
            settings.SpeedSettingsDialog()
        }

        if (settingsDialogVisibility.value.colors) {
            settings.ColorsSettingDialog()
        }

    }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun CuisineSelectionGrid() {
        val coroutineScope = rememberCoroutineScope()
        val sectionGridState = rememberLazyGridState()

        val boardUiState = appViewModel.boardUiState.collectAsStateWithLifecycle()
        val cuisineRollFinished = appViewModel.cuisineRollFinished.collectAsStateWithLifecycle()
        val restrictionsUi = appViewModel.restrictionsList.collectAsStateWithLifecycle()
        val selectedCuisineSquare = appViewModel.selectedCuisineSquare.collectAsStateWithLifecycle()
        val rollEngaged = appViewModel.rollEngaged.collectAsStateWithLifecycle()

        val restrictionsString = foodRestrictionsString(restrictionsUi.value)
        val rolledCuisineString = selectedCuisineSquare.value.name + " Food " + restrictionsString

        if (cuisineRollFinished.value) {
            LaunchedEffect(Unit) {
                coroutineScope.launch {
                    sectionGridState.animateScrollToItem(appViewModel.rolledSquareIndex)
                    runnables.cuisineBorderStrokeToggleAnimation(2000, 200)
                    //mapInteractions.testRestaurants()
                    appViewModel.cuisineStringUri = rolledCuisineString

                    delay(2000)
                    appViewModel.updateCuisineRollFinished(false)
                }
            }
        }

        var sizeMod: Float

        LazyVerticalGrid(state = sectionGridState,
            columns = GridCells.Adaptive(minSize = 128.dp),
            contentPadding = PaddingValues(
                start = 12.dp,
                top = 16.dp,
                end = 12.dp,
                bottom = 16.dp
            ),
            content = {
                items(boardUiState.value.squareList.size) { index ->
                    if (appViewModel.rolledSquareIndex == index) sizeMod = 1.0f else sizeMod = 0.95f

                    if (rollEngaged.value) {
                        if (appViewModel.restaurantAutoScroll) {
                            coroutineScope.launch {
                                sectionGridState.animateScrollToItem(appViewModel.rolledSquareIndex)
                            }
                        }
                    }

                    Row(modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically) {
                        CuisineCard(modifier = Modifier
                            .fillMaxSize(sizeMod)
                            .animateContentSize()
                            .padding(6.dp)
                            .selectable(
                                selected = true,
                                onClick = {
                                    if (appViewModel.getEditMode) {
                                        appViewModel.toggleEditCuisineHighlightAndAddHighlightedCuisinesToEditList(
                                            index
                                        )
                                    }
                                    if (appViewModel.getCuisineSelectionMode) {
                                        appViewModel.updateSelectedCuisineSquare(appViewModel.getSquareList[index])
                                        appViewModel.updateCuisineStringUriAndHasChangedBoolean(
                                            appViewModel.selectedCuisineSquare.value.name + " Food " + foodRestrictionsString(
                                                appViewModel.getRestrictionsList
                                            )
                                        )
                                        appViewModel.updateSingleCuisineSquareColorAndBorder(
                                            index,
                                            appViewModel.getColorTheme.selectedCuisineSquare,
                                            heavyCuisineSelectionBorderStroke
                                        )
                                    }
                                }
                            ),
                            index = index)
                    }
                }
            }
        )
    }

    @Composable
    fun CuisineCard(modifier: Modifier, index: Int) {
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()
        val boardUiState = appViewModel.boardUiState.collectAsStateWithLifecycle()
        val elevation = if (index == appViewModel.rolledSquareIndex) 12.dp else 4.dp
        val squareColor = boardUiState.value.squareList[index].color

        val borderStroke = appViewModel.getSquareList[index].border

        Card(
            modifier = modifier,
            colors = CardDefaults.cardColors(
                containerColor = colorResource(id = squareColor),
            ),
            border = borderStroke,
            elevation = CardDefaults.cardElevation(
                defaultElevation = elevation
            ),
        ) {
            RegText(
                text = boardUiState.value.squareList[index].name,
                fontSize = 18,
                color = colorResource(id = colorTheme.value.cuisineSquaresText),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}