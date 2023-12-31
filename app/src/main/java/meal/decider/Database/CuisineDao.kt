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

    @Query("SELECT * FROM cuisine WHERE cuisine_name LIKE :name AND " +
            "cuisine_color LIKE :color LIMIT 1")
    fun getCuisineByNameAndColor(name: String, color: Int): Cuisines

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertCuisine(vararg cuisine: Cuisines)

    @Delete
    fun deleteCuisine(cuisine: Cuisines)
}