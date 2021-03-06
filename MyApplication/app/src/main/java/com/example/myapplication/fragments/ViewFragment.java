package com.example.myapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.AddTag;
import com.example.myapplication.R;
import com.example.myapplication.multi.CommentMultiActivity;

public class ViewFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_view, container, false);

        // 标记地图的坐标点x y,测试数据
        float x = 3;
        float y = 5;
        int userId = 0;
        int areaId = 0;

        // 添加标记的button
        // 临时button，测试add_tag界面用
        Button button_view_comment = root.findViewById(R.id.button_view_comment);

        button_view_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), CommentMultiActivity.class);
                intent.putExtra("pointX",x);
                intent.putExtra("pointY",y);
                intent.putExtra("userId", userId);
                intent.putExtra("areaId", areaId);
                startActivity(intent);

            }
        });

        return root;
    }
}
