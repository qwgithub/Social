package com.sugan.qianwei.seeyouseeworld.util;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created by QianWei on 2017/8/24.
 */

public class ImageCompressUtil {

    private static final String TAG = "qianwei";

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    // 如果是放大图片，filter决定是否平滑，如果是缩小图片，filter无影响
    public static Bitmap createScaleBitmap(Bitmap src, int dstWidth, int dstHeight) {
        Bitmap dst = Bitmap.createScaledBitmap(src, dstWidth, dstHeight, false);
        if (src != dst) { // 如果没有缩放，那么不回收
            src.recycle(); // 释放Bitmap的native像素数组
        }
        return dst;
    }

    // 从sd卡上加载图片
    public static Bitmap decodeSampledBitmapFromFile(String pathName, int reqWidth, int reqHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathName, options);
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
        options.inJustDecodeBounds = false;
        Bitmap src = BitmapFactory.decodeFile(pathName, options);
        return src;
    }

    public static String compressFile(String sourceFile, Context context){
        String resultFile = FileUtil.getOutputMediaFile(context).getPath();
        int reqWidth = PhoneSizeMeasureUtil.getDisplayMetrics(context).widthPixels;
        int reqHeight = PhoneSizeMeasureUtil.getDisplayMetrics(context).heightPixels;
        Log.d(TAG, "compressFile: "+reqWidth);
        Bitmap pic = decodeSampledBitmapFromFile(sourceFile, reqWidth, reqHeight);
        Log.d(TAG, "compressFile: "+pic.getByteCount());
        //将处理后的图片重新写回本地
        FileOutputStream b = null;
        try {
            b = new FileOutputStream(resultFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        pic.compress(Bitmap.CompressFormat.JPEG, 80, b);
        return resultFile;
    }

    public static String compressFile(String sourceFile, Context context, int reqWidth, int reqHeight){
        String resultFile = FileUtil.getOutputMediaFile(context).getPath();
        Log.d(TAG, "compressFile: "+reqWidth);
        Bitmap pic = decodeSampledBitmapFromFile(sourceFile, reqWidth, reqHeight);
        Log.d(TAG, "compressFile: "+pic.getByteCount());
        //将处理后的图片重新写回本地
        FileOutputStream b = null;
        try {
            b = new FileOutputStream(resultFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        pic.compress(Bitmap.CompressFormat.JPEG, 80, b);
        return resultFile;
    }

    private static long getFileSize(String path){
        File file = new File(path);
        if (file.exists()) {
            return file.length();
        }
        return 0;
    }

}
