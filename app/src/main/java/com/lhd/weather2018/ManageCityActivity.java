package com.lhd.weather2018;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;

import com.lhd.weather2018.database.AddedCity;
import com.lhd.weather2018.util.Utility;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class ManageCityActivity extends BaseActivity implements CityManageAdapter.SlidingViewClickListener{
    private RecyclerView cityManageRecycler;
    private CityManageAdapter cityManageAdapter;
    private List<String> cities=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_city);
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

        Button addCity=findViewById(R.id.add_city);
        addCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(ManageCityActivity.this,AddCityActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {
        String city=cities.get(position);
        if (Utility.isNetworkAvailable(ManageCityActivity.this)){
            Utility.getWeather(ManageCityActivity.this,city);
        }else{
            Utility.setCurrentCity(ManageCityActivity.this,city);
            Intent intent=new Intent(ManageCityActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    public void onDeleteBtnCilck(View view, int position) {
        cityManageAdapter.removeCity(position);

    }
}
