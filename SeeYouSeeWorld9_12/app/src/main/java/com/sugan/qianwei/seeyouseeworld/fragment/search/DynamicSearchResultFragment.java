package com.sugan.qianwei.seeyouseeworld.fragment.search;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.views.XListView;

/**
 * A simple {@link Fragment} subclass.
 */
public class DynamicSearchResultFragment extends Fragment implements XListView.IXListViewListener{


    public DynamicSearchResultFragment() {
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
