package com.miracleworld.lingxingdao.android.activity;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.nfc.FormatException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.adapter.SubscriptionAdapter;
import com.miracleworld.lingxingdao.android.base.BaseActivity;
import com.miracleworld.lingxingdao.android.bean.MusicLesson;
import com.miracleworld.lingxingdao.android.http.NetClient;
import com.miracleworld.lingxingdao.android.http.NetResponseHandler;
import com.miracleworld.lingxingdao.android.http.Url;
import com.miracleworld.lingxingdao.android.options.ImageLoaderOptions;
import com.miracleworld.lingxingdao.android.utils.SharedPreUtils;
import com.miracleworld.lingxingdao.android.view.DefinedSingleToast;
import com.miracleworld.lingxingdao.android.view.myroundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Created by donghaifeng on 2015/12/21.
 *
 */
public class  SubscriptionActivity extends BaseActivity {
    //上页传递数据
    private int teacherId;
    private String portraitUrlSmall;
    private String portraitUrlBig;
    private String nickname;
    //字符串的拼接：拿到下一页继续使用
    private String catgoryName;
    private String catgoryId;
    private String remark;
    private String pricerange;
    //网络需要的参数：保存老师的信息
    private int userId;
    //网络需要的参数：音频列表
    private int maxId;
    private int minId;
    private int pageSize;
    private int type;
    //老师相关的控件
    //内容介绍的 点击切换显示内容
    private TextView subscription_introduce_content_click_tv;
    //内容介绍的 点击切换 箭头变化
    private ImageView subscription_introduce_content_click_iv;
    //点击区域
    private LinearLayout extends_group_click;
    //点击扩张的容器
    private RelativeLayout extends_group;
    //点击扩张动画需要的尺寸
    private int minHeight;
    private int maxHeight;
    //扩展动画需要的boolean判断：初始是波扩张的
    private boolean isExtened = false;
    //老师头像
    private RoundedImageView subscription_teacher_head;
    //老师名字
    private TextView subscription_teacher_name;
    //老师课程的价格区间
    private TextView subscription_channel_price_range;
    //讲师简介下面的内容
    private TextView teacher_guide_go_tv;
    private TextView subscription_introduce_content;
    private ListView subscription_lv;
    private ArrayList<MusicLesson> lessons;
    private SubscriptionAdapter adapter;
    //网络不好的提示
    private RelativeLayout activity_subscription_net_message;
    //没有内容的提示
    private RelativeLayout activity_subscription_none;
    //一进入的刷新动画
    private RelativeLayout activity_subscription_loading_rl;
    private ImageView activity_subscription_loading_iv;
    private RotateAnimation rotateAnimation;
    //订阅频道
    private RelativeLayout subscription_issubscrip;
    //课程分类：免费，付费，已订阅
    private TextView free_text,payed_text,subscribed_text;
    private ImageView free_line,payed_line,subscribed_line;
    //防止重复点击刷新:true表示都可以点击
    private boolean isFree=true;
    private boolean isSubscribed=true;
    private boolean isPayed=true;

    @Override
    protected void initView() {
        ImageView subscription_goback= (ImageView) findViewById(R.id.subscription_goback);
        subscription_goback.setOnClickListener(this);
        LinearLayout go_group_ll= (LinearLayout) findViewById(R.id.go_group_ll);
        go_group_ll.setOnClickListener(this);
        getBundle();
        extends_group_click= (LinearLayout) findViewById(R.id.extends_group_click);
        subscription_introduce_content_click_tv= (TextView) findViewById(R.id.subscription_introduce_content_click_tv);
        subscription_introduce_content_click_iv= (ImageView) findViewById(R.id.subscription_introduce_content_click_iv);
        extends_group= (RelativeLayout) findViewById(R.id.extends_group);
        extends_group_click.setOnClickListener(this);
        subscription_lv= (ListView) findViewById(R.id.subscription_lv);
        subscription_teacher_head= (RoundedImageView) findViewById(R.id.subscription_teacher_head);
        subscription_teacher_head.setOnClickListener(this);
        subscription_teacher_name= (TextView) findViewById(R.id.subscription_teacher_name);
        subscription_channel_price_range= (TextView) findViewById(R.id.subscription_channel_price_range);
        subscription_issubscrip= (RelativeLayout) findViewById(R.id.subscription_issubscrip);
        subscription_issubscrip.setOnClickListener(this);
        subscription_introduce_content= (TextView) findViewById(R.id.subscription_introduce_content);
        teacher_guide_go_tv= (TextView) findViewById(R.id.teacher_guide_go_tv);
        setTeacherUI();
        findViewById(R.id.free_layout).setOnClickListener(this);
        findViewById(R.id.payed_layout).setOnClickListener(this);
        findViewById(R.id.subscribed_layout).setOnClickListener(this);
        free_line = (ImageView) findViewById(R.id.free_line);
        free_text = (TextView) findViewById(R.id.free_text);
        payed_line = (ImageView) findViewById(R.id.payed_line);
        payed_text = (TextView) findViewById(R.id.payed_text);
        subscribed_line = (ImageView) findViewById(R.id.subscribed_line);
        subscribed_text = (TextView) findViewById(R.id.subscribed_text);
        isFree=false;
        userId= (int)SharedPreUtils.get(this,"user_id",0);
        type=0;
        maxId=0;
        minId=0;
        pageSize=0;
        activity_subscription_net_message= (RelativeLayout) findViewById(R.id.activity_subscription_net_message);
        activity_subscription_net_message.setVisibility(View.GONE);
        activity_subscription_none= (RelativeLayout) findViewById(R.id.activity_subscription_none);
        activity_subscription_none.setVisibility(View.GONE);
        activity_subscription_loading_rl= (RelativeLayout) findViewById(R.id.activity_subscription_loading_rl);
        activity_subscription_loading_iv = (ImageView) findViewById(R.id.activity_subscription_loading_iv);
        rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.rotate_refresh_drawable_default);
        lessons=new ArrayList<MusicLesson>();
        loadDatas();
        subscription_lv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        adapter=new SubscriptionAdapter(this,lessons);
        subscription_lv.setAdapter(adapter);
        subscription_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (type==0){
                    for (int i=0;i<lessons.size();i++){
                        if (i==position){
                            lessons.get(i).isCheck=true;
                        }else{
                            lessons.get(i).isCheck=false;
                        }
                    }
                    adapter.notifyDataSetChanged();
                    Intent intent=new Intent(SubscriptionActivity.this,PlayActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("portraitUrlSmall", portraitUrlSmall);
                    bundle.putString("nickname", nickname);
                    bundle.putInt("itemposition", position);
                    intent.putExtras(bundle);
                    intent.putExtra("list", (Serializable) lessons);
                    startActivityForResult(intent, 500);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
                else if (type==1){
//                  DefinedSingleToast.showToast(SubscriptionActivity.this,"未订阅，请订阅！");
                    final AlertDialog dialog=  new AlertDialog.Builder(SubscriptionActivity.this).create();
                    dialog.show();
                    dialog.setCancelable(true);
                    Window window=dialog.getWindow();
                    View dialogView=View .inflate(SubscriptionActivity.this, R.layout.subscription_paytodetail_dialog, null);
                    window.setContentView(dialogView);
                    dialogView.findViewById(R.id.leave).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialogView.findViewById(R.id.go).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //申请接口，请求下来数据才能跳转，还有传递别的数据：到支付页面要使用的
                            //携带：lessons.get(position).id;
                            dialog.dismiss();
                            Intent intent=new Intent(SubscriptionActivity.this, PayInterfaceActivity.class);
                            Bundle bundle=new Bundle();
                            bundle.putInt("teacherId", teacherId);
                            bundle.putString("portraitUrlSmall", portraitUrlSmall);
                            bundle.putString("nickname", nickname);
                            bundle.putInt("id", lessons.get(position).id);
                            bundle.putString("title", lessons.get(position).title);
                            Log.e("jxf","跳转付费频道需要的参数id"+lessons.get(position).id);
                            intent.putExtras(bundle);
                            startActivityForResult(intent, 600);
                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        }
                    });
                }else if (type==2){
                    for (int i=0;i<lessons.size();i++){
                        if (i==position){
                            lessons.get(i).isCheck=true;
                        }else{
                            lessons.get(i).isCheck=false;
                        }
                    }
                    adapter.notifyDataSetChanged();
                    Intent intent=new Intent(SubscriptionActivity.this,PlayActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putString("portraitUrlSmall", portraitUrlSmall);
                    bundle.putString("nickname", nickname);
                    bundle.putInt("itemposition", position);
                    intent.putExtras(bundle);
                    intent.putExtra("list", (Serializable) lessons);
                    startActivityForResult(intent, 500);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }

            }
        });
    }

    private void loadDatas() {
        Log.e("jxf", "开始加载数据");
        activity_subscription_net_message.setVisibility(View.GONE);
        activity_subscription_none.setVisibility(View.GONE);
        activity_subscription_loading_rl.setVisibility(View.VISIBLE);
        activity_subscription_loading_iv.setAnimation(rotateAnimation);
        activity_subscription_loading_iv.setVisibility(View.VISIBLE);
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
    private void netRequestAndResponse(){
        RequestParams params = new RequestParams();
        params.put("userId", userId);
        params.put("teacherId", teacherId);
        params.put("type", type);
        params.put("maxId", maxId);
        params.put("minId", minId);
        params.put("pageSize", pageSize);
        Log.e("jxf", "网络请求中的参数userId" + userId+"teacherId"+teacherId+"type"+type+"maxId"+maxId+"minId"+minId+"pageSize"+pageSize);
        NetClient.headGet(this, Url.MUSIC_LIST, params, new NetResponseHandler() {
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
                                musicLesson.creatTime=obj.optLong("createTime");
                                musicLesson.currentposition = 0;
                                musicLesson.isCheck=false;
                                temp.add(musicLesson);
                            }
                            lessons.addAll(temp);
                            activity_subscription_loading_iv.clearAnimation();
                            activity_subscription_loading_iv.setVisibility(View.GONE);
                            activity_subscription_loading_rl.setVisibility(View.GONE);
                            adapter.notifyDataSetChanged();
                        }
                        else {
                            activity_subscription_loading_iv.clearAnimation();
                            activity_subscription_loading_iv.setVisibility(View.GONE);
                            activity_subscription_loading_rl.setVisibility(View.GONE);
                            activity_subscription_none.setVisibility(View.VISIBLE);
                        }
                    }
                    else {
                        activity_subscription_loading_iv.clearAnimation();
                        activity_subscription_loading_iv.setVisibility(View.GONE);
                        activity_subscription_loading_rl.setVisibility(View.GONE);
                        String errorCode = jSONObjectAll.optString("errorCode");
                        if (errorCode.equals("2")) {
                            activity_subscription_none.setVisibility(View.VISIBLE);
                        } else if (errorCode.equals("90001")) {
                            DefinedSingleToast.showToast(SubscriptionActivity.this, getResources().getString(R.string.system_exception));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("jxf", "异常catch了"+e.toString());
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                netOff();
            }
        });
    }

    private void netOff(){
        Log.e("jxf", "内容详情请求失败fail");
        activity_subscription_loading_iv.clearAnimation();
        activity_subscription_loading_iv.setVisibility(View.GONE);
        activity_subscription_loading_rl.setVisibility(View.GONE);
        activity_subscription_net_message.setVisibility(View.VISIBLE);
        activity_subscription_net_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity_subscription_net_message.setVisibility(View.GONE);
                loadDatas();
            }
        });
    }

    private void getBundle() {
        Bundle bundle=getIntent().getExtras();
        teacherId = bundle.getInt("teacherId");
        Log.e("jxf","teacherId"+teacherId);
        portraitUrlSmall = bundle.getString("portraitUrlSmall");
        portraitUrlBig=bundle.getString("portraitUrlBig");
        nickname=bundle.getString("nickname");
        catgoryName = bundle.getString("catgoryName");
        catgoryId=bundle.getString("catgoryId");
        remark = bundle.getString("remark");
        pricerange=bundle.getString("pricerange");
        }

    int i=0;
    int z=0;
    private void setTeacherUI(){
        ImageLoader.getInstance().displayImage(portraitUrlSmall, subscription_teacher_head, ImageLoaderOptions.headOptions);
        subscription_teacher_name.setText(nickname);
        subscription_channel_price_range.setText(getResources().getString(R.string.money)+pricerange+getResources().getString(R.string.subscription_everymouth));
        subscription_introduce_content.setText(remark);
        teacher_guide_go_tv.setText("浏览"+nickname+"老师全部频道?  ");
        Log.e("jxf", "扩展动画在一进入的时候设定显示全部");
        subscription_introduce_content.setMaxLines(Integer.MAX_VALUE);
        subscription_introduce_content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Log.e("jxf", "监听布局的次数i" + (i++));
                subscription_introduce_content.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                maxHeight = subscription_introduce_content.getHeight();
                Log.e("jxf", "最大高度" + maxHeight);
                Log.e("jxf", "行数" + subscription_introduce_content.getLineCount());
                if (subscription_introduce_content.getLineCount() > 2) {
                    extends_group.setVisibility(View.VISIBLE);
                    Log.e("jxf", "设定显示2行");
                    subscription_introduce_content.setMaxLines(2);
                    subscription_introduce_content.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            Log.e("jxf", "监听布局的次数z" + (z++));
                            subscription_introduce_content.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                            minHeight = subscription_introduce_content.getHeight();
                            Log.e("jxf", "最小高度" + minHeight);
                            subscription_introduce_content.getLayoutParams().height = minHeight;
                            subscription_introduce_content.requestLayout();
                        }
                    });
                } else {
                    extends_group.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_subscription);
    }

    @Override
    protected void onClickEvent(View view) {
        switch (view.getId()){
            case R.id.subscription_goback:
                SubscriptionActivity.this.finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                break;
            case R.id.subscription_issubscrip:
                Intent intent=new Intent(this, ChannelActivity.class);
                Bundle bundle=new Bundle();
                bundle.putInt("teacherId", teacherId);
                bundle.putString("portraitUrlSmall", portraitUrlSmall);
                bundle.putString("nickname", nickname);
                bundle.putString("catgoryNames", catgoryName);
                bundle.putString("catgoryIds",catgoryId);
                intent.putExtras(bundle);
                startActivityForResult(intent, 400);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.go_group_ll:
                Intent intent2=new Intent(this, ChannelActivity.class);
                Bundle bundle2=new Bundle();
                bundle2.putInt("teacherId", teacherId);
                bundle2.putString("portraitUrlSmall", portraitUrlSmall);
                bundle2.putString("nickname", nickname);
                bundle2.putString("catgoryNames", catgoryName);
                bundle2.putString("catgoryIds",catgoryId);
                intent2.putExtras(bundle2);
                startActivityForResult(intent2, 400);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.extends_group_click:
                ValueAnimator valueAnimator;
                if (!isExtened){
                    subscription_introduce_content_click_tv.setText("点击收起");
                    subscription_introduce_content_click_iv.setImageResource(R.drawable.content_click_away);
                    valueAnimator = ValueAnimator.ofInt(minHeight,maxHeight);
                }
                else{
                    subscription_introduce_content_click_tv.setText("查看全部");
                    subscription_introduce_content_click_iv.setImageResource(R.drawable.content_view_all);
                    valueAnimator = ValueAnimator.ofInt(maxHeight,minHeight);
                }
                valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        int animatedValue = (Integer) animation.getAnimatedValue();
                        Log.e("jxf","高度变化的值"+animatedValue);
                        subscription_introduce_content.getLayoutParams().height=animatedValue;
                        subscription_introduce_content.requestLayout();
                    }
                });
                isExtened=!isExtened;
                valueAnimator.setDuration(350);
                Log.e("jxf","属性动画开始执行");
                valueAnimator.start();
                break;
            case R.id.subscription_teacher_head:
                Intent intent1=new Intent(SubscriptionActivity.this,ScraleActivity.class);
                intent1.putExtra("imgSmallUrl",portraitUrlSmall);
                intent1.putExtra("different","1");
                startActivity(intent1);
                overridePendingTransition(R.anim.small_to_big, R.anim.unchange);
                break;
            /**课程分类：免费*/
            case R.id.free_layout:
                free_text.setTextColor(getResources().getColor(R.color.subscripte_green_color));
                free_line.setVisibility(View.VISIBLE);
                payed_text.setTextColor(getResources().getColor(R.color.subscripte_grey_color));
                payed_line.setVisibility(View.INVISIBLE);
                subscribed_text.setTextColor(getResources().getColor(R.color.subscripte_grey_color));
                subscribed_line.setVisibility(View.INVISIBLE);
                isPayed=true;
                isSubscribed=true;
                if (isFree) {
                    isFree=false;
                    type = 0;
                    lessons.clear();
                    adapter.notifyDataSetChanged();
                    loadDatas();
                }
                break;
            /**课程分类：付费，*/
            case R.id.payed_layout:
                payed_text.setTextColor(getResources().getColor(R.color.subscripte_green_color));
                payed_line.setVisibility(View.VISIBLE);
                free_text.setTextColor(getResources().getColor(R.color.subscripte_grey_color));
                free_line.setVisibility(View.INVISIBLE);
                subscribed_text.setTextColor(getResources().getColor(R.color.subscripte_grey_color));
                subscribed_line.setVisibility(View.INVISIBLE);
                isFree=true;
                isSubscribed=true;
                if (isPayed) {
                    isPayed = false;
                    type = 1;
                    lessons.clear();
                    adapter.notifyDataSetChanged();
                    loadDatas();
                }
                break;
            /**课程分类：已订阅*/
            case R.id.subscribed_layout:
                subscribed_text.setTextColor(getResources().getColor(R.color.subscripte_green_color));
                subscribed_line.setVisibility(View.VISIBLE);
                free_text.setTextColor(getResources().getColor(R.color.subscripte_grey_color));
                free_line.setVisibility(View.INVISIBLE);
                payed_text.setTextColor(getResources().getColor(R.color.subscripte_grey_color));
                payed_line.setVisibility(View.INVISIBLE);
                isFree=true;
                isPayed=true;
                if (isSubscribed) {
                    isSubscribed = false;
                    type = 2;
                    lessons.clear();
                    adapter.notifyDataSetChanged();
                    loadDatas();
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==400){
            if (resultCode==480){
                Log.e("jxf","支付成功返回频道列表：频道列表返回：重新请求数据");
                lessons.clear();
                Log.e("jxf", "支付成功返回频道列表：频道列表返回：首先清空数据，再去刷界面");
                adapter.notifyDataSetChanged();
                loadDatas();
            }
            else if (resultCode==481){
                Log.e("jxf", "支付失败返回频道列表：频道列表返回:不用做任何操作");
            }
        }
        if (requestCode==500&&resultCode==510){
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
        if (requestCode==600){
            //支付成功
            if (resultCode==610){
                Log.e("jxf","支付成功返回付费频道：付费频道返回：重新请求数据");
                lessons.clear();
                Log.e("jxf", "支付成功返回付费频道：付费频道返回：首先清空数据，再去刷界面");
                adapter.notifyDataSetChanged();
                loadDatas();
            }
            //支付失败
            else if (resultCode==620){
                Log.e("jxf", "支付失败返回付费频道：付费频道返回：不用做任何操作");
            }
        }
    }


    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        SubscriptionActivity.this.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
}
