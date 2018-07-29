package com.lhd.weather2018;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.lhd.weather2018.database.AddedCity;
import com.lhd.weather2018.database.ForecastWeather;
import com.lhd.weather2018.util.Utility;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class ManageCityActivity extends AppCompatActivity implements CityManageAdapter.SlidingViewClickListener{
    private FrameLayout managerTitle;
    private RecyclerView cityManageRecycler;
    private CityManageAdapter cityManageAdapter;
    private List<String> cities=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT>=21){
            View decorView=getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN|View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_manage_city);
        managerTitle=findViewById(R.id.manage_title);
        if (Utility.isDay()){
            managerTitle.setBackgroundResource(R.drawable.title_bg);
        }
        cityManageRecycler=findViewById(R.id.city_manage_recycler);
        List<AddedCity> cityList= LitePal.select("cityName").find(AddedCity.class);
        for (AddedCity addedCity:cityList){
            cities.add(addedCity.getCityName());
        }
        LinearLayoutManager layoutManager=new LinearLayoutManager(this);
        cityManageRecycler.setLayoutManager(layoutManager);
        cityManageAdapter=new CityManageAdapter(cities);
        cityManageRecycler.setAdapter(cityManageAdapter);
        cityManageRecycler.setItemAnimator(new DefaultItemAnimator());
        cityManageAdapter.setDeleteClickListener(this);

        Button addCity=findViewById(R.id.add_city);
        addCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ManageCityActivity.this,AddCityActivity.class);
                startActivity(intent);
            }
        });
        ImageView back=findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        String city=cities.get(position);
        Utility.setCurrentCity(ManageCityActivity.this,city);
        Intent intent=new Intent(ManageCityActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onDeleteBtnCilck(View view, int position) {
        cityManageAdapter.removeCity(position);
        String deletedcity=cities.get(position);
        cities.remove(position);
        LitePal.deleteAll(AddedCity.class,"cityName=?",deletedcity);
        LitePal.deleteAll(ForecastWeather.class,"cityName=?",deletedcity);
        if (Utility.getCurrentCity(ManageCityActivity.this).equals(deletedcity)||cities.isEmpty()){
            Utility.setCurrentCity(ManageCityActivity.this,null);
        }
    }
}
