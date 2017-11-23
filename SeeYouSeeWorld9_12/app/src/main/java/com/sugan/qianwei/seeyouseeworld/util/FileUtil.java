package com.sugan.qianwei.seeyouseeworld.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by QianWei on 2017/8/28.
 */

public class FileUtil {
    /**
     * Create a File for saving recognized images
     */
    public static File getOutputMediaFile(Context context) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/" + context.getPackageName()
                + "/ImageFiles");
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d("qianwei", "getOutputMediaFile: return null");
                return null;
            }
        }
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + "/" + timeStamp);
        return mediaFile;
    }

    public static File getExternalCacheDirFile(Context context) {
        return StorageUtils.getCacheDirectory(context, true);
    }

    public static void saveBitmapToSpecifiedDir(final Activity activity, final Bitmap bitmap) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) // 判断是否可以对SDcard进行操作
                {      // 获取SDCard指定目录下
                    String sdCardDir = Environment.getExternalStorageDirectory() + "/DCIM/See/";
                    File dirFile = new File(sdCardDir);  //目录转化成文件夹
                    if (!dirFile.exists()) {                //如果不存在，那就建立这个文件夹
                        if (!dirFile.mkdirs()){
                            return;
                        }
                    }                            //文件夹有啦，就可以保存图片啦
                    File file = new File(sdCardDir, System.currentTimeMillis() + ".jpg");// 在SDcard的目录下创建图片文,以当前时间为其命名
                    FileOutputStream out = null;
                    try {
                        out = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    notifyPhoneAlbum(file, activity);
                    try {
                        if (out != null) {
                            out.flush();
                            out.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
        Toast.makeText(activity, "图片已保存", Toast.LENGTH_SHORT).show();

    }

    //这个广播的目的就是更新图库
    private static void notifyPhoneAlbum(File file, Context activity) {
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(file);
        intent.setData(uri);
        activity.sendBroadcast(intent);
    }

}
