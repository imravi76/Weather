package com.revengeos.weather

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TableLayout
import androidx.annotation.Nullable
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.revengeos.revengeui.view.ExpandableIndicator
import com.revengeos.weather.util.WeatherUtils.Companion.getFormattedDistance
import com.revengeos.weather.util.WeatherUtils.Companion.getFormattedSpeed
import com.revengeos.weather.util.WeatherUtils.Companion.getFormattedTemperature
import com.revengeos.weather.util.WeatherUtils.Companion.getTimeFromEpoch
import rjsv.expframelayout.ExpandableFrameLayout

class WeatherDataGridView : ConstraintLayout {

    private val sunriseView: IconTextView
    private val sunsetView: IconTextView
    private val pressureView: IconTextView
    private val humidityView: IconTextView
    private val windView: IconTextView
    private val visibilityDistanceView : IconTextView
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
        minTempView = findViewById(R.id.current_min_temp)
        maxTempView = findViewById(R.id.current_max_temp)

        additionalData = findViewById(R.id.additional_data)
        additionalData.collapse()
        additionalData.animationDuration = 700

        expandableIndicator = findViewById(R.id.expandable_indicator)
        expandableIndicator.setOnClickListener { view ->
            expandableIndicator.toggleExpansion()
            additionalData.toggle()
        }
    }

    fun updateData(sunrise: Long, sunset: Long, timeZone : Int, pressure: Int, humidity: Int, windDirection: Int,
                   windSpeed: Float, visibility: Int, minTemp: Float, maxTemp: Float) {

        minTempView.textView.text = getFormattedTemperature(minTemp)
        maxTempView.textView.text = getFormattedTemperature(maxTemp)

        sunriseView.textView.text = getTimeFromEpoch(sunrise, timeZone)
        sunsetView.textView.text = getTimeFromEpoch(sunset, timeZone)
        pressureView.textView.text = "$pressure hPa"
        humidityView.textView.text = "$humidity %"
        windView.iconView.rotation = windDirection.toFloat()
        windView.textView.text = getFormattedSpeed(windSpeed)
        visibilityDistanceView.textView.text = getFormattedDistance(visibility.toFloat())

    }
}