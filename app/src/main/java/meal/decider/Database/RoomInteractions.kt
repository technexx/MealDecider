package meal.decider.Database

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import meal.decider.AppViewModel

class RoomInteractions (cuisineDatabase: CuisineDatabase.AppDatabase, val appViewModel: AppViewModel) {
    private val cuisineDao = cuisineDatabase.cuisineDao()

    suspend fun test(function: () -> Unit) {
        function()
    }

    //Todo: Insert needs to get name and color from SquareValues object. We're saving it as "cuisine_name" and "cuisine_color" in database. Also need a 3rd ("id") entry.
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

    //(appViewModel.getSquareList[0].name, appViewModel.getSquareList[0].color)
}