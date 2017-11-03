package com.sugan.qianwei.seeyouseeworld.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class RegisterActivity extends Activity {
    private static final String TAG = "qianwei";

    private InputMethodManager manager;
    private AutoCompleteTextView actv_email;
    private EditText et_password;
    private EditText et_confirm_password;
    private EditText et_verificationcode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        findViews();

        et_confirm_password.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO){
                    postRegister();
                }
                return false;
            }
        });
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
                findViewById(R.id.activity_register).requestFocus();
            }
        }
        return super.onTouchEvent(event);
    }

    private void findViews() {
        actv_email = (AutoCompleteTextView) findViewById(R.id.email_1);
        et_password = (EditText) findViewById(R.id.password_1);
        et_confirm_password = (EditText) findViewById(R.id.confiem_password_1);
        et_verificationcode = (EditText) findViewById(R.id.verificationcode_1);
    }

    public void getverificationcode(View view) {

    }

    private boolean checkEmail(String email) {
        if (!Matchers.checkEmail(email)) {
            actv_email.requestFocus();
            if (email.isEmpty()) {
                Toast.makeText(this, "请输入邮箱", Toast.LENGTH_SHORT).show();
                return false;
            }
            Toast.makeText(this, "邮箱格式不正确", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * 数据格式检查
     * @param email
     * @param password
     * @param confirmPassword
     * @param //verificationCode
     */
    private boolean checkDate(String email, String password, String confirmPassword){
        if (!checkEmail(email)) {
            return false;
        }
//        if (verificationCode.isEmpty()){
//            et_verificationcode.requestFocus();
//            Toast.makeText(this, "请输入验证码", Toast.LENGTH_SHORT).show();
//            return false;
//        }
        if (!password.equals(confirmPassword)){
            Toast.makeText(this, "两次密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
            et_password.setText("");
            et_confirm_password.setText("");
            et_password.requestFocus();
            return false;
        } else if (password.isEmpty()){
            Toast.makeText(this, "密码不能为空", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    /**
     * 开始注册
     */
    private void postRegister() {
        final String em = actv_email.getText().toString();
        final String pw = et_password.getText().toString();
        final String cpw = et_confirm_password.getText().toString();
//        final String vc = et_verificationcode.getText().toString();
        if (checkDate(em, pw, cpw)) {
            try {
                sendRegisterRequest(em, pw, cpw);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 注册
     * @param em
     * @param pw
     * @param cpw
     * @throws JSONException
     */
    private void sendRegisterRequest(final String em, final String pw, final String cpw) throws JSONException{
        String url = Constants.MAIN_URL + "register";
        RequestParams params = new RequestParams();
        params.put("mail", em);
        params.put("password", pw);
        params.put("confirmpwd", cpw);
        ((MyApp) getApplication()).getClient().post(RegisterActivity.this, url,
                null, params, null, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            JSONObject responseJson = new JSONObject(new String(responseBody));
                            if (responseJson.getInt("status_code") == 0) {
                                //注册成功，把用户信息直接传到登录界面
                                Intent intent = new Intent();
                                Bundle bundle = new Bundle();
                                bundle.putString("mail", em);
                                bundle.putString("password", pw);
                                intent.putExtra("userinfo", bundle);
                                setResult(RESULT_OK, intent);
                                finish();
                            } else {
                                Toast.makeText(RegisterActivity.this, responseJson.getString("status_msg"), Toast.LENGTH_SHORT).show();
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


    public void register(View view) {
        postRegister();
    }

    public void cancel_register(View view) {
        finish();
    }
}
