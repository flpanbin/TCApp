package com.tc.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.pb.myapplication.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tc.application.App;
import com.tc.bean.Code;
import com.tc.bean.ResponseResult;
import com.tc.conf.Config;
import com.tc.data.Constant;
import com.tc.model.TcType;
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

/**
 * Created by PB on 2017/10/15.
 */

@ContentView(R.layout.activity_select_type)
public class SelectTypeActivity extends BaseActivity {

    @ViewInject(R.id.listview)
    private ListView listView;
    //    private List<TcType> typeDatas;
    private List<String> typeNames;
    private ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        init();
        initList();
    }

    private void init() {
        new HeaderHolder().init(this, "类型").hideRightText().setLeftBtn(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void initList() {
//        typeDatas = new ArrayList<>();
        typeNames = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(this, R.layout.item_list_select_type, R.id.tv_content, typeNames);
        listView.setAdapter(arrayAdapter);
        getTypeData();
    }

    /**
     * 获取吐槽类型
     */
    private void getTypeData() {
        final ProgressDialog progressDialog = Windows.loading(this);
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

                        for (TcType tcType : types) {
                            typeNames.add(tcType.getTypeName());
                        }
//                        typeDatas.addAll(types);
                        arrayAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                CommonUtil.toast(SelectTypeActivity.this, Constant.NETWORK_ERROR);
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
