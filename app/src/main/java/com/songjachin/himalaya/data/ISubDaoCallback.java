package com.songjachin.himalaya.data;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * Created by matthew on 2020/5/12 15:10
 * day day up!
 */
public interface ISubDaoCallback {


    /**
     * 添加的结果回调方法
     *
     * @param isSuccess
     */
    void onAddResult(boolean isSuccess);


    /**
     * 删除结果回调方法
     *
     * @param isSuccess
     */
    void onDelResult(boolean isSuccess);


    /**
     * 加载的结果
     *
     * @param result
     */
    void onSubListLoaded(List<Album> result);
}
