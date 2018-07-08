package com.tc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.pb.myapplication.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tc.adapter.TcTargetAdapter;
import com.tc.bean.Code;
import com.tc.bean.ResponseResult;
import com.tc.conf.Config;
import com.tc.model.TcTarget;
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
 * Created by PB on 2017/10/16.
 */
@ContentView(R.layout.activity_select_target)
public class SelectTargetActivity extends BaseActivity {

    @ViewInject(R.id.listview)
    private ListView listView;
    @ViewInject(R.id.ll_search)
    private LinearLayout ll_search;
    @ViewInject(R.id.tv_target)
    private EditText edt_target;
    private String typeId;
    List<TcTarget> targetData;
    TcTargetAdapter targetAdapter;
    List<TcTarget> listData;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(R.color.main_color);
        x.view().inject(this);
        init();
    }

    private void init() {
        new HeaderHolder().init(this, "选择对象");
        typeId = getIntent().getStringExtra("typeId");
        initList();
        ll_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(SelectTargetActivity.this, SearchTcTargetActivity.class);
//                intent.putExtra("typeId", typeId);
//                startActivity(intent);
            }
        });

        edt_target.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    listData.clear();
                    listData.addAll(targetData);
                    targetAdapter.notifyDataSetChanged();
                } else {
                    selectData(s.toString());
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void initList() {
        targetData = new ArrayList<>();
        listData = new ArrayList<>();
        targetAdapter = new TcTargetAdapter(this, listData);
        listView.setAdapter(targetAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent();
                intent.putExtra("targetId", listData.get(position).getTargetId());
                intent.putExtra("targetName", listData.get(position).getTargetName());
                setResult(0, intent);
                finish();
            }
        });
        getTargetData();
    }

    private void getTargetData() {
        final ProgressDialog progressDialog = Windows.loading(this);
        RequestParams params = new RequestParams(Config.SERVER_API_URL + "common/getTargetByType");
        params.addQueryStringParameter("schoolId", "1");
        params.addQueryStringParameter("typeId", typeId);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                Type type = new TypeToken<ResponseResult<List<TcTarget>>>() {
                }.getType();
                ResponseResult<List<TcTarget>> response = gson.fromJson(result, type);
                if (response.getCode() == Code.SUCCESS) {
                    List<TcTarget> targets = response.getData();
                    if (targets != null && targets.size() > 0) {
                        targetData.addAll(targets);
                        listData.addAll(targets);
                        targetAdapter.notifyDataSetChanged();
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

    private void selectData(String text) {
        listData.clear();
        for (TcTarget tcTarget : targetData) {
            if (tcTarget.getTargetName().contains(text)) {
                listData.add(tcTarget);
            }
        }
        targetAdapter.notifyDataSetChanged();
    }
}
