package meal.decider.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Query("SELECT * FROM cuisine")
    fun getAll(): List<Cuisine>

    @Query("SELECT * FROM cuisine WHERE uid IN (:userIds)")
    fun loadAllByIds(userIds: IntArray): List<Cuisine>

    @Query("SELECT * FROM cuisine WHERE cuisine_name LIKE :name AND " +
            "cuisine_color LIKE :color LIMIT 1")
    fun findByName(name: String, color: Int): Cuisine

    @Insert
    fun insertAll(vararg cuisine: Cuisine)

    @Delete
    fun delete(cuisine: Cuisine)
}
