package com.miracleworld.lingxingdao.android.activity.login;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.miracleworld.lingxingdao.android.App;
import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.activity.MainActivity;
import com.miracleworld.lingxingdao.android.base.BaseActivity;
import com.miracleworld.lingxingdao.android.http.NetClient;
import com.miracleworld.lingxingdao.android.http.NetResponseHandler;
import com.miracleworld.lingxingdao.android.http.Url;
import com.miracleworld.lingxingdao.android.utils.CommanUtil;
import com.miracleworld.lingxingdao.android.utils.SharedPreUtils;
import com.miracleworld.lingxingdao.android.view.DefinedSingleToast;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;
/**
 * 注册页
 */
public class RegistActivity extends BaseActivity {
    private EditText registNumber,regist_send_editcode,rigist_password;
    private TextView send_Editcod,regist_agreement;
    private CheckBox regist_seepw;
    int i = 90;
    private Boolean codeTag = true;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    if (i == 1) {
                        codeTag = true;
                        send_Editcod.setText(getResources().getString(R.string.regist_text1));
                        send_Editcod.setClickable(true);
                        send_Editcod.setBackgroundResource(R.drawable.blue_fillet_background);
                        i = 90;
                        mHandler.removeMessages(0);
                    } else {
                        i--;
                        send_Editcod.setText(getResources().getString(R.string.regist_text0)+i + "s)");
                        send_Editcod.setTextSize(11);
                        send_Editcod.setClickable(false);
                        mHandler.sendEmptyMessageDelayed(0, 1000);
                        send_Editcod.setBackgroundResource(R.drawable.grey_fillet_background);
                    }

                    break;

                default:
                    break;
            }
        }
    };

    @Override
    public void setContentLayout() {
        setContentView(R.layout.regist_activity);
    }
    @Override
    protected void initView() {
        registNumber=(EditText)findViewById(R.id.rigist_number);
        regist_send_editcode=(EditText)findViewById(R.id.regist_send_editcode);
        rigist_password=(EditText)findViewById(R.id.rigist_password);
        regist_seepw=(CheckBox)findViewById(R.id.regist_seepw);

        send_Editcod = (TextView)findViewById(R.id.windowregist_butt_send_editcode);
        regist_agreement  = (TextView)findViewById(R.id.regist_agreement);
        send_Editcod.setOnClickListener(this);
        regist_seepw.setOnClickListener(this);
        regist_agreement.setOnClickListener(this);
        findViewById(R.id.regist_title_left).setOnClickListener(this);
        findViewById(R.id.protocol).setOnClickListener(this);

    }
    @Override
    protected void onClickEvent(View view) {
        switch (view.getId()) {
            /**用户协议*/
            case R.id.protocol:

                break;
            /**返回箭头*/
            case R.id.regist_title_left:
                finish();
                break;

            /**控制密码可见*/
            case R.id.regist_seepw:
                if (regist_seepw.isChecked()) {
                    rigist_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    Editable etable = rigist_password.getText();
                    Selection.setSelection(etable, etable.length());
                }else {
                    rigist_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    Editable etable = rigist_password.getText();
                    Selection.setSelection(etable, etable.length());
                }
                break;
            case R.id.windowregist_butt_send_editcode:
                if(CommanUtil.isNetworkAvailable()) {
                    if (TextUtils.isEmpty(registNumber.getText().toString())) {
                        DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_toast1));
                        return;
                    }
                    if (!CommanUtil.isMobilePhone(registNumber.getText().toString())) {
                        DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text2));
                        return;
                    }
                    send_Editcod.setClickable(false);
                    getEditCodeAndNext(registNumber.getText().toString());
                }else {
                    DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.toast_net));
                }

                break;
            case R.id.regist_agreement:
                if(CommanUtil.isNetworkAvailable()) {
                    if (TextUtils.isEmpty(registNumber.getText().toString())) {
                        DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_toast1));
                        return;
                    }else if (!CommanUtil.isMobilePhone(registNumber.getText().toString())) {
                        DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text2));
                        return ;
                    }else if (TextUtils.isEmpty(rigist_password.getText().toString())){
                        DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text11));
                        return;
                    }else if (!CommanUtil.isPSW(rigist_password.getText().toString())) {
                        DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text4));
                        return ;
                    } else if (TextUtils.isEmpty(regist_send_editcode.getText().toString())) {
                        DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text12));
                    }else{
                        regist_agreement.setClickable(false);
                        RequestParams params2=new RequestParams();
                        params2.put("mobile",registNumber.getText().toString());
                        params2.put("password", CommanUtil.md5Hex(rigist_password.getText().toString(), true));
                        params2.put("code", regist_send_editcode.getText().toString());
                        //注册操作
                        regist(params2);
                    }
                }else {
                    DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.toast_net));
                }

                break;

        }
    }

    /**
     * 短信验证
     */
    private void getEditCodeAndNext(String phone) {
        if (TextUtils.isEmpty(phone)) {
            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text3));
            return;
        }
        if (!CommanUtil.isMobilePhone(phone)) {
            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text2));
            return;
        }
//        codeTag = false;
        RequestParams params = new RequestParams();
        params.put("mobile", phone);
        //请求验证码
        Log.e("haifeng", "短信上传"+params);
        NetClient.post(Url.REGIST_MSGCODE_URL, params, new NetResponseHandler() {
            @Override
            public void onResponse(String json) {
                Log.e("haifeng", "短信返回"+json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String status = jsonObject.getString("status");
                    String errorCode = jsonObject.getString("errorCode");
                    if (status.equals("1")) {
                        mHandler.sendEmptyMessage(0);
                        DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.regist_text28));
//                        String code = jsonObject.getString("code");

                    } else {
                        send_Editcod.setClickable(true);
                        if (errorCode.equals("11001")){
                            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text29));
                        }
                        if (errorCode.equals("90001")){
                            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text24));
                        }
                        if (errorCode.equals("90002")){
                            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.send_error_text));
                        }

                    }
                } catch (JSONException e) {
                    Log.e("jxf","注册-发送短信，解析服务返回JSONObject时异常");
                    send_Editcod.setClickable(true);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                send_Editcod.setClickable(true);
                DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.toast_net));
            }
        });

    }
    /** 注册*/
    private void regist( RequestParams params) {
        NetClient.post(Url.REGIST_URL, params, new NetResponseHandler() {
            @Override
            public void onResponse(String json) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String status = jsonObject.getString("status");
                    String errorCode  = jsonObject.getString("errorCode");
                    if (status.equals("1")) {
                        String datas = jsonObject.getString("datas");
                        JSONObject jsonObject2 = new JSONObject(datas);
                        int userId = jsonObject2.getInt("userId");
                        SharedPreUtils.put(App.getContext(), "user_id", userId);
                        RequestParams loginParams = new RequestParams();
                        loginParams.put("userLoginName", registNumber.getText().toString());
                        loginParams.put("passwd", CommanUtil.md5Hex(rigist_password.getText().toString(), true));
                        //登录操作
                        login(loginParams);
                    }else {
                        /**		11002=手机号为空，
                         11003=验证码为空，
                         11004=密码为空，
                         11001=用户已存在，
                         22001=验证码错误，
                         90001=系统异常*/
                        regist_agreement.setClickable(true);
                        if (errorCode.equals("1101")){
                            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text7));
                        }
                        if (errorCode.equals("1102")){
                            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_toast1));
                        }
                        if (errorCode.equals("1103")){
                            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text12));
                        }
                        if (errorCode.equals("1104")){
                            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text11));
                        }
                        if (errorCode.equals("22001")){
                            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text7));
                        }
                        if (errorCode.equals("90001")){
                            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text24));
                        }
                    }
                } catch (JSONException e) {
                    Log.e("jxf","注册-点击确认，解析服务返回JSONObject时异常");
                    e.printStackTrace();
                    regist_agreement.setClickable(true);
                    DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text24));
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                regist_agreement.setClickable(true);
                DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.toast_net));
            }
        });
    }

    /**登录*/
    private void login( RequestParams params) {

        NetClient.post(Url.LOGIN_URL, params, new NetResponseHandler() {
            @Override
            public void onResponse(String json) {
                try {
                    JSONObject jsonObject3 = new JSONObject(json);
                    String status = jsonObject3.getString("status");
                    String errorCode  = jsonObject3.getString("errorCode");
                    if (status.equals("1")) {
                        String datas = jsonObject3.getString("datas");
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(datas);

                            int userId=jsonObject.getInt("userId");
                            String country=jsonObject.getString("country");
                            String province=jsonObject.getString("province");
                            String city=jsonObject.getString("city");
                            String userName = jsonObject.getString("userName");
                            String portraitUrlSmall = jsonObject.getString("portraitUrlSmall");
                            String portraitUrlBig = jsonObject.getString("portraitUrlBig");
                            String nickname = jsonObject.getString("nickname");
                            String mobile = jsonObject.getString("mobile");
                            String isUpdate = jsonObject.getString("isUpdate");

                            SharedPreUtils.put(App.getContext(), "login_time",System.currentTimeMillis());
                            SharedPreUtils.put(App.getContext(), "user_id", userId);
                            SharedPreUtils.put(App.getContext(), "user_name",userName);
                            SharedPreUtils.put(App.getContext(), "user_nick_name",nickname);
                            SharedPreUtils.put(App.getContext(), "user_mobile", mobile);
                            SharedPreUtils.put(App.getContext(), "user_big_avatar",portraitUrlBig);
                            SharedPreUtils.put(App.getContext(), "user_small_avatar",portraitUrlSmall);
                            SharedPreUtils.put(App.getContext(), "isUpdate", isUpdate);
                            SharedPreUtils.put(App.getContext(), "city", city);
                            SharedPreUtils.put(App.getContext(), "province", province);
                            SharedPreUtils.put(App.getContext(), "country",country);

                        } catch (JSONException e) {
                            Log.e("jxf","注册-点击确认-登陆，解析服务返回JSONObject时异常");
                            e.printStackTrace();
                            regist_agreement.setClickable(true);
                        }

                        Intent intent = new Intent(RegistActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        /**11002=手机号为空，
                         11004=密码为空，
                         11005=查无此用户，
                         22001=密码错误，
                         90001=系统异常*/
                        regist_agreement.setClickable(true);

                        if (errorCode.equals("11002")) {
                            DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.regist_toast1));
                        }
                        if (errorCode.equals("11004")) {
                            DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.regist_text11));
                        }
                        if (errorCode.equals("11005")) {
                            DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.regist_text25));
                        }
                        if (errorCode.equals("22001")) {
                            DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.regist_text14));
                        }
                        if (errorCode.equals("90001")) {
                            DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.regist_text24));
                        }
                    }
                } catch (JSONException e) {
                    Log.e("jxf","注册-点击确认-登陆，解析服务返回JSONObject时异常");
                    DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.regist_text24));
                    regist_agreement.setClickable(true);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.toast_net));
                regist_agreement.setClickable(true);
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
