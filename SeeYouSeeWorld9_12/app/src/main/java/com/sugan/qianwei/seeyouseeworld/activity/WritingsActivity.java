package com.sugan.qianwei.seeyouseeworld.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.OnItemClickListener;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.application.MyApp;
import com.sugan.qianwei.seeyouseeworld.bean.GroupDetail;
import com.sugan.qianwei.seeyouseeworld.event.AutoRefreshEvent;
import com.sugan.qianwei.seeyouseeworld.util.Constants;
import com.sugan.qianwei.seeyouseeworld.util.DialogFactoryUtil;
import com.sugan.qianwei.seeyouseeworld.util.FileUtil;
import com.sugan.qianwei.seeyouseeworld.util.ImageCompressUtil;
import com.sugan.qianwei.seeyouseeworld.util.SharedPreferenceUtil;
import com.sugan.qianwei.seeyouseeworld.views.AutoLinefeedLayout;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cz.msebera.android.httpclient.Header;

public class WritingsActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "qianwei";
    private static final int GET_PICTURE_FROM_CAMERA = 0x01;
    private static final int GET_PICTURE_FROM_ALBUM = 0x02;
    private static final int PERMISSIONS_REQUEST_CALL_ALBUM = 0x16;
    private static final int PERMISSIONS_REQUEST_CALL_CAMERA = 0x17;
    private static final int GET_GROUP_INFO = 0x20;
    private AutoLinefeedLayout writing_selectedimage_container;
    private TextView writing_cancel;
    private TextView writing_confirm;
    private EditText writing_content;
    private TextView writing_usecameraoralbum;
    private RelativeLayout writing_selectgroup;
    private TextView select_groupname;
    private InputMethodManager manager;
    private File momentPic;
    private Uri imageUri;

    //上传多张图片备用
    private int imageViewTag = 1;
    private int wantDeleteImageTag;

    private int selectedGroupId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_writings);
        findViews();
        initInputMethodManage();
        writing_content.addTextChangedListener(watcher);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                writing_content.clearFocus();
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            int reqWidth = writing_usecameraoralbum.getWidth();
            switch (requestCode) {
                case GET_PICTURE_FROM_CAMERA:
                    new DisplayImageFromFileTask().execute(reqWidth, reqWidth);
                    break;
                case GET_PICTURE_FROM_ALBUM:
                    if (imageUri != null) {
                        new DisplayImageFromFileTask().execute(reqWidth, reqWidth);
                    }
                    break;
                case GET_GROUP_INFO:
                    GroupDetail groupDetail = (GroupDetail) data.getSerializableExtra("groupinfo");
                    select_groupname.setText(groupDetail.getGroupName());
                    selectedGroupId = groupDetail.getGroupId();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CALL_CAMERA) {
            if ((grantResults.length > 0) && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "相机权限获取失败,请前往设置开启权限", Toast.LENGTH_SHORT).show();
            } else if ((grantResults.length > 1) && grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "文件读写权限获取失败,请前往设置开启权限", Toast.LENGTH_SHORT).show();
            } else {
                openCamera();
            }
        }
        if (requestCode == PERMISSIONS_REQUEST_CALL_ALBUM) {
            if ((grantResults.length > 0) && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "相册权限获取失败,请前往设置开启权限", Toast.LENGTH_SHORT).show();
            } else {
                openAlbum();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 取消
     *
     * @param view
     */
    public void cancel(View view) {
        if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        onBackPressed();
    }

    /**
     * 发表
     *
     * @param view
     */
    public void release(View view) {
        if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        sendReleaseRequest(writing_content.getText().toString());
    }

    /**
     * 选择动态分组
     *
     * @param view
     */
    public void select_group(View view) {
        if (selectedGroupId == -1) {
            Intent selectGroupIntent = new Intent(WritingsActivity.this, SelectGroupActivity.class);
            startActivityForResult(selectGroupIntent, GET_GROUP_INFO);
        } else {
            DialogFactoryUtil.createActionSheet(this, "是否删除已选分组", null, "取消", new String[]{"删除"}, new OnItemClickListener() {
                @Override
                public void onItemClick(Object o, int position) {
                    if (position == 0) {
                        select_groupname.setText("选择分组");
                        selectedGroupId = -1;
                    }
                }
            }).show();
        }
    }

    /**
     * 发表动态请求
     *
     * @param introduction
     */
    private void sendReleaseRequest(String introduction) {
        final String releaseUrl = Constants.MAIN_URL + "release";
        String token = SharedPreferenceUtil.getFromCache(getApplicationContext(), "userinfo", "token");
        String userid = SharedPreferenceUtil.getFromCache(getApplicationContext(), "userinfo", "userid");
        RequestParams params = new RequestParams();
        try {
            params.put("userid", userid);
            params.put("introduction", introduction);
            params.put("token", token);
            if (selectedGroupId >= 0){
                params.put("group_id", selectedGroupId);
            }
            String path = ImageCompressUtil.compressFile(momentPic.getPath(), getApplicationContext());
            File file = new File(path);
            params.put("imagefile", file);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ((MyApp) getApplication()).getClient().post(WritingsActivity.this, releaseUrl,
                null, params, null, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            JSONObject result = new JSONObject(new String(responseBody));
                            String status_code = result.getString("status_code");
                            if ("0".equals(status_code)) {
                                Toast.makeText(getApplicationContext(), result.getString("status_msg"), Toast.LENGTH_SHORT).show();
                                EventBus.getDefault().post(new AutoRefreshEvent(true));
                            }
                            finish();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(WritingsActivity.this, "动态发布失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        DialogFactoryUtil.showProgressDialog("动态正在发布", WritingsActivity.this);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        DialogFactoryUtil.diamissDialog();
                    }
                });
    }

    /**
     * 选择相册或系统相机
     *
     * @param view
     */
    public void use_cameraoralbum(View view) {
        if (writing_selectedimage_container.getChildCount() > 1) {
            Toast.makeText(getApplicationContext(), "暂时只能上传一张图片", Toast.LENGTH_SHORT).show();
            return;
        }
        if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        String[] destructive = {"拍照", "手机相册选择"};
        DialogFactoryUtil.createActionSheet(this, "选择图片", null,
                "取消", destructive, alterViewItemClickListener).show();
    }

    private void findViews() {
        writing_cancel = (TextView) findViewById(R.id.writing_cancel);
        writing_confirm = (TextView) findViewById(R.id.writing_release);
        writing_content = (EditText) findViewById(R.id.writing_content);
        select_groupname = (TextView) findViewById(R.id.select_groupname);
        writing_selectgroup = (RelativeLayout) findViewById(R.id.writing_selectgroup);
        writing_usecameraoralbum = (TextView) findViewById(R.id.writing_cameraoralbum);
        writing_selectedimage_container = (AutoLinefeedLayout) findViewById(R.id.writing_selectedimage_container);
    }

    private void initInputMethodManage() {
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        writing_content.requestFocus();
    }

    /**
     * 选取本地图片
     */
    private void getPictureFromFromAlbum() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSIONS_REQUEST_CALL_ALBUM);
            } else {
                openAlbum();
            }
        } else {
            if ((PackageManager.PERMISSION_GRANTED == getPackageManager().checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName()))) {
                openAlbum();
            } else {
                Toast.makeText(getApplicationContext(), "相册权限获取失败,请前往设置开启权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 相机权限检查
     */
    private void getPictureFromCamera() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                openCamera();
            } else {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_CALL_CAMERA);
                } else if (checkSelfPermission(
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            PERMISSIONS_REQUEST_CALL_CAMERA);
                } else if (checkSelfPermission(
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            PERMISSIONS_REQUEST_CALL_CAMERA);
                }
            }
        } else {
            if ((PackageManager.PERMISSION_GRANTED == getPackageManager().checkPermission(Manifest.permission.CAMERA, getPackageName()))) {
                openCamera();
            } else {
                Toast.makeText(getApplicationContext(), "相机权限获取失败,请前往设置开启权限", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * 打开系统相机拍照
     */
    private void openCamera() {
        //先验证手机是否有sdcard
        String status = Environment.getExternalStorageState();
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            try {
                //獲取系統版本
                int currentapiVersion = android.os.Build.VERSION.SDK_INT;
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                momentPic = FileUtil.getOutputMediaFile(getApplicationContext());
                if (currentapiVersion < 24) {
                    // 从文件中创建uri
                    imageUri = Uri.fromFile(momentPic);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                } else {
                    //兼容android7.0 使用共享文件的形式
                    ContentValues contentValues = new ContentValues(1);
                    contentValues.put(MediaStore.Images.Media.DATA, momentPic.getAbsolutePath());
                    imageUri = this.getApplication().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                }

                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                startActivityForResult(intent, GET_PICTURE_FROM_CAMERA);
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(WritingsActivity.this, "没有找到储存目录", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(WritingsActivity.this, "没有储存卡", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * 选取本地图片
     */
    private void openAlbum() {
        momentPic = FileUtil.getOutputMediaFile(getApplicationContext());
        if (momentPic == null) {
            Log.d(TAG, "momentPic == null");
            return;
        }
        imageUri = Uri.fromFile(momentPic);
        //3.4 更新 action
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, GET_PICTURE_FROM_ALBUM);
    }


    //动态添加Imageview预览图片
    private void addImageView(Bitmap bitmap, int tag) {
        int width = writing_usecameraoralbum.getWidth();
        ImageView image = new ImageView(this);
        image.setImageBitmap(bitmap);
        image.setTag(tag);
        image.setOnClickListener(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, width);
        writing_selectedimage_container.addView(image, params);
    }

    private void deleteImageView() {
        writing_selectedimage_container.removeViewAt(wantDeleteImageTag);
        imageViewTag--;
    }

    /**
     * 动态输入框监听
     */
    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s != null && s.length() != 0) {
                writing_confirm.setTextColor(getResources().getColor(R.color.black));
                writing_confirm.setClickable(true);
            } else {
                writing_confirm.setTextColor(getResources().getColor(R.color.gray_cc));
                writing_confirm.setClickable(false);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    /**
     * 选择相机或相册
     */
    private OnItemClickListener alterViewItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(Object o, int position) {
            switch (position) {
                case 0:
                    getPictureFromCamera();
                    break;
                case 1:
                    getPictureFromFromAlbum();
                    break;
            }
        }
    };

    /**
     * 删除已选图片
     */
    private OnItemClickListener deleteImageItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(Object o, int position) {
            switch (position) {
                case 0:
                    deleteImageView();
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {

        wantDeleteImageTag = (int) v.getTag();
        if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        String[] destructive = {"确定删除"};
        DialogFactoryUtil.createActionSheet(this, "删除图片", null,
                "取消", destructive, deleteImageItemClickListener).show();

    }

    /**
     * 异步显示已选图片
     */
    class DisplayImageFromFileTask extends AsyncTask<Integer, Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(Integer... params) {
            Bitmap bitmap = ImageCompressUtil.decodeSampledBitmapFromFile(momentPic.getPath(), params[0], params[1]);
            int bitmapWidth = bitmap.getWidth();
            int bitmapHeight = bitmap.getHeight();
            Bitmap scaledBitmap;
            int x;
            if (bitmapHeight >= bitmapWidth) {
                x = (bitmapHeight - bitmapWidth) / 2;
                scaledBitmap = Bitmap.createBitmap(bitmap, 0, x, bitmapWidth, bitmapWidth);
            } else {
                x = (bitmapWidth - bitmapHeight) / 2;
                scaledBitmap = Bitmap.createBitmap(bitmap, x, 0, bitmapHeight, bitmapHeight);
            }
            return scaledBitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            if (bitmap != null) {
                addImageView(bitmap, imageViewTag++);
            }
        }
    }

}
