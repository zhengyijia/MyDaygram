package com.oneplus.mydaygram;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.oneplus.model.DiaryItem;
import com.oneplus.util.ABAdapter;
import com.oneplus.util.MyApplication;
import com.oneplus.util.TextAdapter;
import com.oneplus.widget.BounceListView;
import com.oneplus.widget.MyTextView1;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ShowPageActivity extends AppCompatActivity {

    private static final String TAG = "ShowPageActivity";

    boolean showTAG = false;
    private BounceListView diaryListView;
    private List<DiaryItem> subList = new ArrayList<>();//具有内容的选定月份日记数据
    private List<DiaryItem> diaryList = new ArrayList<>();//从文件读取的全部日记数据
    private Map<Integer, DiaryItem> tempMap = new HashMap<>();//读取数据中选定月份的数据
    private ArrayList<DiaryItem> monthDiaryList = new ArrayList<>();//选定月份的日记数据
    private MyTextView1 monthView;
    int currentYear;//当前年份
    int currentMonth;//当前月份
    int currentDay;//当前日期
    int setYear;//选定年份
    int setMonth;//选定月份

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_show_page);

        MyApplication application = (MyApplication) this.getApplicationContext();
        if(application.getLockSetting())
            if(application.getIsLocked()){
                Intent intent = new Intent(ShowPageActivity.this, InputPasswordPage.class);
                startActivity(intent);
            }

        diaryList = (List<DiaryItem>) getObject("Daygram.dat");
        if (null == diaryList)
            diaryList = new ArrayList<>();
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy");
        currentYear = Integer.parseInt(sDateFormat.format(new java.util.Date()));
        sDateFormat = new SimpleDateFormat("MM");
        currentMonth = Integer.parseInt(sDateFormat.format(new java.util.Date()));
        sDateFormat = new SimpleDateFormat("dd");
        currentDay = Integer.parseInt(sDateFormat.format(new java.util.Date()));

        setYear = currentYear;
        setMonth = currentMonth;

        monthView = (MyTextView1) findViewById(R.id.month);
        monthView.setText(getMonthText(setMonth));
        monthView.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {
                final AlertDialog monthSelectedDialog = new AlertDialog.Builder(ShowPageActivity.this, R.style.MyDialog).create();
                monthSelectedDialog.show();
                monthSelectedDialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
                WindowManager.LayoutParams lp = monthSelectedDialog.getWindow().getAttributes();
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                lp.width = dm.widthPixels;
                monthSelectedDialog.getWindow().setAttributes(lp);
                monthSelectedDialog.getWindow().setContentView(R.layout.month_select_bar);
                monthSelectedDialog.setCancelable(true);
                monthSelectedDialog.getWindow().findViewById(R.id.month_select_whole_page)
                        .setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v){
                                monthSelectedDialog.dismiss();
                            }
                        });
                String[] strTotal = {"Jan_button","Feb_button","Mar_button","Apr_button","May_button","Jun_button",
                    "Jul_button","Aug_button","Sep_button","Oct_button","Nov_button","Dec_button"};
                List<String> buttonStr = new ArrayList<String>();
                if((setYear == currentYear) && (setMonth == currentMonth)){
                    for(int i = 0; i < currentMonth; i++){
                        buttonStr.add(strTotal[i]);
                    }
                }else{
                    for(int i = 0; i < 12; i++){
                        buttonStr.add(strTotal[i]);
                    }
                }
                int tempMonth = setMonth - 1;
                monthSelectedDialog.getWindow().findViewById(getResources()
                        .getIdentifier(strTotal[tempMonth],"id","com.oneplus.mydaygram"))
                        .setBackground(ContextCompat.getDrawable(getBaseContext(), R.drawable.circle_dark));
                for(String s:buttonStr) {
                    int id = getResources().getIdentifier(s,"id","com.oneplus.mydaygram");
                    monthSelectedDialog.getWindow().findViewById(id).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String month = ((TextView)v).getText().toString();
                            String[] str = {"JAN","FEB","MAR","APR","MAY","JUN","JUL","AUG","SEP","OCT","NOV","DEC"};
                            for(int i = 0; i < 12; i++)
                            {
                                if(month.equals(str[i])) {
                                    setMonth = ++i;
                                    break;
                                }
                            }
                            resetData();
                            monthView.setText(getMonthText(setMonth));
                            if(!showTAG){
                                ABAdapter adapter = new ABAdapter(ShowPageActivity.this, monthDiaryList);
                                diaryListView.setAdapter(adapter);
                                diaryListView.setOnItemClickListener(ABClickListener);
                                diaryListView.setOnItemLongClickListener(ABLongClickListener);
                            }else {
                                TextAdapter adapter = new TextAdapter(ShowPageActivity.this, subList);
                                diaryListView.setAdapter(adapter);
                                diaryListView.setOnItemClickListener(TextClickListener);
                                diaryListView.setOnItemLongClickListener(TextLongClickListener);
                            }
                            monthSelectedDialog.dismiss();
                        }
                    });
                }
                monthSelectedDialog.setOnKeyListener(onKeyListener);
            }

            private DialogInterface.OnKeyListener onKeyListener = new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode,
                                     KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK
                            && event.getAction() == KeyEvent.ACTION_DOWN) {
                        dialog.dismiss();
                    }
                    return false;
                }
            };
        });

        final MyTextView1 yearView = (MyTextView1) findViewById(R.id.year);
        yearView.setText(Integer.toString(setYear));
        yearView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog yearSelectedDialog = new AlertDialog.Builder(ShowPageActivity.this, R.style.MyDialog).create();
                yearSelectedDialog.show();
                yearSelectedDialog.getWindow().setWindowAnimations(R.style.DialogAnimation);
                WindowManager.LayoutParams lp = yearSelectedDialog.getWindow().getAttributes();
                DisplayMetrics dm = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(dm);
                lp.width = dm.widthPixels;
                yearSelectedDialog.getWindow().setAttributes(lp);
                yearSelectedDialog.getWindow().setContentView(R.layout.year_select_bar);
                yearSelectedDialog.setCancelable(true);
                yearSelectedDialog.getWindow().findViewById(R.id.year_select_whole_page)
                        .setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View v){
                                yearSelectedDialog.dismiss();
                            }
                        });
                String[] strTotal = {"sixth_year","fifth_year","fourth_year","third_year","second_year","first_year"};
                int num = currentYear - setYear;
                ((TextView)(yearSelectedDialog.getWindow().findViewById(getResources()
                        .getIdentifier(strTotal[num],"id","com.oneplus.mydaygram"))))
                        .setTextColor(ContextCompat.getColor(getBaseContext(), R.color.selectedColor));
                int tempYear = currentYear;
                for(String s:strTotal) {
                    int id = getResources().getIdentifier(s, "id", "com.oneplus.mydaygram");
                    ((TextView)(yearSelectedDialog.getWindow().findViewById(id))).setText(Integer.toString(tempYear));
                    tempYear--;
                    yearSelectedDialog.getWindow().findViewById(id).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int year = Integer.parseInt(((TextView)v).getText().toString());
                            setYear = year;
                            resetData();
                            yearView.setText(((TextView)v).getText().toString());
                            if(!showTAG){
                                ABAdapter adapter = new ABAdapter(ShowPageActivity.this, monthDiaryList);
                                diaryListView.setAdapter(adapter);
                                diaryListView.setOnItemClickListener(ABClickListener);
                                diaryListView.setOnItemLongClickListener(ABLongClickListener);
                            }else {
                                TextAdapter adapter = new TextAdapter(ShowPageActivity.this, subList);
                                diaryListView.setAdapter(adapter);
                                diaryListView.setOnItemClickListener(TextClickListener);
                                diaryListView.setOnItemLongClickListener(TextLongClickListener);
                            }
                            yearSelectedDialog.dismiss();
                        }
                    });
                }
                yearSelectedDialog.setOnKeyListener(onKeyListener);
            }

            private DialogInterface.OnKeyListener onKeyListener = new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode,
                                     KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK
                            && event.getAction() == KeyEvent.ACTION_DOWN) {
                        dialog.dismiss();
                    }
                    return false;
                }
            };
        });

        RelativeLayout plusButton = (RelativeLayout) findViewById(R.id.plus);
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setYear = currentYear;
                setMonth = currentMonth;
                Intent intent = new Intent(ShowPageActivity.this, EditPageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("year", currentYear);
                bundle.putInt("month", currentMonth);
                bundle.putInt("day", currentDay);
                boolean find = false;
                for (DiaryItem tempItem : diaryList) {
                    if (tempItem.getYear() == currentYear)
                        if (tempItem.getMonth() == currentMonth)
                            if (tempItem.getDay() == currentDay) {
                                bundle.putString("diaryContent", tempItem.getDiaryContent());
                                find = true;
                            }
                }
                if (!find)
                    bundle.putString("diaryContent", null);
                intent.putExtras(bundle);
                monthView.setText(getMonthText(setMonth));
                yearView.setText(Integer.toString(setYear));
                startActivityForResult(intent, 1);
            }
        });

        resetData();

        ABAdapter adapter = new ABAdapter(ShowPageActivity.this, monthDiaryList);
        diaryListView = (BounceListView) findViewById(R.id.diary);
        diaryListView.setAdapter(adapter);
        diaryListView.setOnItemClickListener(ABClickListener);
        diaryListView.setOnItemLongClickListener(ABLongClickListener);

        RelativeLayout reformatButton = (RelativeLayout) findViewById(R.id.reformat_button);
        reformatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!showTAG) {
                    TextAdapter adapter = new TextAdapter(ShowPageActivity.this, subList);
                    diaryListView.setAdapter(adapter);
                    diaryListView.setOnItemClickListener(TextClickListener);
                    diaryListView.setOnItemLongClickListener(TextLongClickListener);
                    showTAG = true;
                } else {
                    ABAdapter adapter = new ABAdapter(ShowPageActivity.this, monthDiaryList);
                    diaryListView.setAdapter(adapter);
                    diaryListView.setOnItemClickListener(ABClickListener);
                    diaryListView.setOnItemLongClickListener(ABLongClickListener);
                    showTAG = false;
                }
            }
        });

        LinearLayout settingButton = (LinearLayout)findViewById(R.id.setting_button);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShowPageActivity.this, SettingPageActivity.class);
                startActivity(intent);
            }
        });
    }

    private void resetData(){
        tempMap = new HashMap<>();
        subList = new ArrayList<>();
        monthDiaryList = new ArrayList<>();
        for (DiaryItem tempItem : diaryList) {
            if (tempItem.getYear() == setYear)
                if (tempItem.getMonth() == setMonth)
                    tempMap.put(tempItem.getDay(), tempItem);
        }

        if((setMonth == currentMonth) && (setYear == currentYear)) {
            for (int i = 1; i <= currentDay; i++) {
                DiaryItem tempItem;
                tempItem = tempMap.get(i);
                if (tempItem != null) {
                    monthDiaryList.add(tempItem);
                    subList.add(tempItem);
                } else {
                    DiaryItem diaryItem = new DiaryItem();
                    diaryItem.setYear(currentYear);
                    diaryItem.setMonth(currentMonth);
                    diaryItem.setDiaryContent(null);
                    diaryItem.setDay(i);
                    monthDiaryList.add(diaryItem);
                }
            }
        }else{
            String time;
            if (10 > setMonth)
                time = Integer.toString(setYear) + "0" + Integer.toString(setMonth);
            else
                time = Integer.toString(setYear) + Integer.toString(setMonth);
            int dayCount = getDayCount(time);
            for (int i = 1; i <= dayCount; i++) {
                DiaryItem tempItem;
                tempItem = tempMap.get(i);
                if (tempItem != null) {
                    monthDiaryList.add(tempItem);
                    subList.add(tempItem);
                } else {
                    DiaryItem diaryItem = new DiaryItem();
                    diaryItem.setYear(setYear);
                    diaryItem.setMonth(setMonth);
                    diaryItem.setDiaryContent(null);
                    diaryItem.setDay(i);
                    monthDiaryList.add(diaryItem);
                }
            }
        }
    }

    private AdapterView.OnItemClickListener ABClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            DiaryItem selectedItem = monthDiaryList.get(position);
            Intent intent = new Intent(ShowPageActivity.this, EditPageActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("year", selectedItem.getYear());
            bundle.putInt("month", selectedItem.getMonth());
            bundle.putInt("day", selectedItem.getDay());
            bundle.putString("diaryContent", selectedItem.getDiaryContent());
            intent.putExtras(bundle);
            startActivityForResult(intent, 1);
        }
    };

    private AdapterView.OnItemClickListener TextClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            DiaryItem selectedItem = subList.get(position);
            Intent intent = new Intent(ShowPageActivity.this, EditPageActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("year", selectedItem.getYear());
            bundle.putInt("month", selectedItem.getMonth());
            bundle.putInt("day", selectedItem.getDay());
            bundle.putString("diaryContent", selectedItem.getDiaryContent());
            intent.putExtras(bundle);
            startActivityForResult(intent, 1);
        }
    };

    public AdapterView.OnItemLongClickListener ABLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            final DiaryItem selectedItem = monthDiaryList.get(position);
            if (null != selectedItem.getDiaryContent()) {
                final int year = selectedItem.getYear();
                final int month = selectedItem.getMonth();
                final int day = selectedItem.getDay();
                String monthText;
                String dayText;
                if (10 > month)
                    monthText = "0" + Integer.toString(month);
                else
                    monthText = Integer.toString(month);
                if (10 > day)
                    dayText = "0" + Integer.toString(day);
                else
                    dayText = Integer.toString(day);
                AlertDialog.Builder dialog = new AlertDialog.Builder(ShowPageActivity.this);
                dialog.setTitle(R.string.app_name);
                dialog.setMessage(getString(R.string.dialog_text, year, monthText, dayText));
                dialog.setCancelable(true);
                dialog.setPositiveButton(R.string.dialog_select_text2, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (DiaryItem i : subList) {
                            if (day == i.getDay()) {
                                subList.remove(i);
                                break;
                            }
                        }
                        if(null == diaryList){
                            diaryList = (List<DiaryItem>) getObject("Daygram.dat");
                        }
                        for (DiaryItem i : diaryList) {
                            if (year == i.getYear())
                                if (month == i.getMonth())
                                    if (day == i.getDay()) {
                                        diaryList.remove(i);
                                        break;
                                    }
                        }
                        saveObject("Daygram.dat");
                        selectedItem.setDiaryContent(null);
                        ABAdapter adapter = new ABAdapter(ShowPageActivity.this, monthDiaryList);
                        diaryListView.setAdapter(adapter);
                    }
                });
                dialog.setNegativeButton(R.string.dialog_select_text1, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialog.show();
            }
            return true;
        }
    };

    public AdapterView.OnItemLongClickListener TextLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            final DiaryItem textSelectedItem = subList.get(position);
            final int textYear = textSelectedItem.getYear();
            final int textMonth = textSelectedItem.getMonth();
            final int textDay = textSelectedItem.getDay();
            String monthText;
            String dayText;
            if (10 > textMonth)
                monthText = "0" + Integer.toString(textMonth);
            else
                monthText = Integer.toString(textMonth);
            if (10 > textDay)
                dayText = "0" + Integer.toString(textDay);
            else
                dayText = Integer.toString(textDay);
            AlertDialog.Builder dialog = new AlertDialog.Builder(ShowPageActivity.this);
            dialog.setTitle(R.string.app_name);
            dialog.setMessage(getString(R.string.dialog_text, textYear, monthText, dayText));
            dialog.setCancelable(true);
            dialog.setPositiveButton(R.string.dialog_select_text2, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    for (DiaryItem i : subList) {
                        if (textDay == i.getDay()) {
                            subList.remove(i);
                            break;
                        }
                    }
                    if(null == diaryList){
                        diaryList = (List<DiaryItem>) getObject("Daygram.dat");
                    }
                    for (DiaryItem i : diaryList) {
                        if (textYear == i.getYear())
                            if (textMonth == i.getMonth())
                                if (textDay == i.getDay()) {
                                    diaryList.remove(i);
                                    break;
                                }
                    }
                    saveObject("Daygram.dat");
                    for(DiaryItem i:monthDiaryList){
                        if(textDay == i.getDay()) {
                            i.setDiaryContent(null);
                            break;
                        }
                    }
                    TextAdapter adapter = new TextAdapter(ShowPageActivity.this, subList);
                    diaryListView.setAdapter(adapter);
                }
            });
            dialog.setNegativeButton(R.string.dialog_select_text1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
            dialog.show();
            return true;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    Bundle bundle;
                    bundle = data.getExtras();
                    int tempYear = bundle.getInt("year");
                    int tempMonth = bundle.getInt("month");
                    int tempDay = bundle.getInt("day");
                    if(((tempYear == currentYear) && (tempMonth == currentMonth)) && (tempDay == currentDay)) {
                        resetData();
                    }
                    String tempDiaryContent = bundle.getString("diaryContent");
                    for (DiaryItem i : monthDiaryList) {
                        if (tempDay == i.getDay()) {
                            i.setDiaryContent(tempDiaryContent);
                            break;
                        }
                    }
                    subList = new ArrayList<>();
                    for (DiaryItem i : monthDiaryList) {
                        if (null != i.getDiaryContent()) {
                            subList.add(i);
                        }
                    }
                    if (!showTAG) {
                        ABAdapter adapter = new ABAdapter(ShowPageActivity.this, monthDiaryList);
                        diaryListView.setAdapter(adapter);
                    } else {
                        TextAdapter adapter = new TextAdapter(ShowPageActivity.this, subList);
                        diaryListView.setAdapter(adapter);
                    }
                    boolean find = false;
                    if(null == diaryList){
                        diaryList = (List<DiaryItem>) getObject("Daygram.dat");
                    }
                    for (DiaryItem i : diaryList) {
                        if (tempYear == i.getYear())
                            if (tempMonth == i.getMonth())
                                if (tempDay == i.getDay()) {
                                    find = true;
                                    if (null == tempDiaryContent)
                                        diaryList.remove(i);
                                    else
                                        i.setDiaryContent(tempDiaryContent);
                                    break;
                                }
                    }
                    if ((!find) && (null != tempDiaryContent)) {
                        DiaryItem tempDiaryItem = new DiaryItem();
                        tempDiaryItem.setYear(tempYear);
                        tempDiaryItem.setMonth(tempMonth);
                        tempDiaryItem.setDay(tempDay);
                        tempDiaryItem.setDiaryContent(tempDiaryContent);
                        diaryList.add(tempDiaryItem);
                    }
                    saveObject("Daygram.dat");
                }
                break;
            default:
        }
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

    private void saveObject(String name) {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;
        try {
            fos = this.openFileOutput(name, MODE_PRIVATE);
            oos = new ObjectOutputStream(fos);
            oos.writeObject(diaryList);
        } catch (Exception e) {
            e.printStackTrace();
            //这里是保存文件产生异常
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    //fos流关闭异常
                    e.printStackTrace();
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    //oos流关闭异常
                    e.printStackTrace();
                }
            }
        }
    }

    private Object getObject(String name) {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = this.openFileInput(name);
            ois = new ObjectInputStream(fis);
            return ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            //这里是读取文件产生异常
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    //fis流关闭异常
                    e.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    //ois流关闭异常
                    e.printStackTrace();
                }
            }
        }
        //读取产生异常，返回null
        return null;
    }

    private int getDayCount(String time){
        Calendar calendar = new GregorianCalendar();
        // 或者用Calendar calendar = Calendar.getInstance();

        // 格式化日期--设置date
        SimpleDateFormat sdf = new SimpleDateFormat("", Locale.ENGLISH);
        sdf.applyPattern("yyyyMM"); // 201203格式
        try {
            calendar.setTime(sdf.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int num2 = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        return num2;
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
                Intent intent = new Intent(ShowPageActivity.this, InputPasswordPage.class);
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
