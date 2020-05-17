package com.songjachin.himalaya;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.viewpager.widget.ViewPager;

import com.songjachin.himalaya.adapters.PlayerTrackPagerAdapter;
import com.songjachin.himalaya.base.BaseActivity;
import com.songjachin.himalaya.interfaces.IPlayerCallback;
import com.songjachin.himalaya.presenters.PlayerPresenter;
import com.songjachin.himalaya.views.SobPopWindow;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_LIST_LOOP;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_RANDOM;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE;
import static com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl.PlayMode.PLAY_MODEL_SINGLE_LOOP;

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
    private ImageView mSwitchModeBtn;

    private XmPlayListControl.PlayMode mCurrentMode = PLAY_MODEL_LIST;

    //
    private static Map<XmPlayListControl.PlayMode, XmPlayListControl.PlayMode> sPlayModeRule = new HashMap<>();


    //处理播放模式的切换//
    //1、默认的是：PLAY_MODEL_LIST
    //2、列表循环：PLAY_MODEL_LIST_LOOP
    //3、随机播放：PLAY_MODEL_RANDOM
    //4、单曲循环：PLAY_MODEL_SINGLE_LOOP
    static {
        sPlayModeRule.put(PLAY_MODEL_LIST, PLAY_MODEL_LIST_LOOP);
        sPlayModeRule.put(PLAY_MODEL_LIST_LOOP, PLAY_MODEL_RANDOM);
        sPlayModeRule.put(PLAY_MODEL_RANDOM, PLAY_MODEL_SINGLE_LOOP);
        sPlayModeRule.put(PLAY_MODEL_SINGLE_LOOP, PLAY_MODEL_LIST);
    }

    private ImageView mPlayListBtn;
    private SobPopWindow mSobPopWindow;
    private ValueAnimator mEnterBgAnimator;
    private ValueAnimator mOutBgAnimator;
    public final int BG_ANIMATION_DURATION = 500;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initView();
        initStatus();
        //TODO:
        mPlayerPresenter = PlayerPresenter.getInstance();
        mPlayerPresenter.registerViewCallback(this);
        //playerPresenter.play();

        mPlayerPresenter.getPlayList();
        initEvent();
        initBgAnimation();
    }

    private void initStatus() {

    }

    private void initBgAnimation() {
        mEnterBgAnimator = ValueAnimator.ofFloat(1.0f, 0.7f);
        mEnterBgAnimator.setDuration(BG_ANIMATION_DURATION);
        mEnterBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                //处理一下背景，有点透明度
                updateWindowAlpha(value);
            }
        });
        //退出的
        mOutBgAnimator = ValueAnimator.ofFloat(0.7f, 1.0f);
        mOutBgAnimator.setDuration(BG_ANIMATION_DURATION);
        mOutBgAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                updateWindowAlpha(value);
            }
        });
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
                if (fromUser) {
                    mCurrentProgress = progress;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mIsUserTouchSeekBar = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mIsUserTouchSeekBar = false;
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

        mSwitchModeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switchPlayMode();
            }
        });

        mPlayListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //播放列表展开
                mSobPopWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
                mEnterBgAnimator.start();
            }
        });

        mSobPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                mOutBgAnimator.start();
            }
        });

        mSobPopWindow.setPlayListItemListener(new SobPopWindow.PlayListItemListener() {
            @Override
            public void onItemClick(int position) {
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.playByIndex(position);
                }
            }
        });

        mSobPopWindow.setPlayListActionListener(new SobPopWindow.PlayListActionListener() {
            @Override
            public void onPlayModeClick() {
                //切换播放模式
                switchPlayMode();
            }

            @Override
            public void onOrderClick() {
                //点击了切换顺序和逆序
                //Toast.makeText(PlayerActivity.this, "切换列表书序", Toast.LENGTH_SHORT).show();
                if (mPlayerPresenter != null) {
                    mPlayerPresenter.reversePlayList();
                }
            }
        }) ;
 // ===========================initEvent()=======================================
    }


    private void updateWindowAlpha(float alpha){
        Window window = getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.alpha = alpha;
        window.setAttributes(attributes);
    }

    private void switchPlayMode() {
        //根据当前的mode获取到下一个mode
        XmPlayListControl.PlayMode playMode = sPlayModeRule.get(mCurrentMode);
        //修改播放模式
        if (mPlayerPresenter != null) {
            mPlayerPresenter.switchPlayMode(playMode);
        }
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

        mSwitchModeBtn = this.findViewById(R.id.player_mode_switch_btn);

        mPlayListBtn = this.findViewById(R.id.player_list);

        mSobPopWindow = new SobPopWindow();
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
        if (mSobPopWindow != null) {
            mSobPopWindow.setData(list);
        }
    }

    @Override
    public void onPlayModeChange(XmPlayListControl.PlayMode playMode) {
        //更新播放模式,并且修改UI.
        mCurrentMode = playMode;
        //更新pop里的播放模式
        mSobPopWindow.updatePlayMode(mCurrentMode);
        updatePlayModeBtnImg();

    }

    private void updatePlayModeBtnImg() {
        int resId = R.drawable.selector_play_mode_list_order;
        switch (mCurrentMode) {
            case PLAY_MODEL_LIST:
                resId = R.drawable.selector_play_mode_list_order;
                break;
            case PLAY_MODEL_RANDOM:
                resId = R.drawable.selector_paly_mode_random;
                break;
            case PLAY_MODEL_LIST_LOOP:
                resId = R.drawable.selector_paly_mode_list_order_looper;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                resId = R.drawable.selector_paly_mode_single_loop;
                break;
        }
        mSwitchModeBtn.setImageResource(resId);
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
        //播放器里的track update时调用
        mTrackTitleText = track.getTrackTitle();
        if (mTrackTitle != null) {
            mTrackTitle.setText(mTrackTitleText);
        }
        //track update时去修改viewpager的图片
        if (mTrackViewPager != null) {
            mTrackViewPager.setCurrentItem(index,true);
        }
        //todo:
        if (mSobPopWindow != null) {
            mSobPopWindow.setTrackIndex(index);
        }
    }

    @Override
    public void updateListOrder(boolean isReverse) {
        mSobPopWindow.updateOrderIcon(isReverse);
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
