package com.songjachin.himalaya.fragments;

import android.nfc.Tag;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.songjachin.himalaya.R;
import com.songjachin.himalaya.adapters.RecyclerViewListAdapter;
import com.songjachin.himalaya.base.BaseFragment;
import com.songjachin.himalaya.constants.Constants;
import com.songjachin.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by matthew on 2020/4/24 9:10
 * day day up!
 */
public class RecommendFragment extends BaseFragment {
    private static final String TAG = "RecommendFragment";
    private View mRootView;
    private RecyclerView mRecommendRv;
    private RecyclerViewListAdapter mRecyclerViewListAdapter;
    @Override
    protected View onSubViewLoaded(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        //加载界面
        mRootView = inflater.inflate(R.layout.fragment_recommend, container, false);

        //RecyclerView的使用1、找到布局2、布局管理器
        mRecommendRv = (RecyclerView) mRootView.findViewById(R.id.recommend_list);

        LinearLayoutManager  linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecommendRv.setLayoutManager(linearLayoutManager);

        mRecyclerViewListAdapter = new RecyclerViewListAdapter();
        mRecommendRv.setAdapter(mRecyclerViewListAdapter);
        //获取数据
        getRecommendData();

        //返回界面
        return mRootView;
    }

    /**
     *
     */
    private void getRecommendData() {
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
                        updateRecommend(albumList);
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

    private void updateRecommend(List<Album> albumList) {
        //把数据交给Adapter
        mRecyclerViewListAdapter.setData(albumList);
    }

}
