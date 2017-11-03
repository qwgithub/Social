package com.sugan.qianwei.seeyouseeworld.bean.search.dynamic;

import java.io.Serializable;

/**
 * Created by QianWei on 2017/11/2.
 *
 */

public class DynamicsData implements Serializable {

    private int id;
    private int userid;
    private int group_id;
    private String imgurl;
    private String introduction;
    private String sta;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserid() {
        return userid;
    }

    public void setUserid(int userid) {
        this.userid = userid;
    }

    public int getGroup_id() {
        return group_id;
    }

    public void setGroup_id(int group_id) {
        this.group_id = group_id;
    }

    public String getImgurl() {
        return imgurl;
    }

    public void setImgurl(String imgurl) {
        this.imgurl = imgurl;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getSta() {
        return sta;
    }

    public void setSta(String sta) {
        this.sta = sta;
    }
}
