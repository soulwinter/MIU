package com.example.myapplication.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.msgadapter;
import com.example.myapplication.R;

import android.content.SharedPreferences;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.example.myapplication.entity.Area;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;
import okhttp3.Response;

import okhttp3.OkHttpClient;


import android.os.Handler;
import android.os.Message;


public class HomeFragment extends Fragment {

    SharedPreferences sp;
    TextView showhello;

    private ListView mLvMsgList;
    private List<Area> mDatas = new ArrayList<>();
    private msgadapter mAdapter;
    private OkHttpClient okHttpClient = new OkHttpClient();

    private ListView listView;

    private MyHandler  handler1;

    LayoutInflater inflater1 ;

    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            mLvMsgList.setAdapter(mAdapter);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

//    private MenuInflater getMenuInflater() {
//        return MenuInflater;
//    }

//    @Override//普通菜单
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.toolbar_menu_list, menu);
//        //返回true代表普通菜单显示
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.menu_my:
//                Toast.makeText(this, "我的", Toast.LENGTH_SHORT).show();
//                break;
//                .....
//        }
//        return super.onOptionsItemSelected(item);
//    }


    @SuppressLint("ResourceType")
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        mLvMsgList = root.findViewById(R.id.id_lv_msgList);
//        mDatas = netgetArea();
//        mAdapter = new msgadapter(inflater, mDatas);
//        mLvMsgList.setAdapter(mAdapter);






        handler1 = new MyHandler();


        // TODO 从服务器获取区域信息
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    //2、获取到请求的对象
                    Request request = new Request.Builder().url("http://114.116.234.63:8080/area/listArea").get().build();
                    //3、获取到回调的对象
                    Call call = okHttpClient.newCall(request);

                    //4、执行同步请求,获取到响应对象
                    Response response = call.execute();


                    //获取json字符串
                    String json = response.body().string();
                    System.out.println(json);
                    JSONObject jsonObject = JSONObject.parseObject(json);
                    String arrayStr = jsonObject.getString("data");


                    mDatas = JSONObject.parseArray(arrayStr, Area.class);


                    mAdapter = new msgadapter(inflater, mDatas);
                    //不能在子线程操作ui
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            mLvMsgList.setAdapter(mAdapter);
//                        }
//                    });

                    Message msg = new Message();

                    msg.what = 100;  //消息发送的标志
                    msg.obj = "adapter"; //消息发送的内容如：  Object String 类 int
                    handler1.sendMessage(msg);


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        return root;
    }
//
//    public  List<Area> netgetArea (){
////        mLvMsgList = findViewById(R.id.id_lv_msgList);
//        // TODO 从服务器获取区域信息
//        new Thread(new Runnable(){
//            @Override
//            public void run() {
//                try {
//                    //2、获取到请求的对象
//                    Request request = new Request.Builder().url("http://114.116.234.63:8080/area/listArea").get().build();
//                    //3、获取到回调的对象
//                    Call call = okHttpClient.newCall(request);
//
//                    //4、执行同步请求,获取到响应对象
//                    Response response = call.execute();
//
//
//                    //获取json字符串
//                    String json = response.body().string();
//                    System.out.println(json);
//                    JSONObject jsonObject = JSONObject.parseObject(json);
//                    String arrayStr = jsonObject.getString("data");
//
//
//                    mDatas = JSONObject.parseArray(arrayStr, Area.class);
//
//
////                    mAdapter = new msgadapter(inflater, mDatas);
//                    //不能在子线程操作ui
////                    runOnUiThread(new Runnable() {
////                        @Override
////                        public void run() {
////                            mLvMsgList.setAdapter(mAdapter);
////                        }
////                    });
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//        return mDatas;
//    }
}





