package com.sugan.qianwei.seeyouseeworld.util;

import android.content.Context;

import static com.thefinestartist.utils.content.ContextUtil.getApplicationContext;

/**
 * Created by QianWei on 2017/11/2.
 */

public class UserInfoManage {

    public static String getUserId(Context context){
        return SharedPreferenceUtil.getFromCache(context.getApplicationContext(), "userinfo", "userid");
    }
}
