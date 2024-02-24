package meal.decider

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.platform.LocalConfiguration
import java.math.BigDecimal
import java.math.RoundingMode

fun doubleToTwoDecimals(double: Double?): Double {
    return BigDecimal(double!!).setScale(2, RoundingMode.DOWN).toDouble()
}

fun floatArrayToDouble(meters: FloatArray): Double {
    val roundedMiles = BigDecimal(meters[0].toDouble()).setScale(1, RoundingMode.DOWN)
    return roundedMiles.toDouble()
}

fun doubleMetersToMiles(meters: Double): Double {
    return BigDecimal(meters * .00062137).setScale(1, RoundingMode.DOWN).toDouble()
}

fun milesToMeters(miles: Double): Double { return miles*1609 }

fun priceToDollarSigns(price: Int?): String {
    var stringToReturn = ""
    if (price != null) {
        for (i in 1..price) {
            stringToReturn += "$"
        }
    }
    return stringToReturn
}

fun filterList(list: List<String>, searchString: String) : List<String> {
    //If search string equals the first X characters typed, filter list with just those matching entries. If search string is empty, display full list.
    return if (searchString != "") {
        list.filter { a -> a.substring(0, searchString.length).equals(searchString, true) }
    } else {
        list
    }
}

fun foodRestrictionsString(list: SnapshotStateList<RestrictionsValues>): String {
    var stringList = ""
    for (i in list) {
        if (i.selected) {
            stringList = stringList+ "+" + (i.name)
        }
    }
    return stringList
}

fun rollDurationSettingToMillis(setting: Long): Long {
    return setting * 1000
}

//TODO: Speed should increase as roll continues.
fun rollSpeedSettingToMillis(setting: Long, duration: Long): Long {
    val durationInSeconds = duration/1000
    return (1000 / setting) / durationInSeconds
}

@Composable
fun screenHeightPct(pct: Double) : Double {
    val configuration = LocalConfiguration.current
    return configuration.screenHeightDp.toDouble() * pct
}

fun showLog(name: String, text: String) {
    Log.i(name, text)
}