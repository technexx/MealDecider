package meal.decider

import android.content.Context
import android.view.Gravity
import android.widget.Toast
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
import meal.decider.Database.CuisineDatabase
import meal.decider.Database.RoomInteractions

class DialogComposables(private val activityContext: Context, private val appViewModel: AppViewModel, appDatabase: CuisineDatabase.AppDatabase){
    private val roomInteractions = RoomInteractions(appDatabase, appViewModel)

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AddDialogBox() {
        var txtField by remember { mutableStateOf("") }
        val displayedList = appViewModel.displayedCuisineList.collectAsStateWithLifecycle()
        var searchTerms : List<String>

        //Search box will begin with full list shown.
        appViewModel.updateDisplayedCuisineList(fullCuisineList)

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
                                if (!appViewModel.doesCuisineExistsOnBoard(txtField, appViewModel.squareNamesList())) {
                                    appViewModel.addSquareToList(txtField)
                                    appViewModel.updateAddMode(false)
                                } else {
                                    Toast.makeText(activityContext, "Cuisine already exists!", Toast.LENGTH_SHORT).show()
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
        var backgroundColor = R.color.white

        LazyColumn (
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            items (list.value.size) { index ->
                val coroutineScope = rememberCoroutineScope()

                //TODO: Don't allow duplicate entries. Remove if going to duplicate instead (as a toggle).
                //TODO: Three recomps onClick. Should be one.

                //TODO: Even a single true will change all entries.
                if (listOfCuisinesToAdd.value.contains(list.value[index])) {
                    backgroundColor = R.color.grey_300
                    println("true")
                }

                println("recomp")

                Column (modifier = Modifier
                    .background(colorResource(backgroundColor))
                    .padding(4.dp)
                    .selectable(
                        selected = true,
                        onClick = {
                            appViewModel.addSquareToListOfSquaresToAdd(index)
//                            if (!appViewModel.doesCuisineExistsOnBoard(
//                                    list.value[index],
//                                    appViewModel.squareNamesList()
//                                )
//                            ) {
//                                appViewModel.addSquareToList(list.value[index])
//                                appViewModel.updateAddMode(false)
//                                coroutineScope.launch {
//                                    roomInteractions.insertCuisine(
//                                        list.value[index],
//                                        defaultSquareColor
//                                    )
//                                }
//                            } else {
//                                Toast
//                                    .makeText(
//                                        activityContext,
//                                        "Cuisine already exists!",
//                                        Toast.LENGTH_SHORT
//                                    )
//                                    .show()
//                            }
                        }
                    )) {
                    Text(modifier = Modifier
                        .padding(4.dp),
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
            appViewModel.updateAddMode(false)
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