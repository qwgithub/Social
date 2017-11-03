package com.sugan.qianwei.seeyouseeworld.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.adapter.MyFragmentPagerAdapter;
import com.sugan.qianwei.seeyouseeworld.fragment.AllGroupListFragment;
import com.sugan.qianwei.seeyouseeworld.fragment.FollowedGroupListFragment;
import com.sugan.qianwei.seeyouseeworld.fragment.RecommendGroupListFragment;

import java.util.ArrayList;
import java.util.List;

public class GroupFragmentsContainerPageActivity extends AppCompatActivity {
    private TabLayout groupsPageTabLayout;
    private ViewPager groupsPageViewPager;
    private String[] titles = {"推荐", "已关注", "所有"};
    private List<Fragment> list;
    private MyFragmentPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_fragments_container_page);
        findViews();
        initViews();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }

    private void initViews() {
        //页面，数据源
        list = new ArrayList<>();
        list.add(new RecommendGroupListFragment());
        list.add(new FollowedGroupListFragment());
        list.add(new AllGroupListFragment());
        //ViewPager的适配器
        adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), list, titles);
        groupsPageViewPager.setAdapter(adapter);
        //绑定
        groupsPageTabLayout.setupWithViewPager(groupsPageViewPager);
    }

    private void findViews() {
        groupsPageTabLayout = (TabLayout) findViewById(R.id.group_fragments_tablayout);
        groupsPageViewPager = (ViewPager) findViewById(R.id.group_fragments_viewpager);
    }

    public void group_fragments_back(View view) {
        finish();
    }
}
