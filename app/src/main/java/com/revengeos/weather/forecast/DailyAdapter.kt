package com.revengeos.weather.forecast

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.revengeos.weather.R
import com.revengeos.weather.response.Daily
import com.revengeos.weather.util.WeatherUtils
import com.revengeos.weathericons.WeatherIconsHelper

class DailyAdapter(private val dataSet: List<Daily>, private val timeZone : Int) :
        RecyclerView.Adapter<DailyAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */
    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        fun bind(daily: Daily, timeZone : Int) {
            view.findViewById<TextView>(R.id.temperature).text = WeatherUtils.getFormattedTemperature(daily.temp.day)
            if (adapterPosition == 0) {
                view.findViewById<TextView>(R.id.day).text = view.context.getString(R.string.tomorrow_title)
            } else {
                view.findViewById<TextView>(R.id.day).text = WeatherUtils.getWeekDayFromEpoch(daily.dt, timeZone).capitalize()
            }
            view.findViewById<TextView>(R.id.extra_temp_data).text = "Min ${WeatherUtils.getFormattedTemperature(daily.temp.min)} Max ${WeatherUtils.getFormattedTemperature(daily.temp.max)}"

            val isDay = daily.weather[0].icon.takeLast(1) == "d"
            val state = WeatherIconsHelper.mapConditionIconToCode(daily.weather[0].id, isDay)
            view.findViewById<ImageView>(R.id.condition_icon).setImageResource(WeatherIconsHelper.getDrawable(state, view.context)!!)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.daily_forecast_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.bind(dataSet[position], timeZone)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = dataSet.size

}
