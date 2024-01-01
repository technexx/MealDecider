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

    suspend fun updateCuisineName(oldName: String, newName: String) {
        withContext(Dispatchers.IO) {
            cuisineDao.update(oldName, newName)
            for (i in cuisineDao.getAllCuisines()) {
                println(i.name)
            }
        }
    }

    suspend fun getAllCuisines() =
        withContext(Dispatchers.IO) {
            cuisineDao.getAllCuisines()
            println("get all is ${cuisineDao.getAllCuisines()}")
        }
}