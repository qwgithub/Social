package com.sugan.qianwei.seeyouseeworld.receiver;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.sugan.qianwei.seeyouseeworld.activity.DynamicDetailActivity;
import com.sugan.qianwei.seeyouseeworld.bean.ForumContentItem;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

public class JPushReceiver extends BroadcastReceiver {
    private static final String TAG = "JPushReceiver";
    private static final String RECEIVE_COMMENTS_TYPE = "receive_comments";
    private static final String TYPE_ANOTHER = "type_another";

    private NotificationManager nm;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null == nm) {
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        Bundle bundle = intent.getExtras();

        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            Log.d(TAG, "JPush用户注册成功");

        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接受到推送下来的自定义消息");

        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "接受到推送下来的通知");
            receivingNotification(context, bundle);
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "用户点击打开了通知");
            openNotification(context, bundle);
        } else {
            Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
    }

    private void receivingNotification(Context context, Bundle bundle) {
        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        Log.d(TAG, " title : " + title);
        String message = bundle.getString(JPushInterface.EXTRA_ALERT);
        Log.d(TAG, "message : " + message);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        Log.d(TAG, "extras : " + extras);
    }

    private void openNotification(Context context, Bundle bundle) {
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        String type;
        JSONObject data;
        try {
            JSONObject extrasJson = new JSONObject(extras);
            type = extrasJson.getString("type");
            data = extrasJson.getJSONObject("data");
            switch (type){
                case RECEIVE_COMMENTS_TYPE:
                    toDynamicDetailActivity(context, data);
                    break;

            }
        } catch (Exception e) {
            Log.w(TAG, "Unexpected: extras is not a valid json", e);
        }
    }

    private void toDynamicDetailActivity(Context context, JSONObject data) {
        ForumContentItem forumContentItem = getForumContentItem(data);
        if (forumContentItem != null) {
            Intent intent = new Intent(context, DynamicDetailActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("dynamicdetail", forumContentItem);
            context.startActivity(intent);
        } else {
            Log.d(TAG, "openNotification: forumContentItem=null");
        }
    }

    private ForumContentItem getForumContentItem(JSONObject eachDynamicData) {
        ForumContentItem forumContentItem = null;
        try {
            String publisher_name = eachDynamicData.getString("name");
            String publisher_avatar = eachDynamicData.getString("avatar");
            int dynamic_id = eachDynamicData.getInt("id");
            int userid = eachDynamicData.getInt("userid");
            String dynamic_imgurl = eachDynamicData.getString("imgurl");
            String dynamic_introduction = eachDynamicData.getString("introduction");
            int dynamic_praiseNum = eachDynamicData.getInt("praisenumber");
            int praiseState = eachDynamicData.getInt("ispraised");
            int comment_number = eachDynamicData.getInt("comments_num");
            String group_name = eachDynamicData.getString("group_name");
            int group_id = eachDynamicData.getInt("group_id");

            forumContentItem = new ForumContentItem(dynamic_id, userid, publisher_name, publisher_avatar,
                    dynamic_imgurl, dynamic_introduction, dynamic_praiseNum,
                    praiseState, comment_number, group_name, group_id);
        } catch (JSONException e) {
            Log.d(TAG, "getForumContentItem: 解析失败");
            e.printStackTrace();
        }
        return forumContentItem;
    }


}