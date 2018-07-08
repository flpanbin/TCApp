package com.tc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pb.myapplication.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.tc.adapter.ReceivedUpAdapter;
import com.tc.application.App;
import com.tc.bean.Code;
import com.tc.bean.ResponseResult;
import com.tc.conf.Config;
import com.tc.data.Constant;
import com.tc.data.TcData;
import com.tc.model.ReceivedComment;
import com.tc.model.ReceivedLike;
import com.tc.utils.CommonUtil;
import com.tc.view.CommonDialog;
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
 * Created by PB on 2017/12/5.
 */

@ContentView(R.layout.activity_common_list)
public class ReceivedLikeActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.listview)
    private ListView listView;
    @ViewInject(R.id.refreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    @ViewInject(R.id.layout_empty)
    private View layout_empty;
    @ViewInject(R.id.layout_list)
    private View layout_list;


    private ReceivedUpAdapter adapter;
    private List<ReceivedLike> datas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(R.color.main_color);
        x.view().inject(this);
        init();
    }

    private void init() {
        new HeaderHolder().init(this, "收到的赞").hideRightText();
        datas = new ArrayList<>();
        adapter = new ReceivedUpAdapter(this, datas);
        listView.setAdapter(adapter);
        smartRefreshLayout.setOnRefreshLoadmoreListener(new OnRefreshLoadmoreListener() {
            @Override
            public void onLoadmore(RefreshLayout refreshlayout) {
                getData(false);

            }

            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                getData(true);

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
        getData(true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                funSelectDialog.show();
                receivedLike = datas.get(position);
            }
        });
        createSelectFunDialog();
    }

    ReceivedLike receivedLike;
    private CommonDialog funSelectDialog;

    private TextView tv_source;

    private void createSelectFunDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_select_function_like, null);
        tv_source = (TextView) view.findViewById(R.id.tv_source);
        tv_source.setVisibility(View.VISIBLE);
        tv_source.setOnClickListener(this);
        funSelectDialog = new CommonDialog(this, view, 0.8, ViewGroup.LayoutParams.WRAP_CONTENT, false);
    }

    int pageNum = 1;
    int totalNum = 10;

    private void getData(final boolean refresh) {
        final ProgressDialog progressDialog = Windows.loading(this);
        pageNum = refresh ? 1 : pageNum;
        Log.i("pageNum", pageNum + "");
        x.http().get(TcData.getLikeInfo(pageNum), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("sucess", result);
                Gson gson = new Gson();
                Type jsonType = new TypeToken<ResponseResult<List<ReceivedLike>>>() {
                }.getType();
                ResponseResult<List<ReceivedLike>> responseResult = gson.fromJson(result, jsonType);
                if (responseResult.getCode() == Code.SUCCESS) {
                    List<ReceivedLike> tempData = responseResult.getData();
                    if (refresh) {
                        datas.clear();
                        smartRefreshLayout.setLoadmoreFinished(false);
                        smartRefreshLayout.finishRefresh();
                        //如果当前是第一页，则获取下一页数据
                        if (pageNum == 1)
                            pageNum++;
                    } else {
                        pageNum++;
                        smartRefreshLayout.finishLoadmore(0);
                    }
                    datas.addAll(tempData);
                    adapter.notifyDataSetChanged();

                    if (datas.size() == 0) {
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
                CommonUtil.toast(ReceivedLikeActivity.this, Constant.NETWORK_ERROR);
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_source) {
            funSelectDialog.dismiss();
            Intent intent = new Intent(this, TcContentDetailActivity.class);
            intent.putExtra("contentId", receivedLike.getTcContent().getContentId());
            startActivity(intent);
        }
    }
}
