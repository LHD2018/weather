package com.lhd.weather2018;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lhd.weather2018.database.AddedCity;
import com.lhd.weather2018.database.ForecastWeather;
import com.lhd.weather2018.util.Utility;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseActivity {
    private AddedCity currentCity;
    private String currentCityName;
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdeateTime;
    private Button navButton;
    private TextView degreeNow;
    private TextView weatherInfoNow;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView drsgText;
    private TextView sportText;
    private SwipeRefreshLayout swipeRefresh;
    private DrawerLayout drawerLayout;
    private NavigationView navView;
    private CardView aqiLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        weatherLayout=findViewById(R.id.weather_layout);
        aqiLayout=findViewById(R.id.aqi_layout);
        titleCity=findViewById(R.id.title_city);
        titleUpdeateTime=findViewById(R.id.title_update_time);
        navButton=findViewById(R.id.nav_button);
        degreeNow=findViewById(R.id.degree_text);
        weatherInfoNow=findViewById(R.id.weather_info_text);
        forecastLayout=findViewById(R.id.forecast_layout);
        aqiText=findViewById(R.id.aqi_text);
        pm25Text=findViewById(R.id.pm25_text);
        comfortText=findViewById(R.id.comfort_text);
        drsgText=findViewById(R.id.drsg_text);
        sportText=findViewById(R.id.sport_text);
        swipeRefresh=findViewById(R.id.swipe_refresh);
        drawerLayout=findViewById(R.id.drawer_layout);
        navView=findViewById(R.id.nav_view);
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //更新数据
                if (Utility.getCurrentCity(MainActivity.this)!=null){
                    if (Utility.isNetworkAvailable(MainActivity.this)){
                        Utility.updateWeather(MainActivity.this,swipeRefresh);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        showWeather(Utility.getCurrentCity(MainActivity.this));
                    }else{
                        Toast.makeText(MainActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                }else{
                    swipeRefresh.setRefreshing(false);
                }
            }
        });

        currentCityName= Utility.getCurrentCity(this);
        List<String> permissionList=new ArrayList<>();
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_PHONE_STATE)!= PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (!permissionList.isEmpty()){
            String[] permissions=permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this,permissions,1);
        }
        if (currentCityName!=null){
            showWeather(currentCityName);
        }else{
            drawerLayout.openDrawer(Gravity.START);
        }
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(Gravity.START);
            }
        });
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId=item.getItemId();
                if (itemId== R.id.manager_cities){
                    Intent intent=new Intent(MainActivity.this,ManageCityActivity.class);
                    startActivity(intent);
                    drawerLayout.closeDrawers();

                }
                if (itemId== R.id.setting){

                }
                if (itemId== R.id.about){

                }
                return true;
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode==KeyEvent.KEYCODE_BACK){
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        currentCityName=Utility.getCurrentCity(MainActivity.this);
        if (currentCityName!=null){
            showWeather(currentCityName);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case 1:
                if (grantResults.length>0){
                    for (int result:grantResults){
                        if (result!=PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"你需要同意这些权限",Toast.LENGTH_SHORT).show();
                            finish();
                            return;
                        }
                    }
                }
                break;
                default:
                    break;
        }
    }

    private void showWeather(String city){
        aqiLayout.setVisibility(View.VISIBLE);
        currentCity= LitePal.where("cityName=?",city).find(AddedCity.class).get(0);
        String cityName=currentCity.getCityName();
        String updateTime=currentCity.getUpdateTime();
        String degree=currentCity.getDegree();
        String weatherInfo=currentCity.getWeatherInfo();
        titleCity.setText(cityName);
        titleUpdeateTime.setText(updateTime);
        degreeNow.setText(degree+"°C");
        weatherInfoNow.setText(weatherInfo);
        List<ForecastWeather> forecastList=LitePal.where("cityName=?",cityName).find(ForecastWeather.class);
        forecastLayout.removeAllViews();
        for (ForecastWeather forecast:forecastList){
            View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText=view.findViewById(R.id.date_text);
            TextView infoText=view.findViewById(R.id.info_text);
            TextView temRange=view.findViewById(R.id.max_min);
            dateText.setText(forecast.getDate());
            infoText.setText(forecast.getCondText());
            temRange.setText(forecast.getTemRange());
            forecastLayout.addView(view);
        }
        if (currentCity.getAqi()!=null){
            aqiText.setText(currentCity.getAqi());
            pm25Text.setText(currentCity.getPm25());
        }else{
            aqiLayout.setVisibility(View.GONE);
        }
        String comfort=currentCity.getComfor();
        String drsg=currentCity.getDrsg();
        String sport=currentCity.getSport();
        comfortText.setText(comfort);
        drsgText.setText(drsg);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }
}
