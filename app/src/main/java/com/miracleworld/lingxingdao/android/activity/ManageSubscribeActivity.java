package com.miracleworld.lingxingdao.android.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.RequestParams;
import com.miracleworld.lingxingdao.android.App;
import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.adapter.ManageSubscribeAdapter;
import com.miracleworld.lingxingdao.android.base.BaseActivity;
import com.miracleworld.lingxingdao.android.bean.ManageSubscribeBean;
import com.miracleworld.lingxingdao.android.bean.TicketInfoBean;
import com.miracleworld.lingxingdao.android.http.NetClient;
import com.miracleworld.lingxingdao.android.http.NetResponseHandler;
import com.miracleworld.lingxingdao.android.http.Url;
import com.miracleworld.lingxingdao.android.utils.CommanUtil;
import com.miracleworld.lingxingdao.android.utils.SharedPreUtils;
import com.miracleworld.lingxingdao.android.view.DefinedSingleToast;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;

/**
 *   订阅管理
 */
public class ManageSubscribeActivity extends BaseActivity{
    private ManageSubscribeActivity context;
    private RelativeLayout manage_subscribe_net_message,manage_subscribe_not_message;
    private ImageView manage_subscribe_loading_iv;
    private RotateAnimation rotateAnimation;
    private PullToRefreshListView manage_subscribe_refresh;
    private ListView manage_subscribe_lv;
    private View footer;
    //请求网络的数据
    private int userId;
    private int maxId;
    private int minId;
    private int pageSize=10;
    //要进行判断的值
    private boolean isFirst;
    private boolean isfromStart;
    private boolean isfromBelow;
    private boolean isfromEnd;

    private ArrayList<ManageSubscribeBean> manageSubscribeBeanListList;
    private ManageSubscribeAdapter adapter;
    @Override
    protected void initView() {
        context=ManageSubscribeActivity.this;
        //网络参数的设置
        userId=(int) SharedPreUtils.get(this, "user_id", 0);
        maxId=0;
        minId=0;
        manageSubscribeBeanListList = new ArrayList<ManageSubscribeBean>();
        //相关参数
        isFirst=true;
        isfromEnd=true;
        /**查无数据通知*/
        manage_subscribe_not_message= (RelativeLayout)findViewById(R.id.manage_subscribe_not_message);
        manage_subscribe_not_message.setVisibility(View.GONE);
        /**网络不好通知*/
        manage_subscribe_net_message= (RelativeLayout)findViewById(R.id.manage_subscribe_net_message);
        manage_subscribe_net_message.setVisibility(View.GONE);
        /**正在加载图片*/
        manage_subscribe_loading_iv= (ImageView)findViewById(R.id.manage_subscribe_loading_iv);
        /**旋转动画*/
        rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(context, R.anim.rotate_refresh_drawable_default);
        /**脚布局*/
        footer=View.inflate(context, R.layout.list_footer,null);
        /**刷新加载控件*/
        manage_subscribe_refresh= (PullToRefreshListView)findViewById(R.id.manage_subscribe_refresh);
        manage_subscribe_refresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        /**listview*/
        manage_subscribe_lv= manage_subscribe_refresh.getRefreshableView();
        /**去掉底部顶部阴影*/
        manage_subscribe_lv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        findViewById(R.id.manage_subscribe_title_left).setOnClickListener(this);

        initData();
        loadData();
    }
    private void initData() {
        adapter= new ManageSubscribeAdapter(this,manageSubscribeBeanListList);
        manage_subscribe_lv.setAdapter(adapter);
        manage_subscribe_refresh.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if (isfromEnd) {
                    isfromBelow = true;
                    manage_subscribe_refresh.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                    manage_subscribe_refresh.setRefreshing();
                    if (manageSubscribeBeanListList.size() > 0) {
                        minId = manageSubscribeBeanListList.get(manageSubscribeBeanListList.size() - 1).orderId;
                    } else {
                        minId = 0;
                    }
                    maxId = 0;
                    Log.e("haifeng", "《订阅管理》触底滚动");
                    loadData();
                }
            }
        });
        manage_subscribe_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position > manageSubscribeBeanListList.size()) {
                    return;
                } else {
                    //position要减1
                    position = position - 1;
                    Intent intent = new Intent(context, ChannelDetailActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("teacherId", manageSubscribeBeanListList.get(position).teacherId);
                    bundle.putString("portraitUrlSmall", manageSubscribeBeanListList.get(position).portraitUrlSmall);
                    bundle.putString("nickname", manageSubscribeBeanListList.get(position).teacherName);
                    bundle.putInt("channelId", manageSubscribeBeanListList.get(position).channelId);
                    bundle.putDouble("channelPrice", manageSubscribeBeanListList.get(position).amount);
                    bundle.putString("des", manageSubscribeBeanListList.get(position).des);
                    bundle.putString("channelName", manageSubscribeBeanListList.get(position).channelName);
                    bundle.putString("startSource", "3");
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 1101);
                }

            }
        });



        /**下拉刷新*/
        manage_subscribe_refresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                isfromStart = true;
                maxId = 0;
                minId = 0;
                Log.e("haifeng", "《订阅管理》下啦:新的maxId--" + maxId);
                loadData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//                isfromEnd = true;
//                minId = manageSubscribeBeanListList.get(manageSubscribeBeanListList.size() - 1).orderId;
//                maxId = 0;
//                Log.e("haifeng", "《订阅管理》上啦:新的minId--" + minId);
//                loadData();
            }
        });

    }
    @Override
    public void setContentLayout() {
        setContentView(R.layout.manage_subscribe);
    }

    @Override
    protected void onClickEvent(View view) {
        switch (view.getId()){
            case R.id.manage_subscribe_title_left:
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                break;
        }
    }

    private void loadData() {
        if (isfromBelow){
            isfromEnd=false;
        }
        /**第一次进入*/
        if (isFirst){
            manage_subscribe_loading_iv.setAnimation(rotateAnimation);
            manage_subscribe_loading_iv.setVisibility(View.VISIBLE);
        }

        if(CommanUtil.isNetworkAvailable()) {
            netRequestAndResponse();
        }else {
            new AsyncTask<Void,Void,Void>(){

                @Override
                protected Void doInBackground(Void... params) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    netOff();
                }
            }.execute();
        }
    }

    private void netRequestAndResponse() {
        RequestParams params = new RequestParams();
        params.put("userId",userId);
        params.put("maxId", maxId);
        params.put("minId", minId);
        params.put("pageSize", pageSize);
        Log.e("haifeng", "《订阅管理》: 发给后台--" + params);
        NetClient.headGet(this, Url.LOOK_MANAGSUB_URL, params, new NetResponseHandler() {
            @Override
            public void onResponse(String json) {
                Log.e("haifeng", "《订阅管理》: 后台返回--" + json);
                try {
                    JSONObject jSONObject = new JSONObject(json);
                    String status = jSONObject.optString("status");
                    String errorCode = jSONObject.optString("errorCode");
                    if (status.equals("1")) {
                        JSONArray jsonArray = jSONObject.optJSONArray("datas");
                        if (jsonArray != null) {
                            manage_subscribe_not_message.setVisibility(View.GONE);
                            ArrayList<ManageSubscribeBean> temp = new ArrayList<ManageSubscribeBean>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                ManageSubscribeBean bean = new ManageSubscribeBean();
                                bean.teacherId = obj.optInt("teacherId");
                                bean.orderId = obj.optInt("orderId");
                                bean.channelId=obj.optInt("channelId");
                                bean.teacherName = obj.optString("teacherName");
                                bean.channelName=obj.optString("channelName");
                                bean.portraitUrlBig = obj.optString("portraitUrlBig");
                                bean.portraitUrlSmall = obj.optString("portraitUrlSmall");
                                bean.ordersn = obj.optString("ordersn");
                                bean.period= obj.optString("period");
                                bean.des=obj.optString("des");
                                bean.createTime = obj.optLong("createTime");
                                bean.endTime = obj.optLong("endTime");
                                bean.amount = obj.optLong("amount");
                                temp.add(bean);

                            }
                            Log.e("haifeng", "《订阅管理》: temp大小--" + temp.size());
                            /**下拉刷新*/
                            if (isfromStart) {
                                isfromEnd = true;
                                manageSubscribeBeanListList.clear();
                                manageSubscribeBeanListList.addAll(temp);
                                manage_subscribe_lv.removeFooterView(footer);
                                adapter.notifyDataSetChanged();
                                manage_subscribe_refresh.onRefreshComplete();
                                isfromStart = false;
                                manage_subscribe_lv.setSelection(0);
                                /**最后数据*/
                                if (manageSubscribeBeanListList.size() < pageSize) {
                                    manage_subscribe_lv.addFooterView(footer);
                                    isfromEnd = false;
                                }
                            }
                            /**上拉加载*/
                            if (isfromBelow) {
                                manageSubscribeBeanListList.addAll(temp);
                                adapter.notifyDataSetChanged();
                                manage_subscribe_refresh.onRefreshComplete();
                                manage_subscribe_refresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                isfromBelow=false;
                                isfromEnd=true;
                                /**最后数据*/
                                if (temp.size() < pageSize) {
                                    manage_subscribe_lv.addFooterView(footer);
                                    isfromEnd = false;
                                }
                            }
                            /**第一次进入加载数据*/
                            if (isFirst) {
                                manageSubscribeBeanListList.clear();
                                manageSubscribeBeanListList.addAll(temp);
                                adapter.notifyDataSetChanged();
                                manage_subscribe_loading_iv.clearAnimation();
                                manage_subscribe_loading_iv.setVisibility(View.GONE);
                                isFirst = false;
                                /**最后数据*/
                                if (temp.size() < pageSize) {
                                    manage_subscribe_refresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                    manage_subscribe_lv.addFooterView(footer);
                                    isfromEnd = false;
                                }
                            }
                        } else {
                            /**拿到的集合为空*/
                            Log.e("jxf", "没有数据了");
                            if (isFirst){
                                //动画关闭
                                manage_subscribe_loading_iv.clearAnimation();
                                manage_subscribe_loading_iv.setVisibility(View.GONE);
                                manage_subscribe_not_message.setVisibility(View.VISIBLE);
                                isFirst=false;
                                isfromEnd=false;
                            }
                            if (isfromStart){
                                manage_subscribe_refresh.onRefreshComplete();
                                isfromStart=false;
                            }
                            if (isfromBelow){
                                Log.e("jxf", "home上拉没有数据了");
                                manage_subscribe_refresh.onRefreshComplete();
                                manage_subscribe_lv.addFooterView(footer);
                                manage_subscribe_lv.setSelection(adapter.getCount() - 1);
                                manage_subscribe_refresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                isfromBelow=false;
                            }
                        }
                        /**错误代码errorcode*/
                    } else {
                        if (errorCode.equals("2")) {
                            //表示第一次进入
                            if (isFirst) {
                                //动画关闭
                                manage_subscribe_loading_iv.clearAnimation();
                                manage_subscribe_loading_iv.setVisibility(View.GONE);
                                manage_subscribe_not_message.setVisibility(View.VISIBLE);
                                isFirst=false;
                                isfromEnd=false;
                            }
                            if (isfromStart) {
                                //上拉和下拉的刷新完成
                                manage_subscribe_refresh.onRefreshComplete();
                                isfromStart = false;
                            }
                            if (isfromBelow) {
                                //上拉和下拉的刷新完成
                                manage_subscribe_refresh.onRefreshComplete();
                                manage_subscribe_lv.addFooterView(footer);
                                manage_subscribe_lv.setSelection(adapter.getCount() - 1);
                                manage_subscribe_refresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                isfromBelow = false;
                            }
                        } else if (errorCode.equals("90001")) {
                            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text24));
                        }
                    }
                } catch (JSONException e) {
                    Log.e("jxf","订阅管理，解析服务返回JSONObject时异常");
                    e.printStackTrace();
                    manage_subscribe_loading_iv.clearAnimation();
                    manage_subscribe_loading_iv.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                netOff();
            }
        });
    }

    /**关闭刷新*/
    private void netOff() {
        //表示第一次进入
        if (isFirst) {
            //动画关闭
            manage_subscribe_loading_iv.clearAnimation();
            manage_subscribe_loading_iv.setVisibility(View.GONE);
            //让网络不好的页面出来
            manage_subscribe_net_message.setVisibility(View.VISIBLE);
            manage_subscribe_net_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    manage_subscribe_net_message.setVisibility(View.GONE);
                    maxId = 0;
                    minId = 0;
                    isFirst = true;
                    loadData();
                }
            });
        }
        //下拉
        if (isfromStart) {
            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.toast_net));
            manage_subscribe_refresh.onRefreshComplete();
            isfromStart = false;
        }
        //上拉
        if (isfromBelow) {
            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.toast_net));
            manage_subscribe_refresh.onRefreshComplete();
            manage_subscribe_refresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            isfromBelow = false;
            isfromEnd=true;
        }
    }
    private  int result;
    private  String startTime,endTime;
    /**获取后台时间,给支付页*/
    public void getServerTime(int channelId){
        if(CommanUtil.isNetworkAvailable()) {
            // 开始动画
            manage_subscribe_loading_iv.setAnimation(rotateAnimation);
            //显示
            manage_subscribe_loading_iv.setVisibility(View.VISIBLE);
            RequestParams params = new RequestParams();
            params.put("userId",(int)SharedPreUtils.get(App.getContext(), "user_id", 0));
            params.put("channelId",channelId);
            Log.e("haifeng", "《获取后台时间》: 发给后台--" + params);
            NetClient.headGet(this, Url.PAY_STARTANDEND_TIME, params, new NetResponseHandler() {
                @Override
                public void onResponse(String json) {
                    Log.e("haifeng", "《获取后台时间》: 发给后台--" + json);
                    JSONObject jSONObject = null;
                    try {
                        jSONObject = new JSONObject(json);
                        String status = jSONObject.optString("status");
                        if (status.equals("1")) {
                            String datas = jSONObject.getString("datas");
                            JSONObject jsonObject = new JSONObject(datas);

                            startTime = jsonObject.getString("startTime");
                            endTime = jsonObject.getString("endTime");
                            result =1;
                            adapter.notifyDataSetChanged();
                            //动画关闭
                            manage_subscribe_loading_iv.clearAnimation();
                            manage_subscribe_loading_iv.setVisibility(View.GONE);
                        }else {
                            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.toast_net));
                            //动画关闭
                            manage_subscribe_loading_iv.clearAnimation();
                            manage_subscribe_loading_iv.setVisibility(View.GONE);
                            result =0;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        result =0;
                        //动画关闭
                        manage_subscribe_loading_iv.clearAnimation();
                        manage_subscribe_loading_iv.setVisibility(View.GONE);
                        Log.e("jxf","续订，获取后台时间解析异常JSONException");
                    }

                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.toast_net));
                    //动画关闭
                    manage_subscribe_loading_iv.clearAnimation();
                    manage_subscribe_loading_iv.setVisibility(View.GONE);
                    result =0;
                }

            });

        }else {
            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.toast_net));
            result =0;
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*续订--成功*/
        if (requestCode==1010&&resultCode==483){
            maxId = 0;
            minId = 0;
            isfromStart = true;
            isfromEnd=true;
            manage_subscribe_refresh.setMode(PullToRefreshBase.Mode.BOTH);
            manage_subscribe_lv.removeFooterView(footer);
            loadData();
        }
        /*续订--失败*/
        if (requestCode==1010&&resultCode==482){
            Log.e("haifeng", "续订--失败");
        }
        /*条目点击--成功*/
        if (requestCode==1101&&resultCode==251){
            maxId = 0;
            minId = 0;
            isfromStart = true;
            isfromEnd=true;
            manage_subscribe_refresh.setMode(PullToRefreshBase.Mode.BOTH);
            manage_subscribe_lv.removeFooterView(footer);
            loadData();
        }
        /*条目点击--失败*/
        if (requestCode==1101&&resultCode==252){
            Log.e("haifeng", "条目点击--失败");
        }
    }
    /**
     *  获取后台时间成功：1
     *  获取后台时间失败：0
     * */
    public int getResult() {
        return result;
    }
    public String getStartTime() {
        return startTime;
    }
    public String getEndTime() {
        return endTime;
    }
}
