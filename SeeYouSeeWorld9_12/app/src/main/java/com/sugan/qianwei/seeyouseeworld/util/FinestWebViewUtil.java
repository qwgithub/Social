package com.sugan.qianwei.seeyouseeworld.util;

import android.content.Context;

import com.sugan.qianwei.seeyouseeworld.R;
import com.thefinestartist.finestwebview.FinestWebView;

/**
 * Created by QianWei on 2017/9/29.
 */

public class FinestWebViewUtil {

    public static void startFinestWebActivity(Context context, String url){
        FinestWebView.Builder wvBuilder = new FinestWebView.Builder(context);
        //菜单栏
        wvBuilder.stringResCopiedToClipboard(R.string.copied_to_clipboard)
                .stringResRefresh(R.string.refresh)
                .stringResShareVia(R.string.share_via)
                .stringResCopyLink(R.string.copy_link)
                .stringResOpenWith(R.string.open_with);
        //标题栏属性
        wvBuilder.progressBarColor(context.getResources().getColor(R.color.white))
                .progressBarHeight(4)
                .showSwipeRefreshLayout(true)
                .swipeRefreshColor(context.getResources().getColor(R.color.mediumseagreen))
                .toolbarColor(context.getResources().getColor(R.color.mediumseagreen))
                .titleColor(context.getResources().getColor(R.color.white))
                .urlColor(context.getResources().getColor(R.color.white))
                .iconDefaultColor(context.getResources().getColor(R.color.white))
                .menuTextColor(context.getResources().getColor(R.color.black));

        wvBuilder.show(url);
    }
}
