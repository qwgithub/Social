package com.sugan.qianwei.seeyouseeworld.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sugan.qianwei.seeyouseeworld.R;

public class ProfessionActivity extends Activity {

    private ListView profession_listview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profession);
        profession_listview = (ListView) findViewById(R.id.profession_listview);
        profession_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent data = new Intent();
                data.putExtra("profession", parent.getItemAtPosition(position).toString());
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    public void profession_back(View view) {
        finish();
    }
}
