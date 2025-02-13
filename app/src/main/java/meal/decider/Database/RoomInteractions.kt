package meal.decider.Database

import android.app.Activity
import android.content.Context
import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import meal.decider.AppViewModel
import meal.decider.ColorTheme
import meal.decider.SquareValues
import meal.decider.Theme
import meal.decider.milesToMeters

class RoomInteractions (cuisineDatabase: CuisineDatabase.AppDatabase, private val appViewModel: AppViewModel, private val activity: Activity) {
    private val ioScope = CoroutineScope(Job() + Dispatchers.IO)
    val cuisineDao = cuisineDatabase.cuisineDao()
    val restaurantFiltersDao = cuisineDatabase.restaurantFiltersDao()
    val optionsDao = cuisineDatabase.optionsDao()
    val miscOptionsDao = cuisineDatabase.miscOptions()

    private val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)

    fun setSquareDatabaseToDefaultStartingValues() {
        ioScope.launch {
            deleteAllCuisines()
            populateDatabaseWithInitialCuisines()
        }
    }

    private suspend fun populateDatabaseWithInitialCuisines() {
        for (i in appViewModel.starterSquareList().indices) {
            if (i==0) insertCuisine(appViewModel.starterSquareList()[i].name, Theme.themeColorsList[0].selectedCuisineSquare) else
                insertCuisine(appViewModel.starterSquareList()[i].name, Theme.themeColorsList[0].cuisineSquares)
        }
    }

    suspend fun populateSquareValuesWithDatabaseValues() {
        withContext(Dispatchers.IO) {
            val listOfDatabaseCuisines = cuisineDao.getAllCuisines()
            val squareList = SnapshotStateList<SquareValues>()
            for (i in listOfDatabaseCuisines) {
                squareList.add(SquareValues(i.name!!, i.color!!))
            }
            appViewModel.updateSquareList(squareList)
        }
    }

    private suspend fun insertCuisine(name: String, color: Int) =
        withContext(Dispatchers.IO) {
            cuisineDao.insertCuisine(Cuisines(null, name, color))
        }

    suspend fun insertMultipleCuisines(list: List<String>) {
        withContext(Dispatchers.IO) {
            for (i in list) {
                cuisineDao.insertCuisine(Cuisines(null, i, appViewModel.getColorTheme.cuisineSquares))
            }
        }
    }

    //This will rely on lists being same length, so add a check exception.
    //With unique keys set to true in Cuisine Entity, we can't update database values one by one, because that will result in temporary identical entries.

    //List being passed into update is fine, but retrieved list is the repeats.
    suspend fun updateCuisines() {
        withContext(Dispatchers.IO) {
            val newList: MutableList<Cuisines> = mutableListOf()
            for (i in appViewModel.getSquareList) {
                newList.add(Cuisines(null, i.name, i.color))
            }
            cuisineDao.updateCuisineList(newList)

        }
    }

    suspend fun deleteMultipleCuisines() {
        withContext(Dispatchers.IO) {
            val squareList = appViewModel.getSquareList
            for (i in squareList) {
                if (i.isHighlighted) {
                    cuisineDao.deleteCuisineFromName(i.name)
                }
            }
        }
    }

    suspend fun deleteAllCuisines() {
        withContext(Dispatchers.IO) {
            cuisineDao.deleteAllCuisines(cuisineDao.getAllCuisines())
        }
    }

    fun populateRestaurantFiltersWithInitialValues() {
        val restaurantFilters = RestaurantFilters(null, milesToMeters(5.0), 3.0, 4.0, false)
        restaurantFiltersDao.insertRestaurantFilters(restaurantFilters)
    }

    suspend fun getRestaurantFilters(): List<RestaurantFilters> {
        val restaurantFilters: List<RestaurantFilters>
        withContext(Dispatchers.IO) {
            restaurantFilters = restaurantFiltersDao.getAllRestaurantFilters()
        }
        return restaurantFilters
    }

    suspend fun updateRestaurantFilters(distance: Double, rating: Double, price: Int, isOpen: Boolean) {
        withContext(Dispatchers.IO) {
            restaurantFiltersDao.updateFilters(distance, rating, price, isOpen)
        }
    }

    fun populateRollOptionsWithInitialValues() {
        val rollOptions = RollOptions(null, 5, 5, 5, 5)
        optionsDao.insertRollOptions(rollOptions)
    }


    suspend fun getRollOptions(): List<RollOptions> {
        val rollOptions: List<RollOptions>
        withContext(Dispatchers.IO) {
            rollOptions = optionsDao.getRollOptions()
        }
        return rollOptions
    }

    suspend fun updateRollOptions(cuisineDuration: Long, cuisineDelay: Long, restaurantDuration: Long, restaurantDelay: Long) {
        withContext(Dispatchers.IO) {
            optionsDao.updateRollOptions(cuisineDuration, cuisineDelay, restaurantDuration, restaurantDelay)
        }
    }

    suspend fun updateAutoScroll(isOn: Boolean) {
        withContext(Dispatchers.IO) {
            miscOptionsDao.updateRestaurantAutoScroll(isOn)
        }
    }

    suspend fun setViewModelRollDelayVariablesFromDatabaseValues() {
        withContext(Dispatchers.IO) {
            val rollOptions = getRollOptions()
            appViewModel.cuisineRollDurationSetting = rollOptions[0].cuisineRollDurationSetting
            appViewModel.cuisineRollSpeedSetting = rollOptions[0].cuisineRollDelaySetting
            appViewModel.restaurantRollDurationSetting = rollOptions[0].restaurantRollDurationSetting
            appViewModel.restaurantRollSpeedSetting = rollOptions[0].restaurantRollDelaySetting
        }
    }

    suspend fun setMiscOptionsFromDatabaseValues() {
        withContext(Dispatchers.IO) {
            val miscOptions = getMiscOptions()
            appViewModel.restaurantAutoScroll = miscOptions[0].restaurantAutoScroll
        }
    }

    suspend fun getMiscOptions(): List<MiscOptions> {
        val miscOptions: List<MiscOptions>
        withContext(Dispatchers.IO) {
            miscOptions = miscOptionsDao.getMiscOptions()
        }
        return miscOptions
    }

    fun populateMiscOptionsWithInitialValues() {
        val miscOptions = MiscOptions(null, true)
        miscOptionsDao.insertMiscSettings(miscOptions)
    }

    fun saveColorThemeToSharedPref(colorTheme: ColorTheme) {
        var colorThemeString = ""
        if (colorTheme == Theme.themeColorsList[0]) colorThemeString = "light"
        if (colorTheme == Theme.themeColorsList[1]) colorThemeString = "dark"

        sharedPref.edit().putString("theme", colorThemeString).apply()
    }

    fun retrieveColorThemeFromSharedPref(): ColorTheme {
        val colorThemeString = sharedPref.getString("theme", "dark")
        var colorTheme = Theme.themeColorsList[0]
        if (colorThemeString == "light") colorTheme = Theme.themeColorsList[0]
        if (colorThemeString == "dark") colorTheme = Theme.themeColorsList[1]

        return colorTheme
    }

    fun retrieveColorThemeIndexFromSharedPref(): Int {
        var index = 0
        val colorThemeString = sharedPref.getString("theme", "light")
        if (colorThemeString == "light") index = 0
        if (colorThemeString == "dark") index = 1

        return index
    }
}