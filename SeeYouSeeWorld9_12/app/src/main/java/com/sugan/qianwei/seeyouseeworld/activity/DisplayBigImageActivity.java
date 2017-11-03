package com.sugan.qianwei.seeyouseeworld.activity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bigkoo.alertview.OnItemClickListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.util.DialogFactoryUtil;
import com.sugan.qianwei.seeyouseeworld.util.FileUtil;

public class DisplayBigImageActivity extends Activity {

    private static final int MY_PERMISSIONS_REQUEST_SAVEIMAGE = 0x01;
    private Bitmap picBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_big_image);
        ImageView bigImage = (ImageView) findViewById(R.id.big_image);
        String cover = getIntent().getStringExtra("imageurl");
        ImageLoader.getInstance().displayImage(cover, bigImage, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                picBitmap = loadedImage;
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

        bigImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        bigImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                DialogFactoryUtil.createActionSheet(DisplayBigImageActivity.this, "请选择操作", null, "取消", new String[]{"保存图片"}, new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        switch (position) {
                            case 0:
                                savePictureToLocal();
                                break;
                        }
                    }
                }).show();
                return true;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_SAVEIMAGE) {
            if ((grantResults.length > 0) && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "无法获取手机读取权限,请前往设置开启权限", Toast.LENGTH_SHORT).show();
            } else {
                if (picBitmap != null) {
                    FileUtil.saveBitmapToSpecifiedDir(DisplayBigImageActivity.this, picBitmap);
                }
            }
        }
    }

    private void savePictureToLocal() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_SAVEIMAGE);
            } else {
                try {
                    if (picBitmap != null) {
                        FileUtil.saveBitmapToSpecifiedDir(DisplayBigImageActivity.this, picBitmap);
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "无法获取手机读取权限,请前往设置开启权限", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        } else {
            if ((PackageManager.PERMISSION_GRANTED == getPackageManager().checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName()))) {
                if (picBitmap != null) {
                    FileUtil.saveBitmapToSpecifiedDir(DisplayBigImageActivity.this, picBitmap);
                }
            } else {
                Toast.makeText(getApplicationContext(), "无法获取手机读取权限,请前往设置开启权限", Toast.LENGTH_LONG).show();
            }
        }
    }

}
