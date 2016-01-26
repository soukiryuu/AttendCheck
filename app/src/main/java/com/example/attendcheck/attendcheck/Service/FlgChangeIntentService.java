package com.example.attendcheck.attendcheck.Service;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by watanabehiroaki on 2016/01/26.
 */
public class FlgChangeIntentService extends IntentService {

    static String TAG = "FlgChangeIntentService";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public FlgChangeIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.d(TAG, "onHandleIntent");
            // データを受け取る
            String s_id = intent.getStringExtra("SubjectID");
            String o_id = intent.getStringExtra("UserID");
            Log.d(TAG, s_id);
            Log.d(TAG, o_id);
        } catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }
}
