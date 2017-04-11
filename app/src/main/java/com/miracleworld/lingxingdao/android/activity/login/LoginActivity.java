package com.miracleworld.lingxingdao.android.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
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
 *     登陆页
 */
public class LoginActivity extends BaseActivity {
    private EditText window_login_number,window_login_password;
    private TextView window_login_butt;
    @Override
    public void setContentLayout() {
        setContentView(R.layout.login_activity);
    }
    @Override
    protected void initView() {
        window_login_number= (EditText) findViewById(R.id.window_login_number);
        window_login_password = (EditText)findViewById(R.id.window_login_password);


        window_login_butt = (TextView) findViewById(R.id.window_login_butt);
        findViewById(R.id.window_regist_butt).setOnClickListener(this);
        findViewById(R.id.window_forget_pwd).setOnClickListener(this);
        window_login_butt.setOnClickListener(this);

    }
    @Override
    protected void onClickEvent(View view) {
        switch (view.getId()) {
            /**点击登陆*/
            case R.id.window_login_butt:
                if(CommanUtil.isNetworkAvailable()) {
                    if (TextUtils.isEmpty(window_login_number.getText().toString())) {
                        DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.login_toast1));
                        return;
                    } else if (!CommanUtil.isMobilePhone(window_login_number.getText().toString())) {
                        DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.login_toast2));
                        return;
                    } else if (TextUtils.isEmpty(window_login_password.getText().toString())) {
                        DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.login_toast3));
                        return;
                    } else if (!CommanUtil.isPSW(window_login_password.getText().toString())) {
                        DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text4));
                        return;
                    } else {
                        window_login_butt.setClickable(false);
                        RequestParams params = new RequestParams();
                        params.put("userLoginName", window_login_number.getText().toString());
                        params.put("passwd", CommanUtil.md5Hex(window_login_password.getText().toString(), true));
                        login(params);
                    }
                }else {
                    DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.toast_net));
                }
                break;
            /**点击注册*/
            case R.id.window_regist_butt:
                Intent intent3 = new Intent(this, RegistActivity.class);
                startActivity(intent3);
                break;
            /**点击忘记密码*/
            case R.id.window_forget_pwd:
                Intent intent4 = new Intent(this, ForgetActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("forgetMobile",window_login_number.getText().toString());
                intent4.putExtras(bundle);
                startActivityForResult(intent4, 100);
                break;

        }
    }


    //请求网络登录
    private void login(RequestParams params) {
        Log.e("haifeng", "登陆上传"+params);
        NetClient.post(Url.LOGIN_URL, params, new NetResponseHandler() {
            @Override
            public void onResponse(String json) {
                Log.e("haifeng", "登陆返回" + json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String status = jsonObject.getString("status");
                    String errorCode = jsonObject.getString("errorCode");
                    if (status != null && status.equals("1")) {
                        String datas = jsonObject.getString("datas");

                        JSONObject jsonObject2 = new JSONObject(datas);
                        int userId = jsonObject2.getInt("userId");
                        String country = jsonObject2.getString("country");
                        String province = jsonObject2.getString("province");
                        String city = jsonObject2.getString("city");
                        String userName = jsonObject2.getString("userName");
                        String portraitUrlSmall = jsonObject2.getString("portraitUrlSmall");
                        String portraitUrlBig = jsonObject2.getString("portraitUrlBig");
                        String nickname = jsonObject2.getString("nickname");
                        String mobile = jsonObject2.getString("mobile");
                        String isUpdate = jsonObject2.getString("isUpdate");

                        /**存本地*/
                        SharedPreUtils.put(App.getContext(), "login_time", System.currentTimeMillis());
                        SharedPreUtils.put(App.getContext(), "user_id", userId);
                        SharedPreUtils.put(App.getContext(), "user_name", userName);
                        SharedPreUtils.put(App.getContext(), "user_nick_name",nickname);
                        SharedPreUtils.put(App.getContext(), "user_mobile", mobile);
                        SharedPreUtils.put(App.getContext(), "user_big_avatar",portraitUrlBig);
                        SharedPreUtils.put(App.getContext(), "user_small_avatar",portraitUrlSmall);
                        SharedPreUtils.put(App.getContext(), "isUpdate",isUpdate);
                        SharedPreUtils.put(App.getContext(), "city",city);
                        SharedPreUtils.put(App.getContext(), "province",province);
                        SharedPreUtils.put(App.getContext(), "country",country);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    } else {
                        /**
                         0 = 成功
                         11008 = 登录名为空
                         11004 = 密码为空
                         11005 = 查无此用户
                         11006 = 密码错误
                         90001 = 系统异常

                         */
                        window_login_butt.setClickable(true);
                        if (errorCode.equals("11008")) {
                            DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.regist_toast1));
                        }
                        if (errorCode.equals("11004")) {
                            DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.regist_text11));
                        }
                        if (errorCode.equals("11005")) {
                            DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.regist_text25));
                        }
                        if (errorCode.equals("11006")) {
                            DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.regist_text14));
                        }
                        if (errorCode.equals("90001")) {
                            DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.regist_text24));
                        }
                    }

                } catch (JSONException e) {
                    Log.e("jxf","登录-点击确认，解析服务返回JSONObject时异常");
                    window_login_butt.setClickable(true);
                    DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.regist_text24));
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                window_login_butt.setClickable(true);
                DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.toast_net));
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null&&requestCode==100&&resultCode==99){
            window_login_number.setText(data.getExtras().getString("mobile"));
//            window_login_password.setText(data.getExtras().getString("psw"));
            window_login_password.setText("");
        }
    }

}
