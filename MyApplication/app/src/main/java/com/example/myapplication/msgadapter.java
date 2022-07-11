package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.entity.Area;
import com.example.myapplication.fragments.HomeFragment;

import java.util.List;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class msgadapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<Area> mDatas;
    private Bitmap bm = null;
    private Bitmap bm2 = null;
    private static final int COMPLETED = 0;
    private ViewHolder viewHolder = null;




    public msgadapter(LayoutInflater context, List<Area> datas) {

        mInflater = context;
        mDatas = datas;
    }


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

        viewHolder = null;

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
        Glide.with(viewHolder.smallImg) .load("http://114.116.234.63:8080/image/"+msg.getImagePath()) .into(viewHolder.smallImg);
        Glide.with(viewHolder.mIvImg) .load("http://114.116.234.63:8080/image/"+msg.getPhotoPath()) .into(viewHolder.mIvImg);



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
