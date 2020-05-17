package com.songjachin.himalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.songjachin.himalaya.DetailActivity;
import com.songjachin.himalaya.R;
import com.songjachin.himalaya.adapters.AlbumListAdapter;
import com.songjachin.himalaya.base.BaseFragment;
import com.songjachin.himalaya.interfaces.IRecommendViewCallback;
import com.songjachin.himalaya.presenters.DetailPresenter;
import com.songjachin.himalaya.presenters.RecommendPresenter;
import com.songjachin.himalaya.utils.UIUtil;
import com.songjachin.himalaya.views.UILoader;

import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;


/**
 * Created by matthew on 2020/4/24 9:10
 * day day up!
 */
public class AlbumFragment extends BaseFragment implements IRecommendViewCallback, UILoader.OnRetryClickListener, AlbumListAdapter.OnAlbumItemClickListener {
    private static final String TAG = "RecommendFragment";
    private View mRootView;
    private RecyclerView mRecommendRv;
    private AlbumListAdapter mRecyclerViewListAdapter;
    private RecommendPresenter mRecommendPresenter;
    private UILoader mUILoader;
    @Override
    protected View onSubViewLoaded(final LayoutInflater inflater, @Nullable ViewGroup container) {
          mUILoader = new UILoader(getContext()) {
            @Override
            protected View getSuccessView( ViewGroup container) {
                return createSuccessView(inflater,container);
            }
              @Override
              protected View getEmptyView() {
                  //创建一个新的
                  View emptyView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view, this, false);
                  return emptyView;
              }
        };

        /**
         * @method onSubViewLoaded
         * @Description
         * @param  * @param inflater
         * @param container
         * @return android.view.View
         */

        //获取数据，猜你喜欢的接口3.10.6
        //getRecommend(){....updateRecommend()...}
        //获取逻辑层的对象
        mRecommendPresenter = RecommendPresenter.getsInstance();
        //注册更新UI回调的接口,callback--->this相当于A的回调地址，要在B里注册即Present层
        mRecommendPresenter.registerViewCallback(this);
        //通过逻辑层获取数据，A向B发起请求，获得数据
        mRecommendPresenter.getRecommendList();

        //getRecommendData();
        // 跟父类解绑，android不允许一个已经绑定了的view重复绑定android的View有一个规则，如果这个view已经添加到其他的ViewGroup里了，
        // 必须要脱离关系才可以加入到新的ViewGroup里.假设你有这么一个场景,你的这个mUiLoader没有销毁，然后又进了你当前Fragment的声明周期方法,
        // 这个UiLoader不是新创建的,之前已经添加到某个ViewGroup里了.如果你再添加的话则会报错.所以加了这个代码,用于防御.
        if (mUILoader.getParent() instanceof ViewGroup) {
            ((ViewGroup) mUILoader.getParent()).removeView(mUILoader);
        }

        mUILoader.setOnRetryClickListener(this);
        //返回界面
        return mUILoader;
    }

    private View createSuccessView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        //加载界面
        mRootView = inflater.inflate(R.layout.fragment_recommend, container, false);

        //RecyclerView的使用1、找到布局2、布局管理器
        mRecommendRv = (RecyclerView) mRootView.findViewById(R.id.recommend_list);
        TwinklingRefreshLayout twinklingRefreshLayout = mRootView.findViewById(R.id.over_scroll_view);
        twinklingRefreshLayout.setPureScrollModeOn();
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
        mRecyclerViewListAdapter = new AlbumListAdapter();
        mRecommendRv.setAdapter(mRecyclerViewListAdapter);
        mRecyclerViewListAdapter.setOnAlbumItemClickListener(this);
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
        mRecyclerViewListAdapter.setData(result);//数据成功时把它交给adapter,
        mUILoader.updateStatus(UILoader.UIStatus.SUCCESS);
    }

    @Override
    public void onNetworkError() {
        mUILoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
    }

    @Override
    public void onEmpty() {
        mUILoader.updateStatus(UILoader.UIStatus.EMPTY);
    }

    @Override
    public void onLoading() {
        mUILoader.updateStatus(UILoader.UIStatus.LOADING);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //在这里取消注册,避免内存泄漏
        if (mRecommendPresenter != null) {
            mRecommendPresenter.unregisterViewCallback(this);
        }

    }

    @Override
    public void onRetryClick() {
        //retry again
        if (mRecommendPresenter != null) {
            mRecommendPresenter.getRecommendList();
        }
    }

    @Override
    public void onItemClick(int position, Album album) {
        //when the item in recyclerview was clicked and jump to DetailActivity
        DetailPresenter.getInstance().setTargetAlbum(album);
        Intent intent = new Intent(getContext(), DetailActivity.class);
        startActivity(intent);

    }
}
