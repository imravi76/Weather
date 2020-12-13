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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.revengeos.weather.response.OneCallResponse

class MainActivity : AppCompatActivity(), WeatherData.WeatherDataListener {

    val TAG = javaClass.toString()

    private val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
    private val permissionsRequestCode = 420

    private lateinit var todayFragment: Fragment
    private lateinit var tomorrowFragment: Fragment
    private lateinit var nextDaysFragment: Fragment
    private lateinit var settingsFragment: Fragment

    private var activeFragment : Fragment? = null

    private lateinit var bottomNav : BottomNavigationView

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

        var permissionsGranted = true
        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                permissionsGranted = false
            }
        }
        if (permissionsGranted) {
            val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
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
            ActivityCompat.requestPermissions(this, permissions, permissionsRequestCode)
        }
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permissionsRequestCode -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(this,
                                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
                        val criteria = Criteria()
                        criteria.accuracy = Criteria.ACCURACY_FINE
                        val locationProvider = locationManager.getBestProvider(criteria, true)
                        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            val location = locationManager.getLastKnownLocation(locationProvider!!)
                            if (location != null) {
                                updateWeatherUI(location, weatherData)
                            } else {
                                locationManager.requestSingleUpdate(locationProvider, locationListener, null)
                            }
                        }
                    }
                } else {
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
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

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("selectedTab", bottomNav.selectedItemId)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        bottomNav.selectedItemId = savedInstanceState.getInt("selectedTab")
        super.onRestoreInstanceState(savedInstanceState)
    }

    override fun onCurrentWeatherDataUpdated(weatherResponse: WeatherResponse?) {
        if (weatherResponse == null) {
            if (BuildConfig.DEBUG) Log.d(TAG, "Current weather data is null !")
            return
        }
        (todayFragment as FeedFragment).updateCurrentWeather(weatherResponse)
    }

    override fun onOneCallWeatherDataUpdated(oneCallResponse: OneCallResponse?) {
        if (oneCallResponse == null) {
            if (BuildConfig.DEBUG) Log.d(TAG, "Onecall weather data is null !")
            return
        }
        (todayFragment as FeedFragment).updateForecastWeather(oneCallResponse)
    }
}