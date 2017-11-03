package com.sugan.qianwei.seeyouseeworld.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.sugan.qianwei.seeyouseeworld.event.NetworkChangeEvent;
import com.sugan.qianwei.seeyouseeworld.util.NetworkStateChecker;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by QianWei on 17/9/27.
 * 监听网络状态变化
 */
public class NetWorkStateChangeReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean isConnected = NetworkStateChecker.isNetworkAvailable(context);
        EventBus.getDefault().post(new NetworkChangeEvent(isConnected));
    }
}
