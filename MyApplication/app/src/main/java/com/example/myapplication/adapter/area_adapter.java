package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.entity.Area;

import java.util.List;
import android.graphics.Bitmap;


public class area_adapter extends BaseAdapter {

    private LayoutInflater mInflater;
    private List<Area> arealist;
    private Bitmap bm = null;
    private Bitmap bm2 = null;
    private static final int COMPLETED = 0;
    private ViewHolder viewHolder = null;




    public area_adapter(LayoutInflater Inflater, List<Area> datas) {

        mInflater = Inflater;
        arealist = datas;
    }


    


    @Override
    public int getCount() {
        return arealist.size();
    }

    @Override
    public Area getItem(int position) {
        return arealist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        viewHolder = null;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_area, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.bigImg = convertView.findViewById(R.id.id_area_bigImage);
            viewHolder.areaName = convertView.findViewById(R.id.id_area_name);
            viewHolder.longContent = convertView.findViewById(R.id.id_area_longText);
            viewHolder.shortContent = convertView.findViewById(R.id.id_area_shortText);
            viewHolder.smallImg = convertView.findViewById(R.id.id_area_smallImage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Area msg = arealist.get(position);

        viewHolder.areaName.setText(msg.getName());
        viewHolder.shortContent.setText(msg.getShortDescription());
        viewHolder.longContent.setText(msg.getLongDescription());
        boolean flag = true;
        while (flag){
            try{
                Glide.with(viewHolder.smallImg) .load("http://114.116.234.63:8080/image/"+msg.getImagePath()) .into(viewHolder.smallImg);
                Glide.with(viewHolder.bigImg) .load("http://114.116.234.63:8080/image/"+msg.getPhotoPath()) .into(viewHolder.bigImg);
                flag = false;
            }catch (Exception e){
               flag = true;
            }
        }
        return convertView;
    }

    public static class ViewHolder {
        ImageView smallImg;
        TextView areaName;
        TextView shortContent;
        ImageView bigImg;
        TextView longContent;
    }

}
