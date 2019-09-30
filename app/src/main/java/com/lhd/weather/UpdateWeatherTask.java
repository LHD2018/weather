package com.lhd.weather;

import android.content.Context;
import android.os.AsyncTask;

import com.lhd.weather.util.Utility;
//更新天气
public class UpdateWeatherTask extends AsyncTask <Context,Void,Integer>{
    public static final int SUCCESS = 0;
    public static final int FAILED = 1;

    private UpdateWeatherListener listener;

    public interface UpdateWeatherListener{
        void onSuccess();
        void onFailed();
    }
    public UpdateWeatherTask(UpdateWeatherListener listener){
        this.listener=listener;
    }

    @Override
    protected Integer doInBackground(Context... contexts) {
        Utility.updateWeather(contexts[0]);
        if (Utility.flag1){
            return SUCCESS;
        }
        return FAILED;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        switch (integer){
            case SUCCESS:
                listener.onSuccess();
                break;
            case FAILED:
                listener.onFailed();
                break;
                default:
                    break;
        }
    }
}
