package com.miracleworld.lingxingdao.android.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.Selection;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.miracleworld.lingxingdao.android.App;
import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.base.BaseActivity;
import com.miracleworld.lingxingdao.android.http.NetClient;
import com.miracleworld.lingxingdao.android.http.NetResponseHandler;
import com.miracleworld.lingxingdao.android.http.Url;
import com.miracleworld.lingxingdao.android.utils.CommanUtil;
import com.miracleworld.lingxingdao.android.view.DefinedSingleToast;
import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * 找回密码页
 */
public class ForgetActivity extends BaseActivity {
    private EditText forget_number,forget_send_editcode,forget_password;
    private TextView send_Editcod,forget_agreement;
    private String number,psw,mscode;
    private CheckBox forget_seepw;
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
                        send_Editcod.setText(getResources().getString(R.string.regist_text0)+i +"s)");
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
        setContentView(R.layout.forget_activity);

    }
    @Override
    protected void initView() {

        forget_number=(EditText)findViewById(R.id.forget_numbers);
        forget_send_editcode=(EditText)findViewById(R.id.forget_send_editcode);
        forget_password=(EditText)findViewById(R.id.forget_password);

        send_Editcod = (TextView)findViewById(R.id.forget_send_editcode_tv);
        forget_seepw=(CheckBox)findViewById(R.id.forget_seepw);
        forget_agreement = (TextView)findViewById(R.id.forget_agreement);
        forget_agreement.setOnClickListener(this);
        send_Editcod.setOnClickListener(this);
        forget_seepw.setOnClickListener(this);
        findViewById(R.id.forget_title_left).setOnClickListener(this);


        forget_number.setText(getIntent().getExtras().getString("forgetMobile"));

    }

    @Override
    protected void onClickEvent(View view) {
        switch (view.getId()) {
            case R.id.forget_title_left:
                finish();
                break;
            /**密码可见*/
            case R.id.forget_seepw:
                if (forget_seepw.isChecked()) {
                    forget_password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    Editable etable = forget_password.getText();
                    Selection.setSelection(etable, etable.length());
                }else {
                    forget_password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    Editable etable = forget_password.getText();
                    Selection.setSelection(etable, etable.length());
                }
                break;
            case R.id.forget_send_editcode_tv:
                if(CommanUtil.isNetworkAvailable()) {
                    number=  forget_number.getText().toString();
                    if (TextUtils.isEmpty(number)) {
                        DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_toast1));
                        return;
                    }
                    if (!CommanUtil.isMobilePhone(number)) {
                        DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text2));
                        return;
                    }
//                DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.forget_toast3));
                    send_Editcod.setClickable(false);
                    getEditCodeAndNext(number);
                }else {
                    DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.toast_net));
                }
                break;

            case R.id.forget_agreement:
                if(CommanUtil.isNetworkAvailable()) {
                    number=  forget_number.getText().toString();
                    psw = forget_password.getText().toString();
                    mscode=forget_send_editcode.getText().toString();
                    String reSettingpwd = CommanUtil.md5Hex(psw, true) + "";//
                    if (TextUtils.isEmpty(number)) {
                        DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_toast1));
                        return;
                    } else if (!CommanUtil.isMobilePhone(number)) {
                        DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text2));
                        return;
                    } else if (TextUtils.isEmpty(psw)) {
                        DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text11));
                        return;
                    }else if (!CommanUtil.isPSW(psw)) {
                        DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text4));
                        return ;
                    } else if (TextUtils.isEmpty(mscode)) {
                        DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text12));
                        return;
                    } else {

                        reSettingPwdFinish(number, reSettingpwd, mscode);
                        forget_agreement.setClickable(false);
                    }

                }else {
                    DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.toast_net));
                }
                break;
        }

    }
    private void reSettingPwdFinish(String reSettingPhone, String reSettingpwd, String reSettingCode) {
        RequestParams params = new RequestParams();
        params.put("mobile", reSettingPhone);
        params.put("code", reSettingCode);
        params.put("password", reSettingpwd);
        NetClient.post(Url.FORGET_URL, params, new NetResponseHandler() {
            @Override
            public void onResponse(String json) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String status = jsonObject.getString("status");
                    String errorCode = jsonObject.getString("errorCode");
                    if (status.equals("1")) {

                        DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.forget_toast10));
                        Intent intent = new Intent();
                        Bundle bundle = new Bundle();
                        bundle.putString("mobile", forget_number.getText().toString());
                        bundle.putString("psw", forget_password.getText().toString());
                        intent.putExtras(bundle);
                        setResult(99, intent);
                        finish();


                    }else {
                        forget_agreement.setClickable(true);
                        if (errorCode.equals("22001")){
                            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.forget_text9));
                        }
                        if (errorCode.equals("22002")){
                            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.forget_text8));
                        }
                        if (errorCode.equals("90001")){
                            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text24));
                        }
                    }

                } catch (JSONException e) {
                    Log.e("jxf","找回密码-点击确认，解析服务返回JSONObject时异常");
                    forget_agreement.setClickable(true);
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                forget_agreement.setClickable(true);
                DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.toast_net));
            }
        });

    }

    /**
     * 短信验证
     */
    private void getEditCodeAndNext(String phone) {

        if (TextUtils.isEmpty(phone)) {
            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_toast1));
            return;
        }
        if (!CommanUtil.isMobilePhone(phone)) {
            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text2));
            return;
        }

        codeTag = false;
        RequestParams params = new RequestParams();
        params.put("mobile", phone);
        Log.e("haifeng", "找回密码短信上传" + params);
        //请求验证码
        NetClient.post(Url.FORGET_MSGCODE_URL, params, new NetResponseHandler() {

            @Override
            public void onResponse(String json) {
                Log.e("haifeng", "找回密码短信返回" + json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String status = jsonObject.getString("status");
                    String errorCode = jsonObject.getString("errorCode");
                    if (status .equals("1")) {
                        mHandler.sendEmptyMessage(0);
                        DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.regist_text28));
//                        String code = jsonObject.getString("code");

                    } else {
                        send_Editcod.setClickable(true);
                        if (errorCode.equals("11005")){
                            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text25));
                        }
                        if (errorCode.equals("90001")){
                            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text24));
                        }
                        if (errorCode.equals("90002")){
                            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.send_error_text));
                        }

                    }
                } catch (JSONException e) {
                    Log.e("jxf","找回密码-发送短信，解析服务返回JSONObject时异常");
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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }

}
