package com.tc.activity;

import android.app.Activity;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;

import com.example.pb.myapplication.R;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * Created by PB on 2018/2/25.
 */
@ContentView(R.layout.activity_temp)
public class TempActivity extends Activity {

    @ViewInject(R.id.button)
    Button btn;
    @ViewInject(R.id.button2)
    Button btn_close;
    @ViewInject(R.id.rootView)
    View rootView;

    @ViewInject(R.id.emojiEditText)
    EmojiEditText edt;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        x.view().inject(this);
        final EmojiPopup emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(edt);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiPopup.toggle(); // Toggles visibility of the Popup.
            }
        });
        btn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emojiPopup.dismiss(); // Dismisses the Popup.
            }
        });


//        emojiPopup.toggle(); // Toggles visibility of the Popup.
//        emojiPopup.dismiss(); // Dismisses the Popup.
//        emojiPopup.isShowing(); // Returns true when Popup is showing.
    }
}
