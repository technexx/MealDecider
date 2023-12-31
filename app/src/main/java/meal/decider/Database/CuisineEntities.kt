package meal.decider.Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

class Cuisines {
    @Entity
    data class Cuisine(
        @PrimaryKey val uid: Int,
        @ColumnInfo(name = "cuisine_name") val name: String?,
        @ColumnInfo(name = "cuisine_color") val color: Int?
    )
}

