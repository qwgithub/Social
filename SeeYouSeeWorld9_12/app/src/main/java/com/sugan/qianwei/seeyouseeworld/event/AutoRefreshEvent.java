package com.sugan.qianwei.seeyouseeworld.event;

/**
 * Created by QianWei on 2017/9/29.
 */

public class AutoRefreshEvent {
    private boolean releaseSuccess;

    public AutoRefreshEvent(boolean releaseSuccess) {
        this.releaseSuccess = releaseSuccess;
    }

    public boolean isReleaseSuccess() {
        return releaseSuccess;
    }

    public void setReleaseSuccess(boolean releaseSuccess) {
        this.releaseSuccess = releaseSuccess;
    }
}
