package com.songjachin.himalaya.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.songjachin.himalaya.R;
import com.songjachin.himalaya.base.BaseApplication;

/**
 * Created by matthew on 2020/4/25 15:25
 * day day up!
 */
public abstract class UILoader extends FrameLayout {

    private View mLoadingView;
    private View mSuccessView;
    private View mEmptyView;
    private View mErrorView;
    private OnRetryClickListener mOnRetryClickListener;

    public enum UIStatus{
        LOADING,SUCCESS,NETWORK_ERROR,EMPTY,NONE
    }

    public UIStatus mCurrentStatus = UIStatus.NONE;

    public UILoader(@NonNull Context context) {
        this(context,null);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public UILoader(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //
        init();
    }

    private void init() {
        switchUIByCurrentStatus();
    }

    public void updateStatus(UIStatus status){
        mCurrentStatus = status;
        //use handler to update ui
        BaseApplication.getHandler().post(new Runnable() {
            @Override
            public void run() {
                switchUIByCurrentStatus();
            }
        });
    }

    private void switchUIByCurrentStatus() {
        //loading view
        if (mLoadingView == null) {
            mLoadingView = getLoadingView();
            addView(mLoadingView);
        }
        //
        mLoadingView.setVisibility(mCurrentStatus==UIStatus.LOADING?VISIBLE:GONE);

        //when success
        if (mSuccessView == null) {
            mSuccessView = getSuccessView(this);
            addView(mSuccessView);
        }
        //
        mSuccessView.setVisibility(mCurrentStatus==UIStatus.SUCCESS?VISIBLE:GONE);


        if (mErrorView == null) {
            mErrorView = getErrorView();
            addView(mErrorView);
        }
        //
        mErrorView.setVisibility(mCurrentStatus==UIStatus.NETWORK_ERROR?VISIBLE:GONE);


        if (mEmptyView == null) {
            mEmptyView = getEmptyView();
            addView(mEmptyView);
        }
        //
        mEmptyView.setVisibility(mCurrentStatus==UIStatus.EMPTY?VISIBLE:GONE);
    }

    private View getEmptyView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view, this,false);
    }

    private View getErrorView() {
        View viewNetworkError = LayoutInflater.from(getContext()).inflate(R.layout.fragment_error_view, this,false);
        viewNetworkError.findViewById(R.id.network_error_icon).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //get data again
                if (mOnRetryClickListener != null) {
                    mOnRetryClickListener.onRetryClick();
                }
            }
        });
        return viewNetworkError;
    }

    protected abstract View getSuccessView(ViewGroup container);

    private View getLoadingView() {
        return LayoutInflater.from(getContext()).inflate(R.layout.fragment_loading_view, this,false);
    }
    public void setOnRetryClickListener(OnRetryClickListener listener) {
        this.mOnRetryClickListener = listener;
    }

    public interface OnRetryClickListener {
        void onRetryClick();
    }
}
