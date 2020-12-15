package com.revengeos.weather

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.revengeos.weather.BuildConfig.DEBUG
import com.revengeos.weather.forecast.HourlyAdapter
import com.revengeos.weather.response.Hourly
import com.revengeos.weather.response.OneCallResponse
import com.revengeos.weather.util.WeatherUtils.Companion.getFeelsLikeFormattedTemp
import com.revengeos.weather.util.WeatherUtils.Companion.getFormattedTemperature
import com.revengeos.weathericons.WeatherIconsHelper.Companion.getDrawable
import com.revengeos.weathericons.WeatherIconsHelper.Companion.mapConditionIconToCode
import rjsv.expframelayout.ExpandableFrameLayout

class FeedFragment : Fragment() {

    val TAG = javaClass.toString()

    private lateinit var currentTemp: TextView
    private lateinit var currentTempEnd: TextView
    private lateinit var currentLocation: TextView
    private lateinit var currentLocationEnd: TextView
    private lateinit var currentTempFeelsLike: TextView
    private lateinit var currentTempFeelsLikeEnd: TextView
    private lateinit var currentIcon: ImageView

    private lateinit var currentData: WeatherDataGridView

    private lateinit var currentMoreDataLayout: ExpandableFrameLayout
    private lateinit var currentTouchLayer: View

    private lateinit var todayForecast: RecyclerView

    private var mCurrentTime : Long = -1
    private var mCurrentTimeShift : Int = 0
    private var mCurrentSunrise : Long = -1
    private var mCurrentSunset : Long = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_feed, container, false)
        currentTemp = v.findViewById(R.id.current_temperature)
        currentTempEnd = v.findViewById(R.id.current_temperature_end)
        currentLocation = v.findViewById(R.id.current_location)
        currentLocationEnd = v.findViewById(R.id.current_location_end)
        currentTempFeelsLike = v.findViewById(R.id.current_temp_feels_like)
        currentTempFeelsLikeEnd = v.findViewById(R.id.current_temp_feels_like_end)
        currentIcon = v.findViewById(R.id.current_icon)

        currentMoreDataLayout = v.findViewById(R.id.current_more)
        currentMoreDataLayout.collapse()
        currentMoreDataLayout.animationDuration = 700

        currentData = v.findViewById(R.id.current_data)

        currentTouchLayer = v.findViewById(R.id.current_touch_layer)
        currentTouchLayer.setOnClickListener { currentMoreDataLayout.toggle() }

        todayForecast = v.findViewById(R.id.today_forecast)
        var itemDecoration = DividerItemDecoration(this.context, DividerItemDecoration.VERTICAL)
        itemDecoration.setDrawable(resources.getDrawable(R.drawable.forecast_container_separator, v.context.theme))
        todayForecast.addItemDecoration(itemDecoration)
        todayForecast.layoutManager = LinearLayoutManager(v.context)

        ViewCompat.setOnApplyWindowInsetsListener(v) { view, inset ->
            val topInset = WindowInsetsCompat(inset).getInsets(WindowInsetsCompat.Type.systemBars()).top
            val topInsetView = v.findViewById<View>(R.id.top_inset)
            topInsetView.layoutParams.height = topInset

            val bottomInset = WindowInsetsCompat(inset).getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            val bottomInsetView = v.findViewById<View>(R.id.bottom_inset)
            bottomInsetView.layoutParams.height = bottomInset
                    return@setOnApplyWindowInsetsListener inset
        }

        return v
    }

    fun updateCurrentWeather(weatherResponse: WeatherResponse) {
        mCurrentTime = weatherResponse.dt
        mCurrentTimeShift = weatherResponse.timezone
        mCurrentSunrise = weatherResponse.sys.sunrise
        mCurrentSunset = weatherResponse.sys.sunset

        val temperature = getFormattedTemperature(weatherResponse.main.temp)
        currentTemp.text = temperature
        currentTempEnd.text = temperature
        currentLocation.text = weatherResponse.name
        currentLocationEnd.text = weatherResponse.name
        val feelsLikeText = getFeelsLikeFormattedTemp(requireContext(), weatherResponse.main.feels_like)
        currentTempFeelsLike.text = feelsLikeText
        currentTempFeelsLikeEnd.text = feelsLikeText
        currentData.updateData(weatherResponse.sys.sunrise, weatherResponse.sys.sunset, weatherResponse.timezone,
                weatherResponse.main.pressure, weatherResponse.main.humidity, weatherResponse.wind.deg,
                weatherResponse.wind.speed, weatherResponse.visibility, weatherResponse.main.temp_min, weatherResponse.main.temp_max)

        val isDay = weatherResponse.weather[0].icon.takeLast(1) == "d"
        val state = mapConditionIconToCode(weatherResponse.weather[0].id, isDay)
        currentIcon.setImageResource(getDrawable(state, requireContext())!!)
    }

    fun updateHourlyForecast(hourlyForecast : List<Hourly>) {
        val todayAdapter = HourlyAdapter(hourlyForecast, mCurrentTimeShift, mCurrentSunrise, mCurrentSunset)
        todayForecast.adapter = todayAdapter
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                FeedFragment().apply {
                    }
    }
}