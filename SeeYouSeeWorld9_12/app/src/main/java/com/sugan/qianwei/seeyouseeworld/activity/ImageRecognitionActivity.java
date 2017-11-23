package com.sugan.qianwei.seeyouseeworld.activity;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.Area;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.transition.Slide;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.application.MyApplication;
import com.sugan.qianwei.seeyouseeworld.util.Constants;
import com.sugan.qianwei.seeyouseeworld.util.DialogFactoryUtil;
import com.sugan.qianwei.seeyouseeworld.util.FileUtil;
import com.sugan.qianwei.seeyouseeworld.util.FinestWebViewUtil;
import com.sugan.qianwei.seeyouseeworld.util.FormatUtil;
import com.sugan.qianwei.seeyouseeworld.views.DrawingView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.util.TextUtils;
import de.hdodenhof.circleimageview.CircleImageView;


public class ImageRecognitionActivity extends Activity {

    private static final String TAG = "qianwei";

    private static final int DISPLAY_RECOGNIZE_RESULT = 1;
    private static final int REQUEST_CODE_FROM_ALBUM = 200;
    private static final int CAMERA_WITH_DATA = REQUEST_CODE_FROM_ALBUM + 1;
    private static final int MY_PERMISSIONS_REQUEST_CALL_CAMERA = 10;
    private static final int MY_PERMISSIONS_REQUEST_CALL_ALBUM = 11;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 12;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 13;
    private File momentPic;   //图片保存路径

    private TextView tv_usecamera;
    private TextView tv_usealbum;
    private ViewFlipper vf_showresult;
    private GestureDetector cameraFocusGestureDetector;
    private ScaleGestureDetector zoomScaleGestureDetector;
    private FrameLayout rl_selectimage;
    private RelativeLayout ll_showarea;
    private LinearLayout ll_pageguide;

    private ProgressDialog dialog;   //等待框

    private Preview preview;
    private boolean pictureTaken;
    private Uri imageUri;
    private boolean drawingViewSet = true;
    private DrawingView drawingView;
    private ImageView iv_selectedimage;
    private PopupWindow popupWindow;
    //识别结果展示
    private View recognizeResultlayout;
    private CircleImageView sample_image;
    private TextView recognize_result;
    private String searchLink;
    private Button search_from_baidu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_recognition);
        findViews();
        initResultLayout();
//        checkPermissions();
        zoomScaleGestureDetector = new ScaleGestureDetector(this, new CustomScaleGestureDetector());
        cameraFocusGestureDetector = new GestureDetector(this, new CameraFocusGestureDetector());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setEnterTransition(new Slide().setDuration(1000));
            getWindow().setExitTransition(new Slide().setDuration(1000));
        }
        rl_selectimage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                zoomScaleGestureDetector.onTouchEvent(event);
                cameraFocusGestureDetector.onTouchEvent(event);
                return true;
            }
        });

    }

    private void initResultLayout() {
        recognizeResultlayout = LayoutInflater.from(this).inflate(R.layout.image_recognize_result_layout, null);
        sample_image = (CircleImageView) recognizeResultlayout.findViewById(R.id.sample_image);
        recognize_result = (TextView) recognizeResultlayout.findViewById(R.id.recognize_result);
        search_from_baidu = (Button) recognizeResultlayout.findViewById(R.id.search_from_baidu);
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isCameraUsed() && preview.camera != null) {
            try {
                preview.camera.stopPreview();
                preview.camera.release();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (dialog != null) {
            dialog = null;
        }
    }

    private void findViews() {
        rl_selectimage = (FrameLayout) findViewById(R.id.rl_selectimage);
        ll_showarea = (RelativeLayout) findViewById(R.id.ll_showarea);
        tv_usealbum = (TextView) findViewById(R.id.tv_usealbum);
        tv_usecamera = (TextView) findViewById(R.id.tv_usecamera);
        vf_showresult = (ViewFlipper) findViewById(R.id.vf_showresult);
        drawingView = (DrawingView) findViewById(R.id.drawingview);
        preview = new Preview(this);
        View camera_frame = View.inflate(this, R.layout.camera_frame, null);
        rl_selectimage.addView(preview);
        rl_selectimage.addView(camera_frame);
        iv_selectedimage = (ImageView) findViewById(R.id.iv_selectedimage);
        ll_pageguide = (LinearLayout) findViewById(R.id.ll_pageguide);

    }

    /**
     * 相机权限检查
     *
     * @param view
     */
    public void usecamera(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED &&
                    checkSelfPermission(Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                actionPickFromCamera(view);
            } else {
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                        checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_CALL_CAMERA);
                } else if (checkSelfPermission(
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_CALL_CAMERA);
                } else if (checkSelfPermission(
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CALL_CAMERA);
                }
            }
        } else {
            if ((PackageManager.PERMISSION_GRANTED == getPackageManager().checkPermission(Manifest.permission.CAMERA, getPackageName()))) {
                actionPickFromCamera(view);
            } else {
                Toast.makeText(getApplicationContext(), "无法获取相机权限,请前往设置开启权限", Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 相册权限检查
     *
     * @param view
     */
    public void usealbum(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_CALL_ALBUM);
            } else {
                try {
                    actionPickFromAlbum();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "无法获取相册权限,请前往设置开启权限", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        } else {
            if ((PackageManager.PERMISSION_GRANTED == getPackageManager().checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, getPackageName()))) {
                actionPickFromAlbum();
            } else {
                Toast.makeText(getApplicationContext(), "无法获取相册权限,请前往设置开启权限", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void recog_back(View view) {
        onBackPressed();
    }


    /**
     * 识别图片
     * http://www.agrising.cn:8080/blog/public/index.php/api/recognition
     * 参数 imagefile
     */
    private void imageRecognize() {
        if (momentPic == null || !pictureTaken) {
            Toast.makeText(getApplicationContext(), "请选择图片后再识别", Toast.LENGTH_SHORT).show();
            return;
        }
        sendRecognizeRequest(momentPic);
    }

    /**
     * 发送识别请求
     *
     * @param imageFile
     */
    private void sendRecognizeRequest(File imageFile) {
        String url = Constants.MAIN_URL + "recognition";
        RequestParams params = new RequestParams();
        try {
            params.put("imagefile", imageFile);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        ((MyApplication) getApplication()).getClient().post(getApplicationContext(), url,
                null, params, null, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (responseBody != null) {
                            Log.d(TAG, "onSuccess: " + new String(responseBody));
                            try {
                                JSONObject result = new JSONObject(new String(responseBody));
                                String name = result.getString("name");
                                String thumbnail = result.getString("thumbnail");
                                String link = result.getString("link");
                                if (link != null && !link.equals("")) {
                                    search_from_baidu.setVisibility(View.VISIBLE);
                                    searchLink = link;
                                } else {
                                    search_from_baidu.setVisibility(View.GONE);
                                }
                                Message msg = new Message();
                                msg.what = DISPLAY_RECOGNIZE_RESULT;

                                Bundle bundle = new Bundle();
                                bundle.putString("name", name);
                                bundle.putString("thumbnail", thumbnail);
                                msg.setData(bundle);

                                handler.sendMessage(msg);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(ImageRecognitionActivity.this, "识别失败", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        DialogFactoryUtil.showProgressDialog("图片正在上传", ImageRecognitionActivity.this);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        DialogFactoryUtil.diamissDialog();
                    }
                });
    }

    /**
     * 选取本地图片
     */
    /**
     * 选取本地图片
     */
    private void actionPickFromAlbum() {
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
        //2.17 更新  裁剪框比例
        intent.putExtra("aspectX", rl_selectimage.getWidth());
        intent.putExtra("aspectY", rl_selectimage.getHeight());
        intent.putExtra("outputX", rl_selectimage.getWidth());
        intent.putExtra("outputY", rl_selectimage.getHeight());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_CODE_FROM_ALBUM);
    }

    private void actionPickFromCamera(View view) {
        if (pictureTaken) {
            try {
                preview.camera.startPreview();
                ll_showarea.setVisibility(View.GONE);
                ll_showarea.setAnimation(AnimationUtils.makeOutAnimation(ImageRecognitionActivity.this, true));
                ((TextView) view).setText("拍照");
                pictureTaken = false;
                iv_selectedimage.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                preview.camera.takePicture(shutterCallback, rawCallback, jpegCallback);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case DISPLAY_RECOGNIZE_RESULT:
                    Bundle bundle = msg.getData();
                    String name = bundle.getString("name");
                    String thumbnail = bundle.getString("thumbnail");
                    recognize_result.setText(name);
                    ImageLoader.getInstance().displayImage(thumbnail, sample_image);
                    showRecognizeResultPopupWindow(recognizeResultlayout);
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == REQUEST_CODE_FROM_ALBUM) {   //本地相册图片回调
            if (imageUri != null) {
                pictureTaken = true;
                iv_selectedimage.setVisibility(View.VISIBLE);
                iv_selectedimage.setImageURI(imageUri);
            }
            imageRecognize();
        }
    }

    private void startCameraPreview() {
        try {
            if (!isCameraUsed() && preview != null) {
                preview.previewCamera(preview.getHolder());
            }
            if (!pictureTaken && preview != null) {
                preview.camera.startPreview();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "摄像头权限被拒绝,请前往设置打开", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }


    //设置缩放
    private void handleZoom(boolean isZoomIn, Camera camera) {
        try {
            Parameters params = camera.getParameters();
            if (params.isZoomSupported()) {
                int maxZoom = params.getMaxZoom();
                int zoom = params.getZoom();
                if (isZoomIn && zoom < maxZoom - 3) {
                    zoom++;
                } else if (zoom > 0) {
                    zoom--;
                }
                params.setZoom(zoom);
                camera.setParameters(params);
            } else {
                Toast.makeText(getApplicationContext(), "手机暂不支持缩放", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "手机暂不支持缩放", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    /**
     * 检查权限
     * READ_PHONE_STATE、LOCATION
     */
    /*private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(
                    Manifest.permission.READ_PHONE_STATE)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                        MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
            }
        } else {
            if ((PackageManager.PERMISSION_GRANTED != getPackageManager().checkPermission(Manifest.permission.READ_PHONE_STATE, getPackageName()))) {
//                Toast.makeText(this, "无法读取手机状态", Toast.LENGTH_SHORT).show();
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(
                    Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
        } else {
            if ((PackageManager.PERMISSION_GRANTED != getPackageManager().checkPermission(Manifest.permission.ACCESS_FINE_LOCATION, getPackageName()))) {

            }
        }
    }*/

    /**
     * 展示识别结果
     *
     * @param contentView
     */
    private void showRecognizeResultPopupWindow(View contentView) {
        popupWindow = new PopupWindow(this);
        popupWindow.setContentView(contentView);
        popupWindow.setAnimationStyle(R.style.show_comments_anim);
        // 设置弹出窗体的宽和高
        popupWindow.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        // 设置弹出窗体可点击
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(findViewById(R.id.activity_imagerecognize), Gravity.BOTTOM, 0, 0);
    }

    @Override  //请求权限回调
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_CAMERA) {
            if ((grantResults.length > 0) && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "相机权限获取失败,请前往设置开启权限", Toast.LENGTH_SHORT).show();
            } else if ((grantResults.length > 1) && grantResults[1] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "文件读写权限获取失败,请前往设置开启权限", Toast.LENGTH_SHORT).show();
            } else {
                startCameraPreview();
            }
        }
        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_ALBUM) {
            if ((grantResults.length > 0) && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "无法获取相册权限,请前往设置开启权限", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    actionPickFromAlbum();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "无法获取相册权限,请前往设置开启权限", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
            }
        }
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            if ((grantResults.length > 0) && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "手机状态信息获取失败,请前往设置开启权限", Toast.LENGTH_SHORT).show();
            } else {

            }
        }
        if (requestCode == MY_PERMISSIONS_REQUEST_LOCATION) {
            if ((grantResults.length > 0) && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "地理位置获取失败,请前往设置开启权限", Toast.LENGTH_SHORT).show();
            } else {

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //判断相机是否正在被占用
    public boolean isCameraUsed() {
        Camera camera = null;
        try {
            camera = Camera.open();
        } catch (RuntimeException e) {
            return true;
        } finally {
            if (camera != null) {
                camera.release();
            }
        }
        return false;
    }

    //按下快门回调
    ShutterCallback shutterCallback = new ShutterCallback() {
        @Override
        public void onShutter() {

        }
    };

    /**
     * Handles data for raw picture
     */
    PictureCallback rawCallback = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

        }
    };

    /**
     * Handles data for jpeg picture
     */
    PictureCallback jpegCallback = new PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            try {
                Bitmap mBitmap = null;
                if (null != data) {
                    mBitmap = BitmapFactory.decodeByteArray(data, 0, data.length);//data是字节数据，将其解析成位图
                    camera.stopPreview();
                }
                if (mBitmap != null) {
                    //剪切中间部位 createBitmap
                    Bitmap rectBitmap = Bitmap.createBitmap(mBitmap, mBitmap.getWidth() / 5, mBitmap.getHeight() / 5, mBitmap.getWidth() * 3 / 5, mBitmap.getHeight() * 3 / 5);
//                    创建新缩略位图 createScaledBitmap
                    Bitmap thumbnailBitmap = Bitmap.createScaledBitmap(rectBitmap, rectBitmap.getWidth() / 2, rectBitmap.getHeight() / 2, true);
//                    Bitmap finalBitmap = CompressImage.compressImage(thumbnailBitmap);
                    iv_selectedimage.setVisibility(View.VISIBLE);
                    iv_selectedimage.setImageBitmap(rectBitmap);
                    //上传缩略图 thumbnailBitmap
                    momentPic = FileUtil.getOutputMediaFile(getApplicationContext());
                    if (momentPic == null) {
                        Log.d(TAG, "momentPic == null");
                        Toast.makeText(ImageRecognitionActivity.this, "momentPic == null", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        Log.d(TAG, "actionPickFromAlbum: momentPic = " + momentPic.getPath());
                    }
                    FormatUtil.saveBitmapToFile(thumbnailBitmap, momentPic);
                }
                if (data != null && data.length > 0) {
                    pictureTaken = true;
                    tv_usecamera.setText("重拍");
                    imageRecognize();
                }
                if (mBitmap != null && !mBitmap.isRecycled()) {
                    mBitmap.recycle();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public void search_from_baidu(View view) {
        if (!TextUtils.isEmpty(searchLink)) {
            FinestWebViewUtil.startFinestWebActivity(ImageRecognitionActivity.this, searchLink);
        } else {
            Toast.makeText(getApplicationContext(), "链接地址为空", Toast.LENGTH_SHORT).show();
        }
    }

    //缩小放大手势
    class CustomScaleGestureDetector implements ScaleGestureDetector.OnScaleGestureListener {

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor();
            if (scaleFactor > 1) {
                handleZoom(true, preview.camera);
            } else {
                handleZoom(false, preview.camera);
            }
            return true;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public void onScaleEnd(ScaleGestureDetector detector) {

        }
    }

    //相机手动对焦
    class CameraFocusGestureDetector extends GestureDetector.SimpleOnGestureListener {

        //点击事件
        @Override
        public boolean onDown(MotionEvent event) {
            float x = event.getX();
            float y = event.getY();
            Rect touchRect = new Rect(
                    (int) (x - 200),
                    (int) (y - 200),
                    (int) (x + 200),
                    (int) (y + 200));

            final Rect targetFocusRect = new Rect(
                    touchRect.left * 2000 / preview.getWidth() - 1000,
                    touchRect.top * 2000 / preview.getHeight() - 1000,
                    touchRect.right * 2000 / preview.getWidth() - 1000,
                    touchRect.bottom * 2000 / preview.getHeight() - 1000);

            preview.doTouchFocus(targetFocusRect);
            if (drawingViewSet) {
                drawingView.setHaveTouch(true, touchRect);
                drawingView.invalidate();

                // Remove the square indicator after 1000 msec
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        drawingView.setHaveTouch(false, new Rect(0, 0, 0, 0));
                        drawingView.invalidate();
                    }
                }, 1500);
            }
            return super.onDown(event);
        }

    }

    class Preview extends SurfaceView implements SurfaceHolder.Callback {
        private static final String TAG = "Preview";

        public SurfaceHolder mHolder;
        public Camera camera;

        public Preview(Context context) {
            super(context);
            mHolder = getHolder();
            mHolder.addCallback(this);
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.d(TAG, "surfaceCreated: ");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Log.d(TAG, "surfaceCreated: Build.VERSION_CODES.M");
                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CALL_CAMERA);
                } else {
                    startCameraPreview();
                }
            } else {
                if ((PackageManager.PERMISSION_GRANTED == getPackageManager().checkPermission(Manifest.permission.CAMERA, getPackageName()))) {
                    startCameraPreview();
                }
            }

        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // Surface will be destroyed when we return, so stop the preview.
            // Because the CameraDevice object is not a shared resource, it's very
            // important to release it when the activity is paused.
            Log.d(TAG, "surfaceDestroyed: ");
            try {
                if (preview.camera != null) {
                    preview.camera.stopPreview();
                    preview.camera.release();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
            // Now that the size is known, set up the camera parameters and begin
            // the preview.

        }

        @Override
        public void draw(Canvas canvas) {
            super.draw(canvas);
            Paint p = new Paint(Color.RED);
            canvas.drawText("预览失败", canvas.getWidth() / 2, canvas.getHeight() / 2, p);
        }

        private void previewCamera(SurfaceHolder holder) {
            try {
                camera = Camera.open();
                camera.setPreviewDisplay(holder);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    camera.enableShutterSound(false);
                }
                Parameters parameters = camera.getParameters();
                if (parameters.getSupportedFocusModes().contains(
                        Parameters.FOCUS_MODE_CONTINUOUS_VIDEO)) {
                    Log.d(TAG, "previewCamera: FOCUS_MODE_AUTO");
                    parameters.setFocusMode(Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
                }
                if (parameters.getSupportedFlashModes().contains(
                        Parameters.FLASH_MODE_AUTO)) {
                    Log.d(TAG, "previewCamera: FLASH_MODE_AUTO");
                    parameters.setFlashMode(Parameters.FLASH_MODE_AUTO);
                }
                if (parameters.getSupportedSceneModes().contains(
                        Parameters.SCENE_MODE_AUTO)) {
                    Log.d(TAG, "previewCamera: SCENE_MODE_AUTO");
                    parameters.setSceneMode(Parameters.SCENE_MODE_AUTO);
                }
                if (parameters.getSupportedWhiteBalance().contains(Parameters.WHITE_BALANCE_AUTO)) {
                    Log.d(TAG, "previewCamera: WHITE_BALANCE_AUTO");
                    parameters.setWhiteBalance(Parameters.WHITE_BALANCE_AUTO);
                }
//                parameters.set("iso", "400");  // values can be "auto", "100", "200", "400", "800", "1600"
//                parameters.set("contrast", 50);  //对比
//                parameters.set("brightness", 100);  //亮度
//                parameters.set("saturation", 200);  // 饱和
//                parameters.set("sharpness", 50);  //锐度
//                parameters.setPictureFormat(ImageFormat.JPEG);
//                parameters.set("jpeg-quality", 100);
                if (parameters.isZoomSupported()) {
                    Log.d(TAG, "ZoomSupported");
                    parameters.setZoom(0);
                }
                if (this.getResources().getConfiguration().orientation
                        != Configuration.ORIENTATION_LANDSCAPE) {
                    Log.d(TAG, "previewCamera: setRotation(90)");
                    parameters.set("orientation", "portrait");
                    camera.setDisplayOrientation(90);
                    parameters.setRotation(90);
                } else {
                    parameters.set("orientation", "landscape");
                    Log.d(TAG, "previewCamera: setRotation(180)");
                    camera.setDisplayOrientation(0);
                    parameters.setRotation(180);
                }
                camera.setParameters(parameters);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        AutoFocusCallback myAutoFocusCallback = new AutoFocusCallback() {

            @Override
            public void onAutoFocus(boolean arg0, Camera arg1) {
                if (arg0) {
                    camera.cancelAutoFocus();
                }
            }
        };

        /**
         * 触摸对焦
         * Called from PreviewSurfaceView to set touch focus.
         *
         * @param - Rect - new area for auto focus
         */
        public void doTouchFocus(Rect tfocusRect) {
            try {
                Log.d(TAG, "doTouchFocus: " + tfocusRect.left + tfocusRect.top + tfocusRect.right + tfocusRect.bottom);
                List<Area> focusList = new ArrayList<>();
                Area focusArea = new Area(tfocusRect, 1000);
                focusList.add(focusArea);

                List<Area> meterList = new ArrayList<>();
                Area meterArea = new Area(tfocusRect, 1000);
                meterList.add(meterArea);

                Parameters param = camera.getParameters();
                if (param.getMaxNumFocusAreas() > 0) {
                    param.setFocusAreas(focusList);
                }
                if (param.getMaxNumMeteringAreas() > 0) {
                    param.setMeteringAreas(meterList);
                }
                camera.setParameters(param);
                camera.autoFocus(myAutoFocusCallback);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i(TAG, "Unable to autofocus");
            }
        }
    }

}
