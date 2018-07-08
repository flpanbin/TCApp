package com.tc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.pb.myapplication.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tc.adapter.HistoryKeyAdapter;
import com.tc.adapter.TcTargetAdapter;
import com.tc.application.App;
import com.tc.bean.Code;
import com.tc.bean.ResponseResult;
import com.tc.conf.Config;
import com.tc.model.TcTarget;
import com.tc.utils.SPUtil;
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

@ContentView(R.layout.activity_search_tc_target)
public class SearchTcTargetActivity extends BaseActivity implements View.OnClickListener {
    @ViewInject(R.id.lv_history)
    private ListView lv_history;
    @ViewInject(R.id.lv_search_result)
    private ListView lv_search_result;
    @ViewInject(R.id.ll_history)
    private LinearLayout ll_history;

    @ViewInject(R.id.edt_search)
    private EditText edt_search;

    @ViewInject(R.id.iv_text_clear)
    private ImageView iv_text_clear;

    @ViewInject(R.id.tv_clear)
    private TextView tv_clear;
    @ViewInject(R.id.tv_cancel)
    private TextView tv_cancel;

    private TcTargetAdapter searchResultAdapter;
    private HistoryKeyAdapter historyKeysAdapter;
    public static final String HISTORY_KEY = "history_search_key";
    private List<String> historyKeysList;
    private static final String DELIMITER = ",";
    private List<TcTarget> searchResults;
    @ViewInject(R.id.rl_content_null)
    private RelativeLayout rl_content_null;

//    private String typeId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        init();
    }

    private void init() {
//        typeId = getIntent().getStringExtra("typeId");
        tv_clear.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
        iv_text_clear.setOnClickListener(this);
        searchResults = new ArrayList<>();
        searchResultAdapter = new TcTargetAdapter(SearchTcTargetActivity.this, searchResults);
        lv_search_result.setAdapter(searchResultAdapter);

        edt_search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String searchKey = edt_search.getText().toString();
                    saveHistorySearchKeys(searchKey);
                    getData(searchKey);
                    return true;
                }
                return false;
            }
        });
        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s == null || s.length() == 0) {
                    iv_text_clear.setVisibility(View.GONE);
                    rl_content_null.setVisibility(View.GONE);
                    lv_search_result.setVisibility(View.GONE);
                    //编辑框无输入时，显示历史搜索词
                    getHistorySearchKeys();
                    showHistorySearchKeys();
                } else {
                    iv_text_clear.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        historyKeysList = new ArrayList<>();

        historyKeysAdapter = new HistoryKeyAdapter(this, historyKeysList);
        lv_history.setAdapter(historyKeysAdapter);
        lv_history.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                edt_search.setText(historyKeysList.get(i));
                edt_search.setSelection(historyKeysList.get(i).length());
                ll_history.setVisibility(View.GONE);
                getData(edt_search.getText().toString());
            }
        });
        getHistorySearchKeys();
        showHistorySearchKeys();

        lv_search_result.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(SearchTcTargetActivity.this, TcContentAboutTargetActivity.class);
                intent.putExtra("targetId", searchResults.get(position).getTargetId());
                intent.putExtra("targetName", searchResults.get(position).getTargetName());
                intent.putExtra("typeId",searchResults.get(position).getUpTypeId());
                intent.putExtra("typeName",searchResults.get(position).getUpTypeName());
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_clear:
                clear();
                break;
            case R.id.iv_text_clear:
                edt_search.setText("");
                break;
            case R.id.tv_cancel:
                finish();
        }
    }

    private void showHistorySearchKeys() {
        if (historyKeysList.isEmpty()) {
            ll_history.setVisibility(View.GONE);
            tv_clear.setVisibility(View.GONE);
        } else {
            ll_history.setVisibility(View.VISIBLE);
            tv_clear.setVisibility(View.VISIBLE);
        }

    }

    private void clear() {
        SPUtil.remove(this, HISTORY_KEY);
        historyKeysList.clear();
        historyKeysAdapter.notifyDataSetChanged();
        ll_history.setVisibility(View.GONE);
    }

    private void getData(String searchKey) {
        if (searchKey.isEmpty()) {
            return;
        }
        final ProgressDialog progressDialog = Windows.loading(this);
//        String strUrl = "";
//        RequestParams params;
//        if (StringUtil.isEmpty(typeId)) {
//            params = new RequestParams(Config.SERVER_API_URL + "common/searchTarget");
//        } else {
//            params = new RequestParams(Config.SERVER_API_URL + "common/searchTargetInType");
//            params.addQueryStringParameter("typeId", typeId);
//        }
        RequestParams params = new RequestParams(Config.SERVER_API_URL + "common/searchTarget");
        params.addQueryStringParameter("text", searchKey);
        params.addQueryStringParameter("schoolId", App.SCHOOL_ID);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                Type jsonType = new TypeToken<ResponseResult<List<TcTarget>>>() {
                }.getType();
                ResponseResult<List<TcTarget>> response = gson.fromJson(result, jsonType);
                if (response.getCode() == Code.SUCCESS) {
                    searchResults.clear();
                    List<TcTarget> contentList = response.getData();
                    searchResults.addAll(contentList);
                    searchResultAdapter.notifyDataSetChanged();
                    ll_history.setVisibility(View.GONE);
                    lv_search_result.setVisibility(View.VISIBLE);
                    if (searchResults.isEmpty()) {
                        rl_content_null.setVisibility(View.VISIBLE);
                    } else {
                        rl_content_null.setVisibility(View.GONE);
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

    private void saveData(ResponseResult<List<TcTarget>> response) {
        List<TcTarget> contentList = response.getData();
        searchResultAdapter = new TcTargetAdapter(this, contentList);
        lv_search_result.setAdapter(searchResultAdapter);
    }


    private void getHistorySearchKeys() {
        historyKeysList.clear();
        String values = (String) SPUtil.get(this, HISTORY_KEY, "");
        List<String> tempList = new ArrayList<>();
        if (values.equals("")) return;
        String[] keys = values.split(DELIMITER);
        for (int i = 0; i < keys.length; i++) {
            historyKeysList.add(keys[i]);
        }
        historyKeysAdapter.notifyDataSetChanged();
    }

    private void saveHistorySearchKeys(String key) {
        String values = (String) SPUtil.get(this, HISTORY_KEY, "");
        SPUtil.put(this, HISTORY_KEY, key + DELIMITER + values);
    }

}
