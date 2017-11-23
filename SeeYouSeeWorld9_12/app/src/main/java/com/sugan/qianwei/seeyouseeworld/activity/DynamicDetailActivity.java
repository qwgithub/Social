package com.sugan.qianwei.seeyouseeworld.activity;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.alertview.OnItemClickListener;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.adapter.CommentsListAdapter;
import com.sugan.qianwei.seeyouseeworld.application.MyApplication;
import com.sugan.qianwei.seeyouseeworld.bean.CommentItem;
import com.sugan.qianwei.seeyouseeworld.bean.ForumContentItem;
import com.sugan.qianwei.seeyouseeworld.event.ForumListChangeEvent;
import com.sugan.qianwei.seeyouseeworld.util.Constants;
import com.sugan.qianwei.seeyouseeworld.util.DialogFactoryUtil;
import com.sugan.qianwei.seeyouseeworld.util.SharedPreferenceUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import de.hdodenhof.circleimageview.CircleImageView;

public class DynamicDetailActivity extends Activity implements View.OnClickListener {

    private static final String TAG = "qianwei";
    private CircleImageView dynamicdetail_forum_avatar;
    private TextView dynamicdetail_forum_name;
    private TextView dynamicdetail_forum_content;
    private ImageView dynamicdetail_forum_cover;
    private TextView dynamicdetail_forum_praise;
    private TextView dynamicdetail_forum_praiseNumber;
    private ImageView dynamicdetail_forum_comment;
    private TextView dynamicdetail_forum_commentNumber;
    private Button dynamicdetail_forum_commentsShowButton;
    private SwipeRefreshLayout refreshLayout;

    private ForumContentItem dynamicdetail;
    private CommentsListAdapter commentsListAdapter;
    private ArrayList<CommentItem> commentItemArrayList;
    private ListView listView;
    private View comment_listviewlayout;
    private PopupWindow popupWindow;
    private SwipeRefreshLayout refreshLayout_1;
    private InputMethodManager manager;
    private int clickedposition = -1;
    private EditText dynamicdetail_commentinput;
    private Button dynamicdetail_send;
    private ImageView dynamicdetail_delete_operate;
    private String login_userid;
    //是否评论或点赞 用于判断是否post首页更新event
    private boolean hasDynamicDetailChanged = false;
    //判断动态是否被删除
    private boolean dynamicIsDeleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_detail);
        findViews();
        initInputMethodManage();
        initViews();
        initData();
        initListeners();
        initDeleteImageView();
        updateCommentDetail(dynamicdetail.getId());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        int position = getIntent().getIntExtra("selectedforumposition", -1);
        if (position >= 0 && hasDynamicDetailChanged) {
            //刷新首页
            EventBus.getDefault().post(new ForumListChangeEvent(dynamicdetail, position, dynamicIsDeleted));
        }
    }

    private void initDeleteImageView() {
        if (login_userid.equals(String.valueOf(dynamicdetail.getUserid()))) {
            dynamicdetail_delete_operate.setVisibility(View.VISIBLE);
        } else {
            dynamicdetail_delete_operate.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getCurrentFocus() != null && getCurrentFocus().getWindowToken() != null) {
                manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        return false;
    }

    private void initViews() {
        comment_listviewlayout = LayoutInflater.from(this).inflate(R.layout.commentdetail_layout, null);
        listView = (ListView) comment_listviewlayout.findViewById(R.id.lv_forumdetail_commentslistview);
        refreshLayout_1 = (SwipeRefreshLayout) comment_listviewlayout.findViewById(R.id.commentlayout_forumdetail_refreshlayout);
        refreshLayout_1.setColorSchemeResources(R.color.mediumseagreen, R.color.grassgreen);
        commentItemArrayList = new ArrayList<>();
        commentsListAdapter = new CommentsListAdapter(this, commentItemArrayList);
        listView.setAdapter(commentsListAdapter);
        dynamicdetail_commentinput = (EditText) comment_listviewlayout.findViewById(R.id.et_dynamicdetail_commentinput);
        dynamicdetail_send = (Button) comment_listviewlayout.findViewById(R.id.bt_dynamicdetail_send);
    }

    private void initListeners() {
        refreshLayout.setOnRefreshListener(refreshListener);
        refreshLayout_1.setOnRefreshListener(refreshListener);
        dynamicdetail_forum_avatar.setOnClickListener(this);
        dynamicdetail_forum_praise.setOnClickListener(this);
        dynamicdetail_forum_comment.setOnClickListener(this);
        dynamicdetail_delete_operate.setOnClickListener(this);
        listView.setOnItemClickListener(commentItemClickListener);
        listView.setOnScrollListener(scrollListener);
        dynamicdetail_commentinput.addTextChangedListener(textWatcher);
        //软键盘回车监听
        dynamicdetail_commentinput.setOnEditorActionListener(onEditorActionListener);
    }

    private void initData() {
        login_userid = SharedPreferenceUtil.getFromCache(getApplicationContext(), "userinfo", "userid");

        dynamicdetail = (ForumContentItem) getIntent().getSerializableExtra("dynamicdetail");
        if (dynamicdetail != null) {
            ImageLoader.getInstance().displayImage(dynamicdetail.getAvatar(), dynamicdetail_forum_avatar, new DisplayImageOptions.Builder()
                    .cacheOnDisk(true)
                    .cacheInMemory(true)
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .showImageOnLoading(R.drawable.default_myavatar)
                    .showImageOnFail(R.drawable.default_myavatar).build());
            dynamicdetail_forum_name.setText(dynamicdetail.getName());
            dynamicdetail_forum_content.setText(dynamicdetail.getIntroduction());
            dynamicdetail_forum_commentNumber.setText(String.valueOf(dynamicdetail.getComments_num()));
//            int width = PhoneSizeMeasureUtil.getDisplayMetrics(this).widthPixels;
//            int height = PhoneSizeMeasureUtil.getDisplayMetrics(this).heightPixels;
//            ImageSize imageSize = new ImageSize(width, height);
            ImageLoader.getInstance().displayImage(dynamicdetail.getImgurl(), dynamicdetail_forum_cover);
            if (dynamicdetail.getIspraised() == 0) {
                dynamicdetail_forum_praise.setBackgroundResource(R.drawable.praise_button_unselect);
            } else {
                dynamicdetail_forum_praise.setBackgroundResource(R.drawable.praise_button_select);
            }
            dynamicdetail_forum_praiseNumber.setText(String.valueOf(dynamicdetail.getPraisenumber()));
        }
    }

    private void findViews() {
        dynamicdetail_forum_avatar = (CircleImageView) findViewById(R.id.dynamicdetail_forum_avatar);
        dynamicdetail_forum_name = (TextView) findViewById(R.id.dynamicdetail_forum_name);
        dynamicdetail_forum_content = (TextView) findViewById(R.id.dynamicdetail_forum_content);
        dynamicdetail_forum_cover = (ImageView) findViewById(R.id.dynamicdetail_forum_cover);
        dynamicdetail_forum_praise = (TextView) findViewById(R.id.iv_forumdetail_praise);
        dynamicdetail_delete_operate = (ImageView) findViewById(R.id.dynamicdetail_delete_operate);
        dynamicdetail_forum_praiseNumber = (TextView) findViewById(R.id.tv_forumdetail_praisenumber);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.dynamicdetail_refreshlayout);
        refreshLayout.setColorSchemeResources(R.color.mediumseagreen, R.color.grassgreen);
        dynamicdetail_forum_commentNumber = (TextView) findViewById(R.id.tv_forumdetail_commentnumber);
        dynamicdetail_forum_comment = (ImageView) findViewById(R.id.iv_forumdetail_comment);
        dynamicdetail_forum_commentsShowButton = (Button) findViewById(R.id.show_commentsShowButton);
    }

    private void initInputMethodManage() {
        manager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public void dynamicdetail_back(View view) {
        onBackPressed();
    }

    /**
     * 点赞请求
     *
     * @param tv_praise
     * @param tv_praiseNumber
     * @param dynamicId
     */
    private void praiseRequest(final TextView tv_praise, final TextView tv_praiseNumber, final int dynamicId) {
        final String praiseUrl = Constants.MAIN_URL + "addpraise";
        RequestParams params = new RequestParams();
        String userId = SharedPreferenceUtil.getFromCache(getApplicationContext(), "userinfo", "userid");
        if (!"".equals(userId)) {
            params.put("userid", userId);
        } else {
            DialogFactoryUtil.createAlterView(this, "需要登录后才可以点赞", null, "取消", new String[]{"立即登录"}, new OnItemClickListener() {
                @Override
                public void onItemClick(Object o, int position) {
                    if (position == 0) {
                        Intent loginIntent = new Intent(DynamicDetailActivity.this, LoginActivity.class);
                        startActivity(loginIntent);
                    }
                }
            }).show();
            return;
        }
        params.put("dynamicid", dynamicId);
        Log.d(TAG, "praiseRequest: " + praiseUrl);
        ((MyApplication) getApplication()).getClient().post(getApplicationContext(), praiseUrl,
                params, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            JSONObject json = new JSONObject(new String(responseBody));
                            String code = json.getString("status_code");
                            int praiseCount = json.getInt("praiseNumber");
                            int praiseState = json.getInt("ispraised");
                            if ("0".equals(code)) {
                                if (praiseState == 1) {
                                    tv_praiseNumber.setText(String.valueOf(praiseCount));
                                    tv_praise.setBackgroundResource(R.drawable.praise_button_select);
                                } else {
                                    tv_praiseNumber.setText(String.valueOf(praiseCount));
                                    tv_praise.setBackgroundResource(R.drawable.praise_button_unselect);
                                }
                            }
                            //刷新点赞数和点赞按钮状态
                            dynamicdetail.setPraisenumber(praiseCount);
                            dynamicdetail.setIspraised(praiseState);
                            hasDynamicDetailChanged = true;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(DynamicDetailActivity.this, "点赞失败", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    /**
     * 更新评论详情
     *
     * @param dynamicId
     */
    private void updateCommentDetail(int dynamicId) {
        String url = Constants.MAIN_URL + "getCommentDetails";
        RequestParams params = new RequestParams();
        params.put("dynamic_id", dynamicId);
        ((MyApplication) getApplication()).getClient().get(getApplicationContext(), url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (commentItemArrayList != null && !commentItemArrayList.isEmpty()) {
                    commentItemArrayList.clear();
                }
                Log.d(TAG, "onSuccess: updateCommentDetail");
                try {
                    JSONObject json = new JSONObject(new String(responseBody));
                    if ("0".equals(json.getString("status_code"))) {
                        dynamicdetail_forum_commentsShowButton.setText("查看评论");
                        dynamicdetail_forum_commentsShowButton.setTextColor(getResources().getColor(R.color.white));
                        dynamicdetail_forum_commentsShowButton.setBackgroundResource(R.color.mediumseagreen);
                        dynamicdetail_forum_commentsShowButton.setClickable(true);
                        JSONArray array = json.getJSONArray("data");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject data = array.getJSONObject(i);
                            int comment_id = data.getInt("id");
                            int dynamic_id = data.getInt("dynamic_id");
                            int publisher_id = data.getInt("user_id");
                            int responder_id = data.getInt("replyto");
                            String publisher_name = data.getString("name");
                            String responder_name = data.getString("replytoname");
                            String content = data.getString("content");
                            String publisher_avatar = data.getString("avatar");
                            String comment_time = data.getString("created_at");
                            commentItemArrayList.add(new CommentItem(comment_id, dynamic_id, publisher_id,
                                    publisher_name, responder_id, responder_name, content, publisher_avatar, comment_time));
                        }

                    } else if ("10025".equals(json.getString("status_code"))) { //动态已经不存在
                        Toast.makeText(DynamicDetailActivity.this, "该动态不存在或者已被作者删除", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), "获取信息失败", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    Log.d(TAG, "onSuccess: 解析错误");
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, "onFailure: updateCommentDetail");
                dynamicdetail_forum_commentsShowButton.setText("评论加载失败,可下拉重试");
                dynamicdetail_forum_commentsShowButton.setClickable(false);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (refreshLayout.isRefreshing()) {
                    refreshLayout.setRefreshing(false);
                }
                if (refreshLayout_1.isRefreshing()) {
                    refreshLayout_1.setRefreshing(false);
                }
                commentsListAdapter.setCommentList(commentItemArrayList);
                commentsListAdapter.notifyDataSetChanged();
            }
        });
    }

    private void jumpToUserPageActivity() {
        //跳转到用户个人信息界面
        Intent intent = new Intent(DynamicDetailActivity.this, UserPageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("name", dynamicdetail.getName());
        bundle.putString("avatar", dynamicdetail.getImgurl());
        bundle.putInt("userid", dynamicdetail.getUserid());
        intent.putExtra("userinfo", bundle);
        startActivity(intent);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.dynamicdetail_forum_avatar:
                jumpToUserPageActivity();
                break;
            case R.id.iv_forumdetail_praise:
                praiseRequest(dynamicdetail_forum_praise, dynamicdetail_forum_praiseNumber, dynamicdetail.getId());
                break;
            case R.id.iv_forumdetail_comment:
                showshowCommentDetailPopupWindow(comment_listviewlayout);
                break;
            case R.id.dynamicdetail_delete_operate:
                DialogFactoryUtil.createAlterView(DynamicDetailActivity.this, "确定删除动态吗？", null, "取消", new String[]{"确定"}, new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int p) {
                        if (p == 0) {
                            deleteForum();
                        }
                    }
                }).show();
                break;
        }
    }

    public void showCommentDetail(View view) {
        if (commentItemArrayList != null && commentItemArrayList.isEmpty()) {
            updateCommentDetail(dynamicdetail.getId());
        }
        showshowCommentDetailPopupWindow(comment_listviewlayout);
    }

    /**
     * 删除动态
     */
    private void deleteForum() {
        String deleteUrl = Constants.MAIN_URL + "del_dynamic";
        RequestParams params = new RequestParams();
        params.put("id", dynamicdetail.getId());
        ((MyApplication) getApplication()).getClient().post(getApplicationContext(), deleteUrl, params,
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        if (responseBody != null) {
                            Log.d(TAG, "onSuccess: " + new String(responseBody));
                            try {
                                JSONObject json = new JSONObject(new String(responseBody));
                                String status_code = json.getString("status_code");
                                if ("0".equals(status_code)) {
                                    Toast.makeText(DynamicDetailActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                    dynamicIsDeleted = true;
                                    hasDynamicDetailChanged = true;
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d(TAG, "onFailure: " + new String(responseBody));
                    }
                });
    }

    /**
     * 隐藏评论列表
     *
     * @param view
     */
    public void hide_comments(View view) {
        if (getCurrentFocus() != null && view.getWindowToken() != null) {
            manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        if (popupWindow != null && popupWindow.isShowing()) {
            popupWindow.dismiss();
        }
    }

    /**
     * 展示评论列表
     *
     * @param contentView
     */
    private void showshowCommentDetailPopupWindow(View contentView) {
        popupWindow = new PopupWindow(this);
        popupWindow.setContentView(contentView);
        popupWindow.setAnimationStyle(R.style.show_comments_anim);
        // 设置弹出窗体的宽和高
        popupWindow.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        // 设置弹出窗体可点击
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAtLocation(findViewById(R.id.activity_dynamic_detail), Gravity.BOTTOM, 0, 0);
    }

    /**
     * 发送评论
     * user_id（发评论人的id）
     * dynamic_id（被评论动态的id）
     * content（发的内容）
     * replyto（被回复人的id，默认是0）
     *
     * @param position
     */
    private void sendComment(final int position, final String content) {
        String url = Constants.MAIN_URL + "addComment";
        RequestParams params = new RequestParams();
        final String userName = SharedPreferenceUtil.getFromCache(getApplicationContext(), "userinfo", "name");
        final String userId = SharedPreferenceUtil.getFromCache(getApplicationContext(), "userinfo", "userid");
        final String userAvatar = SharedPreferenceUtil.getFromCache(getApplicationContext(), "userinfo", "avatar_url");

        params.put("user_id", userId);
        params.put("dynamic_id", dynamicdetail.getId());
        params.put("content", content);
        if (position >= 0) {
            CommentItem commentItem = commentItemArrayList.get(position);
            params.put("replyto", commentItem.getPublisher_id());
        }
        ((MyApplication) getApplication()).getClient().post(getApplicationContext(), url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                dynamicdetail_send.setClickable(false);
                dynamicdetail_commentinput.setHint("");
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.d(TAG, "onSuccess: " + ((responseBody == null) ? "null" : new String(responseBody)));
                Toast.makeText(DynamicDetailActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                commentItemArrayList.add(new CommentItem(0, dynamicdetail.getId(), Integer.valueOf(userId), userName,
                        (position < 0) ? 0 : commentItemArrayList.get(position).getPublisher_id(),
                        (position < 0) ? "" : commentItemArrayList.get(position).getPublisher_name(),
                        content, userAvatar, "现在"));
                commentsListAdapter.notifyDataSetChanged();
                //更新评论数
                listView.setSelection(commentsListAdapter.getCount());
                dynamicdetail_forum_commentNumber.setText(String.valueOf(commentItemArrayList.size()));
                dynamicdetail.setComments_num(commentItemArrayList.size());
                hasDynamicDetailChanged = true;
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.d(TAG, "onFailure: " + ((responseBody == null) ? "null" : new String(responseBody)));
                Toast.makeText(DynamicDetailActivity.this, "评论发送失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFinish() {
                super.onFinish();
                dynamicdetail_send.setClickable(true);
                dynamicdetail_commentinput.setText("");
            }
        });
    }

    /**
     * EditText获取焦点并显示软键盘
     */
    public static void showSoftInputFromWindow(Activity activity, EditText editText) {
        editText.requestFocus();
        //打开软键盘
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 滑动listview时隐藏软键盘
     */
    private AbsListView.OnScrollListener scrollListener = new AbsListView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            Log.d(TAG, "onScrollStateChanged: " + scrollState);
            if (scrollState == SCROLL_STATE_TOUCH_SCROLL) {
                if (getCurrentFocus() != null && listView.getWindowToken() != null) {
                    manager.hideSoftInputFromWindow(listView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    dynamicdetail_commentinput.clearFocus();
                    dynamicdetail_commentinput.setHint("评论");
                    clickedposition = -1;
                }
            }
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        }
    };

    private SwipeRefreshLayout.OnRefreshListener refreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            updateCommentDetail(dynamicdetail.getId());
        }
    };

    private AdapterView.OnItemClickListener commentItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            clickedposition = position;
            String messagePublisherName = commentItemArrayList.get(position).getPublisher_name();
            dynamicdetail_commentinput.setHint("回复：" + messagePublisherName);
            showSoftInputFromWindow(DynamicDetailActivity.this, dynamicdetail_commentinput);
        }
    };

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            if (s == null || s.toString().isEmpty()) {
                dynamicdetail_send.setBackgroundResource(R.drawable.sendcomment_button_notclickable);
                dynamicdetail_send.setTextColor(getResources().getColor(R.color.black));
                dynamicdetail_send.setClickable(false);
            } else {
                dynamicdetail_send.setBackgroundResource(R.drawable.sendcomment_button_clickable);
                dynamicdetail_send.setTextColor(getResources().getColor(R.color.white));
                dynamicdetail_send.setClickable(true);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_SEND
                    || actionId == EditorInfo.IME_ACTION_DONE
                    || (event != null && KeyEvent.KEYCODE_ENTER == event.getKeyCode() && KeyEvent.ACTION_DOWN == event.getAction())) {
                //处理软键盘回车 发送事件
                dynamicdetail_send.performClick();
            }
            return true;
        }
    };

    public void send(View view) {
        if (getCurrentFocus() != null && view.getWindowToken() != null) {
            Log.d(TAG, "onScrollStateChanged: b");
            manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        String userId = SharedPreferenceUtil.getFromCache(getApplicationContext(), "userinfo", "userid");
        if (userId == null || "".equals(userId)) {
            Toast.makeText(getApplicationContext(), "登录后才可以发表评论", Toast.LENGTH_SHORT).show();
            Intent loginIntent = new Intent(DynamicDetailActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            return;
        }
        if (dynamicdetail_commentinput != null && dynamicdetail_commentinput.getText().toString().trim().equals("")) {
            Toast.makeText(getApplicationContext(), "评论不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (dynamicdetail_commentinput != null) {
            sendComment(clickedposition, dynamicdetail_commentinput.getText().toString());
        }
    }

    public void display_bigimage(View view) {
        Intent intent = new Intent(DynamicDetailActivity.this, DisplayBigImageActivity.class);
        intent.putExtra("imageurl", dynamicdetail.getImgurl());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this, view, "big_image").toBundle());
        } else {
            startActivity(intent);
        }
    }
}
