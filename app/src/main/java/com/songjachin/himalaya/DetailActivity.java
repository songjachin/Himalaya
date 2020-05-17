package com.songjachin.himalaya;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lcodecore.tkrefreshlayout.RefreshListenerAdapter;
import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.lcodecore.tkrefreshlayout.header.bezierlayout.BezierLayout;
import com.songjachin.himalaya.adapters.DetailListAdapter;
import com.songjachin.himalaya.base.BaseActivity;
import com.songjachin.himalaya.base.BaseApplication;
import com.songjachin.himalaya.constants.Constants;
import com.songjachin.himalaya.interfaces.IAlbumDetailViewCallback;
import com.songjachin.himalaya.interfaces.IPlayerCallback;
import com.songjachin.himalaya.interfaces.ISubscriptionCallback;
import com.songjachin.himalaya.interfaces.ISubscriptionPresenter;
import com.songjachin.himalaya.presenters.DetailPresenter;
import com.songjachin.himalaya.presenters.PlayerPresenter;
import com.songjachin.himalaya.presenters.SubscriptionPresenter;
import com.songjachin.himalaya.utils.ImageBlur;
import com.songjachin.himalaya.utils.LogUtil;
import com.songjachin.himalaya.utils.UIUtil;
import com.songjachin.himalaya.views.UILoader;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matthew on 2020/4/27 10:58
 * day day up!
 */
public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallback, UILoader.OnRetryClickListener, DetailListAdapter.OnItemClickListener, IPlayerCallback, ISubscriptionCallback {

    private static final String TAG = "DetailActivity";
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
    private int mCurrentId = 0;
    private PlayerPresenter mPlayerPresenter;
    private ImageView mPlayControlBtn;
    private TextView mPlayControlTips;
    private String mCurrentTrackTitle;
    private List<Track> mCurrentTracks = new ArrayList<>();
    private final static int DEFAULT_PLAY_INDEX = 0;
    private TwinklingRefreshLayout mRefreshLayout;
    private TextView mSubBtn;
    private ISubscriptionPresenter mSubscriptionPresenter;
    private Album mCurrentAlbum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        initView();
        initPresenter();
        updateSubState();
        updatePlaySate(mPlayerPresenter.isPlaying());
        initListener();
    }

    private void updateSubState() {
        if (mSubscriptionPresenter != null) {
            boolean isSub = mSubscriptionPresenter.isSub(mCurrentAlbum);
            mSubBtn.setText(isSub ? R.string.cancel_sub_tips_text : R.string.sub_tips_text);
        }
    }

    private void initPresenter() {
        mAlbumDetailPresenter = DetailPresenter.getInstance();
        mAlbumDetailPresenter.registerViewCallback(this);
        //播放器的Presenter.
        mPlayerPresenter = PlayerPresenter.getInstance();
        mPlayerPresenter.registerViewCallback(this);
        //订阅相关的presenter.
        mSubscriptionPresenter = SubscriptionPresenter.getInstance();
        mSubscriptionPresenter.getSubscriptionList();//从数据库里面拿数据。
        mSubscriptionPresenter.registerViewCallback(this);
    }


    @Override
    protected void onResume() {
        super.onResume();
        updatePlaySate(mPlayerPresenter.isPlaying());
        //mCurrentId = 0;
    }

    private void initListener() {
        mPlayControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    //判断播放器是否有播放列表.

                    mPlayerPresenter.switchPlayMode(XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP);
                    boolean has = mPlayerPresenter.hasPlayList();
                    if (has) {
                        //控制播放器的状态
                        handlePlayControl();
                    } else {
                        handleNoPlayList();
                    }
                }
            }
        });
        mSubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSubscriptionPresenter != null) {
                    boolean isSub = mSubscriptionPresenter.isSub(mCurrentAlbum);
                    //如果没有订阅，就去订阅，如果已经订阅了，那么就取消订阅
                    if (isSub) {
                        mSubscriptionPresenter.deleteSubscription(mCurrentAlbum);
                    } else {
                        mSubscriptionPresenter.addSubscription(mCurrentAlbum);
                    }
                }
            }
        });
    }

    /**
     * 当播放器里面没有播放的内容，我们要进行处理一下。
     */
    private void handleNoPlayList() {
        mPlayerPresenter.setPlayList(mCurrentTracks, DEFAULT_PLAY_INDEX);
    }

    private void handlePlayControl() {
        if (mPlayerPresenter.isPlaying()) {
            //正播放，那么就暂停
            mPlayerPresenter.pause();
        } else {
            mPlayerPresenter.setPlayList(mCurrentTracks, mCurrentId);
            mPlayerPresenter.play();
        }
    }

    private void initView() {
        mFrameLayout = this.findViewById(R.id.detail_frame_container);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(this) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView(container);
                }
                @Override
                protected View getEmptyView() {
                    //创建一个新的
                    View emptyView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view, this, false);
                    return emptyView;
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

        //播放控制的图标
        mPlayControlBtn = this.findViewById(R.id.detail_play_control);
        mPlayControlTips = this.findViewById(R.id.play_control_tv);
        mPlayControlTips.setSelected(true);
        mSubBtn = this.findViewById(R.id.detail_sub_btn);
    }

    private boolean mIsLoaderMore = false;

    private View createSuccessView(ViewGroup container) {
        mDetailListView = LayoutInflater.from(this).inflate(R.layout.list_item_detail, container, false);
        mDataListRecycler = mDetailListView.findViewById(R.id.detail_list_container);
        mRefreshLayout = mDetailListView.findViewById(R.id.refresh_layout);
        //RecyclerView 的使用1、设置布局管理器
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

        mDataListRecycler.setLayoutManager(linearLayoutManager);
        //2、设置适配器 3、设置数据，在getAlbumListLoaded()setData
        mDetailListAdapter = new DetailListAdapter();
        //4、设置adapter的上下间距
        mDataListRecycler.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(), 2);
                outRect.bottom = UIUtil.dip2px(view.getContext(), 2);
                outRect.left = UIUtil.dip2px(view.getContext(), 4);
                outRect.right = UIUtil.dip2px(view.getContext(), 4);
            }
        });
        mDataListRecycler.setAdapter(mDetailListAdapter);
        mDetailListAdapter.setOnItemClickListener(this);
        BezierLayout headerView = new BezierLayout(this);
        mRefreshLayout.setHeaderView(headerView);
        mRefreshLayout.setMaxHeadHeight(140);
        mRefreshLayout.setOverScrollBottomShow(false);
        mRefreshLayout.setOnRefreshListener(new RefreshListenerAdapter() {
            @Override
            public void onRefresh(TwinklingRefreshLayout refreshLayout) {
                super.onRefresh(refreshLayout);
                BaseApplication.getHandler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DetailActivity.this, "刷新成功...", Toast.LENGTH_SHORT).show();
                        mRefreshLayout.finishRefreshing();
                    }
                }, 2000);//2s之后出现
            }

            @Override
            public void onLoadMore(TwinklingRefreshLayout refreshLayout) {
                super.onLoadMore(refreshLayout);
                if (mAlbumDetailPresenter != null) {
                    mAlbumDetailPresenter.loadMore();
                    LogUtil.d(TAG, "detailPresenter---->loadMore()");
                    mIsLoaderMore = true;
                }
            }
        });
        return mDetailListView;
    }

    @Override
    public void getAlbumListLoaded(List<Track> tracks) {
        if (mIsLoaderMore && mRefreshLayout != null) {
            mRefreshLayout.finishLoadmore();
            mIsLoaderMore = false;
        }
        this.mCurrentTracks = tracks;
        this.mCurrentId = 0;
        //this.mPlayerPresenter.setPlayList(mCurrentTracks,DEFAULT_PLAY_INDEX);

        mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
        mDetailListAdapter.setData(tracks);
    }

    @Override
    public void onAlbumLoaded(Album targetAlbum) {
        this.mCurrentAlbum = targetAlbum;
        long id = targetAlbum.getId();
        this.mCurrentId = (int) id;
        //this.mCurrentTracks = targetAlbum.getTracks();
        //获取专辑的详情内容
        mCurrentPage = 1;
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.getAlbumDetail(mCurrentId, mCurrentPage);
        }


        if (mAlbumTitle != null) {
            mAlbumTitle.setText(targetAlbum.getAlbumTitle());
        }
        if (mAlbumAuthor != null) {
            mAlbumAuthor.setText(targetAlbum.getAnnouncer().getNickname());
        }
        //做高斯模糊，毛玻璃效果
        //TO/DO:
        if (mLargeCover != null) {
            Picasso.get().load(targetAlbum.getCoverUrlLarge()).into(mLargeCover, new Callback() {

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
                    LogUtil.e(TAG, "on error");
                }
            });
            //Glide.with(this).load(targetAlbum.getCoverUrlLarge()).into(mLargeCover);
        }
        if (mSmallCover != null) {
            Picasso.get().load(targetAlbum.getCoverUrlSmall()).into(mSmallCover);
        }
    }

    @Override
    public void onNetworkError(int errorCode, String errorMsg) {
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
    public void onLoaderMoreFinished(int size) {
        if (size > 0) {
            Toast.makeText(this, "成功加载" + size + "条节目", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "没有更多节目", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRefreshFinished(int size) {

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
            mAlbumDetailPresenter = null;
        }
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unregisterViewCallback(this);
            mPlayerPresenter = null;
        }
        if (mSubscriptionPresenter != null) {
            mSubscriptionPresenter.unregisterViewCallback(this);
            mSubscriptionPresenter = null;
        }
    }

    @Override
    public void onItemClick(List<Track> detailData, int position) {
        //跳转到具体页面
        //设置播放界面的数据
        PlayerPresenter playerPresenter = PlayerPresenter.getInstance();
        playerPresenter.setPlayList(detailData, position);
        Intent intent = new Intent(this, PlayActivity.class);
        startActivity(intent);
    }

    @Override
    public void onPlayStart() {
        //修改图标为暂停的，文字修改为正在播放.
        updatePlaySate(true);
    }

    private void updatePlaySate(boolean playing) {
        if (mPlayControlBtn != null && mPlayControlTips != null) {
            mPlayControlBtn.setImageResource(playing ? R.drawable.selector_play_control_pause : R.drawable.selector_play_control_play);
            if (!playing) {
                mPlayControlTips.setText(R.string.click_play_tips_text);
            } else {
                if (!TextUtils.isEmpty(mCurrentTrackTitle)) {
                    mPlayControlTips.setText(mCurrentTrackTitle);
                }
            }
        }
    }

    @Override
    public void onPlayPause() {
        //设置成播放的图标，文字修改成已暂停
        updatePlaySate(false);
    }

    @Override
    public void onPlayStop() {

    }

    @Override
    public void onPlayError() {

    }

    @Override
    public void nextPlay(Track track) {

    }

    @Override
    public void onPrePlay(Track track) {

    }

    @Override
    public void onListLoaded(List<Track> list) {
    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(int currentProgress, int total) {

    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onTrackUpdate(Track track, int index) {
        if (track != null) {
            mCurrentId = index;
            mCurrentTrackTitle = track.getTrackTitle();
            if (!TextUtils.isEmpty(mCurrentTrackTitle) && mPlayControlTips != null) {
                mPlayControlTips.setText(mCurrentTrackTitle);
            }
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }
    //======================================================================

    @Override
    public void onAddResult(boolean isSuccess) {
        if (isSuccess) {
            //如果成功了，那就修改UI成取消订阅
            mSubBtn.setText(R.string.cancel_sub_tips_text);
        }
        //给个toast
        String tipsText = isSuccess ? "订阅成功" : "订阅失败";
        Toast.makeText(this, tipsText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDeleteResult(boolean isSuccess) {
        if (isSuccess) {
            //如果成功了，那就修改UI成取消订阅
            mSubBtn.setText(R.string.sub_tips_text);
        }
        //给个toast
        String tipsText = isSuccess ? "删除成功" : "删除失败";
        Toast.makeText(this, tipsText, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSubscriptionsLoaded(List<Album> albums) {
        //在这个界面 不需要处理
    }

    @Override
    public void onSubFull() {
        //处理一个即可，toast
        Toast.makeText(this, "订阅数量不得超过" + Constants.MAX_SUB_COUNT, Toast.LENGTH_SHORT).show();
    }
    //===================================================
}