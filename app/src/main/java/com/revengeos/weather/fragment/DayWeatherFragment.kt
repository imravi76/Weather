package com.revengeos.weather.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.revengeos.weather.*
import com.revengeos.weather.forecast.HourlyAdapter
import com.revengeos.weather.util.WeatherUtils
import com.revengeos.weathericons.WeatherIconsHelper
import eightbitlab.com.blurview.BlurView
import eightbitlab.com.blurview.RenderScriptBlur

open class DayWeatherFragment : Fragment() {

    private lateinit var currentTemp: TextView
    private lateinit var currentTempEnd: TextView
    private lateinit var currentLocation: TextView
    private lateinit var currentLocationEnd: TextView
    private lateinit var currentTempFeelsLike: TextView
    private lateinit var currentTempFeelsLikeEnd: TextView
    private lateinit var currentIcon: ImageView

    private lateinit var currentData: WeatherDataGridView

    private lateinit var offlineModeIndicator : IconTextView

    private lateinit var todayForecast: RecyclerView
    private lateinit var nextDaysForecast : RecyclerView
    private lateinit var updateFailedView : View

    private var weatherDataAvailable = false

    private lateinit var tomorrowTemp : TextView
    private lateinit var tomorrowFeelsLike: TextView
    private lateinit var tomorrowIcon : ImageView
    private lateinit var tomorrowData : WeatherDataGridView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_day_weather, container, false)
        currentTemp = v.findViewById(R.id.current_temperature)
        currentTempEnd = v.findViewById(R.id.current_temperature_end)
        currentLocation = v.findViewById(R.id.current_location)
        currentLocationEnd = v.findViewById(R.id.current_location_end)
        currentTempFeelsLike = v.findViewById(R.id.current_temp_feels_like)
        currentTempFeelsLikeEnd = v.findViewById(R.id.current_temp_feels_like_end)
        currentIcon = v.findViewById(R.id.current_icon)

        currentData = v.findViewById(R.id.current_data)

        updateFailedView = v.findViewById(R.id.weather_data_updated_failed)
        todayForecast = v.findViewById(R.id.today_forecast)
        todayForecast.layoutManager = LinearLayoutManager(v.context, LinearLayoutManager.HORIZONTAL, false)

        nextDaysForecast = v.findViewById(R.id.next_days_forecast)
        nextDaysForecast.layoutManager = LinearLayoutManager(v.context)

        ViewCompat.setOnApplyWindowInsetsListener(v) { view, inset ->
            val topInset = WindowInsetsCompat(inset).getInsets(WindowInsetsCompat.Type.systemBars()).top
            val topInsetView = v.findViewById<View>(R.id.top_inset)
            topInsetView.layoutParams.height = topInset
            val topInsetScrimView = v.findViewById<View>(R.id.top_inset_scrim)
            topInsetScrimView.layoutParams.height = topInset

            val bottomInset = WindowInsetsCompat(inset).getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            val bottomInsetView = v.findViewById<View>(R.id.bottom_inset)
            bottomInsetView.layoutParams.height = bottomInset
                    return@setOnApplyWindowInsetsListener inset
        }

        offlineModeIndicator = v.findViewById(R.id.offline_mode)

        tomorrowTemp = v.findViewById(R.id.tomorrow_temperature)
        tomorrowFeelsLike = v.findViewById(R.id.tomorrow_feels_like)
        tomorrowIcon = v.findViewById(R.id.tomorrow_icon)
        tomorrowData = v.findViewById(R.id.tomorrow_data)

        // Setup blur layers
        val pageBackgroundContainer = v.findViewById<ViewGroup>(R.id.page_background_container)
        val currentDataContainer = v.findViewById<BlurView>(R.id.current_data_container)
        currentDataContainer.clipToOutline = true
        currentDataContainer.setupWith(pageBackgroundContainer).setBlurAlgorithm(RenderScriptBlur(v.context))

        val todayForecastContainer = v.findViewById<BlurView>(R.id.today_forecast_container)
        todayForecastContainer.clipToOutline = true
        todayForecastContainer.setupWith(pageBackgroundContainer).setBlurAlgorithm(RenderScriptBlur(v.context))

        val nextDaysForecastContainer = v.findViewById<BlurView>(R.id.next_days_forecast_container)
        nextDaysForecastContainer.clipToOutline = true
        nextDaysForecastContainer.setupWith(pageBackgroundContainer).setBlurAlgorithm(RenderScriptBlur(v.context))

        return v
    }

    fun setOfflineMode(value : Boolean) {
        offlineModeIndicator.alpha = if (value) 1f else 0f
    }

    fun weatherDataUpdateFailed() {
        if (weatherDataAvailable) {
            // Since weather data has already been loaded let's just show the offline indicator
            setOfflineMode(true)
        } else {
            setForecastVisible(false)
        }
    }

    fun setForecastVisible(value : Boolean) {
        todayForecast.visibility = if (value) View.VISIBLE else View.GONE
        updateFailedView.visibility = if (value) View.GONE else View.VISIBLE
    }

    fun setFragmentData(dayWeatherFragmentData: DayWeatherFragmentData) {
        setTodayWeatherHeaderData(dayWeatherFragmentData.todayWeatherHeaderData)
        setTodayWeatherDataGrid(dayWeatherFragmentData.todayWeatherGridData)
        setHourlyForecastAdapter(dayWeatherFragmentData.todayHourlyAdapter)
        setTomorrowWeatherHeaderData(dayWeatherFragmentData.tomorrowWeatherHeaderData)
        setTomorrowWeatherDataGrid(dayWeatherFragmentData.tomorrowWeatherGridData)
        nextDaysForecast.adapter = dayWeatherFragmentData.nextDaysAdapter
    }

    fun setLocation(name : String) {
        currentLocation.text = name
        currentLocationEnd.text = name
    }

    private fun setTodayWeatherHeaderData(weatherHeaderData: WeatherHeaderData) {
        val temperature = WeatherUtils.getFormattedTemperature(weatherHeaderData.temp)
        currentTemp.text = temperature
        currentTempEnd.text = temperature
        val feelsLikeText = WeatherUtils.getFeelsLikeFormattedTemp(requireContext(), weatherHeaderData.tempFeelsLike)
        currentTempFeelsLike.text = feelsLikeText
        currentTempFeelsLikeEnd.text = feelsLikeText

        val isDay = weatherHeaderData.weatherIcon.takeLast(1) == "d"
        val state = WeatherIconsHelper.mapConditionIconToCode(weatherHeaderData.weatherId, isDay)
        currentIcon.setImageResource(WeatherIconsHelper.getDrawable(state, requireContext())!!)
    }

    private fun setTodayWeatherDataGrid(weatherGridData: WeatherGridData) {
        currentData.updateData(weatherGridData)
    }

    private fun setTomorrowWeatherHeaderData(weatherHeaderData: WeatherHeaderData) {
        val temperature = WeatherUtils.getFormattedTemperature(weatherHeaderData.temp)
        tomorrowTemp.text = temperature
        val feelsLikeText = WeatherUtils.getFeelsLikeFormattedTemp(requireContext(), weatherHeaderData.tempFeelsLike)
        tomorrowFeelsLike.text = feelsLikeText

        val isDay = weatherHeaderData.weatherIcon.takeLast(1) == "d"
        val state = WeatherIconsHelper.mapConditionIconToCode(weatherHeaderData.weatherId, isDay)
        tomorrowIcon.setImageResource(WeatherIconsHelper.getDrawable(state, requireContext())!!)
    }

    private fun setTomorrowWeatherDataGrid(weatherGridData: WeatherGridData) {
        tomorrowData.updateData(weatherGridData)
    }

    private fun setHourlyForecastAdapter(hourlyAdapter: HourlyAdapter) {
        if (!weatherDataAvailable) {
            setForecastVisible(true)
            weatherDataAvailable = true
        }
        todayForecast.adapter = hourlyAdapter
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                DayWeatherFragment().apply {
                }
    }
}