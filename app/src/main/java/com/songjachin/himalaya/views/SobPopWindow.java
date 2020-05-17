package com.songjachin.himalaya.views;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.songjachin.himalaya.R;
import com.songjachin.himalaya.adapters.PlayListAdapter;
import com.songjachin.himalaya.base.BaseApplication;
import com.ximalaya.ting.android.opensdk.model.track.Track;
import com.ximalaya.ting.android.opensdk.player.service.XmPlayListControl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matthew on 2020/5/2 18:13
 * day day up!
 */
public class SobPopWindow extends PopupWindow {

    private final View mPopView;
    private TextView mCloseTextView;
    private RecyclerView mPlayListRv;

    private PlayListAdapter mPlayListAdapter;
    private TextView mPlayModeTv;
    private ImageView mPlayModeIv;
    private View mPlayModeContainer;
    private PlayListActionListener mPlayModeClickListener = null;

    private View mOrderBtnContainer;
    private ImageView mOrderIcon;
    private TextView mOrderText;

    public SobPopWindow() {
        //设置它宽高

        super(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        //这里要注意：设置setOutsideTouchable之前，先要设置：setBackgroundDrawable,
        //否则点击外部无法关闭pop.
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(true);
        //载进来View
        mPopView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.pop_play_list, null);
        //设置内容
        setContentView(mPopView);
        //设置窗口进入和退出的动画
        setAnimationStyle(R.style.pop_animation);
        initView();
        inEvent();
    }

    private void initView() {
        mCloseTextView = mPopView.findViewById(R.id.play_list_close_btn);
        mPlayListRv = mPopView.findViewById(R.id.play_list_rv);
        LinearLayoutManager layoutManager = new LinearLayoutManager(BaseApplication.getAppContext());
        mPlayListRv.setLayoutManager(layoutManager);
        mPlayListAdapter = new PlayListAdapter();
        mPlayListRv.setAdapter(mPlayListAdapter);

        //播放模式相关
        mPlayModeTv = mPopView.findViewById(R.id.play_list_play_mode_tv);
        mPlayModeIv = mPopView.findViewById(R.id.play_list_play_mode_iv);
        mPlayModeContainer = mPopView.findViewById(R.id.play_list_play_mode_container);

        //列表顺序或逆序
        mOrderBtnContainer = mPopView.findViewById(R.id.play_list_order_container);
        mOrderIcon = mPopView.findViewById(R.id.play_list_order_iv);
        mOrderText = mPopView.findViewById(R.id.play_list_order_tv);
    }

    private void inEvent() {
        mCloseTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SobPopWindow.this.dismiss();
            }
        });

        mPlayModeContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换播放模式
                if (mPlayModeClickListener != null) {
                    mPlayModeClickListener.onPlayModeClick();
                }
            }
        });

        mOrderBtnContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //切换播放列表为顺序或者逆序
                mPlayModeClickListener.onOrderClick();
            }
        });

    }//initEvent


    public void setData(List<Track> list) {
        if (mPlayListAdapter != null) {
            mPlayListAdapter.setData(list);
        }
    }
    public void setTrackIndex(int index){
        if (mPlayListAdapter != null) {
            mPlayListAdapter.setCurrentPlayPosition(index);
            mPlayListRv.scrollToPosition(index);
        }

    }

    /**
     * 更新播放列表播放模式
     *
     * @param currentMode
     */
    public void updatePlayMode(XmPlayListControl.PlayMode currentMode) {
        updatePlayModeBtnImg(currentMode);
    }

    /**
     * 更新切换列表顺序和逆序的按钮和文字更新
     *
     * @param isReverse
     */
    public void updateOrderIcon(boolean isReverse) {
        mOrderIcon.setImageResource(isReverse ? R.drawable.selector_play_mode_list_order : R.drawable.selector_play_mode_list_revers);
        mOrderText.setText(BaseApplication.getAppContext().getResources().getString(isReverse ? R.string.order_text : R.string.revers_text));
    }

    /**
     * 根据当前的状态，更新播放模式图标
     * PLAY_MODEL_LIST
     * PLAY_MODEL_LIST_LOOP
     * PLAY_MODEL_RANDOM
     * PLAY_MODEL_SINGLE_LOOP
     */
    private void updatePlayModeBtnImg(XmPlayListControl.PlayMode playMode) {
        int resId = R.drawable.selector_play_mode_list_order;
        int textId = R.string.play_mode_order_text;
        switch (playMode) {
            case PLAY_MODEL_LIST:
                resId = R.drawable.selector_play_mode_list_order;
                textId = R.string.play_mode_order_text;
                break;
            case PLAY_MODEL_RANDOM:
                resId = R.drawable.selector_paly_mode_random;
                textId = R.string.play_mode_random_text;
                break;
            case PLAY_MODEL_LIST_LOOP:
                resId = R.drawable.selector_paly_mode_list_order_looper;
                textId = R.string.play_mode_list_play_text;
                break;
            case PLAY_MODEL_SINGLE_LOOP:
                resId = R.drawable.selector_paly_mode_single_loop;
                textId = R.string.play_mode_single_play_text;
                break;
        }
        mPlayModeIv.setImageResource(resId);
        mPlayModeTv.setText(textId);
    }


    public void setPlayListItemListener(PlayListItemListener listener){
        mPlayListAdapter.setOnItemClickListener(listener);
    }

    public interface PlayListItemListener{
        void onItemClick(int position);
    }

    public void setPlayListActionListener(PlayListActionListener playModeListener) {
        mPlayModeClickListener = playModeListener;
    }

    public interface PlayListActionListener {

        //播放模式被点击了
        void onPlayModeClick();

        //播放逆序或者顺序切换按钮被点击了
        void onOrderClick();
    }
}
