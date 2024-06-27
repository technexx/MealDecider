package meal.decider

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
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
private lateinit var roomInteractions: RoomInteractions
@SuppressLint("StaticFieldLeak")
private lateinit var mapInteractions: MapInteractions
private lateinit var runnables: Runnables
private lateinit var settings: Settings

val ioScope = CoroutineScope(Job() + Dispatchers.IO)
val mainScope = CoroutineScope(Job() + Dispatchers.Main)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity = this@MainActivity
        activityContext = this@MainActivity
        appContext = applicationContext

        appViewModel = AppViewModel()
        cuisineDatabase = Room.databaseBuilder(appContext, CuisineDatabase.AppDatabase::class.java, "cuisine-database").build()
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
            roomInteractions.setViewModelRollDelayVariablesFromDatabaseValues()

            val restaurantFilters = roomInteractions.getRestaurantFilters()[0]
            appViewModel.setLocalRestaurantFilterValues(milesToMeters(restaurantFilters.distance), restaurantFilters.rating, restaurantFilters.price.toInt())

            appViewModel.updateSelectedCuisineSquare(appViewModel.getSquareList[0])
            appViewModel.updateCuisineStringUriAndHasChangedBoolean(appViewModel.getselectedCuisineSquare.name + " Food ")

            appViewModel.updateColorTheme(roomInteractions.retrieveColorThemeFromSharedPref())
        }

        val boardComposables = BoardComposables(appViewModel, cuisineDatabase, activity, roomInteractions, mapInteractions, runnables)

        setContent {
            MealDeciderTheme {
                val colorTheme = appViewModel.colorTheme.collectAsStateWithLifecycle()

                MainSurface (
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Black
                ){
                    //Removed the encompassing Column here, so Box children can overlay.
                    val optionsMode = appViewModel.optionsMode.collectAsStateWithLifecycle()

                    BackHandler {
                        if (optionsMode.value) {
                            appViewModel.updateOptionsMode(false)
                        }
                    }

                    boardComposables.GlobalUi()

                    //TODO: Currently trying to transition entirely new composable (options) over main board.

                    if (optionsMode.value) {
                        AnimatedTransitionVoid (
                            modifier = Modifier
                                .fillMaxSize()){
                            showLog("test", "settings animating in")
                            settings.OptionsDialogUi()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MainSurface(
    modifier: Modifier = Modifier,
    color: Color,
    content: @Composable () -> Unit,
) {
    Surface (
        modifier = modifier,
        color = color) {
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