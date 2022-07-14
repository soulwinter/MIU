package com.example.myapplication.adapter;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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
import com.example.myapplication.AreaDetail;
import com.example.myapplication.R;
import com.example.myapplication.entity.Tag;
import com.example.myapplication.tags_list;

import java.util.List;


public class areadetail_tags_adapter extends RecyclerView.Adapter<areadetail_tags_adapter.ViewHolder>{

    private Context mContext;
    private List<Tag> tags;

    public areadetail_tags_adapter(List<Tag> tags) {
        this.tags = tags;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (mContext == null) {
            mContext = parent.getContext();
        }

        View view = LayoutInflater.from(mContext).inflate(R.layout.item_areadetail_tag,parent,false);


        final areadetail_tags_adapter.ViewHolder holder = new areadetail_tags_adapter.ViewHolder(view);

//        //为每一个子项的cardView设置点击事件
//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//       // 传一个Tag对象进tag详情页面
//            @Override
//            public void onClick(View v) {
//                int position = holder.getAdapterPosition();
//                Tag tag1 = tags.get(position);
////                Toast.makeText(v.getContext(), "you clicked tag: " + tag1.getTagName(), Toast.LENGTH_SHORT).show();
//
//
//////                界面跳转方法
////                Intent intent = new Intent(mContext, tags_list.class);
////                intent.putExtra("areaId", tag1.getAreaId());
////                mContext.startActivity(intent);
//
//            }
//        });
        return holder;



//        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

////
////        if(!isDestroy((Activity)mContext)) {
//            Tag tag = tags.get(position);
//            Glide.with(holder.imageView).load("http://114.116.234.63:8080/image/" + tag.getPicturePath()).into(holder.imageView);
//            holder.tvName.setText(tag.getTagName());
////            holder.imageView.setImageBitmap(tag.getBitmap());

//        }

    }


    public void initCardview(@NonNull ViewHolder holder, Tag tag) {


//
//        if(!isDestroy((Activity)mContext)) {
//        Tag tag = tags.get(position);
//        Glide.with(holder.imageView).load("http://114.116.234.63:8080/image/" + tag.getPicturePath()).into(holder.imageView);
            holder.tvName.setText(tag.getTagName());
            holder.imageView.setImageBitmap(tag.getBitmap());
//        holder.itemView.setTag(position);
//        }

    }


    @Override
    public int getItemCount() {
        return tags.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CardView cardView;
        ImageView imageView;
        TextView tvName;

        public ViewHolder(View view) {
            super(view);
            cardView = (CardView)view;
            imageView = (ImageView) view.findViewById(R.id.areadetail_tag_imag);
            tvName = view.findViewById(R.id.areadt_tag_name);

        }



    }

    public static boolean isDestroy(Activity mActivity) {
        if (mActivity== null || mActivity.isFinishing() || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && mActivity.isDestroyed())) {
            return true;
        } else {
            return false;
        }
    }


}
