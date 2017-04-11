package com.miracleworld.lingxingdao.android.activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.RequestParams;
import com.miracleworld.lingxingdao.android.App;
import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.activity.pay.SubscribeForPayActivity;
import com.miracleworld.lingxingdao.android.adapter.ChannelHorizontrallvAdapter;
import com.miracleworld.lingxingdao.android.adapter.ChannelListAdapter;
import com.miracleworld.lingxingdao.android.base.BaseActivity;
import com.miracleworld.lingxingdao.android.bean.Channel;
import com.miracleworld.lingxingdao.android.bean.ChannelCategoryHorizontalBean;
import com.miracleworld.lingxingdao.android.http.NetClient;
import com.miracleworld.lingxingdao.android.http.NetResponseHandler;
import com.miracleworld.lingxingdao.android.http.Url;
import com.miracleworld.lingxingdao.android.utils.SharedPreUtils;
import com.miracleworld.lingxingdao.android.view.DefinedSingleToast;
import com.miracleworld.lingxingdao.android.view.HorizontalListView;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by donghaifeng on 2015/12/16
 */
public class ChannelActivity extends BaseActivity {
    private HorizontalListView channel_horizontal_listview;
    private PullToRefreshListView channel_ptr_lv;
    private ListView channel_lv;
    private View footer;
    private ImageView activity_channel_none;
    private RelativeLayout activity_channel_net_message;
    private ImageView activity_channel_loading_iv;
    private RotateAnimation rotateAnimation;
    private RelativeLayout channel_listview_group;
    //总集合
    private ArrayList<Channel> channels;
    private ArrayList<ChannelCategoryHorizontalBean> channelCategoryHorizontals;
    //适配器
    private ChannelHorizontrallvAdapter channelHorizontrallvAdapter;
    private ChannelListAdapter adapter;
    //上页传递的书库
    private int teacherId;
    private String catgoryNames;
    private String catgoryIds;
    private String portraitUrlSmall;
    private String nickname;
    //请求网络的数据
    private int categoryId;
    private int maxId;
    private int minId;
    private int pageSize=20;
    //要进行判断的值
    private boolean isFirst;
    private boolean isfromStart;
    private boolean isfromEnd;
    //请求网络的空数据
    private boolean isNull;
    private boolean isRefresh;
    //修改3：添加禁止上拉加载的boolean
    private boolean isLoadMore;
    //数据库需要
    private boolean cursorIsNull;
    private String tableName="channel";
    @Override
    protected void initView() {
        getBundle();
        isFirst=true;
        isfromStart=false;

        isRefresh=false;
        isNull=false;
        categoryId=0;
        maxId=0;
        minId=0;
        //给个初始化 防止走loaddata中他的方法
        isfromEnd=false;
        //一进来允许上拉加载
        isLoadMore=true;

        channels=new ArrayList<Channel>();
        channelCategoryHorizontals=new ArrayList<ChannelCategoryHorizontalBean>();
        RelativeLayout channel_left= (RelativeLayout) findViewById(R.id.channel_left);
        channel_left.setOnClickListener(this);
        channel_horizontal_listview = (HorizontalListView) findViewById(R.id.channel_horizontal_listview);
        activity_channel_none= (ImageView)findViewById(R.id.activity_channel_none);
        activity_channel_none.setVisibility(View.GONE);
        activity_channel_net_message= (RelativeLayout)findViewById(R.id.activity_channel_net_message);
        activity_channel_net_message.setVisibility(View.GONE);
        activity_channel_loading_iv= (ImageView)findViewById(R.id.activity_channel_loading_iv);
        rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.rotate_refresh_drawable_default);
        footer=View.inflate(this, R.layout.list_footer,null);
        channel_listview_group= (RelativeLayout) findViewById(R.id.channel_listview_group);
        channel_ptr_lv = (PullToRefreshListView) findViewById(R.id.channel_ptr_lv);
        //修改1：初始化：禁止上拉
        channel_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        //滑动才会触发  而且是上拉的时候才会触发  下拉的时候触发了onrefreshlistener
        channel_ptr_lv.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                Log.e("jxf","触发setOnLastItemVisibleListener打印 isLoadMore"+isLoadMore);
                if (isLoadMore){
                    Log.e("jxf","触发上拉加载");
                    Log.e("jxf", "设置上拉记载更多");
                    channel_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                    Log.e("jxf","让现实数显");
                    channel_ptr_lv.setRefreshing();
                    //这个是为了标记的boolean  改执行loaddata中那个方法的boolean
                    isfromEnd=true;
                    ArrayList<Channel> temp=new ArrayList<Channel>();
                    temp.addAll(channels);
                    Collections.sort(temp, unordercomp);
                    minId=temp.get(temp.size()-1).id;
                    Log.e("jxf","content页上拉加载的minId"+minId);
                    maxId=0;
                    loadData();
                }
            }
        });
        channel_ptr_lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                Log.e("jxf", "channel页下拉刷新");
                isfromStart = true;
                if (isNull) {
                    maxId = 0;
                } else {
                    ArrayList<Channel> temp=new ArrayList<Channel>();
                    temp.addAll(channels);
                    Collections.sort(temp, unordercomp);
                    maxId=temp.get(0).id;
                }
                Log.e("jxf", "channel页下拉刷新的maxId" + maxId);
                minId = 0;
                loadData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                Log.e("jxf", "channel页上拉加载更多");
//                isfromEnd = true;
//                minId = channels.get(channels.size() - 1).id;
//                Log.e("jxf", "content页上拉加载的minId" + minId);
//                maxId = 0;
//                loadData();
            }
        });
        channel_lv= channel_ptr_lv.getRefreshableView();
        channel_lv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        channel_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position>channels.size()){
                    return;
                }else{
                    //实际位置应该是position-1:因为有头
                    Log.e("jxf", "channel的条目点击的位置：" + position);
                    //loadTime(position - 1);
                    Intent intent=new Intent(ChannelActivity.this,ChannelDetailActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putInt("teacherId",teacherId);
                    bundle.putString("portraitUrlSmall", portraitUrlSmall);
                    bundle.putString("nickname", nickname);
                    bundle.putInt("channelId", channels.get(position - 1).id);
                    bundle.putDouble("channelPrice",channels.get(position-1).channelPrice);
                    bundle.putString("des",channels.get(position-1).des);
                    //支付页使用的channename
                    bundle.putString("channelName",channels.get(position-1).channelName);
                    bundle.putString("startSource","1");
                    intent.putExtras(bundle);
                    ChannelActivity.this.startActivityForResult(intent, 500);
                    ChannelActivity.this.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            }
        });
        getHorizontalList();
        channelHorizontrallvAdapter=new ChannelHorizontrallvAdapter(this,channelCategoryHorizontals);
        channel_horizontal_listview.setAdapter(channelHorizontrallvAdapter);
        channel_horizontal_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < channelCategoryHorizontals.size(); i++) {
                    if (i == position) {
                        channelCategoryHorizontals.get(i).isCheck = true;
                    } else {
                        channelCategoryHorizontals.get(i).isCheck = false;
                    }
                }
                channelHorizontrallvAdapter.notifyDataSetChanged();
                categoryId=channelCategoryHorizontals.get(position).detailCategoryid;
                maxId=0;
                minId=0;
                isFirst=false;
                isRefresh=true;
                isNull=false;
                //以下回复初始状态：refresh的时候 没有数据库了 只有 isnull
                //以下开关重置
                isfromEnd=false;
                //一进来允许上拉加载
                isLoadMore=true;
                channel_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

                activity_channel_net_message.setVisibility(View.GONE);
                activity_channel_none.setVisibility(View.GONE);
                loadData();
            }
        });
        Cursor cursor= App.getSqlManager().seleteAllWhere(tableName, new String[]{"id1","id", "channelname", "price", "categoryname", "iconurl","des"}, "teacher_id=?",new String[]{""+teacherId},"id1 desc");
        if (cursor!=null&&cursor.getCount()>0){
            cursorIsNull=false;
            Log.e("jxf", "channel数据库有数据");
            while(cursor.moveToNext()) {
                Channel channel=new Channel();
                channel.id1=cursor.getInt(0);
                channel.id=cursor.getInt(1);
                channel.channelName=cursor.getString(2);
                channel.channelPrice=cursor.getDouble(3);
                channel.categoryName=cursor.getString(4);
                channel.iconUrl=cursor.getString(5);
                channel.des=cursor.getString(6);
                channels.add(channel);
            }
            cursor.close();
            if (channels.size()>20){
                int id1=channels.get(19).id1;
                App.getSqlManager().deleteOne(tableName, "id1<? and teacher_id=?", new String[]{""+id1,""+teacherId});
                for (int i=20;i<channels.size();i=20){
                    channels.remove(i);
                }
                Log.e("jxf","channel页从数据库取数据：有数据：集合数量"+channels.size());
            }
        }
        else{
            cursorIsNull=true;
            Log.e("jxf", "channel数据库没有数据");
            cursor.close();
        }
        App.getSqlManager().close();
        adapter=new ChannelListAdapter(this,channels);
        channel_lv.setAdapter(adapter);
        loadData();
    }

//    private void loadTime(int position) {
//        //判断网络的操作
//        ConnectivityManager mConnectivity = (ConnectivityManager)ChannelActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
//        if (info == null || !mConnectivity.getBackgroundDataSetting()) {
//            Log.e("jxf", "channel页adapter跳转请求时间:没网");
//            //没网不跳转：不做任何的操作：直接提示网络不好：请重新点击
//            DefinedSingleToast.showToast(ChannelActivity.this,"网络不给力：请重新点击");
//        }
//        else{
//            Log.e("jxf","channel页条目跳转请求时间:有网");
//            netRequestAndResponseTime(position);
//        }
//    }
//
//    private void netRequestAndResponseTime(final int position) {
//        RequestParams params = new RequestParams();
//        params.put("channelId", channels.get(position).id);
//        params.put("userId", (int) SharedPreUtils.get(App.getContext(), "user_id", 0));
//        Log.e("jxf","adapter中请求时间携带参数"+params);
//        NetClient.headGet(ChannelActivity.this, Url.PAY_STARTANDEND_TIME, params, new NetResponseHandler() {
//            @Override
//            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
//                //请求失败不做任何的操作
//                Log.e("jxf","channel页adapter请求onfail：不做任何跳转：提示用户");
//                DefinedSingleToast.showToast(ChannelActivity.this,"网络不给力：请重新点击");
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
//                        Intent intent = new Intent(ChannelActivity.this, SubscribeForPayActivity.class);
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
//                        ChannelActivity.this.startActivityForResult(intent, 500);
//                        ChannelActivity.this.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
//                    }else{
//                        //错误返回：提示用户
//                        DefinedSingleToast.showToast(ChannelActivity.this, "网络不给力：请重新点击");
//                    }
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                    Log.e("jxf", "channel页adapter跳转支付页请求时间:onresponse出现异常" + e.toString());
//                    DefinedSingleToast.showToast(ChannelActivity.this, "网络不给力：请重新点击");
//                }
//            }
//        });
//    }

    private void loadData() {
        //为了第一次数据加载的时候同时下拉
        //上拉加载开始执行loaddata方法的时候：根据标识方法 关闭开关
        if (isfromEnd){
            //上拉在进行了 就不会继续再进行了
            Log.e("jxf","上拉请求网络加载数据，并且关闭开关isLoadMore");
            isLoadMore=false;
            //尝试放开 mode 看疗效吧  不行的 这这更改了模式 执行完的 complete 可能连着一起都会关闭的

        }
        if (isFirst){
            Log.e("jxf", "channel页loadata:isfirst");
            activity_channel_loading_iv.setAnimation(rotateAnimation);
            activity_channel_loading_iv.setVisibility(View.VISIBLE);
        }
        if (isRefresh){
            Log.e("jxf","channel页loadata:isRefresh");
            activity_channel_loading_iv.setAnimation(rotateAnimation);
            activity_channel_loading_iv.setVisibility(View.VISIBLE);
        }
        ConnectivityManager mConnectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info == null || !mConnectivity.getBackgroundDataSetting()) {
            Log.e("jxf","channel页loadata:没网");
            new AsyncTask<Void,Void,Void>(){
                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.e("jxf", "AsyncTask睡着了，发生了异常" + e.toString());
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    netOff();
                }
            }.execute();
        }
        else{
            Log.e("jxf","channel页loadata:有网");
            netRequestAndResponse();
        }
    }

    private void netRequestAndResponse() {
        RequestParams params = new RequestParams();
        params.put("maxId", maxId);
        params.put("minId", minId);
        params.put("pageSize", pageSize);
        params.put("categoryId", categoryId);
        params.put("teacherId",teacherId);
        Log.e("jxf","频道列表请求参数"+params);
        NetClient.headGet(this, Url.CHANNEL_LIST, params, new NetResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.e("jxf","channel页loadata:onfail错误码："+throwable.toString());
                netOff();
            }

            @Override
            public void onResponse(String json) {
                try {
                    JSONObject jSONObjectAll = new JSONObject(json);
                    Log.e("jxf", "channel页请求得到响应的json" + json);
                    String status = jSONObjectAll.optString("status");
                    if (status.equals("1")){
                        JSONArray jSONArray = jSONObjectAll.optJSONArray("datas");
                        if (jSONArray!=null){
                            activity_channel_none.setVisibility(View.GONE);
                            ArrayList<Channel> temp=new ArrayList<Channel>();
                            int length = jSONArray.length();
                            for (int i = 0; i < length; i++){
                                JSONObject obj = jSONArray.optJSONObject(i);
                                Log.e("jxf", "channel的一条数据：" + obj.toString());
                                Channel channel=new Channel();
                                channel.id=obj.optInt("id");
                                channel.channelName=obj.optString("channelName");
                                channel.channelPrice=obj.optDouble("channelPrice");
                                channel.categoryName=obj.optString("categoryName");
                                channel.iconUrl=obj.getString("iconUrl");
                                channel.des=obj.getString("des");
                                temp.add(channel);
                            }
                            if (isRefresh){
                                channels.clear();
                                channels.addAll(temp);
                                activity_channel_loading_iv.clearAnimation();
                                activity_channel_loading_iv.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
//                                channel_ptr_lv.setMode(PullToRefreshBase.Mode.BOTH);
                                //如果有脚 就移除脚布局
                                channel_lv.removeFooterView(footer);
                                if (temp.size() < pageSize) {
                                    Log.e("jxf", "content页loadata已经是最后一条数据");
//                                    channel_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                    channel_lv.addFooterView(footer);
                                    isLoadMore = false;
                                }
                                isRefresh=false;
                            }
                            if (isfromStart) {
                                if (isNull){
                                    isLoadMore = true;
                                    channels.addAll(temp);
                                    Collections.reverse(temp);
                                    pullSaveMore(temp);
                                    adapter.notifyDataSetChanged();
                                    channel_ptr_lv.onRefreshComplete();
                                    if (temp.size() < pageSize) {
                                        Log.e("jxf", "content页loadata已经是最后一条数据");
//                                        channel_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                        channel_lv.addFooterView(footer);
                                        isLoadMore = false;
                                    }
                                    isfromStart = false;
                                    isNull=false;
//                                    channel_ptr_lv.setMode(PullToRefreshBase.Mode.BOTH);

                                }else{
                                    channels.addAll(0,temp);
                                    Collections.reverse(temp);
                                    pullSaveMore(temp);
                                    adapter.notifyDataSetChanged();
                                    channel_ptr_lv.onRefreshComplete();
                                    isfromStart = false;
                                }
                            }
                            if (isfromEnd){
                                Log.e("jxf", "完成刷新：关闭刷新");
                                channel_ptr_lv.onRefreshComplete();
                                isLoadMore = true;
                                channels.addAll(temp);
//                                channel_ptr_lv.onRefreshComplete();
                                if (temp.size() < pageSize) {
                                    Log.e("jxf", "content页loadata已经是最后一条数据");
//                                    channel_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                    channel_lv.addFooterView(footer);
                                    isLoadMore = false;
                                }
                                adapter.notifyDataSetChanged();
                                isfromEnd = false;
                                Log.e("jxf", "完成刷新：更改设置改为下拉mode");
                                channel_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            }
                            if (isFirst) {
                                if (!cursorIsNull){
                                    channels.clear();
                                }
                                channels.addAll(temp);
                                adapter.notifyDataSetChanged();
                                Collections.reverse(temp);
                                saveSQL(temp);
                                if (temp.size() < pageSize) {
                                    Log.e("jxf", "content页loadata已经是最后一条数据");
//                                    channel_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                    channel_lv.addFooterView(footer);
                                    isLoadMore = false;
                                }
                                isFirst = false;
                                activity_channel_loading_iv.clearAnimation();
                                activity_channel_loading_iv.setVisibility(View.GONE);
                            }
                        }
                        else{
                            Log.e("jxf", "content页loadata没有数据了");
                            if (isFirst){
                                empty();
                                if (!cursorIsNull){
                                    channels.clear();
                                    adapter.notifyDataSetChanged();
                                }
                                activity_channel_none.setVisibility(View.VISIBLE);
                                isFirst=false;
                                activity_channel_loading_iv.clearAnimation();
                                activity_channel_loading_iv.setVisibility(View.GONE);
                                isNull=true;
//                                channel_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                isLoadMore = false;
                            }
                            if (isfromStart){
                                if (isNull){
                                    channel_ptr_lv.onRefreshComplete();
                                    isfromStart=false;
                                }else{
                                    channel_ptr_lv.onRefreshComplete();
                                    isfromStart=false;
                                }
                            }
                            if (isfromEnd){
                                //不满足条件 置为false 但是默认加载的时候已经是 false
                                Log.e("jxf", "content页loadata上拉的时候没有数据了");
                                Log.e("jxf", "完成刷新：关闭刷新");
                                channel_ptr_lv.onRefreshComplete();
                                Log.e("jxf", "完成刷新：更改设置改为下拉mode");
                                channel_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                channel_lv.addFooterView(footer);
                                channel_lv.setSelection(adapter.getCount() - 1);
//                                channel_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                isfromEnd = false;
                            }
                            if (isRefresh){
                                //为空的时候 可以下拉  关闭上拉
                                channels.clear();
                                activity_channel_loading_iv.clearAnimation();
                                activity_channel_loading_iv.setVisibility(View.GONE);
                                channel_lv.removeFooterView(footer);
                                adapter.notifyDataSetChanged();
                                activity_channel_none.setVisibility(View.VISIBLE);
                                isRefresh=false;
                                isNull=true;
//                                channel_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                isLoadMore = false;
                            }
                        }

                    }
                    else{
                        String errorCode = jSONObjectAll.optString("errorCode");
                        if (errorCode.equals("2")){
                            if (isFirst) {
                                empty();
                                if (!cursorIsNull){
                                    channels.clear();
                                    adapter.notifyDataSetChanged();
                                }
                                activity_channel_none.setVisibility(View.VISIBLE);
                                isFirst=false;
                                activity_channel_loading_iv.clearAnimation();
                                activity_channel_loading_iv.setVisibility(View.GONE);
                                isNull=true;
//                                channel_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                isLoadMore = false;
                            }
                            if (isfromStart) {
                                if (isNull){
                                    channel_ptr_lv.onRefreshComplete();
                                    isfromStart=false;
                                }else{
                                    channel_ptr_lv.onRefreshComplete();
                                    isfromStart=false;
                                }
                            }
                            if (isfromEnd) {
                                Log.e("jxf","完成刷新：关闭刷新");
                                channel_ptr_lv.onRefreshComplete();
                                Log.e("jxf", "完成刷新：更改设置改为下拉mode");
                                channel_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                channel_lv.addFooterView(footer);
                                // TODO: 2016/3/2 要调节数
                                channel_lv.setSelection(adapter.getCount() - 1);
                                isfromEnd = false;
                            }
                            if (isRefresh){
                                channels.clear();
                                activity_channel_loading_iv.clearAnimation();
                                activity_channel_loading_iv.setVisibility(View.GONE);
                                channel_lv.removeFooterView(footer);
                                adapter.notifyDataSetChanged();
                                activity_channel_none.setVisibility(View.VISIBLE);
                                isRefresh=false;
                                isNull=true;
//                                channel_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                isLoadMore = false;
                            }
                        }else if (errorCode.equals("90001")) {
                            channel_ptr_lv.onRefreshComplete();
                            DefinedSingleToast.showToast(ChannelActivity.this, getResources().getString(R.string.system_exception));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("jxf", "channel页loadata:onresponse出现异常"+e.toString());
                }

            }
        });
    }

    private void netOff() {
        Log.e("jxf","content页loadata:请求失败");
        if (isFirst) {
            activity_channel_loading_iv.clearAnimation();
            activity_channel_loading_iv.setVisibility(View.GONE);
            if (cursorIsNull){
                activity_channel_net_message.setVisibility(View.VISIBLE);
                activity_channel_net_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity_channel_net_message.setVisibility(View.GONE);
                        maxId = 0;
                        minId = 0;
                        isFirst = true;
                        loadData();
                    }
                });
            }
            if (!cursorIsNull){
                DefinedSingleToast.showToast(ChannelActivity.this,getResources().getString(R.string.network_no_force));
                isFirst = false;
            }
        }
        if (isfromStart) {
            if (isNull){
                DefinedSingleToast.showToast(ChannelActivity.this, getResources().getString(R.string.network_no_force));
                channel_ptr_lv.onRefreshComplete();
                isfromStart = false;
            }else{
                DefinedSingleToast.showToast(ChannelActivity.this, getResources().getString(R.string.network_no_force));
                channel_ptr_lv.onRefreshComplete();
                isfromStart = false;
            }
        }
        if (isfromEnd) {
            Log.e("jxf", "完成刷新：关闭刷新");
            channel_ptr_lv.onRefreshComplete();
            Log.e("jxf", "完成刷新：更改设置改为下拉mode");
            channel_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            isLoadMore=true;
            DefinedSingleToast.showToast(ChannelActivity.this, getResources().getString(R.string.network_no_force));
//            channel_ptr_lv.onRefreshComplete();
            isfromEnd = false;
        }
        if (isRefresh){
            channels.clear();
            activity_channel_loading_iv.clearAnimation();
            activity_channel_loading_iv.setVisibility(View.GONE);
            channel_lv.removeFooterView(footer);
            adapter.notifyDataSetChanged();
            activity_channel_net_message.setVisibility(View.VISIBLE);
            activity_channel_net_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity_channel_net_message.setVisibility(View.GONE);
                    maxId = 0;
                    minId = 0;
                    isRefresh = true;
                    loadData();

                }
            });
        }
    }

    private void getHorizontalList() {
        if (catgoryNames.equals("")){
            channel_horizontal_listview.setVisibility(View.GONE);
            channel_listview_group.setVisibility(View.GONE);
        }
        else{
            String[] catgoryNamesArray= catgoryNames.split(",");
            Log.e("jxf","catgoryNamesArray数组长度"+catgoryNamesArray.length);
            String[] catgoryIdsArray= catgoryIds.split(",");
            Log.e("jxf","catgoryIdsArray数组长度"+catgoryIdsArray.length);
            channelCategoryHorizontals.clear();
            ChannelCategoryHorizontalBean channelCategoryHorizontal=new ChannelCategoryHorizontalBean();
            channelCategoryHorizontal.detailCategory=getResources().getString(R.string.subscription_category_all);
            channelCategoryHorizontal.detailCategoryid=0;
            channelCategoryHorizontal.isCheck=true;
            channelCategoryHorizontals.add(channelCategoryHorizontal);
            for (int i=0;i<catgoryNamesArray.length;i++){
                ChannelCategoryHorizontalBean channelCategoryHorizontalfor=new ChannelCategoryHorizontalBean();
                channelCategoryHorizontalfor.detailCategory=catgoryNamesArray[i];
                channelCategoryHorizontalfor.detailCategoryid= Integer.parseInt(catgoryIdsArray[i]);
                channelCategoryHorizontalfor.isCheck=false;
                channelCategoryHorizontals.add(channelCategoryHorizontalfor);
            }
        }
    }

    private void getBundle() {
        Bundle bundle=getIntent().getExtras();
        teacherId=bundle.getInt("teacherId");
        portraitUrlSmall=bundle.getString("portraitUrlSmall");
        nickname=bundle.getString("nickname");
        catgoryNames= bundle.getString("catgoryNames");
        catgoryIds=bundle.getString("catgoryIds");
    }

    private void saveSQL(final ArrayList<Channel> temp){
        new Thread(new Runnable() {
            @Override
            public void run() {
                App.getSqlManager().deleteAllWhere(tableName, "teacher_id=?", new String[]{"" + teacherId});
                Log.e("jxf", "channel页第一次数据库:临时要存储集合的数量" + temp.size());
                for (int i=0;i<temp.size();i++){
                    ContentValues values=new ContentValues();
                    values.put("id",temp.get(i).id);
                    values.put("channelname",temp.get(i).channelName);
                    values.put("price",temp.get(i).channelPrice);
                    values.put("categoryname",temp.get(i).categoryName);
                    values.put("iconurl",temp.get(i).iconUrl);
                    values.put("des",temp.get(i).des);
                    values.put("teacher_id",teacherId);
                    App.getSqlManager().insert(tableName, values);
                }
                Log.e("jxf","channel页:子线程第一次提交数据库完毕！");
            }
        }).start();
    }
    private void pullSaveMore( final ArrayList<Channel> temp){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("jxf","channel页下拉数据库:临时要存储集合的数量"+temp.size());
                for (int i=0;i<temp.size();i++){
                    ContentValues values=new ContentValues();
                    values.put("id",temp.get(i).id);
                    values.put("channelname",temp.get(i).channelName);
                    values.put("price",temp.get(i).channelPrice);
                    values.put("categoryname",temp.get(i).categoryName);
                    values.put("iconurl",temp.get(i).iconUrl);
                    values.put("des",temp.get(i).des);
                    values.put("teacher_id",teacherId);
                    App.getSqlManager().insert(tableName, values);
                    Log.e("jxf","channel页:数据库下拉保存的次数"+i);
                }
                Log.e("jxf", "channel页:子线程下拉提交数据库完毕！");
            }
        }).start();
    }

    private void empty(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                App.getSqlManager().deleteAllWhere(tableName, "teacher_id=?", new String[]{"" + teacherId});
            }
        }).start();
    }


    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_subscript_channel);
    }
    @Override
    protected void onClickEvent(View view) {
        switch (view.getId()){
            case R.id.channel_left:
                Log.e("jxf", "channelactivity界面：头部返回键点击：逐级返回，没有任何操作");
//                ChannelActivity.this.setResult(481);
                ChannelActivity.this.finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==500){
            if (resultCode==501){
                Log.e("jxf", "channelactivity界面：支付界面购买成功：返回频道详情：再返回channelactivity界面");
                ChannelActivity.this.setResult(480);
                ChannelActivity.this.finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            }
            else if (resultCode==502){
                Log.e("jxf", "channelactivity界面：支付界面购买失败：返回频道详情：再返回channelactivity界面");
                ChannelActivity.this.setResult(481);
                ChannelActivity.this.finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            }

        }

    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Log.e("jxf", "channel界面：手机返回键点击：逐级返回：没有任何操作");
//        ChannelActivity.this.setResult(481);
        ChannelActivity.this.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.getSqlManager().close();
    }


    Comparator unordercomp = new Comparator() {
        public int compare(Object o1, Object o2) {
            Channel p1 = (Channel) o1;
            Channel p2 = (Channel) o2;
            if (p1.id < p2.id)
                return 1;
            else if (p1.id == p2.id)
                return 0;
            else if (p1.id > p2.id)
                return -1;
            return 0;
        }
    };

}
