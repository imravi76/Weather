package com.revengeos.weather

data class WeatherHeaderData(val temp : Float, val tempFeelsLike : Float, val location : String,
                             val weatherIcon : String, val weatherId : Int)