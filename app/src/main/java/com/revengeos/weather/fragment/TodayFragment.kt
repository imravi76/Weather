package com.revengeos.weather.fragment

import android.content.Context
import com.revengeos.weather.R
import com.revengeos.weather.response.current.CurrentWeatherResponse
import com.revengeos.weather.util.WeatherUtils
import com.revengeos.weathericons.WeatherIconsHelper

class TodayFragment : DayWeatherFragment() {

    val TAG = javaClass.toString()

    fun updateCurrentWeather(currentWeatherResponse : CurrentWeatherResponse) {

        val temperature = WeatherUtils.getFormattedTemperature(currentWeatherResponse.main.temp)
        currentTemp.text = temperature
        currentTempEnd.text = temperature
        currentLocation.text = currentWeatherResponse.name
        currentLocationEnd.text = currentWeatherResponse.name
        val feelsLikeText = WeatherUtils.getFeelsLikeFormattedTemp(requireContext(), currentWeatherResponse.main.feels_like)
        currentTempFeelsLike.text = feelsLikeText
        currentTempFeelsLikeEnd.text = feelsLikeText
        currentData.updateData(currentWeatherResponse.sys.sunrise, currentWeatherResponse.sys.sunset, currentWeatherResponse.timezone,
                currentWeatherResponse.main.pressure, currentWeatherResponse.main.humidity, currentWeatherResponse.wind.deg,
                currentWeatherResponse.wind.speed, currentWeatherResponse.visibility, currentWeatherResponse.main.temp_min, currentWeatherResponse.main.temp_max, null)

        val isDay = currentWeatherResponse.weather[0].icon.takeLast(1) == "d"
        val state = WeatherIconsHelper.mapConditionIconToCode(currentWeatherResponse.weather[0].id, isDay)
        currentIcon.setImageResource(WeatherIconsHelper.getDrawable(state, requireContext())!!)
    }

    override fun getWeatherPageTitle(context : Context) : String? {
        return context.getString(R.string.today_title)
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                TodayFragment().apply {
                }
    }
}