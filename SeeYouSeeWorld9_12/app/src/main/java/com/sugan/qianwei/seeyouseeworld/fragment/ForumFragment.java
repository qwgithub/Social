package com.sugan.qianwei.seeyouseeworld.fragment;


import android.app.ActivityOptions;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.net.http.HttpResponseCache;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.alertview.OnItemClickListener;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.activity.LoginActivity;
import com.sugan.qianwei.seeyouseeworld.activity.SearchActivity;
import com.sugan.qianwei.seeyouseeworld.activity.WritingsActivity;
import com.sugan.qianwei.seeyouseeworld.adapter.ForumBaseAdapter;
import com.sugan.qianwei.seeyouseeworld.application.MyApplication;
import com.sugan.qianwei.seeyouseeworld.bean.ForumContentItem;
import com.sugan.qianwei.seeyouseeworld.event.AutoRefreshEvent;
import com.sugan.qianwei.seeyouseeworld.event.ForumListChangeEvent;
import com.sugan.qianwei.seeyouseeworld.event.NetworkChangeEvent;
import com.sugan.qianwei.seeyouseeworld.util.DialogFactoryUtil;
import com.sugan.qianwei.seeyouseeworld.util.NetworkStateChecker;
import com.sugan.qianwei.seeyouseeworld.util.SharedPreferenceUtil;
import com.sugan.qianwei.seeyouseeworld.views.XListView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created By Qianwei
 */
public class ForumFragment extends Fragment implements XListView.IXListViewListener {

    private static final String TAG = "qianwei";

    private String firstPageRequestUrl = "http://www.agrising.cn:8080/blog/public/index.php/api/getDynamics?page=1";
    private String nextPageRequestUrl;
    private ForumBaseAdapter adapter;
    private TextView publishWritings;
    private XListView xListView;
    private RelativeLayout root_layout;
    private TextView btn_to_search_activity;
    private ArrayList<ForumContentItem> itemContents;
    private ContentLoadingProgressBar forum_loading_grouppage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        itemContents = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_forum, container, false);
        findViews(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new ForumBaseAdapter(getActivity());
        xListView.setAdapter(adapter);
        xListView.setOnScrollListener(adapter);
        initXListView(xListView);
        xListView.autoRefresh();

        publishWritings.setOnClickListener(publishWritingsClickListener);
        btn_to_search_activity.setOnClickListener(toSearchActivityClickListener);
//        forum_actionbar.setOnClickListener(actionbarDoubleClickListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!NetworkStateChecker.isNetworkAvailable(getActivity())) {
            showSnakeBar("当前网络不可用,请检查网络设置", 3000, true);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        HttpResponseCache cache=HttpResponseCache.getInstalled();
        if(cache!=null){
            cache.flush();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    //动态详情页面用户点赞评论后刷新首页动态列表
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onRefreshListView(ForumListChangeEvent event) {
        if (event.isDeleteForumItem()) {
            itemContents.remove(event.getPosition());
            adapter.setList(itemContents);
            adapter.notifyDataSetChanged();
        } else {
            itemContents.set(event.getPosition(), event.getForumContentItem());
            adapter.setList(itemContents);
            adapter.notifyDataSetChanged();
        }
    }

    //网络连接状态提示
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkChange(NetworkChangeEvent event) {
        if (event.isConnected()) {
//            showSnakeBar("已连接到网络", 1000, false);
            if (itemContents.isEmpty()) {
                xListView.autoRefresh();
            }
        } else {
            showSnakeBar("当前网络不可用,请检查网络设置", 3000, true);
        }
    }

    //成功发布动态后刷新首页动态列表
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAutoRefreshDynamicList(AutoRefreshEvent event){
        if (event.isReleaseSuccess()) {
            xListView.autoRefresh();
            if (!itemContents.isEmpty()) {
                xListView.setSelection(0);
            }
        }
    }

    private void getDynamicsRequest(final String getDynamicsUrl, String userid) throws JSONException {
        RequestParams params = new RequestParams();
        if (!"".equals(userid)) {
            params.put("userid", userid);
        }
        ((MyApplication) getActivity().getApplication()).getClient().post(getActivity(), getDynamicsUrl,
                null, params, null, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d(TAG, "onSuccess: getDynamicsRequest success");
                        parseResponse(getDynamicsUrl, new String(responseBody));
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d(TAG, "onFailure: getDynamicsRequest fail.");
                        if (firstPageRequestUrl.equals(getDynamicsUrl)) {
                            xListView.stopRefresh();
                        } else if (nextPageRequestUrl == null || nextPageRequestUrl.equals(getDynamicsUrl)) {
                            xListView.stopLoadMore();
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if (!itemContents.isEmpty()) {
                            adapter.setList(itemContents);
                            adapter.notifyDataSetChanged();
                        }
                        if (forum_loading_grouppage.isShown()) {
                            forum_loading_grouppage.setVisibility(View.GONE);
                        }
                    }
                });
    }

    private void findViews(View root) {
        btn_to_search_activity = (TextView) root.findViewById(R.id.btn_to_search_activity);
        xListView = (XListView) root.findViewById(R.id.xlistview);
        publishWritings = (TextView) root.findViewById(R.id.publishWritings);
        root_layout = (RelativeLayout) root.findViewById(R.id.main_content);
        forum_loading_grouppage = (ContentLoadingProgressBar) root.findViewById(R.id.forum_loading_grouppage);
    }

    private void initXListView(XListView lv) {
        lv.setPullLoadEnable(true);
        lv.setPullRefreshEnable(true);
        lv.setAutoLoadEnable(true);
        lv.setXListViewListener(this);
    }

    /**
     * 封装Snackbar显示，提示网络状态变化
     *
     * @param message 提示内容
     */
    private void showSnakeBar(String message, int duration, boolean haveAction) {
        Snackbar snackbar = Snackbar.make(root_layout, message, Snackbar.LENGTH_LONG);
        snackbar.setDuration(duration);
        if (haveAction) {  //是否跳转到网络设置
            snackbar.setActionTextColor(Color.WHITE);
            //设置点击动作
            snackbar.setAction("去设置", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "onClick: snakebar clicked.");
                    startWirelessSettings();
                }
            });
        }
        //获取Snackbar的view
        View view = snackbar.getView();
        //设置SnakeBar背景色
        view.setBackgroundColor(getResources().getColor(R.color.mediumseagreen));
        //设置提示字体
//        TextView textView = (TextView) view.findViewById(com.androidadvance.topsnackbar.R.id.snackbar_text);
        TextView textView = new TextView(getActivity());
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }

    private void startWirelessSettings() {
        // 跳转到系统的网络设置界面
        Intent intent;
        // 先判断当前系统版本
        if (android.os.Build.VERSION.SDK_INT > 10) {  // 3.0以上
            intent = new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS);
        } else {
            intent = new Intent();
            intent.setClassName("com.android.settings", "com.android.settings.WirelessSettings");
        }
        getActivity().startActivity(intent);
    }

    private void parseResponse(String getDynamicsUrl, String responseString) {
        try {
            if (firstPageRequestUrl.equals(getDynamicsUrl) && itemContents != null && !itemContents.isEmpty()) {
                itemContents.clear();
            }
            JSONObject response = new JSONObject(responseString);
            JSONArray perPageData = response.getJSONArray("data");
            int itemCount = perPageData.length();
            for (int i = 0; i < itemCount; i++) {
                JSONObject eachDynamicData = perPageData.getJSONObject(i);
                String publisher_name = eachDynamicData.getString("name");
                String publisher_avatar = eachDynamicData.getString("avatar");
                int dynamic_id = eachDynamicData.getInt("id");
                int userid = eachDynamicData.getInt("userid");
                String dynamic_imgurl = eachDynamicData.getString("imgurl");
                String dynamic_introduction = eachDynamicData.getString("introduction");
                int dynamic_praiseNum = eachDynamicData.getInt("praisenumber");
                int praiseState = eachDynamicData.getInt("ispraised");
                int comment_number = eachDynamicData.getInt("comments_num");
                String group_name = eachDynamicData.getString("group_name");
                int group_id = eachDynamicData.getInt("group_id");
                itemContents.add(new ForumContentItem(dynamic_id, userid, publisher_name, publisher_avatar,
                        dynamic_imgurl, dynamic_introduction, dynamic_praiseNum,
                        praiseState, comment_number, group_name, group_id));
            }
            if (firstPageRequestUrl.equals(getDynamicsUrl)) {
                xListView.stopRefresh();
            } else {
                xListView.stopLoadMore();
            }
            //更新下一页url
            nextPageRequestUrl = response.getString("next_page_url");

        } catch (JSONException e) {
            Log.d(TAG, "onResponse: Method getDynamicsRequest() response parse failed");
            e.printStackTrace();
        }
    }

    //点击发布动态按钮
    private View.OnClickListener publishWritingsClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String userId = SharedPreferenceUtil.getFromCache(getActivity(), "userinfo", "userid");
            if (!"".equals(userId)) {
                jumpToWritingsActivity();
            } else {
                DialogFactoryUtil.createAlterView(getActivity(), "需要登录后才可以发动态", null, "取消", new String[]{"立即登录"}, new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        if (position == 0) {
                            Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                            startActivity(loginIntent);
                        }
                    }
                }).show();
            }
        }
    };

    /**
     * 点击搜索框
     */
    private View.OnClickListener toSearchActivityClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            toSearchActivity();
        }
    };

    //双击标题栏回到列表顶部
//    private long lastClickTime;
//    private View.OnClickListener actionbarDoubleClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            long currentTime = System.currentTimeMillis();
//            if (currentTime - lastClickTime < 1500){
////                xListView.setSelection(3);
////                xListView.smoothScrollToPosition(0);
//            }
//            lastClickTime = currentTime;
//        }
//    };

    @Override
    public void onRefresh() {
        try {
            String userId = SharedPreferenceUtil.getFromCache(getActivity().getApplicationContext(),
                    "userinfo", "userid");
            getDynamicsRequest(firstPageRequestUrl, userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoadMore() {
        if (nextPageRequestUrl == null || "null".equals(nextPageRequestUrl)) {
            xListView.stopLoadMore();
            return;
        }
        try {
            String userId = SharedPreferenceUtil.getFromCache(getActivity().getApplicationContext(),
                    "userinfo", "userid");
            getDynamicsRequest(nextPageRequestUrl, userId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void jumpToWritingsActivity(){
        Intent intent = new Intent(getActivity(), WritingsActivity.class);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getActivity().startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity(), publishWritings, "publish_dynamic").toBundle());
//        } else {
//            getActivity().startActivity(intent);
//        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().startActivity(new Intent(getActivity(), WritingsActivity.class), ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
        } else {
            getActivity().startActivity(intent);
        }
    }

    private void toSearchActivity(){
        Intent intent = new Intent(getActivity(), SearchActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity(), btn_to_search_activity, "search").toBundle());
        } else {
            getActivity().startActivity(intent);
        }
    }
}
