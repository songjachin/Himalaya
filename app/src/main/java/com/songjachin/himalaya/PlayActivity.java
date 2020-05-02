package com.songjachin.himalaya;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.songjachin.himalaya.adapters.PlayerTrackPagerAdapter;
import com.songjachin.himalaya.base.BaseActivity;
import com.songjachin.himalaya.interfaces.IPlayerCallback;
import com.songjachin.himalaya.presenters.PlayerPresenter;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by matthew on 2020/4/28 18:21
 * day day up!
 */
public class PlayActivity extends BaseActivity implements IPlayerCallback, ViewPager.OnPageChangeListener {
    private PlayerPresenter mPlayerPresenter;
    private ImageView mPlayControlBtn;
    private SimpleDateFormat  mMinFormat = new SimpleDateFormat("mm:ss");
    private SimpleDateFormat  mHoursFormat = new SimpleDateFormat("hh:mm:ss");
    private TextView mCurrentPosTv;
    private TextView mTrackDurationTv;
    private SeekBar mDurationBar;
    private int mCurrentProgress = 0;
    private boolean mIsUserTouchSeekBar = false;
    private ImageView mPlayNextBtn;
    private ImageView mPlayPreBtn;
    private TextView mTrackTitle;
    private String mTrackTitleText;
    private PlayerTrackPagerAdapter mTrackPagerAdapter;
    private ViewPager mTrackViewPager;
    private boolean mIsUserSlidePager =false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        //TODO:
        mPlayerPresenter = PlayerPresenter.getInstance();
        mPlayerPresenter.registerViewCallback(this);
        //playerPresenter.play();
        initView();
        mPlayerPresenter.getPlayList();
        initEvent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerPresenter != null) {
            mPlayerPresenter.unregisterViewCallback(this);
            mPlayerPresenter = null;
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private void initEvent() {
        mPlayControlBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果现在的状态是正在播放的,那么就暂停
                //todo:
                if (mPlayerPresenter.isPlaying()) {
                    mPlayerPresenter.pause();
                } else {
                    //如果现在的状态是非播放的,那么我们就让播放器播放节目
                    mPlayerPresenter.play();
                }
            }
        });

        mDurationBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mCurrentProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsUserTouchSeekBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mIsUserTouchSeekBar =false;
                //手离开时做更新
                mPlayerPresenter.seekTo(mCurrentProgress);
            }
        });

        mPlayPreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playPre();
                }
            }
        });

        mPlayNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playNext();
                }
            }
        });

        mTrackViewPager.addOnPageChangeListener(this);

        /**
         * note:是否是点击viewpager的动作
         */
        mTrackViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        mIsUserSlidePager = true;
                        break;
                }
                return false;
            }
        });
    }

    private void initView() {
        mPlayControlBtn = this.findViewById(R.id.play_or_pause_btn);
        mCurrentPosTv = this.findViewById(R.id.current_position);
        mTrackDurationTv = this.findViewById(R.id.track_duration);
        mDurationBar = this.findViewById(R.id.track_seek_bar);
        mPlayPreBtn = this.findViewById(R.id.play_pre);
        mPlayNextBtn = this.findViewById(R.id.play_next);
        mTrackTitle = this.findViewById(R.id.track_title);
        if (!TextUtils.isEmpty(mTrackTitleText)) {
            mTrackTitle.setText(mTrackTitleText);
        }
        mTrackViewPager = this.findViewById(R.id.track_pager_view);
        //创建适配器
        mTrackPagerAdapter = new PlayerTrackPagerAdapter();
        //设置适配器
        mTrackViewPager.setAdapter(mTrackPagerAdapter);
    }
   //=====================================   IPlayViewCallBack  ==================================
    @Override
    public void onPlayStart() {
        //开始播放，修改UI成暂停的按钮
        if (mPlayControlBtn != null) {
            mPlayControlBtn.setImageResource(R.drawable.selector_palyer_pause);
        }
    }

    @Override
    public void onPlayPause() {
        if (mPlayControlBtn != null) {
            mPlayControlBtn.setImageResource(R.drawable.selector_palyer_play);
        }
    }

    @Override
    public void onPlayStop() {
        if (mPlayControlBtn != null) {
            mPlayControlBtn.setImageResource(R.drawable.selector_palyer_play);
        }
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
        //LogUtil.d(TAG, "list -- > " + list);
        //把数据设置到适配器里
        if (mTrackPagerAdapter != null) {
            mTrackPagerAdapter.setData(list);
        }
    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {

    }

    @Override
    public void onProgressChange(int currentProgress, int total) {
        mDurationBar.setMax(total);
        String totalDuration;
        String currentPos;
        if(total > 1000*60*60){
            totalDuration = mHoursFormat.format(total);
            currentPos = mHoursFormat.format(currentProgress);
        }else{
            totalDuration = mMinFormat.format(total);
            currentPos = mMinFormat.format(currentProgress);
        }
        if (mTrackDurationTv != null) {
            mTrackDurationTv.setText(totalDuration);
        }
        if (mCurrentPosTv != null) {
            mCurrentPosTv.setText(currentPos);
        }
        //更新进度条seekBar
        if (!mIsUserTouchSeekBar) {
            if (mDurationBar != null) {
                mDurationBar.setProgress(currentProgress);
            }
        }


    }

    @Override
    public void onAdLoading() {

    }

    @Override
    public void onAdFinished() {

    }

    @Override
    public void onTrackUpdate(Track track, int index) {
        mTrackTitleText = track.getTrackTitle();
        if (mTrackTitle != null) {
            mTrackTitle.setText(mTrackTitleText);
        }
        //track update时去修改viewpager的图片
        if (mTrackViewPager != null) {
            mTrackViewPager.setCurrentItem(index,true);
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {

    }
    //=================================================  PlayCallback end  ======================

//==================================ViewPager listener
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //viewpager改变时去修改play的track且此时是用户行为
        if (mPlayerPresenter != null&&mIsUserSlidePager) {
            mPlayerPresenter.playByIndex(position);
        }
        mIsUserSlidePager =false;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
