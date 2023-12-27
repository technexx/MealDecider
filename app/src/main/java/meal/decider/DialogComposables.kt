package meal.decider

import android.content.Context
import android.view.Gravity
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogWindowProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle

class DialogComposables(private val activityContext: Context, private val appViewModel: AppViewModel) {

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
                        TextField(
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                            value = txtField,
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

                        Spacer(modifier = Modifier.height(10.dp))

                        FullCuisineList(displayedList)

                        Row (modifier = Modifier
                            .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            IconButton(onClick = {
                                appViewModel.updateAddMode(false)
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "",
                                    tint = colorResource(android.R.color.holo_red_light),
                                    modifier = Modifier
                                        .width(50.dp)
                                        .height(50.dp)
                                )
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun EditDialogBox() {
        var txtField by remember { mutableStateOf("") }

        txtField = appViewModel.getSquareList[appViewModel.getSquareToEdit].name

        Dialog(onDismissRequest = {
            appViewModel.updateActiveEdit(false)
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
                        TextField(
                            keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                            value = txtField,
                            onValueChange = { txtField = it },
                            singleLine = true,
                            textStyle = TextStyle(color = Color.Black, fontSize = 22.sp, fontWeight = FontWeight.Bold),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = colorResource(id = R.color.grey_50),
                            ),
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Row (modifier = Modifier
                            .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            IconButton(onClick = {
                                appViewModel.updateActiveEdit(false)
                            }) {
                                DialogIcon(imageVector = Icons.Filled.Close, colorResource = android.R.color.holo_red_light)
                            }
                            IconButton(onClick = {
                                appViewModel.updateSquareName(appViewModel.getSquareToEdit, txtField)
                                appViewModel.updateActiveEdit(false)
                            }) {
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
                    .width(200.dp),
                    contentAlignment = Alignment.CenterEnd
                ) {
                    Column(modifier = Modifier
                        .fillMaxSize()

                        .padding(20.dp),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.SpaceEvenly)
                    {

                    }
                }
            }
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