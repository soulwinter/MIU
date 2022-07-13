package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.example.myapplication.entity.Area;
import com.example.myapplication.entity.User;
import com.example.myapplication.mapDrawing.TraceView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class AddTrace extends AppCompatActivity {

    public TraceView traceView;
    private Bitmap bitmap = null; //平面图
    private Area areaObj = null;
    private User user = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        areaObj = (Area)intent.getSerializableExtra("area");
        user = (User)intent.getSerializableExtra("user");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("上传轨迹");

        traceView = (TraceView) findViewById(R.id.trace_map);


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trace);
    }

    private void getAreaInfo(){
        //获取区域平面图
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String path = "http://114.116.234.63:8080/image" + areaObj.getPhotoPath();
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
                        bitmap = BitmapFactory.decodeStream(is);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();


        try {
            thread.join();
            traceView.setBitmap(bitmap);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}