package com.example.myapplication.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.myapplication.AddTag;
import com.example.myapplication.R;

public class ViewFragment extends Fragment {
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_view, container, false);

        float x = 3;
        float y = 5;

        // 添加标记的button
        // 临时button，测试add_tag界面用
        Button button_add_tag = getActivity().findViewById(R.id.button_add_tag);
        button_add_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), AddTag.class);
                intent.putExtra("pointX",x);
                intent.putExtra("pointY",y);
                startActivity(intent);
            }
        });

        return root;
    }
}
