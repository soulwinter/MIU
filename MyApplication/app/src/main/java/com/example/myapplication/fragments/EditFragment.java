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
import com.example.myapplication.entity.User;
import com.example.myapplication.tags_list;

public class EditFragment extends Fragment {

    private User user;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit, container, false);

        initMsg();



        // 添加标记的button
        // 临时button，测试tag详情界面用
        int areaId = 0;
        Button button_tag_list = root.findViewById(R.id.tags_list_test);

        button_tag_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getActivity(), tags_list.class);
                intent.putExtra("areaId", areaId);
                startActivity(intent);

            }
        });
        return root;

    }

    // 初始化
    private void initMsg(){
        this.user = (User) getActivity().getIntent().getSerializableExtra("user");

    }
}
