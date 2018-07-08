package com.tc.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;

/**
 * 获取资源文件
 * Created by PB on 2017/11/8.
 */

public class ResUtil {
    /**
     * 从assets 文件夹中读取图片
     */
    public static Drawable getImageFromAssets(final Context ctx, String fileName) {
        try {
            InputStream is = ctx.getResources().getAssets().open(fileName);
            return Drawable.createFromStream(is, null);
        } catch (IOException e) {
            if (e != null) {
                e.printStackTrace();
            }
        } catch (OutOfMemoryError e) {
            if (e != null) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            if (e != null) {
                e.printStackTrace();
            }
        }
        return null;
    }
    /**
     * 从Assets目录下获取图片方法1
     *
     * @param imageName
     * @return Bitmap
     */
    public Bitmap getImageBitmapFromAssets(Context context,String imageName) {
        AssetManager am = context.getAssets();
        InputStream is = null;
        try {
            is = am.open("page/" + imageName);//得到图片流
        } catch (IOException e) {
            e.printStackTrace();
        }

        return BitmapFactory.decodeStream(is);
    }
}
