package com.songjachin.himalaya;


import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.songjachin.himalaya.base.BaseActivity;
import com.songjachin.himalaya.interfaces.IAlbumDetailViewCallback;
import com.songjachin.himalaya.presenters.AlbumDetailPresenterImpl;
import com.songjachin.himalaya.utils.ImageBlur;
import com.songjachin.himalaya.utils.LogUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.ximalaya.ting.android.opensdk.model.album.Album;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.List;

/**
 * Created by matthew on 2020/4/27 10:58
 * day day up!
 */
public class DetailActivity extends BaseActivity implements IAlbumDetailViewCallback {

    private static final String TAG ="DetailActivity" ;
    private ImageView mLargeCover;
    private ImageView mSmallCover;
    private TextView mAlbumTitle;
    private TextView mAlbumAuthor;
    private AlbumDetailPresenterImpl mAlbumDetailPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        initView();
        mAlbumDetailPresenter = AlbumDetailPresenterImpl.getInstance();
        mAlbumDetailPresenter.registerViewCallback(this);
    }

    private void initView() {
        mLargeCover = this.findViewById(R.id.iv_large_cover);
        mSmallCover = this.findViewById(R.id.iv_small_cover);
        mAlbumTitle = this.findViewById(R.id.tv_album_title);
        mAlbumAuthor = this.findViewById(R.id.tv_album_author);
    }

    @Override
    public void getAlbumListLoaded(List<Track> tracks) {

    }

    @Override
    public void onAlbumLoaded(Album targetAlbum) {
        if (mAlbumTitle != null) {
            mAlbumTitle.setText(targetAlbum.getAlbumTitle());
        }
        if (mAlbumAuthor != null) {
            mAlbumAuthor.setText(targetAlbum.getAnnouncer().getNickname());
        }
        //做高斯模糊，毛玻璃效果
        //TODO:
        if (mLargeCover != null ) {
            Picasso.get().load(targetAlbum.getCoverUrlLarge()).into(mLargeCover,new Callback(){

                @Override
                public void onSuccess() {
                    Drawable drawable = mLargeCover.getDrawable();
                    if (drawable != null) {
                        //到这里才说明是有图片的
                        ImageBlur.makeBlur(mLargeCover, DetailActivity.this);
                    }
                }

                @Override
                public void onError(Exception e) {
                    LogUtil.e(TAG,"on error");
                }
            });
            //Glide.with(this).load(targetAlbum.getCoverUrlLarge()).into(mLargeCover);
        }
        if (mSmallCover != null) {
            Picasso.get().load(targetAlbum.getCoverUrlSmall()).into(mSmallCover);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAlbumDetailPresenter != null) {
            mAlbumDetailPresenter.registerViewCallback(this);
        }
    }
}
