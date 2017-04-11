package com.miracleworld.lingxingdao.android.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.base.BaseActivity;
import com.miracleworld.lingxingdao.android.options.ImageLoaderOptions;
import com.miracleworld.lingxingdao.android.utils.CommanUtil;
import com.miracleworld.lingxingdao.android.view.myroundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;

/**
 * Created by donghaifeng on 2016/1/28.
 */
public class ScheduleDetailActivity extends BaseActivity{
    //商业传递的值
    private int scheduleId;
    private String portraitUrlSmall;
    private String title;
    private String teacherName;
    private String provinceName;
    private String cityName;
    private String address;
    private long startTime;
    private long endTime;
    private double price;
    private double againPrice;
    private double sitInPrice;
    private double askPrice;
    private String priceType;
//    private int isAgain;
    private String url;

    private ImageView first_loading_iv;
    private RotateAnimation rotateAnimation;
    private RelativeLayout first_loading_group;
    private RelativeLayout scheduledetail_net_message;
    private WebView scheduledetail_wv_content;

    @Override
    protected void initView() {
        getBundle();
        //查找控件首先显示webview，再显示其他的
        RelativeLayout scheduledetail_left= (RelativeLayout) findViewById(R.id.scheduledetail_left);
        scheduledetail_left.setOnClickListener(this);
        RoundedImageView scheduledetail_teacher_head= (RoundedImageView) findViewById(R.id.scheduledetail_teacher_head);
        TextView scheduledetail_title= (TextView) findViewById(R.id.scheduledetail_title);
        TextView scheduledetail_datas= (TextView) findViewById(R.id.scheduledetail_datas);
        TextView scheduledetail_price= (TextView) findViewById(R.id.scheduledetail__price);
        LinearLayout scheduledetail_againprice_group= (LinearLayout) findViewById(R.id.scheduledetail_againprice_group);
        TextView scheduledetail_againprice= (TextView) findViewById(R.id.scheduledetail_againprice);
        TextView scheduledetail_teachername= (TextView) findViewById(R.id.scheduledetail_teachername);
        TextView scheduledetail_address= (TextView) findViewById(R.id.scheduledetail_address);
        scheduledetail_wv_content= (WebView) findViewById(R.id.scheduledetail_wv_content);
        //webview的初始化
        scheduledetail_wv_content.setOverScrollMode(View.OVER_SCROLL_NEVER);
        scheduledetail_wv_content.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        WebSettings webSettings = scheduledetail_wv_content.getSettings();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        //设置载入页面自适应手机屏幕，居中显示
        webSettings.setUseWideViewPort(true);//设置此属性，可任意比例缩放
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setJavaScriptEnabled(true);// 启用JavaScript

        //网络不好的提示一进入就gone掉
        scheduledetail_net_message= (RelativeLayout) findViewById(R.id.scheduledetail_net_message);
        //已进入加载的菊花
        first_loading_group= (RelativeLayout) findViewById(R.id.first_loading_group);
        first_loading_iv= (ImageView) findViewById(R.id.first_loading_iv);
        rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_refresh_drawable_default);
        TextView scheduledetail_buy_butt= (TextView) findViewById(R.id.scheduledetail_buy_butt);
        scheduledetail_buy_butt.setOnClickListener(this);

        //webview的回调
        scheduledetail_wv_content.setWebViewClient(new WebViewClient(){
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
                scheduledetail_net_message.setVisibility(View.VISIBLE);
                scheduledetail_net_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        refresh();
                    }
                });
            }
        });

        //开始界面显示
        ImageLoader.getInstance().displayImage(portraitUrlSmall, scheduledetail_teacher_head, ImageLoaderOptions.headOptions);
        scheduledetail_title.setText(title);
        scheduledetail_datas.setText(CommanUtil.transhms(startTime, "yyyy.MM.dd") + "-" + CommanUtil.transhms(endTime,"yyyy.MM.dd"));
        scheduledetail_price.setText(""+(int)price);
        //解析传过来的 String 成数组
//        if (isAgain==0){
//            scheduledetail_againprice_group.setVisibility(View.GONE);
//        }else {
//            scheduledetail_againprice_group.setVisibility(View.VISIBLE);
//            scheduledetail_againprice.setText(""+(int) againPrice);
//        }
        if (priceType.equals("")){
            //表示只有 新学员的价格：原本就被隐藏了
//            holder.fragment_schedule_againprice_group.setVisibility(View.GONE);
        }else{
            String[] typeString=priceType.split(",");
            Log.e("jxf", "打印数组的长度" + typeString.length);
            for (int i=0;i<typeString.length;i++) {
                //表示有复训
                if (typeString[i].equals("1")) {
                    scheduledetail_againprice_group.setVisibility(View.VISIBLE);
                    scheduledetail_againprice.setText("" + ((int) againPrice));
                }
                //不用执行else
            }
        }
        scheduledetail_teachername.setText(teacherName);
        scheduledetail_address.setText(provinceName + cityName + "(" + address + ")");
        //网络不好的时候需要反复执行的操作
        refresh();
    }

    private void refresh() {
        // 开始动画开始执行
        first_loading_iv.setAnimation(rotateAnimation);
        first_loading_group.setVisibility(View.VISIBLE);
        scheduledetail_net_message.setVisibility(View.GONE);
        scheduledetail_wv_content.loadUrl(url);
    }

    private void getBundle() {
        Bundle bundle=getIntent().getExtras();
        scheduleId=bundle.getInt("scheduleId");
        portraitUrlSmall=bundle.getString("portraitUrlSmall");
        title=bundle.getString("title");
        teacherName=bundle.getString("teacherName");
        provinceName=bundle.getString("provinceName");
        cityName=bundle.getString("cityName");
        address=bundle.getString("address");
        startTime=bundle.getLong("startTime");
        endTime=bundle.getLong("endTime");
        price=bundle.getDouble("price");
        againPrice=bundle.getDouble("againPrice");
        sitInPrice=bundle.getDouble("sitInPrice");
        askPrice=bundle.getDouble("askPrice");
        priceType=bundle.getString("priceType");
//        isAgain=bundle.getInt("isAgain");
        url=bundle.getString("url");
//        url="http://139.196.173.207:8080/api/news/newsDetail?id=19";
    }

    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_schedule_detail);
    }

    @Override
    protected void onClickEvent(View view) {
        switch (view.getId()){
            case R.id.scheduledetail_left:
                //finish掉
                ScheduleDetailActivity.this.finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                break;
            case R.id.scheduledetail_buy_butt:
                Intent intent = new Intent(this, PayWriteActivity.class);
                Bundle bundle=new Bundle();
                bundle.putInt("scheduleId",scheduleId);
                bundle.putString("portraitUrlSmall",portraitUrlSmall);
                bundle.putString("title", title);
                bundle.putString("teacherName",teacherName);
                bundle.putString("provinceName",provinceName);
                bundle.putString("cityName",cityName);
                bundle.putString("address",address);
                bundle.putLong("startTime", startTime);
                bundle.putLong("endTime", endTime);
                bundle.putDouble("price", price);
                bundle.putDouble("againPrice", againPrice);
                bundle.putDouble("sitInPrice",sitInPrice);
                bundle.putDouble("askPrice",askPrice);
                bundle.putString("priceType", priceType);
                intent.putExtras(bundle);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }


    @Override
    public void onBackPressed() {
        ScheduleDetailActivity.this.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

//    //转换时间：yyyy.MM.dd
//    private  String transhms(long progress){
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");//初始化Formatter的转换格式。
//
//        String ms = formatter.format(progress);
//        return ms;
//    }
}
