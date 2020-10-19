package com.example.database;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ScanResult extends AppCompatActivity {

    TextView tv1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);

        tv1=findViewById(R.id.barcode);

        Intent intent = getIntent();
        String code=intent.getStringExtra(ScanActivity.CODE_EXTRA);
        tv1.setText(code);
    }
}