package com.sugan.qianwei.seeyouseeworld.adapter.search;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.sugan.qianwei.seeyouseeworld.bean.search.group.GroupsData;

import java.util.List;

/**
 * Created by QianWei on 2017/11/6.
 */

public class GroupListAdapter extends BaseAdapter {

    private Context context;
    private List<GroupsData> groupsDataList;

    @Override
    public int getCount() {
        return groupsDataList.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        return null;
    }


}
