package com.sugan.qianwei.seeyouseeworld.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.List;

/**
 * Created by QianWei on 2017/9/27.
 * 判断app运行状态
 */

public class ApplicationStateUtil {

    /**
     * 是否在前台运行,判断是否在前台运行 就判断栈顶的是否是这个包名
     * info.topActivity.getPackageName().equals("包名")
     *
     * @param context
     * @return
     */
    private static boolean isAppRunning(Context context) {
        String packageName = context.getPackageName();
        String topActivityClassName = getTopActivityName(context);

        if (packageName != null && topActivityClassName != null && topActivityClassName.startsWith(packageName)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 是否在后台运行,判断是否在后台运行 就判断栈中是否有这个包名
     * info.baseActivity.getPackageName().equals("包名")
     *
     * @param context
     * @return
     */
    public static String getTopActivityName(Context context) {
        String topActivityClassName = null;
        ActivityManager activityManager =
                (ActivityManager) (context.getSystemService(android.content.Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = activityManager.getRunningTasks(1);
        if (runningTaskInfos != null) {
            ComponentName f = runningTaskInfos.get(0).topActivity;
            topActivityClassName = f.getClassName();
        }
        return topActivityClassName;
    }
}
