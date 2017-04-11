package com.miracleworld.lingxingdao.android.bean;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by donghaifeng on 2015/10/22.
 */
public class User implements Serializable {

    public int userId;                  // 用户Id
    public String userName;         // 用户姓名
    public String portraitUrlSmall; // 用户头像小图,
    public String portraitUrlBig;   // 用户头像大图,
    public String nickname ;        // 名字,
    public String country ;            // 国家,
    public String province ;           // 省份,
    public String city;                // 城市,
    public String mobile;           // 手机号
    public String isUpdate;         // 用户名只修改一次标识


    public User() {
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
