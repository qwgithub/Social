package com.sugan.qianwei.seeyouseeworld.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.activity.UserPageActivity;
import com.sugan.qianwei.seeyouseeworld.bean.CommentItem;

import java.util.List;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by QianWei on 2017/9/8.
 * 评论列表显示
 */

public class CommentsListAdapter extends BaseAdapter {

    private Context context;
    private List<CommentItem> commentList;

    public void setCommentList(List<CommentItem> commentList) {
        this.commentList = commentList;
    }

    public CommentsListAdapter(Context context, List<CommentItem> commentList) {
        this.context = context;
        this.commentList = commentList;
    }


    @Override
    public int getCount() {
        return (commentList == null) ? 0 : commentList.size();
    }

    @Override
    public Object getItem(int position) {
        return commentList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = View.inflate(context, R.layout.item_comments, null);
            holder.publisher_avatar = (CircleImageView) convertView.findViewById(R.id.comment_publisher_avatar);
            holder.colon_string = (TextView) convertView.findViewById(R.id.string_colon);
            holder.reply_string = (TextView) convertView.findViewById(R.id.string_reply);
            holder.publisher_name = (TextView) convertView.findViewById(R.id.comment_publisher_name);
            holder.responder_name = (TextView) convertView.findViewById(R.id.comment_responder_name);
            holder.comment_content = (TextView) convertView.findViewById(R.id.comment_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        CommentItem commentItem = commentList.get(position);
        int comment_id = commentItem.getComment_id();
        int dynamic_id = commentItem.getDynamic_id();
        final int publisher_id = commentItem.getPublisher_id();
        final String publisher_name = commentItem.getPublisher_name();
        final int responder_id = commentItem.getResponder_id();
        final String responder_name = commentItem.getResponder_name();
        String content = commentItem.getContent();
        final String publisher_avatar_url = commentItem.getPublisher_avatar();
        String comment_time = commentItem.getComment_time();

        if (responder_id == 0) {
            holder.reply_string.setVisibility(View.GONE);
            holder.responder_name.setVisibility(View.GONE);

        } else {
            holder.reply_string.setVisibility(View.VISIBLE);
            holder.responder_name.setVisibility(View.VISIBLE);

            holder.responder_name.setText(responder_name);
        }
        holder.publisher_name.setText(publisher_name);
        ImageLoader.getInstance().displayImage(publisher_avatar_url, holder.publisher_avatar, new DisplayImageOptions.Builder()
                .cacheOnDisk(true)
                .cacheInMemory(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(R.drawable.default_myavatar)
                .showImageOnFail(R.drawable.default_myavatar).build());
        holder.comment_content.setText(content);
        holder.publisher_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToUserPage(publisher_name, publisher_avatar_url, publisher_id);
            }
        });
        holder.publisher_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToUserPage(publisher_name, publisher_avatar_url, publisher_id);
            }
        });
        holder.responder_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToUserPage(responder_name, "", responder_id);
            }
        });
        return convertView;
    }

    static class ViewHolder {
        CircleImageView publisher_avatar;
        TextView publisher_name;
        TextView responder_name;
        TextView reply_string;
        TextView colon_string;
        TextView comment_content;
    }

    private void jumpToUserPage(String name, String avatar_url, int userid){
        //跳转到用户个人信息界面
        Intent intent = new Intent(context, UserPageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("name", name);
        bundle.putString("avatar", avatar_url);
        bundle.putInt("userid", userid);
        intent.putExtra("userinfo", bundle);
        context.startActivity(intent);
    }
}
