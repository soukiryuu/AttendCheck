package com.example.attendcheck.attendcheck.OtherClass;

import android.util.Log;

import com.nifty.cloud.mb.core.FindCallback;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;
import com.nifty.cloud.mb.core.NCMBQuery;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

/**
 * Created by watanabehiroaki on 2016/01/15.
 */
public class TuitionTime {

    final String TAG = "TuitionTime";
    private NCMBObject obj;
    public boolean flg = false;
    public Calendar[] start_period,end_period,attend_period;
    public Calendar nowofTime,sa_Time;
    public int hour,minute,second,sa_minute;
    public String[] pot = {"1period","2period","3period","4period","5period","6period"};
    public String objectId;
    public int[] start_hour = {9,10,11,13,14,15};
    public int[] end_hour = {10,11,12,14,15,16};
    public int[] start_minute = {30,20};
    public int[] end_minute = {20,10};
    public int start_second = 00;
    public int hour_check,x,y;
    public double z,rate;
    public long sa;
    public SimpleDateFormat sdf;
    public Object view;

    public TuitionTime(Object view, String objectId){
        Log.d(TAG, "view = " + (String) view);
        Log.d(TAG, "objectId = " + objectId);
        this.view = view;
        this.objectId = objectId;
        attendFlg();
    }

    public boolean attendFlg (){
//        Calendar calendar = Calendar.getInstance();
        sdf = new SimpleDateFormat("HH:mm");
        Calendar a = Calendar.getInstance();
        nowofTime = Calendar.getInstance();
        sa_Time = Calendar.getInstance();

        SetPeriodTime setPeriodTime = new SetPeriodTime();
        Calendar cal = setPeriodTime.start_period[0];

//        setPeriodTime.start_period[0];


//        start_period = new Calendar[start_hour.length];
//        end_period = new Calendar[end_hour.length];
//        attend_period = new Calendar[start_hour.length];
//
//        for (int i=0; i < start_hour.length; i++) {
//            start_period[i] = Calendar.getInstance();
//            start_period[i].set(Calendar.HOUR_OF_DAY,start_hour[i]);
//            if (i < 3) {
//                start_period[i].set(Calendar.MINUTE, start_minute[0]);
//            }else {
//                start_period[i].set(Calendar.MINUTE, start_minute[1]);
//            }
//            start_period[i].set(Calendar.SECOND, start_second);
//
//            end_period[i] = Calendar.getInstance();
//            end_period[i].set(Calendar.HOUR_OF_DAY,end_hour[i]);
//            if (i < 3) {
//                end_period[i].set(Calendar.MINUTE, end_minute[0]);
//            }else {
//                end_period[i].set(Calendar.MINUTE, end_minute[1]);
//            }
//            end_period[i].set(Calendar.SECOND, start_second);
//
//            attend_period[i] = Calendar.getInstance();
//            attend_period[i].set(Calendar.HOUR_OF_DAY,start_hour[i]);
//            if (i < 3) {
//                attend_period[i].set(Calendar.MINUTE, end_minute[0]);
//            }else {
//                attend_period[i].set(Calendar.MINUTE, end_minute[1]);
//            }
//            attend_period[i].set(Calendar.SECOND, start_second);
//        }

        a.set(Calendar.HOUR_OF_DAY,11);
        a.set(Calendar.MINUTE, 00);
        a.set(Calendar.SECOND, 00);

        nowofTime.get(Calendar.HOUR_OF_DAY);
        nowofTime.get(Calendar.MINUTE);
        nowofTime.get(Calendar.SECOND);

        hour = nowofTime.get(Calendar.HOUR_OF_DAY);
        minute = nowofTime.get(Calendar.MINUTE);
        second = nowofTime.get(Calendar.SECOND);
        Log.d(TAG, hour + ":" + minute + ":" + second);

//        long sa = a.getTimeInMillis() - nowofTime.getTimeInMillis() - c.getTimeZone().getRawOffset();
//        c.setTimeInMillis(sa);

//        String [] s = sdf.format( c.getTime() ).split(":");
//        int hour = Integer.parseInt( s[0] );
//        sa_minute = Integer.parseInt(s[1]);
        Log.d(TAG, String.valueOf(nowofTime.before(start_period[2])));

        flg = false;
//        gethour();
//        Log.d(TAG, String.valueOf(sa_minute));
        Log.d(TAG, String.valueOf(start_period[0].get(Calendar.MINUTE)));
        return flg;
    }

    //現在の時刻を元に何時限目の出席を取ろうとしているのか判断し、出席ができたらtrueを返す
    public boolean gethour () {
        if (nowofTime.before(start_period[0]) == true) {//現在時刻が9:30より前の時
            sa = start_period[0].getTimeInMillis() - nowofTime.getTimeInMillis() - sa_Time.getTimeZone().getRawOffset();
            sa_Time.setTimeInMillis(sa);
            String [] s = sdf.format( sa_Time.getTime() ).split(":");
            sa_minute = Integer.parseInt(s[1]);
            if (sa_minute <= 10) { //授業開始10分前なら出席にする
                Attend_Check();
            }else { //遅刻などの場合

            }
        }else if ((nowofTime.before(start_period[1]) == true) ||
                (nowofTime.after(end_period[0]) == true)) {//現在時刻が10:30より前で、10:20より後の時
            sa = start_period[1].getTimeInMillis() - nowofTime.getTimeInMillis() - sa_Time.getTimeZone().getRawOffset();
            sa_Time.setTimeInMillis(sa);
            String [] s = sdf.format( sa_Time.getTime() ).split(":");
            sa_minute = Integer.parseInt(s[1]);
            if (sa_minute <= 10) { //授業開始10分前なら出席にする
                Attend_Check();
            }else { //遅刻などの場合

            }
        }else if ((nowofTime.before(start_period[2]) == true) ||
                (nowofTime.after(end_period[1]) == true)) {//現在時刻が11:30より前で、11:20より後の時
            sa = start_period[2].getTimeInMillis() - nowofTime.getTimeInMillis() - sa_Time.getTimeZone().getRawOffset();
            sa_Time.setTimeInMillis(sa);
            String [] s = sdf.format( sa_Time.getTime() ).split(":");
            sa_minute = Integer.parseInt(s[1]);
            if (sa_minute <= 10) { //授業開始10分前なら出席にする
                Attend_Check();
            }else { //遅刻などの場合

            }
        }else if ((nowofTime.before(start_period[3]) == true) ||
                (nowofTime.after(end_period[2]) == true)) {//現在時刻が13:20より前で、12:20より後の時
            sa = start_period[3].getTimeInMillis() - nowofTime.getTimeInMillis() - sa_Time.getTimeZone().getRawOffset();
            sa_Time.setTimeInMillis(sa);
            String [] s = sdf.format( sa_Time.getTime() ).split(":");
            sa_minute = Integer.parseInt(s[1]);
            if (sa_minute <= 10) { //授業開始10分前なら出席にする
                Attend_Check();
            }else { //遅刻などの場合

            }
        }else if ((nowofTime.before(start_period[4]) == true) ||
                (nowofTime.after(end_period[3]) == true)) {//現在時刻が14:20より前で、14:10より後の時
            sa = start_period[4].getTimeInMillis() - nowofTime.getTimeInMillis() - sa_Time.getTimeZone().getRawOffset();
            sa_Time.setTimeInMillis(sa);
            String [] s = sdf.format( sa_Time.getTime() ).split(":");
            sa_minute = Integer.parseInt(s[1]);
            if (sa_minute <= 10) { //授業開始10分前なら出席にする
                Attend_Check();
            }else { //遅刻などの場合

            }
        }else if ((nowofTime.before(start_period[5]) == true) ||
                (nowofTime.after(end_period[4]) == true)) {//現在時刻が15:20より前で、15:10より後の時
            sa = start_period[5].getTimeInMillis() - nowofTime.getTimeInMillis() - sa_Time.getTimeZone().getRawOffset();
            sa_Time.setTimeInMillis(sa);
            String [] s = sdf.format( sa_Time.getTime() ).split(":");
            sa_minute = Integer.parseInt(s[1]);
            if (sa_minute <= 10) { //授業開始10分前なら出席にする
                Attend_Check();
            }else { //遅刻などの場合

            }
        }else {

        }

        return flg;
    }

    //出席する科目に出席のカウントをする
    public void Attend_Check() {
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("Pre_Absence");
        query.whereEqualTo("student_id", objectId);
        query.whereEqualTo("subject_id", view);
        query.findInBackground(new FindCallback<NCMBObject>() {
            @Override
            public void done(List<NCMBObject> list, NCMBException e) {
                obj = new NCMBObject("Pre_Absence");
                obj = list.get(0);
                obj.setObjectId(obj.getObjectId());
                Log.d("UserActivity2",obj.getObjectId().toString());
                try {
                    obj.increment("presence", 1);
                    obj.saveInBackground(null);
                    AttendRate(view);
                    flg = true;
                } catch (NCMBException e1) {
                    e1.printStackTrace();
                    flg = false;
                }
            }
        });
    }

    //出席立を求めてデータベースに反映する
    public void AttendRate(Object tag) {
        NCMBQuery<NCMBObject> query1 = new NCMBQuery<>("Pre_Absence");
        query1.whereEqualTo("student_id", objectId);
        query1.whereEqualTo("subject_id", tag);
        query1.findInBackground(new FindCallback<NCMBObject>() {
            @Override
            public void done(List<NCMBObject> list, NCMBException e) {
                obj = new NCMBObject("Pre_Absence");
                obj = list.get(0);
                obj.setObjectId(obj.getObjectId());
                x = obj.getInt("presence");
                y = obj.getInt("absence");
                z = (double)x + y;
                rate = (double)((x/z)*100);
                rate = Math.round(rate);
                DecimalFormat df =  new DecimalFormat("###.#");
                obj.put("attend_rate", df.format(rate));
                obj.saveInBackground(null);
            }
        });
    }
}