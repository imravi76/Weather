package com.revengeos.weather.response.current;

import com.google.gson.annotations.SerializedName;

public class Main {
    @SerializedName("temp")
    public float temp;
    @SerializedName("humidity")
    public int humidity;
    @SerializedName("pressure")
    public int pressure;
    @SerializedName("temp_min")
    public float temp_min;
    @SerializedName("temp_max")
    public float temp_max;
    @SerializedName("feels_like")
    public float feels_like;
}
