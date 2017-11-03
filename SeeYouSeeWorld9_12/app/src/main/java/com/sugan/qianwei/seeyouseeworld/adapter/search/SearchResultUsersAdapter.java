package com.sugan.qianwei.seeyouseeworld.adapter.search;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.bean.search.user.UsersData;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by QianWei on 2017/11/2.
 */

public class SearchResultUsersAdapter extends RecyclerView.Adapter<SearchResultUsersAdapter.ViewHolder>
        implements View.OnClickListener {

    private LayoutInflater mInflater;
    private List<UsersData> mDatas;
    private OnItemClickListener mOnItemClickListener;

    public SearchResultUsersAdapter(Context context, List<UsersData> datats) {
        mInflater = LayoutInflater.from(context);
        mDatas = datats;
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v, (int) v.getTag());
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View arg0) {
            super(arg0);
        }

        CircleImageView user_avatar;
        TextView user_name;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_searchresult_user,
                parent, false);
        //将创建的View注册点击事件
        view.setOnClickListener(this);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.user_avatar = (CircleImageView) view
                .findViewById(R.id.civ_userlistcover);
        viewHolder.user_name = (TextView) view.findViewById(R.id.tv_userlistname);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.user_name.setText(mDatas.get(position).getName());
        ImageLoader.getInstance().displayImage(mDatas.get(position).getAvatar(), holder.user_avatar);
        //将position保存在itemView的Tag中，以便点击时进行获取
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
