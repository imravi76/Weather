package com.revengeos.weather.util

import java.text.SimpleDateFormat
import java.util.*

class WeatherUtils {
    companion object {
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

        fun kelvinToCelsius(degrees : Float) : Float {
            return degrees - 273.15f
        }

        fun kelvinToFahrenheit(degrees : Float) : Float {
            return (degrees - 273.15f) * 1.8f + 32.0f
        }

        fun getFormattedTemperature(degrees : Float) : String {
            return if (default == imperial) {
                "${"%.1f".format(kelvinToFahrenheit(degrees))} °F"
            } else {
                "${"%.1f".format(kelvinToCelsius(degrees))} °C"
            }
        }

        fun getTimeFromEpoch(epoch : Long, shiftSeconds : Int) : String {
            val sdf = SimpleDateFormat("HH:mm")
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val time = Date((epoch + shiftSeconds) * 1000)
            return sdf.format(time)
        }
    }
}
