package com.sugan.qianwei.seeyouseeworld.event;

/**
 * Created by QianWei on 2017/9/27.
 */

public class NetworkChangeEvent {
    private boolean isConnected;

    public NetworkChangeEvent(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }
}
