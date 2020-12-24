package com.revengeos.weather.fragment

import com.revengeos.weather.WeatherGridData
import com.revengeos.weather.WeatherHeaderData
import com.revengeos.weather.forecast.DailyAdapter
import com.revengeos.weather.forecast.HourlyAdapter

data class DayWeatherFragmentData(val todayWeatherHeaderData: WeatherHeaderData,
                                  val todayWeatherGridData: WeatherGridData,
                                  val todayHourlyAdapter: HourlyAdapter,
                                  val tomorrowWeatherHeaderData: WeatherHeaderData,
                                  val tomorrowWeatherGridData: WeatherGridData,
                                  val nextDaysAdapter: DailyAdapter)