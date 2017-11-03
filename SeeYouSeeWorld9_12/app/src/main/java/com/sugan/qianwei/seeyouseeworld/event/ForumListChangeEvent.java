package com.sugan.qianwei.seeyouseeworld.event;

import com.sugan.qianwei.seeyouseeworld.bean.ForumContentItem;

/**
 * Created by QianWei on 2017/9/26.
 *
 * 动态详情页改变状态，首页也跟着改变
 */

public class ForumListChangeEvent {

    private ForumContentItem forumContentItem; //动态内容
    private int position;  //动态位置
    private boolean deleteForumItem;  //动态是否被删除

    public ForumListChangeEvent(ForumContentItem forumContentItem, int position, boolean deleteForumItem) {
        this.forumContentItem = forumContentItem;
        this.position = position;
        this.deleteForumItem = deleteForumItem;
    }

    public boolean isDeleteForumItem() {
        return deleteForumItem;
    }

    public void setDeleteForumItem(boolean deleteForumItem) {
        this.deleteForumItem = deleteForumItem;
    }

    public ForumContentItem getForumContentItem() {
        return forumContentItem;
    }

    public void setForumContentItem(ForumContentItem forumContentItem) {
        this.forumContentItem = forumContentItem;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
