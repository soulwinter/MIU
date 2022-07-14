package com.example.myapplication.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.example.myapplication.entity.Tag;

import java.util.List;


public class UserTagsAdapter extends RecyclerView.Adapter<UserTagsAdapter.ViewHolder>{

    private List<Tag> tags;

    public UserTagsAdapter(List<Tag> tags) {
        this.tags = tags;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tags,null,false);
        return new ViewHolder(view);
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
        ImageView imageView;
        TextView tvName, tvDesc;

        public ViewHolder(View view) {
            super(view);
            imageView = (ImageView) view.findViewById(R.id.iv_content);
            tvName = view.findViewById(R.id.tv_name);
            tvDesc = view.findViewById(R.id.tv_desc);
        }
    }
}
