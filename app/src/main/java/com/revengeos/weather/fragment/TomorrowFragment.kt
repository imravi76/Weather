package com.revengeos.weather.fragment

import android.content.Context
import com.revengeos.weather.R
import com.revengeos.weather.response.Daily
import com.revengeos.weather.util.WeatherUtils
import com.revengeos.weathericons.WeatherIconsHelper

class TomorrowFragment : DayWeatherFragment() {

    val TAG = javaClass.toString()

    override fun getWeatherPageTitle(context : Context) : String? {
        return context.getString(R.string.tomorrow_title)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                TomorrowFragment().apply {
                }
    }
}