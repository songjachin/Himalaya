package com.songjachin.himalaya.indicators;

/**
 * Created by matthew on 2020/4/23 22:13
 * day day up!
 */
public class PositionData {
    public int mLeft;
    public int mTop;
    public int mRight;
    public int mBottom;
    public int mContentLeft;
    public int mContentTop;
    public int mContentRight;
    public int mContentBottom;

    public int width() {
        return mRight - mLeft;
    }

    public int height() {
        return mBottom - mTop;
    }

    public int contentWidth() {
        return mContentRight - mContentLeft;
    }

    public int contentHeight() {
        return mContentBottom - mContentTop;
    }

    public int horizontalCenter() {
        return mLeft + width() / 2;
    }

    public int verticalCenter() {
        return mTop + height() / 2;
    }
}
