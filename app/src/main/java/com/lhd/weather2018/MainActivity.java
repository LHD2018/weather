package com.lhd.weather2018;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lhd.weather2018.database.AddedCity;
import com.lhd.weather2018.database.ForecastWeather;
import com.lhd.weather2018.service.UpdateWeatherService;
import com.lhd.weather2018.util.Utility;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.view.HeConfig;

public class MainActivity extends AppCompatActivity {
    private NavigationView navigationView;

    private AddedCity currentCity;
    private String currentCityName;
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdeateTime;
    private Button navButton;
    private TextView degreeNow;
    private TextView weatherInfoNow;
    private ImageView weatherPic;
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

    private ImageView navWeatherPic;
    private TextView navPoetry;
    private TextView navCityName;
    private TextView navDegree;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT>=21){
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_main);
        String userId="HE1807171032311823";
        String key="24a2d899122b4526b7299924f133c599";
        HeConfig.init(userId,key);
        HeConfig.switchToFreeServerNode();
        boolean isDay=Utility.isDay();
        AppCompatDelegate.setDefaultNightMode(isDay ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES);
        navigationView=findViewById(R.id.nav_view);
        View headLayout=navigationView.inflateHeaderView(R.layout.nav_head);
        navCityName=headLayout.findViewById(R.id.nav_city);
        navPoetry=headLayout.findViewById(R.id.nav_update_time);
        navDegree=headLayout.findViewById(R.id.nav_degree);
        navWeatherPic=headLayout.findViewById(R.id.nav_weather_pic);
        weatherLayout=findViewById(R.id.weather_layout);
        aqiLayout=findViewById(R.id.aqi_layout);
        titleCity=findViewById(R.id.title_city);
        titleUpdeateTime=findViewById(R.id.title_update_time);
        navButton=findViewById(R.id.nav_button);
        degreeNow=findViewById(R.id.degree_text);
        weatherInfoNow=findViewById(R.id.weather_info_text);
        weatherPic=findViewById(R.id.weather_pic);
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
                        new UpdateWeatherTask(new UpdateWeatherTask.UpdateWeatherListener() {
                            @Override
                            public void onSuccess() {
                                swipeRefresh.setRefreshing(false);
                                showWeather(Utility.getCurrentCity(MainActivity.this));
                            }

                            @Override
                            public void onFailed() {
                                Toast.makeText(MainActivity.this,"更新失败",Toast.LENGTH_SHORT).show();
                                swipeRefresh.setRefreshing(false);
                            }
                        }).execute(MainActivity.this);
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
                    Intent intent=new Intent(MainActivity.this,SettingActivity.class);
                    startActivity(intent);

                }
                if (itemId== R.id.about){
                    Intent intent=new Intent(MainActivity.this,AboutActivity.class);
                    startActivity(intent);

                }
                return true;
            }
        });
        Intent intent=new Intent(this, UpdateWeatherService.class);
        startService(intent);
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
        List<AddedCity>currentCityList =LitePal.where("cityName=?",city).find(AddedCity.class);
        if (currentCityList.isEmpty()){
            return;
        }
        currentCity=currentCityList.get(0);
        String cityName=currentCity.getCityName();
        String updateTime=currentCity.getUpdateTime();
        String degree=currentCity.getDegree();
        String weatherInfo=currentCity.getWeatherInfo();
        String weatherCode=currentCity.getWeatherCode();
        Bitmap weatherIco=Utility.getWeatherIco(MainActivity.this,weatherCode);
        weatherPic.setImageBitmap(weatherIco);
        navWeatherPic.setImageBitmap(weatherIco);
        titleCity.setText(cityName);
        navCityName.setText(cityName);
        titleUpdeateTime.setText(updateTime);
        navPoetry.setText(Utility.getPoetry(MainActivity.this));
        degreeNow.setText(degree+"°C");
        navDegree.setText(degree+"°C");
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
