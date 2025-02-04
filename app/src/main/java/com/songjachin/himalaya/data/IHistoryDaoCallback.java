package com.songjachin.himalaya.data;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * Created by matthew on 2020/5/17 15:34
 * day day up!
 */
public interface IHistoryDaoCallback {
    /**
     * 添加历史的结果
     *
     * @param isSuccess
     */
    void onHistoryAdd(boolean isSuccess);


    /**
     * 删除历史的结果
     *
     * @param isSuccess
     */
    void onHistoryDel(boolean isSuccess);


    /**
     * 历史数据加载的结果
     *
     * @param tracks
     */
    void onHistoriesLoaded(List<Track> tracks);


    /**
     * 历史内容清楚结果
     */
    void onHistoriesClean(boolean isSuccess);
}
