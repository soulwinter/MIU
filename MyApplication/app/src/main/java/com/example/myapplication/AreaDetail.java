package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.myapplication.entity.Ap;
import com.example.myapplication.entity.Area;
import com.example.myapplication.entity.Tag;
import com.example.myapplication.entity.Trace;
import com.example.myapplication.entity.TracingPoint;
import com.example.myapplication.entity.User;
import com.example.myapplication.mapDrawing.MapView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AreaDetail extends AppCompatActivity {

    private boolean successScanWifi = false;
    private List<Ap> apList = new ArrayList<>();  //记录该区域的服务器已保存的所有 Aps
    private OkHttpClient okHttpClient = new OkHttpClient();
    private Area areaObj = null;
    public static final int MIN_STRENGTH = -1000;
    private String apStr, strengthStr, x, y, area; // 上传wifi指纹的信息
    private boolean succeedRenewLocation = false;
    private int renewNeedTime = 2; // 刷新的时间间隔
    private int nowTime = 0; // 当前距离上次刷新的间隔
    public int xPosition, yPosition;
    public MapView mapView;

    private User user = null;


    private Bitmap bitmap = null; //平面图
    private List<Tag> tagList = new ArrayList<>(); //标记点
    private int tagListSize = -1;
    private List<View> tagViews = new ArrayList<>(); //记录标记图片控件，用于动态更新
    private List<Trace> traceList = new ArrayList<>(); //轨迹集合


    private List<TracingPoint> tracingPointList = new ArrayList<>();//用来记录用户的轨迹

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        areaObj = (Area)intent.getSerializableExtra("area");
        user = (User)intent.getSerializableExtra("user");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(areaObj.getName());



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_detail);

        mapView = (MapView) findViewById(R.id.area_map);
        ImageView addtagView = (ImageView) findViewById(R.id.add_tag_image);
        ImageView addtraceView = (ImageView) findViewById(R.id.add_trace_image);

        addtagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //添加标记方法请写在此处
                Intent intent1 =new Intent();
                intent1.putExtra("pointX", xPosition);
                intent1.putExtra("pointY", yPosition);
                intent1.putExtra("userId", user.getId());
                intent1.putExtra("areaId", areaObj.getId());
                intent1.setClass(AreaDetail.this,AddTag.class);
                startActivity(intent1);
            }
        });

        addtraceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //添加轨迹方法请写在此处

            }
        });

        //重新记录轨迹按钮事件绑定
        Button button = (Button) findViewById(R.id.start_record_trace_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                synchronized (tracingPointList){
                    //清空已经记录的轨迹
                    tracingPointList.clear();
                }
            }
        });

        //请求区域信息
        getAreaInfo();

        // 首先要用定位功能
        getWifiLocation();

        // 每renewNeedTime秒自动刷新一次
        new Thread(
                new Runnable() {
                    @Override
                    public void run() {
                        while (true)
                        {
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

//                            getTagList();
                            getWifiLocation();
                            //记录用户的轨迹
                            synchronized (tracingPointList){

                                if (!tracingPointList.isEmpty()){
                                    //同一个点不记录
                                    TracingPoint lastPoint = tracingPointList.get(tracingPointList.size()-1);
                                    if (lastPoint.getX() == xPosition && lastPoint.getY()==yPosition)
                                        continue;
                                }
                                TracingPoint tracingPoint = new TracingPoint();
                                String point = "(" + xPosition + "," + yPosition +","+ tracingPointList.size() +")";
                                tracingPoint.setPoint(point);
                                tracingPoint.setX(xPosition);
                                tracingPoint.setY(yPosition);
                                boolean flag = true;
                                synchronized (tagList){
                                    for (Tag tag : tagList) {
                                        if (tag.getX() == xPosition && tag.getY() == yPosition){
                                            tracingPoint.setTagId(tag.getId());
                                            flag = false;
                                        }
                                    }
                                }
                                if (flag)
                                    tracingPoint.setTagId(-1);
                                tracingPointList.add(tracingPoint);
                            }
                            mapView.directionX = xPosition;
                            mapView.directionY = yPosition;
                            succeedRenewLocation = false;
                            nowTime = 0;


                        }

                    }
                }
        ).start();

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

        getTraceList();
        getTagList();

        try {
            thread.join();
            mapView.setBitmap(bitmap);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void getTagList(){
        //获取区域所有tag
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                    try {
                        //2、获取到请求的对象
                        Request request = new Request.Builder().url("http://114.116.234.63:8080/tag/listTagByAreaId?areaId="+areaObj.getId()).get().build();
                        //3、获取到回调的对象
                        Call call = okHttpClient.newCall(request);
                        //4、执行同步请求,获取到响应对象
                        Response response = call.execute();
                        //获取json字符串
                        String json = response.body().string();

                        JSONObject jsonObject = JSONObject.parseObject(json);
                        String arrayStr = jsonObject.getString("data");
                        tagList = JSONObject.parseArray(arrayStr, Tag.class);  //该area的所有tag

                        //把tag传给mapView用于显示
                        mapView.setTagList(tagList);
                        //请求tag对应的图片
                        ImageUtil imageUtil = new ImageUtil();
                        for (Tag tag : tagList) {
                            try {
                                String path = "http://114.116.234.63:8080/image" + tag.getPicturePath();
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
                                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                                    bitmap = Bitmap.createScaledBitmap(bitmap, 350, 200, true);
                                    bitmap = imageUtil.drawTextToBitmap(AreaDetail.this,bitmap,tag.getTagName());
                                    tag.setBitmap(bitmap);
                                    showTag(tag);
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }



                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        });
        thread1.start();


    }

    private void showTag(Tag tag){
        //显示一个个的tag
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout linearLayoutTag = (LinearLayout) findViewById(R.id.linearlayout1);
                ImageView imageView = new ImageView(AreaDetail.this);
                //给每个ImageView绑定事件请写在此处
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //开启一个Activity并把tag传进去
                    }
                });
                imageView.setPadding(30,50,0,0);
                imageView.setImageBitmap(tag.getBitmap());
                linearLayoutTag.addView(imageView);
                tagViews.add(imageView);

            }
        });
    }

    private void getTraceList(){
        //获取区域所有tag
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //2、获取到请求的对象
                    Request request = new Request.Builder().url("http://114.116.234.63:8080/trace/listTraceByAreaId?areaId="+areaObj.getId()).get().build();
                    //3、获取到回调的对象
                    Call call = okHttpClient.newCall(request);
                    //4、执行同步请求,获取到响应对象
                    Response response = call.execute();
                    //获取json字符串
                    String json = response.body().string();

                    JSONObject jsonObject = JSONObject.parseObject(json);
                    String arrayStr = jsonObject.getString("data");
                    traceList = JSONObject.parseArray(arrayStr, Trace.class);  //该area的所有tag

                    //把trace传给mapView用于显示
                    mapView.setTraceList(traceList);

                    //显示一个个的trace
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LinearLayout linearLayoutTrace = (LinearLayout) findViewById(R.id.linearlayout2);

                            ImageUtil imageUtil = new ImageUtil();
                            for (Trace trace : traceList) {
                                ImageView imageView = new ImageView(AreaDetail.this);
                                //给每个ImageView绑定事件请写在此处
                                imageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        //开启一个Activity并把trace传进去
                                    }
                                });
                                Bitmap bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.trace);
                                bitmap = Bitmap.createScaledBitmap(bitmap, 350, 200, true);
                                bitmap = imageUtil.drawTextToBitmap(AreaDetail.this,bitmap,trace.getTraceName());
                                imageView.setPadding(30,50,0,0);
                                imageView.setImageBitmap(bitmap);
                                linearLayoutTrace.addView(imageView);
                            }
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread1.start();


    }

    private int getWifiLocation()
    {
        //如果没有权限，进行动态分配
        if (ActivityCompat.checkSelfPermission(AreaDetail.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AreaDetail.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);

        }

        // 使用 WifiManager 扫描 Wi-Fi
        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
        successScanWifi = wifiManager.startScan();
        if (successScanWifi) {
            runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                           // Toast.makeText(AreaDetail.this, "扫描成功", Toast.LENGTH_SHORT).show();

                        }
                    }
            );
        } else {
            runOnUiThread(
                    new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(AreaDetail.this, "扫描失败", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        }
        // 获取扫描结果
        List<Integer> strengthList = new ArrayList<>();  //记录最终要上传的strength
        List<Integer> apIdList = new ArrayList<>();   // 最终要上传的ap；总是(1,2,3,...,ap_num)，但我还是写上了
        List<ScanResult> scanResults = wifiManager.getScanResults();
        List<Integer> strengthDetectList = new ArrayList<>();  //记录检测到的strength
        List<Ap> apDetectList = new ArrayList<>();  // 当前检测到的ap

        // 获取 ap 库里所有 ap
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //2、获取到请求的对象
                    Request request = new Request.Builder().url("http://114.116.234.63:8080/ap/listApByAreaId?areaId="+areaObj.getId()).get().build();
                    //3、获取到回调的对象
                    Call call = okHttpClient.newCall(request);
                    //4、执行同步请求,获取到响应对象
                    Response response = call.execute();
                    //获取json字符串
                    String json = response.body().string();
                    System.out.println(json);
                    JSONObject jsonObject = JSONObject.parseObject(json);
                    String arrayStr = jsonObject.getString("data");
                    apList = JSONObject.parseArray(arrayStr, Ap.class);  //该area的所有ap
                    Log.i("apList1", apList.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // 获取检测到的所有ap（有ssid和bssid信息）
                for (ScanResult scanResult : scanResults) {
                    Ap ap = new Ap();
                    ap.setSsid(scanResult.SSID);
                    ap.setBssid(scanResult.BSSID);
                    apDetectList.add(ap);
                    strengthDetectList.add(scanResult.level);
                }

                Log.v("apList", apList.toString());

                // 比对ap库，将检测到的ap赋值对应强度，未检测到的ap默认强度为MIN_STRENGTH
                boolean found;
                for(int i = 0; i < apList.size(); i++){
                    found = false;
                    for(int j = 0; j < apDetectList.size(); j++){

                        if(apList.get(i).getSsid() == null || apDetectList.get(j).getSsid() == null) break;

                        // 匹配上了
                        if(Objects.equals(apList.get(i).getSsid(), apDetectList.get(j).getSsid())
                                && Objects.equals(apList.get(i).getBssid(), apDetectList.get(j).getBssid())){
//                            System.out.println("匹配上了："+i);

                            apIdList.add(i);
                            strengthList.add(strengthDetectList.get(j));
                            found = true;
                            break;
                        }
                    }
                    // 没匹配上，赋值为MIN_STRENGTH
                    if(!found){
//                        System.out.println("没匹配上："+i);
                        apIdList.add(i);
                        strengthList.add(MIN_STRENGTH);
                    }
                }

                // 将apIdList和strengthList转化为string类型存储起来，格式为(1,2,3)

                apStr = toStr(apIdList);
                strengthStr = toStr(strengthList);


                Log.i("APS", apStr);
                Log.i("STRENGTH", strengthStr);

                FormBody formBody = new FormBody.Builder()
                        .add("aps",  apStr)
                        .add("strength", strengthStr)
                        .add("areaId", String.valueOf(areaObj.getId()))
                        .build();

                Request request = new Request.Builder().url("http://114.116.234.63:8080/wifiRecord/getLocation").post(formBody).build();

                //3、获取到回调的对象
                Call call = okHttpClient.newCall(request);
                //4、执行同步请求,获取到响应对象
                Response response = null;
                try {
                    response = call.execute();
                } catch (IOException e) {
                    e.printStackTrace();

                }

                // 获取json字符串
                String json = null;
                try {
                    json = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();

                }
                JSONObject jsonObject = JSONObject.parseObject(json);
                Log.v("JSONCODE", jsonObject.toString());
                Integer code = jsonObject.getInteger("code");
                Looper.prepare();
                if (code == 200){
                    JSONObject dataObject = JSONObject.parseObject(jsonObject.getString("data"));
                    xPosition = dataObject.getInteger("x");
                    yPosition = dataObject.getInteger("y");
//                    Toast.makeText(AreaDetail.this, "X: " + String.valueOf(xPosition) + ", Y: " + String.valueOf(yPosition), Toast.LENGTH_SHORT).show();
                    succeedRenewLocation = true;
                }else{
                    Toast.makeText(AreaDetail.this, "wifi指纹上传失败！", Toast.LENGTH_SHORT).show();

                }
                Looper.loop();

            }
        }).start();



        return 0;
    }


    // 将list转化为string，string格式为(1,2,3)
    private String toStr(List<Integer> list){
        StringBuilder s = new StringBuilder("(");
        for(int i = 0; i < list.size(); i++){
            s.append(list.get(i).toString());
            if(i!=list.size()-1) s.append( ",");
        }
        s.append(")");
//        System.out.println("转化的ap和strength："+ s);
        return s.toString();
    }

    class ImageUtil {

        //gResId:图片id，gContext系统资源，
        public  Bitmap drawTextToBitmap(Context gContext, Bitmap bitmap,
                                              String gText) {
            String text = String.valueOf(gText);

            Resources resources = gContext.getResources();
            float scale = resources.getDisplayMetrics().density;
            // resource bitmaps are imutable,
            // so we need to convert it to mutable one
            bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setColor(Color.WHITE);
            paint.setTextSize((int) (12 * scale));
            paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);
            Rect bounds = new Rect();
            paint.getTextBounds(text, 0, text.length(), bounds);
            int x = (bitmap.getWidth() - bounds.width()) / 6;
            int y = (bitmap.getHeight() - bounds.height()) / 5;
            canvas.drawText(text ,x*scale, y*scale, paint);
            return bitmap;
        }
    }


}