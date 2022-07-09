package com.example.myapplication.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.myapplication.R;
import com.example.myapplication.entity.User;

public class EditFragment extends Fragment {

    private User user;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_edit, container, false);

        initMsg();

        return root;
    }

    // 初始化
    private void initMsg(){
        this.user = (User) getActivity().getIntent().getSerializableExtra("user");

    }
}
