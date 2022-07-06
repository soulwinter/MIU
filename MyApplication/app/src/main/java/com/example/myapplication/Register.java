package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.example.myapplication.entity.User;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.Request;
import okhttp3.Response;

public class Register extends AppCompatActivity {

    private Button startButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //隐藏title
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();
    }

    //初始化方法
    private void init(){
        //初始化button
        initButton();
    }

    //初始化button
    private void initButton(){
        startButton = (Button)findViewById(R.id.button_start);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = ((EditText)findViewById(R.id.name_edit)).getText().toString();
                String pwd = ((EditText)findViewById(R.id.pwd_edit)).getText().toString();
                String pwd2 = ((EditText)findViewById(R.id.pwd_edit2)).getText().toString();

                if (name.length()==0){
                    Toast.makeText(Register.this, "请输入正确的用户名！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pwd.length()==0){
                    Toast.makeText(Register.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (pwd2.length()==0){
                    Toast.makeText(Register.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!pwd.equals(pwd2)){
                    Toast.makeText(Register.this, "两次密码不一致！", Toast.LENGTH_SHORT).show();
                    return;
                }

                // TODO , 向服务器传入用户名和密码信息---用户名和邮箱应该不是同一个东西吧？
                //跳转到app主页，TODO 把变量user传过去
                Intent intent = new Intent();
                intent.setClass(Register.this,Welcome.class);
                startActivity(intent);
            }
        });
    }
}
