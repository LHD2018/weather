package com.lhd.weather2018.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;

import com.lhd.weather2018.MainActivity;
import com.lhd.weather2018.database.AddedCity;
import com.lhd.weather2018.database.ForecastWeather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.air.now.AirNow;
import interfaces.heweather.com.interfacesmodule.bean.weather.Weather;
import interfaces.heweather.com.interfacesmodule.bean.weather.forecast.Forecast;
import interfaces.heweather.com.interfacesmodule.bean.weather.forecast.ForecastBase;
import interfaces.heweather.com.interfacesmodule.bean.weather.lifestyle.LifestyleBase;
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
    public static String getCityName(Weather weather){
        return weather.getBasic().getLocation();
    }

    public static String getUpadateTime(Weather weather){
        return weather.getUpdate().getLoc();
    }

    public static String getDegree(Weather weather){
        return weather.getNow().getTmp();
    }

    public static String getCondInfo(Weather weather){
        return weather.getNow().getCond_txt();
    }

    public static String getDate(ForecastBase forecastBase){
        String date=forecastBase.getDate();
        String[] dateData=date.split("-");
        String dayDate=dateData[1]+"-"+dateData[2];
        return dayDate;
    }
    public static String getForecastInfo(ForecastBase forecastBase){
        return forecastBase.getCond_txt_d();
    }
    public static String getTemRange(ForecastBase forecastBase){
        return forecastBase.getTmp_min()+"~"+forecastBase.getTmp_max();
    }
    public static String getComfor(List<LifestyleBase> lifestyleBaseList){
         for (LifestyleBase lifestyleBase:lifestyleBaseList){
            if (lifestyleBase.getType().equals("comf")){
                return "舒适度："+lifestyleBase.getTxt();
            }
        }
        return null;
    }
    public static String getDrsg(List<LifestyleBase> lifestyleBaseList){
         for (LifestyleBase lifestyleBase:lifestyleBaseList){
            if (lifestyleBase.getType().equals("drsg")){
               return "穿衣指数："+lifestyleBase.getTxt();
            }
        }
        return null;
    }
    public static String getSport(List<LifestyleBase> lifestyleBaseList){
         for (LifestyleBase lifestyleBase:lifestyleBaseList){
            if (lifestyleBase.getType().equals("sport")){
                return "运动建议："+lifestyleBase.getTxt();
            }
        }
        return null;
    }


    public static void getWeather(final Context context, final String cityName){
        final AddedCity addedCity=new AddedCity();
        HeWeather.getWeather(context, cityName, new HeWeather.OnResultWeatherDataListBeansListener() {
            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(List<Weather> list) {
                Weather weather=list.get(0);
                List<LifestyleBase> lifestyleBaseList=weather.getLifestyle();
                addedCity.setCityName(getCityName(weather));
                addedCity.setUpdateTime(getUpadateTime(weather));
                addedCity.setDegree(getDegree(weather));
                addedCity.setWeatherInfo(getCondInfo(weather));
                addedCity.setComfor(getComfor(lifestyleBaseList));
                addedCity.setDrsg(getDrsg(lifestyleBaseList));
                addedCity.setSport(getSport(lifestyleBaseList));
                addedCity.save();
                Utility.setCurrentCity(context,getCityName(weather));
                Intent intent=new Intent(context,MainActivity.class);
                context.startActivity(intent);

            }
        });
        HeWeather.getWeatherForecast(context, cityName, new HeWeather.OnResultWeatherForecastBeanListener() {
            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(List<Forecast> list) {
                Forecast forecast=list.get(0);
                String cityName=forecast.getBasic().getLocation();
                List<ForecastBase> forecastBaselist=forecast.getDaily_forecast();
                for (int i=0;i<forecastBaselist.size();i++){
                    ForecastBase forecastBase=forecastBaselist.get(i);
                    ForecastWeather forecastWeather=new ForecastWeather();
                    forecastWeather.setCityName(cityName);
                    if (i==0){
                        forecastWeather.setDate(getDate(forecastBase)+"（今天）");
                    }
                    if (i==1){
                        forecastWeather.setDate(getDate(forecastBase)+"（明天）");
                    }
                    if (i==2){
                        forecastWeather.setDate(getDate(forecastBase)+"（后天）");
                    }
                    forecastWeather.setCondText(getForecastInfo(forecastBase));
                    forecastWeather.setTemRange(getTemRange(forecastBase));
                    forecastWeather.save();

                }
            }
        });
        HeWeather.getAirNow(context, cityName, new HeWeather.OnResultAirNowBeansListener() {
            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();

            }

            @Override
            public void onSuccess(List<AirNow> list) {
                AirNow airNow=list.get(0);
                addedCity.setAqi(airNow.getAir_now_city().getAqi());
                addedCity.setPm25(airNow.getAir_now_city().getPm25());
                addedCity.save();
            }
        });

    }
    public static boolean setCurrentCity(Context context,String city){
        SharedPreferences.Editor editor= PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("current_city_name",city);
        editor.apply();
        return true;
    }
    public static String getCurrentCity(Context context){
        SharedPreferences preferences= PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("current_city_name",null);
    }
    public static String  updateWeather(Context context,SwipeRefreshLayout refreshLayout){
        String city=getCurrentCity(context);
        refreshLayout.setRefreshing(false);
        return city;

    }
}
