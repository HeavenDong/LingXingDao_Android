package com.miracleworld.lingxingdao.android.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by donghaifeng on 2015/10/9.
 */
@SuppressWarnings("unchecked")
public abstract class BaseFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "BaseFragment";
    protected Context context;

    public BaseFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        context = getActivity();
        View view = inflater.inflate(setLayout(), container, false);
        if (view == null) {
            Log.e(TAG, "BaseFragment: view == null!!");
            return null;
        }
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView(view, savedInstanceState);
    }

    protected abstract void initView(View view, Bundle bundle);
    protected abstract @LayoutRes int setLayout();

    @Override
    public void onClick(View v) {
        onClickEvent(v);
    }

    protected abstract void onClickEvent(View view);



}

