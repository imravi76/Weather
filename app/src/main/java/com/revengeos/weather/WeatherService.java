package com.revengeos.weather;

import com.revengeos.weather.response.OneCallResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {

    @GET("data/2.5/weather?")
    Call<WeatherResponse> getCurrentWeatherData(@Query("lat") String lat, @Query("lon") String lon, @Query("appid") String app_id);

    @GET("data/2.5/onecall?")
    Call<OneCallResponse> getOneCallData(@Query("lat") String lat, @Query("lon") String lon, @Query("appid") String app_id);

}
