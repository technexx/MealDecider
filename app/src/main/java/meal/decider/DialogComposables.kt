package meal.decider

import android.content.Context
import android.view.Gravity
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogWindowProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import meal.decider.Database.CuisineDatabase
import meal.decider.Database.RoomInteractions

class DialogComposables(private val activityContext: Context, private val appViewModel: AppViewModel, appDatabase: CuisineDatabase.AppDatabase){
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
                            }) {
                                DialogIcon(imageVector = Icons.Filled.Close, colorResource = android.R.color.holo_red_light)
                            }
                            IconButton(onClick = {
                                appViewModel.addMultipleSquaresToList(appViewModel.getListOfCuisinesToAdd)
                                appViewModel.updateAddMode(false)
                                coroutineScope.launch {
                                    roomInteractions.insertMultipleCuisines(appViewModel.getListOfCuisinesToAdd)
                                }
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

    //TODO: Set some onClick to toggle this dialog to test.
    @Composable
    fun RestaurantDialog() {
        val restaurantList = appViewModel.restaurantList.collectAsStateWithLifecycle()

        Dialog(onDismissRequest = {
            appViewModel.updateShowRestaurants(false)
        })
        {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = colorResource(id = R.color.grey_300)
            ) {
                Box(modifier = Modifier
                    .size(height = 500.dp, width = 300.dp),
                ) {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        items(restaurantList.value.size) { index ->
                            Column(modifier = Modifier
                                .padding(4.dp)
                                .selectable(
                                    selected = true,
                                    onClick = {
                                    }
                                )) {
                                Column {
                                    //TODO: Populate restaurantList w/ values from json fetch.
                                    RestaurantListTextUi(text = restaurantList.value[index].name.toString())
                                    RestaurantListTextUi(text = restaurantList.value[index].address.toString())
                                    RestaurantListTextUi(text = restaurantList.value[index].distance.toString())
                                    RestaurantListTextUi(text = restaurantList.value[index].priceLevel.toString())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun RestaurantListTextUi(text: String) {
        Text(
            modifier = Modifier
                .background(
                    colorResource(R.color.white),
                    shape = RoundedCornerShape(5.dp)
                )
                .padding(8.dp),
            fontSize = 20.sp,
            color = Color.Black,
            text = text
        )
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
    fun OptionsHeaderTextUi(text: String) {
        Text(text = text,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp,
            color = Color.Black,
            textAlign = TextAlign.Center)
    }

    @Composable
    fun OptionsBoxesUi(text: String) {
        Text(
            text = text,
            fontSize = 18.sp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(12.dp)
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