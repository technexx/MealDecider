package meal.decider

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import meal.decider.Database.CuisineDatabase
import meal.decider.Database.RoomInteractions

class DialogComposables(private val appViewModel: AppViewModel, appDatabase: CuisineDatabase.AppDatabase, private val activity: Activity, private val mapInteractions: MapInteractions, private val runnables: Runnables){
    private val roomInteractions = RoomInteractions(appDatabase, appViewModel, activity)
    private val buttons = Buttons(appViewModel, mapInteractions, runnables)
    private val settings = Settings(appViewModel, roomInteractions)

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
                .background(Color.Transparent)
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
                                searchTerms = filterSearchString(fullCuisineList, txtField)
                                appViewModel.updateDisplayedCuisineList(searchTerms)},
                            singleLine = true,
                            textStyle = TextStyle(color = Color.Black, fontSize = 22.sp, fontWeight = FontWeight.Bold),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = colorResource(colorTheme.value.textBoxBackground),
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
                        MaterialIconButton(icon = Icons.Filled.Close, description = "close", tint = colorTheme.value.cancelDialogButton, modifier = Modifier.size(64.dp)) {
                            appViewModel.updateAddMode(false)
                            appViewModel.updateListOfCuisinesToAdd(emptyList())
                        }
                        MaterialIconButton(icon = Icons.Filled.Check, description = "confirm", tint = colorTheme.value.confirmDialogButton, modifier = Modifier.size(64.dp)) {
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
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()
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
                    colorTheme.value.dialogBackground
                } else {
                    colorTheme.value.dialogTextHighlight
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
                        color = colorResource(id = colorTheme.value.dialogTextColor),
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
                .background(Color.Transparent)
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
                            .fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.SpaceBetween)
                        {
//                            Spacer(modifier = Modifier.height(8.dp))
                            Row() {
                                RegText(
                                    text = "Are you sure? This will restore the original cuisine list.",
                                    fontSize = 20,
                                    color = colorResource(id = colorTheme.value.dialogTextColor),
                                    modifier = Modifier.padding(top = 16.dp, start = 24.dp, end = 24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row (modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                MaterialIconButton(icon = Icons.Default.Close, description = "close", tint = colorTheme.value.cancelDialogButton, modifier = Modifier.size(64.dp)) {
                                    appViewModel.updateRestoreDefaults(false)
                                }
                                MaterialIconButton(icon = Icons.Filled.Check, description = "confirm", tint = colorTheme.value.confirmDialogButton, modifier = Modifier.size(64.dp)) {
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
                    .height(screenHeightPct(0.8).dp)
                    .background(colorResource(id = colorTheme.value.restaurantBoard))
                ) {
                    Row(modifier = Modifier
                        .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center) {
                        //TODO: RestrictionsBarLayout
                    }
                    if (!restaurantList.value.isEmpty()) {
                        RestaurantLazyGrid()
                    } else {
                        Row(modifier = Modifier.fillMaxSize()
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
            RegText(text = restrictionsList[index].name,
                fontSize = 14,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(6.dp))
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
            columns = StaggeredGridCells.Adaptive(128.dp),
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

    @Composable
    fun SortDialog() {
        val coroutineScope = rememberCoroutineScope()
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()
        val showDialog = appViewModel.showDialog.collectAsStateWithLifecycle()
        val selectedCircleIndex = remember { mutableStateOf(0) }
        var circles = listOf<String>()
        var sortText = "A-Z"
        var textSize = 16

        if (showDialog.value == appViewModel.CUISINE_SORT) {
            circles = listOf("A-Z", "Random")
            textSize = 22
        }

        if (showDialog.value == appViewModel.RESTAURANT_SORT) {
            circles = listOf("A-Z", "Distance", "Rating", "Price", "Random")
            textSize = 16
        }

        AnimatedTransitionDialog(
            modifier = Modifier
                .background(Color.Transparent)
                .height(300.dp)
                .width(300.dp),
            onDismissRequest = {
                appViewModel.updateShowDialog(appViewModel.NO_DIALOG)
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
                        )
                        {
                            circles.forEachIndexed { index, circleText ->
                                Row(modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically,
                                ) {
                                    SelectableCircle(
                                        selected = index == selectedCircleIndex.value,
                                        text = circleText,
                                        textSize = textSize,
                                        onClick = {
                                            selectedCircleIndex.value = index
                                            sortText = circleText
                                        }
                                    )
                                }
                                Spacer(modifier = Modifier.height(10.dp))
                            }

                            Row(modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.SpaceBetween) {

                                MaterialIconButton(icon = Icons.Filled.Close, description = "close", tint = colorTheme.value.cancelDialogButton, modifier = Modifier.size(64.dp)) {
                                    appViewModel.updateShowDialog(appViewModel.NO_DIALOG)
                                }

                                MaterialIconButton(icon = Icons.Filled.Check, description = "confirm", tint = colorTheme.value.confirmDialogButton, modifier = Modifier.size(64.dp)) {
                                    if (appViewModel.getShowDialog == appViewModel.CUISINE_SORT) {
                                    appViewModel.sortAndUpdateCuisineList(sortText)
                                        coroutineScope.launch {
                                            roomInteractions.updateCuisines(appViewModel.getSquareList)
                                            roomInteractions.deleteAllCuisines()
                                            roomInteractions.insertMultipleCuisines(appViewModel.getListOfSquareNames())
                                        }
                                    }
                                    if (appViewModel.getShowDialog == appViewModel.RESTAURANT_SORT) {
                                        appViewModel.sortAndUpdateRestaurantList(sortText)
                                    }
                                    appViewModel.updateShowDialog(appViewModel.NO_DIALOG)
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    @Composable
    fun SelectableCircle(
        text: String,
        textSize: Int,
        selected: Boolean,
        onClick: () -> Unit
    ) {
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()

        RegText(text = text, fontSize = 18, color = colorResource(id = colorTheme.value.dialogTextColor))
        Card(
            modifier = Modifier
                .size(textSize.dp)
                .clickable {
                    onClick()
                },
            shape = CircleShape,
            border = BorderStroke(2.dp, colorResource(id = colorTheme.value.circleSelectionColor),
            ),
            colors = if(!selected) CardDefaults.cardColors(
                containerColor = Color.Transparent) else CardDefaults.cardColors(
                containerColor = colorResource(id = colorTheme.value.dialogTextColor))
        ) {
            Box(contentAlignment = Alignment.Center) {
                // Add any content you want inside the circle here
            }
        }
    }

    @Composable
    fun RestaurantFiltersDialog() {
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()
        val coroutineScope: CoroutineScope = rememberCoroutineScope()
        var distanceSliderPosition by remember { mutableFloatStateOf(3f) }
        var ratingSliderPosition by remember { mutableFloatStateOf(3f) }
        var priceSliderPosition by remember { mutableFloatStateOf(1f) }

        LaunchedEffect(Unit) {
            coroutineScope.launch {
                val restaurantFilters = roomInteractions.getRestaurantFilters()
                distanceSliderPosition = metersToMiles(restaurantFilters[0].distance).toFloat()
                ratingSliderPosition = restaurantFilters[0].rating.toFloat()
                priceSliderPosition = restaurantFilters[0].price.toFloat()
            }
        }

        val textColor = colorResource(id = colorTheme.value.dialogTextColor)
        var priceString: String

        AnimatedTransitionDialog(
            modifier = Modifier
                .background(Color.Transparent)
                .height(400.dp)
                .width(500.dp),
            onDismissRequest = {
                appViewModel.updateShowDialog(appViewModel.NO_DIALOG)
            },
            content = {
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
                                RegText(text = "Maximum Distance", fontSize = 20, color = textColor)
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
                                    Column (modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp, end = 6.dp),
                                        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.End){
                                        RegText(text = distanceSliderPosition.toInt().toString() + " mi", fontSize = 18, color = textColor)
                                    }

                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                RegText(text = "Maximum Price", fontSize = 18, color = textColor)
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
                                        .fillMaxWidth()
                                        .padding(top = 12.dp, end = 6.dp),
                                        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.End){
                                        priceString = ""
                                        for (i in 1..priceSliderPosition.toInt()) {
                                            priceString += "$"
                                        }
                                        RegText(text = priceString, fontSize = 18, color = textColor)
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                RegText(text = "Minimum Rating", fontSize = 20 , color = textColor)
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
                                    Column (modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(end = 6.dp),
                                        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.End){
                                        Row(modifier = Modifier
                                            .padding(top = 14.dp)) {
                                            FilterStars(ratingSliderPosition)
                                        }
//                                        RegText(text = "$ratingSliderPosition stars", fontSize = 18, color = textColor)
                                    }
                                }
                            }

                            Row(modifier = Modifier.fillMaxSize(),
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.SpaceBetween) {

                                MaterialIconButton(icon = Icons.Filled.Close, description = "close", tint = colorTheme.value.cancelDialogButton, modifier = Modifier.size(64.dp)) {
                                    appViewModel.updateShowDialog(appViewModel.NO_DIALOG)
                                }

                                MaterialIconButton(icon = Icons.Filled.Check, description = "confirm", tint = colorTheme.value.confirmDialogButton, modifier = Modifier.size(64.dp)) {
                                    coroutineScope.launch {
                                        val distance = milesToMeters(distanceSliderPosition.toDouble())
                                        val rating = ratingSliderPosition.toDouble()
                                        val price = priceSliderPosition.toInt()


                                        if (appViewModel.haveRestaurantFiltersChanged(distance, rating, price)) {
                                            showLog("test", "filters changed")
                                            appViewModel.setLocalRestaurantFilterValues(distance, rating, price)
                                            roomInteractions.updateRestaurantFilters(distance, rating, price)

                                            mapInteractions.mapsApiCall()
                                        }

                                        appViewModel.updateShowDialog(appViewModel.NO_DIALOG)
                                    }
                                }
                            }
                        }
                    }
                }

            }
        )
    }

    @Composable
    fun FilterStars(ratingSliderPosition: Float) {
        val size = 16
        for (i in 1..3) {
            Image(
                painterResource(R.drawable.white_star_2,), "white star full",
                modifier = Modifier.size(size.dp, size.dp))
        }
        Spacer(modifier = Modifier.width(2.dp))
        if (ratingSliderPosition.toDouble() == 3.5) {
            Image(painterResource(R.drawable.white_star_half_2,), "white star half", modifier = Modifier.size(size.dp, size.dp))
        }
        if (ratingSliderPosition.toDouble() == 4.0) {
            Image(painterResource(R.drawable.white_star_2,), "white star full", modifier = Modifier.size(size.dp, size.dp))

        }
        if (ratingSliderPosition.toDouble() == 4.5) {
            Image(painterResource(R.drawable.white_star_2,), "white star full", modifier = Modifier.size(size.dp, size.dp))
            Image(painterResource(R.drawable.white_star_half_2,), "white star half", modifier = Modifier.size(size.dp, size.dp))

        }

    }

}