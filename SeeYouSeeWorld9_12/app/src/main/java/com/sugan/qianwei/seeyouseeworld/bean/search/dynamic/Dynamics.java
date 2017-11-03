package com.sugan.qianwei.seeyouseeworld.bean.search.dynamic;

import com.sugan.qianwei.seeyouseeworld.bean.ForumContentItem;

import java.io.Serializable;
import java.util.List;

/**
 * Created by QianWei on 2017/11/2.
 */

public class Dynamics implements Serializable{

    private String next_page_url;
    private List<ForumContentItem> data;

    public String getNext_page_url() {
        return next_page_url;
    }

    public void setNext_page_url(String next_page_url) {
        this.next_page_url = next_page_url;
    }

    public List<ForumContentItem> getData() {
        return data;
    }

    public void setData(List<ForumContentItem> data) {
        this.data = data;
    }
}
