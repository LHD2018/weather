package com.lhd.weather2018;

import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import com.lhd.weather2018.database.AddedCity;
import com.lhd.weather2018.util.HttpUtil;
import com.lhd.weather2018.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.Lang;
import interfaces.heweather.com.interfacesmodule.bean.air.now.AirNow;
import interfaces.heweather.com.interfacesmodule.bean.basic.Basic;
import interfaces.heweather.com.interfacesmodule.bean.search.Search;
import interfaces.heweather.com.interfacesmodule.view.HeConfig;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddCityActivity extends BaseActivity {

    private List<String>hotCityList=new ArrayList<>();
    private List<String>searchedCities=new ArrayList<>();
    RecyclerView hotCityRecycler;
    HotCityAdapter hotCityAdapter;
    private ListView serchedView;
    private ArrayAdapter<String> searchedAdapter;
    private EditText searchText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);
        String userId="HE1807171032311823";
        String key="24a2d899122b4526b7299924f133c599";
        HeConfig.init(userId,key);
        HeConfig.switchToFreeServerNode();
        hotCityRecycler=findViewById(R.id.hot_city_recycler);
        StaggeredGridLayoutManager layoutManager=new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.VERTICAL);
        hotCityRecycler.setLayoutManager(layoutManager);
        hotCityAdapter=new HotCityAdapter(hotCityList);
        hotCityRecycler.setAdapter(hotCityAdapter);
        initList();
        serchedView=findViewById(R.id.searched_city_list);
        searchText=findViewById(R.id.input_city);
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i== EditorInfo.IME_ACTION_SEARCH){
                    String input=textView.getText().toString();
                    searchCity(input);
                }
                return true;
            }
        });
        searchedAdapter=new ArrayAdapter<>(AddCityActivity.this,android.R.layout.simple_list_item_1,searchedCities);
        serchedView.setAdapter(searchedAdapter);
        serchedView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                view.setBackgroundColor(Color.BLUE);
                String city=searchedCities.get(i);
                //搜索
                //Utility.getWeather(AddCityActivity.this,city);
                checkCity(city);
            }
        });

    }
    private void checkCity(String cityName){
        HeWeather.getAirNow(this, cityName, new HeWeather.OnResultAirNowBeansListener() {
            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(List<AirNow> list) {
                final AirNow airNow=list.get(0);
                final AddedCity addedCity=new AddedCity();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        addedCity.setAirNow(airNow);
                        addedCity.save();
                    }
                });

            }
        });
    }

    private void initList(){
        String url="https://search.heweather.com/top?group=cn&key=24a2d899122b4526b7299924f133c599&number=24";
        HttpUtil.sendOkhttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData=response.body().string();
                final List<String> hotCities= Utility.handleHotCityResponce(responseData);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (String hotCity:hotCities){
                            hotCityList.add(hotCity);
                        }
                        hotCityAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

    }
    private void searchCity(String inputCity){
        HeWeather.getSearch(this, inputCity, "world", 10, Lang.CHINESE_SIMPLIFIED, new HeWeather.OnResultSearchBeansListener() {
            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(Search search) {
                List<Basic> searchedBasic=search.getBasic();
                for (Basic basic:searchedBasic){
                    searchedCities.add(basic.getLocation()+"("+basic.getParent_city()+")");
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        searchedAdapter.notifyDataSetChanged();
                    }
                });

            }
        });

    }
}
