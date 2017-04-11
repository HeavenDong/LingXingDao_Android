package com.miracleworld.lingxingdao.android.base;

import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.miracleworld.lingxingdao.android.utils.PreferenceUtils;

/**
 * Created by donghaifeng on 2015/12/16
 */
public abstract class BaseActivity extends FragmentActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 设置全屏以及非全屏状态
        boolean screenState = PreferenceUtils.instance
                .getIsFullScreen(BaseActivity.this);
        WindowManager.LayoutParams attrs = getWindow().getAttributes();

        if (screenState) {
            attrs.flags |= WindowManager.LayoutParams.FLAG_FULLSCREEN;
            getWindow().setAttributes(attrs);
        } else {
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attrs);
        }

        setContentLayout();
        initView();
    }

    /**
     * 初始化View
     */
    protected abstract void initView();


    @Override
    public void onClick(View v) {
        onClickEvent(v);
    }

    public abstract void setContentLayout();

    /**
     * 处理点击事件
     *
     * @param view
     */
    protected abstract void onClickEvent(View view);

    /*
    *  字体不受系统设置影响
    * */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }
}




