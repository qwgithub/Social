package com.sugan.qianwei.seeyouseeworld.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.bean.search.user.UsersData;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by QianWei on 2017/11/3.
 */

public class UserListAdapter extends BaseAdapter implements View.OnClickListener {

    private Context context;
    private List<UsersData> usersDataList;

    public UserListAdapter(Context context, List<UsersData> usersDataList) {
        this.context = context;
        this.usersDataList = usersDataList;
    }

    public void setUsersDataList(List<UsersData> usersDataList) {
        this.usersDataList = usersDataList;
    }

    @Override
    public int getCount() {
        return usersDataList.size();
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
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder holder = new ViewHolder();
        if (convertView == null){
            convertView = View.inflate(context, R.layout.item_user, null);
            holder.userAvatar = convertView.findViewById(R.id.user_avatar);
            holder.userName = convertView.findViewById(R.id.user_name);
            holder.userIndroduce = convertView.findViewById(R.id.user_introduce);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        UsersData usersData = usersDataList.get(position);
        ImageLoader.getInstance().displayImage(usersData.getAvatar(), holder.userAvatar);
        holder.userName.setText(usersData.getName());
        String introduce = usersData.getProfession() + "·" + usersData.getHobby() + "·" + usersData.getSignature();
        holder.userIndroduce.setText(introduce);
        convertView.setOnClickListener(this);
        return convertView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_user:
                Log.d("qianwei", "onClick ll_user");
                break;
        }
    }

    static class ViewHolder{
        CircleImageView userAvatar;
        TextView userName;
        TextView userIndroduce;
    }
}
