package com.sugan.qianwei.seeyouseeworld.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.sugan.qianwei.seeyouseeworld.R;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                Intent homeIntent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(homeIntent);
                finish();
            }
        }, 1000);
    }
}
