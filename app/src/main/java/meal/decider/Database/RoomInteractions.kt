package meal.decider.Database

import androidx.compose.runtime.snapshots.SnapshotStateList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import meal.decider.AppViewModel
import meal.decider.SquareValues
import meal.decider.defaultSquareColor

class RoomInteractions (cuisineDatabase: CuisineDatabase.AppDatabase, private val appViewModel: AppViewModel) {
    val cuisineDao = cuisineDatabase.cuisineDao()

    suspend fun populateDatabaseWithInitialCuisines() {
        for (i in appViewModel.starterSquareList()) {
            insertCuisine(i.name, defaultSquareColor)
        }
        println("get all is ${cuisineDao.getAllCuisines()}")
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
            println("insert is ${cuisineDao.getAllCuisines()}")
        }

    suspend fun deleteCuisines() {
        withContext(Dispatchers.IO) {
            val listOfNames = appViewModel.getListOfSquaresToEdit
            for (i in listOfNames) {
                cuisineDao.deleteCuisineFromName(i.name)
            }
        }
    }

    suspend fun getAllCuisines() =
        withContext(Dispatchers.IO) {
            cuisineDao.getAllCuisines()
        }
}