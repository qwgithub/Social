package com.sugan.qianwei.seeyouseeworld.fragment.search;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.adapter.search.UserListAdapter;
import com.sugan.qianwei.seeyouseeworld.bean.search.user.UsersData;
import com.sugan.qianwei.seeyouseeworld.views.XListView;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class GroupSearchResultFragment extends Fragment implements XListView.IXListViewListener{

    private static final String TAG = "UserSearch";
    private List<UsersData> groupsDataList;
    private XListView xListView;
    private UserListAdapter adapter;
    private int currentPage = 1;
    private String keyWord;

    public GroupSearchResultFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_search_result, container, false);
        findViews();
        return view;
    }

    private void findViews() {

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {

    }
}
