package com.sugan.qianwei.seeyouseeworld.fragment.search;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.activity.SearchActivity;
import com.sugan.qianwei.seeyouseeworld.activity.SearchResultDisplayActivity;
import com.sugan.qianwei.seeyouseeworld.adapter.UserListAdapter;
import com.sugan.qianwei.seeyouseeworld.adapter.search.SearchResultUsersAdapter;
import com.sugan.qianwei.seeyouseeworld.application.MyApp;
import com.sugan.qianwei.seeyouseeworld.bean.search.SearchResult;
import com.sugan.qianwei.seeyouseeworld.bean.search.user.Users;
import com.sugan.qianwei.seeyouseeworld.bean.search.user.UsersData;
import com.sugan.qianwei.seeyouseeworld.util.Constants;
import com.sugan.qianwei.seeyouseeworld.util.DialogFactoryUtil;
import com.sugan.qianwei.seeyouseeworld.util.UserInfoManage;
import com.sugan.qianwei.seeyouseeworld.views.XListView;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */
public class UserSearchResultFragment extends Fragment implements XListView.IXListViewListener {


    private static final String TAG = "UserSearch";
    private List<UsersData> usersDataList;
    private XListView xListView;
    private UserListAdapter adapter;
    private int currentPage = 1;
    private String keyWord;

    public UserSearchResultFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_search_result, container, false);
        findViews(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
        initXListView(xListView);
        xListView.autoRefresh();
    }

    private void initData() {
        usersDataList = new ArrayList<>();
        adapter = new UserListAdapter(getActivity(), usersDataList);
        keyWord = getArguments().getString("name");
    }

    private void initXListView(XListView lv) {
        lv.setPullLoadEnable(true);
        lv.setPullRefreshEnable(true);
        lv.setAutoLoadEnable(true);
        lv.setXListViewListener(this);
        lv.setAdapter(adapter);
    }

    private void findViews(View view) {
        xListView = view.findViewById(R.id.search_userlist);
    }

    @Override
    public void onRefresh() {
        currentPage = 1;
        getSearchResult(keyWord);
    }

    @Override
    public void onLoadMore() {
        if (currentPage == 1) {
            xListView.stopLoadMore();
        }
        getSearchResult(keyWord);
    }

    /**
     * 获取搜索结果
     * no（没有类型） user group dynamic
     *
     * @param keyWord
     */
    private void getSearchResult(final String keyWord) {

        String url = Constants.MAIN_URL + "search";
        RequestParams params = new RequestParams();
        params.put("user_id", UserInfoManage.getUserId(getActivity()));
        params.put("type", "user");
        params.put("key_word", keyWord);
        params.put("page", currentPage);
        ((MyApp) (getActivity().getApplication())).getClient().get(getActivity(), url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (responseBody == null) {
                    return;
                }
                Log.d(TAG, "onSuccess: " + new String(responseBody));
                Gson gson = new Gson();
                Type type = new TypeToken<SearchResult>() {
                }.getType();
                SearchResult searchResult = gson.fromJson(new String(responseBody), type);
                String code = searchResult.getStatus_code();
                Users users = searchResult.getUsers_data();
                if ("0".equals(code)) {
                    if (currentPage == 1 && !usersDataList.isEmpty()){
                        usersDataList.clear();
                    }
                    usersDataList.addAll(users.getData());
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (responseBody == null) {
                    return;
                }
                Log.d(TAG, "onFailure: " + new String(responseBody));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (!usersDataList.isEmpty()) {
                    adapter.setUsersDataList(usersDataList);
                    adapter.notifyDataSetChanged();
                }
                if (currentPage == 1) {
                    xListView.stopRefresh();
                } else {
                    xListView.stopLoadMore();
                }
                currentPage++;
            }
        });
    }
}
