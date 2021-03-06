package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.example.myapplication.adapter.areadetail_tags_adapter;
import com.example.myapplication.adapter.areadetail_trace_adapter;
import com.example.myapplication.entity.Ap;
import com.example.myapplication.entity.Area;
import com.example.myapplication.entity.Tag;
import com.example.myapplication.entity.Trace;
import com.example.myapplication.entity.TracingPoint;
import com.example.myapplication.entity.User;
import com.example.myapplication.mapDrawing.MapView;
import com.example.myapplication.multi.CommentMultiActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
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
    private List<Ap> apList = new ArrayList<>();  //????????????????????????????????????????????? Aps
    private OkHttpClient okHttpClient = new OkHttpClient();
    private Area areaObj = null;
    public static final int MIN_STRENGTH = -1000;
    private String apStr, strengthStr, x, y, area; // ??????wifi???????????????
    private boolean succeedRenewLocation = false;
    private int renewNeedTime = 2; // ?????????????????????
    private int nowTime = 0; // ?????????????????????????????????
    public int xPosition, yPosition;
    public MapView mapView;

    private User user = null;


    private Bitmap bitmap = null; //?????????
    private List<Tag> tagList = new ArrayList<>(); //?????????
    private int tagListSize = -1;
    private List<View> tagViews = new ArrayList<>(); //?????????????????????????????????????????????
    private List<Trace> traceList = new ArrayList<>(); //????????????
    private List<User> userList = new ArrayList<>();


    private List<TracingPoint> tracingPointList = new ArrayList<>();//???????????????????????????

    private areadetail_tags_adapter tagAdapter ;
    private areadetail_trace_adapter traceAdapter;
    private Context context;

//    private RecyclerView areaDetailTraceRc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        Intent intent = getIntent();
        areaObj = (Area)intent.getSerializableExtra("area");
        user = (User)intent.getSerializableExtra("user");

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(areaObj.getName());



        context  = AreaDetail.this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_area_detail);

        mapView = (MapView) findViewById(R.id.area_map);
//        ImageView addtagView = (ImageView) findViewById(R.id.add_tag_image);
//        ImageView addtraceView = (ImageView) findViewById(R.id.add_trace_image);
        CardView addTagCard = (CardView)findViewById(R.id.add_tag_card);
        CardView addTraceCard = (CardView)findViewById(R.id.add_trace_card);
        TextView textClick_forTag = findViewById(R.id.biaojigailan);//???????????? ??????
        TextView textClick_forTrace = findViewById(R.id.luxianfenxiang); //???????????? ??????
        FloatingActionButton flbt = findViewById(R.id.floatingActionButton);


//        ??????????????????????????????????????????
        flbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.putExtra("areaId", areaObj.getId());
                intent.putExtra("userId", user.getEmail());//
                intent.putExtra("areaName", areaObj.getName());
                intent.setClass(AreaDetail.this,ChatActivity.class);
                startActivity(intent);
            }
        });


        //      ??????????????????????????????????????????
        textClick_forTag.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.putExtra("areaId1", areaObj.getId());
                intent.putExtra("userId1", user.getId());
                intent.setClass(AreaDetail.this,tags_list.class);
                startActivity(intent);
            }

        });

        addTagCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //?????????????????????????????????
                Intent intent1 =new Intent();
                intent1.putExtra("pointX", xPosition);
                intent1.putExtra("pointY", yPosition);
                intent1.putExtra("userId", user.getId());
                intent1.putExtra("areaId", areaObj.getId());
                intent1.setClass(AreaDetail.this,AddTag.class);
                startActivity(intent1);
            }
        });

        addTraceCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // Log.i("IF_NIL", String.valueOf(tracingPointList.size()));
                //?????????????????????????????????
                Intent intent1 = new Intent();

                intent1.putExtra("area", areaObj);
                intent1.putExtra("user", user);
                intent1.putExtra("points", (Serializable) tracingPointList);
                intent1.setClass(AreaDetail.this, AddTrace.class);
                startActivity(intent1);
            }
        });


        //????????????????????????????????????
        Button button = (Button) findViewById(R.id.start_record_trace_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                synchronized (tracingPointList){
                    //???????????????????????????
                    tracingPointList.clear();
                }
            }
        });

        //??????????????????
        getAreaInfo();

        // ????????????????????????
        getWifiLocation();

        // ???renewNeedTime?????????????????????
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

                            getWifiLocation();
                            System.out.println("?????? Wi-Fi ??????");
                            mapView.setUserList(userList);
                            succeedRenewLocation = false;
                            nowTime = 0;
                            //?????????????????????
                            synchronized (tracingPointList){

                                if (!tracingPointList.isEmpty()){
                                    //?????????????????????
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
                                mapView.setTracingPointList(tracingPointList);



                            }
//                            mapView.directionX = xPosition;
//                            mapView.directionY = yPosition;



                        }

                    }
                }
        ).start();

    }

    private void getAreaInfo(){
        //?????????????????????
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        String path = "http://114.116.234.63:8080/image" + areaObj.getPhotoPath();
                        //2:????????????????????????URL??????
                        URL url = new URL(path);
                        //3:????????????????????????????????????????????????????????????????????????
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        //4:?????????????????????
                        conn.setRequestMethod("GET");
                        //??????????????????
                        conn.setConnectTimeout(8000);
                        //??????????????????
                        conn.setReadTimeout(8000);
                        //5:???????????????????????????????????????
                        conn.connect();
                        //??????????????????200?????????????????????
                        if (conn.getResponseCode() == 200) {
                            //?????????????????????????????????
                            InputStream is = conn.getInputStream();
                            //?????????????????????????????????bitmap??????
                            bitmap = BitmapFactory.decodeStream(is);
                        }
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
        //??????????????????tag
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                    try {
                        while (true){
                            //2???????????????????????????
                            Request request = new Request.Builder().url("http://114.116.234.63:8080/tag/listTagByAreaId?areaId="+areaObj.getId()).get().build();
                            //3???????????????????????????
                            Call call = okHttpClient.newCall(request);
                            //4?????????????????????,?????????????????????
                            Response response = call.execute();
                            //??????json?????????
                            String json = response.body().string();

                            JSONObject jsonObject = JSONObject.parseObject(json);
                            if (jsonObject == null)
                                continue;
                            String arrayStr = jsonObject.getString("data");
                            tagList = JSONObject.parseArray(arrayStr, Tag.class);  //???area?????????tag


                            //??????????????????adapter
                            tagAdapter = new areadetail_tags_adapter(tagList);

                            //???tag??????mapView????????????
                            mapView.setTagList(tagList);
                            //??????tag???????????????
                            ImageUtil imageUtil = new ImageUtil();
                            for (Tag tag : tagList) {
                                try {
                                    if (tag.getPicturePath() == null)
                                        continue;
                                    String path = "http://114.116.234.63:8080/image" + tag.getPicturePath();
                                    //2:????????????????????????URL??????
                                    URL url = new URL(path);
                                    //3:????????????????????????????????????????????????????????????????????????
                                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                                    //4:?????????????????????
                                    conn.setRequestMethod("GET");
                                    //??????????????????
                                    conn.setConnectTimeout(8000);
                                    //??????????????????
                                    conn.setReadTimeout(8000);
                                    //5:???????????????????????????????????????
                                    conn.connect();
                                    //??????????????????200?????????????????????
                                    if (conn.getResponseCode() == 200) {
                                        //?????????????????????????????????
                                        InputStream is = conn.getInputStream();
                                        //?????????????????????????????????bitmap??????
                                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                                        bitmap = Bitmap.createScaledBitmap(bitmap, 350, 200, true);
//
                                        tag.setBitmap(bitmap);

                                        //UI????????????
                                        showTag(tag);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            break;
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
        });
        thread1.start();


    }


    private void showTag(Tag tag){
        //??????????????????tag
        runOnUiThread(new Runnable() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void run() {
                LinearLayout linearLayoutTag = (LinearLayout) findViewById(R.id.id_small_area_tags);
               //??????cardview ???viewhold??????????????????
                areadetail_tags_adapter.ViewHolder holder = tagAdapter.onCreateViewHolder(linearLayoutTag,0);


                //?????????ImageView???????????????????????????
                holder.cardView.setOnTouchListener(new View.OnTouchListener() {
                    private long firstClickTime;
                    private long secondClickTime;
                    private long stillTime;
                    private boolean isUp=false;
                    @SuppressLint("ClickableViewAccessibility")
                    private boolean isDoubleClick=false;

                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        switch (event.getAction()){
                            case MotionEvent.ACTION_DOWN:
                                isUp=false;
                                if(firstClickTime==0&secondClickTime==0){//???????????????
                                    firstClickTime=System.currentTimeMillis();
                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(!isUp){
                                                //??????
                                                firstClickTime=0;
                                                secondClickTime=0;
                                                isDoubleClick=false;
                                                mapView.setNowTag(tag);
                                            }else {
                                                if(!isDoubleClick){
                                                    //??????????????????tag?????????

                                                    Intent intent = new Intent(context, CommentMultiActivity.class);

                                                    intent.putExtra("tagName",tag.getTagName());
                                                    intent.putExtra("tagDescribe",tag.getTagDescription());
                                                    intent.putExtra("tagPhotoPath",tag.getPicturePath());
                                                    intent.putExtra("userID",user.getId());
                                                    intent.putExtra("areaID",areaObj.getId());
                                                    intent.putExtra("user", user);
                                                    startActivity(intent);


                                                }
                                                isDoubleClick=false;
                                                firstClickTime=0;
                                                secondClickTime=0;
                                            }
                                        }
                                    }, 1000); //??????1s

                                }else {
                                    secondClickTime=System.currentTimeMillis();
                                    stillTime =secondClickTime-firstClickTime;
                                }
                                break;
                            case MotionEvent.ACTION_UP:
                                isUp=true;
                                break;
                        }
                        return true;
                    }
                });
//
                tagAdapter.initCardview(holder,tag);
//
                linearLayoutTag.addView(holder.cardView);
                tagViews.add(holder.cardView);


            }
        });
    }

    private void getTraceList(){
        //?????????????????? Trace
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true){
                        //2???????????????????????????
                        Request request = new Request.Builder().url("http://114.116.234.63:8080/trace/listTraceByAreaId?areaId="+areaObj.getId()).get().build();
                        //3???????????????????????????
                        Call call = okHttpClient.newCall(request);
                        //4?????????????????????,?????????????????????
                        Response response = call.execute();
                        //??????json?????????
                        String json = response.body().string();

                        JSONObject jsonObject = JSONObject.parseObject(json);
                        if (jsonObject == null)
                            continue;
                        String arrayStr = jsonObject.getString("data");
                        traceList = JSONObject.parseArray(arrayStr, Trace.class);  //???area?????????tag

//                        ??????????????????
                        traceAdapter = new areadetail_trace_adapter(traceList);


                        //???trace??????mapView????????????
                        mapView.setTraceList(traceList);

                        //??????????????????trace
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

//                                ????????????
                                LinearLayout areaDetailTraceRc = findViewById(R.id.id_areadetai_trace);

                                //??????RecyclerView?????????????????????????????????LayoutManter,?????????????????????????????????

//                                LinearLayoutManager lm2 = new LinearLayoutManager(getApplicationContext(),LinearLayoutManager.HORIZONTAL,false);

//                               areaDetailTraceRc.setLayoutManager(lm2);

//                                areaDetailTraceRc.setAdapter(traceAdapter);

                                //??????viewholder,??????trace??????
                                areadetail_trace_adapter.ViewHolder holder2 = traceAdapter.onCreateViewHolder(areaDetailTraceRc,0);




                                ImageUtil imageUtil = new ImageUtil();
                                for (Trace trace : traceList) {

                                    holder2.cardView.setOnClickListener(new View.OnClickListener() {

                                    //?????????cardView????????????????????????
                                        @Override
                                        public void onClick(View view) {
//                                            //????????????Activity??????trace?????????
                                            Intent intent1 = new Intent();
                                            intent1.putExtra("trace", trace);
                                            intent1.putExtra("area", areaObj);
                                            intent1.putExtra("user", user);
                                            intent1.setClass(AreaDetail.this, TraceDetail.class);
                                            startActivity(intent1);
                                        }
                                    });

                                    //??????????????????
                                    traceAdapter.initTraceCardview(holder2,trace);
                                    if (holder2.cardView.getParent() != null) {
                                        ((ViewGroup)holder2.cardView.getParent()).removeView(holder2.cardView);
                                    }
                                    areaDetailTraceRc.addView(holder2.cardView);
                                }
                            }
                        });
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread1.start();


    }

    private int getWifiLocation()
    {
        //???????????????????????????????????????
        if (ActivityCompat.checkSelfPermission(AreaDetail.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AreaDetail.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);

        }

        // ?????? WifiManager ?????? Wi-Fi
        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiManager.setWifiEnabled(true);
        successScanWifi = wifiManager.startScan();

        // ??????????????????
        List<Integer> strengthList = new ArrayList<>();  //????????????????????????strength
        List<Integer> apIdList = new ArrayList<>();   // ??????????????????ap?????????(1,2,3,...,ap_num)????????????????????????
        List<ScanResult> scanResults = wifiManager.getScanResults();
        List<Integer> strengthDetectList = new ArrayList<>();  //??????????????????strength
        List<Ap> apDetectList = new ArrayList<>();  // ??????????????????ap

        // ?????? ap ???????????? ap
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true)
                    {
                        //2???????????????????????????
                        Request request = new Request.Builder().url("http://114.116.234.63:8080/ap/listApByAreaId?areaId="+areaObj.getId()).get().build();
                        //3???????????????????????????
                        Call call = okHttpClient.newCall(request);
                        //4?????????????????????,?????????????????????
                        Response response = call.execute();
                        //??????json?????????
                        String json = response.body().string();
                        System.out.println(json);
                        JSONObject jsonObject = JSONObject.parseObject(json);
                        if (jsonObject == null)
                        {
                            continue;
                        } else {
                            String arrayStr = jsonObject.getString("data");
                            apList = JSONObject.parseArray(arrayStr, Ap.class);  //???area?????????ap
                            break;
                        }

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                // ????????????????????????ap??????ssid???bssid?????????
                for (ScanResult scanResult : scanResults) {
                    Ap ap = new Ap();
                    ap.setSsid(scanResult.SSID);
                    ap.setBssid(scanResult.BSSID);
                    apDetectList.add(ap);
                    strengthDetectList.add(scanResult.level);
                }

                Log.v("apList", apList.toString());

                // ??????ap?????????????????????ap????????????????????????????????????ap???????????????MIN_STRENGTH
                boolean found;
                for(int i = 0; i < apList.size(); i++){
                    found = false;
                    for(int j = 0; j < apDetectList.size(); j++){

                        if(apList.get(i).getSsid() == null || apDetectList.get(j).getSsid() == null) break;

                        // ????????????
                        if(Objects.equals(apList.get(i).getSsid(), apDetectList.get(j).getSsid())
                                && Objects.equals(apList.get(i).getBssid(), apDetectList.get(j).getBssid())){
//                            System.out.println("???????????????"+i);

                            apIdList.add(i);
                            strengthList.add(strengthDetectList.get(j));
                            found = true;
                            break;
                        }
                    }
                    // ????????????????????????MIN_STRENGTH
                    if(!found){
//                        System.out.println("???????????????"+i);
                        apIdList.add(i);
                        strengthList.add(MIN_STRENGTH);
                    }
                }

                // ???apIdList???strengthList?????????string??????????????????????????????(1,2,3)

                apStr = toStr(apIdList);
                strengthStr = toStr(strengthList);


                Log.i("APS", apStr);
                Log.i("STRENGTH", strengthStr);

                FormBody formBody = new FormBody.Builder()
                        .add("aps",  apStr)
                        .add("strength", strengthStr)
                        .add("areaId", String.valueOf(areaObj.getId()))
                        .add("id", String.valueOf(user.getId()))
                        .add("username", user.getUsername())
                        .add("email", user.getEmail())
                        .add("photoPath", user.getPhotoPath())
                        .add("gender", user.getGender()? "1" : "0")
                        .add("ifShare", user.getIfShare()? "1" : "0")
                        .build();

                while (true){
                    Request request = new Request.Builder().url("http://114.116.234.63:8080/wifiRecord/getLocationAndAliveUser").post(formBody).build();

                    //3???????????????????????????
                    Call call = okHttpClient.newCall(request);
                    //4?????????????????????,?????????????????????
                    Response response = null;
                    try {
                        response = call.execute();
                    } catch (IOException e) {
                        e.printStackTrace();

                    }

                    // ??????json?????????
                    String json = null;
                    try {
                        json = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();

                    }
                    JSONObject jsonObject = JSONObject.parseObject(json);
                    if (jsonObject == null)
                        continue;
                    Integer code = jsonObject.getInteger("code");
                    Looper.prepare();
                    if(code == null){
                        xPosition = 0;
                        yPosition = 0;
                    }else if(code == 200){
                        userList = JSONObject.parseArray(jsonObject.getString("data"), User.class);
                        xPosition = userList.get(0).getX().intValue();
                        yPosition = userList.get(0).getY().intValue();
                        succeedRenewLocation = true;
                    }else{
                        Toast.makeText(AreaDetail.this, "wifi?????????????????????", Toast.LENGTH_SHORT).show();

                    }
                    Looper.loop();
                    break;
                }



            }
        }).start();



        return 0;
    }


    // ???list?????????string???string?????????(1,2,3)
    private String toStr(List<Integer> list){
        StringBuilder s = new StringBuilder("(");
        for(int i = 0; i < list.size(); i++){
            s.append(list.get(i).toString());
            if(i!=list.size()-1) s.append( ",");
        }
        s.append(")");

        return s.toString();
    }

    class ImageUtil {

        //gResId:??????id???gContext???????????????
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