package com.revengeos.weather

import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.revengeos.weather.forecast.DailyAdapter
import com.revengeos.weather.forecast.HourlyAdapter
import com.revengeos.weather.fragment.DayWeatherFragment
import com.revengeos.weather.fragment.DayWeatherFragmentData
import com.revengeos.weather.response.Current
import com.revengeos.weather.response.Daily
import com.revengeos.weather.response.Hourly
import com.revengeos.weather.response.OneCallResponse
import com.revengeos.weather.response.current.CurrentWeatherResponse
import com.revengeos.weather.util.WeatherUtils
import com.yayandroid.locationmanager.base.LocationBaseActivity
import com.yayandroid.locationmanager.configuration.*

class MainActivity : LocationBaseActivity(), WeatherDataService.WeatherDataListener {

    val TAG = javaClass.toString()

    private lateinit var dayWeatherFragment: DayWeatherFragment
    private lateinit var nextDaysFragment: Fragment
    private lateinit var settingsFragment: Fragment

    private var activeFragment : Fragment? = null

    private lateinit var bottomNav : BottomNavigationView

    private lateinit var weatherDataService : WeatherDataService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        weatherDataService = WeatherDataService(applicationContext, this)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (savedInstanceState == null) {
            dayWeatherFragment = (setupFragment(DayWeatherFragment.newInstance(), getString(R.string.day_weather_fragment_tag)) as DayWeatherFragment)
            nextDaysFragment = setupFragment(SettingsFragment(), getString(R.string.next_days_title))
            settingsFragment = setupFragment(SettingsFragment(), getString(R.string.nav_settings))
            switchActiveFragment(dayWeatherFragment)
        } else {
            dayWeatherFragment = (supportFragmentManager.findFragmentByTag(getString(R.string.day_weather_fragment_tag))!! as DayWeatherFragment)
            nextDaysFragment = supportFragmentManager.findFragmentByTag(getString(R.string.next_days_title))!!
            settingsFragment = supportFragmentManager.findFragmentByTag(getString(R.string.nav_settings))!!
        }

        bottomNav = findViewById(R.id.main_nav)
        bottomNav.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.today -> {
                    switchActiveFragment(dayWeatherFragment as Fragment)
                    true
                }
                R.id.next_days -> {
                    switchActiveFragment(nextDaysFragment)
                    true
                }
                R.id.nav_settings -> {
                    switchActiveFragment(settingsFragment)
                    true
                }
                else -> false
            }
        }
        getLocation()
    }

    private fun setupFragment(fragment: Fragment, title: String) : Fragment {
        supportFragmentManager.beginTransaction().add(R.id.main_content, fragment, title).hide(fragment).commit()
        return fragment
    }

    private fun switchActiveFragment(newFragment: Fragment) {
        if (newFragment != activeFragment) {
            val transaction = supportFragmentManager.beginTransaction()
            activeFragment?.let { transaction.hide(it) }
            transaction.show(newFragment).commit()
            activeFragment = newFragment
        }
    }

    private fun updateWeatherUI(location: Location, weatherDataService: WeatherDataService) {
        weatherDataService.latitude = location.latitude
        weatherDataService.longitude = location.longitude
        weatherDataService.updateCurrentWeatherData()
        weatherDataService.updateOneCallWeatherData()
    }

    override fun onLocationChanged(location: Location?) {
        if (location != null) {
            updateWeatherUI(location, weatherDataService)
        }
    }

    override fun onLocationFailed(type: Int) {
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("selectedTab", bottomNav.selectedItemId)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        bottomNav.selectedItemId = savedInstanceState.getInt("selectedTab")
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun getLocationConfiguration(): LocationConfiguration? {
        return LocationConfiguration.Builder()
                .keepTracking(false)
                .askForPermission(PermissionConfiguration.Builder().build())
                .useGooglePlayServices(GooglePlayServicesConfiguration.Builder().build())
                .useGooglePlayServices(GooglePlayServicesConfiguration.Builder().build())
                .useDefaultProviders(DefaultProviderConfiguration.Builder().build())
                .build()
    }

    override fun onCurrentWeatherUpdateSuccess(currentWeatherResponse: CurrentWeatherResponse, cached: Boolean) {
        dayWeatherFragment.setLocation(currentWeatherResponse.name)

    }

    override fun onOneCallWeatherUpdateSuccess(oneCallResponse: OneCallResponse, cached: Boolean) {
        val current : Current = oneCallResponse.current
        val today : Daily = oneCallResponse.daily[0]

        val todayWeatherHeaderData = WeatherHeaderData(current.temp, current.feelsLike,
                current.weather[0].icon, current.weather[0].id)
        val todayWeatherGridData = WeatherGridData(current.sunrise, current.sunset,
                oneCallResponse.timezoneOffset, current.pressure, current.humidity, current.windDeg,
                current.windSpeed, current.visibility, today.temp.min, today.temp.max, null)

        // Update today's hourly forecast data
        var todayHourlyForecast = (oneCallResponse.hourly).subList(0, 25).toMutableList()
        if (todayHourlyForecast[0].dt < current.dt) {
            // Remove first element since it's time is earlier than current weather
            todayHourlyForecast.removeAt(0)
        } else {
            todayHourlyForecast.removeLast()
        }
        val todayHourlyAdapter = HourlyAdapter(todayHourlyForecast, oneCallResponse.timezoneOffset)

        // Update tomorrow's hourly forecast data
        val tomorrow : Daily = oneCallResponse.daily[1]

        val tomorrowWeatherHeaderData = WeatherHeaderData(tomorrow.temp.day, tomorrow.feels_like.day,
                tomorrow.weather[0].icon, tomorrow.weather[0].id)

        val tomorrowWeatherGridData = WeatherGridData(tomorrow.sunrise, tomorrow.sunset, oneCallResponse.timezoneOffset,
                tomorrow.pressure, tomorrow.humidity, tomorrow.windDeg,
                tomorrow.windSpeed, null, tomorrow.temp.min, tomorrow.temp.max, tomorrow.pop)

        val nextDaysAdapter = DailyAdapter(oneCallResponse.daily, oneCallResponse.timezoneOffset)

        val weatherFragmentData = DayWeatherFragmentData(todayWeatherHeaderData,
                todayWeatherGridData, todayHourlyAdapter, tomorrowWeatherHeaderData, tomorrowWeatherGridData, nextDaysAdapter)

        dayWeatherFragment.setOfflineMode(cached)
        dayWeatherFragment.setFragmentData(weatherFragmentData)
    }

    override fun onCurrentWeatherUpdateFailed(errorMessage: String) {
        Log.d(TAG, "Current weather data cannot be updated : $errorMessage")
    }

    override fun onOneCallWeatherUpdateFailed(errorMessage: String) {
        Log.d(TAG, "Onecall weather data cannot be updated : $errorMessage")
        (dayWeatherFragment as DayWeatherFragment).weatherDataUpdateFailed()
    }
}