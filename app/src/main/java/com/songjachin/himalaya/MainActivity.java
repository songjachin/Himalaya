package com.songjachin.himalaya;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;


import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.songjachin.himalaya.adapters.IndicatorAdapter;
import com.songjachin.himalaya.adapters.MainContentAdapter;
import com.songjachin.himalaya.indicators.CommonNavigator;
import com.songjachin.himalaya.indicators.CommonNavigatorAdapter;
import com.songjachin.himalaya.indicators.IPagerIndicator;
import com.songjachin.himalaya.indicators.IPagerTitleView;
import com.songjachin.himalaya.indicators.LinePagerIndicator;
import com.songjachin.himalaya.indicators.MagicIndicator;
import com.songjachin.himalaya.indicators.SimplePagerTitleView;
import com.songjachin.himalaya.indicators.ViewPagerHelper;
import com.songjachin.himalaya.utils.LogUtil;


public class MainActivity extends FragmentActivity {
    private static final String TAG = "MainActivity";
    private MagicIndicator mMagicIndicator;
    private ViewPager mContentPager;
    private IndicatorAdapter mIndicatorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
    }

    private void initEvent() {
        mIndicatorAdapter.setOnIndicatorTabClickListener(new IndicatorAdapter.OnIndicatorTabClickListener() {
            @Override
            public void onTabClick(int index) {
                LogUtil.d(TAG,"on click ---> "+ index );
                if(mContentPager!=null){
                    mContentPager.setCurrentItem(index);
                }
            }
        });
    }

    private void initView() {
        mMagicIndicator = this.findViewById(R.id.main_indicator);
        mMagicIndicator.setBackgroundColor(this.getResources().getColor(R.color.main_color));
        //创建indicator的adapter
        mIndicatorAdapter = new IndicatorAdapter(this);
        CommonNavigator commonNavigator = new CommonNavigator(this);
        commonNavigator.setAdapter(mIndicatorAdapter);
        commonNavigator.setAdjustMode(true);


        //ViewPager
        mContentPager = this.findViewById(R.id.content_pager);
        //make the viewpager ..adapter
        FragmentManager fragmentManager = getSupportFragmentManager();
        MainContentAdapter contentAdapter = new MainContentAdapter(fragmentManager);
        mContentPager.setAdapter(contentAdapter);
        //bind the viewPager and the indicator
        mMagicIndicator.setNavigator(commonNavigator);
        ViewPagerHelper.bind(mMagicIndicator,mContentPager);
    }

}


