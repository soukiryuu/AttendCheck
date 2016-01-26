package com.example.attendcheck.attendcheck.Service;

import android.app.Service;
import android.content.Intent;
import android.location.LocationManager;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.nifty.cloud.mb.core.DoneCallback;
import com.nifty.cloud.mb.core.FindCallback;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;
import com.nifty.cloud.mb.core.NCMBQuery;

import java.util.List;

public class FlgChangeService extends Service {

    final String TAG = "FlgChangeService";
    public Object view;
    public String objectId;
    private NCMBObject obj;
    public boolean flg = false;

    public FlgChangeService() {
    }

    public FlgChangeService(Object view, String objectId) {
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
        ShowLogInfo("サービス起動");

        NCMBQuery<NCMBObject> query = new NCMBQuery<>("Pre_Absence");
        query.whereEqualTo("student_id", objectId);
        query.whereEqualTo("subject_id", view);
        query.findInBackground(new FindCallback<NCMBObject>() {
            @Override
            public void done(List<NCMBObject> list, NCMBException e) {
                if (list.size() == 0) {
//                    Toast.makeText(getApplicationContext(), "出欠確認済みです。", Toast.LENGTH_LONG).show();
                } else {
                    obj = new NCMBObject("Pre_Absence");
                    obj = list.get(0);
                    obj.setObjectId(obj.getObjectId());
                    Log.d(TAG, obj.getObjectId().toString());
                    if (obj.getBoolean("check_flg") == false) {
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
                    } else {
                        obj.put("check_flg", false);
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
                    }
                }
            }
        });

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ShowLogInfo("FlgChangeServiceのonDestroy");
    }

    public void ShowLogInfo(String messeage){
        String className = this.getClass().getName();
        String packageName = this.getClass().getPackage().getName();
        String name = className.substring(packageName.length()+1);
        Log.i(name, messeage);
    }
}
