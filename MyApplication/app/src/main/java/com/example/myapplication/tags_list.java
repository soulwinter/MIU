package com.example.myapplication;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.fastjson.JSONObject;
import com.example.myapplication.adapter.tags_list_adapter;
import com.example.myapplication.entity.Area;
import com.example.myapplication.entity.Tag;


import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


import okhttp3.Call;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import androidx.drawerlayout.widget.DrawerLayout;

public class tags_list  extends AppCompatActivity  {

    private int  areaid;//区域id

    private List<Tag> tagsL = new ArrayList<>();

    private tags_list_adapter adapter;
    private OkHttpClient okHttpClient = new OkHttpClient();


    private tagHandler handler1 ;

    private RecyclerView recyclerView;

    private DrawerLayout mDrawerLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tags_list);

        //隐藏title
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        Intent get_intent = getIntent();
        areaid = get_intent.getIntExtra("areaId", areaid);

        handler1 = new tagHandler();


        recyclerView=(RecyclerView)findViewById(R.id.recycler_view);
        //GridLayoutManager的构造方法接收两个参数，第一个是Context,第二个是列数，这里我们希望每一行中会有两列数据
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);
//        initTags();

        tagsL.clear();


        String areaIdStr = JSONObject.toJSONString(areaid);

        //  从服务器获取区域信息
        new Thread(new Runnable(){
            @Override
            public void run() {
                try {

                    while (true){
                        //2、获取到请求的对象
                        Request request = new Request.Builder().url("http://114.116.234.63:8080/tag/listTagByAreaId?areaId="+areaIdStr).get().build();
                        //3、获取到回调的对象
                        Call call = okHttpClient.newCall(request);

                        //4、执行同步请求,获取到响应对象
                        Response response = call.execute();


                        //获取json字符串
                        String json = response.body().string();
                        System.out.println(json);
                        JSONObject jsonObject = JSONObject.parseObject(json);
                        if (jsonObject == null)
                            continue;
                        String arrayStr = jsonObject.getString("data");


                        tagsL = JSONObject.parseArray(arrayStr, Tag.class);
                        adapter = new tags_list_adapter(tagsL);

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

    }


    class tagHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            recyclerView.setAdapter(adapter);
        }
    }






}
