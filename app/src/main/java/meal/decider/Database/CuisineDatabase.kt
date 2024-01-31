package meal.decider.Database

import androidx.room.Database
import androidx.room.RoomDatabase

class CuisineDatabase {
    @Database(entities = [Cuisines::class, RestaurantFilters::class], version = 1)
    abstract class AppDatabase : RoomDatabase() {
        abstract fun cuisineDao(): CuisineDao
        abstract fun restaurantFiltersDao(): RestaurantFiltersDao
    }
}

