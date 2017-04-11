package com.miracleworld.lingxingdao.android.activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.miracleworld.lingxingdao.android.App;
import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.activity.pay.SubscribeForPayActivity;
import com.miracleworld.lingxingdao.android.base.BaseActivity;
import com.miracleworld.lingxingdao.android.http.NetClient;
import com.miracleworld.lingxingdao.android.http.NetResponseHandler;
import com.miracleworld.lingxingdao.android.http.Url;
import com.miracleworld.lingxingdao.android.options.ImageLoaderOptions;
import com.miracleworld.lingxingdao.android.utils.SharedPreUtils;
import com.miracleworld.lingxingdao.android.view.DefinedSingleToast;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by donghaifeng on 2016/1/27.
 */
public class PayInterfaceActivity extends BaseActivity{
    private RelativeLayout paychannel_left;
    private ImageView music_picture;
    private TextView content_title;
    private TextView channel_belong;
    private TextView channel_intro;
    private TextView content_category;
    private TextView content_price;
    private TextView goto_pay;
    //上页传递的值
    private int teacherId;
    private String portraitUrlSmall;
    private String nickname;
    private int id;
    private String title;
    //网络请求的数据：提成全局的
    private int channelId;
    private double channelPrice;
    private String channelName;

    @Override
    protected void initView() {
        getBundle();
        paychannel_left= (RelativeLayout) findViewById(R.id.paychannel_left);
        paychannel_left.setOnClickListener(this);
        ScrollView scrol= (ScrollView) findViewById(R.id.scrol);
        scrol.setOverScrollMode(View.OVER_SCROLL_NEVER);
        music_picture= (ImageView) findViewById(R.id.music_picture);
        content_title= (TextView) findViewById(R.id.content_title);
        content_title.setText(title );
        channel_belong= (TextView) findViewById(R.id.channel_belong);
        channel_intro= (TextView) findViewById(R.id.channel_intro);
        content_category= (TextView) findViewById(R.id.content_category);
        content_price= (TextView) findViewById(R.id.content_price);
        goto_pay= (TextView) findViewById(R.id.goto_pay);
        goto_pay.setEnabled(false);
        goto_pay.setOnClickListener(this);
        loadDatas();
    }

    private void loadDatas() {
        //首先判断网络
        ConnectivityManager mConnectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info == null || !mConnectivity.getBackgroundDataSetting()) {
            Log.e("jxf", "没网");
            DefinedSingleToast.showToast(PayInterfaceActivity.this, getResources().getString(R.string.network_no_force));
        }
        else{
            Log.e("jxf","有网");
            netRequestAndResponse();
        }
    }

    private void netRequestAndResponse() {
        RequestParams params = new RequestParams();
        params.put("resourceId", id);
        NetClient.headGet(this, Url.PAY_CHANNEL, params, new NetResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.e("jxf","loadDatas时候onfail了"+throwable.toString());
                DefinedSingleToast.showToast(PayInterfaceActivity.this,getResources().getString(R.string.network_no_force));
            }
            @Override
            public void onResponse(String json){
                try {
                    JSONObject jSONObjectAll = new JSONObject(json);
                    Log.e("jxf", "付费频道json" + json);
                    String status = jSONObjectAll.optString("status");
                    if (status.equals("1")){
                        JSONObject obj=jSONObjectAll.optJSONObject("datas");
                        channelId=obj.getInt("id");
                        channelName = obj.getString("channelName");
                        channelPrice=obj.getDouble("channelPrice");
                        String categoryName=obj.getString("categoryName");
                        String iconUrl=obj.getString("iconUrl");
                        String des=obj.getString("des");
                        goto_pay.setEnabled(true);
                        ImageLoader.getInstance().displayImage(iconUrl, music_picture, ImageLoaderOptions.playRoundOption);
                        channel_belong.setText(channelName);
                        channel_intro.setText(des);
                        content_category.setText(categoryName);
                        content_price.setText(getResources().getString(R.string.money)+(int)channelPrice+getResources().getString(R.string.subscription_everymouth));
                    }
                    else{
                        Log.e("jxf","loadDatas时候请求成功了但是没有数据");
                        DefinedSingleToast.showToast(PayInterfaceActivity.this,getResources().getString(R.string.network_no_force));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("jxf", "loadDatas时候catch异常了"+e.toString());
                }
            }
        });
        }

    private void getBundle() {
        Bundle bundle=getIntent().getExtras();
        teacherId=bundle.getInt("teacherId");
        portraitUrlSmall=bundle.getString("portraitUrlSmall");
        nickname=bundle.getString("nickname");
        id= bundle.getInt("id");
        title=bundle.getString("title");
    }

    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_pay_buy);
    }

    @Override
    protected void onClickEvent(View view) {
        switch (view.getId()){
            case R.id.paychannel_left:
//                setResult(620);
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                break;
            case R.id.goto_pay:
                Log.e("jxf", "付费频道订阅点击去请求时间");
                loadTime();
                break;
        }

    }

    private void loadTime() {
        ConnectivityManager mConnectivity = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info == null || !mConnectivity.getBackgroundDataSetting()) {
            Log.e("jxf", "付费频道跳转请求时间:没网");
            DefinedSingleToast.showToast(this,getResources().getString(R.string.network_no_force));
        }
        else{
            Log.e("jxf","付费频道跳转请求时间:有网");
            netRequestAndResponseTime();
        }
    }

    private void netRequestAndResponseTime() {
        RequestParams params = new RequestParams();
        params.put("channelId", channelId);
        params.put("userId", (int) SharedPreUtils.get(App.getContext(), "user_id", 0));
        Log.e("jxf","付费频道跳转请求时间携带参数"+params);
        NetClient.headGet(PayInterfaceActivity.this, Url.PAY_STARTANDEND_TIME, params, new NetResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.e("jxf","付费频道跳转请求时间onfail：不做任何跳转：提示用户");
                DefinedSingleToast.showToast(PayInterfaceActivity.this,getResources().getString(R.string.network_no_force));
            }

            @Override
            public void onResponse(String json) {
                try {
                    JSONObject jSONObjectAll = new JSONObject(json);
                    Log.e("jxf", "付费频道跳转支付页请求时间得到响应的json" + json);
                    String status=jSONObjectAll.optString("status");
                    //请求成功有需要的数据:同时在此位置传递数据使用
                    if (status.equals("1")){
                        JSONObject obj=jSONObjectAll.optJSONObject("datas");
                        String startTime=obj.getString("startTime");
                        String endTime=obj.getString("endTime");
                        Intent intent = new Intent(PayInterfaceActivity.this, SubscribeForPayActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("lecturer_head", portraitUrlSmall);
                        bundle.putString("lecturer_name", nickname);
                        bundle.putInt("teacherId", teacherId);
                        bundle.putDouble("Price", channelPrice);
                        bundle.putString("channel_name", channelName);
                        bundle.putInt("channel_Id", channelId);
                        bundle.putString("startTime", startTime);
                        bundle.putString("endTime", endTime);
                        bundle.putInt("source", 3);
                        Log.e("jxf", "付费频道跳转支付页携带的信息" + "bundle::" + bundle);
                        intent.putExtras(bundle);
                        PayInterfaceActivity.this.startActivityForResult(intent, 700);
                        PayInterfaceActivity.this.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }else{
                        //错误返回：提示用户
                        Log.e("jxf","付费频道跳转支付页请求时间请求下来数据有：但是不是要求的格式");
                        DefinedSingleToast.showToast(PayInterfaceActivity.this, getResources().getString(R.string.network_no_force));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("jxf", "channel页adapter跳转支付页请求时间:onresponse出现异常" + e.toString());
                    DefinedSingleToast.showToast(PayInterfaceActivity.this, getResources().getString(R.string.network_no_force));
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==700){
            //支付成功
            if (resultCode==710){
                Log.e("jxf", "付费频道界面：支付界面购买成功：返回应该刷新界面");
                PayInterfaceActivity.this.setResult(610);
                PayInterfaceActivity.this.finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);

            }
            //支付失败
            else if (resultCode==720){
                Log.e("jxf","付费频道界面：支付界面购买失败：返回不做任何操作");
                PayInterfaceActivity.this.setResult(620);
                PayInterfaceActivity.this.finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            }

        }
    }

    @Override
    public void onBackPressed() {
        //手机返回按键手指触发：按照支付失败处理
        //super.onBackPressed();
        //setResult(620);
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
}
