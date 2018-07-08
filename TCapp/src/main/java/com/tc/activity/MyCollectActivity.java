package com.tc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.pb.myapplication.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.tc.adapter.TCContentAdapter;
import com.tc.application.App;
import com.tc.bean.Code;
import com.tc.bean.ResponseResult;
import com.tc.conf.Config;
import com.tc.data.Constant;
import com.tc.model.TCContent;
import com.tc.utils.CommonUtil;
import com.tc.view.HeaderHolder;
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

@ContentView(R.layout.activity_common_list)
public class MyCollectActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.listview)
    private ListView listView;
    @ViewInject(R.id.refreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    @ViewInject(R.id.layout_empty)
    private View layout_empty;
    @ViewInject(R.id.layout_list)
    private View layout_list;

    TCContent content;
    private TCContentAdapter tcContentAdapter;
    private List<TCContent> contentDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(R.color.main_color);
        x.view().inject(this);
        init();
    }

    private void init() {

        new HeaderHolder().init(this, "我的收藏").hideRightText();
        contentDatas = new ArrayList<>();
        tcContentAdapter = new TCContentAdapter(this, contentDatas);
        listView.setAdapter(tcContentAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MyCollectActivity.this, TcContentDetailActivity.class);
                intent.putExtra("content", contentDatas.get(position));
                startActivity(intent);
            }
        });
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
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //当没有可加载的内容，关闭加载更多
                if (totalItemCount >= totalNum) {//totalItemCount-header
                    smartRefreshLayout.setLoadmoreFinished(true);
                }
            }
        });
        getContentData(true);
    }


    @Override
    public void onClick(View v) {

    }

    int pageNum = 1;
    int totalNum = 10;

    private void getContentData(final boolean refresh) {
        final ProgressDialog progressDialog = Windows.loading(this);
        pageNum = refresh ? 1 : pageNum;
        Log.i("pageNum", pageNum + "");
        RequestParams params = new RequestParams(Config.SERVER_API_URL + "user/getCollectedTcContentList");
        params.addQueryStringParameter("token", App.ACCESS_TOKEN);
        params.addQueryStringParameter("userId", App.USER_ID);
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
                        layout_empty.setVisibility(View.VISIBLE);
                        layout_list.setVisibility(View.GONE);
                    } else {
                        layout_empty.setVisibility(View.GONE);
                        layout_list.setVisibility(View.VISIBLE);
                    }
                    try {
                        totalNum = Integer.parseInt(responseResult.getMessage());
                        Log.i("SquareFrag--->totalNum:", totalNum + "");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                Log.i("error", ex.toString());
                CommonUtil.toast(MyCollectActivity.this, Constant.NETWORK_ERROR);
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

}
