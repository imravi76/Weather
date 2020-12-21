package com.revengeos.weather.fragment

import com.revengeos.weather.WeatherGridData
import com.revengeos.weather.WeatherHeaderData
import com.revengeos.weather.forecast.DailyAdapter
import com.revengeos.weather.forecast.HourlyAdapter

data class DayWeatherFragmentData(val weatherHeaderData: WeatherHeaderData,
                                  val weatherGridData: WeatherGridData,
                                  val hourlyAdapter: HourlyAdapter,
                                  val nextDaysAdapter: DailyAdapter)