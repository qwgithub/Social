package com.sugan.qianwei.seeyouseeworld.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.bean.PersonalDataItem;
import com.sugan.qianwei.seeyouseeworld.bean.SettingsItem;

import java.util.ArrayList;

/**
 * Created by QianWei on 2017/8/22.
 * 设置列表
 */

public class SettingListAdapter extends BaseAdapter {

    private ArrayList<SettingsItem> list;
    private Context context;

    public SettingListAdapter(ArrayList<SettingsItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void setList(ArrayList<SettingsItem> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return (list == null) ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        if (list == null){
            return null;
        }
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        SettingListAdapter.ViewHolder holder = new SettingListAdapter.ViewHolder();
        if (convertView == null){
            view = View.inflate(context, R.layout.items_mine, null);
            holder.icon = (ImageView) view.findViewById(R.id.iv_mineitems_icon);
            holder.description = (TextView) view.findViewById(R.id.tv_mineitems_item);
            holder.explain = (TextView) view.findViewById(R.id.tv_mineitems_explain);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (SettingListAdapter.ViewHolder) view.getTag();
        }
        int imageId = list.get(position).getImageID();
        String des = list.get(position).getDescription();
        String explain = list.get(position).getExplain();
        if (imageId != 0){
            holder.icon.setBackgroundResource(imageId);
            holder.icon.setVisibility(View.VISIBLE);
        } else {
            holder.icon.setVisibility(View.GONE);
        }
        if (des != null){
            holder.description.setText(des);
        }
        if (explain != null){
            holder.explain.setText(explain);
        }
        return view;
    }

    static class ViewHolder{
        ImageView icon;
        TextView description;
        TextView explain;
    }
}
