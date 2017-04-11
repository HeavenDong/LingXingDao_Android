package com.miracleworld.lingxingdao.android.fragment;

import android.content.Intent;
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
import com.miracleworld.lingxingdao.android.activity.DetailofHistoryTicketActivity;
import com.miracleworld.lingxingdao.android.adapter.TicketPayHistoryAdapter;
import com.miracleworld.lingxingdao.android.base.BaseFragment;
import com.miracleworld.lingxingdao.android.bean.PayHistoryBean;
import com.miracleworld.lingxingdao.android.bean.Schedule;
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
import java.util.Comparator;

/**
 * 购票支付历史
 */
public class HistoryForTicketFragment extends BaseFragment{
    private static final String TAG = "jxf";
    private RelativeLayout pay_history__net_message,pay_history__not_message;
    private ImageView pay_history_loading_iv;
    private RotateAnimation rotateAnimation;
    private PullToRefreshListView pay_history_refresh;
    private ListView pay_history_lv;
    private View footer;
    //请求网络的数据
    private int userId;
    private int maxId;
    private int minId;
    private int pageSize=10;
    //要进行判断的值
    private boolean isFirst;
    private boolean isfromStart;
    private boolean isfromEnd;
    private boolean isLoadMore=true;
    private ArrayList<TicketInfoBean> ticketInfoBeans = new ArrayList<TicketInfoBean>();
    private TicketPayHistoryAdapter adapter;

    @Override
    protected void initView(View view, Bundle bundle) {
        Log.e("jxf", "进入订票");
        //网络参数的设置
        userId=(int) SharedPreUtils.get(context, "user_id", 0);
        maxId=0;
        minId=0;
        //相关参数
        isFirst=true;
        /**查无数据通知*/
        pay_history__not_message= (RelativeLayout)view.findViewById(R.id.pay_history__not_message);
        pay_history__not_message.setVisibility(View.GONE);
        /**网络不好通知*/
        pay_history__net_message= (RelativeLayout)view.findViewById(R.id.pay_history__net_message);
        pay_history__net_message.setVisibility(View.GONE);
        /**正在加载图片*/
        pay_history_loading_iv= (ImageView)view.findViewById(R.id.pay_history_loading_iv);
        /**旋转动画*/
        rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(context, R.anim.rotate_refresh_drawable_default);
        /**脚布局*/
        footer=View.inflate(context, R.layout.list_footer,null);
        /**刷新加载控件*/
        pay_history_refresh= (PullToRefreshListView)view.findViewById(R.id.ticket_history_refresh);
        pay_history_refresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        /**listview*/
        pay_history_lv= pay_history_refresh.getRefreshableView();
        /**去掉底部顶部阴影*/
        pay_history_lv.setOverScrollMode(View.OVER_SCROLL_NEVER);

        initData();
        loadData();
    }
    private void initData() {

        adapter= new TicketPayHistoryAdapter(context, ticketInfoBeans);
        pay_history_lv.setAdapter(adapter);
        pay_history_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position> ticketInfoBeans.size()){
                    return;
                }
                else{
                    //position要减1
                    Intent intent = new Intent(context, DetailofHistoryTicketActivity.class);
                    TicketInfoBean ticketInfoBean = ticketInfoBeans.get(position-1);
                    Log.e("jxf","打印传递的字符串对象ordersTicketDetailProtocolList+++++++"+ticketInfoBean.ordersTicketDetailProtocolList);
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("ticketInfoBean", ticketInfoBean);
                    intent.putExtras(bundle);
                    context.startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            }
        });
        /**下拉刷新*上拉加载*/
        pay_history_refresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                isfromStart = true;
                ArrayList<TicketInfoBean> temp=new ArrayList<TicketInfoBean>();
                temp.addAll(ticketInfoBeans);
                Collections.sort(temp, unordercomp);
                maxId = temp.get(0).id;
                Log.e("jxf", "下拉maxId=" + maxId);
                minId = 0;
                loadData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });
        pay_history_refresh.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if (isLoadMore) {
                    pay_history_refresh.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                    Log.e("jxf", "让现实数显");
                    pay_history_refresh.setRefreshing();
                    isfromEnd = true;
                    ArrayList<TicketInfoBean> temp = new ArrayList<TicketInfoBean>();
                    temp.addAll(ticketInfoBeans);
                    Collections.sort(temp, unordercomp);
                    minId = temp.get(temp.size() - 1).id;
                    maxId = 0;
                    Log.e("jxf", "下拉minId=" + minId);
                    loadData();
                }
            }
        });
    }
    private void loadData() {
        if (isfromEnd){
            isLoadMore=false;
        }
        Log.e("jxf", "订票loadData加载一次");
        if (isFirst){
            // 开始动画
            pay_history_loading_iv.setAnimation(rotateAnimation);
            pay_history_loading_iv.setVisibility(View.VISIBLE);
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
    }
    @Override
    protected int setLayout() {
        return R.layout.history_ticket_layout_fragment;
    }

    @Override
    protected void onClickEvent(View view) {

    }

    private void netRequestAndResponse(){

            RequestParams params = new RequestParams();
            params.put("userId",userId);
            params.put("maxId", maxId);
            params.put("minId", minId);
            params.put("limit", pageSize);
            Log.e(TAG, "《支付历史》:订票发给后台--" + params);
            NetClient.headGet(context, Url.LOOK_TICKET_HISTORYPAY_URL, params, new NetResponseHandler() {
                @Override
                public void onResponse(String json) {
                    Log.e(TAG, "《支付历史》: 订票后台返回--" + json);
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        String status = jsonObject.optString("status");
                        String errorCode = jsonObject.getString("errorCode");
                        if (status.equals("1")) {
                            JSONArray jsonArray = jsonObject.optJSONArray("datas");
                            if (jsonArray != null) {
                                ArrayList<TicketInfoBean> temp = new ArrayList<TicketInfoBean>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject obj = jsonArray.getJSONObject(i);
                                    TicketInfoBean bean = new TicketInfoBean();
                                    bean.id = obj.optInt("id");
                                    bean.ordersn = obj.optString("ordersn");
                                    bean.teacherName = obj.optString("teacherName");
                                    bean.startDate = obj.optLong("startDate");
                                    bean.endDate = obj.optLong("endDate");
                                    bean.title = obj.optString("title");
                                    bean.amount = obj.optDouble("amount");
                                    bean.ticketNumber = obj.optInt("ticketNumber");
                                    bean.ordersTicketDetailProtocolList = obj.optString("ordersTicketDetailProtocolList");
                                    bean.portraitUrlSmall=obj.optString("portraitUrlSmall");
                                    bean.portraitUrlBig=obj.optString("portraitUrlBig");
                                    bean.cityName=obj.optString("cityName");
                                    temp.add(bean);
                                }
                                /**下拉刷新*/
                                if (isfromStart) {
                                    ticketInfoBeans.addAll(0, temp);
                                    adapter.notifyDataSetChanged();
                                    pay_history_refresh.onRefreshComplete();
                                    isfromStart = false;
                                    isLoadMore = true;
                                    if (temp.size() < pageSize) {
                                        pay_history_lv.addFooterView(footer);
                                        isLoadMore = false;
                                    }
                                }
                                /**上拉加载*/
                                if (isfromEnd) {
                                    ticketInfoBeans.addAll(temp);
                                    adapter.notifyDataSetChanged();
                                    pay_history_refresh.onRefreshComplete();
                                    pay_history_refresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                    isfromEnd = false;
                                    isLoadMore = true;
                                    if (temp.size() < pageSize) {
                                        pay_history_lv.addFooterView(footer);
                                        isLoadMore = false;
                                    }

                                }

                                /**第一次进入加载数据*/
                                if (isFirst) {
                                    ticketInfoBeans.addAll(temp);
                                    adapter.notifyDataSetChanged();
                                    pay_history_loading_iv.clearAnimation();
                                    pay_history_loading_iv.setVisibility(View.GONE);
                                    isFirst = false;
                                    /**最后数据*/
                                    if (temp.size() < pageSize) {
                                        pay_history_refresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                        pay_history_lv.addFooterView(footer);
                                        isLoadMore = false;
                                    }
                                }
                            } else {
                                /**新拿到的集合为空*/
                                if (isFirst) {
                                    //动画关闭
                                    pay_history_loading_iv.clearAnimation();
                                    pay_history_loading_iv.setVisibility(View.GONE);
                                    pay_history__not_message.setVisibility(View.VISIBLE);
                                    isFirst = false;
                                    isLoadMore = false;
                                }
                                if (isfromStart) {
                                    pay_history_refresh.onRefreshComplete();
                                    isfromStart = false;
                                }
                                if (isfromEnd) {
                                    Log.e("jxf", "home上拉没有数据了");
                                    pay_history_refresh.onRefreshComplete();
                                    pay_history_lv.addFooterView(footer);
                                    pay_history_lv.setSelection(adapter.getCount() - 1);
                                    pay_history_refresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                    isfromEnd = false;
                                }
                            }
                            /**错误代码errorcode*/
                        } else {
                            if (errorCode.equals("2")) {
//                            DefinedSingleToast.showToast(context, getResources().getString(R.string.regist_text27));
                                //表示第一次进入
                                if (isFirst) {
                                    //动画关闭
                                    pay_history_loading_iv.clearAnimation();
                                    pay_history_loading_iv.setVisibility(View.GONE);
                                    pay_history__not_message.setVisibility(View.VISIBLE);
                                    isFirst = false;
                                    isLoadMore = false;
                                }
                                if (isfromStart) {
                                    //上拉和下拉的刷新完成
                                    pay_history_refresh.onRefreshComplete();
                                    isfromStart = false;
                                }
                                if (isfromEnd) {
                                    //上拉和下拉的刷新完成
                                    pay_history_refresh.onRefreshComplete();
                                    pay_history_lv.addFooterView(footer);
                                    pay_history_lv.setSelection(adapter.getCount() - 1);
                                    pay_history_refresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                    isfromEnd = false;
                                }
                            } else if (errorCode.equals("90001")) {
                                DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text24));
                            }
                        }


                    } catch (JSONException e) {
                        Log.e("jxf", "支付历史，解析服务返回JSONObject时异常");
                        e.printStackTrace();
                        pay_history_loading_iv.clearAnimation();
                        pay_history_loading_iv.setVisibility(View.GONE);
                        Log.e(TAG, "走异常");
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    netOff();
                }
            });
        }


    private void netOff(){
        //表示第一次进入
        if (isFirst) {
            //动画关闭
            pay_history_loading_iv.clearAnimation();
            pay_history_loading_iv.setVisibility(View.GONE);
            //让网络不好的页面出来
            pay_history__net_message.setVisibility(View.VISIBLE);
            pay_history__net_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    pay_history__net_message.setVisibility(View.GONE);
                    Log.e("haifeng", "点击了加载");
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
            pay_history_refresh.onRefreshComplete();
            isfromStart = false;
        }
        //上拉
        if (isfromEnd) {
            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.toast_net));
            pay_history_refresh.onRefreshComplete();
            pay_history_refresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            isfromEnd = false;
            isLoadMore=true;
        }
    }
    Comparator unordercomp = new Comparator() {
        public int compare(Object o1, Object o2) {
            TicketInfoBean p1 = (TicketInfoBean) o1;
            TicketInfoBean p2 = (TicketInfoBean) o2;
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
