package com.lhd.weather;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lhd.weather.util.HttpUtil;
import com.lhd.weather.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import interfaces.heweather.com.interfacesmodule.bean.Lang;
import interfaces.heweather.com.interfacesmodule.bean.basic.Basic;
import interfaces.heweather.com.interfacesmodule.bean.search.Search;
import interfaces.heweather.com.interfacesmodule.view.HeWeather;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AddCityActivity extends AppCompatActivity {
    public static final int NONETWORK = 1;

    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case NONETWORK:
                    Toast.makeText(AddCityActivity.this,"error",Toast.LENGTH_SHORT).show();
                    break;
                    default:
                        break;
            }
        }
    };

    private List<String>hotCityList=new ArrayList<>();
    private List<String>searchedCities=new ArrayList<>();
    RecyclerView hotCityRecycler;
    HotCityAdapter hotCityAdapter;
    private ListView serchedView;
    private ArrayAdapter<String> searchedAdapter;
    private EditText searchText;
    private Button clearSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);
        clearSearch = findViewById(R.id.clear_search);
        clearSearch.setVisibility(View.GONE);
        hotCityRecycler = findViewById(R.id.hot_city_recycler);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.VERTICAL);
        hotCityRecycler.setLayoutManager(layoutManager);
        hotCityAdapter = new HotCityAdapter(hotCityList);
        hotCityRecycler.setAdapter(hotCityAdapter);
        initList();
        serchedView=findViewById(R.id.searched_city_list);
        searchText=findViewById(R.id.input_city);
        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                clearSearch.setVisibility(View.VISIBLE);
                if (searchText.getText().toString().equals("")){
                    clearSearch.setVisibility(View.INVISIBLE);
                    searchedCities.clear();
                    searchedAdapter.notifyDataSetChanged();
                }

            }
        });
        clearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchText.setText("");
                clearSearch.setVisibility(View.GONE);
                searchedCities.clear();
                searchedAdapter.notifyDataSetChanged();
            }
        });
        searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i== EditorInfo.IME_ACTION_SEARCH){
                    searchedCities.clear();
                    searchedAdapter.notifyDataSetChanged();
                    String input = textView.getText().toString();
                    searchCity(input);
                }
                return true;
            }
        });
        searchedAdapter = new ArrayAdapter<>(AddCityActivity.this,android.R.layout.simple_list_item_1,searchedCities);
        serchedView.setAdapter(searchedAdapter);
        serchedView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                view.setBackgroundColor(Color.TRANSPARENT);
                final String city = searchedCities.get(i).split("（")[0];
                if (Utility.isNetworkAvailable(AddCityActivity.this)){
                    if (!Utility.isExist(city)){
                        Utility.getWeather(AddCityActivity.this,city);
                    }else{
                        Toast.makeText(AddCityActivity.this,"该地区已添加",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(AddCityActivity.this,ManageCityActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }else{
                    Toast.makeText(AddCityActivity.this,"网络异常",Toast.LENGTH_SHORT).show();
                }
            }
        });
        ImageView back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

//初始化热门城市
    private void initList(){
        String url="https://search.heweather.com/top?group=cn&key=24a2d899122b4526b7299924f133c599&number=24";
        HttpUtil.sendOkhttpRequest(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Message message=new Message();
                message.what=NONETWORK;
                handler.sendMessage(message);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseData = response.body().string();
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
    //搜索城市
    private void searchCity(String inputCity){
        HeWeather.getSearch(this, inputCity, "world", 10, Lang.CHINESE_SIMPLIFIED, new HeWeather.OnResultSearchBeansListener() {
            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
                Message message=new Message();
                message.what=NONETWORK;
                handler.sendMessage(message);
            }

            @Override
            public void onSuccess(Search search) {
                List<Basic> searchedBasic = search.getBasic();
                for (Basic basic:searchedBasic){
                    if (basic.getParent_city()!=null){
                        searchedCities.add(basic.getLocation()+"（"+basic.getParent_city()+"）");
                    }
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
