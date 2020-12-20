package com.revengeos.weather

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.Nullable
import androidx.constraintlayout.widget.ConstraintLayout
import com.revengeos.revengeui.view.ExpandableIndicator
import com.revengeos.weather.util.WeatherUtils.Companion.getFormattedDistance
import com.revengeos.weather.util.WeatherUtils.Companion.getFormattedSpeed
import com.revengeos.weather.util.WeatherUtils.Companion.getFormattedTemperature
import com.revengeos.weather.util.WeatherUtils.Companion.getTimeFromEpoch
import rjsv.expframelayout.ExpandableFrameLayout
import rjsv.expframelayout.ExpandableFrameLayoutListener
import rjsv.expframelayout.enumerators.ExpandableFrameLayoutStatus
import kotlin.math.roundToInt

class WeatherDataGridView : ConstraintLayout {

    private val sunriseView: IconTextView
    private val sunsetView: IconTextView
    private val pressureView: IconTextView
    private val humidityView: IconTextView
    private val windView: IconTextView
    private val visibilityDistanceView : IconTextView
    private val precipitationsView : IconTextView
    private val minTempView: IconTextView
    private val maxTempView: IconTextView

    private val expandableIndicator : ExpandableIndicator
    private val additionalData : ExpandableFrameLayout

    constructor (context: Context) : this(context, null) {
    }

    constructor(context: Context, @Nullable attrs: AttributeSet?) : this(context, attrs, 0) {
    }

    constructor(context: Context, @Nullable attrs: AttributeSet?, defStyleAttr: Int) : this(context, attrs, defStyleAttr, 0) {
    }

    constructor(context: Context, @Nullable attrs: AttributeSet?, defStyleAttr: Int, defStyleRes : Int) : super(context, attrs, defStyleAttr, defStyleRes) {
        LayoutInflater.from(context).inflate(R.layout.weather_data_grid, this, true);

        sunriseView = findViewById(R.id.current_sunrise)
        sunsetView = findViewById(R.id.current_sunset)
        pressureView = findViewById(R.id.current_pressure)
        humidityView = findViewById(R.id.current_humidity)
        windView = findViewById(R.id.current_wind)
        visibilityDistanceView = findViewById(R.id.current_visibility)
        precipitationsView = findViewById(R.id.current_precipitations)
        minTempView = findViewById(R.id.current_min_temp)
        maxTempView = findViewById(R.id.current_max_temp)

        expandableIndicator = findViewById(R.id.expandable_indicator)

        additionalData = findViewById(R.id.additional_data)
        additionalData.collapse()
        additionalData.animationDuration = 700
        additionalData.setAnimationListener(object : ExpandableFrameLayoutListener {
            override fun onAnimationEnd(status: ExpandableFrameLayoutStatus) {
            }

            override fun onAnimationStart(status: ExpandableFrameLayoutStatus) {
            }

            override fun onClosed() {
            }

            override fun onOpened() {
            }

            override fun onPreClose() {
                expandableIndicator.setExpanded(false)
            }

            override fun onPreOpen() {
                expandableIndicator.setExpanded(true)
            }

        })

        setOnClickListener { view ->
            additionalData.toggle()
        }
    }

    fun updateData(weatherGridData : WeatherGridData) {

        minTempView.textView.text = getFormattedTemperature(weatherGridData.minTemp)
        maxTempView.textView.text = getFormattedTemperature(weatherGridData.maxTemp)

        sunriseView.textView.text = getTimeFromEpoch(weatherGridData.sunrise, weatherGridData.timeZone)
        sunsetView.textView.text = getTimeFromEpoch(weatherGridData.sunset, weatherGridData.timeZone)
        pressureView.textView.text = "${weatherGridData.pressure} hPa"
        humidityView.textView.text = "${weatherGridData.humidity} %"
        windView.iconView.rotation = weatherGridData.windDirection.toFloat()
        windView.textView.text = getFormattedSpeed(weatherGridData.windSpeed)
        if (weatherGridData.visibility == null) {
            visibilityDistanceView.visibility = GONE
        } else {
            visibilityDistanceView.textView.text = getFormattedDistance(weatherGridData.visibility.toFloat())
            visibilityDistanceView.visibility = VISIBLE
        }
        if (weatherGridData.precipitations == null) {
            precipitationsView.visibility = GONE
        } else {
            if (weatherGridData.precipitations > 0.15f) {
                precipitationsView.iconView.setImageResource(R.drawable.ic_umbrella_outline)
            } else {
                precipitationsView.iconView.setImageResource(R.drawable.ic_umbrella_closed_outline)
            }
            precipitationsView.textView.text = "${(weatherGridData.precipitations * 100).roundToInt()} %"
            precipitationsView.visibility = View.VISIBLE
        }
    }
}