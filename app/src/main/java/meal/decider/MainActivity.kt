package meal.decider

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.room.Room
import kotlinx.coroutines.*
import meal.decider.Database.CuisineDatabase
import meal.decider.Database.RoomInteractions
import meal.decider.ui.theme.MealDeciderTheme
import java.io.*

@SuppressLint("StaticFieldLeak")
private lateinit var activity: Activity
@SuppressLint("StaticFieldLeak")
private lateinit var activityContext : Context
@SuppressLint("StaticFieldLeak")
private lateinit var appContext : Context
@SuppressLint("StaticFieldLeak")
private lateinit var appViewModel : AppViewModel
private lateinit var cuisineDatabase: CuisineDatabase.AppDatabase
@SuppressLint("StaticFieldLeak")
private lateinit var dialogComposables : DialogComposables
@SuppressLint("StaticFieldLeak")
private lateinit var roomInteractions: RoomInteractions
@SuppressLint("StaticFieldLeak")
private lateinit var mapInteractions: MapInteractions
private lateinit var runnables: Runnables
private lateinit var settings: Settings
val ioScope = CoroutineScope(Job() + Dispatchers.IO)
val mainScope = CoroutineScope(Job() + Dispatchers.Main)

//TODO: RolledSquareIndex doesn't update if list goes to zero + adding cuisines.
//TODO: Adding a cuisine from a zero list doesn't update its string for searching.
//TODO: Get App Icon.
//TODO: Test layout on other sized emulated devices
//TODO: Optimize Lazy Lists speed (e.g. scrolling, general speed).
//TODO: Drop down menus should be animated.
//TODO: Sort not retaining on re-app launch (insertion/update stuff)
//TODO: After second sort random, it goes back to A-Z (bubble selection).
//TODO: Work on NextPage retrieval. We are getting its token at top of json response. Invalid request may be due to needing a delay on the request.
//TODO: Restaurant list items sometimes reposition during scroll. Not using staggered grid works but looks worse.

//TODO: Need to be careful about live data and only use it when necessary. It can easily cause chaining recompositions.

//Icons: Krampus_Highjack, drogula, LilaMaeMay,

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity = this@MainActivity
        activityContext = this@MainActivity
        appContext = applicationContext

        appViewModel = AppViewModel()
        cuisineDatabase = Room.databaseBuilder(
            appContext,
            CuisineDatabase.AppDatabase::class.java,
            "cuisine-database"
        ).build()
        roomInteractions = RoomInteractions(cuisineDatabase, appViewModel, activity)

        mapInteractions = MapInteractions(activity, activityContext, appViewModel)
        mapInteractions.fusedLocationListener()

        runnables = Runnables(appViewModel)
        dialogComposables = DialogComposables(appViewModel, cuisineDatabase, activity, mapInteractions, runnables)

        settings = Settings(appViewModel, roomInteractions)

        //Populates SquareValues and DB with default only if empty (i.e. app launched for first time).
        ioScope.launch {
            if (roomInteractions.cuisineDao.getAllCuisines().isEmpty()) {
                roomInteractions.setSquareDatabaseToDefaultStartingValues()
                appViewModel.updateSquareList(appViewModel.starterSquareList())
            } else {
                roomInteractions.populateSquareValuesWithDatabaseValues()
                appViewModel.setFirstSquareToDefaultColorAndBorder()
            }
            if (roomInteractions.restaurantFiltersDao.getAllRestaurantFilters().isEmpty()) {
                roomInteractions.populateRestaurantFiltersWithInitialValues()
            }
            if (roomInteractions.optionsDao.getRollOptions().isEmpty()) {
                roomInteractions.populateRollOptionsWithInitialValues()
            }
            if (roomInteractions.miscOptionsDao.getMiscOptions().isEmpty()) {
                roomInteractions.populateMiscOptionsWithInitialValues()
            }
            roomInteractions.setViewModelRollDelayVariablesFromDatabaseValues()
            roomInteractions.setMiscOptionsFromDatabaseValues()

            val restaurantFilters = roomInteractions.getRestaurantFilters()[0]
            appViewModel.setLocalRestaurantFilterValues(
                restaurantFilters.distance,
                restaurantFilters.rating,
                restaurantFilters.price.toInt(),
                restaurantFilters.openNow
            )

            appViewModel.updateSelectedCuisineSquare(appViewModel.getSquareList[0])
            appViewModel.updateCuisineStringUriAndHasChangedBoolean(appViewModel.getSelectedCuisineSquare.name + " Food ")

            appViewModel.updateColorTheme(roomInteractions.retrieveColorThemeFromSharedPref())
//            appViewModel.updateColorSettingsSelectionList()
        }

        val boardComposables = BoardComposables(
            appViewModel,
            cuisineDatabase,
            activity,
            roomInteractions,
            mapInteractions,
            runnables
        )

        setContent {
            MealDeciderTheme {
                val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()
                val settingsDialogVisibility =
                    appViewModel.settingsDialogVisibility.collectAsStateWithLifecycle()
                val optionsMenuVisibility =
                    appViewModel.optionsMenuVisibility.collectAsStateWithLifecycle()

                MainSurface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ) {
                    //Removed the encompassing Column here, so Box children can overlay.
                    boardComposables.GlobalUi()

                    if (optionsMenuVisibility.value) {
                        AnimatedTransitionVoid(
                            modifier = Modifier
                                .fillMaxSize(),
                            backHandler = {
                                appViewModel.updateOptionsMenuVisibility(false)
                            }) {
                            settings.OptionsDialogUi()
                        }
                    }

                    if (settingsDialogVisibility.value.colors) {
                        AnimatedTransitionVoid(
                            modifier = Modifier
                                .fillMaxSize(),
                            backHandler = {
                                appViewModel.updateSettingsDialogVisibility(
                                    speeds = false,
                                    colors = false,
                                    sounds = false
                                )
                            }) {
                            settings.ColorsSettingDialog()
                        }
                    }
                    if (settingsDialogVisibility.value.speeds) {
                        AnimatedTransitionVoid(
                            modifier = Modifier
                                .fillMaxSize(),
                            backHandler = {
                                appViewModel.updateSettingsDialogVisibility(
                                    speeds = false,
                                    colors = false,
                                    sounds = false
                                )

                            }) {
                            settings.SpeedSettingsDialog()
                        }
                    }
                    if (settingsDialogVisibility.value.sounds) {
                        AnimatedTransitionVoid(
                            modifier = Modifier
                                .fillMaxSize(),
                            backHandler = {
                                appViewModel.updateSettingsDialogVisibility(
                                    speeds = false,
                                    colors = false,
                                    sounds = false
                                )
                                appViewModel.updateOptionsMenuVisibility(true)
                            }) {
                        }
                    }

                }
            }
//        }
        }
    }

    @Composable
    fun MainSurface(
        modifier: Modifier = Modifier,
        color: Color,
        content: @Composable () -> Unit,
    ) {
        Surface(
            modifier = modifier,
            color = color
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                content()
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun GreetingPreview() {
        MealDeciderTheme {
        }
    }
}