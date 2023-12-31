package meal.decider.Database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import meal.decider.SquareValues

class RoomInteractions (cuisineDatabase: CuisineDatabase.AppDatabase) {
    private val cuisineDao = cuisineDatabase.cuisineDao()

//    fun getSingleCuisine(name: String): Cuisines { return cuisineDao.getCuisineByName(name) }
//    fun getAllCuisines(): List<Cuisines> { return cuisineDao.getAllCuisines() }
//    fun insertCuisine(cuisine: Cuisines) { return cuisineDao.insertCuisine(cuisine)}
//    fun deleteCuisine(cuisine: Cuisines) { cuisineDao.deleteCuisine(cuisine) }

    suspend fun test(function: () -> Unit) {
        function()
    }

    suspend fun insertCuisine(cuisine: SquareValues) =
        withContext(Dispatchers.IO) {
            cuisineDao.insertCuisine(cuisine)
            println("insert is ${cuisineDao.getAllCuisines()}")
        }

    suspend fun getAllCuisines() =
        withContext(Dispatchers.IO) {
            cuisineDao.getAllCuisines()
            println("get all is ${cuisineDao.getAllCuisines()}")
        }
}