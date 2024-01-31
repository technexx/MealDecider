package meal.decider.Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity (tableName = "cuisine", indices = [Index(value = ["cuisine_name"], unique = true)])
data class Cuisines(
    @PrimaryKey (autoGenerate = false) val uid: Int? = 0,
    @ColumnInfo(name = "cuisine_name") val name: String?,
    @ColumnInfo(name = "cuisine_color") val color: Int?
)

@Entity (tableName = "restaurant_filters", indices = [Index(value = ["restaurant_filter"], unique = true)])
data class RestaurantFilters(
    @PrimaryKey (autoGenerate = false) val uid: Int? = 0,
    @ColumnInfo(name = "distance") val distance: Double?,
    @ColumnInfo(name = "rating") val rating: Double?,
    @ColumnInfo(name = "price") val price: Double?
)