package com.songjachin.himalaya.presenters;

import com.songjachin.himalaya.constants.Constants;
import com.songjachin.himalaya.interfaces.IAlbumDetailPresenter;
import com.songjachin.himalaya.interfaces.IAlbumDetailViewCallback;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by matthew on 2020/4/27 14:55
 * day day up!
 */
public class DetailPresenter implements IAlbumDetailPresenter {
    private static final String TAG = "AlbumDetailPresenterImp";
    private Album mTargetAlbum = null;
    private List<IAlbumDetailViewCallback> mCallbacks = new ArrayList<>();

    private DetailPresenter(){
    }
    private static DetailPresenter sInstance = null;
    public static DetailPresenter getInstance(){
        if (sInstance == null) {
            synchronized (DetailPresenter.class){
                if (sInstance == null) {
                    sInstance = new DetailPresenter();
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
        updateLoading();
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_ID, albumId + "");
        map.put(DTransferConstants.SORT, "time_asc");
        map.put(DTransferConstants.PAGE, page + "");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_TRACKS + "");
        CommonRequest.getTracks(map, new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(TrackList trackList) {
                if (trackList != null) {
                    List<Track> tracks = trackList.getTracks();
                   // LogUtil.d(TAG,"track list---->" + tracks.size());
                    handlerDetailItemResult(tracks);
                }
            }

            @Override
            public void onError(int i, String s) {
                    //LogUtil.d(TAG,"error--->" + i);
                   // LogUtil.d(TAG,"error msg--->" + s);
                handlerError();

            }
        });

    }

    private void handlerError() {
        if (mCallbacks != null) {
            for (IAlbumDetailViewCallback mCallback : mCallbacks) {
                mCallback.onNetworkError();
            }
        }
    }

    private void updateLoading() {
        if (mCallbacks != null) {
            for (IAlbumDetailViewCallback mCallback : mCallbacks) {
                mCallback.onLoading();
            }
        }
    }

    private void handlerDetailItemResult(List<Track> tracks) {
        if (tracks != null) {
            if (tracks.size() == 0) {
                for (IAlbumDetailViewCallback callback : mCallbacks) {
                    callback.onEmpty();
                }
            }else{
                for (IAlbumDetailViewCallback mCallback : mCallbacks) {
                    mCallback.getAlbumListLoaded(tracks);
                }
            }
        }
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
