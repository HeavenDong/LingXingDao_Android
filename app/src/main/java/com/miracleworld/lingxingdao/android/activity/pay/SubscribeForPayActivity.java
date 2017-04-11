package com.miracleworld.lingxingdao.android.activity.pay;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.loopj.android.http.RequestParams;
import com.miracleworld.lingxingdao.android.App;
import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.alipayRelations.PayResult;
import com.miracleworld.lingxingdao.android.alipayRelations.SignUtils;
import com.miracleworld.lingxingdao.android.base.BaseActivity;
import com.miracleworld.lingxingdao.android.bean.WXPayBean;
import com.miracleworld.lingxingdao.android.http.NetClient;
import com.miracleworld.lingxingdao.android.http.NetResponseHandler;
import com.miracleworld.lingxingdao.android.http.Url;
import com.miracleworld.lingxingdao.android.options.Constants;
import com.miracleworld.lingxingdao.android.options.ImageLoaderOptions;
import com.miracleworld.lingxingdao.android.utils.CommanUtil;
import com.miracleworld.lingxingdao.android.utils.SharedPreUtils;
import com.miracleworld.lingxingdao.android.view.DefinedSingleToast;
import com.miracleworld.lingxingdao.android.view.myroundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import org.apache.http.Header;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

/**
 *    订阅支付
 */
public class SubscribeForPayActivity extends BaseActivity{
    private static final String TAG = "jxf";
    private RoundedImageView pay_avatar_img;
    private TextView pay_lecturer_name,pay_channel_name,pay_lecturer_time,pay_time,pay_lecturer_money,pay_money,pay_tv,pay_send_editcode;
    private CheckBox pay_weixin_box,pay_zhifubao_box,pay_daijinka_box;
    private EditText pay_daijinka,pay_message_code;
    private RelativeLayout pay_sure;
    private int way=-1;
    //上页传递的值
    private int teacherId,channelId;
    private String lecturer_head,lecturer_name,channel_name;
    private double amount;
    private String startTime;
    private String endTime;
    private  int source;

    //支付宝需要的信息静态常量
    // 商户PID商户的 ：共有的不是app的
    public static final String PARTNER = "2088121828454106";
    // 商户收款账号：商户的登录的账号
    public static final String SELLER = "lxd@58tou.com.cn";
    // 商户私钥，pkcs8格式:下载demo中exe生成的
    public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAPfoDbhidzH2HdKjTy5p+CwhpXJ0dGdvb8Cvf6keGXKgI61GmjGFh2AZ9JdaroQQuxQ7o4SAa///ReBXAP1ad3Yva6URnP/8V6DOAekF40Jv1FisAcFIdnJegYimZJfBc7ow0OuZPf4enLVeLYUkCtUoNg6Xx8c133lOGM97O7JVAgMBAAECgYB2fyc/wWE0Mm5i5sjMaL7FaXfJ07xoTK3gLoMY9Vg4oC8tfhfqH+drmjx9tEzCt7SUoUUx6qi0/vIJn8zLTVjX5ky94ixxzWjq02BomO/XNYWk7+6/cAdGDt7DxUuNvDu/0G9RrhO0YkGm/GFfA72x2LnzXFu+KjARGgmQTnQbaQJBAP7zijfFyd1Z7sVzWwLTtaXLtDYZJpykz7GA/GCxc0UQOd7wbEZDM9jkl0ITBEtC3f+eJtFBsysSO91yjFEovIcCQQD47Rh1CwGsbaKifkasteJbRzRXrKA8+5L3wQdoy+EZrHH8AuasfF0To9+PYGmg/g1NNMbbCwjUoo370LjPZI1DAkABT51JViIImlrI9yPjqtUHSjneAVkaexp6TjB+CsuN8lxp0hCsd9H/boV8mH5wKKLdmqGWd+EE+q4GIH2qOSxnAkBPa+Z3wZFBIKHZUozeIhIcqXVL2+osSuAzaEUi7JnfhSPBEnPi6LMRxyFXL53EHgrEbWhdDwI8RbkjdQ7iLOTdAkEAzH8Q3q/fXwdN1Q/4BOhpGeHAu3ykdlOyG1M+Z+7LzkA4PLR+qSxl5QocmDXULcgVw/E+IfINKslc2EXkRobuNg==";
    // 支付宝公钥:不用管
    public static final String RSA_PUBLIC = "";
    private static final int SDK_PAY_FLAG = 1;
    //private static final int SDK_CHECK_FLAG = 2;

    //网路请求服务器的订单信息
    //订单号
    private String ordersn;
    //订单id
    private int id;
    //代金卡号
    private String daiJinKaNum;
    //代金卡的短信验证码
    private String daiJinKaTest;
    //请求前注册微信
    private final IWXAPI msgApi = WXAPIFactory.createWXAPI(this,null);
    //private  PayReq req;
    private BroadcastReceiver WXCallBackeRefresh=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("jxf","接收到广播");
            //0 成功 -1失败  -2 取消
            String payResult=intent.getStringExtra("payResult");
            Log.e("jxf","支付结果"+payResult);
            if (payResult.equals("0")){
                Log.e("jxf", "支付页得到广播开启内容列表刷新的广播");
//                Intent intent1=new Intent();
//                intent1.setAction("pay_success_back");
//                intent1.putExtra("teacherId", teacherId);
//                sendBroadcast(intent1);
                orderCallBack(1, "1");
//                if (source==0){
//                    SubscribeForPayActivity.this.setResult(480);
//                }
//                SubscribeForPayActivity.this.finish();
//                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            }
            if (payResult.equals("-1")){
                orderCallBack(1,"-1");
//                if (source==0){
//                    SubscribeForPayActivity.this.setResult(481);
//                }
//                SubscribeForPayActivity.this.finish();
//                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            }
            if (payResult.equals("-2")){
                orderCallBack(1,"-1");
//                if (source==0){
//                    SubscribeForPayActivity.this.setResult(481);
//                }
//                SubscribeForPayActivity.this.finish();
//                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            }
        }
    };
    //获取代金卡验证码的读秒延时
    int i = 90;
    private Boolean codeTag = true;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (i == 1) {
                        codeTag = true;
                        pay_send_editcode.setText(getResources().getString(R.string.regist_text1));
                        pay_send_editcode.setClickable(true);
                        pay_send_editcode.setBackgroundResource(R.drawable.blue_fillet_background);
                        i = 90;
                        mHandler.removeMessages(0);
                    } else {
                        i--;
                        pay_send_editcode.setText(getResources().getString(R.string.regist_text0) + i + "s)");
                        pay_send_editcode.setTextSize(11);
                        pay_send_editcode.setClickable(false);
                        mHandler.sendEmptyMessageDelayed(0, 1000);
                        pay_send_editcode.setBackgroundResource(R.drawable.grey_fillet_background);
                    }

                    break;
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(SubscribeForPayActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
//                        Intent intentalip=new Intent();
//                        intentalip.setAction("pay_success_back");
//                        intentalip.putExtra("teacherId", teacherId);
//                        sendBroadcast(intentalip);
                        orderCallBack(2,"1");
//                        if (source==0){
//                            SubscribeForPayActivity.this.setResult(480);
//                        }
//                        SubscribeForPayActivity.this.finish();
//                        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
//                        if (TextUtils.equals(resultStatus, "8000")) {
//                            Toast.makeText(SubscribeForPayActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();
//
//                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(SubscribeForPayActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                            orderCallBack(2,"-1");
//                            if (source==0){
//                                SubscribeForPayActivity.this.setResult(481);
//                            }
//                            SubscribeForPayActivity.this.finish();
//                            overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
//                        }
                    }
                    break;
                }
//                case SDK_CHECK_FLAG: {
//                    Toast.makeText(SubscribeForPayActivity.this, "检查结果为：" + msg.obj, Toast.LENGTH_SHORT).show();
//                    break;
//                }
                default:
                    break;
            }
        }
    };


    @Override
    protected void initView() {
        msgApi.registerApp(Constants.APP_ID);
        IntentFilter filter=new IntentFilter();
        filter.addAction("wxPay_callBack_refresh");
        registerReceiver(WXCallBackeRefresh, filter);
        Bundle b=getIntent().getExtras();
        lecturer_head=b.getString("lecturer_head");
        lecturer_name=b.getString("lecturer_name");
        teacherId=b.getInt("teacherId");
        amount = b.getDouble("Price");
        channel_name=b.getString("channel_name");
        channelId=b.getInt("channel_Id");
        startTime=b.getString("startTime");
        endTime=b.getString("endTime");
        source = b.getInt("source");
        Log.e("jxf","跳转到支付页面携带的老师id"+teacherId);
        Log.e("jxf","启动支付页面的来源"+source);
        pay_avatar_img=(RoundedImageView)findViewById(R.id.pay_avatar_img);
        pay_lecturer_name = (TextView) findViewById(R.id.pay_lecturer_name);
        pay_channel_name=(TextView) findViewById(R.id.pay_channel_name);
        pay_lecturer_time = (TextView) findViewById(R.id.pay_lecturer_time);
        pay_time= (TextView) findViewById(R.id.pay_time);
        pay_lecturer_money = (TextView) findViewById(R.id.pay_lecturer_money);
        pay_money = (TextView) findViewById(R.id.pay_money);
        pay_daijinka= (EditText) findViewById(R.id.pay_daijinka);
        pay_message_code= (EditText) findViewById(R.id.pay_message_code);
        pay_weixin_box = (CheckBox) findViewById(R.id.pay_weixin_box);
        pay_zhifubao_box = (CheckBox) findViewById(R.id.pay_zhifubao_box);
        pay_daijinka_box = (CheckBox) findViewById(R.id.pay_daijinka_box);
        pay_send_editcode=(TextView) findViewById(R.id.pay_send_editcode);
        pay_sure = (RelativeLayout) findViewById(R.id.pay_sure);
        pay_tv=(TextView)findViewById(R.id.pay_tv);
        initData();
    }

    private void initData() {
        ImageLoader.getInstance().displayImage(lecturer_head, pay_avatar_img, ImageLoaderOptions.headOptions);
        pay_lecturer_name.setText(lecturer_name);
        pay_channel_name.setText(channel_name);
        pay_lecturer_time.setText("一个月");
        String startTimeTemp=startTime.replace("-",".").split(" ")[0];
        String endTimeTemp=endTime.replace("-",".").split(" ")[0];
        pay_time.setText(startTimeTemp+"-"+endTimeTemp);
        pay_lecturer_money.setText(String.valueOf((int)amount));
        pay_money.setText(String.valueOf((int)amount));
        findViewById(R.id.pay_title_left).setOnClickListener(this);
        findViewById(R.id.order_details_weixin_relat).setOnClickListener(this);
        findViewById(R.id.order_details_zhifubao_relat).setOnClickListener(this);
        findViewById(R.id.order_details_daijinka_relat).setOnClickListener(this);
        pay_send_editcode.setOnClickListener(this);
        pay_sure.setOnClickListener(this);
        /**设置代金卡输入框不可输入*/
        pay_daijinka.setInputType(InputType.TYPE_NULL);
        pay_message_code.setInputType(InputType.TYPE_NULL);
        pay_daijinka.setOnClickListener(this);
        pay_message_code.setOnClickListener(this);
    }

    @Override
    public void setContentLayout() {
        setContentView(R.layout.pay_subscribe);
    }

    @Override
    protected void onClickEvent(View view) {
        switch (view.getId()){
            case R.id.pay_title_left:
                //正常返回上一级：逐级返回：不做任何处理
//                if (source==0){
//                    setResult(502);
//                }else if(source==1){
//                    setResult(482);
//                }
//                else if (source==3){
//                    setResult(720);
//                }
                Log.e("jxf","支付页头上返回键返回");
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                break;
            /**代金卡的输入框*/
            case R.id.pay_daijinka:
                if (way!=2){
                    DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.carpay_text2));
                }
                break;
            case R.id.pay_message_code:
                if (way!=2){
                    DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.carpay_text2));
                }
                break;
            case R.id.pay_send_editcode:
                if(CommanUtil.isNetworkAvailable()) {
                    if (way==2) {
                        //代金卡不为空
                        if (TextUtils.isEmpty(pay_daijinka.getText().toString())) {
                            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.pay_toast1));
                            //不符合规则
                        } else if (!CommanUtil.isGiftCade(pay_daijinka.getText().toString())) {
                            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.pay_toast4));
                        } else {
                            getEditCodeAndNext(pay_daijinka.getText().toString(), SharedPreUtils.get(App.getContext(), "user_mobile", "").toString());
                        }
                    }else {
                        DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.carpay_text2));
                    }
                }
                else {
                    DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.toast_net));
                }
                break;
            case R.id.order_details_weixin_relat:
                Log.e(TAG, "选微信");
                /**设置代金卡输入框不可输入*/
                pay_send_editcode.setText(getResources().getString(R.string.regist_text18));
                pay_send_editcode.setClickable(true);
                pay_send_editcode.setBackgroundResource(R.drawable.blue_fillet_background);
                i = 90;
                mHandler.removeMessages(0);
                pay_daijinka.setText("");
                pay_message_code.setText("");
                pay_daijinka.setInputType(InputType.TYPE_NULL);
                pay_message_code.setInputType(InputType.TYPE_NULL);
                pay_weixin_box.setChecked(true);
                pay_zhifubao_box.setChecked(false);
                pay_daijinka_box.setChecked(false);
                if (pay_weixin_box.isChecked()) {
                    way=0;
                }else{
                    way=-1;
                }
                break;
            case R.id.order_details_zhifubao_relat:
                Log.e(TAG,"选支付宝");
                /**设置代金卡输入框不可输入*/
                pay_send_editcode.setText(getResources().getString(R.string.regist_text18));
                pay_send_editcode.setClickable(true);
                pay_send_editcode.setBackgroundResource(R.drawable.blue_fillet_background);
                i = 90;
                mHandler.removeMessages(0);
                pay_daijinka.setText("");
                pay_message_code.setText("");
                pay_daijinka.setInputType(InputType.TYPE_NULL);
                pay_message_code.setInputType(InputType.TYPE_NULL);
                pay_zhifubao_box.setChecked(true);
                pay_weixin_box.setChecked(false);
                pay_daijinka_box.setChecked(false);
                if (pay_zhifubao_box.isChecked()) {
                    way=1;
                }else{
                    way=-1;
                }
                break;
            case R.id.order_details_daijinka_relat:
                Log.e(TAG,"选代金卡");
                /**设置输入框可以输入*/
                pay_daijinka.setInputType(InputType.TYPE_CLASS_TEXT);
                pay_message_code.setInputType(InputType.TYPE_CLASS_TEXT);
                pay_daijinka_box.setChecked(true);
                pay_weixin_box.setChecked(false);
                pay_zhifubao_box.setChecked(false);
                if (pay_daijinka_box.isChecked()) {
                    way=2;
                }else{
                    way=-1;
                }
                break;
            case R.id.pay_sure:
                if(CommanUtil.isNetworkAvailable()) {
                    /**未选*/
                    if (way==-1){
                        DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.pay_toast3));
                    }
                    /**代金卡*/
                    if (way==2) {
                        if (TextUtils.isEmpty(pay_daijinka.getText().toString())) {
                            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.pay_toast1));
                            break;
                        } else if (!CommanUtil.isGiftCade(pay_daijinka.getText().toString())) {
                            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.pay_toast4));
                            break;
                        } else if (TextUtils.isEmpty(pay_message_code.getText().toString())) {
                            Log.e(TAG, "验证码空");
                            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.pay_toast2));
                            break;
                        } else {
                            daiJinKaNum=pay_daijinka.getText().toString();
                            daiJinKaTest=pay_message_code.getText().toString();
                            pay_sure.setBackgroundResource(R.drawable.grey_fillet_background);
                            pay_sure.setClickable(false);
                            getOrdersn();
                        }
                    }
                    /**微信*/
                    if(way==0){
                        if(isWXAppInstalledAndSupported()) {
                            pay_sure.setBackgroundResource(R.drawable.grey_fillet_background);
                            pay_sure.setClickable(false);
                            Constants.activityTag=1;
                            getOrdersn();
                        }else {
                            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.pay_toast5));
                        }
                    }
                    /**支付宝*/
                    if(way==1){
                        pay_sure.setBackgroundResource(R.drawable.grey_fillet_background);
                        pay_sure.setClickable(false);
                        getOrdersn();
                    }
                }else {
                    DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.toast_net));
                }
                break;

        }
    }
    /**
     * 代金卡验证
     */
    private void getEditCodeAndNext(String card, String phone) {
        codeTag = false;
        pay_send_editcode.setClickable(false);
        RequestParams params = new RequestParams();
        params.put("mobile", phone);
        params.put("card", card);
        Log.e(TAG, "《点击获取代金卡验证》: 发给后台--" + params);
        NetClient.headPost(this, Url.GIFTCAR_PAY_SENDCODE_URL, params, new NetResponseHandler() {
            @Override
            public void onResponse(String json) {
                Log.e(TAG, "《点击获取代金卡验证》: 后台返回--" + json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String status = jsonObject.getString("status");
                    String errorCode  = jsonObject.getString("errorCode");
                    if (status.equals("1")) {
                        DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text28));
                        mHandler.sendEmptyMessage(0);
                    } else {
                            pay_send_editcode.setClickable(true);
                            if (errorCode.equals("11010")) {
                                DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text27));
                            }
                            if (errorCode.equals("90001")) {
                                DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text24));
                            }
                            if (errorCode.equals("90002")) {
                                DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.send_error_text));
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        pay_send_editcode.setClickable(true);
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.toast_net));
                    pay_send_editcode.setClickable(true);
                }
            });
    }

    private void getOrdersn(){
        RequestParams params = new RequestParams();
        params.put("userId", (int) SharedPreUtils.get(App.getContext(), "user_id", 0));
        params.put("teacherId", teacherId);
        params.put("amount", amount);
        //添加字段：频道ID
        params.put("channelId", channelId);
        params.put("startDate",startTime);
        params.put("endDate",endTime);
        Log.e(TAG, "获取服务器生成订单号" + params);
        NetClient.headPost(SubscribeForPayActivity.this, Url.GET_PAY_ORDERS_URL, params, new NetResponseHandler() {
            @Override
            public void onResponse(String json) {
                Log.e(TAG, "获取订单号服务器返回" + json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String status = jsonObject.optString("status");
                    String errorCode = jsonObject.optString("errorCode");
                    if (status.equals("1")) {
                        JSONObject  jsonObject2 =jsonObject.optJSONObject("datas");
                        ordersn=jsonObject2.getString("ordersn");
                        id=jsonObject2.getInt("id");
                        amount=jsonObject2.getDouble("amount");
                        Log.e(TAG, "服务器返回订单号=" + ordersn+"服务器返回订单id="+id+"amount价钱="+amount);
                        /**订单号生成后，调第三方支付*/
                        if (way==0){
                            Log.e(TAG, "微信支付生成订单号成功：开始生成预支付订单");
                            //访问自己服务器：生成预支付订单
                            weixinLoadData(ordersn);
                        }
                        if (way==1){
                            Log.e(TAG, "支付宝支付");
                            zhifubaoLoadData(ordersn);
                        }
                        if (way==2){
                            Log.e(TAG, "代金卡支付");
                            daijinkaLoadData();
                        }

                    } else {
                        Log.e(TAG, "服务器订单号失败");
                        pay_sure.setBackgroundResource(R.drawable.green_fillet_background);
                        pay_sure.setClickable(true);
                        if (errorCode.equals("90001")) {
                            DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.regist_text24));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    pay_sure.setBackgroundResource(R.drawable.green_fillet_background);
                    pay_sure.setClickable(true);
                    Log.e(TAG, "生产订单号失败");
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.toast_net));
                Log.e(TAG, "服务器生成订单号失败onfail");
                pay_sure.setBackgroundResource(R.drawable.green_fillet_background);
                pay_sure.setClickable(true);
            }
        });
    }


    private void weixinLoadData(String ordersn) {
        ConnectivityManager mConnectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info == null || !mConnectivity.getBackgroundDataSetting()) {
            Log.e("jxf","没网");
            pay_sure.setBackgroundResource(R.drawable.green_fillet_background);
            pay_sure.setClickable(true);
            DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.toast_net));
        }
        else{
            Log.e("jxf", "有网开始预支付订单的请求");
            RequestParams params1 = new RequestParams();
            params1.put("appid",Constants.APP_ID);   //微信开放平台设置的AppID，固定值  wx58e0403eaf64980c
            params1.put("mch_id",Constants.MCH_ID);  //微信支付商户号，微信商户平台申请 固定值， 1285906901
            params1.put("ipaddress", "123.120.199.92");//用访问ip 暂无限制，123.120.199.92
            params1.put("orderBody", lecturer_name);//订单名称：老师名称
            params1.put("orderSn", ordersn);//订单号，每次请求预支付接口，订单号必须不相同
            params1.put("total_fee",((int)(amount*100)));//金额 *100：微信生成订单号的时候要*100
            params1.put("type", "Android"); //用户类型，Android 或 iOS

            params1.put("orderType",1);
            Log.e(TAG, "预支付订单上传参数" + params1);
            NetClient.headPost(SubscribeForPayActivity.this, Url.WEIXIN_PAY_URL, params1, new NetResponseHandler() {
                @Override
                public void onResponse(String json) {
                    Log.e(TAG, "《微信支付》接受参数：：" + json);
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        String status = jsonObject.optString("status");
                        String errorCode = jsonObject.optString("errorCode");
                        Log.e("jxf","status==="+status+"----errorCode=="+errorCode);
                        //成功有返回1：
                        if (status.equals("1")) {
                            JSONObject obj = jsonObject.optJSONObject("datas");
                            Log.e(TAG, "《微信支付》预支付订单返回：" + obj.getString("nonce_str"));
                            WXPayBean wxPayBean = new WXPayBean();
                            wxPayBean.setNonceStr(obj.optString("nonce_str"));//随机字符串
                            wxPayBean.setNotifyUrl(obj.optString("notify_url"));//微信回调页面
                            wxPayBean.setPrepayId(obj.optString("prepay_id"));  //微信预支付订单号
                            wxPayBean.setSign(obj.optString("sign"));           //签名
                            wxPayBean.setTime(obj.optString("time"));           //时间戳
                            genPayReq(wxPayBean);
                        } else {
                            if (errorCode.equals("90001")) {
                                DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.regist_text24));
                                pay_sure.setBackgroundResource(R.drawable.green_fillet_background);
                                pay_sure.setClickable(true);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e(TAG, "微信支付失败，异常了");
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.e(TAG, "微信支付预支付订单失败");
                    pay_sure.setBackgroundResource(R.drawable.green_fillet_background);
                    pay_sure.setClickable(true);
                    DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.toast_net));
                }
            });
        }
    }

    private void genPayReq(WXPayBean wxPayBean) {
        PayReq req= new PayReq();
        req.appId = Constants.APP_ID;
        req.partnerId = Constants.MCH_ID;
        req.prepayId = wxPayBean.getPrepayId();
        req.packageValue = "Sign=WXPay";
        req.nonceStr = wxPayBean.getNonceStr();
        req.sign = wxPayBean.getSign();
        req.timeStamp =wxPayBean.getTime();
        List<NameValuePair> signParams = new LinkedList<NameValuePair>();
        signParams.add(new BasicNameValuePair("appid", req.appId));
        signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
        signParams.add(new BasicNameValuePair("packageValue", req.packageValue));
        signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
        signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
        signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));
        signParams.add(new BasicNameValuePair("sign", req.sign));
        Log.e("jxf", "上传微信支付需要的参数" + signParams.toString());
        msgApi.registerApp(Constants.APP_ID);
        msgApi.sendReq(req);
        Log.e("jxf","调用微信支付了");
    }

    private void zhifubaoLoadData(String ordersn) {
        ConnectivityManager mConnectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info == null || !mConnectivity.getBackgroundDataSetting()) {
            Log.e("jxf","没网");
            pay_sure.setBackgroundResource(R.drawable.green_fillet_background);
            pay_sure.setClickable(true);
            DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.toast_net));
        }else{
            Log.e("jxf","有网");
            if (TextUtils.isEmpty(PARTNER) || TextUtils.isEmpty(RSA_PRIVATE)
                    || TextUtils.isEmpty(SELLER)) {
                new AlertDialog.Builder(this)
                        .setTitle("警告")
                        .setMessage("需要配置PARTNER | RSA_PRIVATE| SELLER")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialoginterface, int i) {
                                        //
                                        finish();
                                    }
                                }).show();
                return;
            }
            Log.e("jxf","跳出判断");
           //String orderInfo = getOrderInfo(ordersn,lecturer_name, lecturer_name,""+amount);
            String orderInfo = getOrderInfo(ordersn,lecturer_name, lecturer_name,""+amount);
            // 对订单做RSA 签名
            String sign = sign(orderInfo);
            try {
                sign = URLEncoder.encode(sign, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            final String payInfo = orderInfo + "&sign=\"" + sign + "\"&"
                    + getSignType();

            Runnable payRunnable = new Runnable() {

                @Override
                public void run() {
                    PayTask alipay = new PayTask(SubscribeForPayActivity.this);
                    String result = alipay.pay(payInfo);
                    Message msg = new Message();
                    msg.what = SDK_PAY_FLAG;
                    msg.obj = result;
                    mHandler.sendMessage(msg);
                }
            };
            Thread payThread = new Thread(payRunnable);
            payThread.start();
        }

    }

    private void daijinkaLoadData() {
        ConnectivityManager mConnectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info == null || !mConnectivity.getBackgroundDataSetting()) {
            Log.e("jxf","代金卡支付没网");
            pay_sure.setBackgroundResource(R.drawable.green_fillet_background);
            pay_sure.setClickable(true);
            DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.toast_net));
        }
        else{
            Log.e("jxf", "有网开始代金卡支付");
            RequestParams params = new RequestParams();
            params.put("card",daiJinKaNum);
            params.put("code",daiJinKaTest);
            params.put("orderId",id);
            params.put("amount",amount);
            params.put("userId", SharedPreUtils.get(SubscribeForPayActivity.this, "user_id", 0));
            params.put("userName",SharedPreUtils.get(SubscribeForPayActivity.this,"user_name",""));
            params.put("teacherName",lecturer_name);
            params.put("orderType",1);

            Log.e(TAG, "代金卡订单上传参数" + params);
            NetClient.headPost(SubscribeForPayActivity.this, Url.DAIJINKA_PAY_URL, params, new NetResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.e("jxf", "代金卡支付链接失败");
                    DefinedSingleToast.showToast(SubscribeForPayActivity.this, "网络不给力：代金卡支付失败");
//                    if (source == 0) {
//                        SubscribeForPayActivity.this.setResult(481);
//                    }
//                    SubscribeForPayActivity.this.finish();
//                    overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    orderCallBack(3,"-1");
                }

                @Override
                public void onResponse(String json) {
                    Log.e("jxf", "代金卡支付请求返回字符串" + json);
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        String status = jsonObject.optString("status");
                        //支付成功
                        if (status.equals("1")) {
                            Log.e("jxf", "代金卡支付成功");
                            DefinedSingleToast.showToast(SubscribeForPayActivity.this, "代金卡支付成功");
//                            Intent intent1 = new Intent();
//                            intent1.setAction("pay_success_back");
//                            intent1.putExtra("teacherId", teacherId);
//                            sendBroadcast(intent1);
//                            if (source == 0) {
//                                SubscribeForPayActivity.this.setResult(480);
//                            }
//                            SubscribeForPayActivity.this.finish();
//                            overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                            orderCallBack(3,"1");

                        } else {
                            Log.e("jxf", "代金卡支付失败");
                            String errorCode = jsonObject.optString("errorCode");
                            if (errorCode.equals("90001")) {
                                DefinedSingleToast.showToast(SubscribeForPayActivity.this, "系统异常");
                            } else if (errorCode.equals("22002")) {
                                DefinedSingleToast.showToast(SubscribeForPayActivity.this, "验证码失效");
                            } else if (errorCode.equals("22001")) {
                                DefinedSingleToast.showToast(SubscribeForPayActivity.this, "验证码错误");
                            } else if (errorCode.equals("11011")) {
                                DefinedSingleToast.showToast(SubscribeForPayActivity.this, "代金卡失效");
                            } else if (errorCode.equals("11012")) {
                                DefinedSingleToast.showToast(SubscribeForPayActivity.this, "代金卡余额不足");
                            } else if (errorCode.equals("15001")) {
                                DefinedSingleToast.showToast(SubscribeForPayActivity.this, "无此订单");
                            }
//                            if (source == 0) {
//                                SubscribeForPayActivity.this.setResult(481);
//                            }
//                            SubscribeForPayActivity.this.finish();
//                            overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                            orderCallBack(3,"-1");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }

    private boolean isWXAppInstalledAndSupported() {
        IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);
        msgApi.registerApp(Constants.APP_ID);

        boolean sIsWXAppInstalledAndSupported = msgApi.isWXAppInstalled()
                && msgApi.isWXAppSupportAPI();
        return sIsWXAppInstalledAndSupported;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        if (WXCallBackeRefresh!=null){
            unregisterReceiver(WXCallBackeRefresh);
        }
    }

    public String getOrderInfo(String ordersn,String subject, String body, String price) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" +ordersn + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";
        // TODO: 2016/1/18 后期写死：固定值:必须是线上的服务器
        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + "http://notify.msp.hk/notify.htm"
                + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content
     *            待签名订单信息
     */
    private String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     *
     */
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }


    //提取公共方法
    private void  setResultCode(String result){
        if (result.equals("1")){
            if (source==0){
                SubscribeForPayActivity.this.setResult(910);
            }
            if(source==1){
                SubscribeForPayActivity.this.setResult(483);
            }
            if(source==3){
                SubscribeForPayActivity.this.setResult(710);
            }
            SubscribeForPayActivity.this.finish();
            overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        }
        else{
            if (source==0){
                SubscribeForPayActivity.this.setResult(920);
            }
            if(source==1){
                SubscribeForPayActivity.this.setResult(482);
            }
            if(source==3){
                SubscribeForPayActivity.this.setResult(720);
            }
            SubscribeForPayActivity.this.finish();
            overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
        }
    }
    //订单修改
    private void orderCallBack(int type, final String result){
        ConnectivityManager mConnectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info == null || !mConnectivity.getBackgroundDataSetting()) {
            Log.e("jxf","支付上传给服务器没网");
            pay_sure.setBackgroundResource(R.drawable.green_fillet_background);
            pay_sure.setClickable(true);
            DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.toast_net));
            setResultCode(result);

        }
        else {
            Log.e("jxf", "支付上传给服务器有网");
            RequestParams params = new RequestParams();
            params.put("id", id);
            params.put("type", type);
            params.put("status", result);
            Log.e(TAG, "支付上传给服务器上传参数" + params);
            NetClient.headPost(SubscribeForPayActivity.this, Url.ORDER_CALL_BACK, params, new NetResponseHandler() {
                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.e("jxf", "订单修改上传服务器：链接服务器失败");
                    setResultCode(result);
//                    if (result.equals("1")) {
//                        if (source == 0) {
//                            SubscribeForPayActivity.this.setResult(910);
//                        }
//                        if(source==1){
//                            SubscribeForPayActivity.this.setResult(483);
//                        }
//                        if(source==3){
//                            SubscribeForPayActivity.this.setResult(710);
//                        }
//                        SubscribeForPayActivity.this.finish();
//                        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
//                    } else {
//                        if (source == 0) {
//                            SubscribeForPayActivity.this.setResult(920);
//                        }
//                        if(source==1){
//                            SubscribeForPayActivity.this.setResult(482);
//                        }
//                        if(source==3){
//                            SubscribeForPayActivity.this.setResult(720);
//                        }
//                        SubscribeForPayActivity.this.finish();
//                        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
//                    }
                }

                @Override
                public void onResponse(String json) {
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        String status = jsonObject.optString("status");
                        if (status.equals("1")) {
                            Log.e("jxf", "订单修改上传服务器成功");
                            setResultCode(result);
//                            if (result.equals("1")) {
//                                //支付成功
//                                if (source == 0) {
//                                    SubscribeForPayActivity.this.setResult(910);
//                                }
//                                if(source==1){
//                                    SubscribeForPayActivity.this.setResult(483);
//                                }
//                                if(source==3){
//                                    SubscribeForPayActivity.this.setResult(710);
//                                }
//                                SubscribeForPayActivity.this.finish();
//                                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
//                            } else {
//                                if (source == 0) {
//                                    SubscribeForPayActivity.this.setResult(920);
//                                }
//                                if(source==1){
//                                    SubscribeForPayActivity.this.setResult(482);
//                                }
//                                if(source==3){
//                                    SubscribeForPayActivity.this.setResult(720);
//                                }
//                                SubscribeForPayActivity.this.finish();
//                                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
//                            }

                        } else {
                            Log.e("jxf", "订单修改上传服务器：返回错误码");
                            setResultCode(result);
//                            if (result.equals("1")) {
//                                if (source == 0) {
//                                    SubscribeForPayActivity.this.setResult(910);
//                                }
//                                if(source==1){
//                                    SubscribeForPayActivity.this.setResult(483);
//                                }
//                                if(source==3){
//                                    SubscribeForPayActivity.this.setResult(710);
//                                }
//                                SubscribeForPayActivity.this.finish();
//                                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
//                            } else {
//                                if (source == 0) {
//                                    SubscribeForPayActivity.this.setResult(920);
//                                }
//                                if(source==1){
//                                    SubscribeForPayActivity.this.setResult(482);
//                                }
//                                if(source==3){
//                                    SubscribeForPayActivity.this.setResult(720);
//                                }
//                                SubscribeForPayActivity.this.finish();
//                                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
//                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("jxf", "订单修改上传服务器：发生异常了！"+e.toString());
                    }

                }
            });
        }
    }

    @Override
    public void onBackPressed() {
        //正常返回：逐级返回：不做任何处理
        //super.onBackPressed();
//        if (source==0){
//            Log.e("jxf","支付页手机返回键点击：作为支付失败操作！：没有任何操作");
//            SubscribeForPayActivity.this.setResult(502);
//        }else if(source==1){
//            Log.e("jxf","支付页响应返回键返回：1：做支付失败");
//            SubscribeForPayActivity.this.setResult(482);
//        }
//        else if(source==3){
//            Log.e("jxf","支付页响应返回键返回：1：做支付失败");
//            SubscribeForPayActivity.this.setResult(720);
//        }
        SubscribeForPayActivity.this.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);

    }



}
