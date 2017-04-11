package com.miracleworld.lingxingdao.android.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.miracleworld.lingxingdao.android.options.Constants;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 微信支付  App注册广播
 */
public class AppRegist extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final IWXAPI api= WXAPIFactory.createWXAPI(context, null);
        api.registerApp(Constants.APP_ID);
    }
}
