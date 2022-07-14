package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.myapplication.entity.User;

import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private Button startButton;
    private OkHttpClient okHttpClient = new OkHttpClient();
    private Button getCodeButton, loginPwdButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //隐藏title
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        // FIXME: 直接跳转到地区
//        Intent intent = new Intent();
//        intent.setClass(MainActivity.this, AreaDetail.class);
//        startActivity(intent);


        init();
    }


    //初始化方法
    private void init(){
        //初始化button
        initButton();

        //初始化TextView
        initTextView();
    }

    //初始化button
    private void initButton(){
        getCodeButton = (Button)findViewById(R.id.button_code);
        startButton = (Button)findViewById(R.id.button_start);
        loginPwdButton = (Button)findViewById(R.id.button_login_pwd);

        //按钮点击事件设置
        //1.请求验证码的Button
        getCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = ((EditText)findViewById(R.id.email_edit)).getText().toString();
                if (!check_email(email)){
                    Toast.makeText(MainActivity.this, "请输入正确的邮箱！", Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try {
//                          //1、封装请求体数据
//                          FormBody formBody = new FormBody.Builder().add("description","xzx").add("photoPath","d").add("name","xzx的家").build();
                            //2、获取到请求的对象
                            while (true){
                                Request request = new Request.Builder().url("http://114.116.234.63:8080/code/sendCode?email="+email+"&type=1").get().build();
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
                                String hit = jsonObject.getString("data");
                                Looper.prepare();
                                Toast.makeText(MainActivity.this, hit, Toast.LENGTH_SHORT).show();
                                Looper.loop();
                                break;
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });

        //2.登录的Button
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = ((EditText)findViewById(R.id.email_edit)).getText().toString();
                String code = ((EditText)findViewById(R.id.code_edit)).getText().toString();
                if (!check_email(email)){
                    Toast.makeText(MainActivity.this, "请输入正确的邮箱！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (code.length()==0){
                    Toast.makeText(MainActivity.this, "验证码不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try {
                            while (true){
                                //1、封装请求体数据
                                FormBody formBody = new FormBody.Builder().add("email",email).add("codeValue",code).build();
                                //2、获取到请求的对象
                                Request request = new Request.Builder().url("http://114.116.234.63:8080/user/loginByCode").post(formBody).build();
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
                                if (code == 201){
                                    Looper.prepare();
                                    Toast.makeText(MainActivity.this, "验证码不正确！", Toast.LENGTH_SHORT).show();
                                    Looper.loop();
                                }else if (code == 200){
                                    //登陆成功
                                    User user = jsonObject.getObject("data", User.class);
                                    //跳转到app主页，把登录的邮箱传过去
                                    Intent intent = new Intent();
                                    intent.putExtra("user", user);
                                    intent.setClass(MainActivity.this,Welcome.class);
                                    startActivity(intent);
                                }else{
                                    //进入注册页面
                                    Intent intent = new Intent();
                                    intent.putExtra("user_email", email);
                                    intent.setClass(MainActivity.this,Register.class);
                                    startActivity(intent);
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

        //3.使用密码登录的Button
        loginPwdButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 跳转到密码登录界面
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, Login_pwd.class);
                startActivity(intent);
            }
        });
    }

    private void initTextView(){

    }

    public static boolean check_email(String email){

        if (email == null || email.length() < 1 || email.length() > 256) {
            return false;
        }
        Pattern pattern = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
        return pattern.matcher(email).matches();

    }

}