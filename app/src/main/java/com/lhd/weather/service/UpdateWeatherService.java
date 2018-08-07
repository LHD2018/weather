package com.lhd.weather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;

import com.lhd.weather.util.Utility;

public class UpdateWeatherService extends Service {
    public UpdateWeatherService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Utility.updateWeather(this);
        AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        int hours=6*60*60*1000;
        long triggerAtTime= SystemClock.elapsedRealtime()+hours;
        Intent i=new Intent(this,UpdateWeatherService.class);
        PendingIntent pi=PendingIntent.getService(this,0,i,0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);
    }
}
