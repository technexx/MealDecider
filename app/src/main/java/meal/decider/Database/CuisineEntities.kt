package meal.decider.Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//Tablename is what "SELECT * FROM <xxx> refers to".
@Entity (tableName = "cuisine")
data class Cuisines(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "cuisine_name") val name: String?,
    @ColumnInfo(name = "cuisine_color") val color: Int?
)