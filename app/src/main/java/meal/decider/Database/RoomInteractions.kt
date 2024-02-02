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

    fun setSquareDatabaseToDefaultStartingValues() {
        ioScope.launch {
            deleteAllCuisines()
            populateDatabaseWithInitialCuisines()
        }
    }

    suspend fun populateRestaurantFiltersWithInitialValues() {
        val restaurantFilters = RestaurantFilters(null,5.0, 3.0, 1.0)
        restaurantFiltersDao.insertRestaurantFilters(restaurantFilters)
    }

    suspend fun getRestaurantFilters(): List<RestaurantFilters> {
        val restaurantFilters: List<RestaurantFilters>
        withContext(Dispatchers.IO) {
            restaurantFilters = restaurantFiltersDao.getAllRestaurantFilters()
        }
        return restaurantFilters
    }

    suspend fun updateRestaurantFilters(distance: Double, rating: Double, price: Double) {
        withContext(Dispatchers.IO) {
            restaurantFiltersDao.updateDistance(distance)
            restaurantFiltersDao.updateRating(rating)
            restaurantFiltersDao.updatePrice(price)
        }
    }
}