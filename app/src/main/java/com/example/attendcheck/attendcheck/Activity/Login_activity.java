package com.example.attendcheck.attendcheck.Activity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.attendcheck.attendcheck.R;
import com.nifty.cloud.mb.core.NCMB;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBUser;
import com.nifty.cloud.mb.core.NCMBUserService;


/**
 * Created by watanabehiroaki on 2015/10/02.
 */
public class Login_activity extends Activity {

    private EditText mUserMailAddress;
    private EditText mPassword;
    private NCMBUser LoginUser;
    public ImageView logo;
    public MediaPlayer mp = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        mp = MediaPlayer.create(this, R.raw.system_on);

        //SDKの初期化(アプリで一回だけ)
        NCMB.initialize(this, getString(R.string.app_key),
                getString(R.string.client_key));

        mUserMailAddress = (EditText) findViewById(R.id.Mailaddress);
        mPassword = (EditText) findViewById(R.id.Password);

        //ログインボタンが押されたときの処理
        ImageView loginButton = (ImageView) findViewById(R.id.loginNewUserButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String userMailAddress = mUserMailAddress.getText().toString();
                String userPassword = mPassword.getText().toString();


                try {
                    NCMBUserService userService = (NCMBUserService) NCMB.factory(NCMB.ServiceType.USER);
                    NCMBUser user = userService.loginByMail(userMailAddress, userPassword);
                    Toast.makeText(getApplication(), "ログイン成功", Toast.LENGTH_SHORT).show();
                    LoginUser = NCMBUser.getCurrentUser();
                    ShowLogInfo(LoginUser.getString("position"));
                    String positionName = LoginUser.getString("position");
                    if (positionName.equals("student")) {
                        Log.d("LoginActivityのif文", "student");
                        Intent intent = new Intent(Login_activity.this, UserActivity.class);
                        intent.putExtra("flag",false);
                        startActivity(intent);
                    }else if (positionName.equals("teacher")){
                        Log.d("LoginActivityのif文", "teacher");
                        Intent intent = new Intent(Login_activity.this, TeacherActivity.class);
                        intent.putExtra("flag",false);
                        startActivity(intent);
                    }

                } catch (NCMBException e) {
                    Toast.makeText(getApplication(), "ログイン失敗！", Toast.LENGTH_SHORT).show();
                    Log.d("失敗",e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        //新規登録のボタンが押されたときの処理
        ImageView signButton = (ImageView) findViewById(R.id.NewUserButton);
        signButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Login_activity.this, SignUp_Activity.class);
                startActivity(intent);
            }
        });

        signButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(Login_activity.this, SignUp_Teacher_Activity.class);
                startActivity(intent);
                return false;
            }
        });

        logo = (ImageView)findViewById(R.id.logo);
        logo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
//                mp.start();
                return false;
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
        Log.i(name, messeage);
    }
}
