package com.example.attendcheck.attendcheck.OtherClass;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.attendcheck.attendcheck.Service.FlgChangeService;
import com.nifty.cloud.mb.core.DoneCallback;
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
    private NCMBObject obj,obj2;
    public boolean pa_flg,flg;
    public Calendar[] start,end,attend_period;
    public Calendar nowofTime,sa_Time;
    public int hour,minute,second,sa_minute;
    public String[] pot = {"1period","2period","3period","4period","5period","6period"};
    public String objectId;
    public int[] start_hour = {9,10,11,13,14,15};
    public int[] end_hour = {10,11,12,14,15,16};
    public int[] start_minute = {30,20};
    public int[] end_minute = {20,10};
    public int start_second = 00;
    public int hour_check,n,px,x,py,y;
    public double pz,z,rate;
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

    public void attendFlg (){
//        Calendar calendar = Calendar.getInstance();
        sdf = new SimpleDateFormat("HH:mm");
        Calendar a = Calendar.getInstance();
        nowofTime = Calendar.getInstance();
        sa_Time = Calendar.getInstance();

        PeriodTime_Setting periodTime_setting = new PeriodTime_Setting();
        Calendar cal = periodTime_setting.start_period[0];
        Log.d(TAG, cal.get(Calendar.HOUR_OF_DAY) + ":" + cal.get(Calendar.MINUTE) + ":" + cal.get(Calendar.SECOND));


        start = new Calendar[start_hour.length];
        end = new Calendar[end_hour.length];
        attend_period = new Calendar[start_hour.length];

        for (int i=0; i < start_hour.length; i++) {
            start[i] = Calendar.getInstance();
            start[i] = periodTime_setting.start_period[i];

            end[i] = Calendar.getInstance();
            end[i] = periodTime_setting.end_period[i];
        }

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
//        Log.d(TAG, String.valueOf(nowofTime.before(start_period[2])));

//        flg = false;
        gethour();
//        Log.d(TAG, String.valueOf(sa_minute));
//        Log.d(TAG, String.valueOf(start_period[0].get(Calendar.MINUTE)));
//        return flg;
    }

    //現在の時刻を元に何時限目の出席を取ろうとしているのか判断し、出席ができたらtrueを返す
    public void gethour () {
        if (nowofTime.before(start[0]) == true) {//現在時刻が9:30より前の時
            Log.d(TAG, "1限目始まる前");
            sa = start[0].getTimeInMillis() - nowofTime.getTimeInMillis() - sa_Time.getTimeZone().getRawOffset();
            sa_Time.setTimeInMillis(sa);
            String [] s = sdf.format( sa_Time.getTime() ).split(":");
            sa_minute = Integer.parseInt(s[1]);
            if (sa_minute <= 10) { //授業開始10分前なら出席にする
                pa_flg = true;
                Attend_Check();
            }else { //遅刻などの場合
                pa_flg = false;
                Attend_Check();
            }
        }else if ((nowofTime.before(start[1]) == true) &&
                (nowofTime.after(end[0]) == true)) {//現在時刻が10:30より前で、10:20より後の時
            Log.d(TAG, "2限目始まる前");
            sa = start[1].getTimeInMillis() - nowofTime.getTimeInMillis() - sa_Time.getTimeZone().getRawOffset();
            sa_Time.setTimeInMillis(sa);
            String [] s = sdf.format( sa_Time.getTime() ).split(":");
            sa_minute = Integer.parseInt(s[1]);
            if (sa_minute <= 10) { //授業開始10分前なら出席にする
                pa_flg = true;
                Attend_Check();
            }else { //遅刻などの場合
                pa_flg = false;
                Attend_Check();
            }
        }else if ((nowofTime.before(end[2]) == true) &&
                (nowofTime.after(end[1]) == true)) {//現在時刻が11:30より前で、11:20より後の時
            Log.d(TAG, "3限目始まる前");
            sa = start[2].getTimeInMillis() - nowofTime.getTimeInMillis() - sa_Time.getTimeZone().getRawOffset();
            sa_Time.setTimeInMillis(sa);
            String [] s = sdf.format( sa_Time.getTime() ).split(":");
            sa_minute = Integer.parseInt(s[1]);
            if (sa_minute <= 10) { //授業開始10分前なら出席にする
                pa_flg = true;
                Attend_Check();
            }else { //遅刻などの場合
                pa_flg = false;
                Attend_Check();
            }
        }else if ((nowofTime.before(start[3]) == true) &&
                (nowofTime.after(end[2]) == true)) {//現在時刻が13:20より前で、12:20より後の時
            Log.d(TAG, "4限目始まる前");
            sa = start[3].getTimeInMillis() - nowofTime.getTimeInMillis() - sa_Time.getTimeZone().getRawOffset();
            sa_Time.setTimeInMillis(sa);
            String [] s = sdf.format( sa_Time.getTime() ).split(":");
            sa_minute = Integer.parseInt(s[1]);
            if (sa_minute <= 10) { //授業開始10分前なら出席にする
                pa_flg = true;
                Attend_Check();
            }else { //遅刻などの場合
                pa_flg = false;
                Attend_Check();
            }
        }else if ((nowofTime.before(start[4]) == true) &&
                (nowofTime.after(end[3]) == true)) {//現在時刻が14:20より前で、14:10より後の時
            Log.d(TAG, "5限目始まる前");
            sa = start[4].getTimeInMillis() - nowofTime.getTimeInMillis() - sa_Time.getTimeZone().getRawOffset();
            sa_Time.setTimeInMillis(sa);
            String [] s = sdf.format( sa_Time.getTime() ).split(":");
            sa_minute = Integer.parseInt(s[1]);
            if (sa_minute <= 10) { //授業開始10分前なら出席にする
                pa_flg = true;
                Attend_Check();
            }else { //遅刻などの場合
                pa_flg = false;
                Attend_Check();
            }
        }else if ((nowofTime.before(start[5]) == true) &&
                (nowofTime.after(end[4]) == true)) {//現在時刻が15:20より前で、15:10より後の時
            Log.d(TAG, "6限目始まる前");
            sa = start[5].getTimeInMillis() - nowofTime.getTimeInMillis() - sa_Time.getTimeZone().getRawOffset();
            sa_Time.setTimeInMillis(sa);
            String [] s = sdf.format( sa_Time.getTime() ).split(":");
            sa_minute = Integer.parseInt(s[1]);
            if (sa_minute <= 10) { //授業開始10分前なら出席にする
                pa_flg = true;
                Attend_Check();
            }else { //遅刻などの場合
                pa_flg = false;
                Attend_Check();
            }
        }else {
            Log.d(TAG, "どの時間にも当てはまらない");
            pa_flg = false;
            Attend_Check();
        }

//        return flg;
    }

    //出席する科目に出席のカウントをする
    public boolean Attend_Check() {
        NCMBQuery<NCMBObject> sab_query = new NCMBQuery<>("Subject");
        sab_query.whereEqualTo("subject_id", Integer.parseInt(String.valueOf(view)));
        try{
            List<NCMBObject> num = sab_query.find();
            for (NCMBObject s: num) {
                n = s.getInt("num_schooldays");
                Log.d(TAG, String.valueOf(n));
            }
        }catch (NCMBException error){
            error.printStackTrace();
        }
        NCMBQuery<NCMBObject> pa_query = new NCMBQuery<>("Pre_Absence");
        pa_query.whereEqualTo("student_id", objectId);
        pa_query.whereEqualTo("subject_id", view);
        try {
            List<NCMBObject> num2 = pa_query.find();
            for (NCMBObject n: num2) {
               pz = n.getInt("pa_num");
                Log.d(TAG, String.valueOf(pz));
            }
        }catch (NCMBException error2) {
            error2.printStackTrace();
        }
        if (pz < n) {
            Log.d(TAG,"OK");
            NCMBQuery<NCMBObject> query = new NCMBQuery<>("Pre_Absence");
            query.whereEqualTo("student_id", objectId);
            query.whereEqualTo("subject_id", view);
//        query.whereEqualTo("check_flg", false);
            try {
                List<NCMBObject> list2 = query.find();
                for (NCMBObject a: list2) {
                    obj = new NCMBObject("Pre_Absence");
                    Log.d(TAG, a.getObjectId());
                    obj = list2.get(0);
                    obj.setObjectId(obj.getObjectId());

                    if (pa_flg == true) {
                        try {
                            obj.increment("presence", 1);
                            obj.put("check_flg", true);
                            obj.saveInBackground(new DoneCallback() {
                                @Override
                                public void done(NCMBException e) {
                                    if (e != null) {
                                        e.printStackTrace();
                                        //エラー発生時の処理
                                    } else {

                                        //成功時の処理
                                    }
                                }
                            });
                            AttendRate(view);
                            flg = true;
                        } catch (NCMBException e1) {
                            e1.printStackTrace();
//                        flg = false;
                        }
                    } else if (pa_flg == false){
                        try {
                            obj.increment("absence", 1);
                            obj.put("check_flg", true);
                            obj.saveInBackground(new DoneCallback() {
                                @Override
                                public void done(NCMBException e) {
                                    if (e != null) {
                                        e.printStackTrace();
                                        //エラー発生時の処理
                                    } else {

                                        //成功時の処理
                                    }
                                }
                            });
                            AttendRate(view);
                            flg = true;
                        } catch (NCMBException e1) {
                            e1.printStackTrace();
//                        flg = false;
                        }
                    }
                }
            }catch (NCMBException error3) {
                error3.printStackTrace();
            }
        }else if (pz >= n){
            Log.d(TAG,"NG");
//                    flg = false;
        }
        return flg;
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
                obj.put("pa_num",z);
                obj.saveInBackground(null);
            }
        });
    }
}
