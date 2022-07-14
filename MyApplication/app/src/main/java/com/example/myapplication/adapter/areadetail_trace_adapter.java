package com.example.myapplication.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.AreaDetail;
import com.example.myapplication.R;
import com.example.myapplication.TraceDetail;
import com.example.myapplication.entity.Tag;
import com.example.myapplication.entity.Trace;

import java.util.List;

public class areadetail_trace_adapter extends RecyclerView.Adapter<areadetail_trace_adapter.ViewHolder>{

    private Context mContext;
    private List<Trace> traces;


    public areadetail_trace_adapter(List<Trace> traces) {
        this.traces = traces;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_areadetail_trace,parent,false);

        final areadetail_trace_adapter.ViewHolder holder = new areadetail_trace_adapter.ViewHolder(view);

        return holder;

        //        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

//
//        Trace trace = traces.get(position);
//        holder.traceName.setText(trace.getTraceName());

    }


    public void initTraceCardview(@NonNull areadetail_trace_adapter.ViewHolder holder, Trace  trace) {

        holder.traceName.setText(trace.getTraceName());
    }


    @Override
    public int getItemCount() {
        return traces.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public CardView cardView;

        TextView traceName;

        public ViewHolder(View view) {
            super(view);
           cardView =(CardView)view;

//            imageView = (ImageView) view.findViewById(R.id.areadetail_tag_imag);
            traceName = view.findViewById(R.id.areadt_trace_name);

        }
    }
}
