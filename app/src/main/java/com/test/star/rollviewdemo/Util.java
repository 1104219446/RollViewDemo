package com.test.star.rollviewdemo;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

/**
 * Describe：
 * Author:zhuokai.zeng
 * CreateTime:2019/8/14
 */
public class Util {
    /**
     * get screen width.
     * @param context
     * @return
     */
    public static float getScreenWidth(Activity context) {
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * dp to pix
     */
    public static int dpToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * pix to dp
     */
    public static int pxToDp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
