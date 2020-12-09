package com.revengeos.weather.util

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.floor
import kotlin.math.roundToInt

class WeatherUtils {
    companion object {
        val directions = arrayOf("N", "NNE", "NE", "ENE", "E", "ESE", "SE", "SSE", "S", "SSW", "SW", "WSW", "W", "WNW", "NW", "NNW")

        var imperial: WeatherUtils = WeatherUtils()
        var metric: WeatherUtils = WeatherUtils()
        val default: WeatherUtils
            get() = getFrom(Locale.getDefault())

        fun getFrom(locale: Locale): WeatherUtils {
            val countryCode = locale.country
            if ("US" == countryCode) return imperial // USA
            if ("LR" == countryCode) return imperial // Liberia
            return if ("MM" == countryCode) imperial else metric // Myanmar
        }

        fun kelvinToCelsius(degrees: Float) : Float {
            return degrees - 273.15f
        }

        fun kelvinToFahrenheit(degrees: Float) : Float {
            return (degrees - 273.15f) * 1.8f + 32.0f
        }

        fun getFormattedTemperature(degrees: Float) : String {
            return if (default == imperial) {
                "${"%.1f".format(kelvinToFahrenheit(degrees))} °F"
            } else {
                "${"%.1f".format(kelvinToCelsius(degrees))} °C"
            }
        }

        fun getTimeFromEpoch(epoch: Long, shiftSeconds: Int) : String {
            val sdf = SimpleDateFormat("HH:mm")
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val time = Date((epoch + shiftSeconds) * 1000)
            return sdf.format(time)
        }

        fun getWindDirectionString(degrees: Int) : String {
            val value = floor((degrees / 22.5) + 0.5)
            return directions[(value % 16).roundToInt()]
        }

        fun convertMetersToMiles(meters: Float) : Float {
            return meters * 0.000621371f
        }

        fun truncateFloatToString(number : Float, decimals : Int) : String {
            val shouldTruncate = ((number - number.toInt()) * 10).roundToInt() == 0
            return if (shouldTruncate) "${number.toInt()}" else "${"%.${decimals}f".format(number)}"
        }

        fun getFormattedDistance(meters: Float) : String {
            val distance = if (default == imperial) convertMetersToMiles(meters) else meters / 1000
            val unit = if (default == imperial) "miles" else "km"
            return "${truncateFloatToString(distance, 1)} $unit"
        }

        fun metersPerSecondsToMilesPerHour(metersPerSecond : Float) : Float {
            return metersPerSecond * 2.2369362920544f
        }

        fun getFormattedSpeed(metersPerSecond : Float) : String {
            return if (default == imperial) {
                "${truncateFloatToString(metersPerSecondsToMilesPerHour(metersPerSecond), 1)} mph"
            } else {
                "${truncateFloatToString(metersPerSecond, 1)} m/s"
            }
        }
    }
}
