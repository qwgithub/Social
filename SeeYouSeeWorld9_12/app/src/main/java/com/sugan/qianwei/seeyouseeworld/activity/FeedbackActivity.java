package com.sugan.qianwei.seeyouseeworld.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.application.MyApp;
import com.sugan.qianwei.seeyouseeworld.util.Constants;
import com.sugan.qianwei.seeyouseeworld.util.SharedPreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class FeedbackActivity extends Activity {
    private static final String TAG = "qianwei";
    private EditText et_suggestions;
    private Button bt_sendsuggestions;
    private EditText et_contact_way;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        findViews();
        et_suggestions.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!"".equals(s.toString())) {
                    bt_sendsuggestions.setBackgroundColor(getResources().getColor(R.color.mediumseagreen));
                    bt_sendsuggestions.setClickable(true);
                } else {
                    bt_sendsuggestions.setBackgroundColor(getResources().getColor(R.color.gray));
                    bt_sendsuggestions.setClickable(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void findViews() {
        et_suggestions = (EditText) findViewById(R.id.et_suggestionsinput);
        bt_sendsuggestions = (Button) findViewById(R.id.bt_sendsuggestions);
        et_contact_way = (EditText) findViewById(R.id.et_contact_way);
    }

    public void feedback_back(View view) {
        finish();
    }

    public void sendsuggestions(View view) {
        sendSuggestionsRequest();
    }

    private void sendSuggestionsRequest(){
        String url = Constants.MAIN_URL + "feed_back";
        RequestParams params = new RequestParams();
        if (et_contact_way.getText().toString().trim().length() > 0){
            params.put("contact_way", et_contact_way.getText().toString());
        }
        String userid = SharedPreferenceUtil.getFromCache(getApplicationContext(), "userinfo", "userid");
        if (!"".equals(userid)) {
            params.put("userid", userid);
        } else {
            Toast.makeText(getApplicationContext(), "请先登录再提交", Toast.LENGTH_SHORT).show();
            return;
        }
        params.put("content", et_suggestions.getText().toString());
        ((MyApp)getApplication()).getClient().post(getApplicationContext(), url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (responseBody == null){
                    Toast.makeText(getApplicationContext(), "服务器开小差了", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    JSONObject jsonObject = new JSONObject(new String(responseBody));
                    String statuscode = jsonObject.getString("status_code");
                    if ("0".equals(statuscode)){
                        Toast.makeText(getApplicationContext(), jsonObject.getString("status_msg"), Toast.LENGTH_SHORT).show();
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
}
