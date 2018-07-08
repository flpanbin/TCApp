package com.tc.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.pb.myapplication.R;
import com.tc.view.CommonDialog;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PB on 2017/6/18.
 */

@ContentView(R.layout.activity_tc_list_type_selected)
public class TclistTypeSelectedActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.iv_select)
    private ImageView iv_select;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        init();
    }

    private void init() {
        iv_select.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_select:
                createelectTypeDialog();
                break;
        }
    }

    List<String> typeDatas = new ArrayList<>();
    CommonDialog typeSelectDialog;

    private void createelectTypeDialog() {
        getTypeData();

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_select_type, null);
        ListView listView = (ListView) view.findViewById(R.id.lv_type);
        ArrayAdapter adapter = new ArrayAdapter(this,R.layout.item_type_list,R.id.tv_type,typeDatas);
        listView.setAdapter(adapter);
        typeSelectDialog = new CommonDialog(this,view,true);
        typeSelectDialog.show();

    }

    private void getTypeData() {
        for (int i = 0; i < 50; i++) {
            typeDatas.add("类型" + i);
        }
    }
}
