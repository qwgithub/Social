package com.sugan.qianwei.seeyouseeworld.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.activity.GroupDetailPageActivity;
import com.sugan.qianwei.seeyouseeworld.bean.GroupDetail;

import java.util.ArrayList;

/**
 * Created by QianWei on 2017/9/21.
 *
 */

public class GroupListAdapter extends BaseAdapter {

    private ArrayList<GroupDetail> list;
    private Activity context;

    public GroupListAdapter(ArrayList<GroupDetail> list, Activity context) {
        this.list = list;
        this.context = context;
    }

    public void setList(ArrayList<GroupDetail> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return (list == null) ? 0 : list.size();
    }

    @Override
    public Object getItem(int position) {
        if (list == null) {
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
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_group_list, null);
            holder.groupName = (TextView) convertView.findViewById(R.id.tv_item_group_list_name);
            holder.groupMemberNumber = (TextView) convertView.findViewById(R.id.tv_item_group_list_membernumber);
            holder.groupCover = (ImageView) convertView.findViewById(R.id.iv_item_group_list_cover);
            holder.groupDes = (TextView) convertView.findViewById(R.id.tv_item_group_list_introduction);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            if (holder.groupCover.getDrawable() != null) {
                holder.groupCover.setImageBitmap(null);
            }
        }

        final GroupDetail detail = list.get(position);
        holder.groupName.setText(detail.getGroupName());
        holder.groupDes.setText(detail.getGroupDetail());
        holder.groupMemberNumber.setText("已加入"+detail.getGroupMemberNumber()+"人");
        ImageLoader.getInstance().displayImage(detail.getGroupCover(), holder.groupCover);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent groupPageIntent = new Intent(context, GroupDetailPageActivity.class);
                groupPageIntent.putExtra("group_name", detail.getGroupName());
                groupPageIntent.putExtra("group_id", detail.getGroupId());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    context.startActivity(groupPageIntent, ActivityOptions.makeSceneTransitionAnimation(context, holder.groupCover, "group_image").toBundle());
                } else {
                    context.startActivity(groupPageIntent);
                }
            }
        });

        return convertView;
    }

    static class ViewHolder{
        ImageView groupCover;
        TextView groupMemberNumber;
        TextView groupName;
        TextView groupDes;
    }
}
