package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.entity.Tag;
import com.example.myapplication.entity.User;
import com.example.myapplication.multi.CommentMultiActivity;

import java.util.List;


public class UserTagsAdapter extends RecyclerView.Adapter<UserTagsAdapter.ViewHolder>{
    private Context mContext;
    private List<Tag> tags;
    private  User currentUser;

    public UserTagsAdapter(List<Tag> tags, User user) {
        this.tags = tags;
        this.currentUser = user;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_user_tags,parent,false);

        final UserTagsAdapter.ViewHolder holder = new UserTagsAdapter.ViewHolder(view);

        //为每一个子项的cardView设置点击事件
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            // 传一个Tag对象进tag详情页面
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Tag tag = tags.get(position);

//                //点击，跳转到tag详情页
//                Log.v("TEST_USER", tag.getTagName() + " " + currentUser.getId());
//                Intent intent = new Intent(mContext, CommentMultiActivity.class);
//
//                intent.putExtra("tagName",tag.getTagName());
//                intent.putExtra("tagDescribe",tag.getTagDescription());
//                intent.putExtra("tagPhotoPath",tag.getPicturePath());
//                intent.putExtra("userID",currentUser.getId());
//                intent.putExtra("areaID",tag.getAreaId());//在用户界面没有“当前区域”的概念
//                mContext.startActivity(intent);

            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Tag tag = tags.get(position);
        boolean flag = true;
        while (flag){
            try{
                Glide.with(holder.imageView).load("http://114.116.234.63:8080/image/"+tag.getPicturePath()) .into(holder.imageView);
                flag = false;
            }catch (Exception e){
                flag = true;
            }
        }
        holder.tvName.setText(tag.getTagName());
        holder.tvDesc.setText(tag.getTagDescription());
    }

    @Override
    public int getItemCount() {
        return tags.size();
    }


    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView imageView;
        TextView tvName, tvDesc;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            imageView = (ImageView) view.findViewById(R.id.iv_content);
            tvName = view.findViewById(R.id.tv_name);
            tvDesc = view.findViewById(R.id.tv_desc);
        }
    }
}
