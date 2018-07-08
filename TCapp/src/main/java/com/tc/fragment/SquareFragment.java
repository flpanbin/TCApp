package com.tc.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.example.pb.myapplication.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.tc.activity.ContentReleaseActivity;
import com.tc.activity.SearchTcTargetActivity;
import com.tc.activity.TcContentDetailActivity;
import com.tc.adapter.TCContentAdapter;
import com.tc.adapter.TCTypeAdapter;
import com.tc.adapter.holder.TcContentItemHolder;
import com.tc.application.App;
import com.tc.bean.Code;
import com.tc.bean.ResponseResult;
import com.tc.model.TCContent;
import com.tc.model.TcType;
import com.tc.conf.Config;
import com.tc.utils.CommonUtil;
import com.tc.data.Constant;
import com.tc.view.ProgressDialog;
import com.tc.view.Windows;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import bakerj.backgrounddarkpopupwindow.BackgroundDarkPopupWindow;

/**
 * Created by PB on 2017/7/25.
 */

@ContentView(R.layout.fragment_square)
public class SquareFragment extends Fragment implements View.OnClickListener, AdapterView.OnItemClickListener {

    private TCTypeAdapter typeAdapter;
    private TCContentAdapter tcContentAdapter;
    private List<TCContent> contentDatas;
    private List<TcType> typeDatas;
    private GridView gv_type;
    @ViewInject(R.id.listview)
    private ListView listView;
    private LinearLayout ll_search;

    @ViewInject(R.id.layout_list)
    private View layout_list;
    @ViewInject(R.id.layout_empty)
    private View layout_empty;

    View headerView;

    @ViewInject(R.id.iv_release)
    ImageView iv_release;
    @ViewInject(R.id.refreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    @ViewInject(R.id.tv_type)
    TextView tv_type;
    @ViewInject(R.id.ll_type)
    LinearLayout ll_type;
    @ViewInject(R.id.rl_head)
    RelativeLayout rl_head;
    @ViewInject(R.id.iv_indicator)
    ImageView iv_indicator;
    Activity activity;
    @ViewInject(R.id.iv_search)
    ImageView iv_search;

    String typeId = "002";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        init();
        getTypeData();
        getContentData(true);

    }

    private void init() {
        initPopupWindow();
        contentDatas = new ArrayList<>();
        tcContentAdapter = new TCContentAdapter(getActivity(), contentDatas);
        typeDatas = new ArrayList<>();
        typeDatas.add(new TcType("001", "热门"));
        typeDatas.add(new TcType("002", "全部"));

        typeAdapter = new TCTypeAdapter(activity, typeDatas);
        gv_type.setAdapter(typeAdapter);
        registBroadCast();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = x.view().inject(this, inflater, container);
        initView();
        return view;

    }

    //    PopupWindow popupWindow;
    View popupView;
    LinearLayout popupLayout;

    BackgroundDarkPopupWindow popupWindow;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void showPopupWindow() {
        popupLayout.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.popupwindow_tc_type_translate_in));
        View root = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_square, null);
        popupWindow.setDarkStyle(-1);
        popupWindow.setDarkColor(Color.parseColor("#99333333"));
        popupWindow.resetDarkPosition();
        popupWindow.darkBelow(rl_head);
        popupWindow.showAsDropDown(rl_head, rl_head.getRight() / 2, 0);
    }

    private void initPopupWindow() {
        popupView = LayoutInflater.from(getActivity()).inflate(R.layout.popupwindow_select_type, null);
        popupWindow = new BackgroundDarkPopupWindow(popupView, WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setContentView(popupView);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setFocusable(true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupLayout = (LinearLayout) popupView.findViewById(R.id.layout_popup);
        gv_type = (GridView) popupView.findViewById(R.id.gv_type);
        gv_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tv_type.setText(typeDatas.get(position).getTypeName());
                typeId = typeDatas.get(position).getTypeId();
                showFirstItem = true;
                getContentData(true);
                popupWindow.dismiss();
            }
        });
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                indicatorRotate(180, 360);
            }
        });
    }

    private void initView() {
        initListView();
        iv_release.setOnClickListener(this);
        ll_search.setOnClickListener(this);
        ll_type.setOnClickListener(this);
        iv_search.setOnClickListener(this);
        tv_type.setText("全部");
        smartRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getContentData(false);

            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getContentData(true);

            }
        });
    }

    private void initListView() {
        headerView = (View) LayoutInflater.from(activity).inflate(R.layout.target_search_layout, null);
        headerView.setLayoutParams(new AbsListView.LayoutParams(AbsListView.LayoutParams.MATCH_PARENT, 120));
        ll_search = (LinearLayout) headerView.findViewById(R.id.ll_search);
        // listView.addHeaderView(headerView, null, true);
        listView.setAdapter(tcContentAdapter);
        listView.setOnItemClickListener(this);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                Log.i("SquareFrag--->onScroll:", totalItemCount + "");
                //当没有可加载的内容，关闭加载更多
                if (totalItemCount >= totalNum) {//totalItemCount-header
                    smartRefreshLayout.setLoadmoreFinished(true);
                }
            }
        });

    }


    /**
     * 获取吐槽类型
     */
    private void getTypeData() {
        final ProgressDialog progressDialog = Windows.loading(getContext());
        RequestParams params = new RequestParams(Config.SERVER_API_URL + "common/getTcTypeInfo");
        params.addQueryStringParameter("schoolId", App.SCHOOL_ID);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                Type jsonType = new TypeToken<ResponseResult<List<TcType>>>() {
                }.getType();
                ResponseResult<List<TcType>> response = gson.fromJson(result, jsonType);
                if (response.getCode() == Code.SUCCESS) {
                    List<TcType> types = response.getData();
                    if (types != null && types.size() != 0) {
                        typeDatas.addAll(types);
                        typeAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                CommonUtil.toast(activity, Constant.NETWORK_ERROR);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
                progressDialog.dismiss();
            }
        });

    }

    //是否显示listview的第一个item
    boolean showFirstItem;

    int pageNum = 1;
    int totalNum = 10;

    private void getContentData(final boolean refresh) {
//        final ProgressDialog progressDialog = Windows.loading(getContext());
        pageNum = refresh ? 1 : pageNum;
        Log.i("pageNum", pageNum + "");
        RequestParams params = new RequestParams(Config.SERVER_API_URL + "user/getTcContentList");
        params.addQueryStringParameter("token", App.ACCESS_TOKEN);
        params.addQueryStringParameter("userId", App.USER_ID);
        params.addQueryStringParameter("typeId", typeId);
        params.addQueryStringParameter("schoolId", App.SCHOOL_ID);
        params.addParameter("pageNum", pageNum);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("sucess", result);
                Gson gson = new Gson();
                Type jsonType = new TypeToken<ResponseResult<List<TCContent>>>() {
                }.getType();
                ResponseResult<List<TCContent>> responseResult = gson.fromJson(result, jsonType);
                if (responseResult.getCode() == Code.SUCCESS) {
                    List<TCContent> contentList = responseResult.getData();
                    if (refresh) {
                        contentDatas.clear();
                        smartRefreshLayout.setLoadmoreFinished(false);
                        ;
                        smartRefreshLayout.finishRefresh();

                        //如果当前是第一页，则获取下一页数据
                        if (pageNum == 1)
                            pageNum++;
                    } else {
                        pageNum++;
                        smartRefreshLayout.finishLoadmore(0);
                    }
                    contentDatas.addAll(contentList);
                    tcContentAdapter.notifyDataSetChanged();

                    if (contentDatas.size() == 0) {
                        layout_list.setVisibility(View.GONE);
                        layout_empty.setVisibility(View.VISIBLE);
                    } else {
                        layout_list.setVisibility(View.VISIBLE);
                        layout_empty.setVisibility(View.GONE);
                    }
                    try {
                        totalNum = Integer.parseInt(responseResult.getMessage());
                        Log.i("SquareFrag--->totalNum:", totalNum + "");
                        if (refresh) {
                            listView.setSelection(0);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("error", ex.toString());
                CommonUtil.toast(activity, Constant.NETWORK_ERROR);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {
//                progressDialog.dismiss();
            }
        });


    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = null;
        intent = new Intent(activity, TcContentDetailActivity.class);
        intent.putExtra("content", contentDatas.get(position));
        startActivity(intent);
    }

    public static final int RElEASE_CONTENT = 101;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.iv_release:
                intent = new Intent(activity, ContentReleaseActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_search:
                intent = new Intent(activity, SearchTcTargetActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_type:
                indicatorRotate(0, 180);
                showPopupWindow();
                break;
            case R.id.iv_search:
                intent = new Intent(activity, SearchTcTargetActivity.class);
                startActivity(intent);
                break;
        }
    }


    private void indicatorRotate(int from, int end) {
        LinearInterpolator interpolator = new LinearInterpolator();
        RotateAnimation rotateAnimation = new RotateAnimation(from, end, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setFillAfter(true);
        rotateAnimation.setInterpolator(interpolator);
        rotateAnimation.setDuration(500);
        iv_indicator.startAnimation(rotateAnimation);
    }



    public static void sendBroadCast(int action,Context context,String contentId) {
        Intent intent = new Intent();
        intent.putExtra("contentId", contentId);
        intent.putExtra("action", action);
        String className = ((Activity) context).getComponentName().getClassName();
//        Log.i("className", className);
        intent.putExtra("className", className);
        intent.setAction(TcContentItemHolder.ACTION);
        context.sendBroadcast(intent);
    }


    UpdateBroadCastReceiver updateBroadCastReceiver;

    public void registBroadCast() {
        updateBroadCastReceiver = new UpdateBroadCastReceiver();
        IntentFilter filter = new IntentFilter(TcContentItemHolder.ACTION);
        getActivity().registerReceiver(updateBroadCastReceiver, filter);
    }

    /**
     * 更新界面
     */
    public class UpdateBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int action = intent.getIntExtra("action", 0);
            String contentId = intent.getStringExtra("contentId");
            String className = intent.getStringExtra("className");
            //如果不是详情界面发送的广播，则不处理
            if (!className.equals(TcContentDetailActivity.class.getName()))
                return;
            TCContent tcContent = findContentById(contentId);
            if (tcContent == null)
                return;
            switch (action) {
                case TcContentItemHolder.UP:
                    tcContent.setLikeCount(tcContent.getLikeCount() + 1);
                    tcContent.setLikeId("id");
                    break;
                case TcContentItemHolder.CANCEL_UP:
                    int count = tcContent.getLikeCount() - 1;
                    count = count >= 0 ? count : 0;
                    tcContent.setLikeCount(count);
                    tcContent.setLikeId("");
                    break;
                case TcContentItemHolder.COLLECT:
                    tcContent.setCollectId("id");
                    break;
                case TcContentItemHolder.CANCEL_COLLECT:
                    tcContent.setCollectId("");
                    break;
                case TcContentItemHolder.DELETE_CONTENT:
                    contentDatas.remove(tcContent);
                    break;
                case TcContentItemHolder.DELETE_COMMENT:
                    int commentCount = tcContent.getCommentCount() - 1;
                    commentCount = commentCount >= 0 ? commentCount : 0;
                    tcContent.setCommentCount(commentCount);
                    break;
                case TcContentItemHolder.COMMENT:
                    tcContent.setCommentCount(tcContent.getCommentCount()+1);
            }
            tcContentAdapter.notifyDataSetChanged();
        }
    }

    private TCContent findContentById(String contentId) {
        for (TCContent tcContent : contentDatas) {
            if (tcContent.getContentId().equals(contentId))
                return tcContent;
        }
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(updateBroadCastReceiver);
    }

    @Override
    public void onResume() {
        super.onResume();

        if (tcContentAdapter.getCount() == 0) {
            getContentData(true);
        }
    }
}
