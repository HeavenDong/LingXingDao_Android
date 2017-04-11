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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.RequestParams;
import com.miracleworld.lingxingdao.android.App;
import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.activity.ScheduleDetailActivity;
import com.miracleworld.lingxingdao.android.adapter.ScheduleAdapter;
import com.miracleworld.lingxingdao.android.base.BaseFragment;
import com.miracleworld.lingxingdao.android.bean.Schedule;
import com.miracleworld.lingxingdao.android.http.NetClient;
import com.miracleworld.lingxingdao.android.http.NetResponseHandler;
import com.miracleworld.lingxingdao.android.http.Url;
import com.miracleworld.lingxingdao.android.view.DefinedSingleToast;

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
public class ScheduleFragment extends BaseFragment {
    //需要全局的控件
    private PullToRefreshListView fragment_schedule_ptr_lv;
    private ListView fragment_schedule_lv;
    private ImageView fragment_schedule_loading_iv;
    private View footer;
    private ScheduleAdapter adapter;
    private RelativeLayout fragment_schedule_net_message;
    private RotateAnimation rotateAnimation;
    private ImageView fragment_schedule_none;
    //请求网络的数据
    private int maxId;
    private int minId;
    private int pageSize=20;
    private ArrayList<Schedule> schedules;
    //要进行判断的值
    private boolean isFirst;
    private boolean isfromStart;
    private boolean isfromEnd;
    private boolean isNull;
    //修改3：添加禁止上拉加载的boolean
    private boolean isLoadMore;
    //数据库需要
    private boolean cursorIsNull;
    private String tableName="schedule";
    @Override
    protected void initView(View view, Bundle bundle) {
        Log.e("jxf", "进入schedule");
        maxId=0;
        minId=0;
        schedules=new ArrayList<Schedule>();
        isFirst=true;
        isNull=false;
        //给个初始化 防止走loaddata中他的方法
        isfromEnd=false;
        //一进来允许上拉加载
        isLoadMore=true;
        fragment_schedule_none= (ImageView) view.findViewById(R.id.fragment_schedule_none);
        fragment_schedule_none.setVisibility(View.GONE);
        fragment_schedule_net_message= (RelativeLayout) view.findViewById(R.id.fragment_schedule_net_message);
        fragment_schedule_net_message.setVisibility(View.GONE);
        fragment_schedule_loading_iv= (ImageView) view.findViewById(R.id.fragment_schedule_loading_iv);
        rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(context, R.anim.rotate_refresh_drawable_default);
        footer=View.inflate(context, R.layout.list_footer, null);
        fragment_schedule_ptr_lv= (PullToRefreshListView) view.findViewById(R.id.fragment_schedule_ptr_lv);
        //修改1：初始化：禁止上拉
        fragment_schedule_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        //滑动才会触发  而且是上拉的时候才会触发  下拉的时候触发了onrefreshlistener
        fragment_schedule_ptr_lv.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if (isLoadMore){
                    Log.e("jxf","触发上拉加载");
                    Log.e("jxf", "设置上拉记载更多");
                    fragment_schedule_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                    Log.e("jxf","让现实数显");
                    fragment_schedule_ptr_lv.setRefreshing();
                    //这个是为了标记的boolean  改执行loaddata中那个方法的boolean
                    isfromEnd=true;
                    ArrayList<Schedule> temp=new ArrayList<Schedule>();
                    temp.addAll(schedules);
                    Collections.sort(temp, unordercomp);
                    minId=temp.get(temp.size()-1).id;
                    Log.e("jxf","schedule页的上拉刷新的minId："+minId);
                    maxId=0;
                    loadDatas();
                }
            }
        });
        fragment_schedule_lv=fragment_schedule_ptr_lv.getRefreshableView();
        fragment_schedule_lv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        fragment_schedule_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position>schedules.size()){
                    return;
                }
                else{
                    Log.e("jxf", "home页列表点击位置id：" + schedules.get(position - 1).id + "和position：" + position);
                    Schedule schedule=schedules.get(position - 1);
                    Intent intent = new Intent(context, ScheduleDetailActivity.class);
                    Bundle bundle=new Bundle();
                    bundle.putInt("scheduleId", schedule.id);
                    bundle.putString("portraitUrlSmall", schedule.portraitUrlSmall);
                    bundle.putString("title", schedule.title);
                    bundle.putString("teacherName", schedule.teacherName);
                    bundle.putString("provinceName",schedule.provinceName);
                    bundle.putString("cityName",schedule.cityName);
                    bundle.putString("address", schedule.address);
                    bundle.putLong("startTime", schedule.startTime);
                    bundle.putLong("endTime", schedule.endTime);
                    bundle.putDouble("price", schedule.price);
                    bundle.putDouble("againPrice", schedule.againPrice);
                    bundle.putDouble("sitInPrice", schedule.sitInPrice);
                    bundle.putDouble("askPrice", schedule.askPrice);
                    bundle.putString("priceType", schedule.priceType);
                    bundle.putString("url", schedule.url);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            }
        });

        Cursor cursor= App.getSqlManager().seleteAll(tableName,new String[]{"id1","id","title","teacher_name","teacher_img_small","teacher_img_big","start_time","end_time","province_name","city_name","address","price","again_price","sitin_price","ask_price","url","color","price_type"},"id1 desc");
        if (cursor!=null&&cursor.getCount()>0){
            cursorIsNull=false;
            Log.e("jxf", "schedule数据库有数据");
            while(cursor.moveToNext()) {
                Schedule schedule=new Schedule();
                schedule.id1=cursor.getInt(0);
                schedule.id= cursor.getInt(1);
                schedule.title= cursor.getString(2);
                schedule.teacherName= cursor.getString(3);
                schedule.portraitUrlSmall=cursor.getString(4);
                schedule.portraitUrlBig=cursor.getString(5);
                schedule.startTime=cursor.getLong(6);
                schedule.endTime=cursor.getLong(7);
                schedule.provinceName=cursor.getString(8);
                schedule.cityName=cursor.getString(9);
                schedule.address=cursor.getString(10);
                schedule.price=cursor.getDouble(11);
                schedule.againPrice=cursor.getDouble(12);
                schedule.sitInPrice=cursor.getDouble(13);
                schedule.askPrice=cursor.getDouble(14);
                schedule.url=cursor.getString(15);
                schedule.color=cursor.getString(16);
                schedule.priceType=cursor.getString(17);
                schedules.add(schedule);
            }
            cursor.close();
            if (schedules.size()>20){
                int id1=schedules.get(19).id1;
                App.getSqlManager().deleteOne(tableName,"id1<?",new String[]{""+id1});
                for (int i=20;i<schedules.size();i=20){
                    schedules.remove(i);
                }
                Log.e("jxf","schedule页从数据库取数据：有数据：集合数量"+schedules.size());
            }
            Log.e("jxf","打印排完序的集合"+schedules.get(0).id1);
        }
        else{
            cursorIsNull=true;
            Log.e("jxf","schedule数据库没有数据");
            cursor.close();
        }
        adapter=new ScheduleAdapter(context,schedules,getActivity());
        fragment_schedule_lv.setAdapter(adapter);
        loadDatas();
        fragment_schedule_ptr_lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                Log.e("jxf", "schedule页下拉刷新");
                isfromStart=true;
                if (isNull){
                    maxId=0;
                }
                else{
                    ArrayList<Schedule> temp=new ArrayList<Schedule>();
                    temp.addAll(schedules);
                    Collections.sort(temp, unordercomp);
                    maxId=temp.get(0).id;
                }
                Log.e("jxf","schedule页的下拉刷新的maxId："+maxId);
                minId=0;
                loadDatas();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                Log.e("jxf", "schedule页onPullUpToRefresh上拉加载");
//                isfromEnd=true;
//                ArrayList<Schedule> temp=new ArrayList<Schedule>();
//                temp.addAll(schedules);
//                Collections.sort(temp, unordercomp);
//                minId=temp.get(temp.size()-1).id;
//                Log.e("jxf","schedule页的下拉刷新的minId："+minId);
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
            Log.e("jxf", "schedule页loadata:isfirst");
            fragment_schedule_loading_iv.setAnimation(rotateAnimation);
            fragment_schedule_loading_iv.setVisibility(View.VISIBLE);
        }
        ConnectivityManager mConnectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info == null || !mConnectivity.getBackgroundDataSetting()) {
            Log.e("jxf","schedule页loadata:没网");
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
            Log.e("jxf", "schedule页loadata:有网");
            netRequestAndResponse();
        }
    }


    private void netRequestAndResponse(){
        RequestParams params = new RequestParams();
        params.put("maxId", maxId);
        params.put("minId", minId);
        params.put("pageSize", pageSize);
        Log.e("jxf", "打印请求参数" + maxId + "====" + minId+"===="+pageSize );
        NetClient.headGet(context, Url.SCHEDULE, params, new NetResponseHandler() {
            @Override
            public void onResponse(String json) {
                try {
                    JSONObject jSONObjectAll = new JSONObject(json);
                    Log.e("jxf", "schedule页请求得到响应的json：" + json);
                    String status = jSONObjectAll.optString("status");
                    if (status.equals("1")) {
                        JSONArray jSONArray = jSONObjectAll.optJSONArray("datas");
                        if (jSONArray != null) {
                            fragment_schedule_none.setVisibility(View.GONE);
                            ArrayList<Schedule> temp = new ArrayList<Schedule>();
                            int length = jSONArray.length();
                            for (int i = 0; i < length; i++) {
                                JSONObject obj = jSONArray.optJSONObject(i);
                                Log.e("jxf", "schedule的一条数据" + obj.toString());
                                Schedule schedule = new Schedule();
                                schedule.id = obj.optInt("id");
                                schedule.title = obj.optString("title");
                                schedule.teacherName = obj.optString("teacherName");
                                schedule.portraitUrlSmall = obj.optString("portraitUrlSmall");
                                schedule.portraitUrlBig = obj.optString("portraitUrlBig");
                                schedule.startTime = obj.optLong("startTime");
                                schedule.endTime = obj.optLong("endTime");
                                schedule.provinceName = obj.optString("provinceName");
                                schedule.cityName = obj.optString("cityName");
                                schedule.address = obj.optString("address");
                                schedule.price = obj.getDouble("price");
                                schedule.againPrice = obj.getDouble("againPrice");
                                schedule.sitInPrice = obj.getDouble("sitInPrice");
                                schedule.askPrice = obj.getDouble("askPrice");
                                schedule.url = obj.optString("detail");
                                schedule.color = obj.optString("color");
                                schedule.priceType = obj.optString("priceType");
                                temp.add(schedule);
                            }
                            if (isfromStart) {
                                if (isNull) {
                                    isLoadMore = true;
                                    schedules.addAll(temp);
                                    Collections.reverse(temp);
                                    pullSaveMore(temp);
                                    adapter.notifyDataSetChanged();
                                    fragment_schedule_ptr_lv.onRefreshComplete();
//                                    fragment_schedule_ptr_lv.setMode(PullToRefreshBase.Mode.BOTH);
                                    if (temp.size() < pageSize) {
                                        Log.e("jxf", "schedule页loadata:已经是最后一条数据");
//                                        fragment_schedule_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                        fragment_schedule_lv.addFooterView(footer);
                                        isLoadMore = false;
                                    }
                                    isfromStart = false;
                                    isNull = false;
                                } else {
                                    schedules.addAll(0, temp);
                                    Collections.reverse(temp);
                                    pullSaveMore(temp);
                                    adapter.notifyDataSetChanged();
                                    fragment_schedule_ptr_lv.onRefreshComplete();
                                    isfromStart = false;
                                }
                            }
                            if (isfromEnd) {
                                Log.e("jxf", "完成刷新：关闭刷新");
                                fragment_schedule_ptr_lv.onRefreshComplete();
                                isLoadMore = true;
                                schedules.addAll(temp);
//                                fragment_schedule_ptr_lv.onRefreshComplete();
                                if (temp.size() < pageSize) {
                                    Log.e("jxf", "schedule页loadata:已经是最后一条数据");
                                    fragment_schedule_lv.addFooterView(footer);
                                    isLoadMore = false;
                                }
                                adapter.notifyDataSetChanged();
                                Log.e("jxf", "完成刷新：更改设置改为下拉mode");
                                isfromEnd = false;
                                fragment_schedule_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                            }
                            if (isFirst) {
                                //这里不用变：因为没有走
                                if (!cursorIsNull) {
                                    schedules.clear();
                                }
                                schedules.addAll(temp);
                                adapter.notifyDataSetChanged();
                                Collections.reverse(temp);
                                saveSQL(temp);
                                if (temp.size() < pageSize) {
                                    Log.e("jxf", "schedule页loadata:已经是最后一条数据");
//                                    fragment_schedule_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                    fragment_schedule_lv.addFooterView(footer);
                                    isLoadMore = false;
                                }
                                isFirst = false;
                                fragment_schedule_loading_iv.clearAnimation();
                                fragment_schedule_loading_iv.setVisibility(View.GONE);
                            }
                        } else {
                            Log.e("jxf", "schedule页loadata:没有数据了");
                            if (isFirst) {
                                empty();
                                if (!cursorIsNull) {
                                    schedules.clear();
                                    adapter.notifyDataSetChanged();
                                }
                                fragment_schedule_none.setVisibility(View.VISIBLE);
                                isFirst = false;
                                fragment_schedule_loading_iv.clearAnimation();
                                fragment_schedule_loading_iv.setVisibility(View.GONE);
                                isNull = true;
//                                fragment_schedule_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                isLoadMore = false;
                            }
                            if (isfromStart) {
                                if (isNull) {
                                    fragment_schedule_ptr_lv.onRefreshComplete();
                                    isfromStart = false;
                                } else {
                                    fragment_schedule_ptr_lv.onRefreshComplete();
                                    isfromStart = false;
                                }
                            }
                            if (isfromEnd) {
                                //不满足条件 置为false 但是默认加载的时候已经是 false
//                                isLoadMore=false;
                                Log.e("jxf", "schedule页loadata上拉没有数据了");
                                Log.e("jxf","完成刷新：关闭刷新");
                                fragment_schedule_ptr_lv.onRefreshComplete();
                                Log.e("jxf", "完成刷新：更改设置改为下拉mode");
                                fragment_schedule_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                fragment_schedule_lv.addFooterView(footer);
                                fragment_schedule_lv.setSelection(adapter.getCount() - 1);
                                isfromEnd = false;
                            }
                        }
                    } else {
                        String errorCode = jSONObjectAll.optString("errorCode");
                        if (errorCode.equals("2")) {
                            if (isFirst) {
                                empty();
                                if (!cursorIsNull) {
                                    schedules.clear();
                                    adapter.notifyDataSetChanged();
                                }
                                fragment_schedule_none.setVisibility(View.VISIBLE);
                                isFirst = false;
                                fragment_schedule_loading_iv.clearAnimation();
                                fragment_schedule_loading_iv.setVisibility(View.GONE);
                                isNull = true;
//                                fragment_schedule_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                isLoadMore = false;
                            }
                            if (isfromStart) {
                                if (isNull) {
//                                    fragment_schedule_none.setVisibility(View.VISIBLE);
                                    fragment_schedule_ptr_lv.onRefreshComplete();
                                    isfromStart = false;
                                } else {
                                    fragment_schedule_ptr_lv.onRefreshComplete();
                                    isfromStart = false;
                                }
                            }
                            if (isfromEnd) {
//                                fragment_schedule_ptr_lv.onRefreshComplete();
                                Log.e("jxf","完成刷新：关闭刷新");
                                fragment_schedule_ptr_lv.onRefreshComplete();
                                Log.e("jxf", "完成刷新：更改设置改为下拉mode");
                                fragment_schedule_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                fragment_schedule_lv.addFooterView(footer);
                                // TODO: 2016/3/2 要调节数
                                fragment_schedule_lv.setSelection(adapter.getCount() - 1);
//                                fragment_schedule_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                isfromEnd = false;
                            }
                        } else if (errorCode.equals("90001")) {
                            DefinedSingleToast.showToast(context, getResources().getString(R.string.system_exception));
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("jxf", "schedule页loadata:onresponse出现异常了" + e.toString());
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.e("jxf", "schedule页loadata:onfail错误码：" + throwable.toString());
                netOff();
            }
        });
    }
    private void netOff(){
        if (isFirst) {
            fragment_schedule_loading_iv.clearAnimation();
            fragment_schedule_loading_iv.setVisibility(View.GONE);
            if (cursorIsNull){
                fragment_schedule_net_message.setVisibility(View.VISIBLE);
                fragment_schedule_net_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fragment_schedule_net_message.setVisibility(View.GONE);
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
                DefinedSingleToast.showToast(context,getResources().getString(R.string.network_no_force));
                fragment_schedule_ptr_lv.onRefreshComplete();
                isfromStart = false;
            }
            else {
                DefinedSingleToast.showToast(context,getResources().getString(R.string.network_no_force));
                fragment_schedule_ptr_lv.onRefreshComplete();
                isfromStart = false;
            }
        }
        if (isfromEnd) {
            Log.e("jxf","完成刷新：关闭刷新");
            fragment_schedule_ptr_lv.onRefreshComplete();
            Log.e("jxf", "完成刷新：更改设置改为下拉mode");
            fragment_schedule_ptr_lv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            isLoadMore=true;
            DefinedSingleToast.showToast(context, getResources().getString(R.string.network_no_force));
//            fragment_schedule_ptr_lv.onRefreshComplete();
            isfromEnd = false;
        }
    }

    private void saveSQL(final ArrayList<Schedule> temp){
        new Thread(new Runnable() {
            @Override
            public void run() {
                App.getSqlManager().deleteAll(tableName);
                Log.e("jxf", "schedule页第一次数据库:临时要存储集合的数量" + temp.size());
                for (int i=0;i<temp.size();i++){
                    ContentValues values=new ContentValues();
                    values.put("id",temp.get(i).id);
                    values.put("title",temp.get(i).title);
                    values.put("teacher_name",temp.get(i).teacherName);
                    values.put("teacher_img_small",temp.get(i).portraitUrlSmall);
                    values.put("teacher_img_big",temp.get(i).portraitUrlBig);
                    values.put("start_time",temp.get(i).startTime);
                    values.put("end_time",temp.get(i).endTime);
                    values.put("province_name",temp.get(i).provinceName);
                    values.put("city_name",temp.get(i).cityName);
                    values.put("address",temp.get(i).address);
                    values.put("price",temp.get(i).price);
                    values.put("again_price",temp.get(i).againPrice);
                    values.put("sitin_price",temp.get(i).sitInPrice);
                    values.put("ask_price",temp.get(i).askPrice);
                    values.put("url",temp.get(i).url);
                    values.put("color",temp.get(i).color);
                    values.put("price_type",temp.get(i).priceType);
                    App.getSqlManager().insert(tableName,values);
                }
                Log.e("jxf","schedule页:子线程第一次提交数据库完毕！");
            }
        }).start();
    }
    private void pullSaveMore( final ArrayList<Schedule> temp){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("jxf","schedule页下拉:临时要存储集合的数量"+temp.size());
                for (int i=0;i<temp.size();i++){
                    ContentValues values=new ContentValues();
                    values.put("id",temp.get(i).id);
                    values.put("title",temp.get(i).title);
                    values.put("teacher_name",temp.get(i).teacherName);
                    values.put("teacher_img_small",temp.get(i).portraitUrlSmall);
                    values.put("teacher_img_big",temp.get(i).portraitUrlBig);
                    values.put("start_time",temp.get(i).startTime);
                    values.put("end_time",temp.get(i).endTime);
                    values.put("province_name",temp.get(i).provinceName);
                    values.put("city_name",temp.get(i).cityName);
                    values.put("address", temp.get(i).address);
                    values.put("price",temp.get(i).price);
                    values.put("again_price",temp.get(i).againPrice);
                    values.put("sitin_price",temp.get(i).sitInPrice);
                    values.put("ask_price",temp.get(i).askPrice);
                    values.put("url",temp.get(i).url);
                    values.put("color",temp.get(i).color);
                    values.put("price_type",temp.get(i).priceType);
                    App.getSqlManager().insert(tableName, values);
                }
                Log.e("jxf","schedule页:子线程下拉提交数据库完毕！");
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
        return R.layout.schedule_layout_fragment;
    }

    @Override
    protected void onClickEvent(View view) {

    }
    Comparator unordercomp = new Comparator() {
        public int compare(Object o1, Object o2) {
            Schedule p1 = (Schedule) o1;
            Schedule p2 = (Schedule) o2;
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
