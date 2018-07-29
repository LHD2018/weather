package com.lhd.weather2018;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;

import com.lhd.weather2018.util.Utility;

public class UpdateWeatherTask extends AsyncTask <Context,Void,Integer>{
    public static final int SUCCESS=0;
    public static final int FAILED=1;

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
