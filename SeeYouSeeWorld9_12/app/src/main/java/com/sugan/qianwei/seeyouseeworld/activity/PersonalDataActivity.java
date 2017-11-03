package com.sugan.qianwei.seeyouseeworld.activity;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.OnItemClickListener;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.adapter.PersonalDataListAdapter;
import com.sugan.qianwei.seeyouseeworld.application.MyApp;
import com.sugan.qianwei.seeyouseeworld.bean.PersonalDataItem;
import com.sugan.qianwei.seeyouseeworld.util.Constants;
import com.sugan.qianwei.seeyouseeworld.util.DialogFactoryUtil;
import com.sugan.qianwei.seeyouseeworld.util.FileUtil;
import com.sugan.qianwei.seeyouseeworld.util.ImageCompressUtil;
import com.sugan.qianwei.seeyouseeworld.util.NetworkStateChecker;
import com.sugan.qianwei.seeyouseeworld.util.SharedPreferenceUtil;
import com.sugan.qianwei.seeyouseeworld.views.SpecialListView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class PersonalDataActivity extends Activity {

    private static final String TAG = "qianwei";
    private static final int PROFESSIONS_REQUEST = 1; //修改职业
    private static final int HOBBIES_REQUEST = 2;  //修改爱好
    private static final int SIGNATURE_REQUEST = 3;  // 签名
    private static final int PERMISSIONS_REQUEST_CALL_CAMERA = 4;    //拍照权限
    private static final int PERMISSIONS_REQUEST_CALL_ALBUM = 5;    //相册权限
    private static final int GET_PICTURE_FROM_ALBUM = 6;    //跳转到相册
    private static final int GET_PICTURE_FROM_CAMERA = 7;   //跳转到相机
    private SpecialListView dataListView;
    private PersonalDataListAdapter dataListAdapter;
    private ArrayList<PersonalDataItem> dataList;

    private View layout;

    private CircleImageView personal_avatar;
    private TextView modifyData;
    private File momentPic;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);
        findViews();
        initList();
        String userid = SharedPreferenceUtil.getFromCache(getApplicationContext(), "userinfo", "userid");
        getPersonalDataFromServer(userid);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            modifyData.setVisibility(View.VISIBLE);
            switch (requestCode) {
                case PROFESSIONS_REQUEST:
                    String selectedProfession = data.getStringExtra("profession");
                    dataList.get(2).setDescription(selectedProfession);
                    dataListAdapter.notifyDataSetChanged();
                    break;
                case HOBBIES_REQUEST:
                    String selectedHobby = data.getStringExtra("hobby");
                    dataList.get(3).setDescription(selectedHobby);
                    dataListAdapter.notifyDataSetChanged();
                    break;
                case SIGNATURE_REQUEST:
                    String selectedSignature = data.getStringExtra("signature");
                    dataList.get(4).setDescription(selectedSignature);
                    dataListAdapter.notifyDataSetChanged();
                    break;
                case GET_PICTURE_FROM_CAMERA:
                    new DisplayImageFromFileTask().execute(personal_avatar.getWidth(), personal_avatar.getHeight());
                    break;
                case GET_PICTURE_FROM_ALBUM:
                    new DisplayImageFromFileTask().execute(personal_avatar.getWidth(), personal_avatar.getHeight());
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
                Toast.makeText(PersonalDataActivity.this, "没有找到储存目录", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(PersonalDataActivity.this, "没有储存卡", Toast.LENGTH_LONG).show();
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
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, GET_PICTURE_FROM_ALBUM);
    }

    private void findViews() {
        dataListView = (SpecialListView) findViewById(R.id.personaldata_listview);
        personal_avatar = (CircleImageView) findViewById(R.id.personal_avatar);
        modifyData = (TextView) findViewById(R.id.modifydata);
    }

    private void initList() {
        dataList = new ArrayList<>();
        dataListAdapter = new PersonalDataListAdapter(dataList, PersonalDataActivity.this);
        dataListView.setAdapter(dataListAdapter);
        dataListView.setOnItemClickListener(dataListItemClickListener);
        personal_avatar.setOnClickListener(avatarClickListener);
    }

    public void personaldata_back(View view) {
        finish();
    }

    /**
     * "修改"按钮监听
     * @param view
     */
    public void modifydata(View view) {
        if (!NetworkStateChecker.isNetworkAvailable(this)) {
            Toast.makeText(this, "请连接网络后重试", Toast.LENGTH_SHORT).show();
            return;
        }
        String nickname = dataList.get(0).getDescription();
        String gender = dataList.get(1).getDescription();
        String profession = dataList.get(2).getDescription();
        String hobby = dataList.get(3).getDescription();
        String signature = dataList.get(4).getDescription();
        String userid = SharedPreferenceUtil.getFromCache(getApplicationContext(), "userinfo", "userid");
        String token = SharedPreferenceUtil.getFromCache(getApplicationContext(), "userinfo", "token");
        if (!"".equals(token)) {
            sendModifyDataRequest(userid, token, nickname, gender, profession, hobby, signature);
        } else {
            Toast.makeText(getApplicationContext(), "登录已超时,请退出后重新登录", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 发送修改资料请求
     * @param userid
     * @param token
     * @param nickname
     * @param gender
     * @param profession
     * @param hobby
     * @param signature
     */
    private void sendModifyDataRequest(String userid, String token, String nickname, String gender, String profession, String hobby, String signature) {
        String url = Constants.MAIN_URL + "updateUserData";
        RequestParams params = new RequestParams();
        params.put("name", nickname);
        params.put("hobby", hobby);
        params.put("signature", signature);
        params.put("profession", profession);
        params.put("gender", gender);
        params.put("userid", userid);
        params.put("token", token);
        if (momentPic != null) {
            String path = ImageCompressUtil.compressFile(momentPic.getPath(), getApplicationContext(), personal_avatar.getWidth(), personal_avatar.getHeight());
            File file = new File(path);
            try {
                params.put("avatar", file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        ((MyApp) getApplication()).getClient().post(getApplicationContext(), url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String response = new String(responseBody);
                try {
                    JSONObject json = new JSONObject(response);
                    if ("0".equals(json.getString("status_code"))) {
                        String name = json.getString("name");
                        String avatar = json.getString("avatar");
                        Log.d(TAG, "onSuccess: avatar=" + avatar + ",name=" + name);
                        SharedPreferenceUtil.saveToCache(getApplicationContext(), "userinfo", "name", name);
                        if (avatar != null && !avatar.equals("null")) {
                            Log.d(TAG, "onSuccess: avatar != null");
                            SharedPreferenceUtil.saveToCache(getApplicationContext(), "userinfo", "avatar_url", avatar);
                        }
                        Toast.makeText(getApplicationContext(), json.getString("status_msg"), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
            }
        });
    }

    private void getPersonalDataFromServer(String userid) {

        String url = Constants.MAIN_URL + "getUserInfo";
        RequestParams params = new RequestParams();
        params.put("userid", userid);
        ((MyApp) getApplication()).getClient().get(getApplicationContext(), url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                if (!dataList.isEmpty()) {
                    dataList.clear();
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    JSONObject json = new JSONObject(new String(responseBody));
                    String code = json.getString("status_code");
                    if ("0".equals(code)) {
                        JSONObject data = json.getJSONObject("data");
                        ImageLoader.getInstance().displayImage(data.getString("avatar"), personal_avatar);
                        dataList.add(new PersonalDataItem("昵称", data.getString("name"), false));
                        dataList.add(new PersonalDataItem("性别", data.getString("gender"), false));
                        dataList.add(new PersonalDataItem("职业", data.getString("profession"), true));
                        dataList.add(new PersonalDataItem("爱好", data.getString("hobby"), true));
                        dataList.add(new PersonalDataItem("签名", data.getString("signature"), true));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "请求失败", Toast.LENGTH_SHORT).show();
                if (responseBody != null) {
                    Log.d(TAG, "onFailure: " + new String(responseBody));
                }
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dataListAdapter.setList(dataList);
                dataListAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 自定义对话框layout修改昵称
     * @param context
     * @param title
     * @param confirmClickListener
     * @param cancelClickListener
     * @return
     */
    public AlertDialog createAlertDialog(Context context, String title, DialogInterface.OnClickListener confirmClickListener, DialogInterface.OnClickListener cancelClickListener) {
        layout = View.inflate(context, R.layout.modifynickname_layout, null);
        EditText et_NickName = ((EditText) (layout.findViewById(R.id.et_modify_nickname)));
        String content = dataList.get(0).getDescription();
        et_NickName.setText(content);
        et_NickName.setSelection(content.length());
        return (new AlertDialog.Builder(context).setTitle(title).setView(layout)
                .setPositiveButton("确定", confirmClickListener).setNegativeButton("取消", cancelClickListener).create());
    }

    /**
     * 个人资料列表监听事件
     */
    private AdapterView.OnItemClickListener dataListItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "onItemClick: " + position);
            switch (position) {
                case 0:
                    createAlertDialog(PersonalDataActivity.this, "修改昵称", new DialogInterface.OnClickListener() {
                        @Override  //确认
                        public void onClick(DialogInterface dialog, int which) {
                            String nickname = ((EditText) (layout.findViewById(R.id.et_modify_nickname))).getText().toString();
                            if (!"".equals(nickname.trim())) {
                                dataList.get(0).setDescription(nickname);
                                dataListAdapter.notifyDataSetChanged();
                                modifyData.setVisibility(View.VISIBLE);
                            }
                        }
                    }, new DialogInterface.OnClickListener() {
                        @Override  //取消
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();
                    break;
                case 1:
                    DialogFactoryUtil.createActionSheet(PersonalDataActivity.this, "选择性别", null,
                            "取消", new String[]{"男", "女"}, new OnItemClickListener() {
                                @Override
                                public void onItemClick(Object o, int position) {
                                    if (position == 0) {
                                        dataList.get(1).setDescription("男");
                                    } else if (position == 1) {
                                        dataList.get(1).setDescription("女");
                                    }
                                    dataListAdapter.notifyDataSetChanged();
                                    modifyData.setVisibility(View.VISIBLE);
                                }
                            }).show();
                    break;
                case 2:
                    Intent professionIntent = new Intent(PersonalDataActivity.this, ProfessionActivity.class);
                    startActivityForResult(professionIntent, PROFESSIONS_REQUEST);
                    break;
                case 3:
                    Intent hobbiesIntent = new Intent(PersonalDataActivity.this, HobbiesActivity.class);
                    startActivityForResult(hobbiesIntent, HOBBIES_REQUEST);
                    break;
                case 4:
                    Intent signatureIntent = new Intent(PersonalDataActivity.this, ChangeSignatureActivity.class);
                    startActivityForResult(signatureIntent, SIGNATURE_REQUEST);
                    break;
            }
        }
    };

    /**
     * 头像点击事件
     */
    private View.OnClickListener avatarClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            DialogFactoryUtil.createActionSheet(PersonalDataActivity.this, "修改头像", null, "取消",
                    new String[]{"拍照", "手机相册选择"}, new OnItemClickListener() {
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
                    }).show();
        }
    };

    /**
     * 异步显示图片
     */
    class DisplayImageFromFileTask extends AsyncTask<Integer, Integer, Bitmap> {

        @Override
        protected Bitmap doInBackground(Integer... params) {
            Bitmap bitmap = ImageCompressUtil.decodeSampledBitmapFromFile(momentPic.getPath(), params[0], params[1]);
            return bitmap;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (bitmap != null) {
                personal_avatar.setImageBitmap(bitmap);
                modifyData.setVisibility(View.VISIBLE);
            }
        }
    }

}
