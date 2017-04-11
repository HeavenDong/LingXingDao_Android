package com.miracleworld.lingxingdao.android.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.miracleworld.lingxingdao.android.App;
import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.options.Constants;
import com.miracleworld.lingxingdao.android.view.DefinedSingleToast;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 微信支付，结果页
 */
public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {
    private IWXAPI api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //保持空白页
        api= WXAPIFactory.createWXAPI(this, Constants.APP_ID);
        api.handleIntent(getIntent(),this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(getIntent(), this);
    }

    @Override
    public void onReq(BaseReq baseReq) {

    }

    @Override
    public void onResp(BaseResp baseResp) {
        if (baseResp.getType()== ConstantsAPI.COMMAND_PAY_BY_WX){
            switch (baseResp.errCode){
                //支付成功
                case 0:
                    if (Constants.activityTag == 1) {
                        /**服务器再次确认支付成功*/
                        Log.e("jxf","支付回调页面成功");
                        DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.wxpay_text1));
                        Intent intent0=new Intent();
                        intent0.setAction("wxPay_callBack_refresh");
                        intent0.putExtra("payResult", "0");
                        sendBroadcast(intent0);
                        finish();
                        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    }
                    else if (Constants.activityTag == 2){
                        Log.e("jxf","支付回调页面成功");
                        DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.wxpay_text1));
                        Intent intent0=new Intent();
                        intent0.setAction("wxPay_callBack_refresh_ticket");
                        intent0.putExtra("payResult", "0");
                        sendBroadcast(intent0);
                        finish();
                        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    }

//                     netResult();
                    break;
                /**支付失败:错误*/
                case -1:
                    if (Constants.activityTag == 1) {
                        Log.e("jxf","支付回调页面成功：有错误");
                        DefinedSingleToast.showToast(App.getInstance(),getResources().getString(R.string.wxpay_text2));
                        Intent intent1=new Intent();
                        intent1.setAction("wxPay_callBack_refresh");
                        intent1.putExtra("payResult", "-1");
                        sendBroadcast(intent1);
                        finish();
                        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    }
                    else if (Constants.activityTag == 2){
                        Log.e("jxf","支付回调页面成功：有错误");
                        DefinedSingleToast.showToast(App.getInstance(),getResources().getString(R.string.wxpay_text2));
                        Intent intent1=new Intent();
                        intent1.setAction("wxPay_callBack_refresh_ticket");
                        intent1.putExtra("payResult", "-1");
                        sendBroadcast(intent1);
                        finish();
                        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    }
                    break;
                /**取消支付*/
                case -2:
                    if (Constants.activityTag == 1) {
                        Log.e("jxf","支付回调页面成功：用户取消");
                        DefinedSingleToast.showToast(App.getInstance(),getResources().getString(R.string.wxpay_text3));
                        Intent intent2=new Intent();
                        intent2.setAction("wxPay_callBack_refresh");
                        intent2.putExtra("payResult", "-2");
                        sendBroadcast(intent2);
                        finish();
                        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    }
                    else if (Constants.activityTag == 2){
                        Log.e("jxf","支付回调页面成功：用户取消");
                        DefinedSingleToast.showToast(App.getInstance(),getResources().getString(R.string.wxpay_text3));
                        Intent intent2=new Intent();
                        intent2.setAction("wxPay_callBack_refresh_ticket");
                        intent2.putExtra("payResult", "-2");
                        sendBroadcast(intent2);
                        finish();
                        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    }
                    break;
            }
        }
    }

//    private void netResult() {
//        //请求服务器是否支付成功
//        //服务器支付成功才是真的成功
//        DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.wxpay_text1));
//        Intent intent0=new Intent();
//        intent0.setAction("wxPay_callBack_refresh");
//        intent0.putExtra("payResult", "0");
//        sendBroadcast(intent0);
//        finish();
//
//    }
}
