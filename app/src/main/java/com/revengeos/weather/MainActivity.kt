package com.revengeos.weather

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.revengeos.weather.fragment.TodayFragment
import com.revengeos.weather.fragment.TomorrowFragment
import com.revengeos.weather.response.Daily
import com.revengeos.weather.response.Hourly
import com.revengeos.weather.response.OneCallResponse
import com.revengeos.weather.response.current.CurrentWeatherResponse
import com.revengeos.weather.util.WeatherUtils
import com.yayandroid.locationmanager.configuration.DefaultProviderConfiguration
import com.yayandroid.locationmanager.configuration.GooglePlayServicesConfiguration
import com.yayandroid.locationmanager.configuration.LocationConfiguration
import com.yayandroid.locationmanager.configuration.PermissionConfiguration
import com.yayandroid.locationmanager.LocationManager
import com.yayandroid.locationmanager.listener.LocationListener

class MainActivity : AppCompatActivity(), WeatherData.WeatherDataListener {

    val TAG = javaClass.toString()

    private lateinit var todayFragment: Fragment
    private lateinit var tomorrowFragment: Fragment
    private lateinit var nextDaysFragment: Fragment
    private lateinit var settingsFragment: Fragment

    private var activeFragment : Fragment? = null

    private lateinit var bottomNav : BottomNavigationView

    private lateinit var weatherData : WeatherData
    private lateinit var locationManager : LocationManager

    private var mCurrentTime : Long? = null

    private val locationListener: LocationListener = object : LocationListener {
        override fun onProcessTypeChanged(processType: Int) {

        }

        override fun onLocationChanged(location: Location?) {
            if (location != null) {
                updateWeatherUI(location, weatherData)
            }
        }

        override fun onLocationFailed(type: Int) {
        }

        override fun onPermissionGranted(alreadyHadPermission: Boolean) {
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        }

        override fun onProviderEnabled(provider: String?) {
        }

        override fun onProviderDisabled(provider: String?) {
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        weatherData = WeatherData(applicationContext, this)

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

        val awesomeConfiguration = LocationConfiguration.Builder()
                .keepTracking(false)
                .askForPermission(PermissionConfiguration.Builder().build())
                .useGooglePlayServices(GooglePlayServicesConfiguration.Builder().build())
                .useDefaultProviders(DefaultProviderConfiguration.Builder().build())
                .build()
        locationManager = LocationManager.Builder(applicationContext)
                .activity(this) // Only required to ask permission and/or GoogleApi - SettingsApi
                .configuration(awesomeConfiguration)
                .notify(locationListener)
                .build()
        locationManager.get()
    }

    private fun setupFragment(fragment : Fragment, title : String) : Fragment {
        supportFragmentManager.beginTransaction().add(R.id.main_content, fragment, title).hide(fragment).commit()
        return fragment
    }

    private fun switchActiveFragment(newFragment : Fragment) {
        if (newFragment != activeFragment) {
            val transaction = supportFragmentManager.beginTransaction()
            activeFragment?.let { transaction.hide(it) }
            transaction.show(newFragment).commit()
            activeFragment = newFragment
        }
    }

    private fun updateWeatherUI(location : Location, weatherData : WeatherData) {
        weatherData.latitude = location.latitude
        weatherData.longitude = location.longitude
        weatherData.updateCurrentWeatherData()
        weatherData.updateOneCallWeatherData()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("selectedTab", bottomNav.selectedItemId)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        bottomNav.selectedItemId = savedInstanceState.getInt("selectedTab")
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        locationManager.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onDestroy() {
        super.onDestroy()
        locationManager.onDestroy()
    }

    override fun onResume() {
        super.onResume()
        locationManager.onResume()
    }

    override fun onPause() {
        super.onPause()
        locationManager.onPause()
    }

    override fun onCurrentWeatherDataUpdated(currentWeatherResponse: CurrentWeatherResponse?, cached : Boolean) {
        if (currentWeatherResponse == null) {
            if (BuildConfig.DEBUG) Log.d(TAG, "Current weather data is null !")
            return
        }
        mCurrentTime = currentWeatherResponse.dt
        (todayFragment as TodayFragment).updateCurrentWeather(currentWeatherResponse)
        (todayFragment as TodayFragment).setOfflineMode(cached)
        (tomorrowFragment as TomorrowFragment).locationName = currentWeatherResponse.name
        (tomorrowFragment as TomorrowFragment).setOfflineMode(cached)

    }

    override fun onOneCallWeatherDataUpdated(oneCallResponse: OneCallResponse?, cached : Boolean) {
        if (oneCallResponse == null || mCurrentTime == null) {
            if (BuildConfig.DEBUG) Log.d(TAG, "Onecall weather data is null !")
            return
        }
        // Update today's hourly forecast data
        var hourlyForecast = (oneCallResponse.hourly).subList(0, 25).toMutableList()
        if (hourlyForecast[0].dt < mCurrentTime!!) {
            // Remove first element since it's time is earlier than current weather
            hourlyForecast.removeAt(0)
        } else {
            hourlyForecast.removeLast()
        }
        (todayFragment as TodayFragment).updateHourlyForecast(hourlyForecast, oneCallResponse.timezoneOffset)

        // Update tomorrow's hourly forecast data
        val tomorrow : Daily = oneCallResponse.daily[1]
        var tomorrowHourlyForecast = mutableListOf<Hourly>()

        // Filter all the items oneCallResponse.hourly that have the same day of tomorrow
        for (hourlyItem in oneCallResponse.hourly) {
            if (WeatherUtils.getDateFromEpoch(hourlyItem.dt, oneCallResponse.timezoneOffset)
                    == WeatherUtils.getDateFromEpoch(tomorrow.dt, oneCallResponse.timezoneOffset)) {
                tomorrowHourlyForecast.add(hourlyItem)
            }
        }
        (tomorrowFragment as TomorrowFragment).updateTomorrowWeather(tomorrow, oneCallResponse.timezoneOffset)
        (tomorrowFragment as TomorrowFragment).updateHourlyForecast(tomorrowHourlyForecast, oneCallResponse.timezoneOffset)

        (todayFragment as TodayFragment).setOfflineMode(cached)
        (tomorrowFragment as TomorrowFragment).setOfflineMode(cached)
    }
}