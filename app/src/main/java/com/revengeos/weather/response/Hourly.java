package com.revengeos.weather.response;

import java.util.List;

import com.google.gson.annotations.SerializedName;

public class Hourly {
    @SerializedName("dt")
    public long dt;
    @SerializedName("temp")
    public float temp;
    @SerializedName("feels_like")
    public float feelsLike;
    @SerializedName("pressure")
    public int pressure;
    @SerializedName("humidity")
    public int humidity;
    @SerializedName("dew_point")
    public float dewPoint;
    @SerializedName("uvi")
    public float uvi;
    @SerializedName("clouds")
    public int clouds;
    @SerializedName("visibility")
    public int visibility;
    @SerializedName("wind_speed")
    public float windSpeed;
    @SerializedName("wind_deg")
    public int windDeg;
    @SerializedName("weather")
    public List<Weather> weather = null;
    @SerializedName("pop")
    public int pop;
}