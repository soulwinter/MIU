package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class Login_pwd extends AppCompatActivity {

    private Button loginVeriButton, startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //隐藏title
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_pwd);

        init();
    }

    //初始化方法
    private void init(){
        //初始化button
        initButton();
    }

    private void initButton(){
        loginVeriButton = (Button)findViewById(R.id.button_login_veri);
        startButton = (Button)findViewById(R.id.button_start);

        String email = ((EditText)findViewById(R.id.email_edit)).getText().toString();

        //1.登录的button
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                if(!MainActivity.check_email(email)) {
//                    Toast.makeText(Login_pwd.this, "请输入正确的邮箱！", Toast.LENGTH_SHORT).show();
//                    return;
//                }
                // TODO 后端检测邮箱、密码是否正确

                //跳转到app主页，TODO 把变量user传过去
                Intent intent = new Intent();
                intent.setClass(Login_pwd.this,Welcome.class);
                startActivity(intent);
            }
        });

        //2.使用验证码登录的button
        loginVeriButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //跳转至验证码登录界面
                Intent intent = new Intent();
                intent.setClass(Login_pwd.this,MainActivity.class);
                startActivity(intent);
            }
        });

    }
}
