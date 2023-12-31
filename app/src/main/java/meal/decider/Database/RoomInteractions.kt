package meal.decider.Database

class RoomInteractions (cuisineDatabase: CuisineDatabase.AppDatabase) {

    private val cuisineDao = cuisineDatabase.cuisineDao()
    val cuisines: List<Cuisines> = cuisineDao.getAll()

}