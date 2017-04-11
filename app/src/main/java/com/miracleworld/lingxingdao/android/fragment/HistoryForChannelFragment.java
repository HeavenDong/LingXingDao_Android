package com.miracleworld.lingxingdao.android.fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.RequestParams;
import com.miracleworld.lingxingdao.android.App;
import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.adapter.ChannelPayHistoryAdapter;
import com.miracleworld.lingxingdao.android.base.BaseFragment;
import com.miracleworld.lingxingdao.android.bean.PayHistoryBean;
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
import java.util.List;

/**
 * 订阅频道支付历史
 */
public class HistoryForChannelFragment extends BaseFragment {

    private static final String TAG = "haifeng";
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
    private boolean isfromBelow;
    private boolean isfromEnd;


    private List<PayHistoryBean> payHistoryList = new ArrayList<PayHistoryBean>();
    private ChannelPayHistoryAdapter adapter;
    @Override
    protected void initView(View view, Bundle bundle) {
        Log.e("jxf", "进入订阅");
        //网络参数的设置
        userId=(int) SharedPreUtils.get(context, "user_id", 0);
        maxId=0;
        minId=0;
        //相关参数
        isFirst=true;
        isfromEnd=true;
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
        pay_history_refresh= (PullToRefreshListView)view.findViewById(R.id.pay_history_refresh);
        pay_history_refresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        /**listview*/
        pay_history_lv= pay_history_refresh.getRefreshableView();
        /**去掉底部顶部阴影*/
        pay_history_lv.setOverScrollMode(View.OVER_SCROLL_NEVER);

        initData();
        loadData();
    }

    private void initData() {

        adapter= new ChannelPayHistoryAdapter(context,payHistoryList);
        pay_history_lv.setAdapter(adapter);
        pay_history_refresh.setOnLastItemVisibleListener(new PullToRefreshBase.OnLastItemVisibleListener() {
            @Override
            public void onLastItemVisible() {
                if (isfromEnd) {
                    isfromBelow=true;
                    pay_history_refresh.setMode(PullToRefreshBase.Mode.PULL_FROM_END);
                    pay_history_refresh.setRefreshing();
                    ArrayList<PayHistoryBean> temp=new ArrayList<PayHistoryBean>();
                    temp.addAll(payHistoryList);
                    Collections.sort(temp, unordercomp);
                    minId = temp.get(temp.size()-1).orderId;
                    Log.e("haifeng", "触底" + minId);
                    maxId = 0;
                    loadData();

                }
            }
        });
        /**下拉刷新*上拉加载*/
        pay_history_refresh.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                isfromStart = true;
                ArrayList<PayHistoryBean> temp=new ArrayList<PayHistoryBean>();
                temp.addAll(payHistoryList);
                Collections.sort(temp, unordercomp);
                maxId = temp.get(0).orderId;
                Log.e("haifeng", "下拉=" + maxId);
                minId = 0;
                loadData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
//                //触发这个肯定是会有数据的
//                isfromEnd = true;
//                ArrayList<PayHistoryBean> temp=new ArrayList<PayHistoryBean>();
//                temp.addAll(payHistoryList);
//                Collections.sort(temp, unordercomp);
//                minId = temp.get(temp.size()-1).orderId;
                Log.e("haifeng", "上拉控件=111");
//                maxId = 0;
//                loadData();
            }
        });
    }

    private void loadData() {
        if (isfromBelow){
            isfromEnd=false;
        }
        /**第一次进入*/
        if (isFirst){
            // 开始动画
            pay_history_loading_iv.setAnimation(rotateAnimation);
            pay_history_loading_iv.setVisibility(View.VISIBLE);

        }
        /**解决断网情况下，刷新根本停不下来的问题
         *异步给它点时间关闭*/
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



    private void netRequestAndResponse() {
        RequestParams params = new RequestParams();
        params.put("userId",userId);
        params.put("maxId", maxId);
        params.put("minId", minId);
        params.put("pageSize", pageSize);
        Log.e(TAG, "《支付历史》: 频道发给后台--" + params);
        NetClient.headGet(context, Url.LOOK_HISTORYPAY_URL, params, new NetResponseHandler() {
            @Override
            public void onResponse(String json) {
                Log.e(TAG, "《支付历史》: 频道后台返回--" + json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String status = jsonObject.optString("status");
                    String errorCode = jsonObject.getString("errorCode");
                    if (status.equals("1")) {
                        JSONArray jsonArray = jsonObject.optJSONArray("datas");
                        if (jsonArray != null) {
                            ArrayList<PayHistoryBean> temp = new ArrayList<PayHistoryBean>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                PayHistoryBean bean = new PayHistoryBean();
                                bean.teacherId = obj.optInt("teacherId");
                                bean.orderId = obj.optInt("orderId");
                                bean.teacherName = obj.optString("teacherName");
                                bean.channelName = obj.optString("channelName");
                                bean.portraitUrlBig = obj.optString("portraitUrlBig");
                                bean.portraitUrlSmall = obj.optString("portraitUrlSmall");
                                bean.ordersn = obj.optString("ordersn");
                                bean.type = obj.optString("type");
                                bean.endTime = obj.optString("endTime");
                                bean.period = obj.optString("period");
                                bean.createTime = obj.optLong("createTime");
                                bean.amount = obj.optDouble("amount");
                                temp.add(bean);
                            }
                            Log.e(TAG, "《支付历史》: temp大小--" + temp.size());
                            /**下拉刷新*/
                            if (isfromStart) {
                                payHistoryList.addAll(0, temp);
                                adapter.notifyDataSetChanged();
                                pay_history_refresh.onRefreshComplete();
                                isfromStart = false;
                                isfromEnd = true;

                                /**最后数据*/
                                if (payHistoryList.size()  < pageSize) {
//                                    pay_history_refresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                    pay_history_lv.addFooterView(footer);
                                    isfromEnd = false;
                                }
                            }
                            /**上拉加载*/
                            if (isfromBelow) {
                                payHistoryList.addAll(temp);
                                adapter.notifyDataSetChanged();
                                pay_history_refresh.onRefreshComplete();
                                pay_history_refresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                isfromBelow=false;
                                isfromEnd=true;
                                /**最后数据*/
                                if (temp.size()  < pageSize) {
                                    pay_history_lv.addFooterView(footer);
                                    isfromEnd = false;
                                }
                            }

                            /**第一次进入加载数据*/
                            if (isFirst) {
                                payHistoryList.addAll(temp);
                                adapter.notifyDataSetChanged();
                                pay_history_loading_iv.clearAnimation();
                                pay_history_loading_iv.setVisibility(View.GONE);
                                isFirst = false;
                                /**最后数据*/
                                if (temp.size()  < pageSize) {
                                    pay_history_refresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                    pay_history_lv.addFooterView(footer);
                                    isfromEnd=false;
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
                                isfromEnd=false;
                            }
                            if (isfromStart) {
                                pay_history_refresh.onRefreshComplete();
                                isfromStart = false;
                            }
                            if (isfromBelow) {
                                Log.e("jxf", "home上拉没有数据了");
                                pay_history_refresh.onRefreshComplete();
                                pay_history_lv.addFooterView(footer);
                                pay_history_lv.setSelection(adapter.getCount() - 1);
                                pay_history_refresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                isfromBelow = false;
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
                                isfromEnd=false;
                            }
                            if (isfromStart) {
                                //上拉和下拉的刷新完成
                                pay_history_refresh.onRefreshComplete();
                                isfromStart = false;
//                                isfromEnd=true;
                            }
                            if (isfromBelow) {
                                pay_history_refresh.onRefreshComplete();
                                pay_history_lv.addFooterView(footer);
                                pay_history_lv.setSelection(adapter.getCount() - 1);
                                pay_history_refresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
                                isfromBelow = false;
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

    /**关闭刷新*/
    private void netOff() {
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
            pay_history_refresh.onRefreshComplete();
            isfromStart = false;
            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.toast_net));
        }
        //上拉
        if (isfromBelow) {
            pay_history_refresh.onRefreshComplete();
            pay_history_refresh.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            isfromBelow = false;
            isfromEnd=true;
            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.toast_net));
        }

    }
    @Override
    protected int setLayout() {
        return R.layout.history_channel_layout_fragment;
    }

    @Override
    protected void onClickEvent(View view) {

    }


    Comparator unordercomp = new Comparator() {
        public int compare(Object o1, Object o2) {
            PayHistoryBean p1 = (PayHistoryBean) o1;
            PayHistoryBean p2 = (PayHistoryBean) o2;
            if (p1.orderId < p2.orderId)
                return 1;
            else if (p1.orderId == p2.orderId)
                return 0;
            else if (p1.orderId > p2.orderId)
                return -1;
            return 0;
        }
    };
}
