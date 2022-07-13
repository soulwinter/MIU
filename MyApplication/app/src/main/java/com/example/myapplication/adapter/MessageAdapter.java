package com.example.myapplication.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.example.myapplication.R;
import com.example.myapplication.entity.ChatHisMessageDTO;
import com.example.myapplication.entity.User;

public class MessageAdapter extends BaseAdapter {

    private List<ChatHisMessageDTO> mData;
    private Context mContext;
    public String userId = "s";
    //定义两个类别标志
    private static final int TYPE_ME = 0;
    private static final int TYPE_OTHER = 1;

    public List<ChatHisMessageDTO> getmData() {
        return mData;
    }

    public void setmData(List<ChatHisMessageDTO> mData) {
        this.mData = mData;
    }

    public MessageAdapter(List<ChatHisMessageDTO> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    //多布局的核心，通过这个判断类别
    @Override
    public int getItemViewType(int position) {
        if (mData.get(position).getUser().getEmail().equals(userId)) {
            return TYPE_ME;
        } else{
            return TYPE_OTHER;
        }
    }

    //类别数目
    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        ViewHolder1 holder1 = null;
        ViewHolder2 holder2 = null;
        if(convertView == null){
            switch (type){
                case TYPE_OTHER:
                    holder1 = new ViewHolder1();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item, parent, false);
                    holder1.touxiang = (ImageView) convertView.findViewById(R.id.touxiang);
                    holder1.username = (TextView) convertView.findViewById(R.id.username);
                    holder1.content = (TextView) convertView.findViewById(R.id.message_content);
                    convertView.setTag(R.id.Tag_ME,holder1);
                    break;
                case TYPE_ME:
                    holder2 = new ViewHolder2();
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.message_item_other, parent, false);
                    holder2.touxiang = (ImageView) convertView.findViewById(R.id.touxiang);
                    holder2.content = (TextView) convertView.findViewById(R.id.message_content);
                    convertView.setTag(R.id.Tag_OTHER,holder2);
                    break;
            }
        }else{
            switch (type){
                case TYPE_OTHER:
                    holder1 = (ViewHolder1) convertView.getTag(R.id.Tag_ME);
                    break;
                case TYPE_ME:
                    holder2 = (ViewHolder2) convertView.getTag(R.id.Tag_OTHER);
                    break;
            }
        }

        ChatHisMessageDTO chatHisMessageDTO = mData.get(position);
        //设置下控件的值
        switch (type){
            case TYPE_OTHER:
                if(chatHisMessageDTO != null){
                    holder1.touxiang.setImageBitmap(mData.get(position).getUser().getBitmap());
                    holder1.username.setText(mData.get(position).getUser().getUsername());
                    holder1.content.setText(mData.get(position).getMessage().getContent());
                }
                break;
            case TYPE_ME:
                if(chatHisMessageDTO != null){
                    holder2.touxiang.setImageBitmap(mData.get(position).getUser().getBitmap());
                    holder2.content.setText(mData.get(position).getMessage().getContent());
                }
                break;
        }
        return convertView;
    }

    //两个不同的ViewHolder
    private static class ViewHolder1{
        ImageView touxiang;
        TextView username;
        TextView content;
    }

    private static class ViewHolder2{
        ImageView touxiang;
        TextView content;
    }


}
