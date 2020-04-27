package com.songjachin.himalaya.presenters;

import com.songjachin.himalaya.interfaces.IAlbumDetailPresenter;
import com.songjachin.himalaya.interfaces.IAlbumDetailViewCallback;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matthew on 2020/4/27 14:55
 * day day up!
 */
public class AlbumDetailPresenterImpl implements IAlbumDetailPresenter {
    private Album mTargetAlbum = null;
    private List<IAlbumDetailViewCallback> mCallbacks = new ArrayList<>();

    private AlbumDetailPresenterImpl(){
    }
    private static AlbumDetailPresenterImpl sInstance = null;
    public static  AlbumDetailPresenterImpl getInstance(){
        if (sInstance == null) {
            synchronized (AlbumDetailPresenterImpl.class){
                if (sInstance == null) {
                    sInstance = new AlbumDetailPresenterImpl();
                }
            }
        }
        return sInstance;
    }

    @Override
    public void refreshMore() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void getAlbumDetail(int albumId, int page) {

    }

    public void setTargetAlbum(Album targetAlbum){
        this.mTargetAlbum = targetAlbum;
    }

    public void registerViewCallback(IAlbumDetailViewCallback callback){
        if (!mCallbacks.contains(callback)) {
            mCallbacks.add(callback);
            if (mTargetAlbum != null) {
                callback.onAlbumLoaded(mTargetAlbum);
            }
        }
    }

    public void  unregisterViewCallback(IAlbumDetailViewCallback callback){
        if (mCallbacks != null) {
            mCallbacks.remove(callback);
        }
    }
}
