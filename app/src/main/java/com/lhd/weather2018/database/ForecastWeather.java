package com.lhd.weather2018.database;

import org.litepal.crud.LitePalSupport;

public class ForecastWeather extends LitePalSupport {
    private String cityName;
    private String date;
    private String condText;
    private String temRange;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCondText() {
        return condText;
    }

    public void setCondText(String condText) {
        this.condText = condText;
    }

    public String getTemRange() {
        return temRange;
    }

    public void setTemRange(String temRange) {
        this.temRange = temRange;
    }
}
