package com.example.attendcheck.attendcheck.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.attendcheck.attendcheck.R;
import com.nifty.cloud.mb.core.DoneCallback;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBUser;

/**
 * Created by watanabehiroaki on 2015/09/28.
 */
public class SignUp_Activity extends Activity {

    private EditText mUserName;
    private EditText mEmail;
    private EditText mPassword;
    String mailAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mUserName = (EditText)findViewById(R.id.AddName);
        mEmail = (EditText) findViewById(R.id.AddMailAddress);
        mPassword = (EditText) findViewById(R.id.AddPassword);

        //アカウントボタンが押されたときの処理
        ImageView addButton = (ImageView) findViewById(R.id.AddButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // ここに Sign up（登録）処理
                NCMBUser user = new NCMBUser();
                user.setUserName(mUserName.getText().toString());
                user.setPassword(mPassword.getText().toString());

                mailAddress = mEmail.getText().toString();


                user.signUpInBackground(new DoneCallback() {
                    @Override
                    public void done(NCMBException e) {
                        if (e == null) {
                            // Sign up 成功！
                            Toast.makeText(getApplication(), "ユーザー登録成功！", Toast.LENGTH_SHORT).show();
                            AlertDialog.Builder dialog = new AlertDialog.Builder(SignUp_Activity.this);
                            dialog.setMessage("登録したアドレスにメールが送信されますので、メール内のURLからアドレスの登録の完了をしてください。" +
                                    "\n登録を完了しないと次回のログインが出来ません。")
                                    .setTitle("まだ登録は完了していません。")
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(SignUp_Activity.this, UserActivity.class);
                                            intent.putExtra("mailaddress",mailAddress);
                                            intent.putExtra("flag",true);
                                            startActivity(intent);
                                        }
                                    });
                            dialog.create().show();
                        } else {
                            // Sign up 失敗！
                            Toast.makeText(getApplication(), "失敗！", Toast.LENGTH_SHORT).show();
                            Log.d("登録失敗", e.getMessage());
                        }
                    }
                });
            }
        });
    }

    public void ShowLogInfo(String messeage){
        String className = this.getClass().getName();
        String packageName = this.getClass().getPackage().getName();
        String name = className.substring(packageName.length()+1);
        Log.i(name, messeage);
    }
}
