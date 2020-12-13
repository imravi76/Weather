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
import com.revengeos.weather.response.OneCallResponse
import com.revengeos.weather.util.WeatherUtils.Companion.getFeelsLikeFormattedTemp
import com.revengeos.weather.util.WeatherUtils.Companion.getFormattedTemperature
import com.revengeos.weathericons.WeatherIconsHelper.Companion.getDrawable
import com.revengeos.weathericons.WeatherIconsHelper.Companion.mapConditionIconToCode
import rjsv.expframelayout.ExpandableFrameLayout

class FeedFragment : Fragment(), WeatherData.WeatherDataListener {

    val TAG = javaClass.toString()

    private val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    private val permissionsRequestCode = 420

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

    private var mCurrentTime : Long = -1;

    private val weatherData = WeatherData(this)

    val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            updateWeatherUI(location, weatherData)
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {}
        override fun onProviderEnabled(provider: String) {}
        override fun onProviderDisabled(provider: String) {}
    }

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

        var permissionsGranted = true
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(requireActivity(), permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsGranted = false
            }
        }
        if (permissionsGranted) {
            val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
            val criteria = Criteria()
            criteria.accuracy = Criteria.ACCURACY_FINE
            val locationProvider = locationManager.getBestProvider(criteria, true)
            val location = locationManager.getLastKnownLocation(locationProvider!!)
            if (location != null) {
                updateWeatherUI(location, weatherData)
            } else {
                locationManager.requestSingleUpdate(locationProvider, locationListener, null)
            }
        } else {
            ActivityCompat.requestPermissions(requireActivity(), permissions, permissionsRequestCode)
        }

        ViewCompat.setOnApplyWindowInsetsListener(requireActivity().window.decorView) { view, inset ->
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            permissionsRequestCode -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(requireContext(),
                                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        val locationManager = requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        val criteria = Criteria()
                        criteria.accuracy = Criteria.ACCURACY_FINE
                        val locationProvider = locationManager.getBestProvider(criteria, true)
                        if (ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(this.requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            val location = locationManager.getLastKnownLocation(locationProvider!!)
                            if (location != null) {
                                updateWeatherUI(location, weatherData)
                            } else {
                                locationManager.requestSingleUpdate(locationProvider, locationListener, null)
                            }
                        }
                    }
                } else {
                    Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    private fun updateWeatherUI(location : Location, weatherData : WeatherData) {
        weatherData.latitude = location.latitude
        weatherData.longitude = location.longitude
        weatherData.updateCurrentWeatherData()
        weatherData.updateOneCallWeatherData()
    }

    override fun onCurrentWeatherDataUpdated(weatherResponse: WeatherResponse?) {
        if (weatherResponse == null) {
            if (DEBUG) Log.d(TAG, "Current weather data is null !")
            return
        }

        mCurrentTime = weatherResponse.dt

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
        val state = mapConditionIconToCode(weatherResponse.weather[0].id,
                weatherResponse.sys.sunrise, weatherResponse.sys.sunset)
        currentIcon.setImageDrawable(resources.getDrawable(getDrawable(state, requireContext())!!))
    }

    override fun onOneCallWeatherDataUpdated(oneCallResponse: OneCallResponse?) {
        if (oneCallResponse == null) {
            if (DEBUG) Log.d(TAG, "Onecall weather data is null !")
            return
        }

        var hourlyForecast = (oneCallResponse.hourly).subList(0, 25).toMutableList()
        if (hourlyForecast[0].dt < mCurrentTime) {
            hourlyForecast.removeAt(0)
        } else {
            hourlyForecast.removeAt(24)
        }
        val todayAdapter = HourlyAdapter(hourlyForecast)
        todayForecast.adapter = todayAdapter
    }

    companion object {
        @JvmStatic
        fun newInstance() =
                FeedFragment().apply {
                    }
    }
}