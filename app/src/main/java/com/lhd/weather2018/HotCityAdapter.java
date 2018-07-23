package com.lhd.weather2018;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.lhd.weather2018.util.Utility;

import java.util.List;

public class HotCityAdapter extends RecyclerView.Adapter<HotCityAdapter.ViewHolder>{
    private List<String> cityList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView hotCity;

        public ViewHolder(View itemView) {
            super(itemView);
            hotCity=itemView.findViewById(R.id.hot_city);
        }
    }

    public HotCityAdapter(List<String> dataList) {
        cityList=dataList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.hotcity_item,parent,false);
        final ViewHolder holder=new ViewHolder(view);
        holder.hotCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //搜索
                view.setBackgroundColor(Color.BLUE);
                int position=holder.getAdapterPosition();
                String city=cityList.get(position);
                Context context=parent.getContext();
                Utility.getWeather(context,city);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String city=cityList.get(position);
        holder.hotCity.setText(city);
    }

    @Override
    public int getItemCount() {
        return cityList.size();
    }
}
