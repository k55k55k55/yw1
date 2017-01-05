package com.example.suguiming.calendar.calendar;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.suguiming.calendar.R;

/**
 * Created by yw .
 */
public class CalendarView extends RelativeLayout {

    //---日历相关参数,翻页的时候一起变化
    private RelativeLayout calendarLayout;

    private int centerYear,centerMonth;
    private int preMonth,preMonthYear;
    private int nextMonth,nextMonthYear;

    private int currentDay,selectedDay;//今天几号，选中几号
    private int cellWidth;
    private LayoutInflater inflater;

    //--课程列表相关数据
    public OnPreOrNextMonthTapListener onPreOrNextMonthTapListener;

    public CalendarView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view =  inflater.inflate(R.layout.calendar_view, this);
        calendarLayout = (RelativeLayout)view.findViewById(R.id.calendar_layout);

        currentDay = DateUtil.getCurrentDay();

        DisplayMetrics dm = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(dm);
        int screenWidth =dm.widthPixels;
        cellWidth =screenWidth/7;

    }

    private void resetPreAndNextMonth() {
        preMonth = centerMonth - 1;
        preMonthYear = centerYear;
        if (preMonth == 0) {
            preMonth = 12;
            preMonthYear = centerYear - 1;
        }

        nextMonth = centerMonth + 1;
        nextMonthYear = centerYear;
        if (nextMonth == 13) {
            nextMonth = 1;
            nextMonthYear = centerYear + 1;
        }
    }

    private void resetCalendarView(){
        calendarLayout.removeAllViewsInLayout();

        int preDays = DateUtil.getDaysInMonth(preMonthYear,preMonth);
        int centerDays = DateUtil.getDaysInMonth(centerYear, centerMonth);
        int flag = DateUtil.getFirstdayWeekOfMonth(centerYear,centerMonth);//中间月的1号星期几 从0开始

        //前一个月的View
        for (int i=0;i<flag;i++){
            int tmpDay = i + 1 + (preDays - flag);

            RelativeLayout tmpView = (RelativeLayout)inflater.inflate(R.layout.item_calendar,null);
            LayoutParams params = new LayoutParams(cellWidth,cellWidth);
            params.leftMargin = cellWidth*(i%7);
            params.topMargin = cellWidth*(i/7);
            calendarLayout.addView(tmpView, params);

            TextView numTv = (TextView)tmpView.findViewById(R.id.num_tv);
            numTv.setText(tmpDay + "");
            numTv.setTextColor(Color.WHITE);
            numTv.setEnabled( false );

            //是今天
            if (currentDay == tmpDay && preMonth==DateUtil.getCurrentMonth() && preMonthYear == DateUtil.getCurrentYear()){
                numTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.cirlce_calendar_blue_circle));
            }
        }
        //中间月的View
        for (int i=flag;i<centerDays+flag;i++){
            int tmpDay = i+1-flag;

            RelativeLayout tmpView = (RelativeLayout)inflater.inflate(R.layout.item_calendar,null);
            tmpView.setTag(tmpDay);

            LayoutParams params = new LayoutParams(cellWidth,cellWidth);
            params.leftMargin = cellWidth*(i%7);
            params.topMargin = cellWidth*(i/7);
            calendarLayout.addView(tmpView, params);

            TextView numTv = (TextView)tmpView.findViewById(R.id.num_tv);
            numTv.setText(tmpDay + "");
            numTv.setEnabled( true );

            //center cell添加点击事件
            tmpView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    clearLastSelectedStyle();

                    TextView clickTv = (TextView)v.findViewById(R.id.num_tv);
                    setCenterCalendarTapDay(Integer.parseInt(clickTv.getText().toString()));
                }
            });

            //是今天
            if (currentDay == (tmpDay) && centerMonth==DateUtil.getCurrentMonth() && centerYear == DateUtil.getCurrentYear()){
                numTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.cirlce_calendar_blue_circle));
            }
            //是点击的view
            if (selectedDay == (tmpDay)){
                numTv.setTextColor(Color.WHITE);
                numTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.cirlce_calendar_blue));
            }
        }
        //下一个月的View
        int nextMonthDays= (((flag+centerDays)+6)/7)*7-(flag+centerDays);//下个月还有几天
        int tmpDay = 1;
        for (int i=centerDays+flag;i<centerDays+flag+nextMonthDays;i++){
            RelativeLayout tmpView = (RelativeLayout)inflater.inflate(R.layout.item_calendar,null);
            LayoutParams params = new LayoutParams(cellWidth,cellWidth);
            params.leftMargin = cellWidth*(i%7);
            params.topMargin = cellWidth*(i/7);
            calendarLayout.addView(tmpView, params);

            TextView numTv = (TextView)tmpView.findViewById(R.id.num_tv);
            numTv.setText(tmpDay+"");
            numTv.setTextColor(Color.WHITE);
            tmpDay++;
            numTv.setEnabled( false );

            //是今天
            if (currentDay == (tmpDay) && nextMonth==DateUtil.getCurrentMonth() && nextMonthYear == DateUtil.getCurrentYear()){
                numTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.cirlce_calendar_blue_circle));
            }

        }
    }

    public void setCenterCalendarTapDay(int day){
        selectedDay = day;
        //选中的样式
        RelativeLayout layout = (RelativeLayout)calendarLayout.findViewWithTag(selectedDay);
        TextView numTv = (TextView)layout.findViewById(R.id.num_tv);
        numTv.setTextColor(Color.WHITE);
        numTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.cirlce_calendar_blue));

    }

    public void clearLastSelectedStyle(){
        //清除上次选中的样式
        RelativeLayout layout = (RelativeLayout)calendarLayout.findViewWithTag(selectedDay);
        TextView numTv = (TextView)layout.findViewById(R.id.num_tv);
        numTv.setTextColor(Color.parseColor( "#2b2b34" ));
        numTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.circle_calendar_white));

        //如果是今天 的样式
        int textNum = Integer.parseInt(numTv.getText().toString());
        if (currentDay == textNum  && centerMonth==DateUtil.getCurrentMonth() && centerYear == DateUtil.getCurrentYear()){
            numTv.setBackgroundDrawable(getResources().getDrawable(R.drawable.cirlce_calendar_blue_circle));
        }
    }

    public void setYearMonthDay(int year,int month,int day){
        centerYear = year;
        centerMonth = month;
        selectedDay = day;

        resetPreAndNextMonth();
        resetCalendarView();
        setCenterCalendarTapDay(selectedDay);
    }


    public interface OnPreOrNextMonthTapListener{
        void preMonthDayTap(int day);
        void nextMonthDayTap(int day);
    }
    public OnPreOrNextMonthTapListener getOnPreOrNextMonthTapListener() {
        return onPreOrNextMonthTapListener;
    }

    public void setOnPreOrNextMonthTapListener(OnPreOrNextMonthTapListener onPreOrNextMonthTapListener) {
        this.onPreOrNextMonthTapListener = onPreOrNextMonthTapListener;
    }

}
