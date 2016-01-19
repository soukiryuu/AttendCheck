package com.example.attendcheck.attendcheck.Service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.nifty.cloud.mb.core.FindCallback;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;
import com.nifty.cloud.mb.core.NCMBQuery;

import java.text.DecimalFormat;
import java.util.List;

public class Absence_Service extends Service {

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
        NCMBQuery<NCMBObject> query = new NCMBQuery<>("Pre_Absence");
        query.whereEqualTo("student_id", objectId);
        query.whereEqualTo("subject_id", view);
        query.findInBackground(new FindCallback<NCMBObject>() {
            @Override
            public void done(List<NCMBObject> list, NCMBException e) {
                obj = new NCMBObject("Pre_Absence");
                obj = list.get(0);
                obj.setObjectId(obj.getObjectId());
                Log.d("UserActivity2", obj.getObjectId().toString());
                try {
                    obj.increment("abesence", 1);
                    obj.saveInBackground(null);
                    AttendRate(view);
                    flg = true;
                } catch (NCMBException e1) {
                    e1.printStackTrace();
                    flg = false;
                }
                callback.onAbsenceTaskChecked(flg);
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

    public static void startAlarm(Context context) {
        // 実行するサービスを指定する
        PendingIntent pendingIntent = PendingIntent.getService(context, 0,
                new Intent(context, Absence_Service.class),
                PendingIntent.FLAG_UPDATE_CURRENT);

        // 10秒毎にサービスの処理を実行する
        AlarmManager am = (AlarmManager) context
                .getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime(), 10 * 1000, pendingIntent);
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
