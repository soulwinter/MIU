package com.example.myapplication.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;

import com.alibaba.fastjson.JSONObject;
import com.example.myapplication.ModifyMyInfo;
import com.example.myapplication.R;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class UserFragment extends Fragment {

    private String str;
    private MyHandler handler1;
    private View root;
    private User user;

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            ImageView imageView = (ImageView)root.findViewById(R.id.head_image);
            imageView.setImageBitmap((Bitmap)msg.obj);
        }
    }

    public void flushInfo(){

        TextView username1 = (TextView)root.findViewById(R.id.top_view);
        username1.setText(user.getUsername());

        TextView username2 = (TextView) root.findViewById(R.id.username);
        username2.setText(user.getUsername());

        TextView description = (TextView) root.findViewById(R.id.description);
        description.setText(user.getDescription());

        handler1 = new MyHandler();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    String path = "http://114.116.234.63:8080/image" + user.getPhotoPath();
                    //2:把网址封装为一个URL对象
                    URL url = new URL(path);
                    //3:获取客户端和服务器的连接对象，此时还没有建立连接
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    //4:初始化连接对象
                    conn.setRequestMethod("GET");
                    //设置连接超时
                    conn.setConnectTimeout(8000);
                    //设置读取超时
                    conn.setReadTimeout(8000);
                    //5:发生请求，与服务器建立连接
                    conn.connect();
                    //如果响应码为200，说明请求成功
                    if (conn.getResponseCode() == 200) {
                        //获取服务器响应头中的流
                        InputStream is = conn.getInputStream();
                        //读取流里的数据，构建成bitmap位图
                        Bitmap bm = BitmapFactory.decodeStream(is);
                        Message msg = new Message();
                        msg.obj = bm;
                        handler1.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user, container, false);
        this.root = root;

        user = (User)getActivity().getIntent().getSerializableExtra("user");

        //初始化页面显示信息
        flushInfo();

        //事件绑定(修改个人信息)
        Button changeInfoButton = (Button) root.findViewById(R.id.changeInfoButton);
        changeInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("user", user);
                intent.setClass(getActivity(), ModifyMyInfo.class);
                startActivity(intent);
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        //重新开启此页面需要更新user
        String email = user.getEmail();
        String password = user.getPassword();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //1、封装请求体数据
                    FormBody formBody = new FormBody.Builder().add("email", email).add("password", password).build();
                    //2、获取到请求的对象
                    Request request = new Request.Builder().url("http://114.116.234.63:8080/user/loginByPassword").post(formBody).build();
                    //3、获取到回调的对象
                    OkHttpClient okHttpClient = new OkHttpClient();
                    Call call = okHttpClient.newCall(request);
                    //4、执行同步请求,获取到响应对象
                    Response response = call.execute();

                    //获取json字符串
                    String json = response.body().string();
                    JSONObject jsonObject = JSONObject.parseObject(json);
                    Integer code = jsonObject.getInteger("code");
                    if (code == 200) {
                        //登陆成功
                        user = jsonObject.getObject("data", User.class);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();
        //等待数据更新进行flushInfo
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        flushInfo();

    }
}
