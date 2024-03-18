package meal.decider

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

class Buttons (private val appViewModel: AppViewModel, private val mapInteractions: MapInteractions, private val runnables: Runnables){



    @SuppressLint("MissingPermission")
    @Composable
    fun InteractionButtons() {
        val coroutineScope = rememberCoroutineScope()

        Column (
            modifier = Modifier
                .wrapContentSize()
                .padding(top = 12.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.Bottom

        ) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 0.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!appViewModel.getShowRestaurants) {
                    ButtonUi(text = "Places", onClickAction =  {
                        if (!appViewModel.getRollEngaged && !appViewModel.getEditMode) {
                            if (appViewModel.getRestaurantQueryFinished) {
                                coroutineScope.launch {
                                    if (!appViewModel.getShowRestaurants) {
                                        if (appViewModel.hasCuisineStringUriChanged) {
                                            mapInteractions.mapsApiCall()
                                            //Sets first entry to string for maps launch.
                                            appViewModel.updateSelectedRestaurantSquare(appViewModel.getRestaurantList[0])
                                            appViewModel.updateSingleRestaurantColorAndBorder(0, chosenRestaurantColor, defaultRestaurantBorderStroke)
                                            appViewModel.restaurantStringUri = appViewModel.getRestaurantList[0].name.toString()
                                        }
                                        appViewModel.updateShowRestaurantsDialog(true)
                                        appViewModel.updateShowRestaurants(true)
                                    }
                                }
                            }
                        }
                    })
                }
                CustomIconButton(size = 72, image = R.drawable.dice, description = "dice", tint = ThemeObject.interactionButtons) {
                    if (!appViewModel.getRollEngaged && !appViewModel.getEditMode) {
                        if (!appViewModel.getShowRestaurants) {
                            runnables.rollCuisine()
                        } else {
                            if (appViewModel.getRestaurantQueryFinished) {
                                runnables.rollRestaurant()
//                            appViewModel.testRestaurantRoll()
                            }
                        }
                    }
                }
                ButtonUi(text = "Map", onClickAction = {
                    if (!appViewModel.getRollEngaged && !appViewModel.getEditMode) {
                        coroutineScope.launch {
                            if (!appViewModel.getShowRestaurants) {
                                mapInteractions.mapIntent(appViewModel.cuisineStringUri)
                            } else {
                                if (appViewModel.getRestaurantQueryFinished) {
                                    mapInteractions.mapIntent(appViewModel.restaurantStringUri)
                                }
                            }
                        }
                    }
                })
            }
        }
    }

    @Composable
    fun ButtonUi(text: String, onClickAction: () -> Unit) {
        Button(
            onClick = {
                onClickAction()
            },
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 6.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorResource(id = R.color.blue_400)),
        ) {
            RegText(text = text, fontSize = 18, color = Color.Black, fontWeight = FontWeight.Bold)
        }
    }
}