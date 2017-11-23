package com.sugan.qianwei.seeyouseeworld.adapter.search;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.OnItemClickListener;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.activity.DisplayBigImageActivity;
import com.sugan.qianwei.seeyouseeworld.activity.GroupDetailPageActivity;
import com.sugan.qianwei.seeyouseeworld.activity.LoginActivity;
import com.sugan.qianwei.seeyouseeworld.activity.UserPageActivity;
import com.sugan.qianwei.seeyouseeworld.application.MyApplication;
import com.sugan.qianwei.seeyouseeworld.bean.ForumContentItem;
import com.sugan.qianwei.seeyouseeworld.util.Constants;
import com.sugan.qianwei.seeyouseeworld.util.DialogFactoryUtil;
import com.sugan.qianwei.seeyouseeworld.util.SharedPreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by QianWei on 2017/11/2.
 */

public class SearchResultDynamicsAdapter extends RecyclerView.Adapter<SearchResultDynamicsAdapter.ViewHolder>
implements View.OnClickListener{

    private static final String TAG = "SearchActivity";
    private final String login_userid;
    private LayoutInflater mInflater;
    private List<ForumContentItem> mDatas;
    private Activity mContext;
    private OnItemClickListener onItemClickListener;

    public SearchResultDynamicsAdapter(Activity context, List<ForumContentItem> datats) {
        mInflater = LayoutInflater.from(context);
        mDatas = datats;
        mContext = context;
        login_userid = SharedPreferenceUtil.getFromCache(context.getApplicationContext(), "userinfo", "userid");
    }

    @Override
    public void onClick(View view) {
        if (onItemClickListener != null) {
            onItemClickListener.onItemClick(view, (int) view.getTag());
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View arg0) {
            super(arg0);
        }
        TextView name;
        CircleImageView avatar;
        TextView content;
        ImageView cover;
        TextView num_praise;
        TextView icon_praise;
        TextView comment_num;
        TextView group_name;
        ImageView delete_forum;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_forum,
                parent, false);
        view.setOnClickListener(this);
        ViewHolder holder = new ViewHolder(view);
        holder.name = (TextView) view.findViewById(R.id.forum_name);
        holder.avatar = (CircleImageView) view.findViewById(R.id.forum_avatar);
        holder.cover = (ImageView) view.findViewById(R.id.forum_cover);
        holder.content = (TextView) view.findViewById(R.id.forum_content);
        holder.num_praise = (TextView) view.findViewById(R.id.tv_forum_praise);
        holder.icon_praise = (TextView) view.findViewById(R.id.iv_forum_praise);
        holder.comment_num = (TextView) view.findViewById(R.id.tv_forum_commentnumber);
        holder.group_name = (TextView) view.findViewById(R.id.forum_group_name);
        holder.delete_forum = (ImageView) view.findViewById(R.id.iv_delete_operate);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final int dynamic_id = mDatas.get(position).getId();
        final String cover_url = mDatas.get(position).getImgurl();
        final String avatar_url = mDatas.get(position).getAvatar();
        final String user_name = mDatas.get(position).getName();
        String content = mDatas.get(position).getIntroduction();
        final int praiseNum = mDatas.get(position).getPraisenumber();
        int praiseState = mDatas.get(position).getIspraised();
        final int userid = mDatas.get(position).getUserid();
        int commentNum = mDatas.get(position).getComments_num();
        final String groupName = mDatas.get(position).getGroup_name();
        final int groupId = mDatas.get(position).getGroup_id();

        holder.num_praise.setText(String.valueOf(praiseNum));
        holder.name.setText(user_name);
        holder.content.setText(content);
        holder.comment_num.setText(String.valueOf(commentNum));
        holder.group_name.setText(groupName);
        ImageLoader.getInstance().displayImage(cover_url, holder.cover);
        ImageLoader.getInstance().displayImage(avatar_url, holder.avatar,
                new DisplayImageOptions.Builder()
                        .cacheOnDisk(true)
                        .cacheInMemory(true)
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .showImageOnLoading(R.drawable.default_myavatar)
                        .showImageOnFail(R.drawable.default_myavatar).build());
        if (praiseState == 1) {   // 已点赞
            holder.icon_praise.setBackgroundResource(R.drawable.praise_button_select);
        } else {
            holder.icon_praise.setBackgroundResource(R.drawable.praise_button_unselect);
        }

        if (login_userid.equals(String.valueOf(userid))) {
            holder.delete_forum.setVisibility(View.VISIBLE);
        } else {
            holder.delete_forum.setVisibility(View.GONE);
        }

        holder.group_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent groupPageIntent = new Intent(mContext, GroupDetailPageActivity.class);
                groupPageIntent.putExtra("group_name", groupName);
                groupPageIntent.putExtra("group_id", groupId);
                mContext.startActivity(groupPageIntent);
            }
        });

        //点击头像
        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到用户个人信息界面
                Intent intent = new Intent(mContext, UserPageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", user_name);
                bundle.putString("avatar", avatar_url);
                bundle.putInt("userid", userid);
                intent.putExtra("userinfo", bundle);
                mContext.startActivity(intent);
            }
        });

        //点击删除按钮
        holder.delete_forum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFactoryUtil.createAlterView(mContext, "确定删除动态吗？", null, "取消", new String[]{"确定"}, new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int p) {
                        if (p == 0) {
                            deleteForum(position);
                        }
                    }
                }).show();

            }
        });

        //点赞
        holder.icon_praise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                praiseRequest("addpraise", holder.num_praise, holder.icon_praise, dynamic_id, position);
            }
        });

        holder.cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DisplayBigImageActivity.class);
                intent.putExtra("imageurl", cover_url);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    mContext.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(mContext, holder.cover, "big_image").toBundle());
                } else {
                    mContext.startActivity(intent);
                }
            }
        });
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    private void praiseRequest(String addPraise, final TextView tv_praise, final TextView iv_praise, int dynamicId, final int position) {
        final String praiseUrl = Constants.MAIN_URL + addPraise;
        String userId = SharedPreferenceUtil.getFromCache(mContext, "userinfo", "userid");
        RequestParams params = new RequestParams();
        if (!"".equals(userId)) {
            params.put("userid", userId);
        } else {
            DialogFactoryUtil.createAlterView(mContext, "需要登录后才可以点赞", null, "取消", new String[]{"立即登录"}, new OnItemClickListener() {
                @Override
                public void onItemClick(Object o, int position) {
                    if (position == 0) {
                        Intent loginIntent = new Intent(mContext, LoginActivity.class);
                        mContext.startActivity(loginIntent);
                    }
                }
            }).show();
            return;
        }
        params.put("dynamicid", dynamicId);
        Log.d(TAG, "praiseRequest: " + praiseUrl);
        ((MyApplication) mContext.getApplication()).getClient().post(mContext.getApplicationContext(), praiseUrl,
                params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            JSONObject json = new JSONObject(new String(responseBody));
                            String code = json.getString("status_code");
                            int praiseCount = json.getInt("praiseNumber");
                            int praiseState = json.getInt("ispraised");
                            if ("0".equals(code)) {
                                if (praiseState == 1) {
                                    tv_praise.setText(String.valueOf(praiseCount));
                                    mDatas.get(position).setPraisenumber(praiseCount);
                                    mDatas.get(position).setIspraised(1);
                                    iv_praise.setBackgroundResource(R.drawable.praise_button_select);
                                } else {
                                    tv_praise.setText(String.valueOf(praiseCount));
                                    mDatas.get(position).setPraisenumber(praiseCount);
                                    mDatas.get(position).setIspraised(0);
                                    iv_praise.setBackgroundResource(R.drawable.praise_button_unselect);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                    }

                    @Override
                    public void onStart() {
                        super.onStart();

                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();

                    }
                }
        );
    }

    private void deleteForum(final int position) {
        String deleteUrl = Constants.MAIN_URL + "del_dynamic";
        RequestParams params = new RequestParams();
        params.put("id", mDatas.get(position).getId());
        ((MyApplication) mContext.getApplication()).getClient().post(mContext.getApplicationContext(), deleteUrl, params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (responseBody != null) {
                            Log.d(TAG, "onSuccess: " + new String(responseBody));
                            try {
                                JSONObject json = new JSONObject(new String(responseBody));
                                String status_code = json.getString("status_code");
                                if ("0".equals(status_code)) {
                                    Toast.makeText(mContext, "删除成功", Toast.LENGTH_SHORT).show();
                                    mDatas.remove(position);
                                    notifyDataSetChanged();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d(TAG, "onFailure: " + new String(responseBody));
                    }
                });
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
    }
}
