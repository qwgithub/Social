package com.sugan.qianwei.seeyouseeworld.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.sugan.qianwei.seeyouseeworld.R;

public class ChangeSignatureActivity extends Activity {

    private EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signature);
        editText = (EditText) findViewById(R.id.signature_edittext);
    }

    public void signature_back(View view) {
        finish();
    }

    public void complete(View view) {
        String content = editText.getText().toString();
        Intent data = new Intent();
        data.putExtra("signature", content);
        setResult(RESULT_OK, data);
        finish();
    }
}
