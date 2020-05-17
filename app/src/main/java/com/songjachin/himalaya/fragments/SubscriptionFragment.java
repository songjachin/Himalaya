package com.songjachin.himalaya.fragments;

import android.content.Intent;
import android.graphics.Rect;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.lcodecore.tkrefreshlayout.TwinklingRefreshLayout;
import com.songjachin.himalaya.DetailActivity;
import com.songjachin.himalaya.R;
import com.songjachin.himalaya.adapters.AlbumListAdapter;
import com.songjachin.himalaya.base.BaseApplication;
import com.songjachin.himalaya.base.BaseFragment;
import com.songjachin.himalaya.constants.Constants;
import com.songjachin.himalaya.interfaces.ISubscriptionCallback;
import com.songjachin.himalaya.presenters.DetailPresenter;
import com.songjachin.himalaya.presenters.SubscriptionPresenter;
import com.songjachin.himalaya.utils.LogUtil;
import com.songjachin.himalaya.utils.UIUtil;
import com.songjachin.himalaya.views.ConfirmDialog;
import com.songjachin.himalaya.views.UILoader;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.List;

/**
 * Created by matthew on 2020/4/24 9:12
 * day day up!
 */
public class SubscriptionFragment extends BaseFragment implements ISubscriptionCallback, AlbumListAdapter.OnAlbumItemClickListener, AlbumListAdapter.OnAlbumItemLongClickListener, ConfirmDialog.OnDialogActionClickListener {
    private static final String TAG = "SubscriptionFragment";

    private SubscriptionPresenter mSubscriptionPresenter;
    private UILoader mUiLoader = null;
    private RecyclerView mSubscriptionRv;
    private AlbumListAdapter mAdapter;
    private Album mCurrentAlbum = null;

    @Override
    protected View onSubViewLoaded(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container) {

        FrameLayout rootView = (FrameLayout) inflater.inflate(R.layout.fragment_subscription, container, false);
        if (mUiLoader == null) {
            mUiLoader = new UILoader(container.getContext()) {
                @Override
                protected View getSuccessView(ViewGroup container) {
                    return createSuccessView();
                }

                @Override
                protected View getEmptyView() {
                    //创建一个新的
                    View emptyView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_empty_view, this, false);
                    TextView tipsView = emptyView.findViewById(R.id.empty_view_tips_tv);
                    tipsView.setText(R.string.no_sub_content_tips_text);
                    return emptyView;
                }
            };
            if (mUiLoader.getParent() instanceof ViewGroup) {
                ((ViewGroup) mUiLoader.getParent()).removeView(mUiLoader);
            }
            rootView.addView(mUiLoader);
        }
        return rootView;
    }

    private View createSuccessView() {
        View itemView = LayoutInflater.from(BaseApplication.getAppContext()).inflate(R.layout.item_subscription, null);

        TwinklingRefreshLayout refreshLayout = itemView.findViewById(R.id.over_scroll_view);
        refreshLayout.setEnableRefresh(false);

        refreshLayout.setEnableLoadmore(false);

        mSubscriptionRv = itemView.findViewById(R.id.rv_subscription_album);
        mSubscriptionRv.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        mSubscriptionRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                outRect.top = UIUtil.dip2px(view.getContext(),4);
                outRect.bottom = UIUtil.dip2px(view.getContext(),4);
                outRect.left = UIUtil.dip2px(view.getContext(),4);
                outRect.right = UIUtil.dip2px(view.getContext(),4);

            }
        });

        mAdapter = new AlbumListAdapter();
        mAdapter.setOnAlbumItemClickListener(this);
        mAdapter.setOnAlbumItemLongClickListener(this);
        mSubscriptionRv.setAdapter(mAdapter);

        mSubscriptionPresenter = SubscriptionPresenter.getInstance();
        mSubscriptionPresenter.registerViewCallback(this);
        mSubscriptionPresenter.getSubscriptionList();

        if (mUiLoader != null) {
            mUiLoader.updateStatus(UILoader.UIStatus.LOADING);
        }
        return itemView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSubscriptionPresenter != null) {
            mSubscriptionPresenter.unregisterViewCallback(this);
        }
        mAdapter.setOnAlbumItemClickListener(null);
        mAdapter.setOnAlbumItemLongClickListener(null);
    }


    @Override
    public void onAddResult(boolean isSuccess) {

    }

    @Override
    public void onDeleteResult(boolean isSuccess) {
         //给出取消订阅的提示.
         Toast.makeText(BaseApplication.getAppContext(), isSuccess ? R.string.cancel_sub_success : R.string.cancel_sub_failed, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSubscriptionsLoaded(List<Album> albums) {
        if (albums.size() == 0) {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.EMPTY);
            }
        } else {
            if (mUiLoader != null) {
                mUiLoader.updateStatus(UILoader.UIStatus.SUCCESS);
            }
        }
        if (mAdapter != null) {
            LogUtil.d(TAG,"Album---------->" + albums.size() );
            mAdapter.setData(albums);
        }
    }

    @Override
    public void onSubFull() {
        Toast.makeText(getActivity(), "订阅数量不得超过" + Constants.MAX_SUB_COUNT, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(int position, Album album) {
        DetailPresenter.getInstance().setTargetAlbum(album);
        Intent intent = new Intent(getContext(), DetailActivity.class);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(Album album) {
        this.mCurrentAlbum = album;
        ConfirmDialog confirmDialog = new ConfirmDialog(getActivity());
        confirmDialog.setOnDialogActionClickListener(this);
        confirmDialog.show();
    }

    @Override
    public void onCancelSubClick() {
        //取消订阅内容
        if (mCurrentAlbum != null && mSubscriptionPresenter != null) {
            mSubscriptionPresenter.deleteSubscription(mCurrentAlbum);
        }
    }

    @Override
    public void onGiveUpClick() {

    }
}
