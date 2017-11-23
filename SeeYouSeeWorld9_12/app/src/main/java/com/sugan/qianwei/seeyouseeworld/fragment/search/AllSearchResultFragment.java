package com.sugan.qianwei.seeyouseeworld.fragment.search;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.sugan.qianwei.seeyouseeworld.R;
import com.sugan.qianwei.seeyouseeworld.activity.DynamicDetailActivity;
import com.sugan.qianwei.seeyouseeworld.activity.GroupDetailPageActivity;
import com.sugan.qianwei.seeyouseeworld.activity.UserPageActivity;
import com.sugan.qianwei.seeyouseeworld.adapter.search.DividerItemDecoration;
import com.sugan.qianwei.seeyouseeworld.adapter.search.OnItemClickListener;
import com.sugan.qianwei.seeyouseeworld.adapter.search.SearchResultDynamicsAdapter;
import com.sugan.qianwei.seeyouseeworld.adapter.search.SearchResultGroupsAdapter;
import com.sugan.qianwei.seeyouseeworld.adapter.search.SearchResultUsersAdapter;
import com.sugan.qianwei.seeyouseeworld.bean.ForumContentItem;
import com.sugan.qianwei.seeyouseeworld.bean.search.SearchResult;
import com.sugan.qianwei.seeyouseeworld.bean.search.group.GroupsData;
import com.sugan.qianwei.seeyouseeworld.bean.search.user.UsersData;
import com.sugan.qianwei.seeyouseeworld.util.RecycleViewConfig;

import org.greenrobot.eventbus.EventBus;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllSearchResultFragment extends Fragment implements View.OnClickListener {

    private RecyclerView usersRecyclerView;
    private RecyclerView groupsRecycleView;
    private RecyclerView dynamicsRecycleView;
    private RelativeLayout rl_moreuser;
    private RelativeLayout rl_moregroup;
    private RelativeLayout rl_moredynamic;
    private SearchResult result;

    public AllSearchResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_all_search_result, container, false);
        findViews(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            result = (SearchResult) bundle.getSerializable("result");
        }
        initListener();
        initUsersRecycleView();
    }

    private void findViews(View view) {
        usersRecyclerView = (RecyclerView) view.findViewById(R.id.users_list);
        groupsRecycleView = (RecyclerView) view.findViewById(R.id.groups_list);
        dynamicsRecycleView = (RecyclerView) view.findViewById(R.id.dynamics_list);
        rl_moreuser = view.findViewById(R.id.more_users);
        rl_moregroup = view.findViewById(R.id.more_groups);
        rl_moredynamic = view.findViewById(R.id.more_dynamics);
    }

    private void initUsersRecycleView() {

        //用户列表设置适配器
        usersRecyclerView.setLayoutManager(RecycleViewConfig.getHorizontalLinearLayoutManager(getActivity()));
        final SearchResultUsersAdapter usersAdapter = new SearchResultUsersAdapter(getActivity(), result.getUsers_data().getData());
        usersRecyclerView.setAdapter(usersAdapter);
        usersAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                jumpToUserPage(position);
            }
        });
        //小组列表设置适配器
        groupsRecycleView.setLayoutManager(RecycleViewConfig.getHorizontalLinearLayoutManager(getActivity()));
        SearchResultGroupsAdapter groupsAdapter = new SearchResultGroupsAdapter(getActivity(), result.getGroups_data().getData());
        groupsRecycleView.setAdapter(groupsAdapter);
        groupsAdapter.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                jumpToGroupDetailPage(position);
            }
        });
        //动态列表设置适配器,设置不可滑动,解决与ScrollView滑动冲突
        dynamicsRecycleView.setLayoutManager(RecycleViewConfig.getCustomVerticalLinearLayoutManager(getActivity(), false));
        SearchResultDynamicsAdapter dynamicsAdapter = new SearchResultDynamicsAdapter(getActivity(), result.getDynamics_data().getData());
        dynamicsRecycleView.setAdapter(dynamicsAdapter);
        //设置Item增加、移除动画
        dynamicsRecycleView.setItemAnimator(new DefaultItemAnimator());
//添加分割线
        dynamicsRecycleView.addItemDecoration(new DividerItemDecoration(
                getActivity(), DividerItemDecoration.VERTICAL_LIST));
        dynamicsAdapter.setOnItemClickListener(new com.bigkoo.alertview.OnItemClickListener() {
            @Override
            public void onItemClick(Object o, int position) {
                jumpToDynamicDetailPage(position);
            }
        });

    }

    private void initListener(){
        rl_moreuser.setOnClickListener(this);
        rl_moregroup.setOnClickListener(this);
        rl_moredynamic.setOnClickListener(this);
    }

    private void jumpToUserPage(int position) {
        //跳转到用户个人信息界面
        UsersData usersData = result.getUsers_data().getData().get(position);
        Intent intent = new Intent(getActivity(), UserPageActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("name", usersData.getName());
        bundle.putString("avatar", usersData.getAvatar());
        bundle.putInt("userid", usersData.getId());
        intent.putExtra("userinfo", bundle);
        getActivity().startActivity(intent);
    }

    private void jumpToGroupDetailPage(int position) {
        GroupsData groupsData = result.getGroups_data().getData().get(position);
        Intent groupPageIntent = new Intent(getActivity(), GroupDetailPageActivity.class);
        groupPageIntent.putExtra("group_name", groupsData.getName());
        groupPageIntent.putExtra("group_id", groupsData.getId());
        getActivity().startActivity(groupPageIntent);
    }

    private void jumpToDynamicDetailPage(int position){
        ForumContentItem forumContentItem = result.getDynamics_data().getData().get(position);
        Intent intent = new Intent(getActivity(), DynamicDetailActivity.class);
        intent.putExtra("dynamicdetail", forumContentItem);
        intent.putExtra("selectedforumposition", position);
        getActivity().startActivity(intent);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getActivity().startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(getActivity(), holder.cover, "big_image").toBundle());
//        } else {
//            getActivity().startActivity(intent);
//        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.more_users:
                EventBus.getDefault().post(new SwitchFragmentEvent(SwitchFragmentEvent.USER_FRAGMENT));
                break;
            case R.id.more_groups:
                EventBus.getDefault().post(new SwitchFragmentEvent(SwitchFragmentEvent.GROUP_FRAGMENT));
                break;
            case R.id.more_dynamics:
                EventBus.getDefault().post(new SwitchFragmentEvent(SwitchFragmentEvent.DYNAMIC_FRAGMENT));
                break;
        }
    }
}
