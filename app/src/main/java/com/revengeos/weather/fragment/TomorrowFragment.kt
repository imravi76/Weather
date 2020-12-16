package com.revengeos.weather.fragment

import android.content.Context
import com.revengeos.weather.R
import com.revengeos.weather.response.Daily
import com.revengeos.weather.util.WeatherUtils
import com.revengeos.weathericons.WeatherIconsHelper

class TomorrowFragment : DayWeatherFragment() {

    val TAG = javaClass.toString()
    lateinit var locationName: String

    fun updateTomorrowWeather(daily : Daily, timeZone : Int) {

        val temperature = WeatherUtils.getFormattedTemperature(daily.temp.day)
        currentTemp.text = temperature
        currentTempEnd.text = temperature
        currentLocation.text = locationName
        currentLocationEnd.text = locationName
        val feelsLikeText = WeatherUtils.getFeelsLikeFormattedTemp(requireContext(), daily.feels_like.day)
        currentTempFeelsLike.text = feelsLikeText
        currentTempFeelsLikeEnd.text = feelsLikeText
        currentData.updateData(daily.sunrise, daily.sunset, timeZone,
                daily.pressure, daily.humidity, daily.windDeg,
                daily.windSpeed, 0, daily.temp.min, daily.temp.max)

        val isDay = daily.weather[0].icon.takeLast(1) == "d"
        val state = WeatherIconsHelper.mapConditionIconToCode(daily.weather[0].id, isDay)
        currentIcon.setImageResource(WeatherIconsHelper.getDrawable(state, requireContext())!!)
    }

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