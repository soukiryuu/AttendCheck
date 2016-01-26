package com.example.attendcheck.attendcheck.Service;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.*;
import android.location.Location;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

public class LocationService extends Service implements LocationListener{

    private String TAG = "LocationService";
    private LocationManager manager;
    public double latitude,longitude;


    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ShowLogInfo("サービス起動");
        manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//        return super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLatitude();
        float accuracy = location.getAccuracy();
        Log.d("TAG", String.valueOf(accuracy));
        Log.d("TAG", String.valueOf(location.getAltitude()));


        Intent broadcastIntent = new Intent();
        broadcastIntent.putExtra("Latitude", location.getLatitude());
        broadcastIntent.setAction("UPDATE_ACTION");
        getBaseContext().sendBroadcast(broadcastIntent);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //位置情報取得を止める
        manager.removeUpdates(this);
        ShowLogInfo("LocationServiceのonDestroy");
    }

    public void ShowLogInfo(String messeage){
        String className = this.getClass().getName();
        String packageName = this.getClass().getPackage().getName();
        String name = className.substring(packageName.length()+1);
        Log.i(name, messeage);
    }
}
