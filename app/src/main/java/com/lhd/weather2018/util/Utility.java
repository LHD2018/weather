package com.lhd.weather2018.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.ConditionVariable;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.lhd.weather2018.BaseActivity;
import com.lhd.weather2018.MainActivity;
import com.lhd.weather2018.database.AddedCity;
import com.lhd.weather2018.database.ForecastWeather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.logging.Handler;

import interfaces.heweather.com.interfacesmodule.bean.air.now.AirNow;
import interfaces.heweather.com.interfacesmodule.bean.weather.Weather;
import interfaces.heweather.com.interfacesmodule.bean.weather.forecast.Forecast;
import interfaces.heweather.com.interfacesmodule.bean.weather.forecast.ForecastBase;
import interfaces.heweather.com.interfacesmodule.bean.weather.lifestyle.LifestyleBase;
import interfaces.heweather.com.interfacesmodule.view.HeConfig;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

public class Utility {
    public static final int FINISH_OK=1;
    public static final int INTERNET_ERROR=2;

    public static boolean flag1=false;
    public static boolean flag2=false;
    public static boolean flag3=false;
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
        final android.os.Handler handler = new android.os.Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case FINISH_OK:
                        if (flag1&&flag2&&flag3){
                            Utility.setCurrentCity(context,cityName);
                            Intent intent=new Intent(context,MainActivity.class);
                            context.startActivity(intent);
                        }
                        break;
                    case INTERNET_ERROR:
                        Toast.makeText(context,"网络异常",Toast.LENGTH_SHORT).show();
                        break;
                        default:
                            break;

                }
            }
        };
        final AddedCity addedCity=new AddedCity();
        HeWeather.getWeather(context, cityName, new HeWeather.OnResultWeatherDataListBeansListener() {
            Message message=new Message();
            @Override
            public void onError(Throwable throwable) {
                message.what=INTERNET_ERROR;
                handler.sendMessage(message);
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
                flag1=true;
                message.what=FINISH_OK;
                handler.sendMessage(message);
            }
        });
        HeWeather.getWeatherForecast(context, cityName, new HeWeather.OnResultWeatherForecastBeanListener() {
            Message message=new Message();
            @Override
            public void onError(Throwable throwable) {
                message.what=INTERNET_ERROR;
                handler.sendMessage(message);
            }

            @Override
            public void onSuccess(List<Forecast> list) {
                Forecast forecast=list.get(0);
                List<ForecastBase> forecastBaselist=forecast.getDaily_forecast();
                for (int i=0;i<forecastBaselist.size();i++){
                    ForecastBase forecastBase=forecastBaselist.get(i);
                    ForecastWeather forecastWeather=new ForecastWeather();
                    forecastWeather.setId(i);
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
                flag2=true;
                message.what=FINISH_OK;
                handler.sendMessage(message);
            }
        });
        HeWeather.getAirNow(context, cityName, new HeWeather.OnResultAirNowBeansListener() {
            Message message=new Message();
            @Override
            public void onError(Throwable throwable) {
                message.what=INTERNET_ERROR;
                handler.sendMessage(message);

            }

            @Override
            public void onSuccess(List<AirNow> list) {
                AirNow airNow=list.get(0);
                addedCity.setAqi(airNow.getAir_now_city().getAqi());
                addedCity.setPm25(airNow.getAir_now_city().getPm25());
                addedCity.save();
                flag3=true;
                message.what=FINISH_OK;
                handler.sendMessage(message);
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
    public static String  updateWeather(Context context,final SwipeRefreshLayout refreshLayout){
        final android.os.Handler handler = new android.os.Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case FINISH_OK:
                        if (flag1&&flag2&&flag3){
                            refreshLayout.setRefreshing(false);
                        }
                        break;
                }
            }
        };
        final String city=getCurrentCity(context);
        HeWeather.getWeather(context, city, new HeWeather.OnResultWeatherDataListBeansListener() {

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(List<Weather> list) {
                Weather weather=list.get(0);
                List<LifestyleBase> lifestyleBaseList=weather.getLifestyle();
                AddedCity addedCity=new AddedCity();
                addedCity.setUpdateTime(getUpadateTime(weather));
                addedCity.setDegree(getDegree(weather));
                addedCity.setWeatherInfo(getCondInfo(weather));
                addedCity.setComfor(getComfor(lifestyleBaseList));
                addedCity.setDrsg(getDrsg(lifestyleBaseList));
                addedCity.setSport(getSport(lifestyleBaseList));
                addedCity.updateAll("cityName=?",city);
                flag1=true;
                Message message=new Message();
                message.what=FINISH_OK;
                handler.sendMessage(message);
            }
        });
        HeWeather.getWeatherForecast(context, city, new HeWeather.OnResultWeatherForecastBeanListener() {
            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(List<Forecast> list) {
                Forecast forecast=list.get(0);
                List<ForecastBase> forecastBaselist=forecast.getDaily_forecast();
                List<ForecastWeather>  idList=LitePal.where("cityName=?",city).select("id").find(ForecastWeather.class);
                int lastId=idList.get(idList.size()-1).getId();
                for (int i=0;i<forecastBaselist.size();i++){
                    ForecastBase forecastBase=forecastBaselist.get(i);
                    ForecastWeather forecastWeather=new ForecastWeather();
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
                    if (i<=lastId){
                        forecastWeather.updateAll();
                    }else{
                        forecastWeather.save();
                    }
                }
                flag2=true;
                Message message=new Message();
                message.what=FINISH_OK;
                handler.sendMessage(message);
            }
        });
        HeWeather.getAirNow(context, city, new HeWeather.OnResultAirNowBeansListener() {
            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();

            }

            @Override
            public void onSuccess(List<AirNow> list) {
                AirNow airNow=list.get(0);
                AddedCity addedCity=new AddedCity();
                addedCity.setAqi(airNow.getAir_now_city().getAqi());
                addedCity.setPm25(airNow.getAir_now_city().getPm25());
                addedCity.updateAll("cityName=?",city);
                flag3=true;
                Message message=new Message();
                message.what=FINISH_OK;
                handler.sendMessage(message);
            }
        });
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return city;

    }
    public static boolean isExist(String city){
        List<AddedCity> addedCities = LitePal.where("cityName=?",city).find(AddedCity.class);
        if (!addedCities.isEmpty()){
            return true;
        }else{
            return false;
        }
    }
    /**
     * 获取当前网络状态(是否可用)
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connManager != null) {
            NetworkInfo activeNetInfo = connManager.getActiveNetworkInfo();
            if (activeNetInfo != null) {
                return activeNetInfo.isAvailable();
            }
        }
        return  false;
    }


}
