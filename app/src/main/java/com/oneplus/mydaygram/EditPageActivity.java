package com.oneplus.mydaygram;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.oneplus.util.MyApplication;
import com.oneplus.widget.MyTextView1;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

public class EditPageActivity extends AppCompatActivity {

    private MyTextView1 TimeTitle;
    private MyTextView1 TimeTitleWeek;
    private MyTextView1 DoneButton;
    private RelativeLayout ClockButton;
    private EditText diaryContentEditor;
    private int year;
    private int month;
    private int day;
    private String diaryContent;
    private String week;
    private String monthText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_edit_page);

        Bundle bundle;
        bundle = this.getIntent().getExtras();
        year = bundle.getInt("year");
        month = bundle.getInt("month");
        day = bundle.getInt("day");
        diaryContent = bundle.getString("diaryContent");
        String tempMonth;
        String tempDay;
        if (10 > month)
            tempMonth = "0" + Integer.toString(month);
        else
            tempMonth = Integer.toString(month);
        if (10 > day)
            tempDay = "0" + Integer.toString(day);
        else
            tempDay = Integer.toString(day);
        week = getWeek(Integer.toString(year) + tempMonth + tempDay);
        TimeTitleWeek = (MyTextView1) findViewById(R.id.time_title_week);
        TimeTitleWeek.setText(week);
        if (week.equals("SUNDAY"))
            TimeTitleWeek.setTextColor(ContextCompat.getColor(getBaseContext(), R.color.colorRed));
        monthText = getMonthText(month);
        TimeTitle = (MyTextView1) findViewById(R.id.time_title);
        TimeTitle.setText(getString(R.string.time_title, monthText, day, year));
        diaryContentEditor = (EditText) findViewById(R.id.diary_content_editor);
        diaryContentEditor.setText(diaryContent);
        /*diaryContentEditor.setOnFocusChangeListener(new android.view.View.OnFocusChangeListener(){
            @Override
            public void onFocusChange(View v, boolean hasFocus){
                if(hasFocus){
                    BottomBar.setVisibility(View.VISIBLE);
                }
                else{
                    BottomBar.setVisibility(View.GONE);
                }
            }
        });*/
        DoneButton = (MyTextView1) findViewById(R.id.done_button);
        DoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diaryContent = diaryContentEditor.getText().toString();
                if (diaryContent.equals(""))
                    diaryContent = null;
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putInt("year", year);
                bundle.putInt("month", month);
                bundle.putInt("day", day);
                bundle.putString("diaryContent", diaryContent);
                intent.putExtras(bundle);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        ClockButton = (RelativeLayout) findViewById(R.id.clock);
        ClockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = diaryContentEditor.getSelectionStart();//获取光标所在位置
                String time_quantum;
                String hour_s;
                SimpleDateFormat sDateFormat = new SimpleDateFormat("HH");
                int hour = Integer.parseInt(sDateFormat.format(new java.util.Date()));
                sDateFormat = new SimpleDateFormat(":mm");
                String minute = sDateFormat.format(new java.util.Date());
                if (hour >= 12) {
                    time_quantum = "pm ";
                    hour = hour - 12;
                    if (hour > 9) {
                        hour_s = " " + Integer.toString(hour);
                    } else {
                        hour_s = " 0" + Integer.toString(hour);
                    }
                } else {
                    time_quantum = "am ";
                    if (hour > 9) {
                        hour_s = " " + Integer.toString(hour);
                    } else {
                        hour_s = " 0" + Integer.toString(hour);
                    }
                }
                String timeTag = hour_s + minute + time_quantum;
                Editable edit = diaryContentEditor.getEditableText();//获取EditText的文字
                if (index < 0 || index >= edit.length()) {
                    edit.append(timeTag);
                } else {
                    edit.insert(index, timeTag);//光标所在位置插入文字
                }
            }
        });

    }

    @Override
    public void onBackPressed() {
        diaryContent = diaryContentEditor.getText().toString();
        if (diaryContent.equals(""))
            diaryContent = null;
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt("year", year);
        bundle.putInt("month", month);
        bundle.putInt("day", day);
        bundle.putString("diaryContent", diaryContent);
        intent.putExtras(bundle);
        setResult(RESULT_OK, intent);
        finish();
    }

    /**
     * 判断当前日期是星期几
     *
     * @param pTime 设置的需要判断的时间  //格式如2012-09-08
     * @return dayForWeek 判断结果
     * @Exception 发生异常
     */

    private String getWeek(String pTime) {
        String Week = "";

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(format.parse(pTime));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 1) {
            Week += "SUNDAY";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 2) {
            Week += "MONDAY";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 3) {
            Week += "TUESDAY";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 4) {
            Week += "WEDNESDAY";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 5) {
            Week += "THURSDAY";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 6) {
            Week += "FRIDAY";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 7) {
            Week += "SATURDAY";
        }

        return Week;
    }

    private String getMonthText(int month) {
        switch (month) {
            case 1:
                return "JANUARY";
            case 2:
                return "FEBRUARY";
            case 3:
                return "MARCH";
            case 4:
                return "APRIL";
            case 5:
                return "MAY";
            case 6:
                return "JUNE";
            case 7:
                return "JULY";
            case 8:
                return "AUGUST";
            case 9:
                return "SEPTEMBER";
            case 10:
                return "OCTOBER";
            case 11:
                return "NOVEMBER";
            case 12:
                return "DECEMBER";
            default:
                return null;
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

    @Override
    protected void onRestart(){
        super.onRestart();
        MyApplication application = (MyApplication) this.getApplicationContext();
        if(application.getLockSetting())
            if(application.getIsLocked()){
                Intent intent = new Intent(EditPageActivity.this, InputPasswordPage.class);
                startActivity(intent);
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
