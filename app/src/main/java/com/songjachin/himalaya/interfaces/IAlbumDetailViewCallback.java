package com.songjachin.himalaya.interfaces;

import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * Created by matthew on 2020/4/27 15:07
 * day day up!
 */
public interface IAlbumDetailViewCallback {
    /**
     * note: the detail of album 加载更新
     */
     void getAlbumListLoaded(List<Track> tracks);

     /**
      * @param targetAlbum
      * targetAlbum不空时，把targetAlbum传给UI
      */
     void onAlbumLoaded(Album targetAlbum);


    void onNetworkError(int errorCode, String errorMsg);

    void onEmpty();

    void onLoading();

    /**
     * 加载更多的结果
     *
     * @param size size>0表示加载成功，否则表示加载失败.
     */
    void onLoaderMoreFinished(int size);


    /**
     * 下拉加载更多的结果
     *
     * @param size size>0表示加载成功，否则表示加载失败.
     */
    void onRefreshFinished(int size);
}
