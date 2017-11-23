package com.sugan.qianwei.seeyouseeworld.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.adapter.MyEssayAdapter;
import com.sugan.qianwei.seeyouseeworld.application.MyApplication;
import com.sugan.qianwei.seeyouseeworld.bean.ForumContentItem;
import com.sugan.qianwei.seeyouseeworld.util.SharedPreferenceUtil;
import com.sugan.qianwei.seeyouseeworld.views.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

/**
 * Created By Qianwei
 */
public class MyEssayFragment extends Fragment implements XListView.IXListViewListener {

    private static final String TAG = "qianwei";

    private String firstPageRequestUrl = "http://www.agrising.cn:8080/blog/public/index.php/api/getMyDynamics";
    private String nextPageRequestUrl;
    private MyEssayAdapter adapter;
//    private TextView publishWritings;
    private XListView xListView;

    private ArrayList<ForumContentItem> itemContents;
    private Bundle bundle;

    public MyEssayFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemContents = new ArrayList<>();
        Log.d(TAG, "onCreate: "+(bundle == null));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_myessay, container, false);
        findViews(view);
        //从UserPageActivity才能获取
        bundle = getArguments();
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        adapter = new MyEssayAdapter(getActivity());
        xListView.setAdapter(adapter);
        xListView.setOnScrollListener(adapter);
        initXListView(xListView);
        xListView.setOnItemClickListener(itemClickListener);
//        xListView.setOnScrollListener(new PauseOnScrollListener(ImageLoader.getInstance(), false, true));
        xListView.autoRefresh();
        /*publishWritings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = SharedPreferenceUtil.getFromCache(getActivity(), "userinfo", "userid");
                if (!"".equals(userId)) {
                    startActivity(new Intent(getActivity(), WritingsActivity.class));
                } else {
                    DialogFactoryUtil.createAlterView(getActivity(), "需要登录后才可以发动态", null, "取消", new String[]{"立即登录"}, new OnItemClickListener() {
                        @Override
                        public void onItemClick(Object o, int position) {
                            if (position == 0) {
                                Intent loginIntent = new Intent(getActivity(), LoginActivity.class);
                                startActivity(loginIntent);
                            }
                        }
                    }).show();
                }
            }
        });*/
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void getDynamicsRequest(final String getDynamicsUrl, String myid) throws JSONException {
        RequestParams params = new RequestParams();
        if (!"".equals(myid)) {
            params.put("myid", myid);
        }
        if (bundle != null){
            params.put("userid", bundle.getInt("userid"));
        }
        ((MyApplication) getActivity().getApplication()).getClient().post(getActivity(), getDynamicsUrl,
                null, params, null, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        Log.d(TAG, "onSuccess: getDynamicsRequest success");
                        parseResponse(getDynamicsUrl, new String(responseBody));
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Log.d(TAG, "onFailure: getDynamicsRequest fail.");
                        if (firstPageRequestUrl.equals(getDynamicsUrl)) {
                            xListView.stopRefresh();
                        } else if (nextPageRequestUrl == null || nextPageRequestUrl.equals(getDynamicsUrl)) {
                            xListView.stopLoadMore();
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if (!itemContents.isEmpty()) {
                            adapter.setList(itemContents);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
    }

    private void findViews(View root) {
        xListView = (XListView) root.findViewById(R.id.xlistview);
//        publishWritings = (TextView) root.findViewById(R.id.publishWritings);
    }

    private void initXListView(XListView lv) {
        lv.setPullLoadEnable(true);
        lv.setPullRefreshEnable(true);
        lv.setAutoLoadEnable(true);
        lv.setXListViewListener(this);
    }

    private void parseResponse(String getDynamicsUrl, String responseString) {
        try {
            if (firstPageRequestUrl.equals(getDynamicsUrl) && itemContents != null && !itemContents.isEmpty()) {
                itemContents.clear();
            }
            JSONObject response = new JSONObject(responseString);
            JSONArray perPageData = response.getJSONArray("data");
            int itemCount = perPageData.length();
            for (int i = 0; i < itemCount; i++) {
                JSONObject eachDynamicData = perPageData.getJSONObject(i);
                String publisher_name = eachDynamicData.getString("name");
                String publisher_avatar = eachDynamicData.getString("avatar");
                int dynamic_id = eachDynamicData.getInt("id");
                int userid = eachDynamicData.getInt("userid");
                String dynamic_imgurl = eachDynamicData.getString("imgurl");
                String dynamic_introduction = eachDynamicData.getString("introduction");
                int dynamic_praiseNum = eachDynamicData.getInt("praisenumber");
                int praiseState = eachDynamicData.getInt("ispraised");
                int comment_number = eachDynamicData.getInt("comments_num");
                String group_name = eachDynamicData.getString("group_name");
                int group_id = eachDynamicData.getInt("group_id");
                itemContents.add(new ForumContentItem(dynamic_id, userid, publisher_name, publisher_avatar,
                        dynamic_imgurl, dynamic_introduction, dynamic_praiseNum,
                        praiseState, comment_number, group_name, group_id));
            }
            if (firstPageRequestUrl.equals(getDynamicsUrl)) {
                xListView.stopRefresh();
            } else {
                xListView.stopLoadMore();
            }
            //更新下一页url
            nextPageRequestUrl = response.getString("next_page_url");

        } catch (JSONException e) {
            Log.d(TAG, "onResponse: Method getDynamicsRequest() response parse failed");
            e.printStackTrace();
        }
    }


    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Log.d(TAG, "onItemClick: " + position);
        }
    };

    @Override
    public void onRefresh() {
        try {
            String myId = SharedPreferenceUtil.getFromCache(getActivity().getApplicationContext(),
                    "userinfo", "userid");
            getDynamicsRequest(firstPageRequestUrl, myId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoadMore() {
        if (nextPageRequestUrl == null || "null".equals(nextPageRequestUrl)) {
            xListView.stopLoadMore();
            return;
        }
        try {
            String myId = SharedPreferenceUtil.getFromCache(getActivity().getApplicationContext(),
                    "userinfo", "userid");
            getDynamicsRequest(nextPageRequestUrl, myId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
