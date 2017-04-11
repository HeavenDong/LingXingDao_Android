package com.miracleworld.lingxingdao.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.miracleworld.lingxingdao.android.App;
import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.activity.ChannelActivity;
import com.miracleworld.lingxingdao.android.activity.pay.SubscribeForPayActivity;
import com.miracleworld.lingxingdao.android.bean.Channel;
import com.miracleworld.lingxingdao.android.http.NetClient;
import com.miracleworld.lingxingdao.android.http.NetResponseHandler;
import com.miracleworld.lingxingdao.android.http.Url;
import com.miracleworld.lingxingdao.android.options.ImageLoaderOptions;
import com.miracleworld.lingxingdao.android.utils.SharedPreUtils;
import com.miracleworld.lingxingdao.android.view.DefinedSingleToast;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by donghaifeng on 2016/01/24.
 */
public class ChannelListAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<Channel> channels;
//    private int teacherId;
//    private String portraitUrlSmall;
//    private String nickname;

    //public ChannelListAdapter(ChannelActivity context,ArrayList<Channel> channels,int teacherId,String portraitUrlSmall,String nickname) {
    public ChannelListAdapter(Context context,ArrayList<Channel> channels) {
        this.context=context;
        this.channels=channels;
//        this.teacherId=teacherId;
//        this.portraitUrlSmall=portraitUrlSmall;
//        this.nickname=nickname;
    }

    @Override
    public int getCount() {
        return channels.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    class ViewHolder{
        ImageView channel_lv_item_iv;
        TextView channel_lv_item_title;
        TextView channel_lv_item_category;
        TextView channel_lv_item_price;
        TextView channel_lv_item_butt;
        TextView channel_lv_item_des;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView==null){
            holder=  new ViewHolder();
            convertView= View.inflate(context, R.layout.channel_lv_item, null);
            holder.channel_lv_item_iv= (ImageView) convertView.findViewById(R.id.channel_lv_item_iv);
            holder.channel_lv_item_title= (TextView) convertView.findViewById(R.id.channel_lv_item_title);
            holder.channel_lv_item_category= (TextView) convertView.findViewById(R.id.channel_lv_item_category);
            holder.channel_lv_item_price= (TextView) convertView.findViewById(R.id.channel_lv_item_price);
            holder.channel_lv_item_butt= (TextView) convertView.findViewById(R.id.channel_lv_item_butt);
            holder.channel_lv_item_des=(TextView) convertView.findViewById(R.id.channel_lv_item_des);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder)convertView.getTag();
        }
        //赋值
        ImageLoader.getInstance().displayImage(channels.get(position).iconUrl, holder.channel_lv_item_iv, ImageLoaderOptions.playRoundOption);
        holder.channel_lv_item_title.setText(channels.get(position).channelName);
        holder.channel_lv_item_category.setText(channels.get(position).categoryName);
        holder.channel_lv_item_price.setText(""+(int)(channels.get(position).channelPrice));
        holder.channel_lv_item_des.setText(channels.get(position).des);
//        holder.channel_lv_item_butt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.e("jxf", "channel的adapter点击的位置：" + position);
//                loadTime(position);
//            }
//        });

        return convertView;
    }

//    private void loadTime(int position) {
//        //判断网络的操作
//        ConnectivityManager mConnectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
//        if (info == null || !mConnectivity.getBackgroundDataSetting()) {
//            Log.e("jxf", "channel页adapter跳转请求时间:没网");
//            //没网不跳转：不做任何的操作：直接提示网络不好：请重新点击
//            DefinedSingleToast.showToast(context,"网络不给力：请重新点击");
//        }
//        else{
//            Log.e("jxf","channel页条目跳转请求时间:有网");
//            netRequestAndResponse(position);
//        }
//    }

//    private void netRequestAndResponse(final int position) {
//        RequestParams params = new RequestParams();
//        params.put("channelId", channels.get(position).id);
//        params.put("userId", (int) SharedPreUtils.get(App.getContext(), "user_id", 0));
//        Log.e("jxf","adapter中请求时间携带参数"+params);
//        NetClient.headGet(context, Url.PAY_STARTANDEND_TIME, params, new NetResponseHandler() {
//            @Override
//            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
//                //请求失败不做任何的操作
//                Log.e("jxf","channel页adapter请求onfail：不做任何跳转：提示用户");
//                DefinedSingleToast.showToast(context,"网络不给力：请重新点击");
//            }
//
//            @Override
//            public void onResponse(String json) {
//                try {
//                    JSONObject jSONObjectAll = new JSONObject(json);
//                    Log.e("jxf", "channel页adapter跳转支付页请求时间得到响应的json" + json);
//                    String status=jSONObjectAll.optString("status");
//                    //请求成功有需要的数据:同时在此位置传递数据使用
//                    if (status.equals("1")){
//                        JSONObject obj=jSONObjectAll.optJSONObject("datas");
//                        String startTime=obj.getString("startTime");
//                        String endTime=obj.getString("endTime");
//                        Intent intent = new Intent(context, SubscribeForPayActivity.class);
//                        Bundle bundle = new Bundle();
//                        bundle.putString("lecturer_head", portraitUrlSmall);
//                        bundle.putString("lecturer_name", nickname);
//                        bundle.putInt("teacherId", teacherId);
//                        bundle.putDouble("Price", channels.get(position).channelPrice);
//                        bundle.putString("channel_name", channels.get(position).channelName);
//                        bundle.putInt("channel_Id", channels.get(position).id);
//                        bundle.putString("startTime", startTime);
//                        bundle.putString("endTime", endTime);
//                        bundle.putInt("source", 0);
//                        Log.e("jxf", "channel页adapter跳转支付页携带的信息" + "bundle::" + bundle);
//                        intent.putExtras(bundle);
//                        context.startActivityForResult(intent, 500);
//                        context.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//                    }else{
//                        //错误返回：提示用户
//                        DefinedSingleToast.showToast(context, "网络不给力：请重新点击");
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Log.e("jxf", "channel页adapter跳转支付页请求时间:onresponse出现异常" + e.toString());
//                    DefinedSingleToast.showToast(context, "网络不给力：请重新点击");
//                }
//            }
//        });
//        }


    }