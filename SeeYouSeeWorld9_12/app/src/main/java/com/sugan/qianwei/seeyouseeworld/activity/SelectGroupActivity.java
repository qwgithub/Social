package com.sugan.qianwei.seeyouseeworld.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.adapter.SimpleGroupDetailListAdapter;
import com.sugan.qianwei.seeyouseeworld.application.MyApp;
import com.sugan.qianwei.seeyouseeworld.bean.GroupDetail;
import com.sugan.qianwei.seeyouseeworld.util.Constants;
import com.sugan.qianwei.seeyouseeworld.util.SharedPreferenceUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class SelectGroupActivity extends Activity {

    private ArrayList<GroupDetail> groupDetailList;
    private ListView groupListView;
    private SimpleGroupDetailListAdapter adapter;
    private SwipeRefreshLayout refreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_group);
        findViews();
        refreshLayout.setRefreshing(true);
        initData();
        getMyGroupList();
    }

    private void initData() {
        groupDetailList = new ArrayList<>();
        adapter = new SimpleGroupDetailListAdapter(this, groupDetailList);
        groupListView.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getMyGroupList();
            }
        });
        refreshLayout.setColorSchemeResources(R.color.mediumseagreen, R.color.mediumturquoise, R.color.pink);
        groupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GroupDetail groupDetail = groupDetailList.get(position);
                Intent intent = new Intent();
                intent.putExtra("groupinfo", groupDetail);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    private void findViews() {
        groupListView = (ListView) findViewById(R.id.my_group_list);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.selectgroup_refreshlayout);
    }

    /**
     * 获取分组列表
     */
    private void getMyGroupList(){
        String getGroupListUrl = Constants.MAIN_URL + "getGropsByUserId";
        String userid = SharedPreferenceUtil.getFromCache(getApplicationContext(), "userinfo", "userid");
        if (userid.equals("")){
            Toast.makeText(this, "您的账号未登录", Toast.LENGTH_SHORT).show();
            return;
        }
        RequestParams params = new RequestParams();
        params.put("user_id", userid);
        params.put("simple", 1);
        ((MyApp)getApplication()).getClient().get(getApplicationContext(), getGroupListUrl, params, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (responseBody != null){
                    Log.d("qianwei", "onSuccess: "+new String(responseBody));
                    try {
                        if (!groupDetailList.isEmpty()){
                            groupDetailList.clear();
                        }
                        JSONObject jsonObject = new JSONObject(new String(responseBody));
                        String status_code = jsonObject.getString("status_code");
                        if ("0".equals(status_code)){
                            JSONArray data = jsonObject.getJSONArray("data");
                            int length = data.length();
                            for (int i=0; i<length; i++){
                                JSONObject group_info = data.getJSONObject(i);
                                String group_name = group_info.getString("name");
                                int group_id = group_info.getInt("id");
                                String group_cover = group_info.getString("cover");
                                groupDetailList.add(new GroupDetail(group_id, group_name, group_cover));
                            }
                        } else {
                            Toast.makeText(SelectGroupActivity.this, ""+jsonObject.getString("status_msg"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(SelectGroupActivity.this, "分组信息拉取失败", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (!groupDetailList.isEmpty()){
                    adapter.setList(groupDetailList);
                    adapter.notifyDataSetChanged();
                }
                refreshLayout.setRefreshing(false);
            }
        });
    }

    public void selectgroup_back(View view) {
        finish();
    }
}
