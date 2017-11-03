package com.sugan.qianwei.seeyouseeworld.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * Created by QianWei on 2017/8/25.
 */

public class PhoneSizeMeasureUtil {

    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics dm = null;
        try {
            Resources resources = context.getResources();
            dm = resources.getDisplayMetrics();
            return dm;
        } catch (Exception e) {
            if (dm != null) {
                dm.widthPixels = 480;
                dm.heightPixels = 800;
            }
            Log.d("qianwei", "getDisplayMetrics run failed.");
            e.printStackTrace();
        }
        return null;
    }

}
