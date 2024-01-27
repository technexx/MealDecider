package meal.decider

import android.view.Gravity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogWindowProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import meal.decider.Database.CuisineDatabase
import meal.decider.Database.RoomInteractions

class DialogComposables(private val appViewModel: AppViewModel, appDatabase: CuisineDatabase.AppDatabase, private val mapInteractions: MapInteractions){
    private val roomInteractions = RoomInteractions(appDatabase, appViewModel)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AddDialogBox() {
        val coroutineScope = rememberCoroutineScope()
        var txtField by remember { mutableStateOf("") }
        val displayedList = appViewModel.displayedCuisineList.collectAsStateWithLifecycle()
        var searchTerms : List<String>

        //Full list of cuisines added, then existing squares on main board subtracted.
        appViewModel.updateDisplayedCuisineList(fullCuisineList)
        appViewModel.adjustDisplayedCuisineListFromDisplayedSquares()

        Dialog(onDismissRequest = {
            appViewModel.updateAddMode(false)
            appViewModel.updateListOfCuisinesToAdd(emptyList())
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
                                    searchTerms = appViewModel.filterList(fullCuisineList, txtField)
                                    appViewModel.updateDisplayedCuisineList(searchTerms)},
                                singleLine = true,
                                textStyle = TextStyle(color = Color.Black, fontSize = 22.sp, fontWeight = FontWeight.Bold),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    containerColor = colorResource(id = R.color.grey_50),
                                ),
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        DisplayedCuisineList(displayedList)
                        appViewModel.adjustDisplayedCuisineListFromDisplayedSquares()

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
                                    appViewModel.updateListOfCuisinesToAdd(emptyList())
                                }
                                appViewModel.updateAddMode(false)
                            }) {
                                DialogIcon(imageVector = Icons.Filled.Check, colorResource = android.R.color.holo_green_light)
                            }
                        }
                    }
                }
            }

        }
    }

    //List (or any object) in State<Object> is accessed w/ (Var).value.
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
        Dialog(onDismissRequest = {
            appViewModel.updateRestoreDefaults(false)
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
                                roomInteractions.setSquareValuesAndDatabaseToDefaultStartingValues()
                                appViewModel.updateRestoreDefaults(false)
                            }) {
                                DialogIcon(imageVector = Icons.Default.Check, colorResource = android.R.color.holo_green_light)
                            }
                        }
                    }
                }
            }

        }
    }

    @Composable
    fun RestaurantDialog() {
        Dialog(onDismissRequest = {
            appViewModel.updateShowRestaurants(false)
        })
        {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = colorResource(id = R.color.grey_300),
            ) {
                Box(modifier = Modifier
                    .fillMaxSize(),
                ) {
                    Column {
                        Row (modifier = Modifier
                            .fillMaxWidth(),
                            horizontalArrangement = Arrangement.End) {
                            RestaurantSortMenu()

                        }
                        RestaurantLazyGrid()
                    }
                }
                Box(modifier = Modifier
                    .fillMaxSize()
                )
                {
                    Column (modifier = Modifier
                        .fillMaxSize(),
                        verticalArrangement = Arrangement.Bottom) {
                        InteractionButtons()
                    }
                }

            }
        }
    }

    @Composable
    fun RestaurantSortMenu() {
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
                .background(colorResource(id = R.color.black))
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
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(6.dp)
        )
    }

    @Composable
    fun RestaurantLazyGrid() {
        val coroutineScope = rememberCoroutineScope()
        val sectionGridState = rememberLazyStaggeredGridState()
        val restaurantList = appViewModel.restaurantList.collectAsStateWithLifecycle()
        val selectedRestaurantSquare = appViewModel.selectedRestaurantSquare.collectAsStateWithLifecycle()
        val restaurantRollFinished = appViewModel.restaurantRollFinished.collectAsStateWithLifecycle()
        val dummyList = appViewModel.dummyRestaurantList()
        val restaurantSelectionBorderStroke = appViewModel.restaurantSelectionBorderStroke.collectAsStateWithLifecycle()

        //        val restaurantUri = dummyList[appViewModel.rolledRestaurantIndex].name.toString()
        val rolledRestaurantString = selectedRestaurantSquare.value.name.toString()

        var borderStroke: BorderStroke

        if (restaurantRollFinished.value) {
            LaunchedEffect(Unit) {
                coroutineScope.launch {
                    sectionGridState.animateScrollToItem(appViewModel.rolledRestaurantIndex)
                    appViewModel.restaurantStringUri = rolledRestaurantString
                    appViewModel.restaurantBorderStrokeToggleAnimation()

                    delay(2000)

                    appViewModel.cancelRestaurantBorderStrokeToggleRunnable()
                    appViewModel.resetRestaurantSelectionBorderStroke()
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
                if (index == appViewModel.rolledRestaurantIndex) {
                    borderStroke = restaurantSelectionBorderStroke.value
                } else {
                    borderStroke = BorderStroke(1.dp,Color.Black)
                }

                Card(
                    colors = CardDefaults.cardColors(
//                        containerColor = colorResource(dummyList[index].color!!),
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
//                    RestaurantListTextUi(dummyList[index].name.toString(), true)
//                    RestaurantListTextUi(dummyList[index].distance.toString(), false)
//                    RatingStars(dummyList[index].rating)

                            RestaurantListTextUi(restaurantList.value[index].name.toString(), true)
//                            RestaurantListTextUi(restaurantList.value[index].address.toString(), false)
                            RestaurantListTextUi(restaurantList.value[index].distance.toString() + " miles", false)
//                            RestaurantListTextUi(priceToDollarSigns(restaurantList.value[index].priceLevel), false)
                            RatingStars(restaurantList.value[index].rating)
                }
            }
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

    private fun priceToDollarSigns(price: Int?): String {
        var stringToReturn = ""
        if (price != null) {
            for (i in 1..price) {
                stringToReturn += "$"
            }
        }
        return stringToReturn
    }

    @Composable
    fun OptionsDialog() {
        Dialog(onDismissRequest = {
            appViewModel.updateOptionsMode(false)
        })
        {
            val windowProvider = LocalView.current.parent as DialogWindowProvider
            windowProvider.window.setGravity(Gravity.END)

            Surface(
                shape = RoundedCornerShape(16.dp),
                color = colorResource(id = R.color.grey_300)
            ) {
                Box(modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(),
                ) {
                    Column(modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),)
                    {
                        OptionsDialogUi()
                    }
                }
            }
        }
    }


    @Composable
    fun OptionsDialogUi() {
        Column (modifier = Modifier
            .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally) {

        }
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