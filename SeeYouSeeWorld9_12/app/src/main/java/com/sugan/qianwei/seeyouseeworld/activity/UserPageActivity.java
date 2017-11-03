package com.sugan.qianwei.seeyouseeworld.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.fragment.MyEssayFragment;
import com.sugan.qianwei.seeyouseeworld.adapter.MyFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class UserPageActivity extends AppCompatActivity {
    private TabLayout userPageTabLayout;
    private ViewPager userPageViewPager;

    private List<Fragment> list;
    private MyFragmentPagerAdapter adapter;
    private String[] titles = {"个人动态"};
    private TextView userpage_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_page);
        findViews();
        initViews();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void findViews() {
        userPageTabLayout = (TabLayout) findViewById(R.id.userpage_tablayout);
        userPageViewPager = (ViewPager) findViewById(R.id.userpage_viewpager);
        userpage_title = (TextView) findViewById(R.id.userpage_title);
    }

    private void initViews(){
        Bundle bundle = getIntent().getBundleExtra("userinfo");
        String name = bundle.getString("name");
        userpage_title.setText(name);
        MyEssayFragment essayFragment = new MyEssayFragment();
        essayFragment.setArguments(bundle);
        //页面，数据源
        list = new ArrayList<>();
        list.add(essayFragment);
//        list.add(new PersonalDataFragment());
        //ViewPager的适配器
        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), list, titles);
        userPageViewPager.setAdapter(adapter);
        //绑定
        userPageTabLayout.setupWithViewPager(userPageViewPager);
    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void userpage_back(View view) {
        finish();
    }
}
