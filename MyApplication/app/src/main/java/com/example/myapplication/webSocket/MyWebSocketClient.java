package com.example.myapplication.webSocket;

import android.util.Log;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import java.lang.Exception;
import java.net.URI;

public class MyWebSocketClient extends WebSocketClient{

    String TAG = "MyWebSocketClient";

    public MyWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.i(TAG, "onOpen handshakedata=$handshakedata");
    }

    @Override
    public void onMessage(String message) {
        if (!message.isEmpty()) {
            Log.e("接收到的数据：", message);
        }
        Log.i(TAG, "onMessage message=$message");
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.i(TAG, "onClose code=$code reason=$reason remote=$remote");
    }

    @Override
    public void onError(Exception ex) {
        Log.i(TAG, "onError ex=$ex");
    }
}

