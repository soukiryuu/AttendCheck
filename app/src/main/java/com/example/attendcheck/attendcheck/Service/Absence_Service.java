package com.example.attendcheck.attendcheck.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.example.attendcheck.attendcheck.OtherClass.PeriodTime_Setting;
import com.nifty.cloud.mb.core.DoneCallback;
import com.nifty.cloud.mb.core.FindCallback;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;
import com.nifty.cloud.mb.core.NCMBQuery;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

public class Absence_Service extends Service {

    public long target_ms1,target_ms2,target_ms3,target_ms4,target_ms5,target_ms6,now_ms;
    public AlarmManager am1,am2,am3,am4,am5,am6;

    public interface SendAbsenceCallback {
        public void onAbsenceTaskChecked(boolean flg);
    }

    final String TAG = "Absence_Service";
    public Object view;
    public String objectId;
    private NCMBObject obj;
    public boolean flg = false;
    public int x,y;
    public double z,rate;
    private SendAbsenceCallback callback = null;

    public Absence_Service(){};

    public Absence_Service(Object view, String objectId) {
        Log.d(TAG, "view = " + view);
        Log.d(TAG, "objectId = " + objectId);
        this.view = view;
        this.objectId = objectId;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ShowLogInfo("Absence_Service起動");
        Absence_Check();
//        return super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        ShowLogInfo("Absence_ServiceのonDestroy");
    }

    public void Absence_Check() {
        Log.d(TAG, "Absence_Check");
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("Pre_Absence");
        query.whereEqualTo("student_id", objectId);
        query.whereEqualTo("subject_id", view);
        query.whereEqualTo("check_flg", false);
        query.findInBackground(new FindCallback<NCMBObject>() {
            @Override
            public void done(List<NCMBObject> list, NCMBException e) {
                if (list.size() == 0) {
                    Toast.makeText(getApplicationContext(), "出欠確認済みです。", Toast.LENGTH_LONG).show();
                } else {
                    obj = new NCMBObject("Pre_Absence");
                    obj = list.get(0);
                    obj.setObjectId(obj.getObjectId());
                    Log.d(TAG, obj.getObjectId().toString());
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
                                    Log.d(TAG, "成功");
                                    //成功時の処理
                                }
                            }
                        });
                        AttendRate(view);
                        flg = true;
                    } catch (NCMBException e1) {
                        e1.printStackTrace();
                        flg = false;
                    }
                }
            }
        });
    }

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

    public static void startAlarm(Context context,long target_time) {
        Log.d("Absence_Service","startAlarm");

        // 実行するサービスを指定する
        PendingIntent pendingIntent = PendingIntent.getService(context, 0,
                new Intent(context, Absence_Service.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        // 10秒毎にサービスの処理を実行する
        AlarmManager am1 = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        AlarmManager am2 = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        AlarmManager am3 = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        AlarmManager am4 = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        AlarmManager am5 = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        AlarmManager am6 = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);

        am1.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(), target_time, pendingIntent);
//        am2.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                SystemClock.elapsedRealtime(), target_time[1], pendingIntent);
//        am3.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                SystemClock.elapsedRealtime(), target_time[2], pendingIntent);
//        am4.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                SystemClock.elapsedRealtime(), target_time[3], pendingIntent);
//        am5.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                SystemClock.elapsedRealtime(), target_time[4], pendingIntent);
//        am6.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                SystemClock.elapsedRealtime(), target_time[5], pendingIntent);

    }

    public static void setAlarmTime(Context context) {
        PeriodTime_Setting periodTime_setting = new PeriodTime_Setting();
        //現在時刻取得
        TimeZone tz = TimeZone.getTimeZone("Asia/Tokyo");
        Calendar now_cal = Calendar.getInstance();
        now_cal.setTimeZone(tz);
        now_cal.get(Calendar.HOUR_OF_DAY);
        now_cal.get(Calendar.MINUTE);
        now_cal.get(Calendar.SECOND);

        //ミリ秒取得
//        long target_ms1 = periodTime_setting.start_period[0].getTimeInMillis();
//        long target_ms2 = periodTime_setting.start_period[1].getTimeInMillis();
//        long target_ms3 = periodTime_setting.start_period[2].getTimeInMillis();
//        long target_ms4 = periodTime_setting.start_period[3].getTimeInMillis();
//        long target_ms5 = periodTime_setting.start_period[4].getTimeInMillis();
        long target_ms6 = periodTime_setting.start_period[5].getTimeInMillis();
//        long[] target_ms = {target_ms1,target_ms2,target_ms3,target_ms4,target_ms5,target_ms6};
        long now_ms = now_cal.getTimeInMillis();


        if (target_ms6 >= now_ms) {
            Log.d("Absence_Service", "現在の日時と同じ");
            startAlarm(context,target_ms6);

        }else {
            Log.d("Absence_Service","前回の設定が過去");
            for (int i=0; i > periodTime_setting.start_period.length; i++) {
                periodTime_setting.start_period[i].add(Calendar.DAY_OF_MONTH,1);
            }
//            target_ms1 = periodTime_setting.start_period[0].getTimeInMillis();
//            target_ms2 = periodTime_setting.start_period[1].getTimeInMillis();
//            target_ms3 = periodTime_setting.start_period[2].getTimeInMillis();
//            target_ms4 = periodTime_setting.start_period[3].getTimeInMillis();
//            target_ms5 = periodTime_setting.start_period[4].getTimeInMillis();
            target_ms6 = periodTime_setting.start_period[5].getTimeInMillis();
//            target_ms = new long[]{target_ms1, target_ms2, target_ms3, target_ms4, target_ms5, target_ms6};
            startAlarm(context,target_ms6);
        }
    }

    private void addSendAbsenceCallback(SendAbsenceCallback callback) {
        this.callback = callback;
    }

    public void ShowLogInfo(String messeage){
        String className = this.getClass().getName();
        String packageName = this.getClass().getPackage().getName();
        String name = className.substring(packageName.length()+1);
        Log.i(name, messeage);
    }
}
