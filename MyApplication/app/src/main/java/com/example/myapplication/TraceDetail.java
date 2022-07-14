package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.myapplication.entity.Area;
import com.example.myapplication.entity.Trace;
import com.example.myapplication.entity.User;
import com.example.myapplication.mapDrawing.TraceView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.OkHttpClient;

public class TraceDetail extends AppCompatActivity {

    Trace trace;
    public TraceView traceView;
    public TextView tracePointNumber;
    public TextView traceNameText;
    public TextView traceDescriptionText;

    private Area areaObj = null;
    private User user = null;
    private Bitmap bitmap = null; //平面图
    private OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        trace = (Trace) intent.getSerializableExtra("trace");
        areaObj = (Area) intent.getSerializableExtra("area");
        user = (User) intent.getSerializableExtra("user");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trace_detail);

        traceView = (TraceView) findViewById(R.id.trace_map2);
        getAreaInfo();
        tracePointNumber = (TextView) findViewById(R.id.traceNumber2);
        traceNameText = (TextView) findViewById(R.id.traceNameDetail);
        traceDescriptionText = (TextView) findViewById(R.id.traceDeseriptionDetail);

        tracePointNumber.setText(String.valueOf(trace.getPointList().size()));
        traceNameText.setText(trace.getTraceName());
        traceDescriptionText.setText(trace.getDescription());


    }

    private void getAreaInfo(){
        //获取区域平面图
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String path = "http://114.116.234.63:8080/image" + areaObj.getPhotoPath();
                    Log.i("PATH", path);
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