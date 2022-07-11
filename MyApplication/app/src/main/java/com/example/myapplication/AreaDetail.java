package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.myapplication.entity.Ap;
import com.example.myapplication.entity.User;
import com.example.myapplication.mapDrawing.MapView;

import java.io.IOException;
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
    private int areaId = 18;
    public static final int MIN_STRENGTH = -1000;
    private String apStr, strengthStr, x, y, area; // 上传wifi指纹的信息
    private boolean succeedRenewLocation = false;
    private int renewNeedTime = 2; // 刷新的时间间隔
    private int nowTime = 0; // 当前距离上次刷新的间隔
    public int xPosition, yPosition;
    public MapView mapView;

    TextView renewTimeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        areaId = intent.getIntExtra("area_id", -1);



        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_detail);
        renewTimeText = (TextView) findViewById(R.id.next_time);
        mapView = (MapView) findViewById(R.id.area_map);



        // 首先要用定位功能
        getWifiLocation();

        Button renewLocation = (Button) findViewById(R.id.get_location);
        renewLocation.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getWifiLocation();
                    }
                }
        );







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
                            if (renewNeedTime > nowTime) {
                                nowTime++;
                                runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                renewTimeText.setText(String.valueOf(renewNeedTime - nowTime));
                                            }
                                        }
                                );
                            } else {
                                getWifiLocation();
                                while (!succeedRenewLocation) {

                                }
                                runOnUiThread(
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                mapView.directionX = xPosition;
                                                mapView.directionY = yPosition;
                                                //mapView.postInvalidate();
                                                Toast.makeText(AreaDetail.this, "X: " + String.valueOf(xPosition) + ", Y: " + String.valueOf(yPosition), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                );
                                succeedRenewLocation = false;
                                nowTime = 0;

                            }
                        }

                    }
                }
        ).start();

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
                    Request request = new Request.Builder().url("http://114.116.234.63:8080/ap/listApByAreaId?areaId="+areaId).get().build();
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
                        .add("areaId", String.valueOf(areaId))
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

}