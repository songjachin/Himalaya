package com.songjachin.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * Created by matthew on 2020/4/25 9:50
 * day day up!
 */
public interface IRecommendViewCallback {
    /**
     * 获取推荐内容
     * @param  result
     */
    void onRecommendListLoaded(List<Album> result) ;
    /**
     * 加载更多
     * @param result
     */
    void onLoadedMore(List<Album> result);
    /**
     * 刷新
     * @param  result
     */
    void onRefreshMore(List<Album> result);
}
