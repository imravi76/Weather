package com.revengeos.weather

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var feedFragment: FeedFragment
    private lateinit var citiesFragment: CitiesFragment
    private lateinit var settingsFragment: SettingsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        val mMainNav = findViewById<BottomNavigationView>(R.id.main_nav)

        feedFragment = FeedFragment()
        citiesFragment = CitiesFragment()
        settingsFragment = SettingsFragment()

        supportFragmentManager.beginTransaction().add(R.id.main_frame, feedFragment, "").show(feedFragment).commit()
        setFragment(feedFragment)

        mMainNav.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_feed -> {
                    setFragment(feedFragment)
                    true
                }
                R.id.nav_cities -> {
                    setFragment(citiesFragment)
                    true
                }
                R.id.nav_settings -> {
                    setFragment(settingsFragment)
                    true
                }
                else -> false
            }
        }

        val fragmentFrame = findViewById<ViewGroup>(R.id.main_frame)
    }

    private fun setFragment(fragment: Fragment) {
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.main_frame, fragment)
        fragmentTransaction.commit()
    }
}