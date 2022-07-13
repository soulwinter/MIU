package com.example.myapplication;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.example.myapplication.adapter.MessageAdapter;
import com.example.myapplication.entity.ChatHisMessageDTO;
import com.example.myapplication.entity.User;
import com.example.myapplication.webSocket.MyWebSocketClient;


import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ChatActivity extends AppCompatActivity {
    String TAG="XZXChat";
    private MyWebSocketClient myWebSocketClient;
    private List<ChatHisMessageDTO> mData = null;
    private Context mContext;
    private MessageAdapter messageAdapter = null;
    private ListView list_message;
    private String userId = "268022625@qq.com";
    private String areaId = "1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        areaId =  String.valueOf((Integer)intent.getSerializableExtra("areaId"));
        userId = (String) intent.getSerializableExtra("userId");
        String areaName = (String) intent.getSerializableExtra("areaName");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("聊天室:"+areaName);

        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_chat);



        mContext = this;
        list_message = (ListView)findViewById(R.id.message_list);
        Button button = (Button)findViewById(R.id.sendButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = (EditText) findViewById(R.id.input);
                String content = editText.getText().toString();
                String message = "{ "+"\"userId\":"+"\"" + userId +"\","
                                +"\"channel\":"+"\"" + areaId +"\","
                                +"\"content\":"+"\"" + content +"\","
                                +"\"type\":"+"\"" + "GROUP_CHAT" +"\"}";
                sendMsg(message);
                editText.setText("");
            }
        });
        /**
         * 加入频道
         * */
        new Thread(new Runnable() {
            @Override
            public void run() {
                //1、封装请求体数据
//        FormBody formBody = new FormBody.Builder().add("email",email).add("password",code).build();
                //2、获取到请求的对象
                Request request = new Request.Builder().url("http://114.116.234.63:8080/chat/channel/join?channel="+areaId).get().build();
                //3、获取到回调的对象
                OkHttpClient okHttpClient = new OkHttpClient();
                Call call = okHttpClient.newCall(request);
                //4、执行同步请求,获取到响应对象
                Response response = null;
                try {
                    response = call.execute();
                    //获取json字符串
                    String json = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        getData();

        /**
         * 连接webSocket
         * */
        initWebSocket();

    }

    @Override
    protected void onResume() {
        super.onResume();

        /**
        * 连接webSocket
        * */
        if (myWebSocketClient == null) {
            Log.e(TAG, "``````````````````````onResume");
            initWebSocket();

        } else if (!myWebSocketClient.isOpen()) {
            reconnectWs();//进入页面发现断开开启重连
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG, "``````````````````````````````onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "`````````````````````````onDestroy");
//        closeConnect();
    }

    /**
     * 初始化websocket
     */
    public void initWebSocket() {
        URI uri = URI.create("ws://114.116.234.63:8080/chatSocket/" + userId);
        //TODO 创建websocket
        myWebSocketClient = new MyWebSocketClient(uri);
        //TODO 设置超时时间
        myWebSocketClient.setConnectionLostTimeout(110 * 1000);
        //TODO 连接websocket
        new Thread() {
            @Override
            public void run() {
                try {
                    //connectBlocking多出一个等待操作，会先连接再发送，否则未连接发送会报错
                    myWebSocketClient.connectBlocking();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 发送消息
     *
     * @param msg
     */
    public void sendMsg(String msg) {
        if (null != myWebSocketClient) {
            Log.e("", "^_^Websocket发送的消息：-----------------------------------^_^" + msg);
            if (myWebSocketClient.isOpen()) {
                myWebSocketClient.send(msg);
            }

        }
    }

    /**
     * 开启重连
     */
    private void reconnectWs() {
        new Thread() {
            @Override
            public void run() {
                try {
                    Log.e("开启重连", "");
                    myWebSocketClient.reconnectBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 断开连接
     */
    private void closeConnect() {
        try {
            //关闭websocket
            if (null != myWebSocketClient) {
                myWebSocketClient.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            myWebSocketClient = null;
        }
    }

    private void getData(){
        /**
         * 请求消息
         * */
        new Thread(new Runnable() {
            @Override
            public void run() {
                //1、封装请求体数据
//        FormBody formBody = new FormBody.Builder().add("email",email).add("password",code).build();
                //2、获取到请求的对象
                while (true){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Request request = new Request.Builder().url("http://114.116.234.63:8080/chat/channel/chathis?channel=" + areaId).get().build();
                    //3、获取到回调的对象
                    OkHttpClient okHttpClient = new OkHttpClient();
                    Call call = okHttpClient.newCall(request);
                    //4、执行同步请求,获取到响应对象
                    Response response = null;
                    try {
                        response = call.execute();
                        //获取json字符串
                        String json = response.body().string();
                        JSONObject jsonObject = JSONObject.parseObject(json);
                        Integer code = jsonObject.getInteger("code");
                        if (code == 200){
                            List<ChatHisMessageDTO> resultList = JSONObject.parseArray(jsonObject.getString("data"), ChatHisMessageDTO.class);

                            if (resultList == null)
                                continue;
                            for (ChatHisMessageDTO chatHisMessageDTO : resultList) {
                                User user = chatHisMessageDTO.getUser();
                                try {
                                    String path = "http://114.116.234.63:8080/image" + user.getPhotoPath();
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
                                        user.setBitmap(bitmap);
                                    }
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (messageAdapter == null){
                                        messageAdapter = new MessageAdapter(resultList,mContext);
                                        messageAdapter.userId = userId;
                                        list_message.setAdapter(messageAdapter);
                                    }else{
                                        messageAdapter.setmData(resultList);
                                        messageAdapter.notifyDataSetChanged();
                                    }

                                }
                            });
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }
        }).start();
    }

}

