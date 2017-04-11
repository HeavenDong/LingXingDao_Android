package com.miracleworld.lingxingdao.android.activity;

import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.adapter.DetailofHistoryTicketAdapter;
import com.miracleworld.lingxingdao.android.base.BaseActivity;
import com.miracleworld.lingxingdao.android.bean.DetailofHictoryTicketBean;
import com.miracleworld.lingxingdao.android.bean.TicketInfoBean;
import com.miracleworld.lingxingdao.android.options.ImageLoaderOptions;
import com.miracleworld.lingxingdao.android.utils.CommanUtil;
import com.miracleworld.lingxingdao.android.view.myroundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 *   购票历史之详情
 */
public class DetailofHistoryTicketActivity extends BaseActivity{
    private ListView  lv;
    private String ordersn;
    private String teacherName;
    private long startDate;
    private long endDate;
    private String title;
    private double amount;
    private int ticketNumber;
    private String portraitUrlSmall;
    private String portraitUrlBig;
    private String cityName;
    private ArrayList<DetailofHictoryTicketBean> detailofHictoryTicketBeans;

    @Override
    protected void initView() {
        getBundle();
        RelativeLayout detail_ticket_title_left= (RelativeLayout) findViewById(R.id.detail_ticket_title_left);
        detail_ticket_title_left.setOnClickListener(this);
        lv = (ListView) findViewById(R.id.detail_ticket_history_lv);
        lv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        View head=View.inflate(this,R.layout.activity_ticket_payhistory_head,null);
        RoundedImageView ticket_history_detail_head= (RoundedImageView) head.findViewById(R.id.ticket_history_detail_head);
        ImageLoader.getInstance().displayImage(portraitUrlSmall,ticket_history_detail_head, ImageLoaderOptions.headOptions);
        TextView ticket_history_detail_pay_code= (TextView) head.findViewById(R.id.ticket_history_detail_pay_code);
        ticket_history_detail_pay_code.setText(ordersn);
        TextView ticket_history_detail_scedule_lecturer= (TextView) head.findViewById(R.id.ticket_history_detail_scedule_lecturer);
        ticket_history_detail_scedule_lecturer.setText(teacherName);
        TextView ticket_history_detail_scedule_context= (TextView) head.findViewById(R.id.ticket_history_detail_scedule_context);
        ticket_history_detail_scedule_context.setText(title);
        TextView ticket_history_detail_scedule_time= (TextView) head.findViewById(R.id.ticket_history_detail_scedule_time);
        ticket_history_detail_scedule_time.setText((CommanUtil.transhms(startDate, "yyyy.MM.dd HH:mm")));
        TextView ticket_history_detail_scedule_address= (TextView) head.findViewById(R.id.ticket_history_detail_scedule_address);
        ticket_history_detail_scedule_address.setText(cityName);
        TextView ticket_history_detail_scedule_price= (TextView) head.findViewById(R.id.ticket_history_detail_scedule_price);
        ticket_history_detail_scedule_price.setText((int)amount+"");
        TextView ticket_history_detail_ticket_number= (TextView) head.findViewById(R.id.ticket_history_detail_ticket_number);
        ticket_history_detail_ticket_number.setText(""+ticketNumber);
        lv.addHeaderView(head);
        DetailofHistoryTicketAdapter adapter=new DetailofHistoryTicketAdapter(this,detailofHictoryTicketBeans);
        lv.setAdapter(adapter);
    }

    private void getBundle() {
        TicketInfoBean ticketInfoBean= (TicketInfoBean) getIntent().getSerializableExtra("ticketInfoBean");
        ordersn=ticketInfoBean.ordersn;
        teacherName=ticketInfoBean.teacherName;
        startDate=ticketInfoBean.startDate;
        endDate=ticketInfoBean.endDate;
        title=ticketInfoBean.title;
        amount= ticketInfoBean.amount;
        ticketNumber=ticketInfoBean.ticketNumber;
        String ordersTicketDetailProtocolList=ticketInfoBean.ordersTicketDetailProtocolList;
        portraitUrlSmall =ticketInfoBean.portraitUrlSmall;
        portraitUrlBig=ticketInfoBean.portraitUrlBig;
        cityName=ticketInfoBean.cityName;
        detailofHictoryTicketBeans=new ArrayList<DetailofHictoryTicketBean>();
        try {
            JSONArray array=new JSONArray(ordersTicketDetailProtocolList);
            for (int i = 0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                DetailofHictoryTicketBean bean=new DetailofHictoryTicketBean();
                bean.id=obj.optInt("id");
                bean.buyType=obj.optInt("buyType");
                bean.price=obj.getDouble("price");
                bean.name=obj.optString("name");
                bean.mobile=obj.optString("mobile");
                bean.identityCard=obj.optString("identityCard");
                bean.agent=obj.optString("agent");
                detailofHictoryTicketBeans.add(bean);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setContentLayout() {
        setContentView(R.layout.ticket_pay_history_detail);
    }

    @Override
    protected void onClickEvent(View view) {
        switch (view.getId()){
            case R.id.detail_ticket_title_left:
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                break;
        }
    }
}
