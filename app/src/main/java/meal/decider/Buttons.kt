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
    fun InteractionButtons(restaurantVisibility: Int) {
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()

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
                if (restaurantVisibility == 0) { PlacesButton(color = colorTheme.value.interactionButtons) }
                DiceButton(color = colorTheme.value.interactionButtons)
                MapButton(color = colorTheme.value.interactionButtons)
            }
        }
    }

    @Composable
    fun PlacesButton(color: Int) {
        val coroutineScope = rememberCoroutineScope()

        ButtonUi(
            text = "Places",
            fontSize = 20,
            color = color,
            onClick =  {
                if (!appViewModel.getSquareList.isEmpty()) {
                    if (!appViewModel.getRollEngaged && !appViewModel.getEditMode) {
                        if (appViewModel.getRestaurantQueryFinished) {
                            appViewModel.updateMapQueryInProgress(true)

                            coroutineScope.launch {
                                mapInteractions.mapsApiCall()
                                appViewModel.updateRestaurantVisibility(1)
                            }
                        }
                    }
                }
            })
    }

    @Composable
    fun MapButton(color: Int) {
        val coroutineScope = rememberCoroutineScope()

        ButtonUi(text = "Map", fontSize = 20, color = color, onClick = {
            if (!appViewModel.getRollEngaged && !appViewModel.getEditMode) {
                coroutineScope.launch {
                    if (appViewModel.getRestaurantVisibility == 0) {
                        if (!appViewModel.getSquareList.isEmpty()) {
                            mapInteractions.mapIntent(appViewModel.cuisineStringUri)
                        }
                    } else {
                        if (!appViewModel.getRestaurantList.isEmpty()) {
                            if (appViewModel.getRestaurantQueryFinished) {
                                mapInteractions.mapIntent(appViewModel.restaurantStringUri)
                            }
                        }
                    }
                }
            }
        })
    }

    @Composable
    fun DiceButton(color: Int) {
        CustomIconButton(size = 72, image = R.drawable.dice, description = "dice", tint = color) {
            if (!appViewModel.getRollEngaged && !appViewModel.getEditMode) {
                if (appViewModel.getRestaurantVisibility == 0) {
                    if (!appViewModel.getSquareList.isEmpty()) {
                        runnables.rollCuisine()
                        runnables.timer()
                    }

                } else {
                    if (!appViewModel.getRestaurantList.isEmpty()) {
                        if (appViewModel.getRestaurantQueryFinished) {
                            runnables.rollRestaurant()
//                            appViewModel.testRestaurantRoll()
                        }
                    }
                }
            }
        }
    }
}