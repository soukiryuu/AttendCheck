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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.attendcheck.attendcheck.Adapter.Period_Time_Adapter;
import com.example.attendcheck.attendcheck.AsyncTask.PeriodTimeAsyncTask;
import com.example.attendcheck.attendcheck.OtherClass.PeriodTime_Subject;
import com.example.attendcheck.attendcheck.Service.LocationService;
import com.example.attendcheck.attendcheck.R;
import com.nifty.cloud.mb.core.DoneCallback;
import com.nifty.cloud.mb.core.FindCallback;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;
import com.nifty.cloud.mb.core.NCMBQuery;
import com.nifty.cloud.mb.core.NCMBUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by watanabehiroaki on 2016/01/06.
 */
public class TeacherActivity extends Activity implements PeriodTimeAsyncTask.AsyncTaskCallback {

    private String TAG = "TeacherActivity";
    private NCMBUser LoginUser;
    private NCMBObject object,object2;
//    private Button Logoutbtn, periodbtn, Editbtn;
    public ImageView Logoutbtn, periodbtn, Editbtn, departmentbtn;
    private boolean flg;
    public Spinner spinner, dayWeekSpinner, periodTimeSpinner, classroomSpinner;
    public Period_Time_Adapter period_time_adapter;
    public String mailAddress, subjname;
    public static PeriodTimeAsyncTask periodTimeAsyncTask;
    private Context context;
    public PeriodTime_Subject periodTimeSubject;
    public int[] pti = new int[3];

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

        //       AsyncTaskの生成
        periodTimeAsyncTask = new PeriodTimeAsyncTask(this, context, this);
        periodTimeAsyncTask.execute(subjname);

        dayWeekSpinner = (Spinner)findViewById(R.id.dayweekSpinner);

        dayWeekSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner) parent;
                String dayweek = (String) spinner.getSelectedItem();
                Log.d("TeacherActivityの曜日", dayweek);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.d("TeacherActivityの曜日", "選択なし");
            }
        });

        periodTimeSpinner = (Spinner)findViewById(R.id.periodTimeSpinner);

        periodTimeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner)parent;
                String periodtime = (String)spinner.getSelectedItem();
                Log.d("TeacherActivityの時限", periodtime);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        classroomSpinner = (Spinner)findViewById(R.id.classroomSpinner);

        classroomSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner)parent;
                String classroom = (String)spinner.getSelectedItem();
                Log.d("TeacherActivityの場所", classroom);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        Logoutbtn = (ImageView) findViewById(R.id.logoutbtn);
        Logoutbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogOut();
            }
        });

        Editbtn = (ImageView) findViewById(R.id.t_accountedit);
        Editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowLogInfo("Editbtnが押された");

                Intent edit_intent = new Intent(TeacherActivity.this, EditActivity.class);
                edit_intent.putExtra("LoginUser", LoginUser.getObjectId());
//                startActivity(edit_intent);
                startActivityForResult(edit_intent, 1);
            }
        });

        periodbtn = (ImageView)findViewById(R.id.periodBtn);
        periodbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                object = new NCMBObject("PeriodOfTime");

                Log.d("登録がクリックされた", (String) dayWeekSpinner.getSelectedItem());
                object.put("day_week", (String) dayWeekSpinner.getSelectedItem());

                Log.d("登録がクリックされた", (String) periodTimeSpinner.getSelectedItem());
                String spStr = (String) periodTimeSpinner.getSelectedItem();
                String[] pts = spStr.split(",", 0);
                ArrayList<Integer> listInt  = new ArrayList<Integer>();
                for (int i=0; i < pts.length; i++){
                    pti[i] = Integer.parseInt(pts[i]);
                    Log.d(TAG, String.valueOf(pti[i]));
                    listInt.add(pti[i]);
                }
                object.put("subject_time", listInt);

                //配列作る
//                int[] arInt = {1,2,3};
//                try {
//                    //lisへ変換
//                    object.addToList("subject_time", Arrays.asList(arInt));
//                } catch (NCMBException e) {
//                    ShowLogInfo("エラーコード:" + e.getCode().toString());
//                    e.printStackTrace();
//                }
                Log.d("登録がクリックされた", (String) classroomSpinner.getSelectedItem());
                object.put("classroom_name", (String) classroomSpinner.getSelectedItem());
//                Location geo = new Location("test");
                //仮の位置情報
//                geo.setLatitude(10.0);
//                geo.setLongitude(10.0);

//                NCMBBase base = new NCMBBase("classroom");
//                object.put("geo", geo);

                Log.d("登録がクリックされた", periodTimeSubject.getSubjectName());
                object.put("subject_name", periodTimeSubject.getSubjectName());
                NCMBQuery<NCMBObject> query = new NCMBQuery<NCMBObject>("Subject");
                query.whereEqualTo("subject_name", periodTimeSubject.getSubjectName());
                query.findInBackground(new FindCallback<NCMBObject>() {
                    @Override
                    public void done(List<NCMBObject> list, NCMBException e) {
                        if (e != null) {
                            //検索失敗時の処理
                        } else {
                            //検索成功時の処理
                            object2 = new NCMBObject("Subject");
                            object2 = list.get(0);
                            Log.d(TAG, object2.getObjectId());
                            object2.setObjectId(object2.getObjectId());
                            object.put("subject_pointer", object2);
                            object.put("subject_id", object2.getString("subject_id"));
                            object.saveInBackground(new DoneCallback() {
                                @Override
                                public void done(NCMBException e) {
                                    if (e != null) {
                                        AlertDialog.Builder dialog = new AlertDialog.Builder(TeacherActivity.this);
                                        dialog.setMessage("授業が登録できませんでした。")
                                                .setTitle("登録エラーです")
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                });
                                        dialog.create().show();
                                    } else {
                                        AlertDialog.Builder dialog = new AlertDialog.Builder(TeacherActivity.this);
                                        dialog.setMessage("授業の登録が完了しました。")
                                                .setTitle("登録完了")
                                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {

                                                    }
                                                });
                                        dialog.create().show();
                                    }
                                }
                            });
                        }
                    }
                });
            }
        });

        departmentbtn = (ImageView)findViewById(R.id.department_btn);
        departmentbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent d_intent = new Intent(TeacherActivity.this, DepartmentActivity.class);
//                startActivity(d_intent);
                Intent d_intent = new Intent(TeacherActivity.this, DepartmentActivity.class);
                d_intent.putExtra("LoginUser", LoginUser.getObjectId());
                startActivityForResult(d_intent, 2);
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

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Spinner spinner = (Spinner) parent;
                int index = spinner.getSelectedItemPosition();
                Log.d("TeacherActivity", String.valueOf(index));
                String item = spinner.getSelectedItem().toString();
//                String item = (String) spinner.getSelectedItem();

                //AdapterにitemがセットされてるのでAdapterのgetItemで取得する
                periodTimeSubject =
                        (PeriodTime_Subject) spinner.getAdapter().getItem(position);


                Log.d("TeacherActivity", periodTimeSubject.getSubjectName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    if (data != null){
                        finish();
                    }
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    if (data != null){
                        //       AsyncTaskの生成
                        periodTimeAsyncTask = new PeriodTimeAsyncTask(this, context, this);
                        periodTimeAsyncTask.execute(subjname);
                    }
                }
                break;
            default:
                break;
        }
    }
}
