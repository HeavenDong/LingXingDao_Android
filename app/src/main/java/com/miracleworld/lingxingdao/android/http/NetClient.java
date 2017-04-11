package com.miracleworld.lingxingdao.android.http;

import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.miracleworld.lingxingdao.android.App;
import com.miracleworld.lingxingdao.android.utils.CommanUtil;
import com.miracleworld.lingxingdao.android.utils.SharedPreUtils;

/**
 * Created by donghaifeng on 2015/12/16
 */
public class NetClient {

    private static AsyncHttpClient client = new AsyncHttpClient();

    public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setTimeout(5000);
        client.get(url, params, responseHandler);
    }

    public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setTimeout(5000);
        client.post(url, params, responseHandler);
    }
    public static void headPost(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setTimeout(20000);
        Log.e("jxf", "headPost提交ConnectTimeout" + client.getConnectTimeout());
        Log.e("jxf","headPost提交ResponseTimeout"+client.getResponseTimeout());
        String mobile = (String)SharedPreUtils.get(context,"user_mobile","");
        String password = (String)SharedPreUtils.get(context,"user_password","");
        client.addHeader("User-Agent", App.httpUserAgent);
        Log.e("jxf", "headPost提交请求携带头部信息userLoginName" + mobile);
        client.addHeader("userLoginName", mobile);
        Log.e("jxf", "请求携带头部信息passwd" + CommanUtil.md5Hex(password, true));
        client.addHeader("passwd", CommanUtil.md5Hex(password,true));
        // TODO: 2016/2/26 压缩
        client.post(url, params, responseHandler);
    }
    public static void headGet(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
        client.setTimeout(5000);
        Log.e("jxf", "headGet提交ConnectTimeout" + client.getConnectTimeout());
        Log.e("jxf", "headGet提交ResponseTimeout" + client.getResponseTimeout());
        String mobile = (String)SharedPreUtils.get(context,"user_mobile","");
        String password = (String)SharedPreUtils.get(context,"user_password","");
        Log.e("jxf", "headGet提交请求携带头部信息useragent" + App.httpUserAgent);
        client.addHeader("User-Agent", App.httpUserAgent);
        Log.e("jxf", "headGet提交请求携带头部信息userLoginName" + mobile);
        client.addHeader("userLoginName", mobile);
        Log.e("jxf", "请求携带头部信息passwd"+CommanUtil.md5Hex(password, true));
        client.addHeader("passwd", CommanUtil.md5Hex(password, true));
        // TODO: 2016/2/26 压缩
        client.get(url, params, responseHandler);
    }
}

