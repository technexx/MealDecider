package meal.decider

import java.math.BigDecimal
import java.math.RoundingMode

fun floatArrayMetersToMiles(meters: FloatArray): Double {
    val miles = (meters[0] * .00062137)
    val roundedMiles = BigDecimal(miles).setScale(1, RoundingMode.DOWN)
    return roundedMiles.toDouble()
}

fun doubleMetersToMiles(meters: Double): Double { return meters * .00062137}

fun milesToMeters(miles: Int): Int { return miles*1609}

fun ratingToStarValue(rating: Double?): Double {
    var valueToReturn = 0.0

    if (rating != null) {
        val ratingRoundedDown = rating.toInt()
        val remainder = rating - ratingRoundedDown

        for (i in 1..ratingRoundedDown) {
            valueToReturn += 1.0
        }
        if (remainder >.2 && remainder <.8) {
            valueToReturn += 0.5
        }
        if (remainder >= .8) {
            valueToReturn += 1.0
        }
    }
    return valueToReturn
}