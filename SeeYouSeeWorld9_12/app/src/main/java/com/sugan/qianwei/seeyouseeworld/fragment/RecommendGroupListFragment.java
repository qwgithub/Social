package com.sugan.qianwei.seeyouseeworld.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.adapter.GroupListAdapter;
import com.sugan.qianwei.seeyouseeworld.application.MyApplication;
import com.sugan.qianwei.seeyouseeworld.bean.GroupDetail;
import com.sugan.qianwei.seeyouseeworld.views.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class RecommendGroupListFragment extends Fragment implements XListView.IXListViewListener {

    private final static String TAG = "RecommendGroupFragment";
    private String firstPageRequestUrl = "http://www.agrising.cn:8080/blog/public/api/getGroups?page=0&recommend=1";

    private ArrayList<GroupDetail> list;
    private GroupListAdapter groupItemAdapter;

    private XListView xListView;
    private String nextPageRequestUrl;

    public RecommendGroupListFragment() {
        // Required empty public constructor
        list = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups_list, container, false);
        findViews(view);
        initXListView(xListView);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        groupItemAdapter = new GroupListAdapter(list, getActivity());
        xListView.setAdapter(groupItemAdapter);
        initXListView(xListView);
        if (list != null && list.isEmpty()) {
            xListView.autoRefresh();
        }
    }

    private void findViews(View root) {
        xListView = (XListView) root.findViewById(R.id.recommendgroup_listview);
    }

    private void initXListView(XListView lv) {
        lv.setPullLoadEnable(true);
        lv.setPullRefreshEnable(true);
        lv.setAutoLoadEnable(true);
        lv.setXListViewListener(this);
    }

    private void getGroupList(final String url) {
        ((MyApplication) getActivity().getApplication()).getClient().get(getActivity().getApplicationContext(), url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (responseBody != null) {
                    parseResponse(url, new String(responseBody));
                } else {
                    Toast.makeText(getActivity(), "返回数据为空", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getActivity(), "请求失败", Toast.LENGTH_SHORT).show();
                if (firstPageRequestUrl.equals(url)) {
                    xListView.stopRefresh();
                } else if (nextPageRequestUrl == null || nextPageRequestUrl.equals(url)) {
                    xListView.stopLoadMore();
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (list != null) {
                    groupItemAdapter.setList(list);
                    groupItemAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void parseResponse(String getDynamicsUrl, String responseString) {
        try {
            if (firstPageRequestUrl.equals(getDynamicsUrl) && list != null && !list.isEmpty()) {
                list.clear();
            }
            JSONObject response = new JSONObject(responseString);
            Log.d(TAG, "parseResponse: " + responseString);
            JSONArray dataArray = response.getJSONArray("data");
            int length = dataArray.length();
            for (int i = 0; i < length; i++) {
                JSONObject groupinfo = dataArray.getJSONObject(i);
                int groupId = groupinfo.getInt("id");
                String groupName = groupinfo.getString("name");
                String intro = groupinfo.getString("intro");
                String cover = groupinfo.getString("cover");
                int count = groupinfo.getInt("members_count");
                list.add(new GroupDetail(groupId, groupName, intro, cover, count));
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

    @Override
    public void onRefresh() {
        getGroupList(firstPageRequestUrl);
    }

    @Override
    public void onLoadMore() {
        if (nextPageRequestUrl == null || "null".equals(nextPageRequestUrl)) {
            xListView.stopLoadMore();
            return;
        }
        getGroupList(nextPageRequestUrl);
    }

    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "onItemClick: " + position);

        }
    };
}
