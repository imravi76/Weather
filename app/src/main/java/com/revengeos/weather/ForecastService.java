package com.revengeos.weather;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ForecastService {

    @SerializedName("lat")
    public int lat;
    @SerializedName("lon")
    public int lon;
    @SerializedName("timezone")
    public String timezone;

    @SerializedName("current")
    public Current current;

    @SerializedName("minutely")
    public ArrayList<Minutely> minutely = new ArrayList<Minutely>();

    @SerializedName("hourly")
    public ArrayList<Hourly> hourly = new ArrayList<Hourly>();

    @SerializedName("daily")
    public ArrayList<Daily> daily = new ArrayList<Daily>();

}

class Current    {
    public int dt;
    public int sunrise;
    public int sunset;
    public double temp;
    public double feels_like;
    public int pressure;
    public int humidity;
    public double dew_point;
    public int uvi;
    public int clouds;
    public int visibility;
    public double wind_speed;
    public int wind_deg;
    public double wind_gust;
    public List<Weather> weather;
}

class Minutely    {
    public int dt;
    public int precipitation;
}

class Hourly    {
    public int dt ;
    public double temp ;
    public double feels_like ;
    public int pressure ;
    public int humidity ;
    public double dew_point ;
    public double uvi ;
    public int clouds ;
    public int visibility ;
    public double wind_speed ;
    public int wind_deg ;
    public List<Weather2> weather ;
    public double pop ;
    public Rain rain ;
}

class Weather2    {
    public int id ;
    public String main ;
    public String description ;
    public String icon ;
}

class Daily    {
    public int dt ;
    public int sunrise ;
    public int sunset ;
    public Temp temp ;
    public FeelsLike feels_like ;
    public int pressure ;
    public int humidity ;
    public double dew_point ;
    public double wind_speed ;
    public int wind_deg ;
    public List<Weather3> weather ;
    public int clouds ;
    public double pop ;
    public double rain ;
    public double uvi ;
}

class Weather3    {
    public int id ;
    public String main ;
    public String description ;
    public String icon ;
}

class Temp    {
    public double day ;
    public double min ;
    public double max ;
    public double night ;
    public double eve ;
    public double morn ;
}

class FeelsLike    {
    public double day ;
    public double night ;
    public double eve ;
    public double morn ;
}
