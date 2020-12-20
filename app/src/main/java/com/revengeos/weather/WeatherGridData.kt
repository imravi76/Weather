package com.revengeos.weather

data class WeatherGridData(val sunrise: Long, val sunset: Long, val timeZone: Int, val pressure: Int, val humidity: Int, val windDirection: Int,
                           val windSpeed: Float, val visibility: Int?, val minTemp: Float, val maxTemp: Float, val precipitations: Float?)