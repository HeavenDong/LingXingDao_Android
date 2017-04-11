package com.miracleworld.lingxingdao.android.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.base.BaseFragment;

/**
 * Created by donghaifeng on 2015/12/16
 */
public class MallFragment extends BaseFragment {
    @Override
    protected void initView(View view, Bundle bundle) {
        Log.e("jxf", "进入mall");
    }

    @Override
    protected int setLayout() {
        return R.layout.mall_layout_fragment;
    }

    @Override
    protected void onClickEvent(View view) {

    }
}
