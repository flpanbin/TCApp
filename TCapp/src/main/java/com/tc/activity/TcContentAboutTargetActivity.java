package com.tc.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ImageView;
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
import com.tc.model.TCContent;
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

/**
 * Created by PB on 2017/10/18.
 */
@ContentView(R.layout.activity_tc_content_about_target)
public class TcContentAboutTargetActivity extends BaseActivity {
    @ViewInject(R.id.listview)
    ListView listView;
    @ViewInject(R.id.refreshLayout)
    SmartRefreshLayout smartRefreshLayout;
    String targetId;
    String targetName;
    String typeId;
    String typeName;

    @ViewInject(R.id.layout_empty)
    private View layout_empty;
    @ViewInject(R.id.layout_list)
    private View layout_list;
    @ViewInject(R.id.iv_release)
    private ImageView iv_release;

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

        getIntentData();
        new HeaderHolder().init(this, getIntent().getStringExtra("targetName"));
        contentDatas = new ArrayList<>();
        tcContentAdapter = new TCContentAdapter(this, contentDatas);
        tcContentAdapter.setTargetClickable(false);
        listView.setAdapter(tcContentAdapter);
        getContentData(true);

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
                Log.i("SquareFrag--->onScroll:", totalItemCount + "");
                //当没有可加载的内容，关闭加载更多
                if (totalItemCount >= totalNum) {//totalItemCount-header
                    smartRefreshLayout.setLoadmoreFinished(true);
                }
            }
        });

        iv_release.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(TcContentAboutTargetActivity.this, ContentReleaseActivity.class);
//                intent.putExtra("targetId", targetId);
//                intent.putExtra("targetName", targetName);
//                intent.putExtra("typeId",typeId);
//                intent.putExtra("typeName",typeName);
//                startActivity(intent);
                ContentReleaseActivity.startActivity(TcContentAboutTargetActivity.this, targetId, targetName, typeId, typeName);
            }
        });
    }

    private void getIntentData() {
        targetId = getIntent().getStringExtra("targetId");
        targetName = getIntent().getStringExtra("targetName");
        typeId = getIntent().getStringExtra("typeId");
        typeName = getIntent().getStringExtra("typeName");
    }

    public static void startActivity(Context context, TCContent content) {
        Intent intent = new Intent(context, TcContentAboutTargetActivity.class);
        intent.putExtra("targetId", content.getTcTargetId());
        intent.putExtra("targetName", content.getTargetName());
        intent.putExtra("typeId", content.getTcTypeId());
        intent.putExtra("typeName", content.getTypeName());
        context.startActivity(intent);
    }


    int pageNum = 1;
    int totalNum = 10;

    private void getContentData(final boolean refresh) {
        pageNum = refresh ? 1 : pageNum;
        final ProgressDialog progressDialog = Windows.loading(this);
        RequestParams params = new RequestParams(Config.SERVER_API_URL + "user/getTcContentListByTarget");
        params.addQueryStringParameter("token", App.ACCESS_TOKEN);
        params.addQueryStringParameter("userId", App.USER_ID);
        params.addQueryStringParameter("targetId", targetId);
        params.addQueryStringParameter("schoolId", App.SCHOOL_ID);
        params.addParameter("pageNum", pageNum);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
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
