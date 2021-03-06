package com.example.myapplication.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.adapter.area_adapter;
import com.example.myapplication.AreaDetail;
import com.example.myapplication.entity.Area;

import com.example.myapplication.R;

import android.content.SharedPreferences;

import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;

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


    private ListView mAreaListView;
    private List<Area> arealist = new ArrayList<>();
    private area_adapter mAdapter;
    private OkHttpClient okHttpClient = new OkHttpClient();


    private MyHandler  handler1;



    class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            mAreaListView.setAdapter( mAdapter);
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @SuppressLint("ResourceType")
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        mAreaListView = root.findViewById(R.id.id_lv_msgList);
        handler1 = new MyHandler();


        //  从服务器获取区域信息
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    while (true){
                        //2、获取到请求的对象
                        Request request = new Request.Builder().url("http://114.116.234.63:8080/area/listArea").get().build();
                        //3、获取到回调的对象
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


                        arealist = JSONObject.parseArray(arrayStr, Area.class);


                        mAdapter = new area_adapter(inflater, arealist);

                        Message msg = new Message();

                        msg.what = 100;  //消息发送的标志
                        msg.obj = "adapter"; //消息发送的内容如：  Object String 类 int
                        handler1.sendMessage(msg);
                        break;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }).start();

        mAreaListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                ListView listView = (ListView) adapterView;
                Area area = (Area) listView.getItemAtPosition(position);

                Intent intent = new Intent(getActivity(), AreaDetail.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("area", area);
                bundle.putSerializable("user", getActivity().getIntent().getSerializableExtra("user"));
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        return root;
    }

}





