package com.songjachin.himalaya.utils;

import android.content.Context;

/**
 * Created by matthew on 2020/4/23 22:47
 * day day up!
 */
public final class UIUtil {

    public static int dip2px(Context context, double dpValue) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5);
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }
}
