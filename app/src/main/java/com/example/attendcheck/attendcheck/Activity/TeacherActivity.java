package com.example.attendcheck.attendcheck.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.attendcheck.attendcheck.Adapter.Period_Time_Adapter;
import com.example.attendcheck.attendcheck.AsyncTask.PeriodTimeAsyncTask;
import com.example.attendcheck.attendcheck.AsyncTask.SubjectAsyncTask;
import com.example.attendcheck.attendcheck.GetterSetterClass.PeriodTime_Subject;
import com.example.attendcheck.attendcheck.Service.LocationService;
import com.example.attendcheck.attendcheck.R;
import com.nifty.cloud.mb.core.DoneCallback;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBUser;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by watanabehiroaki on 2016/01/06.
 */
public class TeacherActivity extends Activity implements PeriodTimeAsyncTask.AsyncTaskCallback {

    private NCMBUser LoginUser;
    private Button Logoutbtn;
    private boolean flg;
    public Spinner spinner;
    public Period_Time_Adapter period_time_adapter;
    public String mailAddress, subjname;
    public static PeriodTimeAsyncTask periodTimeAsyncTask;
    private Context context;

//    public TeacherActivity(Context context) {
//        this.context = context;
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        LoginUser = NCMBUser.getCurrentUser();

        Intent flgIntent = getIntent();
        flg = flgIntent.getBooleanExtra("flag", false);

        if (flg == true) {
            ShowLogInfo("flgがtrue");
            mailAddress = flgIntent.getStringExtra("mailaddress");
            LoginUser.setMailAddress(mailAddress);
            LoginUser.put("position", "teacher");

            try {
                LoginUser.save();
            } catch (NCMBException e) {
                e.printStackTrace();
                AlertDialog.Builder dialog = new AlertDialog.Builder(TeacherActivity.this);
                dialog.setMessage("登録したアドレスが重複しているため使用できません。")
                        .setTitle("登録エラーです")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
//                                Intent intent = new Intent(TeacherActivity.this, TeacherActivity.class);
//                                intent.putExtra("mailaddress", mailAddress);
//                                intent.putExtra("flag", true);
//                                startActivity(intent);
                            }
                        });
                dialog.create().show();
            }
        }

        if (LoginUser == null) {
            // 未ログインのときは、
            Intent intent = new Intent(TeacherActivity.this,Login_activity.class);
            startActivity(intent);
        } else {
            TextView userName = (TextView)findViewById(R.id.LoginUserName);
            userName.setText(LoginUser.getUserName());
        }

        Logoutbtn = (Button) findViewById(R.id.logoutbtn);
        Logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogOut();
            }
        });

        //       AsyncTaskの生成
        periodTimeAsyncTask = new PeriodTimeAsyncTask(this, context, this);
        periodTimeAsyncTask.execute(subjname);
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

    //  ログアウトの処理
    public void LogOut() {
        NCMBUser.logoutInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {

//                Intent intent3 = new Intent(UserActivity.this, Login_activity.class);
//                startActivity(intent3);
                ShowLogInfo("ログアウト");
                stopService(new Intent(TeacherActivity.this, LocationService.class));
                finish();
            }
        });
    }

    public void ShowLogInfo(String messeage){
        String className = this.getClass().getName();
        String packageName = this.getClass().getPackage().getName();
        String name = className.substring(packageName.length()+1);
        Log.i(name, messeage);
    }

    @Override
    public void PostExecute(ArrayList<PeriodTime_Subject> result) {
        ShowLogInfo("TeacherActivityのPostExecute");
        Context cn = getBaseContext();

        spinner = (Spinner)findViewById(R.id.subjectSpinner);
        period_time_adapter = new Period_Time_Adapter(TeacherActivity.this);
        period_time_adapter.setSubjlist(result);
        spinner.setAdapter(period_time_adapter);

        String item = (String)spinner.getSelectedItem();
        ShowLogInfo("選択されたitem" + item.toString());
    }
}
