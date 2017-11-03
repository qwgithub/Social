package com.sugan.qianwei.seeyouseeworld.fragment.search;

/**
 * Created by QianWei on 2017/11/3.
 */

public class SwitchFragmentEvent {

    public static final int USER_FRAGMENT = 1;
    public static final int GROUP_FRAGMENT = 2;
    public static final int DYNAMIC_FRAGMENT = 3;

    private int fragmentId;

    public SwitchFragmentEvent(int fragmentId) {
        this.fragmentId = fragmentId;
    }

    public int getFragmentId() {
        return fragmentId;
    }

    public void setFragmentId(int fragmentId) {
        this.fragmentId = fragmentId;
    }
}
