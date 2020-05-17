package com.songjachin.himalaya.data;

import com.ximalaya.ting.android.opensdk.model.album.Album;

/**
 * Created by matthew on 2020/5/12 15:09
 * day day up!
 */
public interface ISubDao {


    void setCallback(ISubDaoCallback callback);

    /**
     * 添加专辑订阅
     *
     * @param album
     */
    void addAlbum(Album album);

    /**
     * 删除订阅内容
     *
     * @param album
     */
    void delAlbum(Album album);


    /**
     * 获取订阅内容
     */
    void listAlbums();
}
