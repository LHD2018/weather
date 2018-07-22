package com.lhd.weather2018.util;

import android.app.Activity;
import android.content.Context;

import com.lhd.weather2018.database.AddedCity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.Lang;
import interfaces.heweather.com.interfacesmodule.bean.Unit;
import interfaces.heweather.com.interfacesmodule.bean.air.now.AirNow;
import interfaces.heweather.com.interfacesmodule.bean.weather.Weather;
import interfaces.heweather.com.interfacesmodule.bean.weather.forecast.Forecast;
import interfaces.heweather.com.interfacesmodule.bean.weather.now.Now;
import interfaces.heweather.com.interfacesmodule.view.HeConfig;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

public class Utility {
    public static List<String> handleHotCityResponce(String responce){
        List<String> list=new ArrayList<>();
        try {
            JSONObject object1=new JSONObject(responce);
            JSONArray array1=object1.getJSONArray("HeWeather6");
            JSONObject object2=array1.getJSONObject(0);
            JSONArray array2=object2.getJSONArray("basic");
            for (int i=0;i<array2.length();i++){
                JSONObject object3=array2.getJSONObject(i);
                list.add(object3.getString("location"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }
    public static void getWeather(Context context, final String cityName){
        String userId="HE1807171032311823";
        String key="24a2d899122b4526b7299924f133c599";
        HeConfig.init(userId,key);
        HeConfig.switchToFreeServerNode();
        final AddedCity addedCity=new AddedCity();
        HeWeather.getWeather(context, cityName, new HeWeather.OnResultWeatherDataListBeansListener() {
            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(List<Weather> list) {
                addedCity.setNowWeather(list.get(0));
                addedCity.save();
            }
        });
        HeWeather.getWeatherForecast(context, cityName, new HeWeather.OnResultWeatherForecastBeanListener() {
            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(List<Forecast> list) {
                addedCity.setForecast(list.get(0));
                addedCity.save();
            }
        });
        HeWeather.getAirNow(context, cityName, new HeWeather.OnResultAirNowBeansListener() {
            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();

            }

            @Override
            public void onSuccess(List<AirNow> list) {
                addedCity.setAirNow(list.get(0));
                addedCity.save();
            }
        });

    }
}
