package com.example.myapplication.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;

import com.example.myapplication.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UserFragment extends Fragment {

    private String str;
    private MyHandler handler1;
    private View root;

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            ImageView imageView = (ImageView)root.findViewById(R.id.head_image);
            imageView.setImageBitmap((Bitmap)msg.obj);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_user, container, false);
        this.root = root;

        handler1 = new MyHandler();
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    String path = "http://114.116.234.63:8080/image/home/project/miu/images/user/1/sss.jpg";
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
                        Bitmap bm = BitmapFactory.decodeStream(is);
                        Message msg = new Message();
                        msg.obj = bm;
                        handler1.sendMessage(msg);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return root;
    }
}
