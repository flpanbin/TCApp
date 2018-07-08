package com.tc.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.pb.myapplication.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadmoreListener;
import com.tc.activity.ReceivedLikeActivity;
import com.tc.activity.TcContentDetailActivity;
import com.tc.adapter.ReceivedUpAdapter;
import com.tc.bean.Code;
import com.tc.bean.ResponseResult;
import com.tc.data.Constant;
import com.tc.data.TcData;
import com.tc.model.ReceivedLike;
import com.tc.utils.CommonUtil;
import com.tc.view.CommonDialog;
import com.tc.view.HeaderHolder;

import org.xutils.common.Callback;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by PB on 2018/1/25.
 */
@ContentView(R.layout.activity_common_list)
public class ReceivedLikeFragment extends Fragment implements View.OnClickListener {
    @ViewInject(R.id.listview)
    private ListView listView;
    @ViewInject(R.id.refreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    @ViewInject(R.id.layout_empty)
    private View layout_empty;
    @ViewInject(R.id.layout_list)
    private View layout_list;


    @ViewInject(R.id.header)
    private View header;
    private ReceivedUpAdapter adapter;
    private List<ReceivedLike> datas;
    Activity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this.getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = x.view().inject(this, inflater, container);
        init();
        return view;
    }

    private void init() {
        header.setVisibility(View.GONE);
        datas = new ArrayList<>();
        adapter = new ReceivedUpAdapter(activity, datas);
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
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_select_function_like, null);
        tv_source = (TextView) view.findViewById(R.id.tv_source);
        tv_source.setVisibility(View.VISIBLE);
        tv_source.setOnClickListener(this);
        funSelectDialog = new CommonDialog(activity, view, 0.8, ViewGroup.LayoutParams.WRAP_CONTENT, false);
    }

    int pageNum = 1;
    int totalNum = 10;

    private void getData(final boolean refresh) {
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
                CommonUtil.toast(activity, Constant.NETWORK_ERROR);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_source) {
            funSelectDialog.dismiss();
            Intent intent = new Intent(activity, TcContentDetailActivity.class);
            intent.putExtra("contentId", receivedLike.getTcContent().getContentId());
            startActivity(intent);
        }
    }
}
