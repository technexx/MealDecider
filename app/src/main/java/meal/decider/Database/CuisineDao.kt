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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCuisine(vararg cuisine: Cuisines)

    @Query("UPDATE cuisine SET cuisine_name = :newName WHERE cuisine_name = :oldName")
    fun updateCuisineName(oldName: String, newName: String)

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

    //Not working.
    @Query("UPDATE restaurant_filters SET distance = :newDistance, rating =:newRating, price = :newPrice")
    fun updateFilters(newDistance: Double, newRating: Double, newPrice: Double)


}