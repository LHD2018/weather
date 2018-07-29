package com.lhd.weather2018.database;

import org.litepal.crud.LitePalSupport;

import interfaces.heweather.com.interfacesmodule.bean.air.now.AirNow;
import interfaces.heweather.com.interfacesmodule.bean.weather.Weather;
import interfaces.heweather.com.interfacesmodule.bean.weather.forecast.Forecast;
import interfaces.heweather.com.interfacesmodule.bean.weather.lifestyle.Lifestyle;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;

public class AddedCity extends LitePalSupport {

    private String cityName;
    private String updateTime;
    private String degree;
    private String weatherInfo;
    private String weatherCode;

    private String aqi;
    private String pm25;

    private String comfor;
    private String drsg;
    private String sport;

    public String getWeatherCode() {
        return weatherCode;
    }

    public void setWeatherCode(String weatherCode) {
        this.weatherCode = weatherCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getWeatherInfo() {
        return weatherInfo;
    }

    public void setWeatherInfo(String weatherInfo) {
        this.weatherInfo = weatherInfo;
    }

    public String getAqi() {
        return aqi;
    }

    public void setAqi(String aqi) {
        this.aqi = aqi;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public String getComfor() {
        return comfor;
    }

    public void setComfor(String comfor) {
        this.comfor = comfor;
    }

    public String getDrsg() {
        return drsg;
    }

    public void setDrsg(String drsg) {
        this.drsg = drsg;
    }

    public String getSport() {
        return sport;
    }

    public void setSport(String sport) {
        this.sport = sport;
    }
}
