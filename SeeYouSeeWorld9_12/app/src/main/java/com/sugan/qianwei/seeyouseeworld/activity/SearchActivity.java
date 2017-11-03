package com.sugan.qianwei.seeyouseeworld.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.OnItemClickListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.application.MyApp;
import com.sugan.qianwei.seeyouseeworld.bean.search.SearchResult;
import com.sugan.qianwei.seeyouseeworld.util.Constants;
import com.sugan.qianwei.seeyouseeworld.util.DialogFactoryUtil;
import com.sugan.qianwei.seeyouseeworld.util.SaveArrayListUtil;
import com.sugan.qianwei.seeyouseeworld.util.UserInfoManage;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class SearchActivity extends Activity {

    private static final String TAG = "SearchActivity";

    public static final String SPNAME = "SearchDataList";

    private AutoCompleteTextView actextview_search;
    //保存搜索词
    private ArrayList<String> searchDataArrayList;

    private ListView searchRecordListView;
    private ArrayAdapter<String> adapter;

    private LinearLayout ll_searchdatarecord;
    private InputMethodManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        findViews();
        initData();
        initListeners();
        initInputMethodManage();
        initListView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchDataArrayList = getArrayList();
        if (searchDataArrayList.isEmpty()) {
            hideSearchRecordView();
        } else {
            showSearchRecordView();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            hideSoftInputFromWindow();
        }
        return super.onTouchEvent(event);
    }

    //初始化软键盘
    private void initInputMethodManage() {
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        actextview_search.requestFocus();
    }

    //隐藏软键盘
    private void hideSoftInputFromWindow() {
        if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            actextview_search.clearFocus();
        }
    }

    private void initListView() {
        searchRecordListView.setAdapter(adapter);
        searchRecordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                actextview_search.setText(searchDataArrayList.get(position));
                searchAction();
            }
        });
        searchRecordListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideSoftInputFromWindow();
                return false;
            }
        });
    }

    //隐藏搜索记录界面
    private void hideSearchRecordView() {
        //隐藏搜索记录列表
        if (ll_searchdatarecord.getVisibility() != View.GONE) {
            ll_searchdatarecord.setVisibility(View.GONE);
        }
    }

    //显示搜索记录界面
    private void showSearchRecordView() {
        //隐藏搜索记录列表
        if (ll_searchdatarecord.getVisibility() == View.GONE) {
            ll_searchdatarecord.setVisibility(View.VISIBLE);
        }
    }

    private void initData() {
        searchDataArrayList = getArrayList();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, searchDataArrayList);
    }

    private void initListeners() {
        actextview_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                //光标在内容末尾
                actextview_search.setSelection(actextview_search.getText().length());
            }
        });
        actextview_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    searchAction();
                }
                return false;
            }
        });
    }

    //获取本地搜索记录列表
    private ArrayList<String> getArrayList() {
        return SaveArrayListUtil.getSearchArrayList(getApplicationContext(), SPNAME);
    }

    //保存搜索词到本地
    private void saveArrayList(String keyWord) {
        SaveArrayListUtil.saveArrayList(getApplicationContext(), searchDataArrayList, keyWord, SPNAME);
    }

    private void findViews() {
        ll_searchdatarecord = (LinearLayout) findViewById(R.id.ll_searchdatarecord);
        actextview_search = (AutoCompleteTextView) findViewById(R.id.actextview_search);
        searchRecordListView = (ListView) findViewById(R.id.searchRecordListView);
    }

    /**
     * 获取搜索结果   no（没有类型） user group dynamic
     *
     * @param keyWord
     */
    private void getSearchResult(String keyWord) {

        String url = Constants.MAIN_URL + "search";
        RequestParams params = new RequestParams();
        params.put("user_id", UserInfoManage.getUserId(this));
        params.put("type", "no");
        params.put("key_word", keyWord);
        ((MyApp) (getApplication())).getClient().get(getApplicationContext(), url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                DialogFactoryUtil.showProgressDialog("正在搜索", SearchActivity.this);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (responseBody == null) {
                    Toast.makeText(SearchActivity.this, "搜索失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d(TAG, "onSuccess: " + new String(responseBody));
                Gson gson = new Gson();
                Type type = new TypeToken<SearchResult>() {
                }.getType();
                SearchResult searchResult = gson.fromJson(new String(responseBody), type);
                String code = searchResult.getStatus_code();
                Log.d(TAG, "onSuccess: "+code);
                if ("0".equals(code)) {
                    Bundle bundle = new Bundle();
                    Intent intent = new Intent(SearchActivity.this, SearchResultDisplayActivity.class);
                    intent.putExtra("name", actextview_search.getText().toString());
                    bundle.putSerializable("result", searchResult);
                    intent.putExtra("search_result", bundle);
                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                if (responseBody == null) {
                    Toast.makeText(SearchActivity.this, "搜索失败", Toast.LENGTH_SHORT).show();
                    return;
                }
                Log.d(TAG, "onFailure: " + new String(responseBody));
            }

            @Override
            public void onFinish() {
                super.onFinish();
                DialogFactoryUtil.diamissDialog();
            }
        });
    }

    //返回主页
    public void search_back(View view) {
        hideSoftInputFromWindow();
        onBackPressed();
    }

    //点击搜索
    public void start_search(View view) {
        searchAction();
    }

    //开始搜索
    private void searchAction() {
        String keyWord = actextview_search.getText().toString();
        if (!"".equals(keyWord.trim())) {
            saveArrayList(keyWord);
//            hideSearchRecordView();
            hideSoftInputFromWindow();
            getSearchResult(keyWord);
        }
    }

    //清除搜索记录
    public void clear_record(View view) {
        DialogFactoryUtil.createActionSheet(this, "确定清除本地搜索记录吗？", null, "取消", new String[]{"确定"}, new OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                if (position == 0) {
                    clearSearchRecord();
                }
            }
        }).show();

    }

    private void clearSearchRecord(){
        searchDataArrayList.clear();
        adapter.notifyDataSetChanged();
        SaveArrayListUtil.clearSearchArrayList(getApplicationContext(), SPNAME);
        hideSearchRecordView();
    }
}
