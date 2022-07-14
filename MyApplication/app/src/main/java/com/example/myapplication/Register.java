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

import java.io.IOException;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Register extends AppCompatActivity {

    private Button startButton;
    private OkHttpClient okHttpClient = new OkHttpClient();


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
                Intent get_intent = getIntent();
                String email = get_intent.getStringExtra("user_email");

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


                // TODO , 向服务器传入用户名和密码信息
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try {

                            while (true){
                                //1、封装请求体数据
                                FormBody formBody = new FormBody.Builder().add("email",email).add("username",name).add("password",pwd).build();
                                //2、获取到请求的对象
                                Request request = new Request.Builder().url("http://114.116.234.63:8080/user/register").post(formBody).build();
                                //3、获取到回调的对象
                                Call call = okHttpClient.newCall(request);
                                //4、执行同步请求,获取到响应对象
                                Response response = call.execute();

                                //获取json字符串
                                String json = response.body().string();
                                JSONObject jsonObject = JSONObject.parseObject(json);
                                if (jsonObject == null)
                                    continue;
                                Integer code = jsonObject.getInteger("code");
                                if (code == 200){
                                    //注册成功
//                                User user = jsonObject.getObject("data", User.class);
//                                System.out.println(user.getEmail());

                                    //跳转到app主页，把登录的邮箱传过去
                                    Intent intent = new Intent();
                                    intent.putExtra("user_email", email);
                                    intent.setClass(Register.this,Welcome.class);
                                    startActivity(intent);

                                    Looper.prepare();
                                    Toast.makeText(Register.this, "注册成功！", Toast.LENGTH_SHORT).show();
                                    Looper.loop();

                                }else{
                                    Looper.prepare();
                                    Toast.makeText(Register.this, "注册失败！", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }
                                break;
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });
    }
}
