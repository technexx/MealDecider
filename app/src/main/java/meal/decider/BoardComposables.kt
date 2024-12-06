package meal.decider

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import meal.decider.Database.CuisineDatabase
import meal.decider.Database.RoomInteractions

class BoardComposables (private val appViewModel: AppViewModel, private val appDatabase: CuisineDatabase.AppDatabase, private val activity: Activity, private val roomInteractions: RoomInteractions, private val mapInteractions: MapInteractions, private val runnables: Runnables) {

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
        val buttonsEnabled = !appViewModel.getRollEngaged
        val cuisinesToEdit = appViewModel.listOfCuisineSquaresToEdit.collectAsStateWithLifecycle()

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
                        if (editMode.value && cuisinesToEdit.value.isNotEmpty()) {
                            MaterialIconButton(
                                icon = Icons.Filled.Delete,
                                description = "delete",
                                tint = colorTheme.value.cuisineIconButtons) {
                                appViewModel.deleteSelectedCuisines()
                                appViewModel.updateEditMode(false)

                                if (!appViewModel.getSquareList.isEmpty()) {
                                    appViewModel.updateAllCuisineBorders(colorTheme.value.defaultCuisineBorderStroke)
                                    appViewModel.updateSingleCuisineSquareColorAndBorder(
                                        0,
                                        appViewModel.getSquareList[appViewModel.rolledSquareIndex].color, heavyCuisineSelectionBorderStroke)
                                }

                                coroutineScope.launch {
                                    roomInteractions.deleteMultipleCuisines()
                                }
                            }
                        }
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
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()
        val restaurantVisibility = appViewModel.restaurantVisibility.collectAsStateWithLifecycle()
        val buttonsEnabled = !appViewModel.getRollEngaged
        var expanded by remember { mutableStateOf(false) }
        val editMode = appViewModel.editMode.collectAsStateWithLifecycle()

        MaterialIconButton(
            icon = Icons.Filled.Menu,
            description = "menu",
            tint = colorTheme.value.cuisineIconButtons,
            enabled = buttonsEnabled
        ) {
            if (!appViewModel.optionsMenuVisibility.value) {
                expanded = !expanded
            }
        }

        //For our drop down menus. Currently not working with them.
        AnimatedVisibility(
            enter = fadeIn(animationSpec = tween(500)) + slideInHorizontally (
                animationSpec = tween(500)
            ),
            exit = fadeOut(animationSpec = tween(500)) + slideOutHorizontally(
                animationSpec = tween(500)
            ),
            visible = expanded) {

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
                    color = colorResource(id = colorTheme.value.dialogTextColor)
                ) {
                    appViewModel.updateAddMode(true)
                    appViewModel.updateEditMode(false)
                    expanded = false
                }
                DropDownItemUi(
                    text = "Edit Cuisines",
                    fontSize = 18,
                    color = colorResource(id = colorTheme.value.dialogTextColor)
                ) {
                    //Resets list of squares to edit.
                    appViewModel.updateListOfCuisineSquaresToEdit(listOf())
                    if (!appViewModel.getEditMode) {
                        appViewModel.updateEditMode(true)
                        appViewModel.updateAllCuisineBorders(colorTheme.value.cuisineEditModeBorderStroke)
                    } else {
                        appViewModel.updateEditMode(false)
                        appViewModel.updateAllCuisineBorders(colorTheme.value.defaultCuisineBorderStroke)
                        appViewModel.updateSingleCuisineSquareColorAndBorder(
                            0,
                            appViewModel.getSquareList[appViewModel.rolledSquareIndex].color, heavyCuisineSelectionBorderStroke)
                    }
                    expanded = false
                }
                DropDownItemUi(
                    text = "Sort",
                    fontSize = 18,
                    color = colorResource(id = colorTheme.value.dialogTextColor)
                ) {
                    appViewModel.updateShowDialog(appViewModel.CUISINE_SORT)
                    appViewModel.updateEditMode(false)
                    expanded = false
                }
                DropDownItemUi(
                    text = "Restore Defaults",
                    fontSize = 18,
                    color = colorResource(id = colorTheme.value.dialogTextColor)
                ) {
                    appViewModel.updateRestoreDefaults(true)
                    expanded = false
                }
                DropDownItemUi(
                    text = "Disclaimer",
                    fontSize = 18,
                    color = colorResource(id = colorTheme.value.dialogTextColor)
                ) {
                    appViewModel.updateShowDialog(appViewModel.DISCLAIMER)
                    expanded = false
                }
            }

            if (restaurantVisibility.value == 1) {
                DropDownItemUi(
                    text = "Filters",
                    fontSize = 18,
                    color = colorResource(id = colorTheme.value.dialogTextColor)
                ) {
                    appViewModel.updateShowDialog(appViewModel.RESTAURANT_FILTERS)
                    expanded = false
                }

                DropDownItemUi(
                    text = "Sort",
                    fontSize = 18,
                    color = colorResource(id = colorTheme.value.dialogTextColor)
                ) {
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
                        Box() {
                            CuisineLayout()
                            Column(modifier = Modifier
                                .fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center) {
                                IndeterminateCircularIndicator()
                            }
                            if (restaurantVisibility.value == 1) {
                                val rollEngaged = appViewModel.getRollEngaged
                                AnimatedComposable(rollEngaged, false,
                                    modifier = Modifier.fillMaxSize(),
                                    backHandler = {
                                        appViewModel.updateRestaurantVisibility(0)
                                    }) {
                                    RestaurantListContent()
                                    appViewModel.updateMapQueryInProgress(false)
                                }
                            }
                        }
                    }
                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .background(colorResource(id = colorTheme.value.cuisineInteractionButtonsRow))
                    ) {
                        buttons.InteractionButtons(0)
                    }

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

            if (showDialog.value == appViewModel.DISCLAIMER) {
                dialogComposables.DisclaimerDialog()
            }
        }
    }

    @Composable
    fun IndeterminateCircularIndicator() {
        val mapQueryInProgress = appViewModel.mapQueryInProgress.collectAsStateWithLifecycle()

        if (mapQueryInProgress.value) {
            Row() {
                Surface(color = Color.Transparent) {
                    CircularProgressIndicator(
                        modifier = Modifier.width(64.dp),
                        color = Color.Red,
                        strokeWidth = 7.dp
                    )
                }
            }
        }
    }

    @Composable
    fun RestrictionsBarLayout() {
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()
        val selectedCuisineSquare = appViewModel.selectedCuisineSquare.collectAsStateWithLifecycle()
        val restrictionsUi = appViewModel.restrictionsList.collectAsStateWithLifecycle()

        val restrictionsString = foodRestrictionsString(restrictionsUi.value)
        appViewModel.cuisineStringUri = selectedCuisineSquare.value.name + " Food " + restrictionsString

        Column (modifier = Modifier
            .fillMaxWidth()
            .background(colorResource(id = colorTheme.value.restrictionRow))
        ) {
            LazyHorizontalGrid(rows = GridCells.Adaptive(minSize = 32.dp),
                contentPadding = PaddingValues(
                    start = 12.dp,
                    top = 16.dp,
                    end = 12.dp,
                    bottom = 20.dp,),
                content = {
                    items(restrictionsUi.value.size) { index ->
                        RestrictionsCards(index) {
                            appViewModel.toggleRestrictionListItems(index)
                        }
                    }
                }
            )
        }
    }

    @Composable
    fun RestrictionsCards(index: Int, onClick: () -> Unit) {
        val restrictionsList = appViewModel.getRestrictionsList
        val cardColor = if (appViewModel.getRestrictionsList[index].selected) colorResource(id = R.color.light_blue_100) else Color.White

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
                        onClick()
                    }
                ),
        ) {
            Column(modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center) {
                RegText(text = restrictionsList[index].name,
                    fontSize = 14,
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(6.dp))}
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

    @Composable
    fun CuisineLayout() {
        val boardUiState = appViewModel.boardUiState.collectAsStateWithLifecycle()

        if (boardUiState.value.squareList.isNotEmpty()) {
            CuisineLazyGrid()
        } else {
            EmptyCuisineMessage()
            appViewModel.updateEditMode(false)
        }
    }

    @Composable
    fun CuisineLazyGrid() {
        val coroutineScope = rememberCoroutineScope()
        val sectionGridState = rememberLazyGridState()
        val boardUiState = appViewModel.boardUiState.collectAsStateWithLifecycle()

        val rollEngaged = appViewModel.rollEngaged.collectAsStateWithLifecycle()
        val cuisineRollFinished = appViewModel.cuisineRollFinished.collectAsStateWithLifecycle()
        val restrictionsUi = appViewModel.restrictionsList.collectAsStateWithLifecycle()
        val selectedCuisineSquare = appViewModel.selectedCuisineSquare.collectAsStateWithLifecycle()
        val restrictionsString = foodRestrictionsString(restrictionsUi.value)
        val rolledCuisineString = selectedCuisineSquare.value.name + " Food " + restrictionsString

        var sizeMod = 1.0f

        if (cuisineRollFinished.value) {
            LaunchedEffect(Unit) {
                coroutineScope.launch {
                    sectionGridState.animateScrollToItem(appViewModel.rolledSquareIndex)
                    runnables.cuisineBorderStrokeToggleAnimation(2000, 200)
                    appViewModel.cuisineStringUri = rolledCuisineString

                    delay(2000)
                    appViewModel.updateCuisineRollFinished(false)
                }
            }
        }

        LazyVerticalGrid(state = sectionGridState,
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(
                start = 12.dp,
                top = 16.dp,
                end = 12.dp,
                bottom = 16.dp
            ),
            content = {
                items(boardUiState.value.squareList.size) { index ->
                    if (appViewModel.rolledSquareIndex == index) sizeMod = 1.0f else sizeMod = 0.99f

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
        val elevation = if (index == appViewModel.rolledSquareIndex) 12.dp else 4.dp
        val squareColor = appViewModel.getSquareList[index].color
        val borderStroke = appViewModel.getSquareList[index].border
        val squareText = appViewModel.getSquareList[index].name

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
                text = squareText,
                fontSize = 18,
                color = colorResource(id = colorTheme.value.cuisineSquaresText),
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(16.dp)
            )
        }
    }

    @Composable
    fun EmptyCuisineMessage() {
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()

        Row(modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            RegText(text = "Where did all the cuisines go?", fontSize = 24, color = colorResource(id = colorTheme.value.dialogTextColor))
        }
    }

    @Composable
    fun RestaurantListContent() {
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()
        val rollEngaged = appViewModel.rollEngaged.collectAsStateWithLifecycle()
        val restaurantList = appViewModel.restaurantList.collectAsStateWithLifecycle()
        var expanded by remember { mutableStateOf(false) }
        if (rollEngaged.value) expanded = false

        Surface() {
            Column(modifier = Modifier
                .fillMaxSize()
            ) {
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .background(colorResource(id = colorTheme.value.restaurantBoard))
                ) {
                    RestaurantFilterBar()
                    if (!restaurantList.value.isEmpty()) {
                        RestaurantLazyGrid()
                    } else {
                        Row(modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            RegText(text = "No results! Try expanding filters or removing restrictions.", fontSize = 24, color = colorResource(id = colorTheme.value.dialogTextColor))
                        }
                    }
                }
                Column(modifier = Modifier
                    .wrapContentSize()
                    .background(colorResource(id = colorTheme.value.restaurantInteractionButtonsRow))
                ) {
                    buttons.InteractionButtons(1)
                }
            }
        }
    }

    @Composable
    fun RestaurantFilterBar() {
        val bullet: @Composable () -> Unit = { RegText(text = "•", modifier = Modifier.padding(top = 2.dp)) }
        Row(modifier = Modifier
            .wrapContentSize()
            .fillMaxWidth()
            .background(colorResource(id = R.color.grey_300)),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center) {
            val fontSize = 16
            val miles = doubleMetersToMilesAsInt(appViewModel.maxRestaurantDistance)
            RegText(text = "<= $miles mi", fontSize = fontSize)
            Spacer(modifier = Modifier.width(6.dp))
            bullet()
            Spacer(modifier = Modifier.width(6.dp))
            var priceString = ""
            for (i in 1..appViewModel.maxRestaurantPrice) priceString += "$"
            RegText(text = "<= $priceString", fontSize = fontSize)
            Spacer(modifier = Modifier.width(6.dp))
            bullet()
            Spacer(modifier = Modifier.width(6.dp))
            RegText(text = ">= ", fontSize = fontSize)
            dialogComposables.FilterStars(ratingSliderPosition = appViewModel.minRestaurantRating.toFloat())
            if (appViewModel.isOpen) {
                Spacer(modifier = Modifier.width(6.dp))
                bullet()
                Spacer(modifier = Modifier.width(6.dp))
                RegText(text = "Open Now", fontSize = fontSize)
            }
        }
    }

    @Composable
    fun RestaurantLazyGrid() {
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()
        val coroutineScope = rememberCoroutineScope()
        val sectionGridState = rememberLazyStaggeredGridState()
        val restaurantList = appViewModel.restaurantList.collectAsStateWithLifecycle()
        val selectedRestaurantSquare = appViewModel.selectedRestaurantSquare.collectAsStateWithLifecycle()
        val rollEngaged = appViewModel.rollEngaged.collectAsStateWithLifecycle()
        val restaurantRollFinished = appViewModel.restaurantRollFinished.collectAsStateWithLifecycle()

        val rolledRestaurantString = selectedRestaurantSquare.value.name.toString() + " " + selectedRestaurantSquare.value.address
        var borderStroke: BorderStroke

        if (restaurantRollFinished.value) {
            LaunchedEffect(Unit) {
                coroutineScope.launch {
                    sectionGridState.animateScrollToItem(appViewModel.rolledRestaurantIndex)
                    appViewModel.restaurantStringUri = rolledRestaurantString
                    runnables.restaurantBorderStrokeToggleAnimation(2000, 200)

                    delay(2000)

                    appViewModel.updateRestaurantRollFinished(false)
                }
            }
        }

        LazyVerticalStaggeredGrid(state = sectionGridState,
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            columns = StaggeredGridCells.Adaptive(160.dp),
        ) {
            items(restaurantList.value.size) { index ->
                if (rollEngaged.value) {
                    if (appViewModel.restaurantAutoScroll) {
                        coroutineScope.launch {
                            sectionGridState.animateScrollToItem(appViewModel.rolledRestaurantIndex)
                        }
                    }
                }

                borderStroke = appViewModel.getRestaurantList[index].border
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = colorResource(restaurantList.value[index].color!!),
                    ),
                    border = borderStroke,
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    modifier = Modifier
                        .padding(8.dp)
                        .selectable(
                            selected = true,
                            onClick = {
                                if (appViewModel.getRestaurantSelectionMode) {
                                    appViewModel.restaurantStringUri =
                                        appViewModel.getRestaurantList[index].name.toString()
                                    appViewModel.updateSelectedRestaurantSquare(appViewModel.getRestaurantList[index])
                                    appViewModel.updateSingleRestaurantColorAndBorder(
                                        appViewModel.getRestaurantList,
                                        index,
                                        appViewModel.getColorTheme.selectedRestaurantSquare,
                                        heavyRestaurantSelectionBorderStroke
                                    )
                                }
                            }
                        ),
                ) {
                    Column (modifier = Modifier.padding(12.dp)){
                        RegText(restaurantList.value[index].name.toString(), fontSize = 16, color = colorResource( id = colorTheme.value.restaurantSquaresText))
                        val distanceInMeters = (restaurantList.value[index].distance)
                        RegText(doubleMetersToMiles(distanceInMeters!!).toString() + " miles", fontSize = 16, color = colorResource( id = colorTheme.value.restaurantSquaresText))
                        Row {
                            RegText(text = restaurantList.value[index].rating.toString(), fontSize = 16, color = colorResource( id = colorTheme.value.restaurantSquaresText))
                            Spacer(modifier = Modifier.width(2.dp))
                            Column(modifier = Modifier
                                .padding(top = 3.dp)) {
                                RatingStars(restaurantList.value[index].rating)
                            }
                        }
                        RegText(priceToDollarSigns(restaurantList.value[index].priceLevel), fontSize = 16, color = colorResource( id = colorTheme.value.restaurantSquaresText))
                    }
                }
            }
        }
    }

    @Composable
    fun RatingStars(rating: Double?) {
        if (rating != null) {
            val roundedDown = rating.toInt()
            val remainder = rating - roundedDown
            Row (modifier = Modifier
                .padding(0.dp, 0.dp)) {
                for (i in 1..5) {
                    if (roundedDown >= i) {
                        Image(painterResource(R.drawable.full_star_black,), "full star")
                    } else {
                        if (i < 5) {
                            Image(painterResource(R.drawable.empty_star,), "empty star")
                        } else {
                            if (remainder >=.5 && remainder <.9) {
                                Image(painterResource(R.drawable.half_empty_star_black), "half star")
                            } else {
                                Image(painterResource(R.drawable.empty_star,), "empty star")
                            }
                        }
                    }
                }
            }
        }
    }
}