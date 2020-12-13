package com.revengeos.weather

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var todayFragment: Fragment
    private lateinit var tomorrowFragment: Fragment
    private lateinit var nextDaysFragment: Fragment
    private lateinit var settingsFragment: Fragment

    private var activeFragment : Fragment? = null

    private lateinit var bottomNav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (savedInstanceState == null) {
            todayFragment = setupFragment(FeedFragment(), getString(R.string.today_title))
            tomorrowFragment = setupFragment(FeedFragment(), getString(R.string.tomorrow_title))
            nextDaysFragment = setupFragment(FeedFragment(), getString(R.string.next_days_title))
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

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt("selectedTab", bottomNav.selectedItemId)
        super.onSaveInstanceState(outState)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        bottomNav.selectedItemId = savedInstanceState.getInt("selectedTab")
        super.onRestoreInstanceState(savedInstanceState)
    }
}