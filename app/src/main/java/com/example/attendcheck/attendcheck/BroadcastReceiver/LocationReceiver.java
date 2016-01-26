package com.example.attendcheck.attendcheck.BroadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class LocationReceiver extends BroadcastReceiver {

    public static Handler handler;
    public LocationReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
//        throw new UnsupportedOperationException("Not yet implemented");
        Bundle bundle = intent.getExtras();
        double latitude = bundle.getDouble("Latitude");
        if(handler !=null){
            Message msg = new Message();

            Bundle data = new Bundle();
            data.putDouble("Latitude",latitude);
            msg.setData(data);
            handler.sendMessage(msg);
        }
    }
}
