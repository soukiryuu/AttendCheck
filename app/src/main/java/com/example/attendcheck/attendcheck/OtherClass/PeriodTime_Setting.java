package com.example.attendcheck.attendcheck.OtherClass;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class PeriodTime_Setting {
    public Calendar[] start_period,end_period,attend_period;
    public Calendar nowofTime,sa_Time;
    public int hour,minute,second,sa_minute;
    public int[] start_hour = {9,10,11,13,14,15};
    public int[] end_hour = {10,11,12,14,15,16};
    public int[] start_minute = {30,20};
    public int[] end_minute = {20,10};
    public int start_second = 0;
    public SimpleDateFormat sdf;

    public PeriodTime_Setting() {
        nowofTime = Calendar.getInstance();
        sa_Time = Calendar.getInstance();
        setStartPeriod();
        setEndPeriod();
        setAttendPeriod();
    }

    public Calendar[] setStartPeriod() {
        sdf = new SimpleDateFormat("HH:mm");
        Calendar a = Calendar.getInstance();

        start_period = new Calendar[start_hour.length];

        for (int i=0; i < start_hour.length; i++) {
            start_period[i] = Calendar.getInstance();
            start_period[i].set(Calendar.HOUR_OF_DAY,start_hour[i]);
            if (i < 3) {
                start_period[i].set(Calendar.MINUTE, start_minute[0]);
            }else {
                start_period[i].set(Calendar.MINUTE, start_minute[1]);
            }
            start_period[i].set(Calendar.SECOND, start_second);
        }
        return start_period;
    }

    public Calendar[] setEndPeriod() {
        end_period = new Calendar[end_hour.length];

        for (int i=0; i < start_hour.length; i++) {
            end_period[i] = Calendar.getInstance();
            end_period[i].set(Calendar.HOUR_OF_DAY,end_hour[i]);
            if (i < 3) {
                end_period[i].set(Calendar.MINUTE, end_minute[0]);
            }else {
                end_period[i].set(Calendar.MINUTE, end_minute[1]);
            }
            end_period[i].set(Calendar.SECOND, start_second);
        }
        return end_period;
    }

    public Calendar[] setAttendPeriod() {
        attend_period = new Calendar[start_hour.length];

        for (int i=0; i < start_hour.length; i++) {
            attend_period[i] = Calendar.getInstance();
            attend_period[i].set(Calendar.HOUR_OF_DAY,start_hour[i]);
            if (i < 3) {
                attend_period[i].set(Calendar.MINUTE, end_minute[0]);
            }else {
                attend_period[i].set(Calendar.MINUTE, end_minute[1]);
            }
            attend_period[i].set(Calendar.SECOND, start_second);
        }
        return attend_period;
    }
}
