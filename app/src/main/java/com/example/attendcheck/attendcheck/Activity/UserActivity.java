package com.example.attendcheck.attendcheck.Activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
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
import android.widget.ImageView;
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
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by watanabehiroaki on 2015/11/24.
 */
public class UserActivity extends Activity implements SubjectAsyncTask.AsyncTaskCallback,LocationListener, GpsStatus.Listener {

    private NCMBUser LoginUser;
    public String mailAddress, subjname, str1,str2;
    private boolean flg, gps_flg;
    private Button PAbtn;
    public ImageView Editbtn,Logoutbtn;
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
    public static ProgressDialog pa_dialog, gps_dialog;
    public MediaPlayer mp = null;
    private Timer timer;
    public long time = 0L;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

//        mp = MediaPlayer.create(this, R.raw.user_ok);
//        mp.start();

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
            String[] mailSplit = mailAddress.split("@",0);
            Log.d("UserActivity", mailSplit[1]);
            //アドレスが学校のメールアドレスのときに学科も入れる
            if (mailSplit[1].equals("wiz.ac.jp")) {
                str1 = mailSplit[0].substring(mailSplit[0].length()-8);
                str2 = str1.substring(2, 4);
                Log.d("UserActivity", str2);
                switch (str2){
                    case "21": LoginUser.put("department", "情報システム科");
                        break;

                    case "24": LoginUser.put("department", "モバイルアプリケーション科");
                        break;

                    case "31": LoginUser.put("department", "情報システム工学科");
                        break;

                    case "41": LoginUser.put("department", "高度情報工学科");
                        break;
                }
            }
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

        Logoutbtn = (ImageView) findViewById(R.id.logoutbtn);
        Logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogOut();
            }
        });

        Editbtn = (ImageView) findViewById(R.id.accountedit);
        Editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowLogInfo("Editbtnが押された");

                Intent edit_intent = new Intent(UserActivity.this, EditActivity.class);
                edit_intent.putExtra("LoginUser", LoginUser.getObjectId());
//                startActivity(edit_intent);
                startActivityForResult(edit_intent,1);
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
        gps_dialog.dismiss();
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
//                tuitionTime = new TuitionTime(view.getTag(), LoginUser.getObjectId());
                if (((37.39300 <= lat) && (lat <= 37.39452)) && ((140.39000 <= lon) && (lon <= 140.39144))) {
                    pa_dialog = new ProgressDialog(UserActivity.this);
                    pa_dialog.setTitle("処理中");
                    pa_dialog.setMessage("出欠を確認中です。");
                    pa_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    pa_dialog.setCancelable(true);
//                dialog.setOnCancelListener();
                    pa_dialog.show();
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
                    }else {
                        AlertDialog.Builder dialog = new AlertDialog.Builder(UserActivity.this);
                        dialog.setMessage("この時間の出欠確認が完了しました。")
                                .setTitle("出欠完了")
                                .setNegativeButton("OK", null);
                        dialog.create().show();
                    }
                    pa_dialog.dismiss();
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
            manager.addGpsStatusListener(UserActivity.this);
            manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);
//            startService(new Intent(this, LocationService.class));
            Toast.makeText(getApplicationContext(), "GPSはONです。", Toast.LENGTH_LONG).show();

            gps_dialog = new ProgressDialog(UserActivity.this);
            gps_dialog.setTitle("取得中");
            gps_dialog.setMessage("位置情報を取得中です。");
            gps_dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            gps_dialog.setCancelable(false);
            gps_dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "キャンセル", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    timer.cancel();
                    gps_dialog.dismiss();
                    AlertDialog.Builder cancel_dialog = new AlertDialog.Builder(UserActivity.this);
                    cancel_dialog.setMessage("位置情報取得中にキャンセルしました。GPS設定画面を開きますか？")
                            .setTitle("位置情報キャンセル")
                            .setPositiveButton("開く", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    // 設定画面の呼出し
                                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(intent);
                                }
                            })
                            .setNegativeButton("キャンセル", null);
                    cancel_dialog.create().show();
                }
            });
//            gps_dialog.show();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    if (data != null){
                        manager.removeUpdates(this);
                        finish();
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onGpsStatusChanged(int event) {
        String status = "";
        if (event == GpsStatus.GPS_EVENT_FIRST_FIX) {
            status = "FIRST FIX：初めて位置情報を確定した：";
            timer.cancel();
            gps_dialog.dismiss();
            Log.d("UserActivity", "FIRST FIX gps_flg =" + gps_flg);
        } else if (event == GpsStatus.GPS_EVENT_SATELLITE_STATUS) {
            status = "SATELLITE STATUS：GPSが位置情報を取得中";
            Log.d("UserActivity", "SATELLITE STATUS gps_flg =" + gps_flg);
        } else if (event == GpsStatus.GPS_EVENT_STARTED) {
            status = "STARTED：GPSを使い位置情報の取得を開始した";
            timer = new Timer(true);
            final Handler handler = new Handler();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (time == 30000L) {
                                gps_dialog.dismiss();
                                AlertDialog.Builder dialog = new AlertDialog.Builder(UserActivity.this);
                                dialog.setMessage("位置情報取得中タイムアウトしました。GPSの設定を確認してください。設定画面を開きますか？")
                                        .setTitle("位置情報エラー")
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
                            }

                            time = time + 1000L;
                        }
                    });
                }
            },0L,1000L);
            gps_dialog.show();
        } else if (event == GpsStatus.GPS_EVENT_STOPPED) {
            status = "STOPPED：GPSの位置情報取得が終了した";
        }
        Log.d("onGpsStatusChanged", status);

    }
}
