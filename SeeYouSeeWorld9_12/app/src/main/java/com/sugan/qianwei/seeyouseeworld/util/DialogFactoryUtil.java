package com.sugan.qianwei.seeyouseeworld.util;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.activity.PersonalDataActivity;

/**
 * Created by QianWei on 2017/8/23.
 */

public class DialogFactoryUtil {

    static ProgressDialog dialog;

    public static void showProgressDialog(String waitingMessage, Context context) {
        dialog = new ProgressDialog(context);
        // 设置进度条风格，风格为圆形旋转的
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        // 设置ProgressDialog 提示信息
        dialog.setMessage(waitingMessage);
        // 设置ProgressDialog 的进度条是否不明确
        dialog.setIndeterminate(false);
        // 设置ProgressDialog 是否可以按退回按键取消
        dialog.setCancelable(false);
        dialog.show();
    }

    public static void diamissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }

    public static AlertView createActionSheet(Context context, String title, String message, String cancel, String[] destructive, OnItemClickListener listener) {
        return (new AlertView.Builder().setContext(context)
                .setStyle(AlertView.Style.ActionSheet)
                .setTitle(title)
                .setMessage(message)
                .setCancelText(cancel)
                .setDestructive(destructive)
                .setOnItemClickListener(listener)
                .build().setCancelable(true));
    }

    public static AlertView createAlterView(Context context, String title, String message, String cancel, String[] destructive, OnItemClickListener listener) {
        return (new AlertView.Builder().setContext(context)
                .setStyle(AlertView.Style.Alert)
                .setTitle(title)
                .setMessage(message)
                .setCancelText(cancel)
                .setDestructive(destructive)
                .setOnItemClickListener(listener)
                .build().setCancelable(true));
    }

}
