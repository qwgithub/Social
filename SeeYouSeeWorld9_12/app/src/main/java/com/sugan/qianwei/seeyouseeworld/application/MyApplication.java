package com.sugan.qianwei.seeyouseeworld.application;

import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.HttpResponseCache;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.util.FileUtil;
import com.tencent.stat.MtaSDkException;
import com.tencent.stat.StatCrashReporter;
import com.tencent.stat.StatService;


import cn.jpush.android.api.JPushInterface;

/**
 * Created by QianWei on 2017/8/21.
 * 自定义Application
 */

public class MyApplication extends Application {

    private AsyncHttpClient mClient;

    private RefWatcher refWatcher;

    private final static String MTA_APP_KEY = "AIRU2U93XA5H";

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        refWatcher = LeakCanary.install(this);
        initAsyncHttpClient();
        initUILConfig();
        initJPush();
        initMTA();
    }

    public static RefWatcher getRefWatcher(Context context) {
        MyApplication application = (MyApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    //初始化网络请求
    private void initAsyncHttpClient(){
        mClient = new AsyncHttpClient();
    }

    //初始化腾讯移动分析
    private void initMTA() {
        try {
            StatService.startStatService(this, MTA_APP_KEY, com.tencent.stat.common.StatConstants.VERSION);
            Log.d("MTA","MTA初始化成功");
        } catch (MtaSDkException e) {
            Log.d("MTA","MTA初始化失败");
            e.printStackTrace();
        }
        StatService.setContext(this);
        //2.1.0及以上版本的SDK支持自动埋点页面统计，免除开发者对每个页面调用API之困扰。
        StatService.registerActivityLifecycleCallbacks(this);
        //Crash跟踪
        StatCrashReporter.getStatCrashReporter(getApplicationContext()).setJavaCrashHandlerStatus(true);
    }

    //初始化极光推送SDK
    private void initJPush() {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }

    public AsyncHttpClient getClient() {
        return mClient;
    }

    //配置UIL
    private void initUILConfig(){
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .showImageOnFail(R.drawable.default_cover)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions(defaultOptions)
                .threadPoolSize(5)//线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 1)
                //缓存目录
                .diskCache(new UnlimitedDiskCache(FileUtil.getExternalCacheDirFile(getApplicationContext())))
                .memoryCache(new LruMemoryCache(5* 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
                .memoryCacheSize(5 * 1024 * 1024)
                .discCacheSize(5 * 1024 * 1024)
                .discCacheFileCount(100) //缓存的文件数量
                .build();
        ImageLoader.getInstance().init(config);
    }

}
