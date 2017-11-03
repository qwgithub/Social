package com.sugan.qianwei.seeyouseeworld.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.bean.GroupDetail;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by QianWei on 2017/9/29.
 */

public class SimpleGroupDetailListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<GroupDetail> list;

    public SimpleGroupDetailListAdapter(Context context, ArrayList<GroupDetail> list) {
        this.context = context;
        this.list = list;
    }

    public void setList(ArrayList<GroupDetail> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_selectresult_group, null);
            holder.group_name = (TextView) convertView.findViewById(R.id.tv_grouplistname);
            holder.group_cover = (CircleImageView) convertView.findViewById(R.id.civ_grouplistcover);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.group_name.setText(list.get(position).getGroupName());
        ImageLoader.getInstance().displayImage(list.get(position).getGroupCover(), holder.group_cover,
                new DisplayImageOptions.Builder()
                        .cacheOnDisk(true)
                        .cacheInMemory(true)
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .showImageOnLoading(R.drawable.default_myavatar)
                        .showImageOnFail(R.drawable.default_myavatar).build());
        return convertView;
    }

    static class ViewHolder {
        TextView group_name;
        CircleImageView group_cover;
    }

}
