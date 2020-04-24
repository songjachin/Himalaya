package com.songjachin.himalaya.indicators;

import java.util.List;

/**
 * Created by matthew on 2020/4/23 22:12
 * day day up!
 */
public interface IPagerIndicator {
    void onPageScrolled(int position, float positionOffset, int positionOffsetPixels);

    void onPageSelected(int position);

    void onPageScrollStateChanged(int state);

    void onPositionDataProvide(List<PositionData> dataList);
}
