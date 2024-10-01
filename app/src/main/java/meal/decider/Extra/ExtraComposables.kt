package meal.decider.Extra

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import meal.decider.AppViewModel
import meal.decider.R
import meal.decider.RestrictionsValues
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditDialogBox(appViewModel: AppViewModel) {
    var txtField by remember { mutableStateOf("") }
    txtField = appViewModel.getSquareList[appViewModel.singleSquareIndexToEdit].name

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
//                            DialogIcon(imageVector = Icons.Filled.Close, colorResource = android.R.color.holo_red_light)
                        }
                        IconButton(onClick = {
                            appViewModel.updateSquareName(appViewModel.singleSquareIndexToEdit, txtField)
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
fun DietaryRestrictions(appViewModel: AppViewModel) {
    val restrictionsUi = appViewModel.restrictionsList.collectAsStateWithLifecycle()
    var cardColor: Color

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        content = {
            items(restrictionsUi.value.size) { index ->
                if (appViewModel.getRestrictionsList[index].selected) {
                    cardColor = colorResource(id = R.color.blue_grey_800)
                } else  {
                    cardColor = Color.White
                }
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = cardColor,
                    ),
                    border =  BorderStroke(1.dp, Color.Black),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(4.dp)
                        .selectable(
                            selected = true,
                            onClick = {
                                //Will only work if new item is added or item is subtracted w/ List, but will work w/ SnapshotStateList provided a new instance is created before update.
                                val list = appViewModel.getRestrictionsList
                                list[index].selected = !list[index].selected
                                val updatedList = mutableStateListOf<RestrictionsValues>()
                                updatedList.addAll(list)

                                appViewModel.updateRestrictionsList(updatedList)
                            }
                        ),
                ) {
//                    OptionsBoxesUi(text = appViewModel.getRestrictionsList[index].name)
                }
            }
        }
    )
}