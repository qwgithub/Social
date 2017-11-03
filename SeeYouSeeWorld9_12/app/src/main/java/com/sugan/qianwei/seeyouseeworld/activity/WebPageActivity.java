package com.sugan.qianwei.seeyouseeworld.activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.LinearLayout;

//import com.just.library.AgentWeb;
//import com.just.library.ChromeClientCallbackManager;
import com.sugan.qianwei.seeyouseeworld.R;

public class WebPageActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about_us);

        String webUrl = getIntent().getStringExtra("webUrl");
        loadAboutUsPage(webUrl);
    }

    private void loadAboutUsPage(String url) {
//        AgentWeb.with(this)//传入Activity
//                .setAgentWebParent((LinearLayout) findViewById(R.id.activity_about_us), new LinearLayout.LayoutParams(-1, -1))//传入AgentWeb 的父控件 ，如果父控件为 RelativeLayout ， 那么第二参数需要传入 RelativeLayout.LayoutParams ,第一个参数和第二个参数应该对应。
//                .useDefaultIndicator()// 使用默认进度条
//                .defaultProgressBarColor() // 使用默认进度条颜色
//                .setReceivedTitleCallback(new ChromeClientCallbackManager.ReceivedTitleCallback() {
//                    @Override
//                    public void onReceivedTitle(WebView view, String title) {
//                        Log.d("qianwei", "onReceivedTitle: " + title);
//                    }
//                }) //设置 Web 页面的 title 回调
//                .createAgentWeb()//
//                .ready()
//                .go(url);
    }

    public void aboutus_back(View view) {
        finish();
    }
}
