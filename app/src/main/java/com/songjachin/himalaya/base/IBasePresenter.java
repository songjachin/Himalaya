package com.songjachin.himalaya.base;

/**
 * Created by matthew on 2020/4/29 11:29
 * day day up!
 */
public interface IBasePresenter<T > {
    void registerViewCallback(T t);

    void  unregisterViewCallback(T t);
}
