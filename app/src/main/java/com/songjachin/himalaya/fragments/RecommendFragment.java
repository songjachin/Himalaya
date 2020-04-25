package com.songjachin.himalaya.fragments;

import android.graphics.Rect;
import android.nfc.Tag;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.songjachin.himalaya.R;
import com.songjachin.himalaya.adapters.RecyclerViewListAdapter;
import com.songjachin.himalaya.base.BaseFragment;
import com.songjachin.himalaya.constants.Constants;
import com.songjachin.himalaya.interfaces.IRecommendViewCallback;
import com.songjachin.himalaya.presenters.RecommendPresenter;
import com.songjachin.himalaya.utils.LogUtil;
import com.songjachin.himalaya.utils.UIUtil;
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
public class RecommendFragment extends BaseFragment implements IRecommendViewCallback {
    private static final String TAG = "RecommendFragment";
    private View mRootView;
    private RecyclerView mRecommendRv;
    private RecyclerViewListAdapter mRecyclerViewListAdapter;
    private RecommendPresenter mRecommendPresenter;
    @Override
    protected View onSubViewLoaded(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        /**
         * @method onSubViewLoaded
         * @Description
         * @param  * @param inflater
         * @param container
         * @return android.view.View
         */
        //加载界面
        mRootView = inflater.inflate(R.layout.fragment_recommend, container, false);

        //RecyclerView的使用1、找到布局2、布局管理器
        mRecommendRv = (RecyclerView) mRootView.findViewById(R.id.recommend_list);

        LinearLayoutManager  linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        mRecommendRv.setLayoutManager(linearLayoutManager);

        mRecommendRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(),4);
                outRect.bottom = UIUtil.dip2px(view.getContext(),4);
                outRect.left = UIUtil.dip2px(view.getContext(),4);
                outRect.right = UIUtil.dip2px(view.getContext(),4);

            }
        });
        //adapter
        mRecyclerViewListAdapter = new RecyclerViewListAdapter();
        mRecommendRv.setAdapter(mRecyclerViewListAdapter);
        //获取数据，猜你喜欢的接口
        //getRecommend(){....updateRecommend()...}
        //获取逻辑层的对象
        mRecommendPresenter = RecommendPresenter.getsInstance();
        //通过逻辑层获取数据
        mRecommendPresenter.getRecommendList();
        //注册更新UI回调的接口
        mRecommendPresenter.registerViewCallback(this);
        //getRecommendData();

        //返回界面
        return mRootView;
    }

    /**
     *
     */


    private void updateRecommend(List<Album> albumList) {
        //把数据交给Adapter
        mRecyclerViewListAdapter.setData(albumList);
    }

    @Override
    public void onRecommendListLoaded(List<Album> result) {
        //当获取到数据时，在这里回调更新UI
        mRecyclerViewListAdapter.setData(result);
    }

    @Override
    public void onLoadedMore(List<Album> result) {

    }

    @Override
    public void onRefreshMore(List<Album> result) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //在这里取消注册,避免内存泄漏
        if (mRecommendPresenter != null) {
            mRecommendPresenter.unregisterViewCallback(this);
        }

    }
}
