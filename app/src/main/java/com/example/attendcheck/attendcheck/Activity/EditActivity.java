package com.example.attendcheck.attendcheck.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.attendcheck.attendcheck.R;
import com.nifty.cloud.mb.core.NCMBException;
import com.nifty.cloud.mb.core.NCMBUser;

/**
 * Created by watanabehiroaki on 2016/01/28.
 */
public class EditActivity extends Activity {

    final String TAG = "EditActivity";
    private NCMBUser LoginUser;
    private Button Editpassbtn;
    public ImageView deletetbtn, Editmailbtn;
    public EditText mEmail,mPassword;
    public String userIdpublic, str1, str2;;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        LoginUser = NCMBUser.getCurrentUser();
        mEmail = (EditText) findViewById(R.id.editMailaddress);

        deletetbtn = (ImageView)findViewById(R.id.deleteBtn);
        deletetbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder deletedialog = new AlertDialog.Builder(EditActivity.this);
                deletedialog.setMessage("このアカウントを削除してもよろしいですか？")
                        .setTitle("アカウント削除")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                LoginUser.deleteObjectInBackground(null);
                                Intent deleteIntent = new Intent();
                                setResult(RESULT_OK,deleteIntent);
                                finish();
                            }
                        })
                        .setNegativeButton("キャンセル", null);
                deletedialog.create().show();
            }
        });

        Editmailbtn = (ImageView)findViewById(R.id.editmailBtn);
        Editmailbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder editmaildialog = new AlertDialog.Builder(EditActivity.this);
                editmaildialog.setMessage("このアドレスに変更してもよろしいですか？")
                        .setTitle("アドレス変更")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String mail = mEmail.getText().toString();
                                String[] mailSplit = mail.split("@",0);
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
                                LoginUser.setMailAddress(mEmail.getText().toString());
                                try {
                                    LoginUser.save();
                                } catch (NCMBException e) {
                                    e.printStackTrace();
                                }
                                AlertDialog.Builder deletedialog = new AlertDialog.Builder(EditActivity.this);
                                deletedialog.setMessage("確認メールから変更を完了してください。")
                                        .setTitle("変更を受け付けました")
                                        .setPositiveButton("OK",null);
                                deletedialog.create().show();
                            }
                        })
                        .setNegativeButton("キャンセル", null);
                editmaildialog.create().show();
            }
        });
    }

    public void onDestroy() {
        super.onDestroy();
        ShowLogInfo("onDestroy");
//        Intent data = new Intent();
//        setResult(re, data);
        finish();
    }

    public void ShowLogInfo(String messeage){
        String className = this.getClass().getName();
        String packageName = this.getClass().getPackage().getName();
        String name = className.substring(packageName.length()+1);
        Log.i(name, messeage);
    }
}
