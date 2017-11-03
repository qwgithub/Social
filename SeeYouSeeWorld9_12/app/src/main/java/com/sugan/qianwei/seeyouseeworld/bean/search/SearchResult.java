package com.sugan.qianwei.seeyouseeworld.bean.search;

import android.os.Parcel;
import android.os.Parcelable;

import com.sugan.qianwei.seeyouseeworld.bean.search.dynamic.Dynamics;
import com.sugan.qianwei.seeyouseeworld.bean.search.dynamic.DynamicsData;
import com.sugan.qianwei.seeyouseeworld.bean.search.group.Groups;
import com.sugan.qianwei.seeyouseeworld.bean.search.group.GroupsData;
import com.sugan.qianwei.seeyouseeworld.bean.search.user.Users;
import com.sugan.qianwei.seeyouseeworld.bean.search.user.UsersData;

import java.io.Serializable;
import java.util.List;

/**
 * Created by QianWei on 2017/11/2.
 */

public class SearchResult implements Serializable {

    private String status_code;
    private String status_msg;
    private Groups groups_data;
    private Users users_data;
    private Dynamics dynamics_data;

    public String getStatus_code() {
        return status_code;
    }

    public void setStatus_code(String status_code) {
        this.status_code = status_code;
    }

    public String getStatus_msg() {
        return status_msg;
    }

    public void setStatus_msg(String status_msg) {
        this.status_msg = status_msg;
    }

    public Groups getGroups_data() {
        return groups_data;
    }

    public void setGroups_data(Groups groups_data) {
        this.groups_data = groups_data;
    }

    public Users getUsers_data() {
        return users_data;
    }

    public void setUsers_data(Users users_data) {
        this.users_data = users_data;
    }

    public Dynamics getDynamics_data() {
        return dynamics_data;
    }

    public void setDynamics_data(Dynamics dynamics_data) {
        this.dynamics_data = dynamics_data;
    }
}
