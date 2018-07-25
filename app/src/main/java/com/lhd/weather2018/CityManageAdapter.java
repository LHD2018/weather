package com.lhd.weather2018;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lhd.weather2018.util.Utility;
import com.lhd.weather2018.view.SlidingButtonView;

import java.util.ArrayList;
import java.util.List;

public class CityManageAdapter extends RecyclerView .Adapter<CityManageAdapter.ViewHolder>implements SlidingButtonView.SlidingButtonListener{
    private List<String>mCityList;
    private Context mContext;
    private SlidingViewClickListener deleteClickListener;
    private SlidingButtonView mMenu;


    public interface SlidingViewClickListener {
        void onItemClick(View view, int position);
        void onDeleteBtnCilck(View view,int position);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public TextView cityDelete;
        public TextView mCity;
        public LinearLayout layoutContent;

        public ViewHolder(View itemView) {
            super(itemView);
            cityDelete=itemView.findViewById(R.id.city_delete);
            mCity=itemView.findViewById(R.id.city_manage_text);
            layoutContent=itemView.findViewById(R.id.layout_content);
            ((SlidingButtonView)itemView).setSlidingButtonListener(CityManageAdapter.this);
        }
    }

    public CityManageAdapter(List<String> cityList) {
        mCityList = cityList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext=parent.getContext();
        View view= LayoutInflater.from(mContext).inflate(R.layout.city_manage_item,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mCity.setText(mCityList.get(position));
        holder.layoutContent.getLayoutParams().width= Utility.getScreenWidth(mContext);
        holder.mCity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (menuIsOpen()){
                    closeMenu();
                }else{
                    int n=holder.getLayoutPosition();
                    deleteClickListener.onItemClick(view,n);
                }
            }
        });
        holder.cityDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int n=holder.getLayoutPosition();
                deleteClickListener.onDeleteBtnCilck(view,n);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCityList.size();
    }

    @Override
    public void onMenuIsOpen(View view) {
        mMenu=(SlidingButtonView)view;
    }

    @Override
    public void onDownOrMove(SlidingButtonView slidingButtonView) {
        if (menuIsOpen()){
            if (mMenu!=slidingButtonView){
                closeMenu();
            }
        }
    }

    public void removeCity(int i){
        mCityList.remove(i);
        notifyDataSetChanged();
    }

    public void closeMenu(){
        mMenu.closeMenu();
        mMenu=null;
    }
    public boolean menuIsOpen(){
        if (mMenu!=null){
            return true;
        }
        return false;
    }

    public void setOnSlisdListener(SlidingViewClickListener listener){
        deleteClickListener=listener;
    }
}
