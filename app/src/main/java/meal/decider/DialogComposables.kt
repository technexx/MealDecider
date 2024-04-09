package meal.decider

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import meal.decider.Database.CuisineDatabase
import meal.decider.Database.RoomInteractions

class DialogComposables(private val appViewModel: AppViewModel, appDatabase: CuisineDatabase.AppDatabase, private val activity: Activity, private val mapInteractions: MapInteractions, private val runnables: Runnables){
    private val roomInteractions = RoomInteractions(appDatabase, appViewModel, activity)
    private val buttons = Buttons(appViewModel, mapInteractions, runnables)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AddDialogBox() {
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()
        val coroutineScope = rememberCoroutineScope()
        var txtField by remember { mutableStateOf("") }
        val displayedList = appViewModel.displayedCuisineList.collectAsStateWithLifecycle()
        var searchTerms : List<String>

        AnimatedTransitionDialog(
            modifier = Modifier
                .background(colorResource(id = colorTheme.value.dialogBackground))
                .height(400.dp)
                .width(500.dp),
            onDismissRequest = {
                appViewModel.updateAddMode(false)
                appViewModel.updateListOfCuisinesToAdd(emptyList())
            },
            content = {
                Column(modifier = Modifier
                    .background(colorResource(id = colorTheme.value.dialogBackground))
                    .fillMaxSize()
                    .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly)
                {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(modifier = Modifier,
//                                .fillMaxWidth(0.8f),
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                            value = txtField,
                            placeholder = {Text( "e.g. Filipino") },
                            onValueChange = {
                                txtField = it
                                searchTerms = filterList(fullCuisineList, txtField)
                                appViewModel.updateDisplayedCuisineList(searchTerms)},
                            singleLine = true,
                            textStyle = TextStyle(color = Color.Black, fontSize = 22.sp, fontWeight = FontWeight.Bold),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = colorResource(id = R.color.grey_50),
                            ),
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    appViewModel.adjustDisplayedCuisineListFromDisplayedSquares()
                    DisplayedCuisineList(displayedList)

                    Row (modifier = Modifier
                        .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        MaterialIconButton(icon = Icons.Filled.Close, description = "close", colorTheme.value.cancelDialogButton) {
                            appViewModel.updateAddMode(false)
                            appViewModel.updateListOfCuisinesToAdd(emptyList())
                        }
                        MaterialIconButton(icon = Icons.Filled.Check, description = "confirm", colorTheme.value.confirmDialogButton) {
                            appViewModel.addMultipleSquaresToList(appViewModel.getListOfCuisinesToAdd)
                            coroutineScope.launch {
                                roomInteractions.insertMultipleCuisines(appViewModel.getListOfCuisinesToAdd)
                            }
                            appViewModel.updateAddMode(false)
                        }
                    }
                }
            }
        )
    }

    @Composable
    fun DisplayedCuisineList(list: State<List<String>>) {
        val listOfCuisinesToAdd = appViewModel.listOfCuisinesToAdd.collectAsStateWithLifecycle()
        var backgroundColor: Int

        LazyColumn (
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            items (list.value.size) { index ->
                backgroundColor = if (!listOfCuisinesToAdd.value.contains(list.value[index])) {
                    R.color.grey_300
                } else {
                    R.color.grey_500
                }
                Column (modifier = Modifier
                    .padding(4.dp)
                    .selectable(
                        selected = true,
                        onClick = {
                            appViewModel.toggleAddCuisineSelections(list.value[index])
                        }
                    )) {
                    Text(modifier = Modifier
                        .background(
                            colorResource(backgroundColor),
                            shape = RoundedCornerShape(5.dp)
                        )
                        .padding(8.dp),
                        fontSize = 20.sp,
                        color = Color.Black,
                        text = list.value[index])
                }
            }
        }
    }

    @Composable
    fun ConfirmRestoreDefaultsDialog() {
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()

        AnimatedTransitionDialog(
            modifier = Modifier
                .background(colorResource(id = colorTheme.value.dialogBackground))
                .height(200.dp)
                .width(300.dp),
            onDismissRequest = {
                appViewModel.updateRestoreDefaults(false)
            },
            content = {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = colorResource(colorTheme.value.dialogBackground)
                ) {
                    Box(modifier = Modifier
                    ) {
                        Column(modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceEvenly)
                        {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RegText(
                                    text = "This will restore cuisine list to default!",
                                    fontSize = 18,
                                    color = Color.Black,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row (modifier = Modifier
                                .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                MaterialIconButton(icon = Icons.Default.Close, description = "close", colorTheme.value.cancelDialogButton) {
                                    appViewModel.updateRestoreDefaults(false)
                                }
                                MaterialIconButton(icon = Icons.Filled.Check, description = "confirm", colorTheme.value.confirmDialogButton) {
                                    roomInteractions.setSquareDatabaseToDefaultStartingValues()
                                    appViewModel.updateSquareList(appViewModel.starterSquareList())
                                    appViewModel.updateSelectedCuisineSquare(appViewModel.getSquareList[0])
                                    appViewModel.updateEditMode(false)
                                    appViewModel.updateRestoreDefaults(false)
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    @Composable
    fun RestaurantDialog() {
        val restaurantDialogVisibility = appViewModel.restaurantDialogVisibility.collectAsStateWithLifecycle()

        //TODO: Works like this, without animation function.
        Dialog(onDismissRequest = {
            if (appViewModel.getRestaurantDialogVisibility == 1) {
                appViewModel.updateRestaurantDialogVisibility(0)
                appViewModel.updateShowRestaurants(false)
            }
            if (appViewModel.getRestaurantDialogVisibility == 2) {
                appViewModel.updateRestaurantDialogVisibility(1)
            }
        }) {
            if (restaurantDialogVisibility.value == 1) {
                RestaurantListContent()
            }
            if (restaurantDialogVisibility.value == 2) {
                RestaurantFilters()
            }
        }

        AnimatedTransitionDialog(
            modifier = Modifier.fillMaxSize(),
            onDismissRequest = {
                if (appViewModel.getRestaurantDialogVisibility == 1) {
                    appViewModel.updateRestaurantDialogVisibility(0)
                    appViewModel.updateShowRestaurants(false)
                }
                if (appViewModel.getRestaurantDialogVisibility == 2) {
                    appViewModel.updateRestaurantDialogVisibility(1)
                }
        }){
            if (restaurantDialogVisibility.value == 1) {
                RestaurantListContent()
            }
            if (restaurantDialogVisibility.value == 2) {
                RestaurantFilters()
            }
        }
    }

    @Composable
    fun RestaurantListContent() {
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()
        val selectMode = appViewModel.restaurantSelectionMode.collectAsStateWithLifecycle()
        val rollEngaged = appViewModel.rollEngaged.collectAsStateWithLifecycle()

        var selectIconColor: Int

        val buttonsEnabled = !rollEngaged.value
        var expanded by remember { mutableStateOf(false) }
        if (rollEngaged.value) expanded = false

        Surface(
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(modifier = Modifier
                .fillMaxSize()
            ) {
                Column(modifier = Modifier
                    .wrapContentSize()
                ) {
                    Row (modifier = Modifier
                        .fillMaxWidth()
                        .background(colorResource(id = colorTheme.value.restaurantTopRow)),
                        horizontalArrangement = Arrangement.End) {
                        if (selectMode.value) {
                            selectIconColor = appViewModel.getColorTheme.selectedRestaurantIcon
                        } else {
                            selectIconColor = appViewModel.getColorTheme.restaurantsIconButtons
                        }
                        MaterialIconButton(
                            icon = Icons.Filled.Create,
                            description = "select",
                            tint = selectIconColor,
                            enabled = buttonsEnabled) {
                            appViewModel.updateRestaurantSelectionMode(!appViewModel.getRestaurantSelectionMode)
                        }
                        MaterialIconButton(
                            icon = Icons.Filled.Settings,
                            description = "settings",
                            tint = colorTheme.value.restaurantsIconButtons,
                            enabled = buttonsEnabled) {
                            appViewModel.updateRestaurantDialogVisibility(2)
//                            appViewModel.updateShowRestaurantSettings(true)
                        }
                        RestaurantSortDropdownMenu()
                    }
                }
                Column(modifier = Modifier
                    .height(screenHeightPct(0.8).dp)
                    .background(colorResource(id = colorTheme.value.restaurantBoard))
                ) {
                    RestaurantLazyGrid()
                }
                Column(modifier = Modifier
                    .wrapContentSize()
                    .background(colorResource(id = colorTheme.value.restaurantInteractionButtonsRow))
                ) {
                    buttons.InteractionButtons()
                }
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
        val restaurantRollFinished = appViewModel.restaurantRollFinished.collectAsStateWithLifecycle()


        val rolledRestaurantString = selectedRestaurantSquare.value.name.toString()
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
            columns = StaggeredGridCells.Adaptive(128.dp),
        ) {
            items(restaurantList.value.size) { index ->
//            items(dummyList.size) { index ->
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
                                    appViewModel.toggleSelectionOfSingleRestaurantSquareColorAndBorder(
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
                        RatingStars(restaurantList.value[index].rating)
                        RegText(priceToDollarSigns(restaurantList.value[index].priceLevel), fontSize = 16, color = colorResource( id = colorTheme.value.restaurantSquaresText))
                    }
                }
            }
        }
    }

    @Composable
    fun RestaurantSortDropdownMenu() {
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()
        val rollEngaged = appViewModel.rollEngaged.collectAsStateWithLifecycle()
        var expanded by remember { mutableStateOf(false) }
        if (rollEngaged.value) expanded = false

        Box(
            modifier = Modifier
                .wrapContentSize(Alignment.TopEnd)
        ) {
            MaterialIconButton(icon = Icons.Filled.Menu, description = "menu", colorTheme.value.restaurantsIconButtons) {
                expanded = !expanded
            }

            //Order of modifiers matters. Background needs to be set BEFORE padding, otherwise background outside of padding will not be changed.
            DropdownMenu(modifier = Modifier
                .background(colorResource(id = colorTheme.value.dropDownMenuBackground))
                .padding(0.dp)
                .wrapContentSize(),
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                DropDownItemUi(
                    text = "Sort A-Z",
                    fontSize = 18,
                    color = colorResource(id = colorTheme.value.dialogTextColor)) {
                    appViewModel.sortAndUpdateRestaurantList("name")
                    expanded = false
                }
                DropDownItemUi(
                    text = "Sort by Distance",
                    fontSize = 18,
                    color = colorResource(id = colorTheme.value.dialogTextColor)) {
                    appViewModel.sortAndUpdateRestaurantList("distance")
                    expanded = false
                }
                DropDownItemUi(
                    text = "Sort by Rating",
                    fontSize = 18,
                    color = colorResource(id = colorTheme.value.dialogTextColor)) {
                    appViewModel.sortAndUpdateRestaurantList("rating")
                    expanded = false
                }
                DropDownItemUi(
                    text = "Sort by Price",
                    fontSize = 18,
                    color = colorResource(id = colorTheme.value.dialogTextColor)) {
                    appViewModel.sortAndUpdateRestaurantList("price")
                    expanded = false
                }
                DropDownItemUi(
                    text = "Sort Randomly",
                    fontSize = 18,
                    color = colorResource(id = colorTheme.value.dialogTextColor)) {
                    appViewModel.sortAndUpdateRestaurantList("random")
                    expanded = false
                }
            }
        }
    }

    @Composable
    fun RestaurantFilters() {
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()
        val coroutineScope: CoroutineScope = rememberCoroutineScope()
        var distanceSliderPosition by remember { mutableFloatStateOf(3f) }
        var ratingSliderPosition by remember { mutableFloatStateOf(3f) }
        var priceSliderPosition by remember { mutableFloatStateOf(1f) }

        LaunchedEffect(Unit) {
            coroutineScope.launch {
                val restaurantFilters = roomInteractions.getRestaurantFilters()
                distanceSliderPosition = restaurantFilters[0].distance.toFloat()
                ratingSliderPosition = restaurantFilters[0].rating.toFloat()
                priceSliderPosition = restaurantFilters[0].price.toFloat()
            }
        }

        val textColor = colorResource(id = colorTheme.value.dialogTextColor)
        var priceString: String

        AnimatedTransitionVoid {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = colorResource(id = colorTheme.value.dialogBackground),
            ) {
                Box(modifier = Modifier
                    .fillMaxSize(),
                ) {
                    Column (modifier = Modifier
                        .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally)
                    {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            RegText(text = "Filters", fontSize = 22, color = textColor, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Column (){
                            RegText(text = "Distance", fontSize = 20, color = textColor)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row (modifier = Modifier
                                .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween) {
                                Slider(modifier = Modifier
                                    .fillMaxWidth(0.75f)
                                    .padding(start = 4.dp),
                                    value = distanceSliderPosition,
                                    onValueChange = { distanceSliderPosition = it
                                    },
                                    valueRange = 1f..10f
                                )
                                RegText(text = distanceSliderPosition.toInt().toString() + " mi", fontSize = 18, color = textColor)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            RegText(text = "Rating", fontSize = 20 , color = textColor)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row (modifier = Modifier
                                .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween) {
                                Slider(modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .padding(start = 4.dp),
                                    value = ratingSliderPosition,
                                    onValueChange = { ratingSliderPosition = it },
                                    valueRange = 3f..4.5f,
                                    steps = 2
                                )
                                RegText(text = "$ratingSliderPosition stars", fontSize = 18, color = textColor)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            RegText(text = "Max Price", fontSize = 18, color = textColor)
                            Spacer(modifier = Modifier.height(8.dp))
                            Row (modifier = Modifier
                                .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween) {
                                Slider(modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .padding(start = 4.dp),
                                    value = priceSliderPosition,
                                    onValueChange = { priceSliderPosition = it },
                                    valueRange = 1f..4f,
                                    steps = 2
                                )
                                Column (modifier = Modifier
                                    .fillMaxWidth(),
                                    verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally){
                                    priceString = ""
                                    for (i in 1..priceSliderPosition.toInt()) {
                                        priceString += "$"
                                    }
                                    RegText(text = priceString, fontSize = 18, color = textColor)
                                }

                            }
                        }
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
                for (i in 1..roundedDown) {
                    Image(painterResource(R.drawable.full_star_black,), "full star")
                }
                if (remainder >.2 && remainder <.8) {
                    Image(painterResource(R.drawable.half_empty_star_black,), "half star")
                }
                if (remainder >= .8) {
                    Image(painterResource(R.drawable.full_star_black,), "full star")
                }
            }
        }
    }

    @Composable
    fun OptionsDialog() {
//         val windowProvider = LocalView.current.parent as DialogWindowProvider
//            windowProvider.window.setGravity(Gravity.END)
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()

        AnimatedTransitionDialog(
            modifier = Modifier
                .background(colorResource(id = colorTheme.value.dialogBackground))
                .fillMaxSize(),
            onDismissRequest = {
                appViewModel.updateOptionsMode(false) },
            content = {
                Column(modifier = Modifier
                    .background(colorResource(id = colorTheme.value.dialogBackground))
                    .fillMaxSize()
                    .padding(20.dp)
                )
                {
                    OptionsDialogUi()
                }
            }
        )
    }

    @Composable
    fun OptionsDialogUi() {
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()
        val textColor = colorResource(id = colorTheme.value.dialogTextColor)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = colorTheme.value.dialogBackground)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Column (horizontalAlignment = Alignment.CenterHorizontally){
                RegText("Settings", fontSize = 28, color = textColor)
                Spacer(modifier = Modifier.height(20.dp))
                RegTextButton(text = "Speeds", fontSize = 26, color = textColor,
                    onClick = {
                        appViewModel.updateOptionsMode(false)
                        appViewModel.updateSettingsDialogVisibility(speeds = true, sounds = false, colors = false)
                    })
                Spacer(modifier = Modifier.height(10.dp))
                RegTextButton(text = "Sounds", fontSize = 26, color = textColor,
                    onClick = {
                        appViewModel.updateOptionsMode(false)
                        appViewModel.updateSettingsDialogVisibility(speeds = false, sounds = true, colors = false)
                    })
                Spacer(modifier = Modifier.height(10.dp))
                RegTextButton(text = "Colors",  fontSize = 26, color = textColor,
                    onClick = {
                        appViewModel.updateOptionsMode(false)
                        appViewModel.updateSettingsDialogVisibility(speeds = false, sounds = false, colors = true)
                    })
            }
        }
    }

    @Composable
    fun ColorsSettingDialog() {
        val colorSettingsToggle = appViewModel.colorSettingsSelectionList.collectAsStateWithLifecycle()
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()

        AnimatedTransitionDialog(
            modifier = Modifier
                .background(colorResource(id = colorTheme.value.dialogBackground))
                .fillMaxSize(),
            onDismissRequest = {
                appViewModel.updateSettingsDialogVisibility(speeds = false, colors = false, sounds = false)
                appViewModel.updateOptionsMode(true)
            },
            content = {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .background(colorResource(id = colorTheme.value.dialogBackground)), horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row {
                        RegText(text = "Theme", fontSize = 28, color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 128.dp),
                        contentPadding = PaddingValues(
                            start = 24.dp,
                            top = 16.dp,
                            end = 24.dp,
                            bottom = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        content = {
                            items(colorSettingsToggle.value.size) { index ->
                                val cardColor = if (appViewModel.getColorSettingsSelectionList[index].selected) colorResource(id = R.color.blue_grey_100) else Color.White

                                Box(contentAlignment = Alignment.Center) {
                                    CardUi(
                                        color = cardColor,
                                        onClick = {
                                            appViewModel.switchColorSettingsUi(index)
                                            appViewModel.updateColorTheme(Theme.themeColorsList[index])
                                            roomInteractions.saveColorThemeToSharedPref(Theme.themeColorsList[index])
                                        },
                                        content = {
                                            RegText(
                                                text = colorSettingsToggle.value[index].name,
                                                fontSize = 26,
                                                color = Color.Black,
                                                fontWeight = FontWeight.Bold,
                                                modifier = Modifier.padding(12.dp))
                                        })
                                }
                            }
                        }
                    )
                }
            })
    }

    @Composable
    fun SpeedSettingsDialog() {
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()
        val coroutineScope: CoroutineScope = rememberCoroutineScope()
        var cuisineRollDurationSliderPosition by remember { mutableFloatStateOf(3f) }
        var cuisineRollDelaySliderPosition by remember { mutableFloatStateOf(3f) }
        var restaurantRollDurationSliderPosition by remember { mutableFloatStateOf(3f) }
        var restaurantRollDelaySliderPosition by remember { mutableFloatStateOf(3f) }

        LaunchedEffect(Unit) {
            cuisineRollDurationSliderPosition = appViewModel.cuisineRollDurationSetting.toFloat()
            cuisineRollDelaySliderPosition = appViewModel.cuisineRollSpeedSetting.toFloat()
            restaurantRollDurationSliderPosition = appViewModel.restaurantRollDurationSetting.toFloat()
            restaurantRollDelaySliderPosition = appViewModel.restaurantRollDelaySetting.toFloat()
        }

        AnimatedTransitionDialog(
            modifier = Modifier
                .background(colorResource(id = colorTheme.value.dialogBackground))
                .fillMaxSize(),
            onDismissRequest = {
                appViewModel.updateSettingsDialogVisibility(speeds = false, colors = false, sounds = false)
                appViewModel.updateOptionsMode(true)
                coroutineScope.launch {
                    roomInteractions.updateRollOptions(cuisineRollDurationSliderPosition.toLong(), cuisineRollDelaySliderPosition.toLong(), restaurantRollDurationSliderPosition.toLong(), restaurantRollDelaySliderPosition.toLong())
                }
                appViewModel.updateRollOptions(cuisineRollDurationSliderPosition.toLong(), cuisineRollDelaySliderPosition.toLong(), restaurantRollDurationSliderPosition.toLong(), restaurantRollDelaySliderPosition.toLong())
            },
            content = {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                ) {
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .background(colorResource(id = colorTheme.value.dialogBackground)),
                        horizontalAlignment = Alignment.CenterHorizontally) {
                        Column {
                            RegText("Cuisine Selection Duration", fontSize = 18, color = Color.Black)
                            Row () {
                                Slider(modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .padding(start = 4.dp),
                                    value = cuisineRollDurationSliderPosition,
                                    onValueChange = { cuisineRollDurationSliderPosition = it },
                                    valueRange = 1f..10f,
                                    steps = 9
                                )
                                RegText(text = "${cuisineRollDurationSliderPosition.toInt()}", fontSize = 18, color = Color.Black)
                            }
                            RegText("Cuisine Selection Speed", fontSize = 18, color = Color.Black)
                            Row () {
                                Slider(modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .padding(start = 4.dp),
                                    value = cuisineRollDelaySliderPosition,
                                    onValueChange = { cuisineRollDelaySliderPosition = it },
                                    valueRange = 1f..10f,
                                    steps = 9
                                )
                                RegText(text = "${cuisineRollDelaySliderPosition.toInt()}", fontSize = 18, color = Color.Black)
                            }
                            RegText("Restaurant Selection Duration", fontSize = 18, color = Color.Black)
                            Row () {
                                Slider(modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .padding(start = 4.dp),
                                    value = restaurantRollDurationSliderPosition,
                                    onValueChange = { restaurantRollDurationSliderPosition = it },
                                    valueRange = 1f..10f,
                                    steps = 9
                                )
                                RegText(text = "${restaurantRollDurationSliderPosition.toInt()}", fontSize = 18, color = Color.Black)
                            }
                            RegText("Restaurant Selection Speed", fontSize = 18, color = Color.Black)
                            Row () {
                                Slider(modifier = Modifier
                                    .fillMaxWidth(0.7f)
                                    .padding(start = 4.dp),
                                    value = restaurantRollDelaySliderPosition,
                                    onValueChange = { restaurantRollDelaySliderPosition = it },
                                    valueRange = 1f..10f,
                                    steps = 9
                                )
                                RegText(text = "${restaurantRollDelaySliderPosition.toInt()}", fontSize = 18, color = Color.Black)
                            }
                        }
                    }
                }
            })
    }
}