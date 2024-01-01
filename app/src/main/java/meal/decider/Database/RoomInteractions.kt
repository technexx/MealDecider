package meal.decider.Database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import meal.decider.AppViewModel

class RoomInteractions (private val appViewModel: AppViewModel, cuisineDatabase: CuisineDatabase.AppDatabase) {
    private val cuisineDao = cuisineDatabase.cuisineDao()

    suspend fun insertCuisine(name: String, color: Int) =
        withContext(Dispatchers.IO) {
            cuisineDao.insertCuisine(Cuisines(null, name, color))
            println("insert is ${cuisineDao.getAllCuisines()}")
        }

    suspend fun getAllCuisines() =
        withContext(Dispatchers.IO) {
            cuisineDao.getAllCuisines()
            println("get all is ${cuisineDao.getAllCuisines()}")
        }
}