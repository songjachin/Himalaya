package com.songjachin.himalaya.presenters;

import androidx.annotation.Nullable;

import com.songjachin.himalaya.constants.Constants;
import com.songjachin.himalaya.data.XimalayApi;
import com.songjachin.himalaya.interfaces.IAlbumDetailPresenter;
import com.songjachin.himalaya.interfaces.IAlbumDetailViewCallback;
import com.songjachin.himalaya.utils.LogUtil;
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
    private static final String TAG = "DetailPresenter";
    private Album mTargetAlbum = null;
    private List<IAlbumDetailViewCallback> mCallbacks = new ArrayList<>();
    private List<Track> mTracks = new ArrayList<>();
    //当前的专辑id
    private int mCurrentAlbumId = -1;
    //当前页
    private int mCurrentPageIndex = 0;

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
        //去加载更多内容
        mCurrentPageIndex++;
        LogUtil.d(TAG,"mCurrentpage"+ mCurrentPageIndex);
        //传入true，表示结果会追加到列表的后方。
        doLoaded(true);
    }

    private void doLoaded(final boolean isLoaderMore) {
        XimalayApi ximalayApi = XimalayApi.getXimalayApi();
        ximalayApi.getAlbumDetail(new IDataCallBack<TrackList>() {
            @Override
            public void onSuccess(@Nullable TrackList trackList) {
                if (trackList != null) {
                    List<Track> tracks = trackList.getTracks();
                    LogUtil.d(TAG, "tracks size -- > " + mTracks.size());
                    if (isLoaderMore) {
                        //上拉加载，结果放到后面去
                        mTracks.addAll(tracks);
                        LogUtil.d(TAG,"mTrack add All tracks");
                        int size = tracks.size();
                        handlerLoaderMoreResult(size);
                    } else {
                        //这个是下拉加载，结果放到前面去
                        mTracks.addAll(0, tracks);
                    }
                    LogUtil.d(TAG,"track size--2----->"+ mTracks.size());
                    handlerAlbumDetailResult(mTracks);
                }
            }

            @Override
            public void onError(int errorCode, String errorMsg) {
                if (isLoaderMore) {
                    mCurrentPageIndex--;
                }
                LogUtil.d(TAG, "errorCode -- >   " + errorCode);
                LogUtil.d(TAG, "errorMsg -- >   " + errorMsg);
                handlerLoadError(errorCode, errorMsg);
            }
        }, mCurrentAlbumId, mCurrentPageIndex);
    }

    private void handlerLoadError(int errorCode, String errorMsg) {
        for (IAlbumDetailViewCallback callback : mCallbacks) {
            callback.onNetworkError(errorCode, errorMsg);
        }
    }

    private void handlerAlbumDetailResult(List<Track> tracks) {
        for (IAlbumDetailViewCallback mCallback : mCallbacks) {
            mCallback.getAlbumListLoaded(tracks);
        }
    }

    private void handlerLoaderMoreResult(int size) {
        for (IAlbumDetailViewCallback callback : mCallbacks) {
            callback.onLoaderMoreFinished(size);
        }
    }

    @Override
    public void getAlbumDetail(int albumId, int page) {
        mTracks.clear();
        this.mCurrentAlbumId = albumId;
        this.mCurrentPageIndex = page;
        //根据页码和专辑id获取列表
        doLoaded(false);


    }

    private void handlerError(int errorCode, String errorMsg) {
        if (mCallbacks != null) {
            for (IAlbumDetailViewCallback mCallback : mCallbacks) {
                mCallback.onNetworkError( errorCode, errorMsg);
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
