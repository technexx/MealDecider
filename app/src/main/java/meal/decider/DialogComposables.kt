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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import meal.decider.Database.CuisineDatabase
import meal.decider.Database.RoomInteractions

class DialogComposables(private val appViewModel: AppViewModel, appDatabase: CuisineDatabase.AppDatabase, private val activity: Activity, private val mapInteractions: MapInteractions, private val runnables: Runnables){
    private val roomInteractions = RoomInteractions(appDatabase, appViewModel, activity)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AddDialogBox() {
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()
        val coroutineScope = rememberCoroutineScope()
        var txtField by remember { mutableStateOf("") }
        val displayedList = appViewModel.displayedCuisineList.collectAsStateWithLifecycle()

        var modifiedList = appViewModel.modifiedAddCuisineList(appViewModel.getSquareList)
        appViewModel.updateDisplayedCuisineList(modifiedList)

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
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                            value = txtField,
                            placeholder = {Text( "e.g. Filipino") },
                            onValueChange = {
                                //Default to modified list (full list minus cuisines on board).
                                modifiedList = appViewModel.modifiedAddCuisineList(appViewModel.getSquareList)
                                txtField = it
                                //Modified list is filtered from text entered.
                                modifiedList = filterSearchString(modifiedList, txtField)
                                showLog("test", "update list in onValueChanged is $modifiedList")
                                appViewModel.updateDisplayedCuisineList(modifiedList) },
                            singleLine = true,
                            textStyle = TextStyle(color = Color.Black, fontSize = 22.sp, fontWeight = FontWeight.Bold),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = colorResource(colorTheme.value.textBoxBackground),
                            ),
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    DisplayedCuisineList(displayedList)

                    Row (modifier = Modifier
                        .fillMaxSize(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        MaterialIconButton(icon = Icons.Filled.Close, description = "close", tint = colorTheme.value.cancelDialogButton, modifier = Modifier.size(96.dp)) {
                            appViewModel.updateAddMode(false)
                            appViewModel.updateListOfCuisinesToAdd(emptyList())
                        }
                        MaterialIconButton(icon = Icons.Filled.Check, description = "confirm", tint = colorTheme.value.confirmDialogButton, modifier = Modifier.size(96.dp)) {
                            coroutineScope.launch {
                                if (appViewModel.getListOfCuisinesToAdd.isNotEmpty()) {
                                    val cuisineListIsEmpty = appViewModel.getSquareList.isEmpty()

                                    appViewModel.addMultipleSquaresToList(appViewModel.getListOfCuisinesToAdd, cuisineListIsEmpty)
                                    roomInteractions.insertMultipleCuisines(appViewModel.getListOfCuisinesToAdd)
                                    appViewModel.updateAddMode(false)
                                    appViewModel.updateEditMode(false)
                                    appViewModel.updateListOfCuisinesToAdd(emptyList())
                                }
                            }
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
                    val offSet = Offset(3.0f, 6.0f)

                    Text(
                        modifier = Modifier
//                            .shadow(6.dp)
                            .background(
                                colorResource(backgroundColor),
                                shape = RoundedCornerShape(5.dp)
                            )
                            .padding(8.dp),
                        fontSize = 20.sp,
                        color = colorResource(id = colorTheme.value.dialogTextColor),
                        style = TextStyle(
                            fontSize = 24.sp,
                            shadow = Shadow(
                                color = colorResource(id = colorTheme.value.dialogShadow), offset = offSet, blurRadius = 1f
                            )
                        ),
                        text = list.value[index],
                    )
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
    fun SortDialog() {
        val coroutineScope = rememberCoroutineScope()
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()
        val showDialog = appViewModel.showDialog.collectAsStateWithLifecycle()
        var height = 300
        var width = 300
        val selectedCircleIndex = remember { mutableStateOf(0) }
        var circles = listOf<String>()
        var sortText = "A-Z"
        var indexToSave = 0
        var textSize = 16

        if (showDialog.value == appViewModel.CUISINE_SORT) {
            circles = listOf("A-Z", "Random")
            textSize = 22
        }

        if (showDialog.value == appViewModel.RESTAURANT_SORT) {
            circles = listOf("A-Z", "Distance", "Rating", "Price", "Random")
            textSize = 16
        }

        if (appViewModel.getShowDialog == appViewModel.CUISINE_SORT) {
            height = 200
            selectedCircleIndex.value = appViewModel.cuisineSortIndex
            sortText = circles[appViewModel.cuisineSortIndex]
        }
        if (appViewModel.getShowDialog == appViewModel.RESTAURANT_SORT) {
            selectedCircleIndex.value = appViewModel.restaurantSortIndex
            sortText = circles[appViewModel.restaurantSortIndex]
        }

        AnimatedTransitionDialog(
            modifier = Modifier
                .background(Color.Transparent)
                .height(height.dp)
                .width(width.dp),
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
//                                    showLog("test", "selectable is ${selectedCircleIndex.value}")

                                    SelectableCircle(
                                        selected = index == selectedCircleIndex.value,
                                        text = circleText,
                                        textSize = textSize,
                                        onClick = {
                                            selectedCircleIndex.value = index
                                            sortText = circleText
                                            indexToSave = index
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
//                                            roomInteractions.updateCuisines()
                                            roomInteractions.deleteAllCuisines()
                                            roomInteractions.insertMultipleCuisines(appViewModel.getListOfSquareNames())
                                        }

                                        appViewModel.cuisineSortIndex = indexToSave
                                    }
                                    if (appViewModel.getShowDialog == appViewModel.RESTAURANT_SORT) {
                                        appViewModel.sortAndUpdateRestaurantList(sortText)
                                        appViewModel.restaurantSortIndex = indexToSave
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
    fun DisclaimerDialog() {
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()
        val textColor = colorResource(id = colorTheme.value.dialogTextColor)

        AnimatedTransitionDialog(
            modifier = Modifier
                .height(200.dp)
                .width(400.dp)
                .background(Color.Transparent)
            ,
            onDismissRequest = {
                appViewModel.updateShowDialog(appViewModel.NO_DIALOG)
            },
            content = {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = colorResource(colorTheme.value.dialogBackground)
                ) {
                    Box(modifier = Modifier
                        .wrapContentSize()
                    ) {
                        Column(modifier = Modifier
                            .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        )
                        {
                            RegText(text = "Restaurants are pulled directly from Google Maps searches, and no guarantee can be made as to their relevance or accuracy.", color = textColor, fontSize = 20,
                                modifier = Modifier
                                    .padding(start = 4.dp, end = 4.dp))
                        }
                    }
                }
            })
    }

    @Composable
    fun RestaurantFiltersDialog() {
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()
        val coroutineScope: CoroutineScope = rememberCoroutineScope()
        var distanceSliderPosition by remember { mutableFloatStateOf(3f) }
        var ratingSliderPosition by remember { mutableFloatStateOf(3f) }
        var priceSliderPosition by remember { mutableFloatStateOf(1f) }
        var isOpen by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            coroutineScope.launch {
                val restaurantFilters = roomInteractions.getRestaurantFilters()
                distanceSliderPosition = doubleMetersToMiles(restaurantFilters[0].distance).toFloat()
                ratingSliderPosition = restaurantFilters[0].rating.toFloat()
                priceSliderPosition = restaurantFilters[0].price.toFloat()
                isOpen = restaurantFilters[0].openNow
            }
        }

        val textColor = colorResource(id = colorTheme.value.dialogTextColor)
        var priceString: String

        AnimatedTransitionDialog(
            modifier = Modifier
                .height(500.dp)
                .fillMaxWidth()
                .background(Color.Transparent),
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
                                        valueRange = 1f..20f
                                    )
                                    Column (modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 10.dp, end = 6.dp),
                                        verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.End){
                                        RegText(text = distanceSliderPosition.toInt().toString() + " mi", fontSize = 18, color = textColor)
                                    }

                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                RegText(text = "Maximum Price", fontSize = 20, color = textColor)
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
                                    }
                                }

                                Spacer(modifier = Modifier.height(24.dp))

                                Column (modifier = Modifier){
                                    Row(modifier = Modifier
                                        .fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween) {
                                        RegText(text = "Open Now", fontSize = 20 , color = textColor, modifier = Modifier
                                            .padding(top = 6.dp))
                                        Checkbox(modifier = Modifier,
                                            checked = isOpen, onCheckedChange = {
                                            isOpen = it
                                        })
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

                                        if (appViewModel.haveRestaurantFiltersChanged(distance, rating, price, isOpen)) {
                                            appViewModel.setLocalRestaurantFilterValues(distance, rating, price, isOpen)
                                            roomInteractions.updateRestaurantFilters(distance, rating, price, isOpen)
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