package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class AddTag extends AppCompatActivity {

    float pointX, pointY;
    int areaId;
    Intent get_intent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        get_intent = getIntent();
        pointX = get_intent.getFloatExtra("pointX", pointX);
        pointX = get_intent.getFloatExtra("pointXY", pointY);
        areaId = 0;


    }
}