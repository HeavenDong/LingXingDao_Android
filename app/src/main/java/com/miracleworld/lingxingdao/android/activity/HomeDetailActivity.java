package com.miracleworld.lingxingdao.android.activity;

import android.content.Intent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.base.BaseActivity;

/**
 * Created by donghaifeng on 2015/12/18.
 */
public class HomeDetailActivity extends BaseActivity{

    //控件全局
    private RelativeLayout home_detail_net_message;
    private WebView home_detail_wv_content;
    RelativeLayout first_loading_group;
    private ImageView first_loading_iv;
    //上页传参
    private String url;

    @Override
    protected void initView() {
        Intent intent=getIntent();
        url=intent.getStringExtra("url");
        //一进入就加载刷新的图片
        first_loading_group= (RelativeLayout) findViewById(R.id.first_loading_group);
        first_loading_iv= (ImageView) findViewById(R.id.first_loading_iv);
        RotateAnimation rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_refresh_drawable_default);
        // 开始动画
        first_loading_iv.setAnimation(rotateAnimation);
        first_loading_group.setVisibility(View.VISIBLE);

        //返回键  备注：需要添加点击事件
        ImageView home_detail_iv_title_left= (ImageView) findViewById(R.id.home_detail_iv_title_left);
        home_detail_iv_title_left.setOnClickListener(this);
        //网络不好：提示
        home_detail_net_message= (RelativeLayout) findViewById(R.id.home_detail_net_message);
        home_detail_net_message.setVisibility(View.GONE);
       //滚动的webview
        home_detail_wv_content= (WebView) findViewById(R.id.home_detail_wv_content);

        //备注：缺少获得数据
        displayView();
        
    }

    private void displayView() {
        home_detail_wv_content.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //一进入就加载刷新的图片消失
                first_loading_iv.clearAnimation();
                first_loading_group.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                //一进入就加载刷新的图片消失
                first_loading_iv.clearAnimation();
                first_loading_group.setVisibility(View.GONE);
                //网络不好提示显示
                home_detail_net_message.setVisibility(View.VISIBLE);
                home_detail_net_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        initView();
                    }
                });

            }
        });

        //listview,webview中滚动拖动到顶部或者底部时的阴影：成功
        home_detail_wv_content.setOverScrollMode(View.OVER_SCROLL_NEVER);
        //取消webview的滚动条：不起作用:联合属性srrallbar一起:起作用
        home_detail_wv_content.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);


        WebSettings webSettings =   home_detail_wv_content .getSettings();
        //不使用缓存
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //设置载入页面自适应手机屏幕，居中显示
        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);

        webSettings.setJavaScriptEnabled(true);// 启用JavaScript

        home_detail_wv_content.loadUrl(url);
    }

    @Override
    public void setContentLayout() {
        setContentView(R.layout.home_detail_activity);
    }

    @Override
    protected void onClickEvent(View view) {
        switch (view.getId()){
            case R.id.home_detail_iv_title_left:
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                break;
        }

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
