package com.songjachin.himalaya.presenters;

import com.songjachin.himalaya.constants.Constants;
import com.songjachin.himalaya.data.XimalayApi;
import com.songjachin.himalaya.interfaces.IRecommendPresenter;
import com.songjachin.himalaya.interfaces.IRecommendViewCallback;
import com.songjachin.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by matthew on 2020/4/25 10:45
 * day day up!
 */
public class RecommendPresenter implements IRecommendPresenter {
    private static final String TAG = "RecommendPresenter";
    private List<IRecommendViewCallback> mCallbacks = new ArrayList<>();
    private List<Album> mCurrentRecommend = null;
    private List<Album> mAlbumList;

    private  RecommendPresenter(){
    }
    private static RecommendPresenter sInstance = null;
    /**
     * note: DCL
     */
    public static RecommendPresenter getsInstance(){
        if (sInstance == null) {
            synchronized (RecommendPresenter.class){
                if (sInstance == null) {
                    sInstance = new RecommendPresenter();
                }
            }
        }
        return sInstance;
    }
    /**
     * 获取当前的推荐专辑列表
     *
     * @return 推荐专辑列表，使用这前要判空
     */
    public List<Album> getCurrentRecommend() {
        return mCurrentRecommend;
    }
    @Override
    public void getRecommendList() {
        //如果内容不空的话，那么直接使用当前的内容
        if(mAlbumList != null && mAlbumList.size() > 0) {
            LogUtil.d(TAG,"getRecommendList -- > from list.");
            handlerRecommendResult(mAlbumList);
            return;
        }
        //获取数据3.X.x猜你喜欢的接口
        updateLoading();//B向A发送数据，这是正在加载中的页面显示
        XimalayApi ximalayApi = XimalayApi.getXimalayApi();
        ximalayApi.getRecommendList(new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                LogUtil.d(TAG,"thread -----> " + Thread.currentThread().getName());
                if (gussLikeAlbumList != null) {
                    mAlbumList = gussLikeAlbumList.getAlbumList();
                    if (mAlbumList != null) {
                        //LogUtil.d(TAG, "album size--->" + albumList.size());
                        //LogUtil.d(TAG, "album" + albumList);
                        //updateRecommend(albumList);
                        handlerRecommendResult(mAlbumList);//B向A发送请求到的数据，
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                //LogUtil.d(TAG, "error code" + i);
                //LogUtil.d(TAG, "error" + s);
                handlerError();
            }
        });

    }

    private void handlerError() {
        if (mCallbacks != null) {
            for (IRecommendViewCallback mCallback : mCallbacks) {
                mCallback.onNetworkError();
            }
        }
    }

    private void updateLoading(){
        if (mCallbacks != null) {
            for (IRecommendViewCallback mCallback : mCallbacks) {
                mCallback.onLoading();
            }
        }
    }

    private void handlerRecommendResult(List<Album> albumList) {
        if (albumList != null) {
            //albumList.clear();
            if (albumList.size()==0) {
                for (IRecommendViewCallback mCallback : mCallbacks) {
                    mCallback.onEmpty();
                }
            }else {
                for (IRecommendViewCallback mCallback : mCallbacks) {
                    mCallback.onRecommendListLoaded(albumList);//mCallback是地址，在A的地址调用更新的方法
                }

                this.mCurrentRecommend = albumList;
            }
        }
        //notify the UI

    }

    @Override
    public void refreshMore() {

    }

    @Override
    public void loadMore() {

    }

    @Override
    public void registerViewCallback(IRecommendViewCallback callback) {
        if(mCallbacks!=null && !mCallbacks.contains(callback)){
            mCallbacks.add(callback);//A中调用B的方法，注册A的地址
        }
    }

    @Override
    public void unregisterViewCallback(IRecommendViewCallback callback) {
        if (mCallbacks != null) {
            mCallbacks.remove(callback);
        }
    }
}
