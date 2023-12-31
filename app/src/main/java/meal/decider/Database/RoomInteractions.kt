package meal.decider.Database

class RoomInteractions (cuisineDatabase: CuisineDatabase.AppDatabase) {
    private val cuisineDao = cuisineDatabase.cuisineDao()

    fun getSingleCuisine(name: String): Cuisines { return cuisineDao.getCuisineByName(name) }
    fun getAllCuisines(): List<Cuisines> { return cuisineDao.getAllCuisines() }
    fun insertCuisine(cuisine: Cuisines) { return cuisineDao.insertCuisine(cuisine)}
    fun deleteCuisine(cuisine: Cuisines) { cuisineDao.deleteCuisine(cuisine) }


}