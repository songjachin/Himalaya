package com.songjachin.himalaya;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.songjachin.himalaya.adapters.DetailListAdapter;
import com.songjachin.himalaya.base.BaseActivity;
import com.songjachin.himalaya.interfaces.IAlbumDetailViewCallback;
import com.songjachin.himalaya.presenters.DetailPresenter;
import com.songjachin.himalaya.presenters.PlayerPresenter;
import com.songjachin.himalaya.utils.ImageBlur;
import com.songjachin.himalaya.utils.LogUtil;
import com.songjachin.himalaya.utils.UIUtil;
import com.songjachin.himalaya.views.UILoader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * Created by matthew on 2020/4/27 10:58
 * day day up!
 */
public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallback, UILoader.OnRetryClickListener, DetailListAdapter.OnItemClickListener {

    private static final String TAG ="DetailActivity" ;
    private ImageView mLargeCover;
    private ImageView mSmallCover;
    private TextView mAlbumTitle;
    private TextView mAlbumAuthor;
    private DetailPresenter mAlbumDetailPresenter;
    private int mCurrentPage = 0;
    private RecyclerView mDataListRecycler;
    private DetailListAdapter mDetailListAdapter;
    private FrameLayout mFrameLayout;
    private UILoader mUiLoader;
    private View mDetailListView;
    private int mCurrentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        initView();
        mAlbumDetailPresenter = DetailPresenter.getInstance();
        mAlbumDetailPresenter.registerViewCallback(this);

    }

    private void initView() {
        mFrameLayout = this.findViewById(R.id.detail_frame_container);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container );
                }
            };
            mFrameLayout.removeAllViews();
            mUiLoader.setOnRetryClickListener(this);
            mFrameLayout.addView(mUiLoader);
        }

        mLargeCover = this.findViewById(R.id.iv_large_cover);
        mSmallCover = this.findViewById(R.id.iv_small_cover);
        mAlbumTitle = this.findViewById(R.id.tv_album_title);
        mAlbumAuthor = this.findViewById(R.id.tv_album_author);

    }

    private View createSuccessView(ViewGroup container) {
        mDetailListView = LayoutInflater.from(this).inflate(R.layout.list_item_detail, container,false );
        mDataListRecycler = mDetailListView.findViewById(R.id.detail_list_container);
        //RecyclerView 的使用1、设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        mDataListRecycler.setLayoutManager(linearLayoutManager);
        //2、设置适配器 3、设置数据，在getAlbumListLoaded()setData
        mDetailListAdapter = new DetailListAdapter();
        //4、设置adapter的上下间距
        mDataListRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(),2);
                outRect.bottom = UIUtil.dip2px(view.getContext(),2);
                outRect.left = UIUtil.dip2px(view.getContext(),4);
                outRect.right = UIUtil.dip2px(view.getContext(),4);
            }
        });
        mDataListRecycler.setAdapter(mDetailListAdapter);
        mDetailListAdapter.setOnItemClickListener(this);
        return mDetailListView;
    }

    @Override
    public void getAlbumListLoaded(List<Track> tracks) {
        mDetailListAdapter.setData(tracks);
        mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
    }

    @Override
    public void onAlbumLoaded(Album targetAlbum) {

        long id = targetAlbum.getId();
        this.mCurrentId = (int)id;
        //获取专辑的详情内容
        mCurrentPage = 1;
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.getAlbumDetail((int) id, mCurrentPage);
        }



        if (mAlbumTitle != null) {
            mAlbumTitle.setText(targetAlbum.getAlbumTitle());
        }
        if (mAlbumAuthor != null) {
            mAlbumAuthor.setText(targetAlbum.getAnnouncer().getNickname());
        }
        //做高斯模糊，毛玻璃效果
        //TO/DO:
        if (mLargeCover != null ) {
            Picasso.get().load(targetAlbum.getCoverUrlLarge()).into(mLargeCover,new Callback(){

                @Override
                public void onSuccess() {
                    Drawable drawable = mLargeCover.getDrawable();
                    if (drawable != null) {
                        //到这里才说明是有图片的
                        ImageBlur.makeBlur(mLargeCover, DetailActivity.this);
                    }
                }

                @Override
                public void onError(Exception e) {
                    LogUtil.e(TAG,"on error");
                }
            });
            //Glide.with(this).load(targetAlbum.getCoverUrlLarge()).into(mLargeCover);
        }
        if (mSmallCover != null) {
            Picasso.get().load(targetAlbum.getCoverUrlSmall()).into(mSmallCover);
        }
    }

    @Override
    public void onNetworkError() {
        mUiLoader.updateStatus(UILoader.UIStatus.NETWORK_ERROR);
    }

    @Override
    public void onEmpty() {
        mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
    }

    @Override
    public void onLoading() {
        mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
    }

    @Override
    public void onRetryClick() {
        //retry again
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.getAlbumDetail(mCurrentId, mCurrentPage);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.unregisterViewCallback(this);
        }
    }

    @Override
    public void onItemClick(List<Track> detailData, int position) {
        //跳转到具体页面
        //设置播放界面的数据
        PlayerPresenter playerPresenter = PlayerPresenter.getInstance();
        playerPresenter.setPlayList(detailData , position);
        Intent intent = new Intent(this,PlayActivity.class);
        startActivity(intent);
    }
}
