package com.songjachin.himalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.songjachin.himalaya.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matthew on 2020/5/1 10:59
 * day day up!
 */
public class PlayerTrackPagerAdapter extends PagerAdapter {
    private List<Track> mTrackList = new ArrayList<>();

    @Override
    public int getCount() {
        return mTrackList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        View itemView = LayoutInflater.from(container.getContext()).inflate(R.layout.item_pager_view, container,false);
        container.addView(itemView);
        ImageView imageView = itemView.findViewById(R.id.track_pager_view);
        Track track = mTrackList.get(position);
        String image = track.getCoverUrlLarge();
        Glide.with(container.getContext()).load(image).into(imageView);
        return itemView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public void setData(List<Track> list) {
        mTrackList.clear();
        mTrackList.addAll(list);
        notifyDataSetChanged();
    }
}
