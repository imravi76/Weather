package com.revengeos.simpleweather.util

import java.util.*
import kotlin.math.round

class LocaleUtils {
    companion object {
        var Imperial: LocaleUtils = LocaleUtils()
        var Metric: LocaleUtils = LocaleUtils()
        val default: LocaleUtils
            get() = getFrom(Locale.getDefault())

        fun getFrom(locale: Locale): LocaleUtils {
            val countryCode = locale.country
            if ("US" == countryCode) return Imperial // USA
            if ("LR" == countryCode) return Imperial // Liberia
            return if ("MM" == countryCode) Imperial else Metric // Myanmar
        }

        fun kelvinToCelsius(degrees : Float) : Float {
            return degrees - 273.15f
        }

        fun kelvinToFahrenheit(degrees : Float) : Float {
            return (degrees - 273.15f) * 1.8f + 32.0f
        }

        fun getFormattedTemperature(degrees : Float) : String {
            return if (default == Imperial) {
                "${"%.1f".format(kelvinToFahrenheit(degrees))} °F"
            } else {
                "${"%.1f".format(kelvinToCelsius(degrees))} °C"
            }
        }
    }
}
