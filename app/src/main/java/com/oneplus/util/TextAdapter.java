package com.oneplus.util;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.oneplus.model.DiaryItem;
import com.oneplus.mydaygram.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Created by Oneplus on 2016/9/20.
 */
public class TextAdapter extends BaseAdapter {
    private List<DiaryItem> subList = new ArrayList<>();
    private String week;
    Context context;

    public TextAdapter(Context context,List<DiaryItem> subList){
        this.subList = subList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return (subList==null)?0:subList.size();
    }

    @Override
    public Object getItem(int position) {
        return subList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public class ViewHolder{
        TextView listTextDay;
        TextView listTextWeek;
        TextView listTextContent;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final DiaryItem diaryItem = (DiaryItem) getItem(position);
        ViewHolder viewHolder = null;
        if(convertView==null){
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.list_text_item, null);

            viewHolder = new ViewHolder();
            viewHolder.listTextDay = (TextView)convertView.findViewById(
                    R.id.list_text_day);
            viewHolder.listTextWeek = (TextView)convertView.findViewById(
                    R.id.list_text_week);
            viewHolder.listTextContent = (TextView)convertView.findViewById(
                    R.id.list_text_content);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)convertView.getTag();
        }

        viewHolder.listTextDay.setText(Integer.toString(diaryItem.getDay()) + " ");
        String month;
        String day;
        if(10 > diaryItem.getMonth())
            month = "0" + Integer.toString(diaryItem.getMonth());
        else
            month = Integer.toString(diaryItem.getMonth());
        if(10 > diaryItem.getDay())
            day = "0" + Integer.toString(diaryItem.getDay());
        else
            day = Integer.toString(diaryItem.getDay());
        week = getWeek(Integer.toString(diaryItem.getYear()) + month + day);
        viewHolder.listTextWeek.setText(week);
        if(week.equals("Sunday")){
            viewHolder.listTextWeek.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
        }else{
            viewHolder.listTextWeek.setTextColor(ContextCompat.getColor(context, R.color.colorGray));
        }
        viewHolder.listTextContent.setText(diaryItem.getDiaryContent());

        return convertView;
    }

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
            Week += "Sunday";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 2) {
            Week += "Monday";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 3) {
            Week += "Tuesday";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 4) {
            Week += "Wednesday";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 5) {
            Week += "Thursday";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 6) {
            Week += "Friday";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 7) {
            Week += "Saturday";
        }

        return Week;
    }
}
