package com.miracleworld.lingxingdao.android.fragment;

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
import com.miracleworld.lingxingdao.android.activity.ChannelActivity2;
import com.miracleworld.lingxingdao.android.activity.SubscriptionActivity;
import com.miracleworld.lingxingdao.android.adapter.ContentAdapter;
import com.miracleworld.lingxingdao.android.base.BaseFragment;
import com.miracleworld.lingxingdao.android.bean.Content;
import com.miracleworld.lingxingdao.android.http.NetClient;
import com.miracleworld.lingxingdao.android.http.NetResponseHandler;
import com.miracleworld.lingxingdao.android.http.Url;
import com.miracleworld.lingxingdao.android.utils.SharedPreUtils;
import com.miracleworld.lingxingdao.android.view.DefinedSingleToast;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by donghaifeng on 2015/12/16
 */
public class ContentFragment extends BaseFragment {
    //需要全局的控件
    private PullToRefreshListView fragment_content_ptr_lv;
    private ListView fragment_content_lv;
    private ImageView fragment_content_loading_iv;
    private View footer;
    private ContentAdapter adapter;
    private RelativeLayout fragment_content_net_message;
    private RotateAnimation rotateAnimation;
    private ImageView fragment_content_none;
    //请求网络的数据
    private int maxId;
    private int minId;
    private int pageSize=10;
    private int catgoryId;
    private int userId;
    private ArrayList<Content> contents;
    //要进行判断的值
    //为跳转做准备。需要判断的boolean
    private boolean isSelsect;
    private boolean isFirst;
    private boolean isfromStart;
    private boolean isfromEnd;
    private boolean isRefresh;
    private boolean isNull;
    //修改3：添加禁止上拉加载的boolean
    private boolean isLoadMore;
    //数据库需要
    private boolean cursorIsNull;
    private String tableName="contents";

    @Override
    protected void initView(View view, Bundle bundle) {
        isSelsect=false;
        Log.e("jxf", "进入content");
        maxId=0;
        minId=0;
        catgoryId=0;
        userId= (int)SharedPreUtils.get(context,"user_id",0);
        Log.e("jxf","内容列表：需要的user_id"+userId);
        contents=new ArrayList<Content>();
        isFirst=true;
        isRefresh=false;
        isNull=false;
        //给个初始化 防止走loaddata中他的方法
        isfromEnd=false;
        //一进来允许上拉加载
        isLoadMore=true;
        fragment_content_none= (ImageView) view.findViewById(R.id.fragment_content_none);
        fragment_content_none.setVisibility(View.GONE);
        fragment_content_net_message= (RelativeLayout) view.findViewById(R.id.fragment_content_net_message);
        fragment_content_net_message.setVisibility(View.GONE);
        fragment_content_loading_iv= (ImageView) view.findViewById(R.id.fragment_content_loading_iv);
        rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(context, R.anim.rotate_refresh_drawable_default);
        footer=View.inflate(context, R.layout.list_footer,null);
        fragment_content_ptr_lv= (PullToRefreshListView) view.findViewById(R.id.fragment_content_ptr_lv);
        //修改1：初始化：禁止上拉
        fragment_content_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        //滑动才会触发  而且是上拉的时候才会触发  下拉的时候触发了onrefreshlistener
        fragment_content_ptr_lv.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                Log.e("jxf","触发setOnLastItemVisibleListener打印 isLoadMore"+isLoadMore);
                if (isLoadMore){
                    Log.e("jxf","触发上拉加载");
                    Log.e("jxf", "设置上拉记载更多");
                    fragment_content_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                    Log.e("jxf","让现实数显");
                    fragment_content_ptr_lv.setRefreshing();
                    //这个是为了标记的boolean  改执行loaddata中那个方法的boolean
                    isfromEnd=true;
                    ArrayList<Content> temp=new ArrayList<Content>();
                    temp.addAll(contents);
                    Collections.sort(temp, unordercomp);
                    minId=temp.get(temp.size()-1).teacherId;
                    Log.e("jxf","content页上拉加载的minId"+minId);
                    maxId=0;
                    loadDatas();
                }
            }
        });
        fragment_content_lv=fragment_content_ptr_lv.getRefreshableView();
        fragment_content_lv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        Cursor cursor= App.getSqlManager().seleteAll(tableName,new String[]{"id1","teacher_id","portraiturl_big","portraiturl_small","nickname","introduce","catgory_name","catgory_id","remark","pricerange","sort"},"id1 desc");
        if (cursor!=null&&cursor.getCount()>0){
            cursorIsNull=false;
            Log.e("jxf", "content数据库有数据");
            while(cursor.moveToNext()) {
                Content content=new Content();
                content.id1=cursor.getInt(0);
                content.teacherId= cursor.getInt(1);
                content.portraitUrlBig= cursor.getString(2);
                content.portraitUrlSmall= cursor.getString(3);
                content.nickname= cursor.getString(4);
                content.introduce= cursor.getString(5);
                content.catgoryName=cursor.getString(6);
                content.catgoryId=cursor.getString(7);
                content.remark=cursor.getString(8);
                content.pricerange=cursor.getString(9);
                content.sort=cursor.getInt(10);
                content.isCheck=false;
                contents.add(content);
            }
            cursor.close();
            if (contents.size()>20){
                int id1=contents.get(19).id1;
                App.getSqlManager().deleteOne(tableName,"id1<? ",new String[]{""+id1});
                for (int i=20;i<contents.size();i=20){
                    contents.remove(i);
                }
                Log.e("jxf","content页从数据库取数据：有数据：集合数量"+contents.size());
            }
            Log.e("jxf","打印排完序的集合"+contents.get(0).id1);
        }
        else{
            cursorIsNull=true;
            Log.e("jxf", "content数据库没有数据");
            cursor.close();
        }
        adapter=new ContentAdapter(context,contents);
        Log.e("jxf","数据库展现数据完毕");
        fragment_content_lv.setAdapter(adapter);
        loadDatas();
        fragment_content_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position>contents.size()){
                    return;
                }
                else{
                    if (isSelsect){
                        Content content=contents.get(position-1);
                        content.isCheck=true;
                        adapter.notifyDataSetChanged();
                        Intent intent=new Intent(context, ChannelActivity2.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("teacherId", contents.get(position - 1).teacherId);
                        bundle.putString("portraitUrlSmall", contents.get(position - 1).portraitUrlSmall);
                        bundle.putString("portraitUrlBig", contents.get(position - 1).portraitUrlBig);
                        bundle.putString("nickname", contents.get(position - 1).nickname);
                        bundle.putString("catgoryName", contents.get(position - 1).catgoryName);
                        bundle.putString("catgoryId", contents.get(position-1).catgoryId);
                        bundle.putInt("catgoryIdMark", catgoryId);
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }
                    else{
                        Content content=contents.get(position-1);
                        content.isCheck=true;
                        adapter.notifyDataSetChanged();
                        Intent intent=new Intent(context, SubscriptionActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("teacherId", contents.get(position - 1).teacherId);
                        bundle.putString("portraitUrlSmall", contents.get(position - 1).portraitUrlSmall);
                        bundle.putString("portraitUrlBig", contents.get(position - 1).portraitUrlBig);
                        bundle.putString("nickname", contents.get(position - 1).nickname);
                        bundle.putString("catgoryName", contents.get(position - 1).catgoryName);
                        bundle.putString("catgoryId", contents.get(position-1).catgoryId);
                        bundle.putString("remark", contents.get(position - 1).remark);
                        bundle.putString("pricerange", contents.get(position - 1).pricerange);
                        intent.putExtras(bundle);
                        context.startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    }

                }
            }
        });
        fragment_content_ptr_lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                Log.e("jxf", "content页下拉刷新");
                isfromStart=true;
                if (isNull){
                    maxId=0;
                }
                else{
                    ArrayList<Content> temp=new ArrayList<Content>();
                    temp.addAll(contents);
                    Collections.sort(temp, unordercomp);
                    maxId=temp.get(0).teacherId;
                }
                Log.e("jxf","content页下拉刷新的maxId"+maxId);
                minId=0;
                loadDatas();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                Log.e("jxf", "content页上拉加载");
//                isfromEnd=true;
//                ArrayList<Content> temp=new ArrayList<Content>();
//                temp.addAll(contents);
//                Collections.sort(temp, unordercomp);
//                minId=temp.get(temp.size()-1).teacherId;
//                Log.e("jxf","content页上拉加载的minId"+minId);
//                maxId=0;
//                loadDatas();
            }
        });
    }

    private void loadDatas() {
        Log.e("jxf","loadDatas打印ischick"+isSelsect);
        //为了第一次数据加载的时候同时下拉
        //上拉加载开始执行loaddata方法的时候：根据标识方法 关闭开关
        if (isfromEnd){
            //上拉在进行了 就不会继续再进行了
            Log.e("jxf","上拉请求网络加载数据，并且关闭开关isLoadMore");
            isLoadMore=false;
            //尝试放开 mode 看疗效吧  不行的 这这更改了模式 执行完的 complete 可能连着一起都会关闭的

        }
        if (isRefresh){
            Log.e("jxf","content页loadata:isRefresh");
            fragment_content_loading_iv.setAnimation(rotateAnimation);
            fragment_content_loading_iv.setVisibility(View.VISIBLE);
        }
        if (isFirst){
            Log.e("jxf", "content页loadata:isfirst");
            fragment_content_loading_iv.setAnimation(rotateAnimation);
            fragment_content_loading_iv.setVisibility(View.VISIBLE);
        }
        ConnectivityManager mConnectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info == null || !mConnectivity.getBackgroundDataSetting()) {
            Log.e("jxf","content页loadata:没网");
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
            Log.e("jxf", "content页loadata:有网");
            netRequestAndResponse();
        }
    }
    private void netRequestAndResponse(){
        RequestParams params = new RequestParams();
        params.put("maxId", maxId);
        params.put("minId", minId);
        params.put("pageSize", pageSize);
        params.put("catgoryId", catgoryId);
        params.put("userId",userId);
        Log.e("jxf","请求参数"+params);
        NetClient.headGet(context, Url.CONTENT, params, new NetResponseHandler() {
            @Override
            public void onResponse(String json) {
                try {
                    JSONObject jSONObjectAll = new JSONObject(json);
                    Log.e("jxf", "content页请求得到响应的json" + json);
                    String status = jSONObjectAll.optString("status");
                    if (status.equals("1")) {
                        JSONArray jSONArray = jSONObjectAll.optJSONArray("datas");
                        if (jSONArray != null) {
                            fragment_content_none.setVisibility(View.GONE);
                            ArrayList<Content> temp = new ArrayList<Content>();
                            int length = jSONArray.length();
                            for (int i = 0; i < length; i++) {
                                JSONObject obj = jSONArray.optJSONObject(i);
                                Log.e("jxf", "content的一条数据：" + obj.toString());
                                Content content = new Content();
                                content.teacherId=obj.optInt("teacherId");
                                content.portraitUrlSmall=obj.optString("portraitUrlSmall");
                                content.portraitUrlBig=obj.optString("portraitUrlBig");
                                content.nickname=obj.optString("nickname");
                                content.introduce=obj.optString("introduce");
                                content.catgoryName=obj.optString("catgoryName");
                                content.catgoryId=obj.optString("catgoryId");
                                content.remark=obj.optString("remark");
                                String temppriceRange=obj.getString("priceRange");
                                content.pricerange=temppriceRange.replace("-","~");
                                content.sort=obj.getInt("sort");
                                content.isCheck=false;
                                temp.add(content);
                            }
                            if (isRefresh){
                                contents.clear();
                                contents.addAll(temp);
                                fragment_content_loading_iv.clearAnimation();
                                fragment_content_loading_iv.setVisibility(View.GONE);
                                adapter.notifyDataSetChanged();
//                                fragment_content_ptr_lv.setMode(PullToRefreshBase.Mode.BOTH);
                                //如果有脚 就移除脚布局
                                fragment_content_lv.removeFooterView(footer);
                                if (temp.size() < pageSize) {
                                    Log.e("jxf", "content页loadata已经是最后一条数据");
//                                    fragment_content_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                    fragment_content_lv.addFooterView(footer);
                                    isLoadMore = false;
                                }
                                isRefresh=false;
                            }
                            if (isfromStart) {
                                if (isNull){
                                    isLoadMore = true;
                                    contents.addAll(temp);
                                    Collections.reverse(temp);
                                    pullSaveMore(temp);
                                    adapter.notifyDataSetChanged();
                                    fragment_content_ptr_lv.onRefreshComplete();
//                                    fragment_content_ptr_lv.setMode(PullToRefreshBase.Mode.BOTH);
                                    if (temp.size() < pageSize) {
                                        Log.e("jxf", "content页loadata已经是最后一条数据");
//                                        fragment_content_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                        fragment_content_lv.addFooterView(footer);
                                        isLoadMore = false;
                                    }
                                    isfromStart = false;
                                    isNull=false;
                                }else{
                                    contents.addAll(0,temp);
                                    Collections.reverse(temp);
                                    pullSaveMore(temp);
                                    adapter.notifyDataSetChanged();
                                    fragment_content_ptr_lv.onRefreshComplete();
                                    isfromStart = false;
                                }
                            }
                            if (isfromEnd){
                                Log.e("jxf", "完成刷新：关闭刷新");
                                fragment_content_ptr_lv.onRefreshComplete();
                                isLoadMore = true;
                                contents.addAll(temp);
//                                fragment_content_ptr_lv.onRefreshComplete();
                                if (temp.size() < pageSize) {
                                    Log.e("jxf", "content页loadata已经是最后一条数据");
//                                    fragment_content_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                    fragment_content_lv.addFooterView(footer);
                                    isLoadMore = false;
                                }
                                adapter.notifyDataSetChanged();
                                Log.e("jxf", "完成刷新：更改设置改为下拉mode");
                                isfromEnd = false;
                                fragment_content_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            }
                            if (isFirst) {
                                if (!cursorIsNull){
                                    contents.clear();
                                }
                                contents.addAll(temp);
                                adapter.notifyDataSetChanged();
                                Collections.reverse(temp);
                                saveSQL(temp);
                                if (temp.size() < pageSize) {
                                    Log.e("jxf", "content页loadata已经是最后一条数据");
//                                    fragment_content_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                    fragment_content_lv.addFooterView(footer);
                                    isLoadMore = false;
                                }
                                isFirst = false;
                                fragment_content_loading_iv.clearAnimation();
                                fragment_content_loading_iv.setVisibility(View.GONE);
                            }
                        }
                        else {
                            Log.e("jxf", "content页loadata没有数据了");
                            if (isFirst){
                                empty();
                                if (!cursorIsNull){
                                    contents.clear();
                                    adapter.notifyDataSetChanged();
                                }
                                fragment_content_none.setVisibility(View.VISIBLE);
                                isFirst=false;
                                fragment_content_loading_iv.clearAnimation();
                                fragment_content_loading_iv.setVisibility(View.GONE);
                                isNull=true;
//                                fragment_content_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                isLoadMore = false;
                            }
                            if (isfromStart){
                                if (isNull){
                                    fragment_content_ptr_lv.onRefreshComplete();
                                    isfromStart=false;
                                }else{
                                    fragment_content_ptr_lv.onRefreshComplete();
                                    isfromStart=false;
                                }
                            }
                            if (isfromEnd){
                                //不满足条件 置为false 但是默认加载的时候已经是 false
//                                isLoadMore=false;
                                Log.e("jxf", "content页loadata上拉的时候没有数据了");
                                Log.e("jxf", "完成刷新：关闭刷新");
                                fragment_content_ptr_lv.onRefreshComplete();
                                Log.e("jxf", "完成刷新：更改设置改为下拉mode");
                                fragment_content_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                fragment_content_lv.addFooterView(footer);
                                fragment_content_lv.setSelection(adapter.getCount() - 1);
                                isfromEnd = false;
                            }
                            if (isRefresh){
                                //为空的时候 可以下拉  关闭上拉
                                contents.clear();
                                fragment_content_loading_iv.clearAnimation();
                                fragment_content_loading_iv.setVisibility(View.GONE);
                                fragment_content_lv.removeFooterView(footer);
                                adapter.notifyDataSetChanged();
                                fragment_content_none.setVisibility(View.VISIBLE);
                                isRefresh=false;
                                isNull=true;
                                isLoadMore = false;
                            }
                        }
                    }
                    else {
                        String errorCode = jSONObjectAll.optString("errorCode");
                        if (errorCode.equals("2")){
                            if (isFirst) {
                                empty();
                                if (!cursorIsNull){
                                    contents.clear();
                                    adapter.notifyDataSetChanged();
                                }
                                fragment_content_none.setVisibility(View.VISIBLE);
                                isFirst=false;
                                fragment_content_loading_iv.clearAnimation();
                                fragment_content_loading_iv.setVisibility(View.GONE);
                                isNull=true;
//                                fragment_content_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                isLoadMore = false;
                            }
                            if (isfromStart) {
                                if (isNull){
                                    fragment_content_ptr_lv.onRefreshComplete();
                                    isfromStart=false;
                                }else{
                                    fragment_content_ptr_lv.onRefreshComplete();
                                    isfromStart=false;
                                }
                            }
                            if (isfromEnd) {
                                Log.e("jxf","完成刷新：关闭刷新");
                                fragment_content_ptr_lv.onRefreshComplete();
                                Log.e("jxf", "完成刷新：更改设置改为下拉mode");
                                fragment_content_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                fragment_content_lv.addFooterView(footer);
                                // TODO: 2016/3/2 要调节数
                                fragment_content_lv.setSelection(adapter.getCount() - 1);
                                isfromEnd = false;
                            }
                            if (isRefresh){
                                contents.clear();
                                fragment_content_loading_iv.clearAnimation();
                                fragment_content_loading_iv.setVisibility(View.GONE);
                                fragment_content_lv.removeFooterView(footer);
                                adapter.notifyDataSetChanged();
                                fragment_content_none.setVisibility(View.VISIBLE);
                                isRefresh=false;
                                isNull=true;
//                                fragment_content_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                isLoadMore = false;
                            }
                        }else if (errorCode.equals("90001")) {
                            fragment_content_ptr_lv.onRefreshComplete();
                            DefinedSingleToast.showToast(context, getResources().getString(R.string.system_exception));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("jxf", "content页loadata:onresponse出现异常"+e.toString());
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.e("jxf","content页loadata:onfail错误码："+throwable.toString());
                netOff();
            }
        });
    }
    private void netOff(){
        Log.e("jxf", "content页loadata:请求失败");
        if (isFirst) {
            fragment_content_loading_iv.clearAnimation();
            fragment_content_loading_iv.setVisibility(View.GONE);
            if (cursorIsNull){
                fragment_content_net_message.setVisibility(View.VISIBLE);
                fragment_content_net_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fragment_content_net_message.setVisibility(View.GONE);
                        maxId = 0;
                        minId = 0;
                        isFirst = true;
                        loadDatas();
                    }
                });
            }
            if (!cursorIsNull){
                DefinedSingleToast.showToast(context,getResources().getString(R.string.network_no_force));
                isFirst = false;
            }
        }
        if (isfromStart) {
            if (isNull){
                DefinedSingleToast.showToast(context, getResources().getString(R.string.network_no_force));
                fragment_content_ptr_lv.onRefreshComplete();
                isfromStart = false;
            }else{
                DefinedSingleToast.showToast(context, getResources().getString(R.string.network_no_force));
                fragment_content_ptr_lv.onRefreshComplete();
                isfromStart = false;
            }
        }
        if (isfromEnd) {
            Log.e("jxf", "完成刷新：关闭刷新");
            fragment_content_ptr_lv.onRefreshComplete();
            Log.e("jxf", "完成刷新：更改设置改为下拉mode");
            fragment_content_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            isLoadMore=true;
            DefinedSingleToast.showToast(context, getResources().getString(R.string.network_no_force));
//            fragment_content_ptr_lv.onRefreshComplete();
            isfromEnd = false;
        }
        if (isRefresh){
            contents.clear();
            fragment_content_loading_iv.clearAnimation();
            fragment_content_loading_iv.setVisibility(View.GONE);
            fragment_content_lv.removeFooterView(footer);
            adapter.notifyDataSetChanged();
            fragment_content_net_message.setVisibility(View.VISIBLE);
            fragment_content_net_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragment_content_net_message.setVisibility(View.GONE);
                    maxId = 0;
                    minId = 0;
                    isRefresh = true;
                    loadDatas();
                }
            });
        }
    }

    private void saveSQL(final ArrayList<Content> temp){
        new Thread(new Runnable() {
            @Override
            public void run() {
                App.getSqlManager().deleteAll(tableName);
                Log.e("jxf", "content页第一次数据库:临时要存储集合的数量" + temp.size());
                for (int i=0;i<temp.size();i++){
                    ContentValues values=new ContentValues();
                    values.put("teacher_id",temp.get(i).teacherId);
                    values.put("portraiturl_big",temp.get(i).portraitUrlBig);
                    values.put("portraiturl_small",temp.get(i).portraitUrlSmall);
                    values.put("nickname",temp.get(i).nickname);
                    values.put("introduce",temp.get(i).introduce);
                    values.put("catgory_name",temp.get(i).catgoryName);
                    values.put("catgory_id",temp.get(i).catgoryId);
                    values.put("remark",temp.get(i).remark);
                    values.put("pricerange", temp.get(i).pricerange);
                    values.put("sort", temp.get(i).sort);
                    App.getSqlManager().insert(tableName, values);
                }
                Log.e("jxf","content页:子线程第一次提交数据库完毕！");
            }
        }).start();
    }
    private void pullSaveMore( final ArrayList<Content> temp){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("jxf","content页下拉数据库:临时要存储集合的数量"+temp.size());
                for (int i=0;i<temp.size();i++){
                    ContentValues values=new ContentValues();
                    values.put("teacher_id",temp.get(i).teacherId);
                    values.put("portraiturl_big",temp.get(i).portraitUrlBig);
                    values.put("portraiturl_small",temp.get(i).portraitUrlSmall);
                    values.put("nickname",temp.get(i).nickname);
                    values.put("introduce",temp.get(i).introduce);
                    values.put("catgory_name",temp.get(i).catgoryName);
                    values.put("catgory_id",temp.get(i).catgoryId);
                    values.put("remark",temp.get(i).remark);
                    values.put("pricerange", temp.get(i).pricerange);
                    values.put("sort", temp.get(i).sort);
                    App.getSqlManager().insert(tableName, values);
                    Log.e("jxf", "content页:数据库下拉保存的次数" + i);
                }
                Log.e("jxf", "content页:子线程下拉提交数据库完毕！");
            }
        }).start();
    }

    private void empty(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                App.getSqlManager().deleteAll(tableName);
            }
        }).start();
    }

    @Override
    protected int setLayout() {
        return R.layout.content_layout_fragment;
    }

    @Override
    protected void onClickEvent(View view) {

    }

    public void refreshContent(int catgory){
        Log.e("jxf","打印ischick"+isSelsect);
        maxId=0;
        minId=0;
        catgoryId=catgory;
        isFirst=false;
        isRefresh=true;
        isNull=false;
        //以下回复初始状态：refresh的时候 没有数据库了 只有 isnull
        //以下开关重置
        isfromEnd=false;
        //一进来允许上拉加载
        isLoadMore=true;
        fragment_content_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);

        fragment_content_net_message.setVisibility(View.GONE);
        fragment_content_none.setVisibility(View.GONE);
        loadDatas();
    }
    public void refreshIsCheck(boolean ischeck){
        Log.e("jxf","回调方法打ischeck"+isSelsect);
        isSelsect=ischeck;
        Log.e("jxf","回调方法打isCheck"+isSelsect);
    }

    Comparator unordercomp = new Comparator() {
        public int compare(Object o1, Object o2) {
            Content p1 = (Content) o1;
            Content p2 = (Content) o2;
            if (p1.teacherId < p2.teacherId)
                return 1;
            else if (p1.teacherId == p2.teacherId)
                return 0;
            else if (p1.teacherId > p2.teacherId)
                return -1;
            return 0;
        }
    };

}
