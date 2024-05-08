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

fun modifierDelay(duration: Long, speedSetting: Long): Long {
    showLog("test", "duration is $duration")
    //Longest delay. Will range from 300 (duration 1) to 1000 (duration 10). 10 ticks each roll.
    var modifiedDelay = duration / 10
    //Reduces delay based on speed setting, with 1 not changing it at all from longest.
    modifiedDelay /= (speedSetting - (speedSetting * 0.5).toLong())
    return modifiedDelay
}

fun rollDurationSettingToMillis(durationSetting: Long): Long {
    return 3000 + (durationSetting * 1000 * 0.7).toLong()
}

@Composable
fun screenHeightPct(pct: Double) : Double {
    val configuration = LocalConfiguration.current
    return configuration.screenHeightDp.toDouble() * pct
}

fun showLog(name: String, text: String) {
    Log.i(name, text)
}