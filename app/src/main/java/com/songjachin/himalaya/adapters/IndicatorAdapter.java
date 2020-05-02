package com.songjachin.himalaya.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.View;

import com.songjachin.himalaya.R;
import com.songjachin.himalaya.indicators.ColorTransitionPagerTitleView;
import com.songjachin.himalaya.indicators.CommonNavigatorAdapter;
import com.songjachin.himalaya.indicators.IPagerIndicator;
import com.songjachin.himalaya.indicators.IPagerTitleView;
import com.songjachin.himalaya.indicators.LinePagerIndicator;
import com.songjachin.himalaya.indicators.SimplePagerTitleView;

import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Created by matthew on 2020/4/23 23:18
 * day day up!
 */
public class IndicatorAdapter extends CommonNavigatorAdapter {

    private final String[] mTitles;
    public IndicatorAdapter(Context context) {
        mTitles = context.getResources().getStringArray(R.array.indicator_title);
    }

    @Override
    public int getCount() {
        if(mTitles!=null){
            return mTitles.length;
        }
        return 0;
    }

//    @Override
//    public IPagerTitleView getTitleView(Context context, final int index) {
//        SimplePagerTitleView simplePagerTitleView = new ColorTransitionPagerTitleView(context);
//        simplePagerTitleView.setNormalColor(Color.GRAY);
//        simplePagerTitleView.setSelectedColor(Color.WHITE);
//        simplePagerTitleView.setText(mTitles[index]);
//        simplePagerTitleView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //mViewPager.setCurrentItem(index);
//                //
//            }
//        });
//        return simplePagerTitleView;
//    }
//
//    @Override
//    public IPagerIndicator getIndicator(Context context) {
//        LinePagerIndicator linePagerIndicator = new LinePagerIndicator(context);
//        linePagerIndicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
//        linePagerIndicator.setColors(Color.WHITE);
//        return linePagerIndicator;
//    }



    @Override
    public IPagerTitleView getTitleView(Context context, final int index) {

        //创建view
        ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
        //设置一般情况下的颜色为灰色
        colorTransitionPagerTitleView.setNormalColor(Color.parseColor("#aaffffff"));
        //设置选中情况下的颜色为黑色
        colorTransitionPagerTitleView.setSelectedColor(Color.parseColor("#ffffff"));
        //单位sp
        colorTransitionPagerTitleView.setTextSize(20);
        //设置要显示的内容
        colorTransitionPagerTitleView.setText(mTitles[index]);
        //设置title的点击事件，这里的话，如果点击了title,那么就选中下面的viewPager到对应的index里面去
        //也就是说，当我们点击了title的时候，下面的viewPager会对应着index进行切换内容。
        colorTransitionPagerTitleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //切换viewPager的内容，如果index不一样的话。
                if (mOnTabClickListener != null) {
                    mOnTabClickListener.onTabClick(index);
                }
                //
            }
        });
        //把这个创建好的view返回回去
        return colorTransitionPagerTitleView;
    }

    @Override
    public IPagerIndicator getIndicator(Context context) {
        LinePagerIndicator indicator = new LinePagerIndicator(context);
        indicator.setMode(LinePagerIndicator.MODE_WRAP_CONTENT);
        indicator.setColors(Color.parseColor("#ffffff"));
        return indicator;
    }

    //expose the interface to listen the 'click' tab and response
    private OnIndicatorTabClickListener mOnTabClickListener;
    public interface OnIndicatorTabClickListener {
        void onTabClick(int index);
    }
    public void setOnIndicatorTabClickListener(OnIndicatorTabClickListener listener) {
        this.mOnTabClickListener = listener;
    }
}
