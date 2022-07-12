package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.entity.Tag;


import java.util.List;

public class tags_list_adapter extends RecyclerView.Adapter<tags_list_adapter.ViewHolder> {
    private Context mContext;
    private List<Tag> mFruitList;



    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        ImageView fruitImage;
        TextView fruitName;
        TextView tag_descr;
        public ViewHolder(View view) {
            super(view);
            cardView = (CardView) view;
            fruitImage = (ImageView) view.findViewById(R.id.fruit_image);
            fruitName = (TextView) view.findViewById(R.id.fruit_name);
            tag_descr = (TextView) view.findViewById(R.id.tag_descr);
        }
    }

    public tags_list_adapter(List<Tag> fruitList) {
        mFruitList = fruitList;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.tags_item,parent, false);
        final ViewHolder holder = new ViewHolder(view);
//        //为每一个子项的cardView设置点击事件
//        holder.cardView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int position = holder.getAdapterPosition();
//                Tag fruit = mFruitList.get(position);
//                Toast.makeText(v.getContext(), "you clicked image " + fruit.getTagName(), Toast.LENGTH_SHORT).show();
//            }
//        });
        return holder;
    }
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Tag fruit = mFruitList.get(position);
        holder.fruitName.setText(fruit.getTagName());
        holder.tag_descr.setText(fruit.getTagDescription());
        Glide.with(mContext).load("http://114.116.234.63:8080/image/"+fruit.getPicturePath()).into(holder.fruitImage);

    }
    @Override
    public int getItemCount() {
        return mFruitList.size();
    }
}