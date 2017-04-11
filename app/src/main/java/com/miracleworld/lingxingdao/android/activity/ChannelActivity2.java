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
import com.miracleworld.lingxingdao.android.adapter.ChannelHorizontrallvAdapter;
import com.miracleworld.lingxingdao.android.adapter.ChannelListAdapter;
import com.miracleworld.lingxingdao.android.base.BaseActivity;
import com.miracleworld.lingxingdao.android.bean.Channel;
import com.miracleworld.lingxingdao.android.bean.ChannelCategoryHorizontalBean;
import com.miracleworld.lingxingdao.android.http.NetClient;
import com.miracleworld.lingxingdao.android.http.NetResponseHandler;
import com.miracleworld.lingxingdao.android.http.Url;
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
 * Created by donghaifeng on 2016/2/1.
 */
public class ChannelActivity2 extends BaseActivity{

    //上页传递的值
    private int teacherId;
    //以下三个在本页用不着，需要在支付页面使用
    private String portraitUrlSmall;
    private String portraitUrlBig;
    private String nickname;
    //以上三个在本页用不着，需要在支付页面使用
    private String catgoryNames;
    private String catgoryIds;
    private int catgoryIdMark;

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
    private String tableName="channel2";
    //规划：使用同一个bean对象；数据库添加categoryid 字段:用于查找删除：不同id有不同的内容：同时使用teacherid连着查

    //需要的集合
    private ArrayList<Channel> channels;
    private ArrayList<ChannelCategoryHorizontalBean> channelCategoryHorizontals;
    //横向列表
    private HorizontalListView channel2_horizontal_listview;
    private ImageView activity_channel2_none;
    private RelativeLayout activity_channel2_net_message;
    private ImageView activity_channel2_loading_iv;
    private RotateAnimation rotateAnimation;
    private View footer;
    private RelativeLayout channel2_listview_group;
    private PullToRefreshListView channel2_ptr_lv;
    private ListView channel2_lv;
    //适配器
    private ChannelHorizontrallvAdapter channelHorizontrallvAdapter;
    private ChannelListAdapter adapter;
    @Override
    protected void initView() {
        getBundle();
        //相关值的初始化
        isFirst=true;
        isfromStart=false;

        isRefresh=false;
        isNull=false;
        //重点：默认的不再是全部
        categoryId=catgoryIdMark;
        maxId=0;
        minId=0;
        //给个初始化 防止走loaddata中他的方法
        isfromEnd=false;
        //一进来允许上拉加载
        isLoadMore=true;

        channels=new ArrayList<Channel>();
        channelCategoryHorizontals=new ArrayList<ChannelCategoryHorizontalBean>();
        //获得控件以及赋值
        RelativeLayout channel2_left= (RelativeLayout) findViewById(R.id.channel2_left);
        channel2_left.setOnClickListener(this);
        //横向列表
        channel2_horizontal_listview= (HorizontalListView) findViewById(R.id.channel2_horizontal_listview);
        activity_channel2_none= (ImageView) findViewById(R.id.activity_channel2_none);
        activity_channel2_none.setVisibility(View.GONE);
        activity_channel2_net_message= (RelativeLayout) findViewById(R.id.activity_channel2_net_message);
        activity_channel2_net_message.setVisibility(View.GONE);
        //默认是gone
        activity_channel2_loading_iv= (ImageView) findViewById(R.id.activity_channel2_loading_iv);
        rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.rotate_refresh_drawable_default);
        footer=View.inflate(this, R.layout.list_footer,null);
        channel2_listview_group= (RelativeLayout) findViewById(R.id.channel2_listview_group);
        channel2_ptr_lv= (PullToRefreshListView) findViewById(R.id.channel2_ptr_lv);
        //修改1：初始化：禁止上拉
        channel2_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        //滑动才会触发  而且是上拉的时候才会触发  下拉的时候触发了onrefreshlistener
        channel2_ptr_lv.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                Log.e("jxf","触发setOnLastItemVisibleListener打印 isLoadMore"+isLoadMore);
                if (isLoadMore){
                    Log.e("jxf","触发上拉加载");
                    Log.e("jxf", "设置上拉记载更多");
                    channel2_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                    Log.e("jxf","让现实数显");
                    channel2_ptr_lv.setRefreshing();
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
        channel2_ptr_lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
        channel2_lv= channel2_ptr_lv.getRefreshableView();
        channel2_lv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        channel2_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position>channels.size()){
                    return;
                }else{
                    //实际位置应该是position-1:因为有头
                    Log.e("jxf", "channel的条目点击的位置：" + position);
                    //loadTime(position - 1);
                    Intent intent=new Intent(ChannelActivity2.this,ChannelDetailActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putInt("teacherId",teacherId);
                    bundle.putString("portraitUrlSmall", portraitUrlSmall);
                    bundle.putString("nickname", nickname);
                    bundle.putInt("channelId", channels.get(position - 1).id);
                    bundle.putDouble("channelPrice",channels.get(position-1).channelPrice);
                    bundle.putString("des",channels.get(position-1).des);
                    //支付页使用的channename
                    bundle.putString("channelName",channels.get(position-1).channelName);
                    bundle.putString("startSource","2");
                    intent.putExtras(bundle);
                    ChannelActivity2.this.startActivity(intent);
                    ChannelActivity2.this.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }

            }
        });
        getHorizontalList();
        //构建adapter适配器
        channelHorizontrallvAdapter=new ChannelHorizontrallvAdapter(this,channelCategoryHorizontals);
        channel2_horizontal_listview.setAdapter(channelHorizontrallvAdapter);
        channel2_horizontal_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                channel2_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

                activity_channel2_net_message.setVisibility(View.GONE);
                activity_channel2_none.setVisibility(View.GONE);
                loadData();
            }
        });
        //主键的倒序
        Cursor cursor= App.getSqlManager().seleteAllWhere(tableName, new String[]{"id1","id", "channelname", "price", "categoryname", "iconurl","des"}, "teacher_id=? and categoryid=?",new String[]{""+teacherId,""+catgoryIdMark},"id1 desc");
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
                App.getSqlManager().deleteOne(tableName, "id1<? and teacher_id=? and categoryid=?", new String[]{""+id1,""+teacherId,""+catgoryIdMark});
                for (int i=20;i<channels.size();i=20){
                    channels.remove(i);
                }
                Log.e("jxf","channel页从数据库取数据：有数据：集合数量"+channels.size());
            }
        }else{
            cursorIsNull=true;
            Log.e("jxf", "channel数据库没有数据");
            cursor.close();
        }
        App.getSqlManager().close();
        adapter=new ChannelListAdapter(ChannelActivity2.this,channels);
        channel2_lv.setAdapter(adapter);
        loadData();
    }

    private void getHorizontalList() {
        if (catgoryNames.equals("")){
            channel2_horizontal_listview.setVisibility(View.GONE);
            channel2_listview_group.setVisibility(View.GONE);
        }
        else{
            String[] catgoryNamesArray= catgoryNames.split(",");
            Log.e("jxf", "catgoryNamesArray数组长度" + catgoryNamesArray.length);
            String[] catgoryIdsArray= catgoryIds.split(",");
            Log.e("jxf","catgoryIdsArray数组长度"+catgoryIdsArray.length);
            channelCategoryHorizontals.clear();
            ChannelCategoryHorizontalBean channelCategoryHorizontal=new ChannelCategoryHorizontalBean();
            channelCategoryHorizontal.detailCategory=getResources().getString(R.string.subscription_category_all);
            channelCategoryHorizontal.detailCategoryid=0;
            channelCategoryHorizontal.isCheck=false;
            channelCategoryHorizontals.add(channelCategoryHorizontal);
            for (int i=0;i<catgoryNamesArray.length;i++){
                ChannelCategoryHorizontalBean channelCategoryHorizontalfor=new ChannelCategoryHorizontalBean();
                channelCategoryHorizontalfor.detailCategory=catgoryNamesArray[i];
                channelCategoryHorizontalfor.detailCategoryid= Integer.parseInt(catgoryIdsArray[i]);
                channelCategoryHorizontalfor.isCheck=false;
                channelCategoryHorizontals.add(channelCategoryHorizontalfor);
            }
            //遍历更改初始化
            for (int i=0;i<channelCategoryHorizontals.size();i++){
                if (channelCategoryHorizontals.get(i).detailCategoryid==catgoryIdMark){
                    channelCategoryHorizontals.get(i).isCheck=true;
                }
                else{
                    channelCategoryHorizontals.get(i).isCheck=false;
                }
            }
        }
    }

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
            activity_channel2_loading_iv.setAnimation(rotateAnimation);
            activity_channel2_loading_iv.setVisibility(View.VISIBLE);
        }
        if (isRefresh){
            Log.e("jxf","channel页loadata:isRefresh");
            activity_channel2_loading_iv.setAnimation(rotateAnimation);
            activity_channel2_loading_iv.setVisibility(View.VISIBLE);
        }
        ConnectivityManager mConnectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info == null || !mConnectivity.getBackgroundDataSetting()) {
            Log.e("jxf", "channel页loadata:没网");
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
                Log.e("jxf", "channel页loadata:onfail错误码：" + throwable.toString());
                netOff();
            }

            @Override
            public void onResponse(String json) {
                try {
                    JSONObject jSONObjectAll = new JSONObject(json);
                    Log.e("jxf", "channel页请求得到响应的json" + json);
                    String status = jSONObjectAll.optString("status");
                    if (status.equals("1")) {
                        JSONArray jSONArray = jSONObjectAll.optJSONArray("datas");
                        if (jSONArray != null) {
                            activity_channel2_none.setVisibility(View.GONE);
                            ArrayList<Channel> temp = new ArrayList<Channel>();
                            int length = jSONArray.length();
                            for (int i = 0; i < length; i++) {
                                JSONObject obj = jSONArray.optJSONObject(i);
                                Log.e("jxf", "channel的一条数据：" + obj.toString());
                                Channel channel = new Channel();
                                channel.id = obj.optInt("id");
                                channel.channelName = obj.optString("channelName");
                                channel.channelPrice = obj.optDouble("channelPrice");
                                channel.categoryName = obj.optString("categoryName");
                                channel.iconUrl = obj.getString("iconUrl");
                                channel.des = obj.getString("des");
                                temp.add(channel);
                            }
                            if (isRefresh) {
                                channels.clear();
                                channels.addAll(temp);
                                activity_channel2_loading_iv.clearAnimation();
                                activity_channel2_loading_iv.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
//                                channel2_ptr_lv.setMode(PullToRefreshBase.Mode.BOTH);
                                channel2_lv.removeFooterView(footer);
                                if (temp.size() < pageSize) {
                                    Log.e("jxf", "content页loadata已经是最后一条数据");
//                                    channel2_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                    channel2_lv.addFooterView(footer);
                                    isLoadMore = false;
                                }
                                isRefresh = false;
                            }
                            if (isfromStart) {
                                if (isNull) {
                                    isLoadMore = true;
                                    channels.addAll(temp);
                                    Collections.reverse(temp);
                                    pullSaveMore(temp);
                                    adapter.notifyDataSetChanged();
                                    channel2_ptr_lv.onRefreshComplete();
                                    if (temp.size() < pageSize) {
                                        Log.e("jxf", "content页loadata已经是最后一条数据");
//                                        channel2_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                        channel2_lv.addFooterView(footer);
                                        isLoadMore = false;
                                    }
                                    isfromStart = false;
                                    isNull = false;
//                                    channel2_ptr_lv.setMode(PullToRefreshBase.Mode.BOTH);

                                } else {
                                    channels.addAll(0, temp);
                                    Collections.reverse(temp);
                                    pullSaveMore(temp);
                                    adapter.notifyDataSetChanged();
                                    channel2_ptr_lv.onRefreshComplete();
                                    isfromStart = false;
                                }
                            }
                            if (isfromEnd) {
                                Log.e("jxf", "完成刷新：关闭刷新");
                                channel2_ptr_lv.onRefreshComplete();
                                isLoadMore = true;
                                channels.addAll(temp);
//                                channel2_ptr_lv.onRefreshComplete();
                                if (temp.size() < pageSize) {
                                    Log.e("jxf", "content页loadata已经是最后一条数据");
//                                    channel2_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                    channel2_lv.addFooterView(footer);
                                    isLoadMore = false;
                                }
                                adapter.notifyDataSetChanged();
                                isfromEnd = false;
                                Log.e("jxf", "完成刷新：更改设置改为下拉mode");
                                channel2_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            }
                            if (isFirst) {
                                if (!cursorIsNull) {
                                    channels.clear();
                                }
                                channels.addAll(temp);
                                adapter.notifyDataSetChanged();
                                Collections.reverse(temp);
                                saveSQL(temp);
                                if (temp.size() < pageSize) {
                                    Log.e("jxf", "content页loadata已经是最后一条数据");
//                                    channel2_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                    channel2_lv.addFooterView(footer);
                                    isLoadMore = false;
                                }
                                isFirst = false;
                                activity_channel2_loading_iv.clearAnimation();
                                activity_channel2_loading_iv.setVisibility(View.GONE);
                            }
                        } else {
                            Log.e("jxf", "content页loadata没有数据了");
                            if (isFirst) {
                                empty();
                                if (!cursorIsNull) {
                                    channels.clear();
                                    adapter.notifyDataSetChanged();
                                }
                                activity_channel2_none.setVisibility(View.VISIBLE);
                                isFirst = false;
                                activity_channel2_loading_iv.clearAnimation();
                                activity_channel2_loading_iv.setVisibility(View.GONE);
                                isNull = true;
//                                channel2_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                isLoadMore = false;
                            }
                            if (isfromStart) {
                                if (isNull) {
                                    channel2_ptr_lv.onRefreshComplete();
                                    isfromStart = false;
                                } else {
                                    channel2_ptr_lv.onRefreshComplete();
                                    isfromStart = false;
                                }
                            }
                            if (isfromEnd) {
                                //不满足条件 置为false 但是默认加载的时候已经是 false
                                Log.e("jxf", "content页loadata上拉的时候没有数据了");
                                Log.e("jxf", "完成刷新：关闭刷新");
                                channel2_ptr_lv.onRefreshComplete();
                                Log.e("jxf", "完成刷新：更改设置改为下拉mode");
                                channel2_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                channel2_lv.addFooterView(footer);
                                channel2_lv.setSelection(adapter.getCount() - 1);
//                                channel2_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                isfromEnd = false;
                            }
                            if (isRefresh) {
                                //为空的时候 可以下拉  关闭上拉
                                channels.clear();
                                activity_channel2_loading_iv.clearAnimation();
                                activity_channel2_loading_iv.setVisibility(View.GONE);
                                channel2_lv.removeFooterView(footer);
                                adapter.notifyDataSetChanged();
                                activity_channel2_none.setVisibility(View.VISIBLE);
                                isRefresh = false;
                                isNull = true;
//                                channel2_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                isLoadMore = false;
                            }
                        }

                    } else {
                        String errorCode = jSONObjectAll.optString("errorCode");
                        if (errorCode.equals("2")) {
                            if (isFirst) {
                                empty();
                                if (!cursorIsNull) {
                                    channels.clear();
                                    adapter.notifyDataSetChanged();
                                }
                                activity_channel2_none.setVisibility(View.VISIBLE);
                                isFirst = false;
                                activity_channel2_loading_iv.clearAnimation();
                                activity_channel2_loading_iv.setVisibility(View.GONE);
                                isNull = true;
//                                channel2_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                isLoadMore = false;
                            }
                            if (isfromStart) {
                                if (isNull) {
                                    channel2_ptr_lv.onRefreshComplete();
                                    isfromStart = false;
                                } else {
                                    channel2_ptr_lv.onRefreshComplete();
                                    isfromStart = false;
                                }
                            }
                            if (isfromEnd) {
                                Log.e("jxf","完成刷新：关闭刷新");
                                channel2_ptr_lv.onRefreshComplete();
                                Log.e("jxf", "完成刷新：更改设置改为下拉mode");
                                channel2_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                channel2_lv.addFooterView(footer);
                                // TODO: 2016/3/2 要调节数
                                channel2_lv.setSelection(adapter.getCount() - 1);
                                isfromEnd = false;
                            }
                            if (isRefresh) {
                                channels.clear();
                                activity_channel2_loading_iv.clearAnimation();
                                activity_channel2_loading_iv.setVisibility(View.GONE);
                                channel2_lv.removeFooterView(footer);
                                adapter.notifyDataSetChanged();
                                activity_channel2_none.setVisibility(View.VISIBLE);
                                isRefresh = false;
                                isNull = true;
//                                channel2_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                isLoadMore = false;
                            }
                        } else if (errorCode.equals("90001")) {
                            channel2_ptr_lv.onRefreshComplete();
                            DefinedSingleToast.showToast(ChannelActivity2 .this, getResources().getString(R.string.system_exception));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("jxf", "channel页loadata:onresponse出现异常" + e.toString());
                }

            }
        });
    }

    private void netOff() {
        Log.e("jxf","content页loadata:请求失败");
        if (isFirst) {
            activity_channel2_loading_iv.clearAnimation();
            activity_channel2_loading_iv.setVisibility(View.GONE);
            if (cursorIsNull){
                activity_channel2_net_message.setVisibility(View.VISIBLE);
                activity_channel2_net_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        activity_channel2_net_message.setVisibility(View.GONE);
                        maxId = 0;
                        minId = 0;
                        isFirst = true;
                        loadData();
                    }
                });
            }
            if (!cursorIsNull){
                DefinedSingleToast.showToast(ChannelActivity2.this,getResources().getString(R.string.network_no_force));
                isFirst = false;
            }
        }
        if (isfromStart) {
            if (isNull){
                DefinedSingleToast.showToast(ChannelActivity2.this, getResources().getString(R.string.network_no_force));
                channel2_ptr_lv.onRefreshComplete();
                isfromStart = false;
            }else{
                DefinedSingleToast.showToast(ChannelActivity2.this, getResources().getString(R.string.network_no_force));
                channel2_ptr_lv.onRefreshComplete();
                isfromStart = false;
            }
        }
        if (isfromEnd) {
            Log.e("jxf", "完成刷新：关闭刷新");
            channel2_ptr_lv.onRefreshComplete();
            Log.e("jxf", "完成刷新：更改设置改为下拉mode");
            channel2_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            isLoadMore=true;
            DefinedSingleToast.showToast(ChannelActivity2.this,getResources().getString(R.string.network_no_force));
//            channel2_ptr_lv.onRefreshComplete();
            isfromEnd = false;
        }
        if (isRefresh){
            channels.clear();
            activity_channel2_loading_iv.clearAnimation();
            activity_channel2_loading_iv.setVisibility(View.GONE);
            channel2_lv.removeFooterView(footer);
            adapter.notifyDataSetChanged();
            activity_channel2_net_message.setVisibility(View.VISIBLE);
            activity_channel2_net_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity_channel2_net_message.setVisibility(View.GONE);
                    maxId = 0;
                    minId = 0;
                    isRefresh = true;
                    loadData();

                }
            });
        }
    }

    private void getBundle() {
        Bundle bundle=getIntent().getExtras();
        teacherId=bundle.getInt("teacherId");
        portraitUrlSmall=bundle.getString("portraitUrlSmall");
        portraitUrlBig=bundle.getString("portraitUrlBig");
        nickname=bundle.getString("nickname");
        catgoryNames=bundle.getString("catgoryName");
        catgoryIds=bundle.getString("catgoryId");
        catgoryIdMark= bundle.getInt("catgoryIdMark");
    }

    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_subscript_channel2);
    }

    @Override
    protected void onClickEvent(View view) {
        switch (view.getId()){
            case R.id.channel2_left:
                ChannelActivity2.this.finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.getSqlManager().close();
    }

    @Override
    public void onBackPressed() {
        ChannelActivity2.this.finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    private void saveSQL(final ArrayList<Channel> temp){
        new Thread(new Runnable() {
            @Override
            public void run() {
                App.getSqlManager().deleteAllWhere(tableName, "teacher_id=? and categoryid=?", new String[]{"" + teacherId,""+categoryId});
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
                    values.put("categoryid",categoryId);
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
                    values.put("categoryid",categoryId);
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
                App.getSqlManager().deleteAllWhere(tableName, "teacher_id=? and categoryid=?", new String[]{"" + teacherId,""+categoryId});
            }
        }).start();
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
