package com.sugan.qianwei.seeyouseeworld.bean;

import java.io.Serializable;

/**
 * Created by QianWei on 2017/8/23.
 */

public class ForumContentItem implements Serializable{

    private int id;
    private String name;
    private String avatar;
    private String imgurl;
    private String introduction;
    private int praisenumber;
    private int ispraised;
    private int userid;
    private int comments_num;
    private String group_name;
    private int group_id;

    public ForumContentItem(int dynamicId, int userid, String name, String avatar,
                            String cover, String content, int praiseNum, int praiseState,
                            int commentNum, String groupName, int groupId) {
        this.name = name;
        this.avatar = avatar;
        this.introduction = content;
        this.imgurl = cover;
        this.praisenumber = praiseNum;
        this.id = dynamicId;
        this.userid = userid;
        this.ispraised = praiseState;
        this.comments_num = commentNum;
        this.group_name = groupName;
        this.group_id = groupId;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPraisenumber() {
        return praisenumber;
    }

    public void setPraisenumber(int praisenumber) {
        this.praisenumber = praisenumber;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getIspraised() {
        return ispraised;
    }

    public void setIspraised(int ispraised) {
        this.ispraised = ispraised;
    }

    public int getComments_num() {
        return comments_num;
    }

    public void setComments_num(int comments_num) {
        this.comments_num = comments_num;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }
}
