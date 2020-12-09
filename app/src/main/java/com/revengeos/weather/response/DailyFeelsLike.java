
package com.revengeos.weather.response;

import com.google.gson.annotations.SerializedName;

public class DailyFeelsLike {
    @SerializedName("day")
    public float day;
    @SerializedName("night")
    public float night;
    @SerializedName("eve")
    public float eve;
    @SerializedName("morn")
    public float morn;

}
