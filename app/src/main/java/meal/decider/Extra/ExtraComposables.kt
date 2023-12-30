package meal.decider.Extra

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import meal.decider.AppViewModel
import meal.decider.R
import meal.decider.RestrictionsValues

@Composable
fun DietaryRestrictions(appViewModel: AppViewModel) {
    val restrictionsUi = appViewModel.restrictionsList.collectAsStateWithLifecycle()
    var cardColor: Color

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 128.dp),
        content = {
            items(restrictionsUi.value.size) { index ->
                if (appViewModel.getRestrictionsList[index].selected) {
                    cardColor = colorResource(id = R.color.blue_grey_100)
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