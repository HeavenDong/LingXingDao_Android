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
import com.miracleworld.lingxingdao.android.activity.HomeDetailActivity;
import com.miracleworld.lingxingdao.android.adapter.HomeAdapter;
import com.miracleworld.lingxingdao.android.base.BaseFragment;
import com.miracleworld.lingxingdao.android.bean.HomeNewest;
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
 * Created by donghaifeng on 2015/12/16.
 */
public class HomeFragment extends BaseFragment {
    //需要全局的控件
    private PullToRefreshListView fragment_home_ptr_lv;
    private ListView fragment_home_lv;
    private ImageView fragment_home_loading_iv;
    private View footer;
    private HomeAdapter adapter;
    private RelativeLayout fragment_home_net_message;
    private RotateAnimation rotateAnimation;
    private ImageView fragment_home_none;
    //请求网络的数据
    private int userId;
    private int maxId;
    private int minId;
    private int pageSize=20;
    private ArrayList<HomeNewest> homeNewests;
    //要进行判断的值
    private boolean isFirst;
    private boolean isfromStart;
    private boolean isfromEnd;
    private boolean isNull;
    //修改3：添加禁止上拉加载的boolean
    private boolean isLoadMore;
    //数据库需要
    private boolean cursorIsNull;
    private String tableName="home";

    @Override
    protected void initView(View view, Bundle bundle) {
        Log.e("jxf", "进入home");
        userId=(int)SharedPreUtils.get(context,"user_id",0);
        maxId=0;
        minId=0;
        homeNewests=new ArrayList<HomeNewest>();
        isFirst=true;
        isNull=false;
        isfromEnd=false;
        //一进来允许上拉加载
        isLoadMore=true;
        fragment_home_none= (ImageView) view.findViewById(R.id.fragment_home_none);
        fragment_home_none.setVisibility(View.GONE);
        fragment_home_net_message= (RelativeLayout) view.findViewById(R.id.fragment_home_net_message);
        fragment_home_net_message.setVisibility(View.GONE);
        fragment_home_loading_iv= (ImageView) view.findViewById(R.id.fragment_home_loading_iv);
        rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(context, R.anim.rotate_refresh_drawable_default);
        footer=View.inflate(context, R.layout.list_footer,null);
        fragment_home_ptr_lv= (PullToRefreshListView) view.findViewById(R.id.fragment_home_ptr_lv);
        //修改1：初始化：禁止上拉
        fragment_home_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        //滑动才会触发  而且是上拉的时候才会触发  下拉的时候触发了onrefreshlistener
        fragment_home_ptr_lv.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if (isLoadMore){
                    Log.e("jxf","触发上拉加载");
                    Log.e("jxf", "设置上拉记载更多");
                    fragment_home_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                    Log.e("jxf", "让现实数显");
                    fragment_home_ptr_lv.setRefreshing();
                    //这个是为了标记的boolean  改执行loaddata中那个方法的boolean
                    isfromEnd=true;
                    ArrayList<HomeNewest> temp=new ArrayList<HomeNewest>();
                    temp.addAll(homeNewests);
                    Collections.sort(temp, unordercomp);
                    minId=temp.get(temp.size()-1).id;
                    Log.e("jxf","home页的上拉加载的minId："+minId);
                    maxId=0;
                    loadDatas();
                }
            }
        });
        fragment_home_lv=fragment_home_ptr_lv.getRefreshableView();
        fragment_home_lv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        Cursor cursor=App.getSqlManager().seleteAll(tableName,new String[]{"id1","id","title","icon","creat_time","url","update_time"},"id1 desc");
        if (cursor!=null&&cursor.getCount()>0){
            cursorIsNull=false;
            Log.e("jxf","home数据库有数据");
            while(cursor.moveToNext()) {
                HomeNewest homeNewest=new HomeNewest();
                homeNewest.id1=cursor.getInt(0);
                homeNewest.id= cursor.getInt(1);
                homeNewest.title= cursor.getString(2);
                homeNewest.icon= cursor.getString(3);
                homeNewest.creatTime=cursor.getLong(4);
                homeNewest.url=cursor.getString(5);
                homeNewest.updateTime=cursor.getLong(6);
                homeNewest.isCheck=false;
                homeNewests.add(homeNewest);
            }
            cursor.close();
            App.getSqlManager().close();
            if (homeNewests.size()>20){
                int  id1=homeNewests.get(19).id1;
                App.getSqlManager().deleteOne(tableName,"id<?",new String[]{""+id1});
                App.getSqlManager().close();
                for (int i=20;i<homeNewests.size();i=20){
                    homeNewests.remove(i);
                }
                Log.e("jxf","home页从数据库取数据：有数据：集合数量"+homeNewests.size());
            }
        }
        else{
            cursorIsNull=true;
            Log.e("jxf","home数据库没有数据");
            cursor.close();
            App.getSqlManager().close();
        }
        adapter=new HomeAdapter(context, homeNewests);
        fragment_home_lv.setAdapter(adapter);
        loadDatas();
        fragment_home_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position>homeNewests.size()){
                    return;
                }else{
                    Log.e("jxf", "home页列表点击位置id：" + homeNewests.get(position - 1).id + "和position：" + position);
                    HomeNewest newest=homeNewests.get(position-1);
                    newest.isCheck=true;
                    adapter.notifyDataSetChanged();
                    Intent intent = new Intent(context, HomeDetailActivity.class);
                    intent.putExtra("url",newest.url);
                    Log.e("jxf","home页列表点击位置url"+newest.url);
                    context.startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            }
        });
        fragment_home_ptr_lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                Log.e("jxf", "home页的下拉刷新");
                isfromStart=true;
                if (isNull){
                    maxId=0;
                }
                else{
                    ArrayList<HomeNewest> temp=new ArrayList<HomeNewest>();
                    temp.addAll(homeNewests);
                    Collections.sort(temp, unordercomp);
                    maxId=temp.get(0).id;
                }
                Log.e("jxf","home页的下拉刷新的maxId："+maxId);
                minId=0;
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
        if (isfromEnd){
            //上拉在进行了 就不会继续再进行了
            Log.e("jxf","上拉请求网络加载数据，并且关闭开关isLoadMore");
            isLoadMore=false;
            //尝试放开 mode 看疗效吧  不行的 这这更改了模式 执行完的 complete 可能连着一起都会关闭的

        }
        if (isFirst){
            Log.e("jxf", "home页loadata:isfirst");
            fragment_home_loading_iv.setAnimation(rotateAnimation);
            fragment_home_loading_iv.setVisibility(View.VISIBLE);
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
    private void netRequestAndResponse(){
        RequestParams params = new RequestParams();
        params.put("userId", userId);
        params.put("maxId", maxId);
        params.put("minId", minId);
        params.put("pageSize", pageSize);
        Log.e("jxf", "打印请求参数" +userId+"====="+ maxId + "====" + minId + "====" + pageSize);
        NetClient.headGet(context, Url.HOME, params, new NetResponseHandler() {
            @Override
            public void onResponse(String json) {
                try {
                    JSONObject jSONObjectAll=new JSONObject(json);
                    Log.e("jxf","home页请求得到响应的json："+json);
                    String status=jSONObjectAll.optString("status");
                    if (status.equals("1")){
                        JSONArray jSONArray=jSONObjectAll.optJSONArray("datas");
                        if(jSONArray!=null){
                            fragment_home_none.setVisibility(View.GONE);
                            ArrayList<HomeNewest> temp=new ArrayList<HomeNewest>();
                            int length=jSONArray.length();
                            for (int i = 0; i < length; i++){
                                JSONObject obj = jSONArray.optJSONObject(i);
                                Log.e("jxf","home的一条数据："+obj.toString());
                                HomeNewest homeNewest=new HomeNewest();
                                homeNewest.id=obj.optInt("id");
                                homeNewest.icon=obj.optString("icon");
                                homeNewest.title=obj.optString("title");
                                homeNewest.sort=obj.optInt("sort");
                                homeNewest.creatTime=obj.optLong("creatTime");
                                homeNewest.url=obj.optString("url");
                                homeNewest.updateTime=obj.optLong("updateTime");
                                homeNewest.isCheck=false;
                                temp.add(homeNewest);
                            }
                            if(isfromStart){
                                if (isNull){
                                    isLoadMore = true;
                                    homeNewests.addAll(temp);
                                    Collections.reverse(temp);
                                    pullSaveMore(temp);
                                    adapter.notifyDataSetChanged();
                                    fragment_home_ptr_lv.onRefreshComplete();
//                                    fragment_home_ptr_lv.setMode(PullToRefreshBase.Mode.BOTH);
                                    if (temp.size()<pageSize){
                                        Log.e("jxf", "home页loadata已经是最后一条数据");
//                                        fragment_home_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                        fragment_home_lv.addFooterView(footer);
                                        isLoadMore = false;
                                    }
                                    isfromStart=false;
                                    isNull=false;
                                }else{
                                    homeNewests.addAll(0, temp);
                                    Collections.reverse(temp);
                                    pullSaveMore(temp);
                                    adapter.notifyDataSetChanged();
                                    fragment_home_ptr_lv.onRefreshComplete();
                                    isfromStart=false;
                                }
                            }
                            if (isfromEnd){
                                Log.e("jxf", "完成上拉刷新：关闭刷新");
                                fragment_home_ptr_lv.onRefreshComplete();
                                isLoadMore = true;
                                homeNewests.addAll(temp);
//                                fragment_home_ptr_lv.onRefreshComplete();
                                if (temp.size()<pageSize){
                                    Log.e("jxf", "home页loadata已经是最后一条数据");
                                    fragment_home_lv.addFooterView(footer);
                                    isLoadMore = false;
                                }
                                adapter.notifyDataSetChanged();
                                Log.e("jxf", "完成刷新：更改设置改为下拉mode");
                                isfromEnd=false;
                                fragment_home_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            }
                            if (isFirst){
                                if (!cursorIsNull){
                                    homeNewests.clear();
                                }
                                homeNewests.addAll(temp);
                                adapter.notifyDataSetChanged();
                                Collections.reverse(temp);
                                saveSQL(temp);
                                if (temp.size()<pageSize){
                                    Log.e("jxf", "home页loadata已经是最后一条数据");
//                                    fragment_home_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                    fragment_home_lv.addFooterView(footer);
                                    isLoadMore = false;
                                }
                                isFirst=false;
                                fragment_home_loading_iv.clearAnimation();
                                fragment_home_loading_iv.setVisibility(View.GONE);
                            }
                        }
                        else{
                            Log.e("jxf", "home页loadata：没有数据了");
                            if (isFirst){
                                empty();
                                if (!cursorIsNull){
                                    homeNewests.clear();
                                    adapter.notifyDataSetChanged();
                                }
                                fragment_home_none.setVisibility(View.VISIBLE);
                                isFirst=false;
                                fragment_home_loading_iv.clearAnimation();
                                fragment_home_loading_iv.setVisibility(View.GONE);
                                isNull=true;
//                                fragment_home_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                isLoadMore = false;
                            }
                            if (isfromStart){
                                if (isNull){
                                    fragment_home_ptr_lv.onRefreshComplete();
                                    isfromStart=false;
                                }else {
                                    fragment_home_ptr_lv.onRefreshComplete();
                                    isfromStart=false;
                                }
                            }
                            if (isfromEnd){
                                Log.e("jxf", "home页loadata上拉没有数据了");
                                Log.e("jxf", "完成刷新：关闭刷新");
                                fragment_home_ptr_lv.onRefreshComplete();
                                Log.e("jxf", "完成刷新：更改设置改为下拉mode");
                                fragment_home_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                fragment_home_lv.addFooterView(footer);
                                fragment_home_lv.setSelection(adapter.getCount() - 1);
                                isfromEnd=false;
                            }
                        }
                    }
                    else{
                        String errorCode=jSONObjectAll.optString("errorCode");
                        if (errorCode.equals("2")){
                            if (isFirst){
                                empty();
                                if (!cursorIsNull){
                                    homeNewests.clear();
                                    adapter.notifyDataSetChanged();
                                }
                                fragment_home_none.setVisibility(View.VISIBLE);
                                isFirst=false;
                                fragment_home_loading_iv.clearAnimation();
                                fragment_home_loading_iv.setVisibility(View.GONE);
                                isNull=true;
//                                fragment_home_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                isLoadMore = false;
                            }
                            if (isfromStart){
                                if (isNull){
                                    fragment_home_ptr_lv.onRefreshComplete();
                                    isfromStart=false;
                                }else {
                                    fragment_home_ptr_lv.onRefreshComplete();
                                    isfromStart=false;
                                }
                            }
                            if (isfromEnd){
//                                fragment_home_ptr_lv.onRefreshComplete();
                                Log.e("jxf", "完成刷新：关闭刷新");
                                fragment_home_ptr_lv.onRefreshComplete();
                                Log.e("jxf", "完成刷新：更改设置改为下拉mode");
                                fragment_home_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                fragment_home_lv.addFooterView(footer);
                                // TODO: 2016/3/2 要调节数
                                fragment_home_lv.setSelection(adapter.getCount() - 1);
//                                fragment_home_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                isfromEnd=false;
                            }
                        }else if (errorCode.equals("90001")){
                            DefinedSingleToast.showToast(context,getResources().getString(R.string.system_exception));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("jxf", "home页loadata:onresponse出现异常："+e.toString());
                }
            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.e("jxf","home页loadata:onfail错误码："+throwable.toString());
                netOff();
            }
        });
    }
    private void netOff(){
        Log.e("jxf","home页loadata:请求失败");
        if (isFirst){
            fragment_home_loading_iv.clearAnimation();
            fragment_home_loading_iv.setVisibility(View.GONE);
            if (cursorIsNull){
                fragment_home_net_message.setVisibility(View.VISIBLE);
                fragment_home_net_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fragment_home_net_message.setVisibility(View.GONE);
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
        if (isfromStart){
            if (isNull){
                DefinedSingleToast.showToast(context,getResources().getString(R.string.network_no_force));
                fragment_home_ptr_lv.onRefreshComplete();
                isfromStart=false;
            }else {
                DefinedSingleToast.showToast(context,getResources().getString(R.string.network_no_force));
                fragment_home_ptr_lv.onRefreshComplete();
                isfromStart=false;
            }
        }
        if (isfromEnd){
            Log.e("jxf", "完成刷新：关闭刷新");
            fragment_home_ptr_lv.onRefreshComplete();
            Log.e("jxf", "完成刷新：更改设置改为下拉mode");
            fragment_home_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            isLoadMore=true;
            DefinedSingleToast.showToast(context, getResources().getString(R.string.network_no_force));
//            fragment_home_ptr_lv.onRefreshComplete();
            isfromEnd=false;
        }
    }

    private void saveSQL(final ArrayList<HomeNewest> temp){
        new Thread(new Runnable() {
            @Override
            public void run() {
                App.getSqlManager().deleteAll(tableName);
                Log.e("jxf", "home页第一次数据库：临时要存储集合的数量" + temp.size());
                for (int i=0;i<temp.size();i++){
                    ContentValues values=new ContentValues();
                    values.put("id",temp.get(i).id);
                    values.put("title",temp.get(i).title);
                    values.put("icon",temp.get(i).icon);
                    values.put("creat_time",temp.get(i).creatTime);
                    values.put("url",temp.get(i).url);
                    values.put("update_time",temp.get(i).updateTime);
                    App.getSqlManager().insert(tableName,values);
                }
                Log.e("jxf","home页：子线程第一次提交数据库完毕！");
            }
        }).start();
    }
    private void pullSaveMore( final ArrayList<HomeNewest> temp){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("jxf","home页数据库：下拉临时要存储集合的数量"+temp.size());
                for (int i=0;i<temp.size();i++){
                    ContentValues values=new ContentValues();
                    values.put("id",temp.get(i).id);
                    values.put("title",temp.get(i).title);
                    values.put("icon",temp.get(i).icon);
                    values.put("creat_time",temp.get(i).creatTime);
                    values.put("url",temp.get(i).url);
                    values.put("update_time",temp.get(i).updateTime);
                    App.getSqlManager().insert(tableName, values);
                    Log.e("jxf","home页：数据库下拉保存的次数"+i);
                }
                Log.e("jxf","home页：子线程下拉提交数据库完毕！");
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
        return R.layout.home_layout_fragment;
    }

    @Override
    protected void onClickEvent(View view) {

    }
    Comparator unordercomp = new Comparator() {
        public int compare(Object o1, Object o2) {
            HomeNewest p1 = (HomeNewest) o1;
            HomeNewest p2 = (HomeNewest) o2;
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
