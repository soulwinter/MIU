package com.example.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.example.myapplication.entity.User;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Login_pwd extends AppCompatActivity {

    private Button loginVeriButton, startButton;
    private OkHttpClient okHttpClient = new OkHttpClient();

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

        TextView view = (TextView)findViewById(R.id.forget_password);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到密码登录界面
                Intent intent = new Intent();
                intent.setClass(Login_pwd.this, FindPassword.class);
                startActivity(intent);
            }
        });
    }

    private void initButton(){
        loginVeriButton = (Button)findViewById(R.id.button_login_veri);
        startButton = (Button)findViewById(R.id.button_start);


        //1.登录的button
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = ((EditText)findViewById(R.id.email_edit)).getText().toString();
                String code = ((EditText)findViewById(R.id.code_edit)).getText().toString();


                if(!MainActivity.check_email(email)) {
                    Toast.makeText(Login_pwd.this, "请输入正确的邮箱！", Toast.LENGTH_SHORT).show();
                    return;
                }

                // TODO 验证邮箱、密码是否正确
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try {
                            //1、封装请求体数据
                            FormBody formBody = new FormBody.Builder().add("email",email).add("password",code).build();
                            //2、获取到请求的对象
                            Request request = new Request.Builder().url("http://114.116.234.63:8080/user/loginByPassword").post(formBody).build();
                            //3、获取到回调的对象
                            Call call = okHttpClient.newCall(request);
                            //4、执行同步请求,获取到响应对象
                            Response response = call.execute();

                            //获取json字符串
                            String json = response.body().string();
                            JSONObject jsonObject = JSONObject.parseObject(json);
                            Integer code = jsonObject.getInteger("code");
                            if (code == 200){
                                //登陆成功
                                User user = jsonObject.getObject("data", User.class);
//                                System.out.println(user.getEmail());
                                // 保存登录状态
                                SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
                                sp.edit()
                                        .putString("username", user.getEmail())
                                        .putString("password", user.getPassword())
                                        .apply();
                                //跳转到app主页，并传递user对象
                                Intent intent = new Intent();
                                intent.putExtra("user", user);
                                intent.setClass(Login_pwd.this,Welcome.class);
                                startActivity(intent);
                            }else{
                                //登录失败
                                Looper.prepare();
                                Toast.makeText(Login_pwd.this, "邮箱或密码错误！", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

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
