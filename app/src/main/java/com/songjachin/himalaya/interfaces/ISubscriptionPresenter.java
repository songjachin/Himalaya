package com.songjachin.himalaya.interfaces;

import com.songjachin.himalaya.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;

/**
 * Created by matthew on 2020/5/11 10:31
 * day day up!
 */
public interface ISubscriptionPresenter extends IBasePresenter<ISubscriptionCallback> {

    /**
     * 添加订阅
     *
     * @param album
     */
    void addSubscription(Album album);

    /**
     * 删除订阅
     *
     * @param album
     */
    void deleteSubscription(Album album);


    /**
     * 获取订阅列表
     */
    void getSubscriptionList();


    /**
     * 判断当前专辑是否已经收藏/订阅
     *
     * @param album
     */
    boolean isSub(Album album);
}
