package com.songjachin.himalaya.presenters;

import com.songjachin.himalaya.base.BaseApplication;
import com.songjachin.himalaya.data.ISubDaoCallback;
import com.songjachin.himalaya.data.SubscriptionDao;
import com.songjachin.himalaya.interfaces.ISubscriptionCallback;
import com.songjachin.himalaya.interfaces.ISubscriptionPresenter;
import com.ximalaya.ting.android.opensdk.model.album.Album;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by matthew on 2020/5/13 16:34
 * day day up!
 */
public class SubscriptionPresenter implements ISubscriptionPresenter, ISubDaoCallback {


    private List<ISubscriptionCallback> mCallbacks = new ArrayList<>();
    private final SubscriptionDao mSubscriptionDao;
    private Map<Long, Album> mData = new HashMap<>();

    private SubscriptionPresenter(){
        mSubscriptionDao = SubscriptionDao.getInstance();
        mSubscriptionDao.setCallback(this);
    }
    private  static SubscriptionPresenter sInstance = null;
    public  static SubscriptionPresenter getInstance(){
        if (sInstance == null) {
            synchronized (SubscriptionPresenter.class){
                if (sInstance == null) {
                    sInstance = new SubscriptionPresenter();
                }
            }
        }
        return sInstance;
    }

    private void listSubscription(){
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (mSubscriptionDao != null) {
                    mSubscriptionDao.listAlbums();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void addSubscription(final Album album) {
        Observable.create(new ObservableOnSubscribe<Object>() {

            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (mSubscriptionDao != null) {
                    mSubscriptionDao.addAlbum(album);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void deleteSubscription(final Album album) {
        Observable.create(new ObservableOnSubscribe<Object>() {

            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if (mSubscriptionDao != null) {
                    mSubscriptionDao.delAlbum(album);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void getSubscriptionList() {
        listSubscription();
    }

    @Override
    public boolean isSub(Album album) {
        Album result = mData.get(album.getId());
        //不为空，表示已经订阅
        return result != null;
    }

    @Override
    public void registerViewCallback(ISubscriptionCallback iSubscriptionCallback) {
        if (mCallbacks!=null && !mCallbacks.contains(iSubscriptionCallback)) {
            mCallbacks.add(iSubscriptionCallback);
        }
    }

    @Override
    public void unregisterViewCallback(ISubscriptionCallback iSubscriptionCallback) {
        if (mCallbacks != null) {
            mCallbacks.remove(iSubscriptionCallback);
        }
    }

    @Override
    public void onAddResult(final boolean isSuccess) {
        listSubscription();
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onAddResult(isSuccess);
                }
            }
        });
    }

    @Override
    public void onDelResult(final boolean isSuccess) {
        listSubscription();
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onDeleteResult(isSuccess);
                }
            }
        });
    }

    @Override
    public void onSubListLoaded(final List<Album> result) {
        mData.clear();
        for (Album album : result) {
            mData.put(album.getId(),album);
        }

        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for (ISubscriptionCallback callback : mCallbacks) {
                    callback.onSubscriptionsLoaded(result);
                }
            }
        });
    }
}
