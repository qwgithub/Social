package com.sugan.qianwei.seeyouseeworld.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.application.MyApp;
import com.sugan.qianwei.seeyouseeworld.util.Constants;
import com.sugan.qianwei.seeyouseeworld.util.Matchers;
import com.sugan.qianwei.seeyouseeworld.util.SharedPreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import cz.msebera.android.httpclient.Header;

public class LoginActivity extends Activity {

    private static final String TAG = "qianwei";
    private static final int GET_DATA_FROM_REGISTER = 0x001;
    private InputMethodManager manager;
    private AutoCompleteTextView autoTextView_email;
    private EditText et_password;
    private TextView tv_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        findViews();
        initViewListeners();

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
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                findViewById(R.id.activity_login).requestFocus();
            }
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_DATA_FROM_REGISTER) {
            if (resultCode == RESULT_OK) {
                Bundle result = data.getBundleExtra("userinfo");
                String mail = result.getString("mail");
                String passWord = result.getString("password");
                autoTextView_email.setText(mail);
                et_password.setText(passWord);
            }
        }
    }

    private void findViews() {
        autoTextView_email = (AutoCompleteTextView) findViewById(R.id.email);
        et_password = (EditText) findViewById(R.id.password);
        tv_login = (TextView) findViewById(R.id.login);
    }

    private void initViewListeners() {
        et_password.setOnEditorActionListener(actionListener);
        tv_login.setOnClickListener(loginClickListener);
    }

    private TextView.OnEditorActionListener actionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            Log.d(TAG, "onEditorAction: " + actionId);
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                postLogin();
            }
            return false;
        }
    };

    private View.OnClickListener loginClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            postLogin();
        }
    };

    /**
     * 开始登录
     * http://www.agrising.cn:8080/blog/public/index.php/api/login?mail=zhangsugan@qq.com&password=123456
     */
    private void postLogin() {
        final String em = autoTextView_email.getText().toString();
        final String pw = et_password.getText().toString();
        if (checkDate(em, pw)) {
            try {
                sendLoginRequest(em, pw);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 发送登录请求
     *
     * @param em
     * @param pw
     */
    private void sendLoginRequest(String em, String pw) throws JSONException {
        String url = Constants.MAIN_URL + "login";
        RequestParams params = new RequestParams();
        params.put("mail", em);
        params.put("password", pw);
        ((MyApp) getApplication()).getClient().post(LoginActivity.this, url,
                null, params, null, new AsyncHttpResponseHandler() {
                    @Override
                    public void onStart() {
                        super.onStart();
                        tv_login.setText("正在登录…");
                        tv_login.setClickable(false);
                    }

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            JSONObject responseJson = new JSONObject(new String(responseBody));
                            if (responseJson.getInt("status_code") == 0) {
                                Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                                SharedPreferenceUtil.saveToCache(getApplicationContext(), "userinfo", "token", responseJson.getString("token"));
                                SharedPreferenceUtil.saveToCache(getApplicationContext(), "userinfo", "name", responseJson.getString("name"));
                                SharedPreferenceUtil.saveToCache(getApplicationContext(), "userinfo", "avatar_url", responseJson.getString("avatar"));
                                SharedPreferenceUtil.saveToCache(getApplicationContext(), "userinfo", "userid", responseJson.getString("id"));
                                JPushInterface.setAlias(getApplicationContext(), responseJson.getString("id"), new TagAliasCallback() {
                                    @Override
                                    public void gotResult(int i, String s, Set<String> set) {
                                        Log.d("JPushReceiver", "gotResult: "+s);
                                    }
                                });
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, responseJson.getString("status_msg"), Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(LoginActivity.this, "登录失败,请重试", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        tv_login.setText("登录");
                        tv_login.setClickable(true);
                    }
                });
    }

    /**
     * 检查数据格式
     *
     * @param email
     * @param password
     */
    private boolean checkDate(String email, String password) {
        if (!Matchers.checkEmail(email)) {
            if (email.isEmpty()) {
                Toast.makeText(this, "请输入邮箱", Toast.LENGTH_SHORT).show();
                autoTextView_email.requestFocus();
                return false;
            }
            Toast.makeText(this, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
            et_password.requestFocus();
            return false;
        }
        return true;
    }

    public void goToRegister(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivityForResult(intent, GET_DATA_FROM_REGISTER);
    }

    public void forgetpassword(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("重置密码请联系管理员:\n" +
                "微信号：zhangsugan\n" +
                "邮箱:zhangsugan@qq.com")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void cancel_loginaction(View view) {
        finish();
    }

}
