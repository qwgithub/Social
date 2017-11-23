package com.sugan.qianwei.seeyouseeworld.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.OnItemClickListener;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.activity.DynamicDetailActivity;
import com.sugan.qianwei.seeyouseeworld.activity.LoginActivity;
import com.sugan.qianwei.seeyouseeworld.activity.UserPageActivity;
import com.sugan.qianwei.seeyouseeworld.application.MyApplication;
import com.sugan.qianwei.seeyouseeworld.bean.ForumContentItem;
import com.sugan.qianwei.seeyouseeworld.bean.GroupDetail;
import com.sugan.qianwei.seeyouseeworld.util.Constants;
import com.sugan.qianwei.seeyouseeworld.util.DialogFactoryUtil;
import com.sugan.qianwei.seeyouseeworld.util.SharedPreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by QianWei on 2017/9/19.
 */

public class GroupForumPageAdapter extends BaseAdapter {

    private final static String TAG = "GroupForumPageAdapter";

    private final int TYPE_ONE = 0;

    private final int TYPE_TWO = 1;

    private GroupDetail groupDetail;
    private ArrayList<ForumContentItem> list;
    private Activity context;

    private String login_userid;

    public GroupForumPageAdapter(Activity context) {
        this.context = context;
        login_userid = SharedPreferenceUtil.getFromCache(context.getApplicationContext(), "userinfo", "userid");
    }

    public void setList(ArrayList<ForumContentItem> list, GroupDetail groupDetail) {
        this.groupDetail = groupDetail;
        this.list = list;
    }

    public void setList(ArrayList<ForumContentItem> list) {
        this.list = list;
    }

    @Override
    public int getCount() {
        return (list == null) ? 1 : list.size() + 1;
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
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_ONE;
        } else {
            return TYPE_TWO;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        int itemViewType = getItemViewType(position);
        if (itemViewType == TYPE_ONE) {
            ViewHolderOne viewHolderOne;
            if (convertView == null) {
                viewHolderOne = new ViewHolderOne();
                convertView = View.inflate(context, R.layout.item_first_item_of_groupdetail, null);
                viewHolderOne.groupMemberNumber = (TextView) convertView.findViewById(R.id.tv_group_member_number);
                viewHolderOne.groupBack = (ImageView) convertView.findViewById(R.id.iv_group_background);
                viewHolderOne.groupName = (TextView) convertView.findViewById(R.id.tv_group_name);
                viewHolderOne.groupDes = (TextView) convertView.findViewById(R.id.tv_group_description);
                viewHolderOne.groupFollow = (Button) convertView.findViewById(R.id.bt_group_follow);
                convertView.setTag(viewHolderOne);
            } else {
                viewHolderOne = (ViewHolderOne) convertView.getTag();
            }
            if (groupDetail != null) {
                ImageLoader.getInstance().displayImage(groupDetail.getGroupCover(), viewHolderOne.groupBack);
                viewHolderOne.groupMemberNumber.setText("已经有 " + groupDetail.getGroupMemberNumber() + " 名成员");
                viewHolderOne.groupName.setText(groupDetail.getGroupName());
                viewHolderOne.groupDes.setText(groupDetail.getGroupDetail());
                if (groupDetail.getIfConcern() == 0) {
                    viewHolderOne.groupFollow.setText("我要加入");
                } else {
                    viewHolderOne.groupFollow.setText("退出");
                }
                viewHolderOne.groupFollow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snedFollowRequest(groupDetail.getGroupId(), login_userid, (Button) v);
                    }
                });
            }
        } else {
            final ViewHolderTwo viewHolderTwo;
            if (convertView == null) {
                viewHolderTwo = new ViewHolderTwo();
                convertView = View.inflate(context, R.layout.item_forum_from_group, null);
                viewHolderTwo.name = (TextView) convertView.findViewById(R.id.groupitem_publisher_name);
                viewHolderTwo.avatar = (CircleImageView) convertView.findViewById(R.id.group_avatar);
                viewHolderTwo.cover = (ImageView) convertView.findViewById(R.id.group_cover);
                viewHolderTwo.content = (TextView) convertView.findViewById(R.id.group_content);
                viewHolderTwo.num_praise = (TextView) convertView.findViewById(R.id.tv_group_praise);
                viewHolderTwo.icon_praise = (TextView) convertView.findViewById(R.id.iv_group_praise);
                viewHolderTwo.comment_num = (TextView) convertView.findViewById(R.id.tv_group_commentnumber);
                viewHolderTwo.delete_forum = (ImageView) convertView.findViewById(R.id.iv_delete_operate);
                convertView.setTag(viewHolderTwo);
            } else {
                viewHolderTwo = (ViewHolderTwo) convertView.getTag();
            }
            final int dynamic_id = list.get(position - 1).getId();
            final String cover_url = list.get(position - 1).getImgurl();
            final String avatar_url = list.get(position - 1).getAvatar();
            final String user_name = list.get(position - 1).getName();
            String content = list.get(position - 1).getIntroduction();
            final int praiseNum = list.get(position - 1).getPraisenumber();
            int praiseState = list.get(position - 1).getIspraised();
            final int userid = list.get(position - 1).getUserid();
            int commentNum = list.get(position - 1).getComments_num();
            final String groupName = list.get(position - 1).getGroup_name();
            final int groupId = list.get(position - 1).getGroup_id();

            viewHolderTwo.num_praise.setText(String.valueOf(praiseNum));
            viewHolderTwo.name.setText(user_name);
            viewHolderTwo.content.setText(content);
            viewHolderTwo.comment_num.setText(String.valueOf(commentNum));
            ImageLoader.getInstance().displayImage(cover_url, viewHolderTwo.cover);
            ImageLoader.getInstance().displayImage(avatar_url, viewHolderTwo.avatar,
                    new DisplayImageOptions.Builder()
                            .cacheOnDisk(true)
                            .cacheInMemory(true)
                            .bitmapConfig(Bitmap.Config.RGB_565)
                            .showImageOnLoading(R.drawable.default_myavatar)
                            .showImageOnFail(R.drawable.default_myavatar).build());
            if (praiseState == 1) {   // 已点赞
                viewHolderTwo.icon_praise.setBackgroundResource(R.drawable.praise_button_select);
            } else {
                viewHolderTwo.icon_praise.setBackgroundResource(R.drawable.praise_button_unselect);
            }

            if (login_userid.equals(String.valueOf(userid))) {
                viewHolderTwo.delete_forum.setVisibility(View.VISIBLE);
            } else {
                viewHolderTwo.delete_forum.setVisibility(View.GONE);
            }


            //点击头像
            viewHolderTwo.avatar.setOnClickListener(new View.OnClickListener() {
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
            viewHolderTwo.delete_forum.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogFactoryUtil.createAlterView(context, "确定删除动态吗？", null, "取消", new String[]{"确定"}, new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int p) {
                            if (p == 0) {
                                deleteForum(position - 1);
                            }
                        }
                    }).show();

                }
            });

            //点赞
            viewHolderTwo.icon_praise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    praiseRequest("addpraise", viewHolderTwo.num_praise, viewHolderTwo.icon_praise, dynamic_id, position - 1);
                }
            });

            //点击某条动态
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ForumContentItem forumContentItem = list.get(position - 1);
                    Intent intent = new Intent(context, DynamicDetailActivity.class);
                    intent.putExtra("dynamicdetail", forumContentItem);
                    context.startActivity(intent);
                }
            });
        }
        return convertView;
    }

    private void snedFollowRequest(int groupId, String login_userid, final Button groupFollow) {
        String followUrl = Constants.MAIN_URL + "joinGroup";
        RequestParams params = new RequestParams();
        params.put("user_id", login_userid);
        params.put("group_id", groupId);
        ((MyApplication) context.getApplication()).getClient().post(context.getApplicationContext(), followUrl, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (responseBody != null) {
                    Log.d(TAG, "onSuccess: "+new String(responseBody));
                    try {
                        JSONObject data = new JSONObject(new String(responseBody));
                        String status_code = data.getString("status_code");
                        if ("0".equals(status_code)) {
                            String status_msg = data.getString("status_msg");
                            Toast.makeText(context, status_msg, Toast.LENGTH_SHORT).show();
                            int isFollow = data.getInt("isfollow");
                            if (isFollow == 0){
                                groupFollow.setText("我要加入");
                            } else {
                                groupFollow.setText("退出");
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (responseBody != null) {

                }
            }
        });
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
        ((MyApplication) context.getApplication()).getClient().post(context.getApplicationContext(), praiseUrl,
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
        ((MyApplication) context.getApplication()).getClient().post(context.getApplicationContext(), deleteUrl, params,
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

    static class ViewHolderOne {
        ImageView groupBack;
        TextView groupMemberNumber;
        TextView groupName;
        TextView groupDes;
        Button groupFollow;
    }

    static class ViewHolderTwo {
        TextView name;
        CircleImageView avatar;
        TextView content;
        ImageView cover;
        TextView num_praise;
        TextView icon_praise;
        TextView comment_num;
        ImageView delete_forum;
    }
}
