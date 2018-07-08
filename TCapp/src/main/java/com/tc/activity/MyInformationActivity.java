package com.tc.activity;


import android.content.Intent;

import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;

import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import android.view.Gravity;
import android.view.View;

import android.view.WindowManager;
import android.view.animation.AnimationUtils;

import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.example.pb.myapplication.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tc.application.App;
import com.tc.bean.Code;
import com.tc.bean.Require;
import com.tc.bean.ResponseResult;
import com.tc.conf.Config;
import com.tc.data.Constant;
import com.tc.data.UserPreference;
import com.tc.model.User;
import com.tc.utils.CommonUtil;
import com.tc.utils.DateUtil;
import com.tc.utils.NetWorkUtil;
import com.tc.utils.SPUtil;
import com.tc.utils.StringUtil;
import com.tc.utils.UIUtils;
import com.tc.view.ProgressDialog;
import com.tc.view.Windows;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.image.ImageOptions;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


@ContentView(R.layout.activity_my_information)
public class MyInformationActivity extends BaseActivity implements View.OnClickListener {

    @ViewInject(R.id.iv_avatar)
    private ImageView iv_avatar;
    //    @ViewInject(R.id.rl_avatar)
//    private RelativeLayout rl_avatar;
    @ViewInject(R.id.tv_right)
    private TextView tv_right;
    @ViewInject(R.id.tv_birthday)
    private TextView tv_birthday;
    @ViewInject(R.id.rl_birthday)
    private RelativeLayout rl_birthday;
    @ViewInject(R.id.rl_sex)
    private RelativeLayout rl_sex;
    @ViewInject(R.id.tv_sex)
    private TextView tv_sex;
    @ViewInject(R.id.rl_type)
    private RelativeLayout rl_type_select;
    @ViewInject(R.id.tv_type)
    private TextView tv_type;
    @ViewInject(R.id.rl_entrance_time)
    private RelativeLayout rl_get;
    @ViewInject(R.id.tv_entrance_time)
    private TextView tv_entrance_time;
    @ViewInject(R.id.edt_name)
    private EditText edt_name;

    @ViewInject(R.id.iv_back)
    private ImageView iv_back;

    @ViewInject(R.id.iv_modify)
    private ImageView iv_modify;
    private TimePickerView timePicker;
    private OptionsPickerView pvOptionsSex;
    private OptionsPickerView pvOptionsType;
    private ArrayList<String> optionsSexItems = new ArrayList<>();
    private ArrayList<String> optionsTypeItems = new ArrayList<>();

    private String nickName;
    private int sex;
    private String birthday;
    private String entranceTime;
    private int identityType;

    PopupWindow popupWindow;
    LinearLayout popupLayout;
    View popupView;
    String avatarCachePath;
    String imageName;

    boolean modify = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStatusBarColor(R.color.main_color);
        x.view().inject(this);
        init();
    }

    private void init() {
        judge();
        initItems();
        initTimePicker();
        showPickerView();
        iv_avatar.setOnClickListener(this);
        rl_birthday.setOnClickListener(this);
        rl_sex.setOnClickListener(this);
        rl_type_select.setOnClickListener(this);
        rl_get.setOnClickListener(this);
        iv_modify.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        tv_right.setOnClickListener(this);
        edt_name.setOnClickListener(this);

        initPopupWindow();
        avatarCachePath = getExternalCacheDir().getAbsolutePath() + "/picture/";
        makeAvatarDir();

    }

    public void initItems() {
        optionsSexItems.add(Constant.MAN);
        optionsSexItems.add(Constant.WOMAN);
        optionsTypeItems.add(Constant.STUDENT);
        optionsTypeItems.add(Constant.TEACHER);
        optionsTypeItems.add(Constant.LEAVE_SCHOOL);
    }

    @Override
    public void onClick(View v) {
        modify = true;
        if (v.getId() == R.id.rl_birthday && timePicker != null) {
            timePicker.show(tv_birthday, false);
        } else if (v.getId() == R.id.rl_entrance_time && timePicker != null) {
            timePicker.show(tv_entrance_time, false);
        } else if (v.getId() == R.id.rl_sex && pvOptionsSex != null) {
            pvOptionsSex.show();
        } else if (v.getId() == R.id.rl_type && pvOptionsType != null) {
            pvOptionsType.show();
        } else if (v.getId() == R.id.iv_avatar) {
            showPhotoWindow();
        } else if (v.getId() == R.id.tv_right) {
            save();
        } else if (v.getId() == R.id.tv_photograph) {
            takePhoto();
        } else if (v.getId() == R.id.tv_album) {
            pickPhoto();
        } else if (v.getId() == R.id.tv_left) {
            popupWindow.dismiss();
        } else if (v.getId() == R.id.iv_modify) {
            edtNickName();
        } else if (v.getId() == R.id.iv_back) {
//            onBackPressed();
            finish();
        } else if (v.getId() == R.id.edt_name) {
            edtNickName();
        }
    }

    private void edtNickName() {
        edt_name.setEnabled(true);
        edt_name.setSelection(edt_name.length());
        edt_name.setFocusable(true);
        edt_name.setFocusableInTouchMode(true);
        edt_name.requestFocus();
        CommonUtil.manageInputMethod(this);
    }

    public void showAvatar(String path) {
        ImageOptions imageOptions = new ImageOptions.Builder().setCircular(true).build();
        x.image().bind(iv_avatar, path, imageOptions);
    }

    private void initTimePicker() {
        Calendar selectedDate = Calendar.getInstance();
        Calendar startDate = Calendar.getInstance();
        startDate.set(1950, 0, 23);
        Calendar endDate = Calendar.getInstance();
        endDate.set(2040, 11, 28);
        timePicker = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                if (v == tv_birthday) {
                    birthday = getTime(date);
                    tv_birthday.setText(birthday);
                } else if (v == tv_entrance_time) {
                    entranceTime = getTime(date);
                    tv_entrance_time.setText(entranceTime);
                }
            }
        })
                .setLayoutRes(R.layout.pickerview_custom_time, new CustomListener() {

                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        ImageView ivCancel = (ImageView) v.findViewById(R.id.iv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                timePicker.returnData();
                                timePicker.dismiss();
                            }
                        });
                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                timePicker.dismiss();
                            }
                        });
                    }
                })
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("", "", "", "", "", "")
                .setDividerColor(Color.DKGRAY)
                .setContentSize(20)
                .setDate(selectedDate)
                .setRangDate(startDate, selectedDate)
                .setDecorView(null)
                .setBackgroundId(0x00000000)
                .setOutSideCancelable(false)
                .build();
        ;
        timePicker.setKeyBackCancelable(false);
    }

    private String getTime(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }


    private void showPickerView() {

        pvOptionsSex = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                sex = options1;
                tv_sex.setText(optionsSexItems.get(options1));
            }
        })
                .setTitleText("性别")
                .setDividerColor(getResources().getColor(R.color.main_color))
                .setTextColorCenter(getResources().getColor(R.color.main_color))
                .setTitleBgColor(getResources().getColor(R.color.main_color))
                .setTitleColor(Color.WHITE)
                .setCancelColor(Color.WHITE)
                .setSubmitColor(Color.WHITE)
                .setContentTextSize(18)
                .setOutSideCancelable(false)
                .build();
        pvOptionsType = new OptionsPickerView.Builder(this, new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                identityType = options1;
                tv_type.setText(optionsTypeItems.get(options1));
            }
        })
                .setTitleText("身份类别")
                .setDividerColor(getResources().getColor(R.color.main_color))
                .setTextColorCenter(getResources().getColor(R.color.main_color))
                .setTitleBgColor(getResources().getColor(R.color.main_color))
                .setTitleColor(Color.WHITE)
                .setCancelColor(Color.WHITE)
                .setSubmitColor(Color.WHITE)
                .setOutSideCancelable(false)
                .build();

        pvOptionsSex.setPicker(optionsSexItems);
        pvOptionsType.setPicker(optionsTypeItems);
    }

    private boolean checkInfo() {
        nickName = edt_name.getText().toString();
        birthday = tv_birthday.getText().toString();
        entranceTime = tv_entrance_time.getText().toString();

        return StringUtil.paramNull(this, new Require().put(nickName, "昵称不能为空噢!")
                .put(birthday, "请选择出生日期")
                .put(entranceTime, "请选择入校时间")
                .put(identityType, "请选择当前状态"));
    }

    private void save() {
        if (!checkInfo())
            return;
        RequestParams params = new RequestParams(Config.SERVER_API_URL + "user/modifyInfo");
        params.addParameter("sex", sex);
        params.addParameter("identityType", identityType);
        params.addQueryStringParameter("token", App.ACCESS_TOKEN);
        params.addQueryStringParameter("userId", App.USER_ID);
        params.addQueryStringParameter("school", App.SCHOOL_ID);
        params.addQueryStringParameter("nickName", nickName);
        params.addQueryStringParameter("birthday", birthday);
        params.addQueryStringParameter("entranceTime", entranceTime);

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                Type jsonType = new TypeToken<ResponseResult<User>>() {
                }.getType();
                ResponseResult<User> response = gson.fromJson(result, jsonType);
                if (response.getCode() == Code.SUCCESS) {
                    saveData(response);
                    CommonUtil.toast(MyInformationActivity.this, "保存成功");
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

    private void sendBroadCast() {
        Intent intent = new Intent();
        intent.setAction("update_userinfo");
        sendBroadcast(intent);
    }

    private void saveData(ResponseResult<User> response) {
        User user = response.getData();
        UserPreference.updateUserInfo(this, user);
        sendBroadCast();
    }

    public void getInformation() {
        final ProgressDialog progressDialog = Windows.loading(this);
        RequestParams params = new RequestParams(Config.SERVER_API_URL + "user/getUserInfo");
        params.addQueryStringParameter("token", App.ACCESS_TOKEN);
        params.addQueryStringParameter("userId", App.USER_ID);
        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Gson gson = new Gson();
                Type jsonType = new TypeToken<ResponseResult<User>>() {
                }.getType();
                ResponseResult<User> response = gson.fromJson(result, jsonType);
                if (response.getCode() == Code.SUCCESS) {
                    setUserInfo(response);
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

    private void judge() {
        if (NetWorkUtil.isNetworkConnected(this)) {
            getInformation();
        }
        getLocalInformation();
    }

    private void getLocalInformation() {
        switch ((int) SPUtil.get(this, UserPreference.USER_SEX, 0)) {
            case 0:
                tv_sex.setText("男");
                break;
            case 1:
                tv_sex.setText("女");
                break;
        }
        switch ((int) SPUtil.get(this, UserPreference.USER_IDENTITY_TYPE, 0)) {
            case 0:
                tv_type.setText("在校学生");
                break;
            case 1:
                tv_type.setText("教职工");
                break;
            case 2:
                tv_type.setText("已离校");
                break;
        }
        UIUtils.showAvatar(iv_avatar, (String) SPUtil.get(this, UserPreference.USER_AVATAR, ""));
        edt_name.setText((String) SPUtil.get(this, UserPreference.USER_NICK_NAME, ""));
        tv_entrance_time.setText((String) SPUtil.get(this, UserPreference.USER_ENTRANCE_TIME, ""));
        tv_birthday.setText((String) SPUtil.get(this, UserPreference.USER_BIRTHDAY, ""));
    }

    public void setUserInfo(ResponseResult<User> response) {
        User user = response.getData();
        if (user.getSex() == 0)
            tv_sex.setText(Constant.MAN);
        else if (user.getSex() == 1)
            tv_sex.setText(Constant.WOMAN);
        if (user.getIdentityType() == 0)
            tv_type.setText(Constant.STUDENT);
        else if (user.getIdentityType() == 1)
            tv_type.setText(Constant.TEACHER);
        else if (user.getIdentityType() == 2)
            tv_type.setText(Constant.LEAVE_SCHOOL);
        edt_name.setText(user.getNickName());
        tv_entrance_time.setText(user.getEntranceTime());
        tv_birthday.setText(user.getBirthday());
        identityType = user.getIdentityType();
        sex = user.getSex();
        UIUtils.showAvatar(iv_avatar,user.getAvatar());
        UserPreference.updateUserInfo(this, user);
    }

    private void initPopupWindow() {
        popupWindow = new PopupWindow(this);
        popupView = getLayoutInflater().inflate(R.layout.popupwindow_select_pic, null);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        popupWindow.setContentView(popupView);
        popupWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popupLayout = (LinearLayout) popupView.findViewById(R.id.layout_popup);
        popupView.findViewById(R.id.tv_album).setOnClickListener(this);
        popupView.findViewById(R.id.tv_photograph).setOnClickListener(this);
        popupView.findViewById(R.id.tv_left).setOnClickListener(this);

    }

    private void showPhotoWindow() {
        popupLayout.startAnimation(AnimationUtils.loadAnimation(this, R.anim.popupwindow_bottom_translate_in));
        View root = getLayoutInflater().inflate(R.layout.activity_my_information, null);
        popupWindow.showAtLocation(root, Gravity.END, 0, 0);
    }

    public static final int PICK_PHOTO = 1;

    public void pickPhoto() {
        imageName = getImgName();
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, null);
        pickPhotoIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(pickPhotoIntent, PICK_PHOTO);
        popupWindow.dismiss();
    }

    /**
     * 拍照 requestCode
     */
    public static final int TAKE_PHOTO = 2;

    public void takePhoto() {
        imageName = getImgName();
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, getAvatarUri());
        startActivityForResult(takePhotoIntent, TAKE_PHOTO);// 采用ForResult打开
        popupWindow.dismiss();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK)
            switch (requestCode) {
                case PICK_PHOTO:
                    cropPhoto(data.getData());
                    break;
                case TAKE_PHOTO:
                    cropPhoto(getAvatarUri());// 裁剪图片
                    break;
                case PHOTO_CROP:
                    showAvatar(avatarCachePath + imageName);
                    uploadAvatar(avatarCachePath + imageName);
                    break;
                default:
                    break;
            }
    }

    public static final int PHOTO_CROP = 3;

    public void cropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, getAvatarUri());
        intent.putExtra("outputFormat", "JPEG");
        startActivityForResult(intent, PHOTO_CROP);
    }

    private String getImgName() {
        return DateUtil.getCurrentTimeNoDel() + ".jpg";
    }

    private Uri getAvatarUri() {
        return Uri.fromFile(new File(avatarCachePath, imageName));
    }

    private void uploadAvatar(String path) {
        RequestParams params = new RequestParams(Config.SERVER_API_URL + "user/uploadAvatar");
        params.addQueryStringParameter("userId", App.USER_ID);
        params.addQueryStringParameter("token", App.ACCESS_TOKEN);
        params.addBodyParameter("image", new File(path));
        params.setMultipart(true);
        x.http().post(params, new Callback.CommonCallback<String>() {

            @Override
            public void onSuccess(String result) {

                ResponseResult response = new Gson().fromJson(result, ResponseResult.class);
                if (response.getCode() == Code.SUCCESS) {
                    CommonUtil.toast(MyInformationActivity.this, "上传成功");
                } else
                    CommonUtil.toast(MyInformationActivity.this, "上传失败");
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


    private void makeAvatarDir() {
        File avatarDir = new File(avatarCachePath);
        if (!avatarDir.exists()) {
            avatarDir.mkdirs();
        }
    }


}