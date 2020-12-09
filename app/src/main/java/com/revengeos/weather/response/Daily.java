package com.revengeos.weather.response;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Daily {
    @SerializedName("dt")
    public int dt;
    @SerializedName("sunrise")
    public int sunrise;
    @SerializedName("sunset")
    public int sunset;
    @SerializedName("temp")
    public DailyTemperature temp;
    @SerializedName("feels_like")
    public DailyFeelsLike feels_like;
    @SerializedName("pressure")
    public int pressure;
    @SerializedName("humidity")
    public int humidity;
    @SerializedName("dew_point")
    public float dewPoint;
    @SerializedName("wind_speed")
    public float windSpeed;
    @SerializedName("wind_deg")
    public int windDeg;
    @SerializedName("weather")
    public List<Weather> weather = null;
    @SerializedName("clouds")
    public int clouds;
    @SerializedName("pop")
    public float pop;
    @SerializedName("uvi")
    public float uvi;
    @SerializedName("rain")
    public float rain;

}
