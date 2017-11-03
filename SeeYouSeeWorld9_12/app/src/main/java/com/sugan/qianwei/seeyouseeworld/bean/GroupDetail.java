package com.sugan.qianwei.seeyouseeworld.bean;

import java.io.Serializable;

/**
 * Created by QianWei on 2017/9/19.
 */

public class GroupDetail implements Serializable{

    private int groupId;
    private String groupName;
    private String groupDetail;
    private String groupCover;
    private int groupMemberNumber;
    private int ifConcern;

    public GroupDetail(int groupId, String groupName, String groupCover) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupCover = groupCover;
    }

    public GroupDetail(int groupId, String groupName, String groupDetail, String groupCover, int groupMemberNumber, int ifConcern) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupDetail = groupDetail;
        this.groupCover = groupCover;
        this.groupMemberNumber = groupMemberNumber;
        this.ifConcern = ifConcern;
    }

    public GroupDetail(int groupId, String groupName, String groupDetail, String groupCover, int groupMemberNumber) {
        this.groupCover = groupCover;
        this.groupName = groupName;
        this.groupDetail = groupDetail;
        this.groupMemberNumber = groupMemberNumber;
        this.groupId = groupId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupDetail() {
        return groupDetail;
    }

    public void setGroupDetail(String groupDetail) {
        this.groupDetail = groupDetail;
    }

    public String getGroupCover() {
        return groupCover;
    }

    public void setGroupCover(String groupCover) {
        this.groupCover = groupCover;
    }

    public int getGroupMemberNumber() {
        return groupMemberNumber;
    }

    public void setGroupMemberNumber(int groupMemberNumber) {
        this.groupMemberNumber = groupMemberNumber;
    }

    public int getIfConcern() {
        return ifConcern;
    }

    public void setIfConcern(int ifConcern) {
        this.ifConcern = ifConcern;
    }
}
