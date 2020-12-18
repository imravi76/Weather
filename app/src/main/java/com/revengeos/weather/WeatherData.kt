package com.revengeos.weather

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.util.Log
import com.revengeos.weather.BuildConfig.DEBUG
import com.revengeos.weather.response.OneCallResponse
import com.revengeos.weather.response.current.CurrentWeatherResponse
import com.revengeos.weather.util.WeatherUtils
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherData {
    val TAG = this.javaClass.toString()

    private val retrofit : Retrofit
    private val service : WeatherService
    private val cacheSize = (5 * 1024 * 1024).toLong()
    private val cache : Cache
    private val okHttpClient : OkHttpClient

    var latitude : Double = Double.MAX_VALUE
    var longitude : Double = Double.MAX_VALUE

    private val listener : WeatherDataListener

    // Must pass applicationContext as context
    constructor(context : Context, listener : WeatherDataListener) {
        this.listener = listener
        cache = Cache(context.cacheDir, cacheSize)
        okHttpClient = OkHttpClient.Builder()
                .cache(cache)
                .addInterceptor { chain ->
                    var request = chain.request()
                    request = if (isInternetAvailable(context))
                        request.newBuilder().header("Cache-Control", "public, max-age=" + 5).build()
                    else
                        request.newBuilder().header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7).build()
                    chain.proceed(request)
                }
                .build()
        retrofit = Retrofit.Builder()
                .baseUrl(WeatherUtils.OPENWEATHER_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build()
        service = retrofit.create(WeatherService::class.java)
    }

    private fun isInternetAvailable(context : Context) : Boolean {
        var result = false
        val connectivityManager =
                context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = connectivityManager.activeNetwork ?: return false
            val actNw =
                    connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.run {
                connectivityManager.activeNetworkInfo?.run {
                    result = when (type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }

                }
            }
        }

        return result
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