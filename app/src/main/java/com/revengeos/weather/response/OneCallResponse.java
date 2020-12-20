package com.revengeos.weather.response;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class OneCallResponse {

    @SerializedName("lat")
    public float lat;
    @SerializedName("lon")
    public float lon;
    @SerializedName("timezone")
    public String timezone;
    @SerializedName("timezone_offset")
    public int timezoneOffset;
    @SerializedName("current")
    public Current current;
    /* @SerializedName("minutely")
    public List<Minutely> minutely = null;*/
    @SerializedName("hourly")
    public List<Hourly> hourly = null;
    @SerializedName("daily")
    public List<Daily> daily = null;

}
