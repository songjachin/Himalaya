package com.songjachin.himalaya.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * Created by matthew on 2020/4/24 9:11
 * day day up!
 */
public abstract class BaseFragment extends Fragment {

    private View rootView;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = onSubViewLoaded(inflater,container);
        return rootView ;
    }

    protected abstract View onSubViewLoaded(@NonNull LayoutInflater inflater, @Nullable ViewGroup container);
}
