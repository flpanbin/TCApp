package com.tc.view;

import android.content.Context;
import android.support.annotation.LayoutRes;

/**
 * Created by PB on 2017/7/28.
 */

public abstract class ProgressDialog extends CommonDialog {
    public ProgressDialog(Context context,@LayoutRes int layout) {
        super(context, layout, false);
        setCancelable(false);
        setCanceledOnTouchOutside(false);
        init();
    }

    public abstract  void init();

}


