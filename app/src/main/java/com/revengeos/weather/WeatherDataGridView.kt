package com.revengeos.weather

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.annotation.Nullable
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayout
import com.revengeos.weather.util.WeatherUtils.Companion.getFormattedDistance
import com.revengeos.weather.util.WeatherUtils.Companion.getFormattedSpeed
import com.revengeos.weather.util.WeatherUtils.Companion.getFormattedTemperature
import com.revengeos.weather.util.WeatherUtils.Companion.getTimeFromEpoch
import kotlin.math.roundToInt

class WeatherDataGridView : FlexboxLayout {

    private val sunriseView: WeatherGridItemView
    private val sunsetView: WeatherGridItemView
    private val pressureView: WeatherGridItemView
    private val humidityView: WeatherGridItemView
    private val windView: WeatherGridItemView
    private val visibilityDistanceView : WeatherGridItemView
    private val precipitationsView : WeatherGridItemView
    private val minTempView: WeatherGridItemView
    private val maxTempView: WeatherGridItemView

    constructor (context: Context) : this(context, null) {
    }

    constructor(context: Context, @Nullable attrs: AttributeSet?) : super(context, attrs) {
        flexWrap = FlexWrap.WRAP
        LayoutInflater.from(context).inflate(R.layout.weather_data_grid, this, true)

        sunriseView = findViewById(R.id.current_sunrise)
        sunsetView = findViewById(R.id.current_sunset)
        pressureView = findViewById(R.id.current_pressure)
        humidityView = findViewById(R.id.current_humidity)
        windView = findViewById(R.id.current_wind)
        visibilityDistanceView = findViewById(R.id.current_visibility)
        precipitationsView = findViewById(R.id.current_precipitations)
        minTempView = findViewById(R.id.current_min_temp)
        maxTempView = findViewById(R.id.current_max_temp)
    }

    fun updateData(weatherGridData : WeatherGridData) {

        minTempView.valueView.text = getFormattedTemperature(weatherGridData.minTemp)
        maxTempView.valueView.text = getFormattedTemperature(weatherGridData.maxTemp)

        sunriseView.valueView.text = getTimeFromEpoch(weatherGridData.sunrise, weatherGridData.timeZone)
        sunsetView.valueView.text = getTimeFromEpoch(weatherGridData.sunset, weatherGridData.timeZone)
        pressureView.valueView.text = "${weatherGridData.pressure} hPa"
        humidityView.valueView.text = "${weatherGridData.humidity} %"
        windView.valueView.text = getFormattedSpeed(weatherGridData.windSpeed)
        if (weatherGridData.visibility == null) {
            visibilityDistanceView.visibility = GONE
        } else {
            visibilityDistanceView.valueView.text = getFormattedDistance(weatherGridData.visibility.toFloat())
            visibilityDistanceView.visibility = VISIBLE
        }
        if (weatherGridData.precipitations == null) {
            precipitationsView.visibility = GONE
        } else {
            precipitationsView.valueView.text = "${(weatherGridData.precipitations * 100).roundToInt()} %"
            precipitationsView.visibility = View.VISIBLE
        }
    }
}