package meal.decider

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch

class Buttons (private val appViewModel: AppViewModel, private val mapInteractions: MapInteractions, private val runnables: Runnables){

    @SuppressLint("MissingPermission")
    @Composable
    fun InteractionButtons() {
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()
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
                    ButtonUi(text = "Places", fontSize = 20, color = colorTheme.value.interactionButtons, onClick =  {
                        if (!appViewModel.getRollEngaged && !appViewModel.getEditMode) {
                            if (appViewModel.getRestaurantQueryFinished) {
                                coroutineScope.launch {
                                    if (!appViewModel.getShowRestaurants) {
                                        if (appViewModel.hasCuisineStringUriChanged) {
                                            mapInteractions.mapsApiCall()
                                            //Sets first entry to string for maps launch.
                                            appViewModel.updateSelectedRestaurantSquare(appViewModel.getRestaurantList[0])
                                            appViewModel.updateSingleRestaurantColorAndBorder(0, appViewModel.getColorTheme.selectedRestaurantSquare, defaultRestaurantBorderStroke)
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


                CustomIconButton(size = 72, image = R.drawable.dice, description = "dice", tint = colorTheme.value.interactionIcons) {
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
                ButtonUi(text = "Map", fontSize = 20, color = colorTheme.value.interactionButtons, onClick = {
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
}