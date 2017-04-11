package com.miracleworld.lingxingdao.android.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by donghaifeng on 2015/12/16
 * 用来保存全屏
 */
public enum PreferenceUtils {
	instance;

	private static final String PREFERENCE_CONFIG = "com.miracleworld.lingxing.android.preference_config";

	/** 是否全屏 */
	private static final String PREFERENCE_CONFIG_ISFULLSCREEN = "com.miracleworld.lingxing.android.preference_config_isfullscreen";

	/** 获取SharedPreferences对象 */
	private SharedPreferences getPreference(Context context, String config) {
		if (context == null) {
			return null;
		}

		return context.getSharedPreferences(config, Context.MODE_PRIVATE);
	}

	/** 获取是否全屏 */
	public synchronized boolean getIsFullScreen(Context context) {
		SharedPreferences preference = getPreference(context, PREFERENCE_CONFIG);
		if (preference == null) {
			return false;
		}
		return preference.getBoolean(PREFERENCE_CONFIG_ISFULLSCREEN, false);
	}

}
