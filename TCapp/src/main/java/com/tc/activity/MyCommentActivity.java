package com.tc.activity;

import android.content.Intent;
import android.os.Bundle;
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
import com.tc.adapter.MyCommentAdapter;
import com.tc.application.App;
import com.tc.bean.Code;
import com.tc.bean.ResponseResult;
import com.tc.conf.Config;
import com.tc.data.Constant;
import com.tc.data.TcData;
import com.tc.model.MyComment;
import com.tc.utils.CommonUtil;
import com.tc.utils.Report;
import com.tc.view.CommonDialog;
import com.tc.view.HeaderHolder;
import com.tc.view.ProgressDialog;
import com.tc.view.Windows;

import org.w3c.dom.Text;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@ContentView(R.layout.activity_common_list)
public class MyCommentActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.listview)
    private ListView listView;
    @ViewInject(R.id.refreshLayout)
    SmartRefreshLayout smartRefreshLayout;

    @ViewInject(R.id.layout_empty)
    private View layout_empty;
    @ViewInject(R.id.layout_list)
    private View layout_list;

    private MyCommentAdapter myCommentAdapter;
    private List<MyComment> commentDatas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(R.color.main_color);
        x.view().inject(this);
        init();
    }


    MyComment myComment;

    private void init() {
        new HeaderHolder().init(this, "我的回复").hideRightText();
        commentDatas = new ArrayList<>();
        myCommentAdapter = new MyCommentAdapter(this, commentDatas);
        listView.setAdapter(myCommentAdapter);
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
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                myComment = commentDatas.get(position);
                funSelectDialog.show();
            }
        });
        getContentData(true);
        createSelectFunDialog();
        reportTypeSelectDialog = Windows.createSelectReportTypeDialog(this, this);
    }

    CommonDialog reportTypeSelectDialog;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_source:
                funSelectDialog.dismiss();
                Intent intent = new Intent(this, TcContentDetailActivity.class);
                intent.putExtra("contentId", myComment.getTcContent().getContentId());
                startActivity(intent);
                break;
            case R.id.tv_report:
                reportTypeSelectDialog.show();
                funSelectDialog.dismiss();
                break;
            case R.id.tv_delete:
                TcData.deleteComment(this, myComment.getCommentId(), myComment.getTcContent().getContentId());
                commentDatas.remove(myComment);
                myCommentAdapter.notifyDataSetChanged();
                funSelectDialog.dismiss();
                break;
            case R.id.tv_divulge_privacy:
            case R.id.tv_personal_attack:
            case R.id.tv_obscenity:
            case R.id.tv_adv:
            case R.id.tv_false_information:
            case R.id.tv_llegal_information:
            case R.id.tv_other:
                reportTypeSelectDialog.dismiss();
                Report.report(this, v, myComment.getCommentId(), myComment.getTcContent().getContentId());
                break;
        }
    }

    int pageNum = 1;
    int totalNum = 10;

    private void getContentData(final boolean refresh) {
        final ProgressDialog progressDialog = Windows.loading(this);
        pageNum = refresh ? 1 : pageNum;
        Log.i("pageNum", pageNum + "");
        RequestParams params = new RequestParams(Config.SERVER_API_URL + "user/getCommentByUserId");
        params.addQueryStringParameter("token", App.ACCESS_TOKEN);
        params.addQueryStringParameter("userId", App.USER_ID);
        params.addParameter("pageNum", pageNum);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.i("sucess", result);
                Gson gson = new Gson();
                Type jsonType = new TypeToken<ResponseResult<List<MyComment>>>() {
                }.getType();
                ResponseResult<List<MyComment>> responseResult = gson.fromJson(result, jsonType);
                if (responseResult.getCode() == Code.SUCCESS) {
                    List<MyComment> contentList = responseResult.getData();
                    if (refresh) {
                        commentDatas.clear();
                        smartRefreshLayout.setLoadmoreFinished(false);
                        smartRefreshLayout.finishRefresh();
                        //如果当前是第一页，则获取下一页数据
                        if (pageNum == 1)
                            pageNum++;
                    } else {
                        pageNum++;
                        smartRefreshLayout.finishLoadmore(0);
                    }
                    commentDatas.addAll(contentList);
                    myCommentAdapter.notifyDataSetChanged();

                    if (commentDatas.size() == 0) {
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
                CommonUtil.toast(MyCommentActivity.this, Constant.NETWORK_ERROR);
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

    private CommonDialog funSelectDialog;


    private TextView tv_report;
    private TextView tv_delete;
    private TextView tv_source;

    private void createSelectFunDialog() {
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_select_function_comment, null);
        view.findViewById(R.id.tv_reply).setVisibility(View.GONE);
        view.findViewById(R.id.tv_reply_anonymous).setVisibility(View.GONE);
        view.findViewById(R.id.divider_reply_anonymous).setVisibility(View.GONE);

        tv_report = (TextView) view.findViewById(R.id.tv_report);
        tv_delete = (TextView) view.findViewById(R.id.tv_delete);
        tv_source = (TextView) view.findViewById(R.id.tv_source);
        view.findViewById(R.id.ll_divider).setVisibility(View.VISIBLE);
        view.findViewById(R.id.tv_source).setVisibility(View.VISIBLE);
        tv_source.setOnClickListener(this);
        tv_report.setOnClickListener(this);
        tv_delete.setOnClickListener(this);
        funSelectDialog = new CommonDialog(this, view, 0.8, ViewGroup.LayoutParams.WRAP_CONTENT, false);
    }

}
