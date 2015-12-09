package com.example.attendcheck.attendcheck;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.preference.DialogPreference;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.nifty.cloud.mb.core.DoneCallback;
import com.nifty.cloud.mb.core.FindCallback;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;
import com.nifty.cloud.mb.core.NCMBQuery;
import com.nifty.cloud.mb.core.NCMBUser;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;


/**
 * Created by watanabehiroaki on 2015/11/24.
 */
public class UserActivity extends Activity implements SubjectAsyncTask.AsyncTaskCallback {

    private NCMBUser LoginUser;
    public String mailAddress, subjname;
    private boolean flg;
    private Button Logoutbtn, Editbtn;
    ListView subjList;
    public static SubjectAsyncTask sbjAsync;
    private Context context;
    SubjectAdapter adapter;
    NCMBObject obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        GPSSerch();

        LoginUser = NCMBUser.getCurrentUser();

        Intent flgIntent = getIntent();
        flg = flgIntent.getBooleanExtra("flag", false);

        if (flg == true) {
            ShowLogInfo("flgがtrue");
            mailAddress = flgIntent.getStringExtra("mailaddress");
            LoginUser.setMailAddress(mailAddress);

            try {
                LoginUser.save();
            } catch (NCMBException e) {
                e.printStackTrace();
            }

            //データストアにアカウントと関連付けするためにポインタを付ける
            //NCMBObjectをインスタンス生成してデータストアの名前を指定
            obj = new NCMBObject("Pre_Absence");
            //現在ログインしているアカウントをフィールドに入れる
            obj.put("student_id", NCMBUser.getCurrentUser());

            try {
                obj.save();
            } catch (NCMBException e) {
                e.printStackTrace();
            }
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
                obj = new NCMBObject("Pre_Absence");
//                obj.put();
            }
        });


//        同期処理
//        NCMBQuery<NCMBObject> query = new NCMBQuery<>("Subject");
//        query.whereEqualTo("subject_name", "Objective-C");
//        try {
//            List<NCMBObject> sbj_name = query.find();
//            obj = sbj_name.get(0);
//            subjname = obj.getString("subject_name").toString();
//            ShowLogInfo("subjname =" + subjname);
//        } catch (NCMBException e) {
//            e.printStackTrace();
//        }

//       非同期処理
//        query.findInBackground(new FindCallback<NCMBObject>() {
//            @Override
//            public void done(List<NCMBObject> list, NCMBException e) {
//                if (e != null) {
//                    Toast.makeText(getApplication(), "error", Toast.LENGTH_SHORT).show();
//                } else {
//                    obj = list.get(0);
//                    subjname = obj.getString("subject_name").toString();
//                }
//                ShowLogInfo("subjname1 =" + subjname);
//            }
//        });
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
                finish();
            }
        });
    }

//    戻るボタンで戻ったり、途中でアプリを閉じた時にログアウトさせる
    public void onDestroy() {
        super.onDestroy();
        LogOut();
        ShowLogInfo("onDestroy");
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
            startService(new Intent(this, LocationService.class));
            Toast.makeText(getApplicationContext(), "GPSはONです。", Toast.LENGTH_LONG).show();
        }
    }
}
