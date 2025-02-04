package com.songjachin.himalaya.interfaces;

import com.songjachin.himalaya.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

/**
 * Created by matthew on 2020/4/29 11:28
 * day day up!
 */
public interface IPlayerPresenter extends IBasePresenter<IPlayerCallback> {

    /**
     * 播放
     */
    void play();

    /**
     * 暂停
     */
    void pause();

    /**
     * 停止播放
     */
    void stop();

    /**
     * 上一首
     */
    void playPre();

    /**
     * 播放下一首
     */
    void playNext();

    /**
     * 切换播放模式
     *
     * @param mode
     */
    void switchPlayMode(XmPlayListControl.PlayMode mode);


    /**
     * 获取播放列表
     */
    void getPlayList();


    /**
     * 根据节目的位置进行播放
     *
     * @param index 节目在列表中的位置
     */
    void playByIndex(int index);


    /**
     * 切换播放进度
     *
     * @param progress
     */
    void seekTo(int progress);

    /**
     * 判断播放器是否在播放
     *
     * @return
     */
    boolean isPlaying();


    /**
     * 把播放器列表内容翻转
     */
    void reversePlayList();


    /**
     * 播放专辑的第一首节目。
     *
     * @param id
     */
    void playByAlbumId(long id);

}
