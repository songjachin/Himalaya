package com.songjachin.himalaya.interfaces;

import com.songjachin.himalaya.base.IBasePresenter;

/**
 * Created by matthew on 2020/4/25 9:44
 * day day up!
 */
public interface IRecommendPresenter extends IBasePresenter<IRecommendViewCallback> {
    /**
    *获取推荐内容
     */
    void getRecommendList();
    /**
     *下拉加载更多
     */
    void refreshMore();
    /**
     * 加载更多
     */
    void loadMore();
    /**
     * note:这个方法用于注册UI的回调
     */
/*    void registerViewCallback(IRecommendViewCallback callback);

    void unregisterViewCallback(IRecommendViewCallback callback);*/
}
