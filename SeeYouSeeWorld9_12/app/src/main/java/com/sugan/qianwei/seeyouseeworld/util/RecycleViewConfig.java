package com.sugan.qianwei.seeyouseeworld.util;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;

/**
 * Created by QianWei on 2017/11/2.
 */

public class RecycleViewConfig {

    public static LinearLayoutManager getHorizontalLinearLayoutManager(Context context) {
        //设置布局管理器-横向
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        return linearLayoutManager;
    }

    public static LinearLayoutManager getVerticalLinearLayoutManager(Context context) {
        //设置布局管理器-横向
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        return linearLayoutManager;
    }

    public static LinearLayoutManager getCustomVerticalLinearLayoutManager(Context context, boolean isScrollEnabled) {
        //设置布局管理器-横向
        CustomLinearLayoutManager linearLayoutManager = new CustomLinearLayoutManager(context, isScrollEnabled);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        return linearLayoutManager;
    }

}
