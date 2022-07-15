package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.myapplication.entity.Area;
import com.example.myapplication.entity.TracingPoint;
import com.example.myapplication.entity.User;
import com.example.myapplication.mapDrawing.TraceView;

import org.w3c.dom.Text;

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

public class AddTrace extends AppCompatActivity {

    public TraceView traceView;
    public TextView tracePointNumber;
    public EditText traceName;
    public EditText traceDescription;
    public Button submitButton;
    private Bitmap bitmap = null; //平面图
    private Area areaObj = null;
    private User user = null;
    private List<TracingPoint> tracingPointList = new ArrayList<>();
    private OkHttpClient okHttpClient = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        Intent intent = getIntent();
        areaObj = (Area)intent.getSerializableExtra("area");
        user = (User)intent.getSerializableExtra("user");
        tracingPointList = (List<TracingPoint>) getIntent().getSerializableExtra("points");
        Log.i("IF_NIL", String.valueOf(user.getEmail()));
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("上传轨迹");



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_trace);
        traceView = (TraceView) findViewById(R.id.trace_map);
        tracePointNumber = (TextView) findViewById(R.id.traceNumber);
        submitButton = (Button) findViewById(R.id.submitTrace);
        traceName = (EditText) findViewById(R.id.traceNameInput);
        traceDescription = (EditText) findViewById(R.id.traceDescription);

        getAreaInfo();
        traceView.setTracingPointList(tracingPointList);
        tracePointNumber.setText(String.valueOf(tracingPointList.size()));
        submit();

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

    private void submit() {

        submitButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (traceName.getText().toString() == null || traceDescription.getText().toString() == null)
                        {
                            runOnUiThread(
                                    () -> Toast.makeText(AddTrace.this, "请输入完整后再提交！", Toast.LENGTH_SHORT).show()
                            );
                            return;
                        } else {
                            String traceNameText = traceName.getText().toString();
                            String traceDescriptionText = traceDescription.getText().toString();
                            String traceString = JSONObject.toJSONString(tracingPointList);
                            String areaId = String.valueOf(areaObj.getId());
                            String userId = String.valueOf(user.getId());

                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        FormBody formBody = new FormBody.Builder()
                                                .add("jsonList", traceString)
                                                .add("userId", userId)
                                                .add("areaId", areaId)
                                                .add("traceName", traceNameText)
                                                .add("description", traceDescriptionText)
                                                .build();
                                        Request request = new Request.Builder().url("http://114.116.234.63:8080/trace/addTrace").post(formBody).build();
                                        Call call = okHttpClient.newCall(request);
                                        Response response = call.execute();
                                        String json = response.body().string();
                                        JSONObject jsonObject = JSONObject.parseObject(json);
                                        Integer code = jsonObject.getInteger("code");
                                        if (code == 200){
                                            Looper.prepare();
                                            Toast.makeText(AddTrace.this, "添加成功！", Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                        }else{
                                            Looper.prepare();
                                            Toast.makeText(AddTrace.this, "添加失败！", Toast.LENGTH_SHORT).show();
                                            Looper.loop();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }).start();


                        }
                    }
                }
        );

    }
}