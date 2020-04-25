package com.songjachin.himalaya.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.songjachin.himalaya.R;
import com.songjachin.himalaya.base.BaseFragment;

/**
 * @author songjachin
 * @ClassName HistoryFragment
 * @Description
 * @date 2020-04-25 10:18
 */

public class HistoryFragment extends BaseFragment {

    @Override
    protected View onSubViewLoaded(@NonNull LayoutInflater inflater, @Nullable ViewGroup container) {
        View rootView = inflater.inflate(R.layout.fragment_history,container,false);
        return rootView;
    }

}
