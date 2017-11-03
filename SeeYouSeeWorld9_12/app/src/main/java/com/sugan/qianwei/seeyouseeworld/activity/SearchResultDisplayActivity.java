package com.sugan.qianwei.seeyouseeworld.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.adapter.MyFragmentPagerAdapter;
import com.sugan.qianwei.seeyouseeworld.fragment.AllSearchResultFragment;
import com.sugan.qianwei.seeyouseeworld.fragment.MyEssayFragment;

import java.util.ArrayList;
import java.util.List;

public class SearchResultDisplayActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private TabLayout searchResultPageTabLayout;
    private ViewPager searchResultPageViewPager;

    private final String[] titles = {"所有结果"};
    private TextView searchResultPageTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_display);
        findViews();
        initViews();
    }

    private void findViews() {
        searchResultPageTabLayout = (TabLayout) findViewById(R.id.searchpage_tablayout);
        searchResultPageViewPager = (ViewPager) findViewById(R.id.searchpage_viewpager);
        searchResultPageTitle = (TextView) findViewById(R.id.searchpage_title);
    }

    private void initViews() {
        Bundle bundle = getIntent().getBundleExtra("search_result");
        String name = getIntent().getStringExtra("name");
        searchResultPageTitle.setText("查找:" + name);
        initFragmentPager(bundle);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void initFragmentPager(Bundle bundle) {
        //被加载页面列表
        List<Fragment> list = new ArrayList<>();
        AllSearchResultFragment allSearchResultFragment = new AllSearchResultFragment();
        allSearchResultFragment.setArguments(bundle);
        list.add(allSearchResultFragment);

        //ViewPager的适配器
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), list, titles);
        searchResultPageViewPager.setAdapter(adapter);
        //绑定
        searchResultPageTabLayout.setupWithViewPager(searchResultPageViewPager);
    }


    public void searchpage_back(View view) {
        finish();
    }
}
