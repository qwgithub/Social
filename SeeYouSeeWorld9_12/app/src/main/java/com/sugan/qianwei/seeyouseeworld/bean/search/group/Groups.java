package com.sugan.qianwei.seeyouseeworld.bean.search.group;

import java.io.Serializable;
import java.util.List;

/**
 * Created by QianWei on 2017/11/2.
 * 兴趣小组
 */
public class Groups implements Serializable {

    private String next_page_url;
    private List<GroupsData> data;

    public String getNext_page_url() {
        return next_page_url;
    }

    public void setNext_page_url(String next_page_url) {
        this.next_page_url = next_page_url;
    }

    public List<GroupsData> getData() {
        return data;
    }

    public void setData(List<GroupsData> data) {
        this.data = data;
    }
}
