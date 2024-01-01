package meal.decider.Database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import meal.decider.AppViewModel
import meal.decider.defaultSquareColor

class RoomInteractions (cuisineDatabase: CuisineDatabase.AppDatabase, private val appViewModel: AppViewModel) {
    val cuisineDao = cuisineDatabase.cuisineDao()

    suspend fun populateDatabaseWithInitialCuisines() {
        for (i in appViewModel.starterSquareList()) {
            insertCuisine(i.name, defaultSquareColor)
        }
        println("get all is ${cuisineDao.getAllCuisines()}")
    }

    suspend fun insertCuisine(name: String, color: Int) =
        withContext(Dispatchers.IO) {
            cuisineDao.insertCuisine(Cuisines(null, name, color))
            println("insert is ${cuisineDao.getAllCuisines()}")
        }

    suspend fun updateCuisine() {
        withContext(Dispatchers.IO) {
//            val cuisineName = cuisineDao.getCuisineByName(appViewModel.getSquareList[index].name)
            cuisineDao.update("American", "Test")
            println("insert is ${cuisineDao.getAllCuisines()}")

        }
    }

    suspend fun getAllCuisines() =
        withContext(Dispatchers.IO) {
            cuisineDao.getAllCuisines()
            println("get all is ${cuisineDao.getAllCuisines()}")
        }
}