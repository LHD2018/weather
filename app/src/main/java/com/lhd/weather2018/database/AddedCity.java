package com.lhd.weather2018.database;

import org.litepal.crud.LitePalSupport;

import interfaces.heweather.com.interfacesmodule.bean.air.now.AirNow;
import interfaces.heweather.com.interfacesmodule.bean.weather.Weather;
import interfaces.heweather.com.interfacesmodule.bean.weather.forecast.Forecast;
import interfaces.heweather.com.interfacesmodule.bean.weather.lifestyle.Lifestyle;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;

public class AddedCity extends LitePalSupport {
    private int id;
    private Weather nowWeather;
    private Forecast forecast;
    private Lifestyle lifestyle;
    private AirNow airNow;

    public AirNow getAirNow() {
        return airNow;
    }

    public void setAirNow(AirNow airNow) {
        this.airNow = airNow;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Weather getNowWeather() {
        return nowWeather;
    }

    public void setNowWeather(Weather nowWeather) {
        this.nowWeather = nowWeather;
    }

    public Forecast getForecast() {
        return forecast;
    }

    public void setForecast(Forecast forecast) {
        this.forecast = forecast;
    }

    public Lifestyle getLifestyle() {
        return lifestyle;
    }

    public void setLifestyle(Lifestyle lifestyle) {
        this.lifestyle = lifestyle;
    }
}
