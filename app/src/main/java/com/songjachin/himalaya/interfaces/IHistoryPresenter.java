package com.songjachin.himalaya.interfaces;

import com.songjachin.himalaya.base.IBasePresenter;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * Created by matthew on 2020/5/11 20:16
 * day day up!
 */
public interface IHistoryPresenter extends IBasePresenter<IHistoryCallback> {

    /**
     * 获取历史内容.
     */
    void listHistories();

    /**
     * 添加历史
     *
     * @param track
     */
    void addHistory(Track track);

    /**
     * 删除历史
     *
     * @param track
     */
    void delHistory(Track track);

    /**
     * 清除历史
     */
    void cleanHistories();

}
