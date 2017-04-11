package com.miracleworld.lingxingdao.android.view;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.miracleworld.lingxingdao.android.R;

/**
 * Created by donghaifeng on 2015/12/16
 */
public class TempSingleToast {
    private static Toast toast;
    public static void showToast(Context context,String talking) {
        if (toast == null) {
            Log.e("jxf","toast为空");
            toast = new Toast(context);
        }
        View view = View.inflate(context, R.layout.temp, null);
        TextView text = (TextView) view.findViewById(R.id.talks);
        text.setText(talking);
        //按照原来toast的位置
        // toast.setGravity(Gravity.CENTER_HORIZONTAL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(view);
        toast.show();

    }
    public  static void cancleToast(){
        Log.e("jxf","toast为空：取消放弃");
        if (toast!=null){
            Log.e("jxf","toast不为空：取消");
            toast.cancel();
        }
    }


}
