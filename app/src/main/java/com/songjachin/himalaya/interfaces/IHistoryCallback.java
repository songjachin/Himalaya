package com.songjachin.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * Created by matthew on 2020/5/11 20:15
 * day day up!
 */
public interface IHistoryCallback {
    /**
     * 历史内容加载结果.
     *
     * @param tracks
     */
    void onHistoriesLoaded(List<Track> tracks);
}
