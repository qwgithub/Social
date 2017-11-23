package com.sugan.qianwei.seeyouseeworld.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.sugan.qianwei.seeyouseeworld.R;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Timer().schedule(new MyTimerTask(this) , 1000);
    }

    private static class MyTimerTask extends TimerTask {

        private WeakReference<Activity> activity;

        public MyTimerTask(Activity activity) {
            this.activity = new WeakReference<>(activity);
        }

        @Override
        public void run() {
            Activity outer = activity.get();
            if (outer != null){
                Intent homeIntent = new Intent(outer, HomeActivity.class);
                outer.startActivity(homeIntent);
                outer.finish();
            }
        }
    }
}
