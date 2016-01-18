package com.example.attendcheck.attendcheck.GetterSetterClass;

import android.nfc.Tag;
import android.util.Log;

import java.sql.Time;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by watanabehiroaki on 2016/01/15.
 */
public class TuitionTime {

    final String TAG = "TuitionTime";
    public boolean flg = false;
    public Calendar[] calendars;
    public String hour,minute,second;
    public String[] pot = {"1period","2period","3period","4period","5period","6period"};
    public int[] start_hour = {9,10,11,13,14,15};
    public int[] end_hour = {10,11,12,14,15,16};
    public int[] start_minute = {30,20};
    public int[] end_minute = {20,10};
    public int start_second = 00;
    public int hour_check;

    public TuitionTime(){
        attendFlg();
    }

    public boolean attendFlg (){
//        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Calendar a = Calendar.getInstance();
        Calendar b = Calendar.getInstance();
        Calendar c = Calendar.getInstance();

        Calendar[] start_period = new Calendar[start_hour.length];
        Calendar[] end_period = new Calendar[end_hour.length];

        for (int i=0; i < start_hour.length; i++) {
            start_period[i] = Calendar.getInstance();
            start_period[i].set(Calendar.HOUR,start_hour[i]);
            if (i < 3) {
                start_period[i].set(Calendar.MINUTE, start_minute[0]);
            }else {
                start_period[i].set(Calendar.MINUTE, start_minute[1]);
            }
            start_period[i].set(Calendar.SECOND, start_second);

            end_period[i] = Calendar.getInstance();
            end_period[i].set(Calendar.HOUR,end_hour[i]);
            if (i < 3) {
                end_period[i].set(Calendar.MINUTE, end_minute[0]);
            }else {
                end_period[i].set(Calendar.MINUTE, end_minute[1]);
            }
            end_period[i].set(Calendar.SECOND, start_second);
        }

        a.set(Calendar.HOUR,11);
        a.set(Calendar.MINUTE, 00);
        a.set(Calendar.SECOND, 00);

        b.get(Calendar.HOUR);
        b.get(Calendar.MINUTE);
        b.get(Calendar.SECOND);

        hour = String.valueOf(b.get(Calendar.HOUR));
        minute = String.valueOf(b.get(Calendar.MINUTE));
        second = String.valueOf(b.get(Calendar.SECOND));
        Log.d(TAG,hour + ":" + minute + ":" + second);

//        gethour(Calendar.HOUR);

        long sa = a.getTimeInMillis() - b.getTimeInMillis() - c.getTimeZone().getRawOffset();
        c.setTimeInMillis(sa);

        String [] s = sdf.format( c.getTime() ).split(":");
//        int hour = Integer.parseInt( s[0] );
        int minute = Integer.parseInt( s[1] );

        Log.d(TAG, String.valueOf(minute));
        return flg;
    }

    public int gethour (int now_hour) {
        return hour_check;
    }
}
