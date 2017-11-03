package com.sugan.qianwei.seeyouseeworld.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.sugan.qianwei.seeyouseeworld.R;

public class HobbiesActivity extends Activity {

    private ListView hobbies_listview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hobbies);
        hobbies_listview = (ListView) findViewById(R.id.hobbies_listview);
        hobbies_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent data = new Intent();
                data.putExtra("hobby", parent.getItemAtPosition(position).toString());
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }

    public void hobbies_back(View view) {
        finish();
    }
}
