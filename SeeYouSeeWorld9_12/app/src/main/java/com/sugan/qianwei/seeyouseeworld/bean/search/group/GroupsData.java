package com.sugan.qianwei.seeyouseeworld.bean.search.group;

import java.io.Serializable;

/**
 * Created by QianWei on 2017/11/2.
 */

public class GroupsData implements Serializable {
    private int id;
    private String name;
    private String intro;
    private String create_man_id;
    private String cover;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getCreate_man_id() {
        return create_man_id;
    }

    public void setCreate_man_id(String create_man_id) {
        this.create_man_id = create_man_id;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
