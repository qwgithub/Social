package com.sugan.qianwei.seeyouseeworld.util;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by 钱威 on 2015/11/12.
 */
public class SharedPreferenceUtil {

    public static void saveToCache(Context mContext, String name, String key, String value) {
        SharedPreferences sp = mContext.getSharedPreferences(name, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getFromCache(Context mContext, String name, String key) {
        SharedPreferences sp = mContext.getSharedPreferences(name, MODE_PRIVATE);
        return sp.getString(key, "");
    }

    public static void saveToCache(Context mContext, String name, String key, boolean value) {
        SharedPreferences sp = mContext.getSharedPreferences(name, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getFromCache(Context mContext, String name, String key, boolean b) {
        SharedPreferences sp = mContext.getSharedPreferences(name, MODE_PRIVATE);
        return sp.getBoolean(key, b);
    }

    public static void clearFileCache(Context context, String fileName) {
        SharedPreferences sp = context.getSharedPreferences(fileName, MODE_PRIVATE);
        if (sp != null) {
            sp.edit().clear().apply();
        }
    }
}
