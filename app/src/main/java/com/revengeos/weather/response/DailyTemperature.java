
package com.revengeos.weather.response;

import com.google.gson.annotations.SerializedName;

public class DailyTemperature {
    @SerializedName("day")
    public float day;
    @SerializedName("min")
    public float min;
    @SerializedName("max")
    public float max;
    @SerializedName("night")
    public float night;
    @SerializedName("eve")
    public float eve;
    @SerializedName("morn")
    public float morn;

}
