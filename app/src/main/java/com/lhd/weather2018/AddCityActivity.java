package com.lhd.weather2018;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import java.util.ArrayList;
import java.util.List;

public class AddCityActivity extends BaseActivity {
    private List<String>hotCityList=new ArrayList<>();
    private List<String>hotTourList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);
        initList();
        RecyclerView hotCityRecycler=findViewById(R.id.hot_city_recycler);
        RecyclerView hotTourRecycler=findViewById(R.id.hot_tour_recycler);
        StaggeredGridLayoutManager layoutManager=new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.VERTICAL);
        hotCityRecycler.setLayoutManager(layoutManager);
        hotTourRecycler.setLayoutManager(layoutManager);
        HotCityAdapter hotCityAdapter=new HotCityAdapter(hotCityList);
        HotCityAdapter hotTourAdapter=new HotCityAdapter(hotTourList);
        hotCityRecycler.setAdapter(hotCityAdapter);
        hotTourRecycler.setAdapter(hotTourAdapter);

    }
    private void initList(){


    }
}
