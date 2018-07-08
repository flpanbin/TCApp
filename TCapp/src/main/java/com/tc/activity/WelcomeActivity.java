package com.tc.activity;

import android.os.Bundle;

import com.example.pb.myapplication.R;
import com.igexin.sdk.PushManager;
import com.tc.data.CommonData;
import com.tc.service.DemoPushService;
import com.tc.service.IntentService;

import org.xutils.view.annotation.ContentView;
import org.xutils.x;

import static com.tc.utils.CommonUtil.hideVirtualKey;

/**
 * Created by PB on 2017/12/13.
 */

@ContentView(R.layout.activity_welcome)
public class WelcomeActivity extends BaseActivity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        x.view().inject(this);
        init();
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        getVersion();

    }
    private void init() {
        PushManager.getInstance().initialize(this.getApplicationContext(), DemoPushService.class);
        PushManager.getInstance().registerPushIntentService(this.getApplicationContext(), IntentService.class
        );
    }
    private void getVersion() {
        CommonData.getVersion(this,false,true);
    }


}
