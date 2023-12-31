package meal.decider.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import meal.decider.SquareValues

@Dao
interface CuisineDao {
    @Query("SELECT * FROM cuisine")
    fun getAllCuisines(): List<SquareValues>

    @Query("SELECT * FROM cuisine WHERE uid IN (:userIds)")
    fun getAllCuisinesById(userIds: IntArray): List<SquareValues>

    @Query("SELECT * FROM cuisine WHERE cuisine_name LIKE :name LIMIT 1")
    fun getCuisineByName(name: String): SquareValues

    @Query("SELECT * FROM cuisine WHERE cuisine_name LIKE :name AND " +
            "cuisine_color LIKE :color LIMIT 1")
    fun getCuisineByNameAndColor(name: String, color: Int): SquareValues

    @Insert
    fun insertCuisine(vararg cuisine: SquareValues)

    @Delete
    fun deleteCuisine(cuisine: SquareValues)
}