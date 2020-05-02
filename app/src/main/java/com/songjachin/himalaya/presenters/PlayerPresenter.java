package com.songjachin.himalaya.presenters;

import com.songjachin.himalaya.base.BaseApplication;
import com.songjachin.himalaya.interfaces.IPlayerCallback;
import com.songjachin.himalaya.interfaces.IPlayerPresenter;
import com.songjachin.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.PlayableModel;
import com.ximalaya.ting.android.opensdk.model.advertis.Advertis;
import com.ximalaya.ting.android.opensdk.model.advertis.AdvertisList;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.XmPlayerManager;
import com.ximalaya.ting.android.opensdk.player.advertis.IXmAdsStatusListener;
import com.ximalaya.ting.android.opensdk.player.constants.PlayerConstants;
import com.ximalaya.ting.android.opensdk.player.service.IXmPlayerStatusListener;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayerException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matthew on 2020/4/29 13:11
 * day day up!
 */
public class PlayerPresenter implements IPlayerPresenter, IXmAdsStatusListener, IXmPlayerStatusListener {
    private static final String TAG = "PlayerPresenter";

    private List<IPlayerCallback> mPlayerCallbacks = new ArrayList<>();
    private XmPlayerManager mPlayerManager;
    private Track mCurrentTrack;
    private int mCurrentIndex = 0;

    private PlayerPresenter() {
        mPlayerManager = XmPlayerManager.getInstance(BaseApplication.getAppContext());
        //广告相关的接口
        mPlayerManager.addAdsStatusListener(this);
        //播放相关的接口
        mPlayerManager.addPlayerStatusListener(this);
    }

    private static PlayerPresenter sPlayerPresenter;

    public static PlayerPresenter getInstance() {
        if (sPlayerPresenter == null) {
            synchronized (PlayerPresenter.class) {
                if (sPlayerPresenter == null) {
                    sPlayerPresenter = new PlayerPresenter();
                }
            }
        }

        return sPlayerPresenter;
    }

    private boolean isPlayListSet = false;

    public void setPlayList(List<Track> list, int playIndex) {
        if (mPlayerManager != null) {
            mPlayerManager.setPlayList(list, playIndex);
            isPlayListSet = true;
            mCurrentTrack = list.get(playIndex);
            mCurrentIndex = playIndex;
        } else {
            LogUtil.e(TAG, "mPlayerManager is null");
        }
    }

    //=========================================IPlayPresenter=====================================
    @Override
    public void play() {
        if (mPlayerManager != null && isPlayListSet) {
            mPlayerManager.play();
        }
    }

    @Override
    public void pause() {
        if (mPlayerManager != null) {
            mPlayerManager.pause();
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public void playPre() {
        if (mPlayerManager != null) {
            mPlayerManager.playPre();
        }
    }

    @Override
    public void playNext() {
        if (mPlayerManager != null) {
            mPlayerManager.playNext();
        }

    }

    @Override
    public void switchPlayMode(XmPlayListControl.PlayMode mode) {

    }

    @Override
    public void getPlayList() {
        if (mPlayerManager != null) {
            List<Track> playList = mPlayerManager.getPlayList();
            for (IPlayerCallback iPlayerCallback : mPlayerCallbacks) {
                iPlayerCallback.onListLoaded(playList);
            }
        }
    }

    @Override
    public void playByIndex(int index) {
        //切换播放器到第几
        if (mPlayerManager != null) {
            mPlayerManager.play(index);
        }

    }

    @Override
    public void seekTo(int progress) {
        mPlayerManager.seekTo(progress);
    }

    @Override
    public boolean isPlaying() {
        return mPlayerManager.isPlaying();
    }

    @Override
    public void registerViewCallback(IPlayerCallback iPlayerCallback) {
        if (mPlayerCallbacks != null && !mPlayerCallbacks.contains(iPlayerCallback)) {
            mPlayerCallbacks.add(iPlayerCallback);
        }
        //通知UI的pager变化
        getPlayList();
        //通知节目的变化
        iPlayerCallback.onTrackUpdate(mCurrentTrack, mCurrentIndex);

    }

    @Override
    public void unregisterViewCallback(IPlayerCallback iPlayerCallback) {
        if (mPlayerCallbacks != null) {
            mPlayerCallbacks.remove(iPlayerCallback);
        }
    }
    //=========================================IPlayerPresenter end=======================================

    //======================================== 广告相关接口 start =================================
    @Override
    public void onStartGetAdsInfo() {
        LogUtil.d(TAG, "onStartGetAdsInfo");
    }

    @Override
    public void onGetAdsInfo(AdvertisList advertisList) {
        LogUtil.d(TAG, "onGetAdsInfo");
    }

    @Override
    public void onAdsStartBuffering() {
        LogUtil.d(TAG, "onAdsStartBuffering");
    }

    @Override
    public void onAdsStopBuffering() {
        LogUtil.d(TAG, "onAdsStopBuffering");
    }

    @Override
    public void onStartPlayAds(Advertis advertis, int i) {
        LogUtil.d(TAG, "onStartPlayAds");
    }

    @Override
    public void onCompletePlayAds() {
        LogUtil.d(TAG, "onCompletePlayAds");
    }

    @Override
    public void onError(int what, int extra) {
        LogUtil.d(TAG, "onError(int i, int i1) what " + what + " extra " + extra);
    }
    //===========================================Ads end=================================================


    //===========================================播放器接口start==========================================
    @Override
    public void onPlayStart() {
        LogUtil.d(TAG, " onPlayStart() ");
        for (IPlayerCallback playerCallback : mPlayerCallbacks) {
            playerCallback.onPlayStart();
        }
    }

    @Override
    public void onPlayPause() {
        LogUtil.d(TAG, " onPlayPause ");
        for (IPlayerCallback playerCallback : mPlayerCallbacks) {
            playerCallback.onPlayPause();
        }
    }

    @Override
    public void onPlayStop() {
        LogUtil.d(TAG, "  onPlayStop ");
        for (IPlayerCallback playerCallback : mPlayerCallbacks) {
            playerCallback.onPlayStop();
        }
    }

    @Override
    public void onSoundPlayComplete() {
        LogUtil.d(TAG, " onSoundPlayComplete ");
    }

    @Override
    public void onSoundPrepared() {
        LogUtil.d(TAG, " onSoundPrepared ");
        //mPlayerManager.setPlayMode(mCurrentPlayMode);
        if(mPlayerManager.getPlayerStatus() == PlayerConstants.STATE_PREPARED) {
            //播放器准备完了，可以去播放了
            mPlayerManager.play();
        }
    }

    @Override
    public void onSoundSwitch(PlayableModel lastModel, PlayableModel curModel) {
        LogUtil.d(TAG, "onSoundSwitch...");
        if (lastModel != null) {
            LogUtil.d(TAG, "lastModel..." + lastModel.getKind());
        }
        if (curModel != null) {
            LogUtil.d(TAG, "curModel..." + curModel.getKind());
        }
        //curModel代表的是当前播放的内容
        //通过getKind()方法来获取它是什么类型的
        //track表示是track类型
        //第一种写法：不推荐
        //if ("track".equals(curModel.getKind())) {
        //    Track currentTrack = (Track) curModel;
        //    LogUtil.d(TAG, "title == > " + currentTrack.getTrackTitle());
        //}
        //第二种写法

        if (curModel instanceof Track) {
            Track currentTrack = (Track) curModel;
            mCurrentTrack = currentTrack;
            mCurrentIndex = mPlayerManager.getCurrentIndex();
            //保存播放记录

            LogUtil.d(TAG, "title =-= > " + currentTrack.getTrackTitle());
            //更新UI
            for (IPlayerCallback iPlayerCallback : mPlayerCallbacks) {
                iPlayerCallback.onTrackUpdate(mCurrentTrack, mCurrentIndex);
            }
        }
    }

    @Override
    public void onBufferingStart() {
        LogUtil.d(TAG, " onBufferingStart ");
    }

    @Override
    public void onBufferingStop() {
        LogUtil.d(TAG, " onBufferingStop ");
    }

    @Override
    public void onBufferProgress(int i) {
        LogUtil.d(TAG, "  onBufferProgress ----> " + i);
    }

    @Override
    public void onPlayProgress(int currentPos, int duration) {
        //单位是毫秒
        //LogUtil.d(TAG, " onPlayProgress currentPosition----->"+currentPos+"  duration--->"+ duration);
        for (IPlayerCallback playerCallback : mPlayerCallbacks) {
            playerCallback.onProgressChange(currentPos, duration);
        }
    }

    @Override
    public boolean onError(XmPlayerException e) {
        LogUtil.d(TAG, " onError ");
        return false;
    }
    //======================================== end =========================================
}
