package com.oneplus.util;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;

/**
 * Created by Oneplus on 2016/9/25.
 */
public class MyApplication extends Application {

    private boolean clickOff = false;
    private boolean lockSetting;
    private boolean isLocked = false;
    private String password;

    ScreenActionReceiver mScreenActionReceiver;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        if(mScreenActionReceiver == null) {
            mScreenActionReceiver = new ScreenActionReceiver();
            IntentFilter screenfilter = new IntentFilter();
            screenfilter.addAction(Intent.ACTION_SCREEN_OFF);
            screenfilter.addAction(Intent.ACTION_SCREEN_ON);
            registerReceiver(mScreenActionReceiver, screenfilter);
        }
        SharedPreferences pref = getSharedPreferences("data", MODE_PRIVATE);
        lockSetting = pref.getBoolean("lockSetting", false);
        password = pref.getString("password", "");
    }

    public boolean getClickOff(){ return clickOff; }
    public void setClickOff(boolean clickOff){ this.clickOff = clickOff; }
    public boolean getLockSetting(){ return lockSetting; }
    public void setLockSetting(boolean lockSetting){
        this.lockSetting = lockSetting;
        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putBoolean("lockSetting", lockSetting);
        editor.apply();
    }
    public boolean getIsLocked(){ return isLocked; }
    public void setIsLocked(boolean isLocked){ this.isLocked = isLocked; }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
        editor.putString("password", password);
        editor.apply();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        //取消注册
        this.unregisterReceiver(mScreenActionReceiver);
    }

    public class ScreenActionReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Intent.ACTION_SCREEN_ON)) {

            } else if (action.equals(Intent.ACTION_SCREEN_OFF)) {
                //用户关闭屏幕
                setIsLocked(true);
            }
        }
    }
}