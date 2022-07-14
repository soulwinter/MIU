package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.alibaba.fastjson.JSONObject;
import com.example.myapplication.entity.User;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Launcher extends AppCompatActivity {

    OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //隐藏title
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        try {
            checkLoginState();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void checkLoginState() throws InterruptedException {
        SharedPreferences sp = getSharedPreferences("login", Context.MODE_PRIVATE);
        String email = sp.getString("username", null);
        String code = sp.getString("password", null);
        if (email == null) {
            Intent intent = new Intent();
            intent.setClass(Launcher.this,MainActivity.class);
            startActivity(intent);
            return;
        }
        Thread checkStateThread = new Thread(new Runnable(){
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

                        //跳转到app主页，并传递user对象
                        Intent intent = new Intent();
                        intent.putExtra("user", user);
                        intent.setClass(Launcher.this,Welcome.class);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent();
                        intent.setClass(Launcher.this,MainActivity.class);
                        startActivity(intent);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        checkStateThread.start();
        checkStateThread.join();
    }
}