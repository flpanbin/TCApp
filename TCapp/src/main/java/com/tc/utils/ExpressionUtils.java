package com.tc.utils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;

import java.util.ArrayList;
import java.util.List;

/**
 * 表情工具类
 * Created by PB on 2017/11/19.
 */

public class ExpressionUtils {
    /**
     * 判断字符串是否包含表情字符串
     *
     * @param keywords
     * @return
     */
    public static boolean containsKey(String keywords) {
        List<String> expressionList = getExpressionRes(100);
        for (int i = 0; i < expressionList.size(); i++) {
            int index = keywords.indexOf(expressionList.get(i));
            if (index >= 0) {
                return true;
            }
        }
        return false;

    }

    /**
     * 判断字符串是否是表情字符串
     *
     * @param keywords
     * @return
     */
    public static boolean equalsKey(String keywords) {
        List<String> expressionList = getExpressionRes(100);
        for (int i = 0; i < expressionList.size(); i++) {

            if (expressionList.get(i).equals(keywords)) {
                return true;
            }
        }
        return false;

    }

    /**
     * 得到表情集合
     *
     * @param count
     * @return
     */
    public static List<String> getExpressionRes(int count) {
        List<String> reslist = new ArrayList<String>();
        String filename = "";
        for (int x = 0; x < count; x++) {

            if (x < 10)
                filename = "f00" + x + ".png";
            else if (10 <= x && x <= 99)
                filename = "f0" + x + ".png";
            reslist.add(filename);
        }
        return reslist;

    }

    /**
     * 解析消息内容将表情字符串替换为表情
     *
     * @param context
     * @param content 消息内容
     * @return
     */
    public static SpannableString analysisMessageContent(Context context,
                                                         String content) {
        SpannableString spannable = new SpannableString(content);
        List<String> expressionList = getExpressionRes(100);
        for (String filename : expressionList) {
            int i = 0;
            // 找到所有表情的位置并将其替换
            while (content.indexOf(filename, i) >= 0) {
                i = content.indexOf(filename, i);
                ImageSpan span = getImageSpan(context, filename);

                // 将表情字符串替换为表情
                spannable.setSpan(span, i, i + filename.length(),
                        Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                i++;
            }
        }
        return spannable;
    }

    /**
     * 获取ImageSpan
     *
     * @param context
     * @param filename
     * @return
     */
    public static ImageSpan getImageSpan(Context context, String filename) {
        Drawable drawable = ResUtil.getImageFromAssets(context, "expression/" + filename);
        //设置图片显示的大小
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth() * 5,
                drawable.getIntrinsicHeight() * 5);
        return new ImageSpan(drawable,
                ImageSpan.ALIGN_BASELINE);
    }

}
