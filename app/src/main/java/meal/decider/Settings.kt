package meal.decider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import meal.decider.Database.RoomInteractions

class Settings(val appViewModel: AppViewModel, val roomInteractions: RoomInteractions) {
    @Composable
    fun OptionsDialogUi() {
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()
        val textColor = colorResource(id = colorTheme.value.dialogTextColor)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(colorResource(id = colorTheme.value.dialogBackground)),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(20.dp))
            RegText("Settings", fontSize = 34, color = textColor)
            Spacer(modifier = Modifier.height(20.dp))
            RegTextButton(text = "Speeds", fontSize = 26, color = textColor,
                onClick = {
                    appViewModel.updateSettingsDialogVisibility(speeds = true, sounds = false, colors = false)
                })
            Spacer(modifier = Modifier.height(10.dp))
            RegTextButton(text = "Colors",  fontSize = 26, color = textColor,
                onClick = {
                    appViewModel.updateSettingsDialogVisibility(speeds = false, sounds = false, colors = true)
                })
            Row {
                RegTextButton(text = "Auto Scroll",  fontSize = 26, color = textColor,
                    onClick = {
                        appViewModel.updateSettingsDialogVisibility(speeds = false, sounds = false, colors = true)
                    })
                Spacer(modifier = Modifier.width(8.dp))

                var checked by remember { mutableStateOf(false) }

                LaunchedEffect(Unit) {
                    checked = appViewModel.restaurantAutoScroll
                    updateAutoScrollSettingInDatabase(checked)
                }

                Switch(modifier = Modifier
                    .padding(top = 14.dp),
                    checked = checked,
                    onCheckedChange = {
                        checked = it
                        appViewModel.restaurantAutoScroll = checked
                        updateAutoScrollSettingInDatabase(checked)
                    }
                )
            }

        }
    }

    private fun updateAutoScrollSettingInDatabase(isOn: Boolean) {
        ioScope.launch {
            roomInteractions.updateAutoScroll(isOn)
        }
    }

    @Composable
    fun ColorsSettingDialog() {
        val colorSettingsToggle = appViewModel.colorSettingsSelectionList.collectAsStateWithLifecycle()
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()
        //Sets color setting to correct button highlight (for first open of settings)
        appViewModel.switchColorSettingsUi(roomInteractions.retrieveColorThemeIndexFromSharedPref())

        Column(modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = colorTheme.value.dialogBackground)), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(12.dp))
            Row {
                RegText(text = "Theme", fontSize = 28, color = colorResource(id = colorTheme.value.dialogTextColor), fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 128.dp),
                contentPadding = PaddingValues(
                    start = 24.dp,
                    top = 16.dp,
                    end = 24.dp,
                    bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                content = {
                    items(colorSettingsToggle.value.size) { index ->
                        val cardColor = if (appViewModel.getColorSettingsSelectionList[index].selected) colorResource(id = R.color.grey_500) else Color.White

                        Box(contentAlignment = Alignment.Center) {
                            CardUi(
                                color = cardColor,
                                onClick = {
                                    appViewModel.switchColorSettingsUi(index)
                                    appViewModel.updateColorTheme(Theme.themeColorsList[index])
                                    roomInteractions.saveColorThemeToSharedPref(Theme.themeColorsList[index])
                                },
                                content = {
                                    RegText(
                                        text = colorSettingsToggle.value[index].name,
                                        fontSize = 26,
                                        color = Color.Black,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(12.dp))
                                })
                        }
                    }
                }
            )
        }
    }

    private fun updateSpeedSettingsInDatabase(cuisineRollDurationSliderPosition: Float, cuisineRollDelaySliderPosition: Float, restaurantRollDurationSliderPosition:Float, restaurantRollDelaySliderPosition:Float) {
        ioScope.launch {
            roomInteractions.updateRollOptions(cuisineRollDurationSliderPosition.toLong(), cuisineRollDelaySliderPosition.toLong(), restaurantRollDurationSliderPosition.toLong(), restaurantRollDelaySliderPosition.toLong())
            appViewModel.updateRollOptions(cuisineRollDurationSliderPosition.toLong(), cuisineRollDelaySliderPosition.toLong(), restaurantRollDurationSliderPosition.toLong(), restaurantRollDelaySliderPosition.toLong())
        }
    }

    @Composable
    fun SpeedSettingsDialog() {
        val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()
        val textColor = colorResource(id = colorTheme.value.settingsText)
        val fontSize = 24
        val spacerSizeOne = 12
        val spacerSizeTwo = 24
        val coroutineScope: CoroutineScope = rememberCoroutineScope()
        var cuisineRollDurationSliderPosition by remember { mutableFloatStateOf(3f) }
        var cuisineRollDelaySliderPosition by remember { mutableFloatStateOf(3f) }
        var restaurantRollDurationSliderPosition by remember { mutableFloatStateOf(3f) }
        var restaurantRollDelaySliderPosition by remember { mutableFloatStateOf(3f) }


        LaunchedEffect(Unit) {
            cuisineRollDurationSliderPosition = appViewModel.cuisineRollDurationSetting.toFloat()
            cuisineRollDelaySliderPosition = appViewModel.cuisineRollSpeedSetting.toFloat()
            restaurantRollDurationSliderPosition = appViewModel.restaurantRollDurationSetting.toFloat()
            restaurantRollDelaySliderPosition = appViewModel.restaurantRollSpeedSetting.toFloat()
        }

        Column(modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = colorTheme.value.dialogBackground)),
            horizontalAlignment = Alignment.CenterHorizontally) {
            Column (modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally){
                Spacer(modifier = Modifier.height(10.dp))

                RegText("Randomization Duration", fontSize = fontSize, color = textColor)
                Row () {
                    Slider(modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(start = 4.dp),
                        value = cuisineRollDurationSliderPosition,
                        onValueChange = {
                            cuisineRollDurationSliderPosition = it
                            updateSpeedSettingsInDatabase(cuisineRollDurationSliderPosition, cuisineRollDelaySliderPosition, restaurantRollDurationSliderPosition, restaurantRollDelaySliderPosition)
                                        },
                        valueRange = 1f..10f,
                        steps = 9
                    )
                    Spacer(modifier = Modifier.height(spacerSizeOne.dp))
                    RegText(text = "${cuisineRollDurationSliderPosition.toInt()}", fontSize = fontSize, color = textColor)
                }
                Spacer(modifier = Modifier.height(spacerSizeTwo.dp))

                RegText("Randomization Speed", fontSize = fontSize, color = textColor)
                Row () {
                    Slider(modifier = Modifier
                        .fillMaxWidth(0.7f)
                        .padding(start = 4.dp),
                        value = cuisineRollDelaySliderPosition,
                        onValueChange = { cuisineRollDelaySliderPosition = it
                            updateSpeedSettingsInDatabase(cuisineRollDurationSliderPosition, cuisineRollDelaySliderPosition, restaurantRollDurationSliderPosition, restaurantRollDelaySliderPosition)
                                        },
                        valueRange = 1f..10f,
                        steps = 9
                    )
                    Spacer(modifier = Modifier.height(spacerSizeOne.dp))

                    RegText(text = "${cuisineRollDelaySliderPosition.toInt()}", fontSize = fontSize, color = textColor)
                }
                Spacer(modifier = Modifier.height(spacerSizeTwo.dp))

//                RegText("Restaurant Selection Duration", fontSize = fontSize, color = textColor)
//                Row () {
//                    Slider(modifier = Modifier
//                        .fillMaxWidth(0.7f)
//                        .padding(start = 4.dp),
//                        value = restaurantRollDurationSliderPosition,
//                        onValueChange = { restaurantRollDurationSliderPosition = it
//                            updateSpeedSettingsInDatabase(cuisineRollDurationSliderPosition, cuisineRollDelaySliderPosition, restaurantRollDurationSliderPosition, restaurantRollDelaySliderPosition)
//                                        },
//                        valueRange = 1f..10f,
//                        steps = 9
//                    )
//                    Spacer(modifier = Modifier.height(spacerSizeOne.dp))
//
//                    RegText(text = "${restaurantRollDurationSliderPosition.toInt()}", fontSize = fontSize, color = textColor)
//                }
//                Spacer(modifier = Modifier.height(spacerSizeTwo.dp))
//
//                RegText("Restaurant Selection Speed", fontSize = fontSize, color = textColor)
//                Row () {
//                    Slider(modifier = Modifier
//                        .fillMaxWidth(0.7f)
//                        .padding(start = 4.dp),
//                        value = restaurantRollDelaySliderPosition,
//                        onValueChange = { restaurantRollDelaySliderPosition = it
//                            updateSpeedSettingsInDatabase(cuisineRollDurationSliderPosition, cuisineRollDelaySliderPosition, restaurantRollDurationSliderPosition, restaurantRollDelaySliderPosition)
//                                        },
//                        valueRange = 1f..10f,
//                        steps = 9
//                    )
//                    Spacer(modifier = Modifier.height(spacerSizeOne.dp))
//
//                    RegText(text = "${restaurantRollDelaySliderPosition.toInt()}", fontSize = fontSize, color = textColor)
//                }
            }
        }

    }
}