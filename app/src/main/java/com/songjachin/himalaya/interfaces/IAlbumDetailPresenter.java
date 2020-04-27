package com.songjachin.himalaya.interfaces;

/**
 * Created by matthew on 2020/4/27 14:50
 * day day up!
 */
public interface IAlbumDetailPresenter {
    /**
     *下拉加载更多
     */
    void refreshMore();
    /**
     * 加载更多
     */
    void loadMore();

    /**
     * @param albumId
     * @param page
     */
    void getAlbumDetail(int albumId, int page);

    void registerViewCallback(IAlbumDetailViewCallback callback);

    void  unregisterViewCallback(IAlbumDetailViewCallback callback);
}
