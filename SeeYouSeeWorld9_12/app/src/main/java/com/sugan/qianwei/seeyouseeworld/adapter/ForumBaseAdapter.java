package com.sugan.qianwei.seeyouseeworld.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
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
import com.sugan.qianwei.seeyouseeworld.activity.DynamicDetailActivity;
import com.sugan.qianwei.seeyouseeworld.activity.GroupDetailPageActivity;
import com.sugan.qianwei.seeyouseeworld.activity.LoginActivity;
import com.sugan.qianwei.seeyouseeworld.activity.UserPageActivity;
import com.sugan.qianwei.seeyouseeworld.application.MyApp;
import com.sugan.qianwei.seeyouseeworld.bean.ForumContentItem;
import com.sugan.qianwei.seeyouseeworld.util.Constants;
import com.sugan.qianwei.seeyouseeworld.util.DialogFactoryUtil;
import com.sugan.qianwei.seeyouseeworld.util.SharedPreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by QianWei on 2017/8/23.
 * 动态列表显示
 */

public class ForumBaseAdapter extends BaseAdapter implements AbsListView.OnScrollListener {
    private final static String TAG = "qianwei";
    private Activity context;
    private ArrayList<ForumContentItem> list;
    private Animation animation;
    private SparseBooleanArray animationStarted;
    private String login_userid;

    public ForumBaseAdapter(Activity context) {
        this.context = context;
        animation = AnimationUtils.loadAnimation(context, R.anim.forum_gridview_anim);
        animationStarted = new SparseBooleanArray();
        login_userid = SharedPreferenceUtil.getFromCache(context.getApplicationContext(), "userinfo", "userid");
    }

    public void setList(ArrayList<ForumContentItem> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        if (list != null) {
            return list.size();
        }
        return 0;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_forum, null);
            holder.name = (TextView) convertView.findViewById(R.id.forum_name);
            holder.avatar = (CircleImageView) convertView.findViewById(R.id.forum_avatar);
            holder.cover = (ImageView) convertView.findViewById(R.id.forum_cover);
            holder.content = (TextView) convertView.findViewById(R.id.forum_content);
            holder.num_praise = (TextView) convertView.findViewById(R.id.tv_forum_praise);
            holder.icon_praise = (TextView) convertView.findViewById(R.id.iv_forum_praise);
            holder.comment_num = (TextView) convertView.findViewById(R.id.tv_forum_commentnumber);
            holder.group_name = (TextView) convertView.findViewById(R.id.forum_group_name);
            holder.delete_forum = (ImageView) convertView.findViewById(R.id.iv_delete_operate);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            if (holder.cover.getDrawable() != null) {
                holder.cover.setImageBitmap(null);
            }
        }
//        // 如果是第一次加载该view，则使用动画
        if (!animationStarted.get(position)) {
            convertView.startAnimation(animation);
            animationStarted.put(position, true);
        }

        final int dynamic_id = list.get(position).getId();
        final String cover_url = list.get(position).getImgurl();
        final String avatar_url = list.get(position).getAvatar();
        final String user_name = list.get(position).getName();
        String content = list.get(position).getIntroduction();
        final int praiseNum = list.get(position).getPraisenumber();
        int praiseState = list.get(position).getIspraised();
        final int userid = list.get(position).getUserid();
        int commentNum = list.get(position).getComments_num();
        final String groupName = list.get(position).getGroup_name();
        final int groupId = list.get(position).getGroup_id();

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

        //跳转到兴趣小组界面
        holder.group_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent groupPageIntent = new Intent(context, GroupDetailPageActivity.class);
                groupPageIntent.putExtra("group_name", groupName);
                groupPageIntent.putExtra("group_id", groupId);
                context.startActivity(groupPageIntent);
            }
        });

        //点击头像
        holder.avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到用户个人信息界面
                Intent intent = new Intent(context, UserPageActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("name", user_name);
                bundle.putString("avatar", avatar_url);
                bundle.putInt("userid", userid);
                intent.putExtra("userinfo", bundle);
                context.startActivity(intent);
            }
        });

        //点击删除按钮
        holder.delete_forum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFactoryUtil.createAlterView(context, "确定删除动态吗？", null, "取消", new String[]{"确定"}, new OnItemClickListener() {
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
                Intent intent = new Intent(context, DisplayBigImageActivity.class);
                intent.putExtra("imageurl", cover_url);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(context, holder.cover, "big_image").toBundle());
                } else {
                    context.startActivity(intent);
                }
            }
        });

        //点击某条动态
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForumContentItem forumContentItem = list.get(position);
                Intent intent = new Intent(context, DynamicDetailActivity.class);
                intent.putExtra("dynamicdetail", forumContentItem);
                intent.putExtra("selectedforumposition", position);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    context.startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(context, holder.cover, "big_image").toBundle());
                } else {
                    context.startActivity(intent);
                }
            }
        });

        return convertView;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if (scrollState == SCROLL_STATE_IDLE) {  //停止滑动

        } else {

        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

    }

    static class ViewHolder {
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

    private void praiseRequest(String addPraise, final TextView tv_praise, final TextView iv_praise, int dynamicId, final int position) {
        final String praiseUrl = Constants.MAIN_URL + addPraise;
        String userId = SharedPreferenceUtil.getFromCache(context, "userinfo", "userid");
        RequestParams params = new RequestParams();
        if (!"".equals(userId)) {
            params.put("userid", userId);
        } else {
            DialogFactoryUtil.createAlterView(context, "需要登录后才可以点赞", null, "取消", new String[]{"立即登录"}, new OnItemClickListener() {
                @Override
                public void onItemClick(Object o, int position) {
                    if (position == 0) {
                        Intent loginIntent = new Intent(context, LoginActivity.class);
                        context.startActivity(loginIntent);
                    }
                }
            }).show();
            return;
        }
        params.put("dynamicid", dynamicId);
        Log.d(TAG, "praiseRequest: " + praiseUrl);
        ((MyApp) context.getApplication()).getClient().post(context.getApplicationContext(), praiseUrl,
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
                                    list.get(position).setPraisenumber(praiseCount);
                                    list.get(position).setIspraised(1);
                                    iv_praise.setBackgroundResource(R.drawable.praise_button_select);
                                } else {
                                    tv_praise.setText(String.valueOf(praiseCount));
                                    list.get(position).setPraisenumber(praiseCount);
                                    list.get(position).setIspraised(0);
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
        params.put("id", list.get(position).getId());
        ((MyApp) context.getApplication()).getClient().post(context.getApplicationContext(), deleteUrl, params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (responseBody != null) {
                            Log.d(TAG, "onSuccess: " + new String(responseBody));
                            try {
                                JSONObject json = new JSONObject(new String(responseBody));
                                String status_code = json.getString("status_code");
                                if ("0".equals(status_code)) {
                                    Toast.makeText(context, "删除成功", Toast.LENGTH_SHORT).show();
                                    list.remove(position);
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
}
