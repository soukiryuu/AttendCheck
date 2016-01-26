package com.example.attendcheck.attendcheck.Activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attendcheck.attendcheck.AsyncTask.SubjectAsyncTask;
import com.example.attendcheck.attendcheck.BroadcastReceiver.LocationReceiver;
import com.example.attendcheck.attendcheck.OtherClass.TuitionTime;
import com.example.attendcheck.attendcheck.Service.Absence_AlarmManager;
import com.example.attendcheck.attendcheck.Service.Absence_Service;
import com.example.attendcheck.attendcheck.Service.FlgChangeService;
import com.example.attendcheck.attendcheck.Service.LocationService;
import com.example.attendcheck.attendcheck.R;
import com.example.attendcheck.attendcheck.OtherClass.Subject;
import com.example.attendcheck.attendcheck.Adapter.SubjectAdapter;
import com.nifty.cloud.mb.core.DoneCallback;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;
import com.nifty.cloud.mb.core.NCMBRole;
import com.nifty.cloud.mb.core.NCMBUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;


/**
 * Created by watanabehiroaki on 2015/11/24.
 */
public class UserActivity extends Activity implements SubjectAsyncTask.AsyncTaskCallback,LocationListener {

    private NCMBUser LoginUser;
    public String mailAddress, subjname;
    private boolean flg;
    private Button Logoutbtn, Editbtn, PAbtn;
    public ListView subjList;
    public static SubjectAsyncTask sbjAsync;
    private Context context;
    public SubjectAdapter adapter;
    private NCMBObject obj,obj2;
    public Subject sbj;
    public TuitionTime tuitionTime;
    public int now_hour,now_minute;
    public LocationService locationService;
    private LocationReceiver locationReceiver;
    private IntentFilter intentFilter;
    private LocationManager manager = null;
    public double lat,lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        //AlarmManager開始
//        Absence_Service absence_service = new Absence_Service();
//        Absence_Service.setAlarmTime(getApplicationContext());
        //自動欠席をする為のアラーム
//        Calendar now_cal = Calendar.getInstance();
//        now_hour = now_cal.get(Calendar.HOUR_OF_DAY);
//        now_minute = now_cal.get(Calendar.MINUTE);
//        now_cal.get(Calendar.SECOND);
//        Absence_AlarmManager absence_alarmManager = new Absence_AlarmManager(this);
//        absence_alarmManager.addAlarm(now_hour,now_minute);
        //GPSの判断
        GPSSerch();

        LoginUser = NCMBUser.getCurrentUser();
        Log.d("UserActivity", NCMBUser.getCurrentUser().toString());

        Intent flgIntent = getIntent();
        flg = flgIntent.getBooleanExtra("flag", false);

        if (flg == true) {
            ShowLogInfo("flgがtrue");
            mailAddress = flgIntent.getStringExtra("mailaddress");
            LoginUser.setMailAddress(mailAddress);
            LoginUser.put("position", "student");

            try {
                LoginUser.save();
            } catch (NCMBException e) {
                e.printStackTrace();
            }

//            //会員ロールに追加
//            NCMBUser user = new NCMBUser();
//            user.setObjectId(LoginUser.getObjectId());
            ShowLogInfo(LoginUser.getObjectId());

            NCMBRole role = new NCMBRole("Student");
            role.addUserInBackground(Arrays.asList(NCMBUser.getCurrentUser()), new DoneCallback() {
                @Override
                public void done(NCMBException e) {
                    ShowLogInfo("ロールに追加できませんでした。" + e.getMessage()  );
                    ShowLogInfo(e.getCode());
                }
            });

            //↓ログインごとに毎回生成されるので要修正
            //データストアにアカウントと関連付けするためにポインタを付ける
            //NCMBObjectをインスタンス生成してデータストアの名前を指定
            obj = new NCMBObject("Pre_Absence");
            //現在ログインしているアカウントをフィールドに入れる
            obj.put("student_pointer", NCMBUser.getCurrentUser());
            obj.put("student_id", LoginUser.getObjectId());

            try {
                obj.save();
            } catch (NCMBException e) {
                e.printStackTrace();
            }
        } else {

        }


        if (LoginUser == null) {
            // 未ログインのときは、
            Intent intent = new Intent(UserActivity.this,Login_activity.class);
            startActivity(intent);
        } else {
            TextView userName = (TextView)findViewById(R.id.LoginUserName);
            userName.setText(LoginUser.getUserName());
        }

        //       AsyncTaskの生成
        sbjAsync = new SubjectAsyncTask(this, context, this);
        sbjAsync.execute(subjname);

        Logoutbtn = (Button) findViewById(R.id.logoutbtn);
        Logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogOut();
            }
        });

        Editbtn = (Button) findViewById(R.id.accountedit);
        Editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowLogInfo("Editbtnが押された");
//                locationReceiver = new LocationReceiver();
//                intentFilter = new IntentFilter();
//                intentFilter.addAction("UPDATE_ACTION");
//                registerReceiver(locationReceiver, intentFilter);
//
//                Handler updateHandler = new Handler() {
//                    @Override
//                    public void handleMessage(Message msg) {
//
//                        Bundle bundle = msg.getData();
//                        double latitude = bundle.getDouble("Latitude");
//
//                        Log.d("UserActivity", String.valueOf(latitude));
//
//                    }
//                };


//                NCMBQuery<NCMBObject> queryA = new NCMBQuery<>("Subject");
//                queryA.whereEqualTo("subject_name", "Objective-C");
//                queryA.findInBackground(new FindCallback<NCMBObject>() {
//                    @Override
//                    public void done(List<NCMBObject> list, NCMBException e) {
//                        if (e != null) {
//
//                        }else {
//                            obj2 = list.get(0);
//
//                            obj = new NCMBObject("Pre_Absence");
////                            obj.fetchInBackground();
//                            obj.setObjectId("47jmmCb4E5CnVmDA");
//                            obj.put("student_id", LoginUser.getObjectId());
//                            obj.put("student_pointer", NCMBUser.getCurrentUser());
////                            obj.put("sbjPA", Arrays.asList(obj2.getString("subject_name"),1));
//                            obj.saveInBackground(null);
//                            try {
//                                obj.save();
//
//                            } catch (NCMBException ex) {
//                                ex.printStackTrace();
//                            }
//                        }
//                    }
//                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                // ログアウト
                LogOut();
                return true;
        }
        return false;
    }
//  ログアウトの処理
    public void LogOut() {
        NCMBUser.logoutInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {

//                Intent intent3 = new Intent(UserActivity.this, Login_activity.class);
//                startActivity(intent3);
                ShowLogInfo("ログアウト");
                stopService(new Intent(UserActivity.this, LocationService.class));
                stopService(new Intent(UserActivity.this, Absence_Service.class));
                finish();
            }
        });
    }

//    戻るボタンで戻ったり、途中でアプリを閉じた時にログアウトさせる
    public void onDestroy() {
        super.onDestroy();
        LogOut();
        ShowLogInfo("onDestroy");
        manager.removeUpdates(this);
        finish();
    }

    public void onPause() {
        super.onPause();
        ShowLogInfo("onPause");
    }

//   GPSの設定画面から戻ってきた時に再度検証
    public void onRestart() {
        super.onRestart();
        ShowLogInfo("onRestart");
        GPSSerch();
    }

    public void ShowLogInfo(String messeage){
        String className = this.getClass().getName();
        String packageName = this.getClass().getPackage().getName();
        String name = className.substring(packageName.length()+1);
        Log.i(name, messeage);
    }

    //    AsyncTaskCallbackのインターフェース
    @Override
    public void PostExecute(ArrayList<Subject> result) {
        ShowLogInfo("UserActivityのPostExecute");
        Context cn = getBaseContext();
//        その日の時間割に表示するための表示設定の処理
        subjList = (ListView) findViewById(R.id.subjectList);
        adapter = new SubjectAdapter(UserActivity.this);
        adapter.setSubjlist(result);
        subjList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, final int position, long id) {
                tuitionTime = new TuitionTime(view.getTag(), LoginUser.getObjectId());
                if (((37.39300 <= lat) && (lat <= 37.39452)) && ((140.39000 <= lon) && (lon <= 140.39141))) {
                    Log.d("UserActivity", "latOK");
                    tuitionTime = new TuitionTime(view.getTag(), LoginUser.getObjectId());
                    Log.d("ItemClick", "view=" + String.valueOf(view.getTag()));
                    Log.d("UserActivity", "TuitionTime.flg = " + tuitionTime.flg);

//                    if ( tuitionTime.flg == false) {
//                        Intent absenceIntent = new Intent(UserActivity.this, Absence_Service.class);
//                        absenceIntent.putExtra("SubjectID", (String) view.getTag());
//                        absenceIntent.putExtra("UserID", LoginUser.getObjectId());
//                        startService(new Intent(UserActivity.this, Absence_Service.class));
//                        startService(absenceIntent);
//                    } else {
//                        Toast.makeText(getApplicationContext(), "出欠確認済みです。", Toast.LENGTH_LONG).show();
//                    }
                    if (tuitionTime.flg == false) {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(UserActivity.this);
                        dialog.setMessage("今期の授業日数に達しているため、出欠できません。")
                                .setTitle("出欠エラー")
                                .setNegativeButton("OK", null);
                        dialog.create().show();
                    }
                } else {
                    Log.d("UserActivity", "latNO");
                    AlertDialog.Builder dialog = new AlertDialog.Builder(UserActivity.this);
                    dialog.setMessage("現在地がWiZでないか、GPS取得に時間が掛かっているためこの画面のまま数十秒程時間を置いてから再度出席ボタンを押してください。")
                            .setTitle("位置情報エラー")
                            .setNegativeButton("OK", null);
                    dialog.create().show();
                }
            }
    });
        subjList.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

//    GPSのON・OFFの判断メソッド
    public void GPSSerch() {
        // 位置測位プロバイダー一覧を取得
        String providers = Settings.Secure.getString(getContentResolver(), Settings.Secure.LOCATION_PROVIDERS_ALLOWED);
        if(providers.indexOf("gps", 0) < 0) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setMessage("位置情報をONにしないと出席が取れません。\n設定画面を開きますか？")
                    .setTitle("位置情報がOFFになっています。")
                    .setPositiveButton("開く", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 設定画面の呼出し
                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("キャンセル", null);
            dialog.create().show();

        } else {
            ShowLogInfo("GPSがONになっている。");
            manager = (LocationManager) this.getSystemService(Service.LOCATION_SERVICE);
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,this);
//            startService(new Intent(this, LocationService.class));
            Toast.makeText(getApplicationContext(), "GPSはONです。", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lon = location.getLongitude();
        Log.d("UserActivity", String.valueOf(lat));
        Log.d("UserActivity", String.valueOf(lon));
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
}
