package com.example.attendcheck.attendcheck.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by watanabehiroaki on 2016/01/19.
 */
public class Absence_AlarmManager {

    Context c;
    AlarmManager am;
    private PendingIntent mAlarmSender;

    private static final String TAG = Absence_AlarmManager.class.getSimpleName();

    public Absence_AlarmManager(Context c){
        // 初期化
        this.c = c;
        am = (AlarmManager)c.getSystemService(Context.ALARM_SERVICE);
        Log.v(TAG,"初期化完了");
    }

    public void addAlarm(int am_hour,int am_minute){
        // アラームを設定する
        mAlarmSender = this.getPendingIntent();

        // アラーム時間設定
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        // 設定した時刻をカレンダーに設定
        cal.set(Calendar.HOUR_OF_DAY, am_hour);
        cal.set(Calendar.MINUTE, am_minute);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        // 過去だったら明日にする
        if(cal.getTimeInMillis() < System.currentTimeMillis()){
            Log.d(TAG, "明日に設定");
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
//        Toast.makeText(c, String.format("%02d時%02d分に起こします", alarmHour, alarmMinute), Toast.LENGTH_LONG).show();
        am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), mAlarmSender);
//        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
//                SystemClock.elapsedRealtime(), cal.getTimeInMillis(), mAlarmSender);
        Log.d(TAG, cal.getTimeInMillis()+"ms");
        Log.d(TAG, "アラームセット完了");
    }

    public void stopAlarm() {
        // アラームのキャンセル
        Log.d(TAG, "stopAlarm()");
        am.cancel(mAlarmSender);
    }

    private PendingIntent getPendingIntent() {
        // アラーム時に起動するアプリケーションを登録
        Intent intent = new Intent(c, Absence_Service.class);
        PendingIntent pendingIntent = PendingIntent.getService(c, PendingIntent.FLAG_ONE_SHOT, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
}
