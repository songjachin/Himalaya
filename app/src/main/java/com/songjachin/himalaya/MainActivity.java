package com.songjachin.himalaya;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.songjachin.himalaya.adapters.IndicatorAdapter;
import com.songjachin.himalaya.adapters.MainContentAdapter;
import com.songjachin.himalaya.base.BaseApplication;
import com.songjachin.himalaya.indicators.CommonNavigator;
import com.songjachin.himalaya.indicators.CommonNavigatorAdapter;
import com.songjachin.himalaya.indicators.IPagerIndicator;
import com.songjachin.himalaya.indicators.IPagerTitleView;
import com.songjachin.himalaya.indicators.LinePagerIndicator;
import com.songjachin.himalaya.indicators.MagicIndicator;
import com.songjachin.himalaya.indicators.SimplePagerTitleView;
import com.songjachin.himalaya.indicators.ViewPagerHelper;
import com.songjachin.himalaya.interfaces.IPlayerCallback;
import com.songjachin.himalaya.presenters.PlayerPresenter;
import com.songjachin.himalaya.presenters.RecommendPresenter;
import com.songjachin.himalaya.utils.LogUtil;
import com.songjachin.himalaya.views.RoundRectImageView;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.List;


public class MainActivity extends FragmentActivity implements IPlayerCallback {
    private static final String TAG = "MainActivity";
    private MagicIndicator mMagicIndicator;
    private ViewPager mContentPager;
    private IndicatorAdapter mIndicatorAdapter;
    private RoundRectImageView mRoundRectImageView;
    private TextView mHeaderTitle;
    private TextView mSubTitle;
    private ImageView mPlayControl;
    private PlayerPresenter mPlayerPresenter;
    private View mPlayControlItem;
    private View mSearchBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
        //
        initPresenter();
    }

    private void initPresenter() {
        mPlayerPresenter = PlayerPresenter.getInstance();
        mPlayerPresenter.registerViewCallback(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unregisterViewCallback(this);
        }

    }

    private void initEvent() {
        mIndicatorAdapter.setOnIndicatorTabClickListener(new IndicatorAdapter.OnIndicatorTabClickListener() {
            @Override
            public void onTabClick(int index) {
                LogUtil.d(TAG, "on click ---> " + index);
                if (mContentPager != null) {
                    mContentPager.setCurrentItem(index);
                }
            }
        });

        mPlayControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    boolean hasPlayList = mPlayerPresenter.hasPlayList();
                    if (!hasPlayList) {
                        //没有设置过播放列表，我们就播放默认的第一个推荐专辑
                        //第一个推荐专辑，每天都会变的。
                        playFirstRecommend();
                    } else {
                        if (mPlayerPresenter.isPlaying()) {
                            mPlayerPresenter.pause();
                        } else {
                            mPlayerPresenter.play();
                        }
                    }
                }
            }
        });

        mPlayControlItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    boolean hasPlayList = mPlayerPresenter.hasPlayList();
                    if (!hasPlayList) {
                        playFirstRecommend();
                    } else {
                        //跳转到播放器界面
                        startActivity(new Intent(MainActivity.this, PlayActivity.class));
                    }
                }
            }
        });

        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SearchActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * 播放第一个推荐的内容.
     */
    private void playFirstRecommend() {
        List<Album> currentRecommend = RecommendPresenter.getsInstance().getCurrentRecommend();
        if (currentRecommend != null && currentRecommend.size() > 0) {
            Album album = currentRecommend.get(0);
            long albumId = album.getId();
            mPlayerPresenter.playByAlbumId(albumId);
        }
    }

    private void initView() {
        mMagicIndicator = this.findViewById(R.id.main_indicator);
        mMagicIndicator.setBackgroundColor(this.getResources().getColor(R.color.main_color));
        //创建indicator的adapter
        mIndicatorAdapter = new IndicatorAdapter(this);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(mIndicatorAdapter);
        commonNavigator.setAdjustMode(true);


        //ViewPager
        mContentPager = this.findViewById(R.id.content_pager);
        //make the viewpager ..adapter
        FragmentManager fragmentManager = getSupportFragmentManager();
        MainContentAdapter contentAdapter = new MainContentAdapter(fragmentManager);//Fragment和page在这里绑定的
        mContentPager.setAdapter(contentAdapter);
        //bind the viewPager and the indicator
        mMagicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mMagicIndicator, mContentPager);//fragment page 和 indicator绑定

        //播放控制相关的
        mRoundRectImageView = this.findViewById(R.id.main_track_cover);
        mHeaderTitle = this.findViewById(R.id.main_head_title);
        mHeaderTitle.setSelected(true);
        mSubTitle = this.findViewById(R.id.main_sub_title);
        mPlayControl = this.findViewById(R.id.main_play_control);
        mPlayControlItem = this.findViewById(R.id.main_play_control_item);
        //Search
        mSearchBtn = this.findViewById(R.id.search_btn);
    }

    private void updatePlayControl(boolean isPlaying) {
        if (mPlayControl != null) {
            mPlayControl.setImageResource(isPlaying ? R.drawable.selector_palyer_pause : R.drawable.selector_palyer_play);
        }
    }

    @Override
    public void onPlayStart() {
        updatePlayControl(true);
    }

    @Override
    public void onPlayPause() {
        updatePlayControl(false);

    }

    @Override
    public void onPlayStop() {
        updatePlayControl(false);

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
            String trackTitle = track.getTrackTitle();
            String nickname = track.getAnnouncer().getNickname();
            String coverUrlMiddle = track.getCoverUrlMiddle();
            LogUtil.d(TAG, "trackTitle -- > " + trackTitle);
            if (mHeaderTitle != null) {
                mHeaderTitle.setText(trackTitle);
            }
            LogUtil.d(TAG, "nickname -- > " + nickname);
            if (mSubTitle != null) {
                mSubTitle.setText(nickname);
            }
            LogUtil.d(TAG, "coverUrlMiddle -- > " + coverUrlMiddle);
            Picasso.get().load(coverUrlMiddle).into(mRoundRectImageView);
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }
}




