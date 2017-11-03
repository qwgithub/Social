package com.sugan.qianwei.seeyouseeworld.adapter.search;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.bean.search.group.GroupsData;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by QianWei on 2017/11/2.
 */

public class SearchResultGroupsAdapter extends RecyclerView.Adapter<SearchResultGroupsAdapter.ViewHolder>
implements View.OnClickListener{

    private LayoutInflater mInflater;
    private List<GroupsData> mDatas;
    private OnItemClickListener onItemClickListener;

    public SearchResultGroupsAdapter(Context context, List<GroupsData> datats) {
        mInflater = LayoutInflater.from(context);
        mDatas = datats;
    }

    @Override
    public void onClick(View v) {
        if (onItemClickListener != null) {
            //注意这里使用getTag方法获取position
            onItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View arg0) {
            super(arg0);
        }
        CircleImageView group_avatar;
        TextView group_name;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_searchresult_user,
                parent, false);
        view.setOnClickListener(this);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.group_avatar = (CircleImageView) view.findViewById(R.id.civ_userlistcover);
        viewHolder.group_name = (TextView) view.findViewById(R.id.tv_userlistname);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.group_name.setText(mDatas.get(position).getName());
        holder.itemView.setTag(position);
        ImageLoader.getInstance().displayImage(mDatas.get(position).getCover(), holder.group_avatar);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        onItemClickListener = listener;
    }
}
