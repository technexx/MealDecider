package meal.decider.Database

import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import meal.decider.AppViewModel
import meal.decider.SquareValues
import meal.decider.chosenSquareColor
import meal.decider.defaultSquareColor
import meal.decider.showLog

class RoomInteractions (cuisineDatabase: CuisineDatabase.AppDatabase, private val appViewModel: AppViewModel) {
    private val ioScope = CoroutineScope(Job() + Dispatchers.IO)
    val cuisineDao = cuisineDatabase.cuisineDao()
    val restaurantFiltersDao = cuisineDatabase.restaurantFiltersDao()

    suspend fun populateDatabaseWithInitialCuisines() {
        for (i in appViewModel.starterSquareList().indices) {
            if (i==0) insertCuisine(appViewModel.starterSquareList()[i].name, chosenSquareColor) else
                insertCuisine(appViewModel.starterSquareList()[i].name, defaultSquareColor)
        }
    }

    suspend fun populateSquareValuesWithDatabaseValues() {
        withContext(Dispatchers.IO) {
            val listOfDatabaseCuisines = cuisineDao.getAllCuisines()
            val squareList = SnapshotStateList<SquareValues>()
            for (i in listOfDatabaseCuisines) {
                squareList.add(SquareValues(i.name!!, i.color!!))
            }

            showLog("test", "square list is ${squareList.toList()}")
            appViewModel.updateSquareList(squareList)
        }
    }

    suspend fun insertCuisine(name: String, color: Int) =
        withContext(Dispatchers.IO) {
            cuisineDao.insertCuisine(Cuisines(null, name, color))
        }

    suspend fun insertMultipleCuisines(list: List<String>) {
        withContext(Dispatchers.IO) {
            for (i in list) {
                cuisineDao.insertCuisine(Cuisines(null, i, defaultSquareColor))
            }
        }
    }

    suspend fun deleteMultipleCuisines() {
        withContext(Dispatchers.IO) {
            val listOfNames = appViewModel.getlistOfCuisineSquaresToEdit
            for (i in listOfNames) {
                cuisineDao.deleteCuisineFromName(i.name)
            }
        }
    }

    suspend fun deleteAllCuisines() {
        withContext(Dispatchers.IO) {
            cuisineDao.deleteAllCuisines(cuisineDao.getAllCuisines())
        }
    }

    fun setSquareValuesAndDatabaseToDefaultStartingValues() {
        ioScope.launch {
            deleteAllCuisines()
            populateDatabaseWithInitialCuisines()
        }
        appViewModel.updateSquareList(appViewModel.starterSquareList())
        appViewModel.updateSelectedCuisineSquare(appViewModel.getSquareList[0])
    }

    suspend fun populateRestaurantFiltersWithInitialValues() {
        val restaurantFilters = RestaurantFilters(null,5.0, 3.0, 1.0)
        restaurantFiltersDao.insertRestaurantFilters(restaurantFilters)
        showLog("test", "filter list from initial pop" +
                " is ${restaurantFiltersDao.getAllRestaurantFilters()}")
    }

    suspend fun updateRestaurantFilters(distance: Double, rating: Double, price: Double) {
        withContext(Dispatchers.IO) {
            restaurantFiltersDao.updateDistance(distance)
            restaurantFiltersDao.updateRating(rating)
            restaurantFiltersDao.updatePrice(price)
            showLog("test", "filter list from dialog is ${restaurantFiltersDao.getAllRestaurantFilters()}")
        }
    }
}