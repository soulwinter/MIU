package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddTag extends AppCompatActivity {

    float pointX, pointY;
    int areaId;
    Intent get_intent;

    ImageView add_image;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tag);

        get_intent = getIntent();
        pointX = get_intent.getFloatExtra("pointX", pointX);
        pointX = get_intent.getFloatExtra("pointXY", pointY);
        areaId = 0;

//        // 添加图片
//        add_image = (ImageView) findViewById(R.id.add_image);
//
//        // TODO 添加图片
//        add_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//            }
//        });
    }
}