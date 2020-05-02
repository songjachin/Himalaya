package com.songjachin.himalaya.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.songjachin.himalaya.R;
import com.ximalaya.ting.android.opensdk.model.track.Track;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by matthew on 2020/4/28 8:54
 * day day up!
 */
public class DetailListAdapter extends RecyclerView.Adapter<DetailListAdapter.InnerHolder> {

    private List<Track> mDetailData = new ArrayList<>();
    private SimpleDateFormat mUpdateDateFormat = new SimpleDateFormat("yyyy-MM--dd");
    private SimpleDateFormat mDurationFormat = new SimpleDateFormat("mm:ss");
    private OnItemClickListener mItemClickListener = null;

    @NonNull
    @Override
    public InnerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detail, parent, false);
        return new InnerHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final InnerHolder holder, final int position) {
        holder.itemView.setTag(position);
        holder.setData(mDetailData.get(position), position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(), "you click " + holder.itemView.getTag() + "item", Toast.LENGTH_SHORT).show();
                if (mItemClickListener != null) {
                    mItemClickListener.onItemClick(mDetailData, position);
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return mDetailData.size();
    }

    public void setData(List<Track> tracks) {
        if (tracks != null) {
            mDetailData.clear();
            mDetailData.addAll(tracks);
        }

        notifyDataSetChanged();
    }

    public class InnerHolder extends RecyclerView.ViewHolder {
        public InnerHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setData(Track track, int position) {
            //找到控件，设置数据
            //顺序Id
            TextView orderTv = itemView.findViewById(R.id.order_text);
            //标题
            TextView titleTv = itemView.findViewById(R.id.detail_item_title);
            //播放次数
            TextView playCountTv = itemView.findViewById(R.id.detail_item_play_count);
            //时长
            TextView durationTv = itemView.findViewById(R.id.detail_item_duration);
            //更新日期
            TextView updateDateTv = itemView.findViewById(R.id.detail_item_update_time);

            orderTv.setText((position + 1) + "");
            titleTv.setText(track.getTrackTitle());
            playCountTv.setText(track.getPlayCount() + "");

            int durationMil = track.getDuration() * 1000;
            String duration = mDurationFormat.format(durationMil);
            durationTv.setText(duration);
            String updateTimeText = mUpdateDateFormat.format(track.getUpdatedAt());
            updateDateTv.setText(updateTimeText);
        }
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        //
        this.mItemClickListener = listener;
    }
    public interface OnItemClickListener{
        //
        void onItemClick(List<Track> detailData, int position);
    }
}
