package meal.decider.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface CuisineDao {
    @Query("SELECT * FROM cuisine")
    fun getAllCuisines(): List<Cuisines>

    @Query("SELECT * FROM cuisine WHERE uid IN (:userIds)")
    fun getAllCuisinesById(userIds: IntArray): List<Cuisines>

    @Query("SELECT * FROM cuisine WHERE cuisine_name LIKE :name LIMIT 1")
    fun getCuisineByName(name: String): Cuisines

    @Query("SELECT * FROM cuisine WHERE cuisine_name LIKE :name AND " + "cuisine_color LIKE :color LIMIT 1")
    fun getCuisineByNameAndColor(name: String, color: Int): Cuisines

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCuisine(vararg cuisine: Cuisines)

    @Query("UPDATE cuisine SET cuisine_name = :name, cuisine_color = :color")
    fun updateCuisine(name: String, color: Int)

    @Query("DELETE FROM cuisine WHERE cuisine_name = :name")
    fun deleteCuisineFromName(name: String)

    @Delete
    fun deleteCuisine(cuisine: Cuisines)

    @Delete
    fun deleteAllCuisines(list: List<Cuisines>)
}

@Dao
interface RestaurantFiltersDao {
    @Query("SELECT * from restaurant_filters")
    fun getAllRestaurantFilters(): List<RestaurantFilters>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertRestaurantFilters(vararg restaurantFilters: RestaurantFilters)

    @Query("UPDATE restaurant_filters SET distance = :newDistance, rating =:newRating, price = :newPrice")
    fun updateFilters(newDistance: Double, newRating: Double, newPrice: Double)
}

//Test this. Getting everything from options entity but only returning from RollOptions data class.
@Dao interface OptionsDao {
    @Query("SELECT * from options")
    fun getRollOptions(): List<RollOptions>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertRollOptions(vararg rollOptions: RollOptions)

    @Query("UPDATE options SET cuisine_roll_duration = :cuisineRollDuration, cuisine_roll_delay = :cuisineRollDelay, restaurant_roll_duration = :restaurantRollDuration, restaurant_roll_delay = :restaurantRollDelay")
    fun updateRollOptions(cuisineRollDuration: Long, cuisineRollDelay: Long, restaurantRollDuration: Long, restaurantRollDelay: Long)
}