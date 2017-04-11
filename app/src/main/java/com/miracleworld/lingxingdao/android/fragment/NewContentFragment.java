package com.miracleworld.lingxingdao.android.fragment;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.RequestParams;
import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.adapter.NewContentAdapter;
import com.miracleworld.lingxingdao.android.base.BaseFragment;
import com.miracleworld.lingxingdao.android.bean.NewContentMusic;
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

/**
 * Created by donghaifeng on 2016/3/8.
 */
public class NewContentFragment extends BaseFragment {

    private PullToRefreshListView fragment_newcontent_ptr_lv;
    private ImageView fragment_newcontent_loading_iv;
    private RelativeLayout fragment_newcontent_net_message;
    private ImageView fragment_newcontent_none;
    private View footer;
    private RotateAnimation rotateAnimation;
    private ListView fragment_newcontent_lv;
    private NewContentAdapter adapter;

    //请求网络的数据
    private int userId;
    private int maxId;
    private int minId;
    private int pageSize=20;
    private ArrayList<NewContentMusic> newContentMusics;
    //要进行判断的值
    private boolean isFirst;
    private boolean isfromStart;
    private boolean isfromEnd;
    private boolean isNull;
    //修改3：添加禁止上拉加载的boolean
    private boolean isLoadMore;
    //不用操作数据库


    @Override
    protected void initView(View view, Bundle bundle) {
        Log.e("jxf", "进入home");
        userId=(int) SharedPreUtils.get(context, "user_id", 0);
        maxId=0;
        minId=0;
        newContentMusics=new ArrayList<NewContentMusic>();
        isFirst=true;
        isNull=false;
        isfromEnd=false;
        //一进来允许上拉加载
        isLoadMore=true;
        //初始化控件
        //刷新球
        fragment_newcontent_loading_iv= (ImageView) view.findViewById(R.id.fragment_newcontent_loading_iv);
        //网络不好的提示
        fragment_newcontent_net_message= (RelativeLayout) view.findViewById(R.id.fragment_newcontent_net_message);
        //没有数据
        fragment_newcontent_none= (ImageView) view.findViewById(R.id.fragment_newcontent_none);
        footer=View.inflate(context, R.layout.list_footer,null);
        rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(context, R.anim.rotate_refresh_drawable_default);
        //上拉下拉控件
        fragment_newcontent_ptr_lv= (PullToRefreshListView) view.findViewById(R.id.fragment_newcontent_ptr_lv);
        //修改1：初始化：禁止上拉
        fragment_newcontent_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        fragment_newcontent_ptr_lv.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if (isLoadMore) {
                    Log.e("jxf", "触发上拉加载");
                    Log.e("jxf", "设置上拉记载更多");
                    fragment_newcontent_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                    Log.e("jxf", "让现实数显");
                    fragment_newcontent_ptr_lv.setRefreshing();
                    //这个是为了标记的boolean  改执行loaddata中那个方法的boolean
                    isfromEnd = true;
                    ArrayList<NewContentMusic> temp = new ArrayList<NewContentMusic>();
                    temp.addAll(newContentMusics);
                    Collections.sort(temp, unordercomp);
                    minId = temp.get(temp.size() - 1).id;
                    Log.e("jxf", "home页的上拉加载的minId：" + minId);
                    maxId = 0;
                    loadDatas();
                }
            }
        });
        fragment_newcontent_lv=fragment_newcontent_ptr_lv.getRefreshableView();
        fragment_newcontent_lv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        adapter=new NewContentAdapter(context, newContentMusics);
        fragment_newcontent_lv.setAdapter(adapter);
        loadDatas();
        fragment_newcontent_ptr_lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                Log.e("jxf", "home页的下拉刷新");
                isfromStart = true;
                if (isNull) {
                    maxId = 0;
                } else {
                    ArrayList<NewContentMusic> temp = new ArrayList<NewContentMusic>();
                    temp.addAll(newContentMusics);
                    Collections.sort(temp, unordercomp);
                    maxId = temp.get(0).id;
                }
                Log.e("jxf", "home页的下拉刷新的maxId：" + maxId);
                minId = 0;
                loadDatas();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                Log.e("jxf", "home页的onPullUpToRefresh上拉加载");
//                isfromEnd=true;
//                ArrayList<HomeNewest> temp=new ArrayList<HomeNewest>();
//                temp.addAll(homeNewests);
//                Collections.sort(temp, unordercomp);
//                minId=temp.get(temp.size()-1).id;
//                Log.e("jxf","home页的上拉加载的minId："+minId);
//                maxId=0;
//                loadDatas();
            }
        });
    }

    private void loadDatas() {
        //为了第一次数据加载的时候同时下拉
        //上拉加载开始执行loaddata方法的时候：根据标识方法 关闭开关
        //第一次加载出来之前 isLoadMore=true  所以第一次的结果只需要管  用不用给他置为false就可以了
        if (isfromEnd){
            //上拉在进行了 就不会继续再进行了
            Log.e("jxf","上拉请求网络加载数据，并且关闭开关isLoadMore");
            isLoadMore=false;
            //尝试放开 mode 看疗效吧  不行的 这这更改了模式 执行完的 complete 可能连着一起都会关闭的

        }
        if (isFirst){
            Log.e("jxf", "home页loadata:isfirst");
            fragment_newcontent_loading_iv.setAnimation(rotateAnimation);
            fragment_newcontent_loading_iv.setVisibility(View.VISIBLE);
        }
        ConnectivityManager mConnectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info == null || !mConnectivity.getBackgroundDataSetting()) {
            Log.e("jxf","home页loadata没网");
            new AsyncTask<Void,Void,Void>(){

                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Log.e("jxf","AsyncTask睡着了，发生了异常"+e.toString());
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
            Log.e("jxf", "home页loadata有网");
            netRequestAndResponse();
        }
    }

    private void netRequestAndResponse() {
        RequestParams params = new RequestParams();
        params.put("userId", userId);
        params.put("maxId", maxId);
        params.put("minId", minId);
        params.put("pageSize", pageSize);
        Log.e("jxf", "打印请求参数" +userId+"====="+ maxId + "====" + minId + "====" + pageSize);
        NetClient.headGet(context, Url.NEW_CONTENT, params, new NetResponseHandler() {
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.e("jxf","newcontent页loadata:onfail错误码："+throwable.toString());
                netOff();
            }

            @Override
            public void onResponse(String json) {
                try {
                    JSONObject jSONObjectAll = new JSONObject(json);
                    Log.e("jxf"," newcontent页请求得到响应的json："+json);
                    String status=jSONObjectAll.optString("status");
                    if (status.equals("1")){
                        JSONArray jSONArray=jSONObjectAll.optJSONArray("datas");
                        //state=1有数据
                        if(jSONArray!=null){
                            fragment_newcontent_none.setVisibility(View.GONE);
                            ArrayList<NewContentMusic> temp=new ArrayList<NewContentMusic>();
                            int length=jSONArray.length();
                            for (int i = 0; i < length; i++){
                                JSONObject obj = jSONArray.optJSONObject(i);
                                Log.e("jxf","newcontent的一条数据："+obj.toString());
                                NewContentMusic newContentMusic=new NewContentMusic();
                                newContentMusic.id=obj.optInt("id");
                                newContentMusic.title=obj.optString("title");
                                newContentMusic.categoryName=obj.optString("categoryName");
                                newContentMusic.pictureUrlSmall=obj.optString("pictureUrlSmall");
                                newContentMusic.pictureUrlMiddle=obj.optString("pictureUrlMiddle");
                                newContentMusic.pictureUrlBig=obj.optString("pictureUrlBig");
                                newContentMusic.url=obj.optString("url");
                                newContentMusic.isCost=obj.optString("isCost");
                                newContentMusic.type=obj.optInt("type");
                                newContentMusic.sort=obj.optInt("sort");
                                newContentMusic.createTime=obj.optLong("createTime");
                                newContentMusic.subsCount=obj.optInt("subsCount");
                                newContentMusic.score=obj.optDouble("score");
                                newContentMusic.teacherName=obj.optString("teacherName");
                                newContentMusic.userPortrait=obj.optString("userPortrait");
                                temp.add(newContentMusic);
                            }
                            if (isFirst){
                                newContentMusics.addAll(temp);
                                adapter.notifyDataSetChanged();
                                if (temp.size()<pageSize){
                                    Log.e("jxf", "home页loadata已经是最后一条数据");
//                                    fragment_home_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                    fragment_newcontent_lv.addFooterView(footer);
                                    //不让上拉
                                    isLoadMore = false;
                                }
                                isFirst=false;
                                fragment_newcontent_loading_iv.clearAnimation();
                                fragment_newcontent_loading_iv.setVisibility(View.GONE);
                            }
                            if(isfromStart){
                                if (isNull){
                                    isLoadMore = true;
                                    newContentMusics.addAll(temp);
                                    adapter.notifyDataSetChanged();
                                    fragment_newcontent_ptr_lv.onRefreshComplete();
                                    if (temp.size()<pageSize){
                                        Log.e("jxf", "home页loadata已经是最后一条数据");
                                        fragment_newcontent_lv.addFooterView(footer);
                                        isLoadMore = false;
                                    }
                                    isfromStart=false;
                                    isNull=false;
                                }else{
                                    newContentMusics.addAll(0, temp);
                                    adapter.notifyDataSetChanged();
                                    fragment_newcontent_ptr_lv.onRefreshComplete();
                                    isfromStart=false;
                                }
                            }
                            if (isfromEnd){
                                Log.e("jxf", "完成上拉刷新：关闭刷新");
                                fragment_newcontent_ptr_lv.onRefreshComplete();
                                isLoadMore = true;
                                newContentMusics.addAll(temp);
                                if (temp.size()<pageSize){
                                    Log.e("jxf", "home页loadata已经是最后一条数据");
                                    fragment_newcontent_lv.addFooterView(footer);
                                    isLoadMore = false;
                                }
                                adapter.notifyDataSetChanged();
                                Log.e("jxf", "完成刷新：更改设置改为下拉mode");
                                isfromEnd=false;
                                fragment_newcontent_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            }
                        }
                        //state=1没有数据
                        else{
                            if (isFirst){
                                fragment_newcontent_none.setVisibility(View.VISIBLE);
                                isFirst=false;
                                fragment_newcontent_loading_iv.clearAnimation();
                                fragment_newcontent_loading_iv.setVisibility(View.GONE);
                                isNull=true;
                                isLoadMore = false;
                            }
                            if (isfromStart){
                                if (isNull){
                                    fragment_newcontent_ptr_lv.onRefreshComplete();
                                    isfromStart=false;
                                }else {
                                    fragment_newcontent_ptr_lv.onRefreshComplete();
                                    isfromStart=false;
                                }
                            }
                            if (isfromEnd){
                                Log.e("jxf", "home页loadata上拉没有数据了");
                                Log.e("jxf", "完成刷新：关闭刷新");
                                fragment_newcontent_ptr_lv.onRefreshComplete();
                                Log.e("jxf", "完成刷新：更改设置改为下拉mode");
                                fragment_newcontent_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                fragment_newcontent_lv.addFooterView(footer);
                                fragment_newcontent_lv.setSelection(adapter.getCount() - 1);
                                isfromEnd=false;
                            }
                        }
                    }
                    //有错误码:state不等于1
                    else{
                        String errorCode=jSONObjectAll.optString("errorCode");
                        //错误码提示没有数据
                        if (errorCode.equals("2")){
                            if (isFirst){
                                fragment_newcontent_none.setVisibility(View.VISIBLE);
                                isFirst=false;
                                fragment_newcontent_loading_iv.clearAnimation();
                                fragment_newcontent_loading_iv.setVisibility(View.GONE);
                                isNull=true;
                                isLoadMore = false;
                            }
                            if (isfromStart){
                                if (isNull){
                                    fragment_newcontent_ptr_lv.onRefreshComplete();
                                    isfromStart=false;
                                }else {
                                    fragment_newcontent_ptr_lv.onRefreshComplete();
                                    isfromStart=false;
                                }
                            }
                            if (isfromEnd){
                                Log.e("jxf", "完成刷新：关闭刷新");
                                fragment_newcontent_ptr_lv.onRefreshComplete();
                                Log.e("jxf", "完成刷新：更改设置改为下拉mode");
                                fragment_newcontent_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                fragment_newcontent_lv.addFooterView(footer);
                                // TODO: 2016/3/2 要调节数
                                fragment_newcontent_lv.setSelection(adapter.getCount() - 1);
                                isfromEnd=false;
                            }
                        }
                        else if (errorCode.equals("90001")){
                            DefinedSingleToast.showToast(context, getResources().getString(R.string.system_exception));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        }

    private void netOff() {
        Log.e("jxf","newcontent页loadata:请求失败");
        if (isFirst){
            fragment_newcontent_loading_iv.clearAnimation();
            fragment_newcontent_loading_iv.setVisibility(View.GONE);
            fragment_newcontent_net_message.setVisibility(View.VISIBLE);
            fragment_newcontent_net_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragment_newcontent_net_message.setVisibility(View.GONE);
                    maxId = 0;
                    minId = 0;
                    isFirst = true;
                    loadDatas();
                }
            });
        }
        if (isfromStart){
            if (isNull){
                DefinedSingleToast.showToast(context,getResources().getString(R.string.network_no_force));
                fragment_newcontent_ptr_lv.onRefreshComplete();
                isfromStart=false;
            }else {
                DefinedSingleToast.showToast(context,getResources().getString(R.string.network_no_force));
                fragment_newcontent_ptr_lv.onRefreshComplete();
                isfromStart=false;
            }
        }
        if (isfromEnd){
            Log.e("jxf", "完成刷新：关闭刷新");
            fragment_newcontent_ptr_lv.onRefreshComplete();
            Log.e("jxf", "完成刷新：更改设置改为下拉mode");
            fragment_newcontent_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            isLoadMore=true;
            DefinedSingleToast.showToast(context, getResources().getString(R.string.network_no_force));
            isfromEnd=false;
        }
    }

    @Override
    protected int setLayout() {
        return R.layout.newcontent_layout_fragment;
    }

    @Override
    protected void onClickEvent(View view) {

    }


    Comparator unordercomp = new Comparator() {
        public int compare(Object o1, Object o2) {
            NewContentMusic p1 = (NewContentMusic) o1;
            NewContentMusic p2 = (NewContentMusic) o2;
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
