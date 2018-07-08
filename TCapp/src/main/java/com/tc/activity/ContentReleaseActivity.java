package com.tc.activity;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.example.pb.myapplication.MainActivity;
import com.example.pb.myapplication.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tc.adapter.SelectedPicAdapter;
import com.tc.application.App;
import com.tc.bean.Code;
import com.tc.bean.Require;
import com.tc.bean.ResponseResult;
import com.tc.conf.Config;
import com.tc.model.TcType;
import com.tc.utils.ActivityCollector;
import com.tc.utils.CommonUtil;
import com.tc.data.Constant;
import com.tc.utils.ImageUtil;
import com.tc.utils.StringUtil;
import com.tc.view.CommonDialog;
import com.tc.view.HeaderHolder;
import com.tc.view.ProgressDialog;
import com.tc.view.Windows;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import com.yuyh.library.imgsel.ImageLoader;
import com.yuyh.library.imgsel.ImgSelActivity;
import com.yuyh.library.imgsel.ImgSelConfig;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by PB on 2017/6/18.
 */
@ContentView(R.layout.activity_release_cotent)
public class ContentReleaseActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.gv_picture)
    private GridView gv_picture;

    @ViewInject(R.id.rl_type)
    private RelativeLayout rl_type;
    @ViewInject(R.id.rl_target)
    private RelativeLayout rl_target;
    @ViewInject(R.id.tv_type)
    private TextView tv_type;
    @ViewInject(R.id.tv_target)
    private TextView tv_target;

    @ViewInject(R.id.iv_expression)
    private ImageView iv_expression;
    @ViewInject(R.id.iv_expression_pressed)
    private ImageView iv_expression_pressed;
    @ViewInject(R.id.checkbox)
    private CheckBox checkBox;


    @ViewInject(R.id.edt_content)
    private EmojiEditText edt_content;
    @ViewInject(R.id.rootview)
    private View rootView;


    @ViewInject(R.id.ll_layout)
    private LinearLayout ll_layout;

    String strContent;
    String targetId;
    String typeId = "1";

    /**
     * 选择照片
     */
    private static final int SELECT_PHOTO = 0;
    /**
     * 选择类型
     */
    private static final int SELECT_TARGET = 1;
    // int resultCode;

    List<String> pathList;
    SelectedPicAdapter adapter;

    String cachePath;

    private List<String> typeNames;
    //    private ArrayAdapter arrayAdapter;
    EmojiPopup emojiPopup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        x.view().inject(this);
        setStatusBarColor(R.color.main_color);
        init();

    }

    ProgressDialog progressDialog;

    private void init() {
        new HeaderHolder().init(this, "我要吐槽").setRightText("发送", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog = Windows.loading(ContentReleaseActivity.this);
                if (checkInfo()) {
                    releaseContent();
                } else {
                    progressDialog.dismiss();
                }
            }
        });

        rl_target.setOnClickListener(this);
        rl_type.setOnClickListener(this);
        iv_expression.setOnClickListener(this);
        iv_expression_pressed.setOnClickListener(this);
        edt_content.setOnClickListener(this);
        edt_content.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    iv_expression_pressed.setVisibility(View.GONE);
                    iv_expression.setVisibility(View.VISIBLE);
//                    emojiPopup.dismiss();
                }
            }
        });
        initGrideView();
        createSelectTypeDialog();
        cachePath = getExternalCacheDir().getAbsolutePath() + "/picture/";
        tv_type.setText("老师");
        getIntentData();
        emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(edt_content);
        // emojiPopup.dismiss();


        //em
        edt_content.setFocusable(true);
        edt_content.setFocusableInTouchMode(true);
        edt_content.requestFocus();
        InputMethodManager im = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
        im.showSoftInput(edt_content, 0);


    }

    private void getIntentData() {
        targetId = getIntent().getStringExtra("targetId");
        if (StringUtil.isEmpty(targetId))
            return;
        String targetName = getIntent().getStringExtra("targetName");
        typeId = getIntent().getStringExtra("typeId");
        String typeName = getIntent().getStringExtra("typeName");
        tv_target.setText(targetName);
        tv_type.setText(typeName);
    }

    public static void startActivity(Context context, String targetId, String targetName, String typeId, String typeName) {
        Intent intent = new Intent(context, ContentReleaseActivity.class);
        intent.putExtra("targetId", targetId);
        intent.putExtra("targetName", targetName);
        intent.putExtra("typeId", typeId);
        intent.putExtra("typeName", typeName);
        context.startActivity(intent);
    }


    private void initGrideView() {
        pathList = new ArrayList<>();
        adapter = new SelectedPicAdapter(this, pathList);
        gv_picture.setAdapter(adapter);
        gv_picture.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == pathList.size())
                    selectPicture();
            }
        });
    }

    private boolean checkInfo() {
        strContent = edt_content.getText().toString();
        if (!StringUtil.paramNull(this, new Require().put(strContent, "内容不能为空")))
            return false;
        if (StringUtil.isEmpty(typeId)) {
            CommonUtil.toast(this, "请选择一个类型");
            return false;
        }
        return true;
    }

    private int getAnonymous() {

//        else return 0;
        if (checkBox.isChecked())
            return 1;
        return 0;
    }

    //anonymous
    private void releaseContent() {
        RequestParams params = new RequestParams(Config.SERVER_API_URL + "user/releaseTcContent");
        params.addQueryStringParameter("token", App.ACCESS_TOKEN);
        params.addQueryStringParameter("userId", App.USER_ID);
        params.addQueryStringParameter("targetId", targetId);
        params.addQueryStringParameter("typeId", typeId);
        params.addQueryStringParameter("content", strContent);
        params.addQueryStringParameter("schoolId", App.SCHOOL_ID);
        params.addQueryStringParameter("anonymous", getAnonymous() + "");
        for (int i = 0; i < pathList.size(); i++) {
            String path = pathList.get(i);
            Bitmap bitmap;
            try {
                bitmap = ImageUtil.cmpScale(BitmapFactory.decodeFile(path));
                params.addBodyParameter("path" + i, new File(ImageUtil.saveImage(bitmap, cachePath)));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        params.setMultipart(true);
        x.http().post(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                Gson gson = new Gson();
                ResponseResult responseResult = gson.fromJson(result, ResponseResult.class);
                if (responseResult.getCode() == Code.SUCCESS) {
                    CommonUtil.toast(ContentReleaseActivity.this, "发布成功");
                    // setResult(resultCode);
                    startActivity(new Intent(ContentReleaseActivity.this, MainActivity.class));
                    ActivityCollector.finishAll();
                } else if (responseResult.getCode() == Code.PROHIBIT) {
                    CommonUtil.toast(ContentReleaseActivity.this, "您已被禁言");
                } else if (responseResult.getCode() == Code.ILLEGAL_CONTENT) {
                    CommonUtil.toast(ContentReleaseActivity.this, "您发布的信息包含敏感词汇，禁止发送");
                } else {
                    CommonUtil.toast(ContentReleaseActivity.this, "发布失败");
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                CommonUtil.toast(ContentReleaseActivity.this, Constant.NETWORK_ERROR);

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

    private ImageLoader loader = new ImageLoader() {
        @Override
        public void displayImage(Context context, String path, ImageView imageView) {
            Glide.with(context).load(path).into(imageView);
        }
    };


    private void selectPicture() {

        ImgSelConfig config = new ImgSelConfig.Builder(this, loader)
                .multiSelect(true)
                .rememberSelected(false)
                .statusBarColor(Color.parseColor("#3F51B5"))
                .maxNum(9 - pathList.size())
                .build();

        ImgSelActivity.startActivity(this, config, SELECT_PHOTO);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case SELECT_PHOTO:
                if (data != null)
                    showPhoto(data);
                break;
            case SELECT_TARGET:
                if (data != null) {
                    targetId = data.getStringExtra("targetId");
                    tv_target.setText(data.getStringExtra("targetName"));
                }
                CommonUtil.manageInputMethod(this);
                break;
        }
    }

    private void showPhoto(Intent data) {
        List<String> tempList = data.getStringArrayListExtra(ImgSelActivity.INTENT_RESULT);
        for (String path : tempList) {
            pathList.add(path);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_target:
                if (StringUtil.isEmpty(typeId)) {
                    CommonUtil.toast(this, "请先选择类型");
                    return;
                }

                Intent intent = new Intent(this, SelectTargetActivity.class);
                intent.putExtra("typeId", typeId);
                startActivityForResult(intent, SELECT_TARGET);
                break;
            case R.id.rl_type:
                typeSelectDialog.show();
                break;
            case R.id.iv_expression:
//                expressionViewPager.setVisibility(View.VISIBLE);
                iv_expression.setVisibility(View.GONE);
                iv_expression_pressed.setVisibility(View.VISIBLE);
                emojiPopup.toggle();
                //  CommonUtil.hideKeyBoard(this, edt_content.getWindowToken());
                break;
            case R.id.iv_expression_pressed:
//                expressionViewPager.setVisibility(View.GONE);
                iv_expression.setVisibility(View.VISIBLE);
                iv_expression_pressed.setVisibility(View.GONE);
                emojiPopup.dismiss();
                break;
            case R.id.edt_content:
//                expressionViewPager.setVisibility(View.GONE);
                iv_expression_pressed.setVisibility(View.GONE);
                iv_expression.setVisibility(View.VISIBLE);
                emojiPopup.dismiss();
                break;
        }
    }

    CommonDialog typeSelectDialog;
    ArrayAdapter typeAdapter;


    private void createSelectTypeDialog() {
        typeNames = new ArrayList<>();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_select_type, null);
        ListView listView = (ListView) view.findViewById(R.id.lv_type);
        typeAdapter = new ArrayAdapter(this, R.layout.item_type_list, R.id.tv_type, typeNames);
        listView.setAdapter(typeAdapter);
        typeSelectDialog = new CommonDialog(this, view, true);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                tv_type.setText(typeNames.get(position));
                typeId = typeDatas.get(position).getTypeId();
                typeSelectDialog.dismiss();
                targetId = "";
                tv_target.setText("");
            }
        });
        getTypeData();
    }

    List<TcType> typeDatas = new ArrayList<>();

    /**
     * 获取吐槽类型
     */
    private void getTypeData() {
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
                    typeDatas = response.getData();
                    if (typeDatas != null && typeDatas.size() != 0) {

                        for (TcType tcType : typeDatas) {
                            typeNames.add(tcType.getTypeName());
                        }
                        typeAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                CommonUtil.toast(ContentReleaseActivity.this, Constant.NETWORK_ERROR);
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