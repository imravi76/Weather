package com.revengeos.weather

import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.revengeos.weather.forecast.HourlyAdapter
import com.revengeos.weather.fragment.DayWeatherFragment
import com.revengeos.weather.fragment.TodayFragment
import com.revengeos.weather.fragment.TomorrowFragment
import com.revengeos.weather.response.Daily
import com.revengeos.weather.response.Hourly
import com.revengeos.weather.response.OneCallResponse
import com.revengeos.weather.response.current.CurrentWeatherResponse
import com.revengeos.weather.util.WeatherUtils
import com.yayandroid.locationmanager.base.LocationBaseActivity
import com.yayandroid.locationmanager.configuration.*

class MainActivity : LocationBaseActivity(), WeatherDataService.WeatherDataListener {

    val TAG = javaClass.toString()

    private lateinit var todayFragment: Fragment
    private lateinit var tomorrowFragment: Fragment
    private lateinit var nextDaysFragment: Fragment
    private lateinit var settingsFragment: Fragment

    private lateinit var todayWeatherGridData : WeatherGridData
    private lateinit var tomorrowWeatherGridData: WeatherGridData
    private lateinit var todayHourlyAdapter: HourlyAdapter
    private lateinit var tomorrowHourlyAdapter: HourlyAdapter
    private lateinit var todayWeatherHeaderData: WeatherHeaderData
    private lateinit var tomorrowWeatherHeaderData: WeatherHeaderData


    private var activeFragment : Fragment? = null

    private lateinit var bottomNav : BottomNavigationView

    private lateinit var weatherDataService : WeatherDataService

    private var mCurrentTime : Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        weatherDataService = WeatherDataService(applicationContext, this)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (savedInstanceState == null) {
            todayFragment = setupFragment(TodayFragment.newInstance(), getString(R.string.today_title))
            tomorrowFragment = setupFragment(TomorrowFragment.newInstance(), getString(R.string.tomorrow_title))
            nextDaysFragment = setupFragment(TomorrowFragment.newInstance(), getString(R.string.next_days_title))
            settingsFragment = setupFragment(SettingsFragment(), getString(R.string.nav_settings))
            switchActiveFragment(todayFragment)
        } else {
            todayFragment = supportFragmentManager.findFragmentByTag(getString(R.string.today_title))!!
            tomorrowFragment = supportFragmentManager.findFragmentByTag(getString(R.string.tomorrow_title))!!
            nextDaysFragment = supportFragmentManager.findFragmentByTag(getString(R.string.next_days_title))!!
            settingsFragment = supportFragmentManager.findFragmentByTag(getString(R.string.nav_settings))!!
        }

        bottomNav = findViewById(R.id.main_nav)
        bottomNav.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.today -> {
                    switchActiveFragment(todayFragment)
                    true
                }
                R.id.tomorrow -> {
                    switchActiveFragment(tomorrowFragment)
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
        mCurrentTime = currentWeatherResponse.dt

        todayWeatherHeaderData = WeatherHeaderData(currentWeatherResponse.main.temp,
                currentWeatherResponse.main.feels_like, currentWeatherResponse.name,
                currentWeatherResponse.weather[0].icon,
                currentWeatherResponse.weather[0].id)
        (todayFragment as DayWeatherFragment).setWeatherHeaderData(todayWeatherHeaderData)

        todayWeatherGridData = WeatherGridData(currentWeatherResponse.sys.sunrise, currentWeatherResponse.sys.sunset, currentWeatherResponse.timezone,
                currentWeatherResponse.main.pressure, currentWeatherResponse.main.humidity, currentWeatherResponse.wind.deg,
                currentWeatherResponse.wind.speed, currentWeatherResponse.visibility, currentWeatherResponse.main.temp_min, currentWeatherResponse.main.temp_max, null)
        (todayFragment as DayWeatherFragment).setWeatherDataGrid(todayWeatherGridData)

    }

    override fun onOneCallWeatherUpdateSuccess(oneCallResponse: OneCallResponse, cached: Boolean) {
        if (mCurrentTime == null) {
            if (BuildConfig.DEBUG) Log.d(TAG, "Current time is not available !")
            return
        }

        // Update today's hourly forecast data
        var todayHourlyForecast = (oneCallResponse.hourly).subList(0, 25).toMutableList()
        if (todayHourlyForecast[0].dt < mCurrentTime!!) {
            // Remove first element since it's time is earlier than current weather
            todayHourlyForecast.removeAt(0)
        } else {
            todayHourlyForecast.removeLast()
        }

        todayHourlyAdapter = HourlyAdapter(todayHourlyForecast, oneCallResponse.timezoneOffset)
        (todayFragment as DayWeatherFragment).setHourlyForecastAdapter(todayHourlyAdapter)

        // Update tomorrow's hourly forecast data
        val tomorrow : Daily = oneCallResponse.daily[1]

        tomorrowWeatherHeaderData = WeatherHeaderData(tomorrow.temp.day, tomorrow.feels_like.day,
                todayWeatherHeaderData.location, tomorrow.weather[0].icon, tomorrow.weather[0].id)
        (tomorrowFragment as DayWeatherFragment).setWeatherHeaderData(tomorrowWeatherHeaderData)

        var tomorrowHourlyForecast = mutableListOf<Hourly>()

        // Filter all the items oneCallResponse.hourly that have the same day of tomorrow
        for (hourlyItem in oneCallResponse.hourly) {
            if (WeatherUtils.getDateFromEpoch(hourlyItem.dt, oneCallResponse.timezoneOffset)
                    == WeatherUtils.getDateFromEpoch(tomorrow.dt, oneCallResponse.timezoneOffset)) {
                tomorrowHourlyForecast.add(hourlyItem)
            }
        }

        tomorrowWeatherGridData = WeatherGridData(tomorrow.sunrise, tomorrow.sunset, oneCallResponse.timezoneOffset,
                tomorrow.pressure, tomorrow.humidity, tomorrow.windDeg,
                tomorrow.windSpeed, null, tomorrow.temp.min, tomorrow.temp.max, tomorrow.pop)
        (tomorrowFragment as DayWeatherFragment).setWeatherDataGrid(tomorrowWeatherGridData)

        tomorrowHourlyAdapter = HourlyAdapter(tomorrowHourlyForecast, oneCallResponse.timezoneOffset)
        (tomorrowFragment as DayWeatherFragment).setHourlyForecastAdapter(tomorrowHourlyAdapter)

        (todayFragment as DayWeatherFragment).setOfflineMode(cached)
        (tomorrowFragment as DayWeatherFragment).setOfflineMode(cached)
    }

    override fun onCurrentWeatherUpdateFailed(errorMessage: String) {
        Log.d(TAG, "Current weather data cannot be updated : $errorMessage")
    }

    override fun onOneCallWeatherUpdateFailed(errorMessage: String) {
        Log.d(TAG, "Onecall weather data cannot be updated : $errorMessage")
        (todayFragment as TodayFragment).weatherDataUpdateFailed()
        (tomorrowFragment as TomorrowFragment).weatherDataUpdateFailed()
    }
}