package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.entity.Tag;
import com.example.myapplication.multi.CommentMultiActivity;


import java.util.List;

public class tags_list_adapter extends RecyclerView.Adapter<tags_list_adapter.ViewHolder> {
    private Context mContext;
    private List<Tag> tagList;



    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView tagImage;
        TextView tagName;
        TextView tag_descr;
        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            tagImage = (ImageView) view.findViewById(R.id.tag_image);
            tagName = (TextView) view.findViewById(R.id.tag_name);
            tag_descr = (TextView) view.findViewById(R.id.tag_descr);
        }
    }

    public tags_list_adapter(List<Tag> tagList1) {
        tagList = tagList1;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_area_tags,parent, false);
        final ViewHolder holder = new ViewHolder(view);
        //为每一个子项的cardView设置点击事件
        holder.cardView.setOnClickListener(new View.OnClickListener() {
       // 传一个Tag对象进tag详情页面
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Tag tag = tagList.get(position);

                //点击，跳转到tag详情页

                Intent intent = new Intent(mContext, CommentMultiActivity.class);

                intent.putExtra("tagName",tag.getTagName());
                intent.putExtra("tagDescribe",tag.getTagDescription());
                intent.putExtra("tagPhotoPath",tag.getPicturePath());
                mContext.startActivity(intent);

            }
        });
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tag tag = tagList.get(position);
        holder.tagName.setText(tag.getTagName());
        holder.tag_descr.setText(tag.getTagDescription());
        boolean flag = true;
        while (flag){
            try{
                Glide.with(mContext).load("http://114.116.234.63:8080/image/"+tag.getPicturePath()).into(holder.tagImage);
                flag = false;
            }catch (Exception e){
                flag = true;
            }
        }

    }
    @Override
    public int getItemCount() {
        return tagList.size();
    }
}