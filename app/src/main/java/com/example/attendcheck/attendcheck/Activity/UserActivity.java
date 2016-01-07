package com.example.attendcheck.attendcheck.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.example.attendcheck.attendcheck.Service.LocationService;
import com.example.attendcheck.attendcheck.R;
import com.example.attendcheck.attendcheck.GetterSetterClass.Subject;
import com.example.attendcheck.attendcheck.Adapter.SubjectAdapter;
import com.nifty.cloud.mb.core.DoneCallback;
import com.nifty.cloud.mb.core.FindCallback;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;
import com.nifty.cloud.mb.core.NCMBQuery;
import com.nifty.cloud.mb.core.NCMBRole;
import com.nifty.cloud.mb.core.NCMBUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by watanabehiroaki on 2015/11/24.
 */
public class UserActivity extends Activity implements SubjectAsyncTask.AsyncTaskCallback {

    private NCMBUser LoginUser;
    public String mailAddress, subjname;
    private boolean flg;
    private Button Logoutbtn, Editbtn, PAbtn;
    ListView subjList;
    public static SubjectAsyncTask sbjAsync;
    private Context context;
    SubjectAdapter adapter;
    NCMBObject obj,obj2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

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

//        PAbtn = (Button) findViewById(R.id.attendbtn);
//        PAbtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
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
//                            obj.setObjectId("47jmmCb4E5CnVmDA");
//                            obj.fetchInBackground();
//                            obj.put("sbjPA", Arrays.asList(obj2.getString("subject_name"),1));
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
        subjList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                Log.d("ItemClick", "Position=" + String.valueOf(position));
                NCMBQuery<NCMBObject> query = new NCMBQuery<>("Pre_Absence");
                query.whereEqualTo("student_id", LoginUser.getObjectId());
                Log.d("UserActivity", LoginUser.getObjectId());
                query.findInBackground(new FindCallback<NCMBObject>() {
                    @Override
                    public void done(List<NCMBObject> list, NCMBException e) {
                        obj = list.get(0);
                        Log.d("UserActivity",obj.getObjectId().toString());

                    }
                });
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
            startService(new Intent(this, LocationService.class));
            Toast.makeText(getApplicationContext(), "GPSはONです。", Toast.LENGTH_LONG).show();
        }
    }
}
