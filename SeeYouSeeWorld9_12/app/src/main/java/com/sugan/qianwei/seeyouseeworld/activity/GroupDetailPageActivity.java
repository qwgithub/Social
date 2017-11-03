package com.sugan.qianwei.seeyouseeworld.activity;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.adapter.GroupForumPageAdapter;
import com.sugan.qianwei.seeyouseeworld.application.MyApp;
import com.sugan.qianwei.seeyouseeworld.bean.ForumContentItem;
import com.sugan.qianwei.seeyouseeworld.bean.GroupDetail;
import com.sugan.qianwei.seeyouseeworld.util.SharedPreferenceUtil;
import com.sugan.qianwei.seeyouseeworld.views.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class GroupDetailPageActivity extends Activity implements XListView.IXListViewListener {

    private static final String TAG = "GroupDetailPageActivity";
    private String firstPageRequestUrl = "http://www.agrising.cn:8080/blog/public/index.php/api/getGroupDetailById";

    private TextView tv_grouppage_back;
    private ContentLoadingProgressBar grouppage_loading_grouppage;
    private GroupDetail groupDetail;
    private ArrayList<ForumContentItem> itemContents = new ArrayList<>();
    private GroupForumPageAdapter groupItemAdapter;

    private XListView xListView;
    private String nextPageRequestUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_page);
        findViews();
        groupItemAdapter = new GroupForumPageAdapter(this);
        xListView.setAdapter(groupItemAdapter);
        initXListView(xListView);
        xListView.autoRefresh();
        tv_grouppage_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void findViews() {
        xListView = (XListView) findViewById(R.id.grouppage_listview);
        tv_grouppage_back = (TextView) findViewById(R.id.tv_grouppage_back);
        grouppage_loading_grouppage = (ContentLoadingProgressBar) findViewById(R.id.grouppage_loading_grouppage);
    }

    private void getGroupInfo(final String getGroupInfoUrl, String userid) {
        Intent intent = getIntent();
        String group_name = intent.getStringExtra("group_name");
        int group_id = intent.getIntExtra("group_id", 0);
        RequestParams params = new RequestParams();
        params.put("group_id", group_id);
        if (!"".equals(userid)) {
            params.put("user_id", userid);
        }
        ((MyApp) getApplication()).getClient().get(getApplicationContext(), getGroupInfoUrl, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "onSuccess: ");
                if (responseBody != null) {
                    parseResponse(getGroupInfoUrl, new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, "onFailure: ");
                if (firstPageRequestUrl.equals(getGroupInfoUrl)) {
                    xListView.stopRefresh();
                } else if (nextPageRequestUrl == null || nextPageRequestUrl.equals(getGroupInfoUrl)) {
                    xListView.stopLoadMore();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                Log.d(TAG, "onFinish: ");
                if (groupDetail != null) {
                    Log.d(TAG, "onFinish: ");
                    groupItemAdapter.setList(itemContents, groupDetail);
                } else {
                    groupItemAdapter.setList(itemContents);
                }
                groupItemAdapter.notifyDataSetChanged();
                if (grouppage_loading_grouppage.isShown()) {
                    grouppage_loading_grouppage.setVisibility(View.GONE);
                }
            }
        });

    }

    private void initXListView(XListView lv) {
        lv.setPullLoadEnable(true);
        lv.setPullRefreshEnable(true);
        lv.setAutoLoadEnable(true);
        lv.setXListViewListener(this);
    }

    private void parseResponse(String getDynamicsUrl, String responseString) {
        try {
            if (firstPageRequestUrl.equals(getDynamicsUrl) && itemContents != null && !itemContents.isEmpty()) {
                itemContents.clear();
            }
            JSONObject response = new JSONObject(responseString);
            Log.d(TAG, "parseResponse: " + responseString);
            if (firstPageRequestUrl.equals(getDynamicsUrl)) {
                JSONObject groupinfo = response.getJSONObject("groupinfo");
                int groupId = groupinfo.getInt("id");
                String groupName = groupinfo.getString("name");
                String intro = groupinfo.getString("intro");
                String cover = groupinfo.getString("cover");
                int isFollow = groupinfo.getInt("isfollow");
                int count = groupinfo.getInt("count");
                groupDetail = new GroupDetail(groupId, groupName, intro, cover, count, isFollow);
            }
            JSONObject group_dynamics = response.getJSONObject("group_dynamics");
            JSONArray perPageData = group_dynamics.getJSONArray("data");
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
            nextPageRequestUrl = group_dynamics.getString("next_page_url");

        } catch (JSONException e) {
            Log.d(TAG, "onResponse: Method getDynamicsRequest() response parse failed");
            e.printStackTrace();
        }
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "onItemClick: " + position);
        }
    };

    @Override
    public void onRefresh() {
        Log.d(TAG, "onRefresh: ");
        String userId = SharedPreferenceUtil.getFromCache(getApplicationContext(),
                "userinfo", "userid");
        getGroupInfo(firstPageRequestUrl, userId);
    }

    @Override
    public void onLoadMore() {
        if (nextPageRequestUrl == null || "null".equals(nextPageRequestUrl)) {
            xListView.stopLoadMore();
            return;
        }
        String userId = SharedPreferenceUtil.getFromCache(getApplicationContext(),
                "userinfo", "userid");
        getGroupInfo(nextPageRequestUrl, userId);
    }

}
