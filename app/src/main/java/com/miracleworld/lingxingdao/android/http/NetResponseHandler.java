package com.miracleworld.lingxingdao.android.http;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;

/**
 * Created by donghaifeng on 2015/12/16
 */
public abstract class NetResponseHandler extends AsyncHttpResponseHandler {


    public NetResponseHandler() {

    }


    @Override
    public void onStart() {

    }

    @Override
    public void onFinish() {

    }

    @Override
    public void onSuccess(int i, Header[] headers, byte[] bytes) {
        String json = new String(bytes);

        onResponse(json);
    }

    public abstract void onResponse(String json);

////  为了有缺省图片的显示：不能使用这个位置的方法，以后这个位置可能需要优化
//    @Override
//    public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
//
////        if (throwable instanceof HttpResponseException) {
////            erro(((HttpResponseException) throwable).getStatusCode());
////        } else {
////            erro(0);
////        }
//    }


//    protected void erro(int erroCode) {
//
//            if (erroCode == 404) {
//                DefinedSingleToast.showToast(App.getContext(),App.getContext().getResources().getString(R.string.server_not_given_resource));
//            }
//            else if (erroCode == 500) {
//                DefinedSingleToast.showToast(App.getContext(),App.getContext().getResources().getString(R.string.unexpected_no_request));
//            }
//            else if (erroCode == 503) {
//                DefinedSingleToast.showToast(App.getContext(),App.getContext().getResources().getString(R.string.temp_no_request));
//            }
//            else {
//                DefinedSingleToast.showToast(App.getContext(),App.getContext().getResources().getString(R.string.network_no_force));
//            }
//    }
}
