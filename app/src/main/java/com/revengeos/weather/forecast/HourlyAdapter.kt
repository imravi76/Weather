package com.revengeos.weather.forecast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.revengeos.weather.R
import com.revengeos.weather.response.Hourly
import com.revengeos.weather.util.WeatherUtils
import com.revengeos.weather.util.WeatherUtils.Companion.getFeelsLikeFormattedTemp
import com.revengeos.weather.util.WeatherUtils.Companion.getFormattedTemperature
import com.revengeos.weather.util.WeatherUtils.Companion.getTimeFromEpoch

class HourlyAdapter(private val dataSet: List<Hourly>) :
        RecyclerView.Adapter<HourlyAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(hourly: Hourly) {
            view.findViewById<TextView>(R.id.temperature).text = getFormattedTemperature(hourly.temp)
            view.findViewById<TextView>(R.id.feels_like).text = getFeelsLikeFormattedTemp(view.context, hourly.feelsLike)
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
        viewHolder.bind(dataSet[position])
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
