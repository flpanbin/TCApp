package com.tc.fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.pb.myapplication.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tc.activity.WebActivity;
import com.tc.adapter.ActicityAdapter;
import com.tc.bean.Code;
import com.tc.bean.ResponseResult;
import com.tc.data.CommonData;

import org.xutils.common.Callback;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by PB on 2017/7/25.
 */
@ContentView(R.layout.fragment_activity)
public class ActivityFragment extends Fragment {

    Activity activity;
    @ViewInject(R.id.listview)
    ListView listView;

    ActicityAdapter adapter;
    List<com.tc.model.Activity> datas;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
    }

    private void init() {

        datas = new ArrayList<com.tc.model.Activity>();
        adapter = new ActicityAdapter(activity, datas);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(activity, WebActivity.class);
                intent.putExtra("title", datas.get(position).getActivityTitle());
                intent.putExtra("url", datas.get(position).getActivityUrl());
                startActivity(intent);
            }
        });
        getData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = x.view().inject(this, inflater, container);
        init();
        return view;
    }

    private void getData() {
        x.http().get(CommonData.getActivities(), new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                Type jsonType = new TypeToken<ResponseResult<List<com.tc.model.Activity>>>() {
                }.getType();
                ResponseResult<List<com.tc.model.Activity>> responseResult = gson.fromJson(result, jsonType);
                if (responseResult.getCode() == Code.SUCCESS) {
                    datas.addAll(responseResult.getData());
                    adapter.notifyDataSetChanged();
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

            }
        });
    }
}
