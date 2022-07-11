package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FindPassword extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //隐藏title
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_password);

        init();
    }

    private void init(){
        //初始化button
        initButton();

    }


    private void initButton() {
        Button codeButton = (Button) findViewById(R.id.button_code);
        codeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText emailEdit = (EditText) findViewById(R.id.email_edit);
                String email = emailEdit.getText().toString();
                if (check_email(email)){
                    new Thread(new Runnable(){
                        @Override
                        public void run() {
                            try {
                                //2、获取到请求的对象
                                Request request = new Request.Builder().url("http://114.116.234.63:8080/code/sendCode?email="+email+"&type=0").get().build();
                                //3、获取到回调的对象
                                OkHttpClient okHttpClient = new OkHttpClient();
                                Call call = okHttpClient.newCall(request);
                                //4、执行同步请求,获取到响应对象
                                Response response = call.execute();
                                //获取json字符串
                                String json = response.body().string();
                                JSONObject jsonObject = JSONObject.parseObject(json);
                                Integer code = jsonObject.getInteger("code");
                                String hit = jsonObject.getString("data");
                                Looper.prepare();
                                Toast.makeText(FindPassword.this, hit, Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();
                    return ;
                }
                Toast.makeText(FindPassword.this, "请输入正确的邮箱！", Toast.LENGTH_SHORT).show();
            }
        });

        Button findButton = (Button) findViewById(R.id.button_find);
        findButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText emailEdit = (EditText) findViewById(R.id.email_edit);
                EditText codeEdit = (EditText) findViewById(R.id.code_edit);
                EditText passwordEdit = (EditText) findViewById(R.id.new_password_edit);
                String email = emailEdit.getText().toString();
                String codeValue = codeEdit.getText().toString();
                String newPassword = passwordEdit.getText().toString();
                if (!check_email(email)){
                    Toast.makeText(FindPassword.this, "请输入正确的邮箱！", Toast.LENGTH_SHORT).show();
                    return ;
                }
                if (codeValue.isEmpty()){
                    Toast.makeText(FindPassword.this, "验证码不能为空！", Toast.LENGTH_SHORT).show();
                    return ;
                }
                if (newPassword.isEmpty()){
                    Toast.makeText(FindPassword.this, "密码不能为空！", Toast.LENGTH_SHORT).show();
                    return ;
                }
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        try {
                            //1、封装请求体数据
                            FormBody formBody = new FormBody.Builder().add("email",email).add("codeValue",codeValue).add("newPassword",newPassword).build();
                            //2、获取到请求的对象
                            Request request = new Request.Builder().url("http://114.116.234.63:8080/user/updatePassword").post(formBody).build();
                            //3、获取到回调的对象
                            OkHttpClient okHttpClient = new OkHttpClient();
                            Call call = okHttpClient.newCall(request);
                            //4、执行同步请求,获取到响应对象
                            Response response = call.execute();
                            //获取json字符串
                            String json = response.body().string();
                            JSONObject jsonObject = JSONObject.parseObject(json);
                            Integer code = jsonObject.getInteger("code");
                            if (code == 200){
                                Looper.prepare();
                                Toast.makeText(FindPassword.this, "修改密码成功！", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }else{
                                Looper.prepare();
                                Toast.makeText(FindPassword.this, "验证码不正确！", Toast.LENGTH_SHORT).show();
                                Looper.loop();
                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();

            }
        });
    }

    private boolean check_email(String email){

        if (email == null || email.length() < 1 || email.length() > 256) {
            return false;
        }
        Pattern pattern = Pattern.compile("^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$");
        return pattern.matcher(email).matches();

    }
}