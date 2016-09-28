package com.oneplus.mydaygram;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.renderscript.ScriptGroup;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.oneplus.util.MyApplication;
import com.oneplus.widget.MyTextView1;

import java.util.List;

public class SettingPageActivity extends AppCompatActivity {

    private LinearLayout settingButton;
    private MyTextView1 passwordOn;
    private MyTextView1 passwordOff;
    private RelativeLayout passwordReset;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_setting_page);

        final MyApplication application = (MyApplication) this.getApplicationContext();

        settingButton = (LinearLayout) findViewById(R.id.setting_button);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        passwordOn = (MyTextView1)findViewById(R.id.password_on);
        passwordOff = (MyTextView1)findViewById(R.id.password_off);
        passwordOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!application.getLockSetting()){
                    passwordOn.setBackground(ContextCompat.getDrawable(SettingPageActivity.this, R.drawable.circle_dark));
                    passwordOff.setBackground(ContextCompat.getDrawable(SettingPageActivity.this, R.drawable.circle_light_gray));
                    Intent intent = new Intent(SettingPageActivity.this, InputPasswordPage.class);
                    startActivity(intent);
                }
            }
        });
        passwordOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(application.getLockSetting()) {
                    application.setClickOff(true);
                    passwordOff.setBackground(ContextCompat.getDrawable(SettingPageActivity.this, R.drawable.circle_dark));
                    passwordOn.setBackground(ContextCompat.getDrawable(SettingPageActivity.this, R.drawable.circle_light_gray));
                    Intent intent = new Intent(SettingPageActivity.this, InputPasswordPage.class);
                    startActivity(intent);
                }
            }
        });

        passwordReset = (RelativeLayout)findViewById(R.id.password_reset);
        passwordReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingPageActivity.this, InputPasswordPage.class);
                startActivity(intent);
            }
        });

        if(application.getLockSetting()) {
            passwordOn.setBackground(ContextCompat.getDrawable(SettingPageActivity.this, R.drawable.circle_dark));
            passwordOff.setBackground(ContextCompat.getDrawable(SettingPageActivity.this, R.drawable.circle_light_gray));
        }else{
            passwordOn.setBackground(ContextCompat.getDrawable(SettingPageActivity.this, R.drawable.circle_light_gray));
            passwordOff.setBackground(ContextCompat.getDrawable(SettingPageActivity.this, R.drawable.circle_dark));
        }
    }

    @Override
    protected void onStop(){
        super.onStop();
        MyApplication application = (MyApplication) this.getApplicationContext();
        if(application.getLockSetting())
            if(!isAppOnForeground(this)){
                application.setIsLocked(true);
            }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    protected void onRestart(){
        super.onRestart();
        MyApplication application = (MyApplication) this.getApplicationContext();
        if(application.getLockSetting()) {
            if (application.getIsLocked()) {
                Intent intent = new Intent(SettingPageActivity.this, InputPasswordPage.class);
                startActivity(intent);
            }
            passwordOn.setBackground(ContextCompat.getDrawable(SettingPageActivity.this, R.drawable.circle_dark));
            passwordOff.setBackground(ContextCompat.getDrawable(SettingPageActivity.this, R.drawable.circle_light_gray));
        }else{
            passwordOn.setBackground(ContextCompat.getDrawable(SettingPageActivity.this, R.drawable.circle_light_gray));
            passwordOff.setBackground(ContextCompat.getDrawable(SettingPageActivity.this, R.drawable.circle_dark));
        }
    }

    public static boolean isAppOnForeground(Context context){
        ActivityManager activityManager = (ActivityManager)context.getApplicationContext()
                .getSystemService(Context.ACTIVITY_SERVICE);
        String packageName = context.getApplicationContext().getPackageName();
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if(appProcesses == null)
            return false;
        for(ActivityManager.RunningAppProcessInfo appProcess:appProcesses){
            if(appProcess.processName.equals(packageName) &&
                    appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND){
                return true;
            }
        }
        return false;
    }
}
