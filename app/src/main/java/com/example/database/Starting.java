package com.example.database;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class Starting extends AppCompatActivity {

    private ImageView loading;
    private static int TimeOut=2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting);
        loading = findViewById(R.id.start);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(Starting.this,LoginActivity.class));
                finish();
            }
        },TimeOut);

        Animation myanimation = AnimationUtils.loadAnimation(this,R.anim.startinganimation);

        loading.startAnimation(myanimation);
    }
}