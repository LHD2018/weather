package com.lhd.weather2018;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.ColorSpace;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.lhd.weather2018.database.AddedCity;

import org.litepal.LitePal;
import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.weather.Weather;
import interfaces.heweather.com.interfacesmodule.bean.weather.forecast.ForecastBase;
import interfaces.heweather.com.interfacesmodule.bean.weather.lifestyle.LifestyleBase;
import interfaces.heweather.com.interfacesmodule.view.HeConfig;

public class MainActivity extends BaseActivity {
    private AddedCity currentCity;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String userId="HE1807171032311823";
        String key="24a2d899122b4526b7299924f133c599";
        HeConfig.init(userId,key);
        weatherLayout=findViewById(R.id.weather_layout);
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
        currentCity= LitePal.findFirst(AddedCity.class);
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
        if (currentCity!=null){
            showWeather(currentCity);
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

    private void showWeather(AddedCity currentCity){
        String cityName=currentCity.getNowWeather().getBasic().getLocation();
        String updateTime=currentCity.getNowWeather().getUpdate().getLoc();
        String degree=currentCity.getNowWeather().getNow().getTmp();
        String weatherInfo=currentCity.getNowWeather().getNow().getCond_txt();
        titleCity.setText(cityName);
        titleUpdeateTime.setText(updateTime);
        degreeNow.setText(degree+"°C");
        weatherInfoNow.setText(weatherInfo);
        List<ForecastBase> forecastBaseList=currentCity.getForecast().getDaily_forecast();
        forecastLayout.removeAllViews();
        for (ForecastBase forecastBase:forecastBaseList){
            View view= LayoutInflater.from(this).inflate(R.layout.forecast_item,forecastLayout,false);
            TextView dateText=view.findViewById(R.id.date_text);
            TextView infoText=view.findViewById(R.id.info_text);
            TextView maxText=view.findViewById(R.id.max_text);
            TextView minText=view.findViewById(R.id.min_text);
            dateText.setText(forecastBase.getDate());
            infoText.setText(forecastBase.getCond_txt_d());
            maxText.setText(forecastBase.getTmp_max());
            minText.setText(forecastBase.getTmp_min());
            forecastLayout.addView(view);
        }
        if (currentCity.getAirNow()!=null){
            aqiText.setText(currentCity.getAirNow().getAir_now_city().getAqi());
            pm25Text.setText(currentCity.getAirNow().getAir_now_city().getPm25());
        }
        List<LifestyleBase> lifestyleBases=currentCity.getNowWeather().getLifestyle();
        String comfort=null;
        String drsg=null;
        String sport=null;
        for (LifestyleBase lifestyleBase:lifestyleBases){
            if (lifestyleBase.getType().equals("comf")){
                comfort="舒适度："+lifestyleBase.getTxt();
            }else if (lifestyleBase.getType().equals("drsg")){
                drsg="穿衣指数："+lifestyleBase.getTxt();
            }else if (lifestyleBase.getType().equals("sport")){
                sport="运动建议："+lifestyleBase.getTxt();
            }
            if (comfort!=null&&drsg!=null&&sport!=null){
                break;
            }
        }
        comfortText.setText(comfort);
        drsgText.setText(drsg);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);
    }
}
