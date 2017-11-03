package com.sugan.qianwei.seeyouseeworld.bean.search.user;

import java.io.Serializable;
import java.util.List;

/**
 * Created by QianWei on 2017/11/2.
 */

public class Users implements Serializable {
    private String next_page_url;
    private List<UsersData> data;

    public String getNext_page_url() {
        return next_page_url;
    }

    public void setNext_page_url(String next_page_url) {
        this.next_page_url = next_page_url;
    }

    public List<UsersData> getData() {
        return data;
    }

    public void setData(List<UsersData> data) {
        this.data = data;
    }
}
