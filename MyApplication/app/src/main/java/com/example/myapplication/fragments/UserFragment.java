package com.example.myapplication.fragments;

import android.content.Context;
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
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.example.myapplication.MainActivity;
import com.example.myapplication.ModifyMyInfo;
import com.example.myapplication.R;
import com.example.myapplication.adapter.UserTagsAdapter;
import com.example.myapplication.adapter.UserTracesAdapter;
import com.example.myapplication.entity.Tag;
import com.example.myapplication.entity.Trace;
import com.example.myapplication.entity.User;
import android.app.Activity;
import android.content.SharedPreferences;
import static android.content.Context.MODE_PRIVATE;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class UserFragment extends Fragment {

    private String str;

    private View root;
    private User user;

    private RecyclerView clvTags, clvTrace;


    public void flushInfo(){

        TextView username1 = (TextView)root.findViewById(R.id.top_view);
        username1.setText(user.getUsername());

        TextView username2 = (TextView) root.findViewById(R.id.username);
        username2.setText(user.getUsername());

        TextView description = (TextView) root.findViewById(R.id.description);
        description.setText(user.getDescription());

        clvTags = root.findViewById(R.id.clv_tags);
        clvTrace = root.findViewById(R.id.clv_traces);

        ImageView imageView = (ImageView)root.findViewById(R.id.head_image);

        boolean flag = true;
        while (flag){
            try{
                if (user.getPhotoPath() == null || user.getPhotoPath().length() <=0){
                    Glide.with(imageView).load("http://114.116.234.63:8080/image/home/project/miu/images/user/default/deafultTouxiang.png").into(imageView);
                }else {
                    Glide.with(imageView).load("http://114.116.234.63:8080/image" + user.getPhotoPath()).into(imageView);
                }

                flag = false;
            }catch (Exception e){
                flag = true;
            }
        }
//
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user, container, false);
        this.root = root;
        user = (User)getActivity().getIntent().getSerializableExtra("user");

        //初始化页面显示信息
        flushInfo();


        Button logOutButton = (Button) root.findViewById(R.id.logout);
        logOutButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SharedPreferences sp = getActivity().getSharedPreferences("login", getActivity().MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.clear();
                        editor.apply();
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), MainActivity.class);
                        startActivity(intent);
                    }
                }
        );

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
        getUserTag();
        getUserTrace();
        //重新开启此页面需要更新user
        String email = user.getEmail();
        String password = user.getPassword();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true){
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
                        if (jsonObject == null){
                            continue;
                        }
                        Integer code = jsonObject.getInteger("code");
                        if (code == 200) {
                            //登陆成功
                            user = jsonObject.getObject("data", User.class);
                        }
                        break;
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


    private void getUserTag() {
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true){
                        //2、获取到请求的对象
                        Request request = new Request.Builder().url("http://114.116.234.63:8080/tag/listTagByUserId?userId="
                                +user.getId()).get().build();
                        //3、获取到回调的对象
                        OkHttpClient okHttpClient = new OkHttpClient();
                        Call call = okHttpClient.newCall(request);
                        //4、执行同步请求,获取到响应对象
                        Response response = call.execute();
                        //获取json字符串
                        String json = response.body().string();

                        JSONObject jsonObject = JSONObject.parseObject(json);
                        if (jsonObject == null){
                            continue;
                        }
                        String arrayStr = jsonObject.getString("data");
                        final List<Tag> tagList = new ArrayList<>();
                        synchronized (tagList){
                            tagList.addAll(JSONObject.parseArray(arrayStr, Tag.class));
                            clvTags.post(new Runnable() {
                                @Override
                                public void run() {
                                    clvTags.setAdapter(new UserTagsAdapter(tagList));
                                }
                            });
                        }
                        break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        thread1.start();
    }

    private void getUserTrace() {
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true){
                        //2、获取到请求的对象
                        Request request = new Request.Builder().url("http://114.116.234.63:8080/trace/listTraceByUserId?userId="
                                +user.getId()).get().build();
                        //3、获取到回调的对象
                        OkHttpClient okHttpClient = new OkHttpClient();
                        Call call = okHttpClient.newCall(request);
                        //4、执行同步请求,获取到响应对象
                        Response response = call.execute();
                        //获取json字符串
                        String json = response.body().string();

                        JSONObject jsonObject = JSONObject.parseObject(json);
                        if (jsonObject == null)
                            continue;
                        String arrayStr = jsonObject.getString("data");
                        final List<Trace> traceList = new ArrayList<>();
                        synchronized (traceList){
                            traceList.addAll(JSONObject.parseArray(arrayStr, Trace.class));
                            clvTrace.post(new Runnable() {
                                @Override
                                public void run() {
                                    clvTrace.setAdapter(new UserTracesAdapter(traceList));
                                }
                            });
                        }
                        break;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        thread1.start();
    }
}
