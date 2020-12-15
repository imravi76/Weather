package com.revengeos.weather

import android.util.Log
import com.revengeos.weather.BuildConfig.DEBUG
import com.revengeos.weather.response.OneCallResponse
import com.revengeos.weather.response.current.CurrentWeatherResponse
import com.revengeos.weather.util.WeatherUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherData {
    val TAG = this.javaClass.toString()

    private val retrofit : Retrofit
    private val service : WeatherService

    var latitude : Double = Double.MAX_VALUE
    var longitude : Double = Double.MAX_VALUE

    private val listener : WeatherDataListener

    constructor(listener : WeatherDataListener) {
        retrofit = Retrofit.Builder()
                .baseUrl(WeatherUtils.OPENWEATHER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        service = retrofit.create(WeatherService::class.java)
        this.listener = listener
    }

    fun updateCurrentWeatherData() {
        if (latitude == Double.MAX_VALUE || longitude == Double.MAX_VALUE) {
            if (DEBUG) {
                Log.d(TAG, "Location coordinates are not set")
            }
            listener.onCurrentWeatherDataUpdated(null)
        }
        val callCurrentWeather = service.getCurrentWeatherData(latitude.toString(), longitude.toString(), WeatherUtils.API_KEY)
        callCurrentWeather.enqueue(object : Callback<CurrentWeatherResponse?> {
            override fun onResponse(call: Call<CurrentWeatherResponse?>, response: Response<CurrentWeatherResponse?>) {
                if (response.code() == 200) {
                    listener.onCurrentWeatherDataUpdated(response.body()!!)
                }
            }

            override fun onFailure(call: Call<CurrentWeatherResponse?>, t: Throwable) {
                Log.d(TAG, t.toString())
            }
        })
    }

    fun updateOneCallWeatherData() {
        if (latitude == Double.MAX_VALUE || longitude == Double.MAX_VALUE) {
            if (DEBUG) {
                Log.d(TAG, "Location coordinates are not set")
            }
            listener.onOneCallWeatherDataUpdated(null)
        }
        val callForecast = service.getOneCallData(latitude.toString(), longitude.toString(), WeatherUtils.API_KEY)
        callForecast.enqueue(object : Callback<OneCallResponse?> {
            override fun onResponse(call: Call<OneCallResponse?>, response: Response<OneCallResponse?>) {
                if (response.code() == 200) {
                    listener.onOneCallWeatherDataUpdated(response.body()!!)
                }
            }

            override fun onFailure(call: Call<OneCallResponse?>, t: Throwable) {
                Log.d(TAG, t.toString())
            }
        })
    }

    interface WeatherDataListener {
        public fun onCurrentWeatherDataUpdated(currentWeatherResponse: CurrentWeatherResponse?)
        public fun onOneCallWeatherDataUpdated(oneCallResponse: OneCallResponse?)
    }
}