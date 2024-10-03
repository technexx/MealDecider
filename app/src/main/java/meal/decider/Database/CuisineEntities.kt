package meal.decider.Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity (tableName = "cuisine", indices = [Index(value = ["cuisine_name"], unique = false)])
data class Cuisines(
    @PrimaryKey (autoGenerate = false) val uid: Int? = 0,
    @ColumnInfo(name = "cuisine_name") val name: String?,
    @ColumnInfo(name = "cuisine_color") val color: Int?
)

@Entity (tableName = "restaurant_filters")
data class RestaurantFilters(
    @PrimaryKey (autoGenerate = false) val uid: Int? = 0,
    @ColumnInfo(name = "distance") val distance: Double,
    @ColumnInfo(name = "rating") val rating: Double,
    @ColumnInfo(name = "price") val price: Double
)

@Entity (tableName = "options")
data class RollOptions(
    @PrimaryKey (autoGenerate = false) val uid: Int? = 0,
    @ColumnInfo(name="cuisine_roll_duration_setting") val cuisineRollDurationSetting: Long,
    @ColumnInfo(name="cuisine_roll_delay_setting") val cuisineRollDelaySetting: Long,
    @ColumnInfo(name="restaurant_roll_duration_setting") val restaurantRollDurationSetting: Long,
    @ColumnInfo(name="restaurant_roll_delay_setting") val restaurantRollDelaySetting: Long,
    )

@Entity (tableName = "misc_options")
data class MiscOptions(
    @PrimaryKey (autoGenerate = false) val uid: Int? = 0,
    @ColumnInfo(name="restaurant_auto_scroll") val restaurantAutoScroll: Boolean
)