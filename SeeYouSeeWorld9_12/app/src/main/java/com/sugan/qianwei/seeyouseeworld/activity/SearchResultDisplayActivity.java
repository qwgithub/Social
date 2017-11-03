package com.sugan.qianwei.seeyouseeworld.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.adapter.MyFragmentPagerAdapter;
import com.sugan.qianwei.seeyouseeworld.fragment.search.AllSearchResultFragment;
import com.sugan.qianwei.seeyouseeworld.fragment.search.DynamicSearchResultFragment;
import com.sugan.qianwei.seeyouseeworld.fragment.search.GroupSearchResultFragment;
import com.sugan.qianwei.seeyouseeworld.fragment.search.SwitchFragmentEvent;
import com.sugan.qianwei.seeyouseeworld.fragment.search.UserSearchResultFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

public class SearchResultDisplayActivity extends AppCompatActivity {
    private static final String TAG = "SearchActivity";
    private TabLayout searchResultPageTabLayout;
    private ViewPager searchResultPageViewPager;

    private final String[] titles = {"综合", "用户", "小组", "动态"};
    private TextView searchResultPageTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result_display);
        EventBus.getDefault().register(this);
        findViews();
        initViews();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void findViews() {
        searchResultPageTabLayout = (TabLayout) findViewById(R.id.searchpage_tablayout);
        searchResultPageViewPager = (ViewPager) findViewById(R.id.searchpage_viewpager);
        searchResultPageTitle = (TextView) findViewById(R.id.searchpage_title);
    }

    private void initViews() {
        Bundle bundle = getIntent().getBundleExtra("search_result");
        String name = getIntent().getStringExtra("name");
        bundle.putString("name", name);
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

        UserSearchResultFragment userSearchResultFragment = new UserSearchResultFragment();
        userSearchResultFragment.setArguments(bundle);
        list.add(userSearchResultFragment);

        GroupSearchResultFragment groupSearchResultFragment = new GroupSearchResultFragment();
        groupSearchResultFragment.setArguments(bundle);
        list.add(groupSearchResultFragment);

        DynamicSearchResultFragment dynamicSearchResultFragment = new DynamicSearchResultFragment();
        dynamicSearchResultFragment.setArguments(bundle);
        list.add(dynamicSearchResultFragment);

        //ViewPager的适配器
        MyFragmentPagerAdapter adapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), list, titles);
        searchResultPageViewPager.setAdapter(adapter);
        //绑定
        searchResultPageTabLayout.setupWithViewPager(searchResultPageViewPager);
    }


    public void searchpage_back(View view) {
        finish();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSwitchFragment(SwitchFragmentEvent event){
        switch (event.getFragmentId()){
            case SwitchFragmentEvent.USER_FRAGMENT:
                searchResultPageViewPager.setCurrentItem(1);
                break;
            case SwitchFragmentEvent.GROUP_FRAGMENT:
                searchResultPageViewPager.setCurrentItem(2);
                break;
            case SwitchFragmentEvent.DYNAMIC_FRAGMENT:
                searchResultPageViewPager.setCurrentItem(3);
                break;
        }
    }
}
