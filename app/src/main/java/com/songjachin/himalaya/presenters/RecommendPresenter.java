package com.songjachin.himalaya.presenters;

import com.songjachin.himalaya.constants.Constants;
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

    @Override
    public void getRecommendList() {
        //获取数据3.X.x猜你喜欢的接口
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.LIKE_COUNT, Constants.RECOMMEND_COUNT + "");
        CommonRequest.getGuessLikeAlbum(map, new IDataCallBack<GussLikeAlbumList>() {
            @Override
            public void onSuccess(GussLikeAlbumList gussLikeAlbumList) {
                LogUtil.d(TAG,"thread -----> " + Thread.currentThread().getName());
                if (gussLikeAlbumList != null) {
                    List<Album> albumList = gussLikeAlbumList.getAlbumList();
                    if (albumList != null) {
                        //LogUtil.d(TAG, "album size--->" + albumList.size());
                        //LogUtil.d(TAG, "album" + albumList);
                        //updateRecommend(albumList);
                        handlerRecommendResult(albumList);
                    }
                }
            }

            @Override
            public void onError(int i, String s) {
                LogUtil.d(TAG, "error code" + i);
                LogUtil.d(TAG, "error" + s);
            }
        });

    }


    private void handlerRecommendResult(List<Album> albumList) {
        //notify the UI
        if (mCallbacks != null) {
            for (IRecommendViewCallback mCallback : mCallbacks) {
                mCallback.onRecommendListLoaded(albumList);
            }
        }
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
            mCallbacks.add(callback);
        }
    }

    @Override
    public void unregisterViewCallback(IRecommendViewCallback callback) {
        if (mCallbacks != null) {
            mCallbacks.remove(mCallbacks);
        }
    }
}
