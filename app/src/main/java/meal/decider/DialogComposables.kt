package meal.decider

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import meal.decider.Database.CuisineDatabase
import meal.decider.Database.RoomInteractions
import kotlin.math.floor

class DialogComposables(private val appViewModel: AppViewModel, appDatabase: CuisineDatabase.AppDatabase, private val mapInteractions: MapInteractions, private val runnables: Runnables){
    private val roomInteractions = RoomInteractions(appDatabase, appViewModel)
    private val buttons = Buttons(appViewModel, mapInteractions, runnables)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AddDialogBox() {
        val coroutineScope = rememberCoroutineScope()
        var txtField by remember { mutableStateOf("") }
        val displayedList = appViewModel.displayedCuisineList.collectAsStateWithLifecycle()
        var searchTerms : List<String>

        AnimatedTransitionDialog(
            modifier = Modifier
                .height(400.dp)
                .width(500.dp),
            onDismissRequest = {
                appViewModel.updateAddMode(false)
                appViewModel.updateListOfCuisinesToAdd(emptyList())
            },
            content = {
                Column(modifier = Modifier
                    .background(colorResource(id = R.color.grey_300))
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
                        IconButton(onClick = {
                            appViewModel.updateAddMode(false)
                            appViewModel.updateListOfCuisinesToAdd(emptyList())
                        }) {
                            DialogIcon(imageVector = Icons.Filled.Close, colorResource = android.R.color.holo_red_light)
                        }
                        IconButton(onClick = {
                            appViewModel.addMultipleSquaresToList(appViewModel.getListOfCuisinesToAdd)
                            coroutineScope.launch {
                                roomInteractions.insertMultipleCuisines(appViewModel.getListOfCuisinesToAdd)
                            }
                            appViewModel.updateAddMode(false)
                        }) {
                            DialogIcon(imageVector = Icons.Filled.Check, colorResource = android.R.color.holo_green_light)
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
                if (!listOfCuisinesToAdd.value.contains(list.value[index])) {
                    backgroundColor = R.color.grey_300
                } else {
                    backgroundColor = R.color.grey_500
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
        AnimatedTransitionDialog(
            modifier = Modifier
                .height(200.dp)
                .width(300.dp),
            onDismissRequest = {
                appViewModel.updateRestoreDefaults(false)
            },
            content = {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = colorResource(id = R.color.grey_300)
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
                                Text(
                                    text = "This will restore cuisine list to default!",
                                    fontSize = 18.sp,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Row (modifier = Modifier
                                .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                IconButton(onClick = {
                                    appViewModel.updateRestoreDefaults(false)
                                }) {
                                    DialogIcon(imageVector = Icons.Default.Close, colorResource = android.R.color.holo_red_light)
                                }
                                IconButton(onClick = {
                                    roomInteractions.setSquareDatabaseToDefaultStartingValues()
                                    appViewModel.updateSquareList(appViewModel.starterSquareList())
                                    appViewModel.updateSelectedCuisineSquare(appViewModel.getSquareList[0])
                                    appViewModel.updateEditMode(false)
                                    appViewModel.updateRestoreDefaults(false)
                                }) {
                                    DialogIcon(imageVector = Icons.Default.Check, colorResource = android.R.color.holo_green_light)
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
        val showRestaurantSettings = appViewModel.showRestaurantSettings.collectAsStateWithLifecycle()

        AnimatedTransitionDialog(
            modifier = Modifier
                .fillMaxSize(),
            onDismissRequest = {
                appViewModel.updateShowRestaurants(false)
            },
            content = {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = colorResource(id = R.color.grey_300),
                ) {
                    Column(modifier = Modifier
                        .fillMaxSize()
                    ) {
                        if (showRestaurantSettings.value) {
                            RestaurantFilterDialog()
                        }
                        Column(modifier = Modifier
                            .wrapContentSize()
                        ) {
                            Row (modifier = Modifier
                                .fillMaxWidth(),
                                horizontalArrangement = Arrangement.End) {
                                RestaurantFilterIcon()
                                RestaurantSortDropdownMenu()
                            }
                        }
                        Column(modifier = Modifier
                            .height(screenHeightPct(0.8).dp)
                        ) {
                            RestaurantLazyGrid()
                        }
                        Column(modifier = Modifier
                            .wrapContentSize()
                        ) {
                            buttons.InteractionButtons()
                        }
                    }
                }
            }
        )
    }

    @Composable
    fun RestaurantLazyGrid() {
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
                            }
                        ),
                ) {
                    RestaurantListTextUi(restaurantList.value[index].name.toString(), true)
                    val distanceInMeters = (restaurantList.value[index].distance)
                    RestaurantListTextUi(doubleMetersToMiles(distanceInMeters!!).toString() + " miles", false)
                    RatingStars(restaurantList.value[index].rating)
                    RestaurantListTextUi(priceToDollarSigns(restaurantList.value[index].priceLevel), false)
                }
            }
        }
    }

    @Composable
    fun RestaurantSortDropdownMenu() {
        var expanded by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .wrapContentSize(Alignment.TopEnd)
        ) {
            IconButton(onClick = { expanded = !expanded }) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "More",
                    tint = Color.Black
                )
            }

            //Order of modifiers matters. Background needs to be set BEFORE padding, otherwise background outside of padding will not be changed.
            DropdownMenu(modifier = Modifier
                .background(colorResource(id = R.color.white))
                .padding(0.dp)
                .wrapContentSize(),
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                RestaurantDropDownUi("Sort A-Z") {
                    appViewModel.sortAndUpdateRestaurantList("name")
                    expanded = false
                }
                RestaurantDropDownUi("Sort by Distance") {
                    appViewModel.sortAndUpdateRestaurantList("distance")
                    expanded = false
                }
                RestaurantDropDownUi("Sort by Rating") {
                    appViewModel.sortAndUpdateRestaurantList("rating")
                    expanded = false
                }
                RestaurantDropDownUi("Sort by Price") {
                    appViewModel.sortAndUpdateRestaurantList("price")
                    expanded = false
                }
                RestaurantDropDownUi("Sort Randomly") {
                    appViewModel.sortAndUpdateRestaurantList("random")
                    expanded = false
                }
            }
        }
    }

    @Composable
    fun RestaurantDropDownUi(text: String, function: () -> Unit) {
        DropdownMenuItem(
            text = { RestaurantSortTextUi(text = text) },
            onClick = {
                function()
            }
        )
    }

    @Composable
    fun RestaurantSortTextUi(text: String) {
        Text(
            text = text,
            fontWeight = FontWeight.Medium,
            fontSize = 14.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(6.dp)
        )
    }

    @Composable
    fun RestaurantFilterIcon() {
            IconButton(onClick = {
                if (!appViewModel.getShowRestaurantSettings) appViewModel.updateShowRestaurantSettings(true)
            }) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "More",
                    tint = Color.Black
                )
            }
    }

    @Composable
    fun OptionsDialogUi() {
        val coroutineScope: CoroutineScope = rememberCoroutineScope()
        var cuisineRollDurationSliderPosition by remember { mutableFloatStateOf(3f) }
        var cuisineRollDelaySliderPosition by remember { mutableFloatStateOf(3f) }
        var restaurantRollDurationSliderPosition by remember { mutableFloatStateOf(3f) }
        var restaurantRollDelaySliderPosition by remember { mutableFloatStateOf(3f) }

        LaunchedEffect(Unit) {
            coroutineScope.launch {
                val rollOptions = roomInteractions.getRollOptions()
                cuisineRollDurationSliderPosition = rollOptions[0].cuisineRollDuration.toFloat()
                cuisineRollDelaySliderPosition = rollOptions[0].cuisineRollDelay.toFloat()
                restaurantRollDurationSliderPosition = rollOptions[0].restaurantRollDuration.toFloat()
                restaurantRollDelaySliderPosition = rollOptions[0]. restaurantRollDelay.toFloat()
            }
        }

        Column (modifier = Modifier
            .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Row () {
                Slider(modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(start = 4.dp),
                    value = cuisineRollDurationSliderPosition,
                    onValueChange = { cuisineRollDurationSliderPosition = it },
                    valueRange = 1f..10f,
                    steps = 9
                )
                SliderTextUi(text = "$cuisineRollDurationSliderPosition stars", size = 18, bold = false)
            }
            Row () {
                Slider(modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .padding(start = 4.dp),
                    value = cuisineRollDelaySliderPosition,
                    onValueChange = { cuisineRollDelaySliderPosition = it },
                    valueRange = 1f..5f,
                    steps = 4
                )
                SliderTextUi(text = "$cuisineRollDelaySliderPosition stars", size = 18, bold = false)
            }
        }
        Row () {
            Slider(modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(start = 4.dp),
                value = restaurantRollDurationSliderPosition,
                onValueChange = { restaurantRollDurationSliderPosition = it },
                valueRange = 1f..5f,
                steps = 4
            )
            SliderTextUi(text = "$restaurantRollDurationSliderPosition stars", size = 18, bold = false)
        }

        Row () {
            Slider(modifier = Modifier
                .fillMaxWidth(0.7f)
                .padding(start = 4.dp),
                value = restaurantRollDelaySliderPosition,
                onValueChange = { restaurantRollDelaySliderPosition = it },
                valueRange = 1f..5f,
                steps = 4
            )
            SliderTextUi(text = "$restaurantRollDelaySliderPosition stars", size = 18, bold = false)
        }
    }

    @Composable
    fun RestaurantFilterDialog() {
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

        var priceString: String

        AnimatedTransitionDialog(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = R.color.grey_50)),
            onDismissRequest = {
                appViewModel.updateShowRestaurantSettings(false)
                coroutineScope.launch {
                    roomInteractions.updateRestaurantFilters(distanceSliderPosition.toDouble(), ratingSliderPosition.toDouble(), priceSliderPosition.toDouble())
                }
                //Having this in coroutineScope prevented its execution.
                val maxDistance = milesToMeters(floor(distanceSliderPosition).toDouble())
                val minRating = ratingSliderPosition.toDouble()
                val maxPrice = floor(priceSliderPosition).toInt()
                if (appViewModel.haveRestaurantFiltersChanged(maxDistance, minRating, maxPrice)) {
                    appViewModel.setLocalRestaurantFilterValues(maxDistance, minRating, maxPrice)
                    coroutineScope.launch {
                        mapInteractions.mapsApiCall()
//                        mapInteractions.testRestaurants()
                    }
                }
            },
            content = {
                Surface(
                    color = colorResource(id = R.color.grey_300),
                ) {
                    Box(modifier = Modifier
                        .fillMaxSize(),
                    ) {
                        Column (horizontalAlignment = Alignment.CenterHorizontally)
                        {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                SliderTextUi(text = "Filters", size = 22, bold = true)
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                            Column {
                                SliderTextUi(text = "Distance", size = 20 , bold = false)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row () {
                                    Slider(modifier = Modifier
                                        .fillMaxWidth(0.75f)
                                        .padding(start = 4.dp),
                                        value = distanceSliderPosition,
                                        onValueChange = { distanceSliderPosition = it
                                        },
                                        valueRange = 1f..10f
                                    )
                                    SliderTextUi(text = distanceSliderPosition.toInt().toString() + " mi", size = 18, bold = false)
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                SliderTextUi(text = "Rating", size = 20 , bold = false)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row () {
                                    Slider(modifier = Modifier
                                        .fillMaxWidth(0.7f)
                                        .padding(start = 4.dp),
                                        value = ratingSliderPosition,
                                        onValueChange = { ratingSliderPosition = it },
                                        valueRange = 3f..4.5f,
                                        steps = 2
                                    )
                                    SliderTextUi(text = "$ratingSliderPosition stars", size = 18, bold = false)
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                SliderTextUi(text = "Max Price", size = 20 , bold = false)
                                Spacer(modifier = Modifier.height(8.dp))
                                Row () {
                                    Slider(modifier = Modifier
                                        .fillMaxWidth(0.7f)
                                        .padding(start = 4.dp),
                                        value = priceSliderPosition,
                                        onValueChange = { priceSliderPosition = it },
                                        valueRange = 1f..4f,
                                        steps = 2
                                    )
                                    priceString = ""
                                    for (i in 1..priceSliderPosition.toInt()) {
                                        priceString += "$"
                                    }
                                    SliderTextUi(text = priceString, size = 18, bold = false)
                                }
                            }
                        }
                    }
                }
            }
        )
    }

    @Composable
    fun SliderTextUi(text: String?, size: Int, bold: Boolean) {
        var fontWeight: FontWeight = FontWeight.Normal
        if (bold) fontWeight = FontWeight.Bold
        if (text != null) {
            Text(
                modifier = Modifier
                    .padding(8.dp, 10.dp),
                fontSize = size.sp,
                color = Color.Black,
                text = text,
                fontWeight = fontWeight
            )
        }
    }

    //If we don't use ? in front of variable, Kotlin won't let it be null (? == nullable)
    @Composable
    fun RestaurantListTextUi(text: String?, bold: Boolean) {
        var fontWeight: FontWeight = FontWeight.Normal
        if (bold) fontWeight = FontWeight.Bold
        if (text != null) {
            Text(
                modifier = Modifier
                    .padding(8.dp, 4.dp),
                fontSize = 14.sp,
                color = Color.Black,
                text = text,
                fontWeight = fontWeight
            )
        }
    }

    @Composable
    fun RatingStars(rating: Double?) {
        if (rating != null) {
            val roundedDown = rating.toInt()
            val remainder = rating - roundedDown
            Row (modifier = Modifier
                .padding(8.dp, 4.dp)) {
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
            AnimatedTransitionDialog(
                modifier = Modifier
                    .fillMaxSize(),
                onDismissRequest = {
                    appViewModel.updateOptionsMode(false)
                },
                content = {
                    Column(modifier = Modifier
                        .background(colorResource(id = R.color.grey_300))
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
    fun DialogIcon(imageVector: ImageVector, colorResource: Int) {
        Icon(
            imageVector = imageVector,
            contentDescription = "",
            tint = colorResource(colorResource),
            modifier = Modifier
                .width(50.dp)
                .height(50.dp)
        )
    }
}