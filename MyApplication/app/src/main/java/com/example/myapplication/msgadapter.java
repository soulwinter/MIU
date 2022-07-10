package com.example.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication.entity.Area;
import com.example.myapplication.fragments.HomeFragment;

import java.util.List;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class msgadapter extends BaseAdapter {

    private Context mContext;
    private LayoutInflater mInflater;
    private List<Area> mDatas;
    private String str;

    public msgadapter(LayoutInflater context, List<Area> datas) {
//        mContext = context;
//        mInflater = LayoutInflater.from(context);
        mInflater = context;
        mDatas = datas;
    }
//
//    public msgadapter(HomeFragment homeFragment, List<Area> mDatas) {
//    }

    @Override
    public int getCount() {
        return mDatas.size();
    }

    @Override
    public Area getItem(int position) {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_msg, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.mIvImg = convertView.findViewById(R.id.id_iv_img);
            viewHolder.mTvTitle = convertView.findViewById(R.id.id_tv_title);
            viewHolder.mTvContent = convertView.findViewById(R.id.id_tv_content);
            viewHolder.shortContent = convertView.findViewById(R.id.id_tv_content2);
            viewHolder.smallImg = convertView.findViewById(R.id.imageView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Area msg = mDatas.get(position);

        viewHolder.mTvTitle.setText(msg.getName());
        viewHolder.shortContent.setText(msg.getShortDescription());
        viewHolder.mTvContent.setText(msg.getLongDescription());



        //从服务器加载小图（实地照片）
        ViewHolder finalViewHolder = viewHolder;
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bm = null;
                try {
                    String path = "http://114.116.234.63:8080/image/"+msg.getImagePath();//小图
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
                    if(conn.getResponseCode() == 200)
                    {
                        //获取服务器响应头中的流
                        InputStream is = conn.getInputStream();
                        //读取流里的数据，构建成bitmap位图
                        bm = BitmapFactory.decodeStream(is);
                        finalViewHolder.smallImg.setImageBitmap(bm);

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();


        //从服务器加载小图（平面图）
        new Thread(new Runnable() {
            @Override
            public void run() {
                Bitmap bm2 = null;
                try {
                    String path = "http://114.116.234.63:8080/image/"+msg.getPhotoPath();//大图：平面图
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
                    if(conn.getResponseCode() == 200)
                    {
                        //获取服务器响应头中的流
                        InputStream is = conn.getInputStream();
                        //读取流里的数据，构建成bitmap位图
                        bm2 = BitmapFactory.decodeStream(is);
                        finalViewHolder.mIvImg.setImageBitmap(bm2);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        return convertView;
    }

    public static class ViewHolder {
        ImageView smallImg;
        TextView mTvTitle;
        TextView shortContent;
        ImageView mIvImg;
        TextView mTvContent;
    }

}
