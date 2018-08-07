package com.lhd.weather.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.DisplayMetrics;
import android.view.WindowManager;
import android.widget.Toast;

import com.lhd.weather.MainActivity;
import com.lhd.weather.database.AddedCity;
import com.lhd.weather.database.ForecastWeather;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import interfaces.heweather.com.interfacesmodule.bean.air.now.AirNow;
import interfaces.heweather.com.interfacesmodule.bean.weather.Weather;
import interfaces.heweather.com.interfacesmodule.bean.weather.forecast.Forecast;
import interfaces.heweather.com.interfacesmodule.bean.weather.forecast.ForecastBase;
import interfaces.heweather.com.interfacesmodule.bean.weather.lifestyle.LifestyleBase;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;

public class Utility {
    public static final int FINISH_OK=1;

    public static boolean flag1;
    public static boolean flag2;
    public static boolean flag3;
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
    public static String getWeatherCode(Weather weather){
        return weather.getNow().getCond_code();
    }


    public static void getWeather(final Context context, final String cityName){
        if (cityName==null){
            return;
        }
        flag1=flag2=flag3=false;
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
                        default:
                            break;

                }
            }
        };
        final AddedCity addedCity=new AddedCity();
        HeWeather.getWeather(context, cityName, new HeWeather.OnResultWeatherDataListBeansListener() {
            @Override
            public void onError(Throwable throwable) {
                String[] errors=throwable.toString().split("msg");
                Toast.makeText(context,errors[errors.length-1],Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(List<Weather> list) {
                Weather weather=list.get(0);
                List<LifestyleBase> lifestyleBaseList=weather.getLifestyle();
                addedCity.setCityName(getCityName(weather));
                addedCity.setUpdateTime(getUpadateTime(weather));
                addedCity.setDegree(getDegree(weather));
                addedCity.setWeatherInfo(getCondInfo(weather));
                addedCity.setWeatherCode(getWeatherCode(weather));
                addedCity.setComfor(getComfor(lifestyleBaseList));
                addedCity.setDrsg(getDrsg(lifestyleBaseList));
                addedCity.setSport(getSport(lifestyleBaseList));
                addedCity.save();
                flag1=true;
                Message message=new Message();
                message.what=FINISH_OK;
                handler.sendMessage(message);
            }
        });
        HeWeather.getWeatherForecast(context, cityName, new HeWeather.OnResultWeatherForecastBeanListener() {
            @Override
            public void onError(Throwable throwable) {
                flag2=true;
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(List<Forecast> list) {
                Forecast forecast=list.get(0);
                List<ForecastBase> forecastBaselist=forecast.getDaily_forecast();
                for (int i=0;i<forecastBaselist.size();i++){
                    ForecastBase forecastBase=forecastBaselist.get(i);
                    ForecastWeather forecastWeather=new ForecastWeather();
                    forecastWeather.setfId(i);
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
                Message message=new Message();
                message.what=FINISH_OK;
                handler.sendMessage(message);
            }
        });
        HeWeather.getAirNow(context, cityName, new HeWeather.OnResultAirNowBeansListener() {
            @Override
            public void onError(Throwable throwable) {
                flag3=true;
               throwable.printStackTrace();
            }

            @Override
            public void onSuccess(List<AirNow> list) {
                AirNow airNow=list.get(0);
                addedCity.setAqi(airNow.getAir_now_city().getAqi());
                addedCity.setPm25(airNow.getAir_now_city().getPm25());
                addedCity.save();
                flag3=true;
                Message message=new Message();
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


   /* public static int dp2px(Context context,float dp)
    {
        return (int ) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, context.getResources().getDisplayMetrics());
    }*/

    public static int getScreenWidth(Context context)
    {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE );
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics( outMetrics);
        return outMetrics .widthPixels ;
    }

    public static void updateWeather(Context context){
        final String city=getCurrentCity(context);
        if (city==null){
            return;
        }
        HeWeather.getWeather(context, city, new HeWeather.OnResultWeatherDataListBeansListener() {

            @Override
            public void onError(Throwable throwable) {
                flag1=false;
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
                List<ForecastWeather>  idList=LitePal.where("cityName=?",city).select("fId").find(ForecastWeather.class);
                int lastId=idList.get(idList.size()-1).getfId();
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
                        String fId=i+"";
                        forecastWeather.updateAll("fId=?",fId);
                    }else{
                        forecastWeather.save();
                    }
                }
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
            }
        });

    }
    public static boolean isDay(){
        SimpleDateFormat sdf=new SimpleDateFormat("HH");
        String hour=sdf.format(new Date());
        int k=Integer.parseInt(hour);
        if (k>=6&&k<=18){
            return true;
        }
        return false;
    }
    public static String getPoetry(final Context context){
       final List<String> poetryList=new ArrayList<>();
       String poetry=null;
        try {
            InputStream inputStream=context.getAssets().open("poetries.txt");
            BufferedReader reader=new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while((line=reader.readLine())!=null){
                if (line.equals("")){
                    continue;
                }
                poetryList.add(line);
            }
            inputStream.close();
            Random random=new Random();
            int i=random.nextInt(poetryList.size()-1);
            poetry=poetryList.get(i);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return poetry;
    }

    public static Bitmap getWeatherIco(Context context,String weatherCode){
        Bitmap bitmap=null;
        AssetManager manager=context.getAssets();
        String fileName;
        if (isDay()){
            fileName=weatherCode+".png";
        }else{
            fileName=weatherCode+"n.png";
        }
        try {
            InputStream is=manager.open("weather_ico/"+fileName);
            bitmap= BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;

    }
    public static void setApiKey(Context context,String apiKey){
        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString("api_key",apiKey);
        editor.apply();
    }
    public static String getApiKey(Context context){
        SharedPreferences preferences=PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString("api_key",null);
    }

}
