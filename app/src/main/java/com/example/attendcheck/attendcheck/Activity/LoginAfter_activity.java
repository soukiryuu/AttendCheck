package com.example.attendcheck.attendcheck.Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.attendcheck.attendcheck.Service.LocationService;
import com.example.attendcheck.attendcheck.R;
import com.nifty.cloud.mb.core.DoneCallback;
import com.nifty.cloud.mb.core.FindCallback;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBObject;
import com.nifty.cloud.mb.core.NCMBQuery;
import com.nifty.cloud.mb.core.NCMBUser;

import java.util.List;

/**
 * Created by watanabehiroaki on 2015/10/02.
 */
public class LoginAfter_activity extends Activity {

    private NCMBUser currentUser;
    private NCMBObject task;
    private String username,mailAddress;
    private double altitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginafter);

        startService(new Intent(this, LocationService.class));

        Intent mail = getIntent();
        mailAddress = mail.getStringExtra("mailaddress");

        currentUser = NCMBUser.getCurrentUser();

        currentUser.setMailAddress(mailAddress);

        try {
            currentUser.save();
        } catch (NCMBException e) {
            e.printStackTrace();
        }


        //getQuerメソッドを使ってデータストアのクラスにクエリを発行
        NCMBQuery<NCMBObject> query = new NCMBQuery<NCMBObject>("StudentName");
        //orderByAscendingで()内のフィールド名を昇順で取得
        //Asc:昇順 Desc:降順
//        query.orderByAscending("createDate");
        query.findInBackground(new FindCallback<NCMBObject>() {
            @Override
            public void done(List<NCMBObject> list, NCMBException e) {
                for (int i = 0, n = list.size(); i < n; i++) {
                    task = list.get(i);

                    TextView dname = (TextView)findViewById(R.id.dataname);
//                    username = currentUser.getUserName();
                    username = currentUser.getMailAddress();
                    dname.setText(username);
                }
            }
        });


        if (currentUser == null) {
            // 未ログインのときは、
            Intent intent = new Intent(LoginAfter_activity.this,Login_activity.class);
            startActivity(intent);
        } else {
            TextView userName = (TextView)findViewById(R.id.UserID);
            userName.setText(currentUser.getUserName());
        }

        Button dbutton = (Button) findViewById(R.id.dStoreButton);
        dbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //データストアにアカウントと関連付けするためにポインタを付ける
                //NCMBObjectをインスタンス生成してデータストアの名前を指定
                NCMBObject obj = new NCMBObject("StudentName");
                //現在ログインしているアカウントをフィールドに入れる
                obj.put("pointer", NCMBUser.getCurrentUser());
                obj.put("name", username);
                obj.put("gender", "1");
                obj.put("student_id", "3");

                try {
                    obj.save();
                } catch (NCMBException e) {
                    e.printStackTrace();
                }
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
//            case R.id.menu_add:
//                //Intent intent1 = new Intent(MainActivity.this,TaskAddActivity.class);
//                //startActivity(intent1);
//                return true;
//            case R.id.menu_sum:
//                //Intent intent2 = new Intent(MainActivity.this,SumActivity.class);
//                //startActivity(intent2);
//                return true;
            case R.id.menu_logout:
                // ログアウト
                LogOut();
//                try {
//                    NCMBUser.logout();
//                    Intent intent3 = new Intent(LoginAfter_activity.this,MainActivity.class);
//                    startActivity(intent3);
//                } catch(NCMBException e) {
//                    Toast.makeText(this, "エラー: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//                }
                return true;
        }
        return false;
    }

    public void LogOut(){
        NCMBUser.logoutInBackground(new DoneCallback() {
            @Override
            public void done(NCMBException e) {

                Intent intent3 = new Intent(LoginAfter_activity.this,Login_activity.class);
                startActivity(intent3);
                ShowLogInfo("ログアウト");
                stopService(new Intent(LoginAfter_activity.this, LocationService.class));
            }
        });
    }

    public  void onDestroy() {
        super.onDestroy();
        ShowLogInfo("onDestroy");
    }

    public void ShowLogInfo(String messeage){
        String className = this.getClass().getName();
        String packageName = this.getClass().getPackage().getName();
        String name = className.substring(packageName.length()+1);
        Log.i(name,messeage);
    }
}
