package com.revengeos.weather.fragment

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.TransitionDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.revengeos.weather.IconTextView
import com.revengeos.weather.R
import com.revengeos.weather.WeatherDataGridView
import com.revengeos.weather.forecast.HourlyAdapter
import com.revengeos.weather.response.Hourly


open class DayWeatherFragment : Fragment() {

    protected lateinit var currentTemp: TextView
    protected lateinit var currentTempEnd: TextView
    protected lateinit var currentLocation: TextView
    protected lateinit var currentLocationEnd: TextView
    protected lateinit var currentTempFeelsLike: TextView
    protected lateinit var currentTempFeelsLikeEnd: TextView
    protected lateinit var currentIcon: ImageView

    protected lateinit var currentData: WeatherDataGridView

    private lateinit var currentTouchLayer: View

    private lateinit var pageTitle : TextView
    private lateinit var offlineModeIndicator : IconTextView

    private lateinit var todayForecast: RecyclerView
    private lateinit var updateFailedView : View

    private var weatherDataAvailable = false
    private var fragmentVisible = false
    private var backgroundDrawableId = 0

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

        currentTouchLayer = v.findViewById(R.id.current_touch_layer)

        updateFailedView = v.findViewById(R.id.weather_data_updated_failed)
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

        pageTitle = v.findViewById(R.id.today_title)
        val newTitle = getWeatherPageTitle(v.context)
        if (newTitle != null) {
            pageTitle.text = newTitle
        }
        offlineModeIndicator = v.findViewById(R.id.offline_mode)

        return v
    }

    override fun onStart() {
        super.onStart()
        setForecastVisible(isVisible)
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        setFragmentVisibility(!hidden)
    }

    override fun onPause() {
        super.onPause()
        setFragmentVisibility(false)
    }

    private fun setFragmentVisibility(value: Boolean) {
        fragmentVisible = value
        setBackground(backgroundDrawableId)
    }

    protected fun setBackground(drawableId: Int) {
        backgroundDrawableId = drawableId
        if (fragmentVisible && backgroundDrawableId != 0) {
            activity?.findViewById<ImageView>(R.id.background)?.let {
                val currentImage = if (it.drawable == null) ColorDrawable(Color.TRANSPARENT) else {
                    it.drawable
                }
                val transitionDrawable = TransitionDrawable(arrayOf(
                        currentImage,
                        ContextCompat.getDrawable(it.context, drawableId))
                )
                it.setImageDrawable(transitionDrawable)
                transitionDrawable.startTransition(750)
            }
        }
    }

    open fun getWeatherPageTitle(context: Context) : String? {
        return null
    }

    fun setOfflineMode(value: Boolean) {
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

    fun setForecastVisible(value: Boolean) {
        todayForecast.visibility = if (value) View.VISIBLE else View.GONE
        updateFailedView.visibility = if (value) View.GONE else View.VISIBLE

    }

    fun updateHourlyForecast(hourlyForecast: List<Hourly>, timeZone: Int) {
        if (!weatherDataAvailable) {
            setForecastVisible(true)
            weatherDataAvailable = true
        }
        val todayAdapter = HourlyAdapter(hourlyForecast, timeZone)
        todayForecast.adapter = todayAdapter
    }
}