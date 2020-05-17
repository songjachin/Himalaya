package com.songjachin.himalaya.utils;

import com.songjachin.himalaya.base.BaseFragment;
import com.songjachin.himalaya.fragments.HistoryFragment;
import com.songjachin.himalaya.fragments.AlbumFragment;
import com.songjachin.himalaya.fragments.SubscriptionFragment;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by matthew on 2020/4/24 9:32
 * day day up!
 */
public class FragmentCreator {
    //make the cache of fragment
    private static final int INDEX_RECOMMEND = 0;
    private static final int INDEX_SUBSCRIPTION = 1;
    private static final int INDEX_HISTORY = 2;

    public static final int PAGE_COUNT = 3;

    private static Map<Integer, BaseFragment> sCache = new HashMap<>();

    public static BaseFragment getFragment(int index) {
        BaseFragment baseFragment = sCache.get(index);
        if (baseFragment != null) {
            return baseFragment;
        }

        switch (index) {
            case INDEX_SUBSCRIPTION:
                baseFragment = new SubscriptionFragment();
                break;
            case INDEX_RECOMMEND:
                baseFragment = new AlbumFragment();
                break;
            case INDEX_HISTORY:
                baseFragment = new HistoryFragment();
                break;
            default:
                break;
        }

        sCache.put(index,baseFragment);
        return baseFragment;
    }
}
