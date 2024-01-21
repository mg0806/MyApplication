package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.myapplication.utils.FirebaseUtil;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (FirebaseUtil.isLoggedIn()){
                    startActivity(new Intent(Splash.this,MainActivity.class));
                }
                else{
                    startActivity(new Intent(Splash.this,Login.class));
                }
                finish();
            }
        },1000);
    }
}

