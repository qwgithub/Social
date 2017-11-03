package com.sugan.qianwei.seeyouseeworld.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Fragment;

import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.event.AutoRefreshEvent;
import com.sugan.qianwei.seeyouseeworld.fragment.AccountFragment;
import com.sugan.qianwei.seeyouseeworld.fragment.ForumFragment;
import com.sugan.qianwei.seeyouseeworld.receiver.NetWorkStateChangeReceiver;

import org.greenrobot.eventbus.EventBus;


public class HomeActivity extends Activity {

    private RadioGroup navigation_group;
    private ForumFragment forum;
    private AccountFragment settings;
    private Fragment from;   //当前fragment
    private Fragment to;    //下一个fragment
    private InputMethodManager manager;   //软键盘管理
    private NetWorkStateChangeReceiver receiver;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        findviews();
        initFragments();
        initInputMethodManage();
        navigation_group.setOnCheckedChangeListener(changeListener);
        navigation_group.check(R.id.forum);
        registerNetWorkStateChangeReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unRegisterNetWorkStateChangeReceiver();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return super.onTouchEvent(event);
    }

    //连续点击物理返回键退出
    long lastClickTime = 0;
    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastClickTime < 2000){
            finish();
        } else {
            Toast.makeText(this, "再按一次返回键退出应用", Toast.LENGTH_SHORT).show();
        }
        lastClickTime = currentTime;
    }

    private void findviews() {
        navigation_group = (RadioGroup) findViewById(R.id.navigation_group);
    }

    private void initFragments() {
        forum = new ForumFragment();
        settings = new AccountFragment();
    }

    private void initInputMethodManage() {
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    /**
     * 监听Fragment切换
     */
    private RadioGroup.OnCheckedChangeListener changeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {
            switch (checkedId) {
                case R.id.forum:
                    to = forum;
                    break;
                case R.id.settings:
                    to = settings;
                    break;
            }
            switchContent(to);
        }
    };

    //切换主界面Fragment
    public void switchContent(Fragment to) {
        FragmentTransaction transaction = getFragmentManager()
                .beginTransaction();
        if (null == from) {  //第一次进入app
            transaction.replace(R.id.content_layout, forum, "forum");
            transaction.commitAllowingStateLoss();
            from = forum;
        }
        if (from != to) {
            if (!to.isAdded()) { // 先判断是否被add过
                transaction.hide(from).add(R.id.content_layout, to).commitAllowingStateLoss(); // 隐藏当前的fragment，add下一个到Activity中
            } else {
                transaction.hide(from).show(to).commitAllowingStateLoss(); // 隐藏当前的fragment，显示下一个
            }
            from = to;
        }
    }

    //跳转到识别activity
    public void recognize(View view) {
        Intent intent = new Intent(HomeActivity.this, ImageRecognitionActivity.class);
        startActivity(new Intent(HomeActivity.this, ImageRecognitionActivity.class));
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this, view, "take").toBundle());
//        } else {
//            startActivity(intent);
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle());
//        } else {
//            startActivity(intent);
//        }

    }

    /**
     * 注册网络监听广播
     */
    private void registerNetWorkStateChangeReceiver(){
        //获取广播对象
        receiver = new NetWorkStateChangeReceiver();
        //创建意图过滤器
        IntentFilter filter=new IntentFilter();
        //添加动作，监听网络
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);
    }

    /**
     * 反注册网络监听广播
     */
    private void unRegisterNetWorkStateChangeReceiver(){
        if (receiver != null){
            unregisterReceiver(receiver);
            receiver = null;
        }
    }

}
