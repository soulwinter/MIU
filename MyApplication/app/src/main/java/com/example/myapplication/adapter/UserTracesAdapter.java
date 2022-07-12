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
import com.example.myapplication.entity.Trace;

import java.util.List;


public class UserTracesAdapter extends RecyclerView.Adapter<UserTracesAdapter.ViewHolder>{

    private List<Trace> traces;

    public UserTracesAdapter(List<Trace> traces) {
        this.traces = traces;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_trace,null,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trace trace = traces.get(position);
        holder.tv_name.setText("轨迹名称："+trace.getTraceName());
        holder.tv_desc.setText("轨迹描述："+trace.getDescription());
        holder.tv_count.setText("轨迹点位" + trace.getPointList().size()+"个");
    }

    @Override
    public int getItemCount() {
        return traces.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_desc, tv_count;

        public ViewHolder(View view) {
            super(view);
            tv_name = view.findViewById(R.id.tv_name);
            tv_desc = view.findViewById(R.id.tv_desc);
            tv_count = view.findViewById(R.id.tv_count);
        }
    }
}
