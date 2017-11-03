package com.sugan.qianwei.seeyouseeworld.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.alertview.OnItemClickListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.activity.GroupFragmentsContainerPageActivity;
import com.sugan.qianwei.seeyouseeworld.activity.FeedbackActivity;
import com.sugan.qianwei.seeyouseeworld.activity.LoginActivity;
import com.sugan.qianwei.seeyouseeworld.activity.PersonalDataActivity;
import com.sugan.qianwei.seeyouseeworld.activity.UserPageActivity;
import com.sugan.qianwei.seeyouseeworld.adapter.SettingListAdapter;
import com.sugan.qianwei.seeyouseeworld.bean.SettingsItem;
import com.sugan.qianwei.seeyouseeworld.util.DataCleanManager;
import com.sugan.qianwei.seeyouseeworld.util.DialogFactoryUtil;
import com.sugan.qianwei.seeyouseeworld.util.FileUtil;
import com.sugan.qianwei.seeyouseeworld.util.FinestWebViewUtil;
import com.sugan.qianwei.seeyouseeworld.util.SharedPreferenceUtil;
import com.sugan.qianwei.seeyouseeworld.views.SpecialListView;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int UPDATECACHESIZE = 1;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;


    private static final String aboutUsUrl = "https://cyouworld.github.io/";

    private static final String TAG = "qianwei";
    private CircleImageView my_avatar;
    private SpecialListView settingList_top;
    private SpecialListView settingList_center;
    private SpecialListView settingList_bottom;
    private SettingListAdapter settingListTopAdapter;

    private LinearLayout ll_settingslayout;
    private static ArrayList<SettingsItem> list_top;
    private TextView tv_name;
    private Button settings_userlogout;
    private String my_avatar_url;
    private String my_name;
    private String my_id;
    private String cacheSize;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        findViews(view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d(TAG, "onActivityCreated: ");
        initSettingsList();
        //初次计算缓存
        updateCacheSize();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
        my_avatar.setOnClickListener(this);
        settings_userlogout.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        updatePersonalAccountPage();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.d(TAG, "onHiddenChanged: " + hidden);
        //更新缓存值
        if (!hidden) {
            updateCacheSize();
        }
    }

    /**
     * 初始化设置list
     */
    private void initSettingsList() {
        list_top = new ArrayList<>();
        list_top.add(new SettingsItem("清空缓存", cacheSize));
        settingListTopAdapter = new SettingListAdapter(list_top, getActivity());
        settingList_top.setAdapter(settingListTopAdapter);
        settingList_top.setOnItemClickListener(settingsTopItemClickListener);

        ArrayList<SettingsItem> list_center = new ArrayList<>();
        list_center.add(new SettingsItem("意见与反馈"));
        list_center.add(new SettingsItem("关于我们"));
        SettingListAdapter settingListCenterAdapter = new SettingListAdapter(list_center, getActivity());
        settingList_center.setAdapter(settingListCenterAdapter);
        settingList_center.setOnItemClickListener(settingsCenterItemClickListener);

        ArrayList<SettingsItem> list_bottom = new ArrayList<>();
        list_bottom.add(new SettingsItem("兴趣小组"));
        list_bottom.add(new SettingsItem("修改个人资料"));
//        list_bottom.add(new SettingsItem("更多"));
        SettingListAdapter settingListBottomAdapter = new SettingListAdapter(list_bottom, getActivity());
        settingList_bottom.setAdapter(settingListBottomAdapter);
        settingList_bottom.setOnItemClickListener(settingsBottomItemClickListener);
    }

    private void findViews(View view) {
        my_avatar = (CircleImageView) view.findViewById(R.id.my_avatar);
        settingList_top = (SpecialListView) view.findViewById(R.id.settings_list_top);
        settingList_center = (SpecialListView) view.findViewById(R.id.settings_list_center);
        settingList_bottom = (SpecialListView) view.findViewById(R.id.settings_list_bottom);
        tv_name = (TextView) view.findViewById(R.id.my_name);
        settings_userlogout = (Button) view.findViewById(R.id.settings_userlogout);
        ll_settingslayout = (LinearLayout) view.findViewById(R.id.ll_settingslayout);
    }

    private void updateCacheSize() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    long cache = DataCleanManager.getFolderSize(FileUtil.getExternalCacheDirFile(getActivity().getApplicationContext()));
                    cacheSize = DataCleanManager.getFormatSize(cache);
                    handler.sendEmptyMessage(UPDATECACHESIZE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 更新个人信息
     */
    private void updatePersonalAccountPage() {
        my_name = SharedPreferenceUtil.getFromCache(getActivity().getApplicationContext(), "userinfo", "name");
        my_avatar_url = SharedPreferenceUtil.getFromCache(getActivity().getApplicationContext(), "userinfo", "avatar_url");
        my_id = SharedPreferenceUtil.getFromCache(getActivity().getApplicationContext(), "userinfo", "userid");
        if (my_name != null && !my_name.isEmpty()) {
            tv_name.setText(my_name);
        } else {
            tv_name.setText("未登录");
        }
        if (my_avatar_url != null && !my_avatar_url.isEmpty()) {
            loadAvatar(my_avatar_url);
        } else {
            loadAvatar("");
        }
        if (my_id == null || my_id.equals("")) {
            settingList_bottom.setVisibility(View.GONE);
            settings_userlogout.setVisibility(View.GONE);
        } else {
            settingList_bottom.setVisibility(View.VISIBLE);
            settings_userlogout.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 点击头像
     */
    private void avatarClickAction() {
        String token = SharedPreferenceUtil.getFromCache(getActivity().getApplicationContext(), "userinfo", "token");
        if (token != null && token.isEmpty()) {
            //登录
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        } else {
            //跳转到用户个人信息界面
            Intent intent = new Intent(getActivity(), UserPageActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("name", my_name);
            bundle.putString("avatar", my_avatar_url);
            bundle.putInt("userid", Integer.parseInt(my_id));
            intent.putExtra("userinfo", bundle);
            startActivity(intent);
        }
    }

    /**
     * 加载头像
     *
     * @param imageUrl
     */
    private void loadAvatar(String imageUrl) {
        //默认图片
        Bitmap defaultMyAvatar = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.default_myavatar);
        ImageLoader.getInstance().displayImage(imageUrl, my_avatar,
                new DisplayImageOptions.Builder()
                        .cacheInMemory(true)
                        .cacheOnDisk(true)
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .showImageOnLoading(R.drawable.default_myavatar)
                        .showImageOnFail(R.drawable.default_myavatar).build());
        if (defaultMyAvatar != null && !defaultMyAvatar.isRecycled()) {
            defaultMyAvatar.recycle();
        }
    }

    //缓存清理
    private void showCacheCleanBox() {
        DialogFactoryUtil.createActionSheet(getActivity(), "缓存", null,
                "取消", new String[]{"清理缓存"}, new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        switch (position) {
                            case 0:
                                //开启子线程执行缓存清理任务
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DataCleanManager.cleanExternalCache(getActivity().getApplicationContext());
                                    }
                                }).start();
                                list_top.get(0).setExplain("0KB");
                                settingListTopAdapter.notifyDataSetChanged();
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    //退出登录
    private void showLogoutBox() {
        DialogFactoryUtil.createActionSheet(getActivity(), "退出", null,
                "取消", new String[]{"确认退出"}, new OnItemClickListener() {
                    @Override
                    public void onItemClick(Object o, int position) {
                        switch (position) {
                            case 0:
                                SharedPreferenceUtil.clearFileCache(getActivity().getApplicationContext(), "userinfo");
                                updatePersonalAccountPage();
                                break;
                            default:
                                break;
                        }
                    }
                }).show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.my_avatar:
                avatarClickAction();
                break;
            case R.id.settings_userlogout:
                showLogoutBox();
                break;
            default:
                break;
        }
    }

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATECACHESIZE:
                    list_top.get(0).setExplain(cacheSize);
                    settingListTopAdapter.notifyDataSetChanged();
                    break;
            }
            return false;
        }
    });

    //防止handler导致内存泄露,静态内部类+指向外部类的弱引用
//    static class MyHandler extends Handler {
//
//        WeakReference<Activity> mWeakReference;
//
//        public MyHandler(Activity activity) {
//            mWeakReference = new WeakReference<>(activity);
//        }
//
//        @Override
//        public void handleMessage(Message msg) {
//            final Activity activity = mWeakReference.get();
//            if (activity != null) {
//                switch (msg.what) {
//                    case UPDATECACHESIZE:
//                        list_top.get(0).setExplain(cacheSize);
//                        settingListTopAdapter.notifyDataSetChanged();
//                        break;
//                }
//            }
//        }
//    }

    /**
     * 设置列表监听top
     */
    private AdapterView.OnItemClickListener settingsTopItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:  // 清空缓存
                    showCacheCleanBox();
                    break;
                default:
                    break;
            }
        }
    };
    /**
     * 设置列表监听center
     */
    private AdapterView.OnItemClickListener settingsCenterItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:  //我有话说
                    Intent fbIntent = new Intent(getActivity(), FeedbackActivity.class);
                    startActivity(fbIntent);
                    break;
                case 1:  //关于我们
                    FinestWebViewUtil.startFinestWebActivity(getActivity(), aboutUsUrl);
                    break;
            }
        }
    };
    /**
     * 设置列表监听bottom
     */
    private AdapterView.OnItemClickListener settingsBottomItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0: //兴趣小组
                    Intent groupsIntent = new Intent(getActivity(), GroupFragmentsContainerPageActivity.class);
                    startActivity(groupsIntent);
                    break;
                case 1: //个人资料
                    Intent dataIntent = new Intent(getActivity(), PersonalDataActivity.class);
                    startActivity(dataIntent);
                    break;
                case 2: //更多设置

                    break;
            }
        }
    };
}
