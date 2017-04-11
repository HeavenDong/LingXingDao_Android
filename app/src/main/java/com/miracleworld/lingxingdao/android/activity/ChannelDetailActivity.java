package com.miracleworld.lingxingdao.android.activity;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.miracleworld.lingxingdao.android.App;
import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.activity.pay.SubscribeForPayActivity;
import com.miracleworld.lingxingdao.android.adapter.SubscriptionAdapter;
import com.miracleworld.lingxingdao.android.base.BaseActivity;
import com.miracleworld.lingxingdao.android.bean.MusicLesson;
import com.miracleworld.lingxingdao.android.http.NetClient;
import com.miracleworld.lingxingdao.android.http.NetResponseHandler;
import com.miracleworld.lingxingdao.android.http.Url;
import com.miracleworld.lingxingdao.android.utils.SharedPreUtils;
import com.miracleworld.lingxingdao.android.view.DefinedSingleToast;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by donghaifeng on 2016/1/28.
 */
public class ChannelDetailActivity extends BaseActivity {
    //上页传递的值
    private int teacherId;
    private String portraitUrlSmall;
    private String nickname;
    private int channelId;
    private double channelPrice;
    private String des;
    private String channelName;
    private String startSource;

    //防止重复点击刷新:true表示都可以点击
    private boolean isFree=true;
    private boolean isSubscribed=true;
    private boolean isPayed=true;

    //网络需要的参数：音频列表
    private int userId;
    private int maxId;
    private int minId;
    private int pageSize;
    private int type;//0免费 1付费 2已订阅

    //需要提全局的控件
    private TextView channeldetail_price;
    private TextView channedetail_channel_intro;
    private RelativeLayout channeldetail_extends_group;
    private TextView channeldetail_introduce_content_click_tv;
    private ImageView channeldetail_introduce_content_click_iv;
    private RotateAnimation rotateAnimation;

    //动画需要的textview的高度
    //点击扩张动画需要的尺寸
    private int minHeight;
    private int maxHeight;
    //扩展动画需要的boolean判断：初始是波扩张的
    private boolean isExtened = false;

    //列表相关
    private ArrayList<MusicLesson> lessons;
    private SubscriptionAdapter adapter;

    //列表显示数据的控件
    private RelativeLayout activity_channeldetail_loading_rl;
    private ImageView activity_channeldetail_loading_iv;
    private RelativeLayout activity_channeldetail_net_message;
    private RelativeLayout activity_channeldetail_none;

    //横向列表的点击
    private TextView channeldetail_free_text;
    private TextView channeldetail_payed_text;
    private TextView channeldetail_subscribed_text;
    private ImageView channeldetail_free_line;
    private ImageView channeldetail_payed_line;
    private ImageView channeldetail_subscribed_line;
    @Override
    protected void initView() {
        getBundle();
        //查找控件
        RelativeLayout channedetail_left= (RelativeLayout) findViewById(R.id.channedetail_left);
        channedetail_left.setOnClickListener(this);

        channeldetail_price= (TextView) findViewById(R.id.channeldetail_price);

        TextView channeldetail_subscription= (TextView) findViewById(R.id.channeldetail_subscription);
        channeldetail_subscription.setOnClickListener(this);


        channedetail_channel_intro= (TextView) findViewById(R.id.channedetail_channel_intro);

        channeldetail_extends_group= (RelativeLayout) findViewById(R.id.channeldetail_extends_group);
        LinearLayout channeldetail_extends_group_click= (LinearLayout) findViewById(R.id.channeldetail_extends_group_click);
        channeldetail_extends_group_click.setOnClickListener(this);
        channeldetail_introduce_content_click_tv= (TextView) findViewById(R.id.channeldetail_introduce_content_click_tv);
        channeldetail_introduce_content_click_iv= (ImageView) findViewById(R.id.channeldetail_introduce_content_click_iv);

        LinearLayout channeldetail_free_layout= (LinearLayout) findViewById(R.id.channeldetail_free_layout);
        channeldetail_free_layout.setOnClickListener(this);
        LinearLayout channeldetail_payed_layout= (LinearLayout) findViewById(R.id.channeldetail_payed_layout);
        channeldetail_payed_layout.setOnClickListener(this);
        LinearLayout channeldetail_subscribed_layout= (LinearLayout) findViewById(R.id.channeldetail_subscribed_layout);
        channeldetail_subscribed_layout.setOnClickListener(this);
        channeldetail_free_text= (TextView) findViewById(R.id.channeldetail_free_text);
        channeldetail_payed_text= (TextView) findViewById(R.id.channeldetail_payed_text);
        channeldetail_subscribed_text= (TextView) findViewById(R.id.channeldetail_subscribed_text);
        channeldetail_free_line= (ImageView) findViewById(R.id.channeldetail_free_line);
        channeldetail_payed_line= (ImageView) findViewById(R.id.channeldetail_payed_line);
        channeldetail_subscribed_line= (ImageView) findViewById(R.id.channeldetail_subscribed_line);

        ListView channedetail_music_lv= (ListView) findViewById(R.id.channedetail_music_lv);
        channedetail_music_lv.setOverScrollMode(View.OVER_SCROLL_NEVER);

        activity_channeldetail_loading_rl= (RelativeLayout) findViewById(R.id.activity_channeldetail_loading_rl);
        activity_channeldetail_loading_iv= (ImageView) findViewById(R.id.activity_channeldetail_loading_iv);
        rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.rotate_refresh_drawable_default);

        activity_channeldetail_net_message= (RelativeLayout) findViewById(R.id.activity_channeldetail_net_message);
        activity_channeldetail_net_message.setVisibility(View.GONE);
        activity_channeldetail_none= (RelativeLayout) findViewById(R.id.activity_channeldetail_none);
        activity_channeldetail_none.setVisibility(View.GONE);

        //开始显示界面 以及值的初始化
        isFree=false;//表示免费的初始化：但是不可以再点击了
        //网络请求参数的初始化
        userId= (int) SharedPreUtils.get(this, "user_id", 0);
        maxId=0;
        minId=0;
        pageSize=0;
        type=0;
        //另加channelid
        setUI();
        //开始显示列表数据
        lessons=new ArrayList<MusicLesson>();
        adapter=new SubscriptionAdapter(this,lessons);
        channedetail_music_lv.setAdapter(adapter);
        loadDates();
        channedetail_music_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (type==0){
                    for (int i=0;i<lessons.size();i++){
                        if (i==position){
                            lessons.get(i).isCheck=true;
                        }else{
                            lessons.get(i).isCheck=false;
                        }
                    }
                    adapter.notifyDataSetChanged();
                    Intent intent=new Intent(ChannelDetailActivity.this,PlayActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("portraitUrlSmall", portraitUrlSmall);
                    bundle.putString("nickname", nickname);
                    bundle.putInt("itemposition", position);
                    intent.putExtras(bundle);
                    intent.putExtra("list", (Serializable) lessons);
                    startActivityForResult(intent, 110);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
                else if (type==1){
                  DefinedSingleToast.showToast(ChannelDetailActivity.this,"未订阅，请订阅！");
//                    final AlertDialog dialog=  new AlertDialog.Builder(ChannelDetailActivity.this).create();
//                    dialog.show();
//                    dialog.setCancelable(true);
//                    Window window=dialog.getWindow();
//                    View dialogView=View .inflate(SubscriptionActivity.this, R.layout.subscription_paytodetail_dialog, null);
//                    window.setContentView(dialogView);
//                    dialogView.findViewById(R.id.leave).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            dialog.dismiss();
//                        }
//                    });
//                    dialogView.findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            //申请接口，请求下来数据才能跳转，还有传递别的数据：到支付页面要使用的
//                            //携带：lessons.get(position).id;
//                            dialog.dismiss();
//                            Intent intent=new Intent(SubscriptionActivity.this, PayInterfaceActivity.class);
//                            Bundle bundle=new Bundle();
//                            bundle.putInt("teacherId", teacherId);
//                            bundle.putString("portraitUrlSmall", portraitUrlSmall);
//                            bundle.putString("nickname", nickname);
//                            bundle.putInt("id", lessons.get(position).id);
//                            bundle.putString("title", lessons.get(position).title);
//                            Log.e("jxf","跳转付费频道需要的参数id"+lessons.get(position).id);
//                            intent.putExtras(bundle);
//                            startActivityForResult(intent, 600);
//                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//                        }
//                    });
                }else if (type==2){
                    for (int i=0;i<lessons.size();i++){
                        if (i==position){
                            lessons.get(i).isCheck=true;
                        }else{
                            lessons.get(i).isCheck=false;
                        }
                    }
                    adapter.notifyDataSetChanged();
                    Intent intent=new Intent(ChannelDetailActivity.this,PlayActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("portraitUrlSmall", portraitUrlSmall);
                    bundle.putString("nickname", nickname);
                    bundle.putInt("itemposition", position);
                    intent.putExtras(bundle);
                    intent.putExtra("list", (Serializable) lessons);
                    startActivityForResult(intent, 110);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            }
        });
    }

    private void loadDates() {
        Log.e("jxf", "开始加载数据");
        activity_channeldetail_net_message.setVisibility(View.GONE);
        activity_channeldetail_none.setVisibility(View.GONE);
        activity_channeldetail_loading_rl.setVisibility(View.VISIBLE);
        activity_channeldetail_loading_iv.setAnimation(rotateAnimation);
        activity_channeldetail_loading_iv.setVisibility(View.VISIBLE);
        ConnectivityManager mConnectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info == null || !mConnectivity.getBackgroundDataSetting()) {
            Log.e("jxf", "没网");
            netOff();
        }
        else{
            Log.e("jxf","有网");
            netRequestAndResponse();
        }

    }

    private void netRequestAndResponse() {
        RequestParams params = new RequestParams();
        params.put("userId", userId);
        params.put("channelId", channelId);
        params.put("type", type);
        params.put("maxId", maxId);
        params.put("minId", minId);
        params.put("pageSize", pageSize);
        Log.e("jxf", "网络请求中的参数userId" + userId+"channelId"+channelId+"type"+type+"maxId"+maxId+"minId"+minId+"pageSize"+pageSize);
        NetClient.headGet(this, Url.CHANNELDETAIL_LIST, params, new NetResponseHandler() {
            @Override
            public void onResponse(String json) {
                try {
                    JSONObject jSONObjectAll = new JSONObject(json);
                    Log.e("jxf", "内容详情页复杂列表的json" + json);
                    String status = jSONObjectAll.optString("status");
                    if (status.equals("1")) {
                        JSONArray jSONArray = jSONObjectAll.optJSONArray("datas");
                        if (jSONArray != null) {
                            ArrayList<MusicLesson> temp = new ArrayList<MusicLesson>();
                            int length = jSONArray.length();
                            for (int i = 0; i < length; i++) {
                                JSONObject obj = jSONArray.optJSONObject(i);
                                Log.e("jxf", "内容详情列表的一条数据" + obj.toString());
                                MusicLesson musicLesson = new MusicLesson();
                                musicLesson.id = obj.optInt("id");
                                musicLesson.title = obj.optString("title");
                                musicLesson.categoryName = obj.optString("categoryName");
                                musicLesson.pictureUrlSmall = obj.optString("pictureUrlSmall");
                                musicLesson.pictureUrlMiddle = obj.optString("pictureUrlMiddle");
                                musicLesson.pictureUrlBig = obj.optString("pictureUrlBig");
                                musicLesson.url = obj.optString("url");
                                musicLesson.isCost = obj.optString("isCost");
                                musicLesson.type = obj.optInt("type");
                                musicLesson.sort = obj.optInt("sort");
                                musicLesson.creatTime = obj.optLong("createTime");
                                musicLesson.currentposition = 0;
                                musicLesson.isCheck = false;
                                temp.add(musicLesson);
                            }
                            lessons.addAll(temp);
                            activity_channeldetail_loading_iv.clearAnimation();
                            activity_channeldetail_loading_iv.setVisibility(View.GONE);
                            activity_channeldetail_loading_rl.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        } else {
                            activity_channeldetail_loading_iv.clearAnimation();
                            activity_channeldetail_loading_iv.setVisibility(View.GONE);
                            activity_channeldetail_loading_rl.setVisibility(View.GONE);
                            activity_channeldetail_none.setVisibility(View.VISIBLE);
                        }
                    } else {
                        activity_channeldetail_loading_iv.clearAnimation();
                        activity_channeldetail_loading_iv.setVisibility(View.GONE);
                        activity_channeldetail_loading_rl.setVisibility(View.GONE);
                        String errorCode = jSONObjectAll.optString("errorCode");
                        if (errorCode.equals("2")) {
                            activity_channeldetail_none.setVisibility(View.VISIBLE);
                        } else if (errorCode.equals("90001")) {
                            DefinedSingleToast.showToast(ChannelDetailActivity.this, getResources().getString(R.string.system_exception));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("jxf", "异常catch了" + e.toString());
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                netOff();
            }
        });
    }

    private void netOff() {
        Log.e("jxf", "内容详情请求失败fail");
        activity_channeldetail_loading_iv.clearAnimation();
        activity_channeldetail_loading_iv.setVisibility(View.GONE);
        activity_channeldetail_loading_rl.setVisibility(View.GONE);
        activity_channeldetail_net_message.setVisibility(View.VISIBLE);
        activity_channeldetail_net_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_channeldetail_net_message.setVisibility(View.GONE);
                loadDates();
            }
        });
    }

    private void setUI() {
        channeldetail_price.setText("" + (int) channelPrice + getResources().getString(R.string.subscription_everymouth));
        channedetail_channel_intro.setText(des);
        //获取动画的高度
        Log.e("jxf", "扩展动画在一进入的时候设定显示全部");
        channedetail_channel_intro.setMaxLines(Integer.MAX_VALUE);
        channedetail_channel_intro.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                channedetail_channel_intro.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                maxHeight = channedetail_channel_intro.getHeight();
                Log.e("jxf", "最大高度" + maxHeight);
                Log.e("jxf", "行数" + channedetail_channel_intro.getLineCount());
                if (channedetail_channel_intro.getLineCount() > 2) {
                    channeldetail_extends_group.setVisibility(View.VISIBLE);
                    Log.e("jxf", "设定显示2行");
                    channedetail_channel_intro.setMaxLines(2);
                    channedetail_channel_intro.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            channedetail_channel_intro.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            minHeight = channedetail_channel_intro.getHeight();
                            Log.e("jxf", "最小高度" + minHeight);
                            channedetail_channel_intro.getLayoutParams().height = minHeight;
                            channedetail_channel_intro.requestLayout();
                        }
                    });
                } else {
                    channeldetail_extends_group.setVisibility(View.GONE);
                }
            }
        });
    }

    private void getBundle() {
        Bundle bundle=getIntent().getExtras();
        teacherId=bundle.getInt("teacherId");
        portraitUrlSmall = bundle.getString("portraitUrlSmall");
        nickname=bundle.getString("nickname");
        channelId=bundle.getInt("channelId");
        channelPrice=bundle.getDouble("channelPrice");
        des=bundle.getString("des");
        channelName=bundle.getString("channelName");
        startSource=bundle.getString("startSource");
    }

    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_channeldetail);
    }

    @Override
    protected void onClickEvent(View view) {
        switch (view.getId()){
            case R.id.channedetail_left:
                //正常返回：逐级返回：不做任何操作
                ChannelDetailActivity.this.finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                break;
            case R.id.channeldetail_subscription:
                loadTime();
                break;
            case R.id.channeldetail_free_layout:
                channeldetail_free_text.setTextColor(getResources().getColor(R.color.subscripte_green_color));
                channeldetail_free_line.setVisibility(View.VISIBLE);
                channeldetail_payed_text.setTextColor(getResources().getColor(R.color.subscripte_grey_color));
                channeldetail_payed_line.setVisibility(View.INVISIBLE);
                channeldetail_subscribed_text.setTextColor(getResources().getColor(R.color.subscripte_grey_color));
                channeldetail_subscribed_line.setVisibility(View.INVISIBLE);
                isPayed=true;
                isSubscribed=true;
                if (isFree) {
                    isFree=false;
                    type = 0;
                    lessons.clear();
                    adapter.notifyDataSetChanged();
                    loadDates();
                }
                break;
            case R.id.channeldetail_payed_layout:
                channeldetail_payed_text.setTextColor(getResources().getColor(R.color.subscripte_green_color));
                channeldetail_payed_line.setVisibility(View.VISIBLE);
                channeldetail_free_text.setTextColor(getResources().getColor(R.color.subscripte_grey_color));
                channeldetail_free_line.setVisibility(View.INVISIBLE);
                channeldetail_subscribed_text.setTextColor(getResources().getColor(R.color.subscripte_grey_color));
                channeldetail_subscribed_line.setVisibility(View.INVISIBLE);
                isFree=true;
                isSubscribed=true;
                if (isPayed) {
                    isPayed = false;
                    type = 1;
                    lessons.clear();
                    adapter.notifyDataSetChanged();
                    loadDates();
                }
                break;
            case R.id.channeldetail_subscribed_layout:
                channeldetail_subscribed_text.setTextColor(getResources().getColor(R.color.subscripte_green_color));
                channeldetail_subscribed_line.setVisibility(View.VISIBLE);
                channeldetail_free_text.setTextColor(getResources().getColor(R.color.subscripte_grey_color));
                channeldetail_free_line.setVisibility(View.INVISIBLE);
                channeldetail_payed_text.setTextColor(getResources().getColor(R.color.subscripte_grey_color));
                channeldetail_payed_line.setVisibility(View.INVISIBLE);
                isFree=true;
                isPayed=true;
                if (isSubscribed) {
                    isSubscribed = false;
                    type = 2;
                    lessons.clear();
                    adapter.notifyDataSetChanged();
                    loadDates();
                }
                break;
            case R.id.channeldetail_extends_group_click:
                ValueAnimator valueAnimator;
                if (!isExtened){
                    channeldetail_introduce_content_click_tv.setText("点击收起");
                    channeldetail_introduce_content_click_iv.setImageResource(R.drawable.content_click_away);
                    valueAnimator = ValueAnimator.ofInt(minHeight,maxHeight);
                }
                else{
                    channeldetail_introduce_content_click_tv.setText("查看全部");
                    channeldetail_introduce_content_click_iv.setImageResource(R.drawable.content_view_all);
                    valueAnimator = ValueAnimator.ofInt(maxHeight,minHeight);
                }
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int animatedValue = (Integer) animation.getAnimatedValue();
                        Log.e("jxf","高度变化的值"+animatedValue);
                        channedetail_channel_intro.getLayoutParams().height=animatedValue;
                        channedetail_channel_intro.requestLayout();
                    }
                });
                isExtened=!isExtened;
                valueAnimator.setDuration(350);
                Log.e("jxf","属性动画开始执行");
                valueAnimator.start();
                break;
        }

    }

    private void loadTime() {
        //判断网络的操作
        ConnectivityManager mConnectivity = (ConnectivityManager)ChannelDetailActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info == null || !mConnectivity.getBackgroundDataSetting()) {
            Log.e("jxf", "channeldetail页跳转请求时间:没网");
            //没网不跳转：不做任何的操作：直接提示网络不好：请重新点击
            DefinedSingleToast.showToast(this,"网络不给力：请重新点击");
        }
        else{
            Log.e("jxf","channeldetail页跳转请求时间:有网");
            netRequestAndResponseTime();
        }
    }

    private void netRequestAndResponseTime() {
        RequestParams params = new RequestParams();
        params.put("channelId",channelId);
        params.put("userId", userId);
        Log.e("jxf","adapter中请求时间携带参数"+params);
        NetClient.headGet(this, Url.PAY_STARTANDEND_TIME, params, new NetResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                //请求失败不做任何的操作
                Log.e("jxf","channel页adapter请求onfail：不做任何跳转：提示用户");
                DefinedSingleToast.showToast(ChannelDetailActivity.this,"网络不给力：请重新点击");
            }

            @Override
            public void onResponse(String json) {
                try {
                    JSONObject jSONObjectAll = new JSONObject(json);
                    Log.e("jxf", "channel页adapter跳转支付页请求时间得到响应的json" + json);
                    String status=jSONObjectAll.optString("status");
                    //请求成功有需要的数据:同时在此位置传递数据使用
                    if (status.equals("1")){
                        JSONObject obj=jSONObjectAll.optJSONObject("datas");
                        String startTime=obj.getString("startTime");
                        String endTime=obj.getString("endTime");
                        Intent intent = new Intent(ChannelDetailActivity.this, SubscribeForPayActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("lecturer_head", portraitUrlSmall);
                        bundle.putString("lecturer_name", nickname);
                        bundle.putInt("teacherId", teacherId);
                        bundle.putDouble("Price", channelPrice);
                        bundle.putString("channel_name",channelName);
                        bundle.putInt("channel_Id", channelId);
                        bundle.putString("startTime", startTime);
                        bundle.putString("endTime", endTime);
                        bundle.putInt("source", 0);
                        Log.e("jxf", "channel页adapter跳转支付页携带的信息" + "bundle::" + bundle);
                        intent.putExtras(bundle);
                        ChannelDetailActivity.this.startActivityForResult(intent, 900);
                        ChannelDetailActivity.this.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }else{
                        //错误返回：提示用户
                        DefinedSingleToast.showToast(ChannelDetailActivity.this, "网络不给力：请重新点击");
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("jxf", "channel页adapter跳转支付页请求时间:onresponse出现异常" + e.toString());
                    DefinedSingleToast.showToast(ChannelDetailActivity.this, "网络不给力：请重新点击");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        //正常返回：逐级返回：不做任何操作
        ChannelDetailActivity.this.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==110&&resultCode==510){
            int progress=data.getIntExtra("progress",0);
            int endplayPosition=data.getIntExtra("itemposition",0);
            Log.e("jxf","返回进度progress是"+progress+"返回的最终播放的条目"+endplayPosition);
            for (int i=0;i<lessons.size();i++){
                if (i==endplayPosition){
                    lessons.get(i).currentposition=progress;
                    lessons.get(i).isCheck=true;
                }
                else{
                    lessons.get(i).currentposition=0;
                    lessons.get(i).isCheck=false;
                }
            }
            adapter.notifyDataSetChanged();
        }
        if (requestCode==900){
            //来自channelactivity2
            if (startSource.equals("2")){
                //刷新一次界面
                lessons.clear();
                Log.e("jxf", "支付成功返回:刷channeldetail界面");
                adapter.notifyDataSetChanged();
                loadDates();

            }
            //来自channelactivity
            else if(startSource.equals("1")){
                //支付成功
                if (resultCode==910){
                    Log.e("jxf", "支付成功返回:杀死channeldetail界面");
                    //通知来自channelactivity  ：支付成功了
                    setResult(501);
                    finish();
                    overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                }
                //支付失败
                else if (resultCode==920){
                    Log.e("jxf", "支付失败返回:杀死channeldetail界面");
                    //通知来自channelactivity  ：支付失败了
                    setResult(502);
                    finish();
                    overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                }

            }
            //来自续订列表的条目
            else if (startSource.equals("3")){
                //支付成功
                if (resultCode==910){
                    Log.e("jxf", "支付成功返回:杀死channeldetail界面");
                    //通知来自channelactivity  ：支付成功了
                    setResult(251);
                    finish();
                    overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                }
                //支付失败
                else if (resultCode==920){
                    Log.e("jxf", "支付失败返回:杀死channeldetail界面");
                    //通知来自channelactivity  ：支付失败了
                    setResult(252);
                    finish();
                    overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                }
            }
        }
    }
}
