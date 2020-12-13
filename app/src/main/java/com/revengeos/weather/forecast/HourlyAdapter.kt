package com.revengeos.weather.forecast

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.revengeos.weather.R
import com.revengeos.weather.response.Hourly
import com.revengeos.weather.util.WeatherUtils
import com.revengeos.weather.util.WeatherUtils.Companion.getFeelsLikeFormattedTemp
import com.revengeos.weather.util.WeatherUtils.Companion.getFormattedTemperature
import com.revengeos.weather.util.WeatherUtils.Companion.getTimeFromEpoch
import com.revengeos.weathericons.WeatherIconsHelper

class HourlyAdapter(private val dataSet: List<Hourly>, private val timeShift : Int,
                    private val sunrise : Long, private val sunset : Long) :
        RecyclerView.Adapter<HourlyAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(hourly: Hourly, timeShift: Int, sunrise : Long, sunset : Long) {
            view.findViewById<TextView>(R.id.temperature).text = getFormattedTemperature(hourly.temp)
            view.findViewById<TextView>(R.id.feels_like).text = getFeelsLikeFormattedTemp(view.context, hourly.feelsLike)
            view.findViewById<TextView>(R.id.time).text = getTimeFromEpoch(hourly.dt, timeShift)

            val isDay = hourly.weather[0].icon.takeLast(1) == "d"
            val state = WeatherIconsHelper.mapConditionIconToCode(hourly.weather[0].id, isDay)
            view.findViewById<ImageView>(R.id.condition_icon).setImageResource(WeatherIconsHelper.getDrawable(state, view.context)!!)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.hourly_forecast_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(dataSet[position], timeShift, sunrise, sunset)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
