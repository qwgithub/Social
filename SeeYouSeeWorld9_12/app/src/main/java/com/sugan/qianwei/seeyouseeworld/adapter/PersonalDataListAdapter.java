package com.sugan.qianwei.seeyouseeworld.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.bean.PersonalDataItem;
import com.sugan.qianwei.seeyouseeworld.bean.SettingsItem;

import java.util.ArrayList;

/**
 * Created by QianWei on 2017/9/6.
 * 个人资料列表
 */

public class PersonalDataListAdapter extends BaseAdapter {
    private ArrayList<PersonalDataItem> list;
    private Context context;

    public PersonalDataListAdapter(ArrayList<PersonalDataItem> list, Context context) {
        this.list = list;
        this.context = context;
    }

    public void setList(ArrayList<PersonalDataItem> list) {
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
        ViewHolder holder = new ViewHolder();
        if (convertView == null){
            view = View.inflate(context, R.layout.item_personaldata, null);
            holder.description = (TextView) view.findViewById(R.id.tv_personal_description);
            holder.title = (TextView) view.findViewById(R.id.tv_personal_title);
            holder.icon = (ImageView) view.findViewById(R.id.iv_personal_navigateicon);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
        }
        String des = list.get(position).getDescription();
        String title = list.get(position).getTitle();
        boolean iconVisible = list.get(position).isIconVisible();
        if (des != null){
            holder.description.setText(des);
        }
        if (title != null){
            holder.title.setText(title);
        }
        if (iconVisible) {
            holder.icon.setVisibility(View.VISIBLE);
        } else {
            holder.icon.setVisibility(View.GONE);
        }
        return view;
    }

    static class ViewHolder{
        TextView description;
        TextView title;
        ImageView icon;
    }
}
