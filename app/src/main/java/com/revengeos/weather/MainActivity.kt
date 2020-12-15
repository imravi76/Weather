package com.revengeos.weather

import android.content.Intent
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.revengeos.weather.response.OneCallResponse
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

    private val weatherData = WeatherData(this)
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

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (savedInstanceState == null) {
            todayFragment = setupFragment(FeedFragment.newInstance(), getString(R.string.today_title))
            tomorrowFragment = setupFragment(FeedFragment.newInstance(), getString(R.string.tomorrow_title))
            nextDaysFragment = setupFragment(FeedFragment.newInstance(), getString(R.string.next_days_title))
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

    override fun onCurrentWeatherDataUpdated(weatherResponse: WeatherResponse?) {
        if (weatherResponse == null) {
            if (BuildConfig.DEBUG) Log.d(TAG, "Current weather data is null !")
            return
        }
        mCurrentTime = weatherResponse.dt
        (todayFragment as FeedFragment).updateCurrentWeather(weatherResponse)
    }

    override fun onOneCallWeatherDataUpdated(oneCallResponse: OneCallResponse?) {
        if (oneCallResponse == null || mCurrentTime == null) {
            if (BuildConfig.DEBUG) Log.d(TAG, "Onecall weather data is null !")
            return
        }
        // Update today's hourly forecast data
        var hourlyForecast = (oneCallResponse!!.hourly).subList(0, 25).toMutableList()
        if (hourlyForecast[0].dt < mCurrentTime!!) {
            hourlyForecast.removeAt(0)
        } else {
            hourlyForecast.removeAt(24)
        }
        (todayFragment as FeedFragment).updateHourlyForecast(hourlyForecast)
    }
}