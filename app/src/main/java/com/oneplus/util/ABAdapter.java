package com.oneplus.util;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.content.ContextCompat;
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
 * Created by Oneplus on 2016/9/17.
 */
public class ABAdapter extends BaseAdapter{
    //itemA类的type标志
    private static final int TYPE_A = 0;
    //itemB类的type标志
    private static final int TYPE_B = 1;

    private Context context;

    private String week;

    //整合数据
    private List<DiaryItem> data = new ArrayList<>();

    public ABAdapter(Context context, ArrayList<DiaryItem> myList) {
        this.context = context;

        //把数据装载同一个list里面
        //这里把所有数据都转为object类型是为了装载同一个list里面好进行排序
        this.data = myList;
    }

    /**
     * 获得itemView的type
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        int result = 0;
        if (data.get(position).getDiaryContent() != null) {
            result = TYPE_A;
        } else {
            result = TYPE_B;
        }
        return result;
    }

    /**
     * 获得有多少中view type
     *
     * @return
     */
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //创建两种不同种类的viewHolder变量
        ViewHolder1 holder1 = null;
        ViewHolder2 holder2 = null;
        //根据position获得View的type
        int type = getItemViewType(position);
        if (convertView == null) {
            //实例化
            holder1 = new ViewHolder1();
            holder2 = new ViewHolder2();
            //根据不同的type 来inflate不同的item layout
            //然后设置不同的tag
            //这里的tag设置是用的资源ID作为Key
            switch (type) {
                case TYPE_A:
                    convertView = View.inflate(context, R.layout.list_item1, null);
                    holder1.dayInWeek = (TextView) convertView.findViewById(R.id.day_in_week);
                    holder1.dayInMonth = (TextView) convertView.findViewById(R.id.day_in_month);
                    holder1.diaryContent = (TextView) convertView.findViewById(R.id.diary_content);
                    convertView.setTag(R.id.tag_first, holder1);
                    break;
                case TYPE_B:
                    convertView = View.inflate(context, R.layout.list_item2, null);
                    holder2.Dot = convertView.findViewById(R.id.dot);
                    convertView.setTag(R.id.tag_second, holder2);
                    break;
            }

        } else {
            //根据不同的type来获得tag
            switch (type) {
                case TYPE_A:
                    holder1 = (ViewHolder1) convertView.getTag(R.id.tag_first);
                    break;
                case TYPE_B:
                    holder2 = (ViewHolder2) convertView.getTag(R.id.tag_second);
                    break;
            }
        }

        DiaryItem o = data.get(position);
        //根据不同的type设置数据
        switch (type) {
            case TYPE_A:
                String month;
                String day;
                if(10 > o.getMonth())
                    month = "0" + Integer.toString(o.getMonth());
                else
                    month = Integer.toString(o.getMonth());
                if(10 > o.getDay())
                    day = "0" + Integer.toString(o.getDay());
                else
                    day = Integer.toString(o.getDay());
                week = getWeek(Integer.toString(o.getYear()) + month + day);
                holder1.dayInWeek.setText(week);
                if(week.equals("Sun"))
                    holder1.dayInWeek.setTextColor(ContextCompat.getColor(context, R.color.colorRed));
                else
                    holder1.dayInWeek.setTextColor(ContextCompat.getColor(context, R.color.colorGray));
                holder1.dayInMonth.setText(Integer.toString(o.getDay()));
                holder1.diaryContent.setText(o.getDiaryContent());
                break;

            case TYPE_B:
                String monthB;
                String dayB;
                if(10 > o.getMonth())
                    monthB = "0" + Integer.toString(o.getMonth());
                else
                    monthB = Integer.toString(o.getMonth());
                if(10 > o.getDay())
                    dayB = "0" + Integer.toString(o.getDay());
                else
                    dayB = Integer.toString(o.getDay());
                week = getWeek(Integer.toString(o.getYear()) + monthB + dayB);
                if(week.equals("Sun"))
                    holder2.Dot.setBackground(ContextCompat.getDrawable(context, R.drawable.circle_red));
                else
                    holder2.Dot.setBackground(ContextCompat.getDrawable(context,R.drawable.circle));
                break;
        }
        return convertView;
    }

    /**
     * item A 的Viewholder
     */
    private static class ViewHolder1 {
        TextView dayInWeek;
        TextView dayInMonth;
        TextView diaryContent;
    }

    /**
     * item B 的Viewholder
     */
    private static class ViewHolder2 {
        View Dot;
    }

    /**
     * 判断当前日期是星期几
     *
     * @param  pTime     设置的需要判断的时间  //格式如2012-09-08
     *

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
            Week += "Sun";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 2) {
            Week += "Mon";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 3) {
            Week += "Tue";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 4) {
            Week += "Wed";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 5) {
            Week += "Thu";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 6) {
            Week += "Fri";
        }
        if (c.get(Calendar.DAY_OF_WEEK) == 7) {
            Week += "Sat";
        }

        return Week;
    }

}


