package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.List;

public class testA extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        Button button_ap = (Button) findViewById(R.id.button_add_ap);
        Button button_area = (Button) findViewById(R.id.button_add_area);
        Button button_wifiRecord = (Button) findViewById(R.id.button_add_wifiRecord);

        button_ap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(testA.this,AddAp.class);
                startActivity(intent);
            }
        });

        button_wifiRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(testA.this,AddWifiRecord.class);
                startActivity(intent);
            }
        });

        button_area.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(testA.this,AddArea.class);
                startActivity(intent);
            }
        });

    }
}