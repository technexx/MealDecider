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

fun rollDurationSettingToMillis(durationSetting: Long): Long {
    return durationSetting * 1000
}

fun rollSpeedSettingToMillis(duration: Long, speedSetting: Long): Long {
    val delaySettingDivisor = 6 + (speedSetting * 4)
    val additionalDelayBuffer = (100 - (10 * speedSetting))
    val valueToReturn = (duration / delaySettingDivisor) + additionalDelayBuffer

    return valueToReturn
}

fun durationDecreaseIteration(duration: Long): Long {
    return (duration.toDouble() * 0.9).toLong()
}

@Composable
fun screenHeightPct(pct: Double) : Double {
    val configuration = LocalConfiguration.current
    return configuration.screenHeightDp.toDouble() * pct
}

fun showLog(name: String, text: String) {
    Log.i(name, text)
}