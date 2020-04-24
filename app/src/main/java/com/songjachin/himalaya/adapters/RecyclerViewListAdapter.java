package com.songjachin.himalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.songjachin.himalaya.R;
import com.ximalaya.ting.android.opensdk.model.album.Album;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by matthew on 2020/4/24 15:27
 * day day up!
 */
public class RecyclerViewListAdapter extends RecyclerView.Adapter<RecyclerViewListAdapter.InnerHolder> {

    private List<Album> mData = new ArrayList<>();
    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //create a item view
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recommend,parent,false);

        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull InnerHolder holder, int position) {
        //set the data
        holder.itemView.setTag(position);
        holder.setData(mData.get(position));
    }

    @Override
    public int getItemCount() {
        //返回要显示的个数
        if(mData!=null){
            return mData.size();
        }
        return 0;
    }

    public void setData(List<Album> albumList) {
        //对数据进行处理
        if(albumList!=null){
            mData.clear();
            mData.addAll(albumList);
        }
        // update the UI
        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setData(Album album) {
            //find the all views and set data
            //album image
            ImageView albumCoverView = itemView.findViewById(R.id.album_cover);
            //title
            TextView albumTitleText = itemView.findViewById(R.id.album_title_tv);
            //description
            TextView albumDescriptionText = itemView.findViewById(R.id.album_description_tv);
            //album play count
            TextView albumPlayCountText = itemView.findViewById(R.id.album_play_count);

            TextView albumContentSizeText = itemView.findViewById(R.id.album_content_size);

            albumTitleText.setText(album.getAlbumTitle());
            albumDescriptionText.setText(album.getAlbumRichIntro());
            albumPlayCountText.setText(album.getPlayCount() + "");
            albumContentSizeText.setText(album.getIncludeTrackCount() + "");
            Glide.with(itemView.getContext()).load(album.getCoverUrlLarge()).into(albumCoverView);
        }
    }
}
