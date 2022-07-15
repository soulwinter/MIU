package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.AreaDetail;
import com.example.myapplication.R;
import com.example.myapplication.TraceDetail;
import com.example.myapplication.entity.Area;
import com.example.myapplication.entity.Trace;
import com.example.myapplication.entity.User;

import java.util.List;


public class UserTracesAdapter extends RecyclerView.Adapter<UserTracesAdapter.ViewHolder>{

    private Context mcontext;
    private List<Trace> traces;
    private  User currentUser;
    private Area areaObj;
    //todo 获取根据trace对应areaid，获取area对象，并传入trace详情页面

    public UserTracesAdapter(List<Trace> traces, User user) {
        this.traces = traces;
        this.currentUser = user;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(mcontext == null){
            mcontext = parent.getContext();
        }

        View view = LayoutInflater.from(mcontext).inflate(R.layout.item_user_trace,parent,false);

//        在这里为每个carview设置点击事件

        final UserTracesAdapter.ViewHolder holder = new UserTracesAdapter.ViewHolder(view);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            // 传一个User、area、Trace对象进trace详情页面
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                Trace  trace = traces.get(position);
                // areaObj = trace.getAreaId();
//                Log.v("TEST_USER","hello");
//                //开启一个Activity并把trace传进去
//                Intent intent1 = new Intent();
//                intent1.putExtra("trace", trace);
//
//                intent1.putExtra("area", areaObj);
//
//                intent1.putExtra("user", currentUser);
//                intent1.setClass(mcontext, TraceDetail.class);
//                mcontext.startActivity(intent1);


            }
        });
        return holder;

//        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Trace trace = traces.get(position);
        holder.tv_name.setText(trace.getTraceName());
        holder.tv_desc.setText(trace.getDescription());
//        holder.tv_count.setText( trace.getPointList().size()+"个");
    }

    @Override
    public int getItemCount() {
        return traces.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView tv_name, tv_desc, tv_count;

        public ViewHolder(View view) {
            super(view);
            cardView =(CardView) view;
            tv_name = view.findViewById(R.id.tv_name);
            tv_desc = view.findViewById(R.id.tv_desc);
//            tv_count = view.findViewById(R.id.tv_count);
        }
    }
}
