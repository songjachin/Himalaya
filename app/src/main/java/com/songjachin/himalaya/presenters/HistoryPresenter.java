package com.songjachin.himalaya.presenters;

import com.songjachin.himalaya.base.BaseApplication;
import com.songjachin.himalaya.constants.Constants;
import com.songjachin.himalaya.data.HistoryDao;
import com.songjachin.himalaya.data.IHistoryDaoCallback;
import com.songjachin.himalaya.interfaces.IHistoryCallback;
import com.songjachin.himalaya.interfaces.IHistoryPresenter;
import com.songjachin.himalaya.utils.LogUtil;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by matthew on 2020/5/17 17:00
 * day day up!
 */
public class HistoryPresenter implements IHistoryPresenter, IHistoryDaoCallback {
    private static final String TAG = "HistoryPresenter";
    private List<IHistoryCallback> mHistoryCallbacks= new ArrayList<>();
    private final HistoryDao mHistoryDao;
    private List<Track> mCurrentHistories = new ArrayList<>();
    private Track mCurrentAddTrack = null;

    private HistoryPresenter(){
        mHistoryDao = HistoryDao.getInstance();
        mHistoryDao.setCallback(this);
    }

    private static HistoryPresenter sInstance = null;

    public static HistoryPresenter getInstance(){
        if (sInstance==null) {
            synchronized (HistoryPresenter.class){
                if (sInstance == null) {
                    sInstance = new HistoryPresenter();
                }
            }
        }

        return sInstance;
    }

    @Override
    public void listHistories() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if(mHistoryDao != null) {
                    mHistoryDao.listHistories();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    private  boolean isDoDelAsOutOfSize = false;
    @Override
    public void addHistory(final Track track) {
        //需要去判断是否>=100条记录
        if(mCurrentHistories != null && mCurrentHistories.size() >= Constants.MAX_HISTORY_COUNT) {
            isDoDelAsOutOfSize = true;
            this.mCurrentAddTrack = track;
            //先不能添加，先删除最前的一条记录，再添加
            delHistory(mCurrentHistories.get(mCurrentHistories.size() - 1));
        } else {
            doAddHistory(track);
        }
    }

    private void doAddHistory(final Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if(mHistoryDao != null) {
                    mHistoryDao.addHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void delHistory(final Track track) {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if(mHistoryDao != null) {
                    mHistoryDao.delHistory(track);
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void cleanHistories() {
        Observable.create(new ObservableOnSubscribe<Object>() {
            @Override
            public void subscribe(ObservableEmitter<Object> emitter) throws Exception {
                if(mHistoryDao != null) {
                    mHistoryDao.clearHistory();
                }
            }
        }).subscribeOn(Schedulers.io()).subscribe();
    }

    @Override
    public void registerViewCallback(IHistoryCallback iHistoryCallback) {
        if (mHistoryCallbacks != null&& !mHistoryCallbacks.contains(iHistoryCallback)) {
            mHistoryCallbacks.add(iHistoryCallback);
        }
    }

    @Override
    public void unregisterViewCallback(IHistoryCallback iHistoryCallback) {
        if (mHistoryCallbacks != null) {
            mHistoryCallbacks.remove(iHistoryCallback);
        }
    }

    @Override
    public void onHistoryAdd(boolean isSuccess) {
        //nothing to do.
        listHistories();
        //跟subscription不同，这里不需要回调UI显示“添加成功”
    }

    @Override
    public void onHistoryDel(boolean isSuccess) {
        //nothing to do.
        if(isDoDelAsOutOfSize && mCurrentAddTrack != null) {
            isDoDelAsOutOfSize = false;
            //添加当前的数据进到数据库里
            addHistory(mCurrentAddTrack);
        } else {
            listHistories();
        }
    }

    @Override
    public void onHistoriesLoaded(final List<Track> tracks) {
        //此时在IO子线程
        this.mCurrentHistories = tracks;
        LogUtil.d(TAG,"histories size -- > " + tracks.size());

        //通知UI更新数据
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                for(IHistoryCallback callback : mHistoryCallbacks) {
                    callback.onHistoriesLoaded(tracks);
                }
            }
        });
    }

    @Override
    public void onHistoriesClean(boolean isSuccess) {
        //nothing to do.
        listHistories();
    }
}
