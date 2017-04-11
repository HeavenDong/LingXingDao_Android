package com.miracleworld.lingxingdao.android.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.bean.TicketInfoBean;
import com.miracleworld.lingxingdao.android.options.ImageLoaderOptions;
import com.miracleworld.lingxingdao.android.utils.CommanUtil;
import com.miracleworld.lingxingdao.android.view.myroundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.RuleBasedCollator;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 购票支付历史的适配器
 */
public class TicketPayHistoryAdapter extends BaseAdapter{
    private  Context context;
    private ArrayList<TicketInfoBean> ticketInfoBeans;

    public TicketPayHistoryAdapter(Context context, ArrayList<TicketInfoBean> ticketInfoBeans) {
        this.context=context;
        this.ticketInfoBeans = ticketInfoBeans;
    }

    @Override
    public int getCount() {
        return ticketInfoBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }



    class PayHistoryHolder {
        TextView ticket_history_item_pay_code;
        RoundedImageView ticket_history_item_head;
        TextView ticket_history_item_scedule_lecturer;
        TextView ticket_history_item_scedule_context;
        TextView ticket_history_item_ticket_number;
        TextView ticket_history_item_scedule_time;
        TextView ticket_history_item_scedule_address;
        TextView ticket_history_item_scedule_price;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PayHistoryHolder holder;
        if (convertView==null){
            holder=  new PayHistoryHolder();
            convertView= View.inflate(context,R.layout.ticket_pay_history_list_item, null);
            holder.ticket_history_item_pay_code= (TextView) convertView.findViewById(R.id.ticket_history_item_pay_code);
            holder.ticket_history_item_head= (RoundedImageView) convertView.findViewById(R.id.ticket_history_item_head);
            holder.ticket_history_item_scedule_lecturer= (TextView) convertView.findViewById(R.id.ticket_history_item_scedule_lecturer);
            holder.ticket_history_item_scedule_context= (TextView) convertView.findViewById(R.id.ticket_history_item_scedule_context);
            holder.ticket_history_item_ticket_number= (TextView) convertView.findViewById(R.id.ticket_history_item_ticket_number);
            holder.ticket_history_item_scedule_time= (TextView) convertView.findViewById(R.id.ticket_history_item_scedule_time);
            holder.ticket_history_item_scedule_address= (TextView) convertView.findViewById(R.id.ticket_history_item_scedule_address);
            holder.ticket_history_item_scedule_price= (TextView) convertView.findViewById(R.id.ticket_history_item_scedule_price);
            convertView.setTag(holder);
        }else {
            holder = (PayHistoryHolder) convertView.getTag();
        }
        ImageLoader.getInstance().displayImage(ticketInfoBeans.get(position).portraitUrlSmall, holder.ticket_history_item_head, ImageLoaderOptions.headOptions);
        holder.ticket_history_item_pay_code.setText(ticketInfoBeans.get(position).ordersn);
        holder.ticket_history_item_scedule_lecturer.setText(ticketInfoBeans.get(position).teacherName);
        holder.ticket_history_item_scedule_context.setText(ticketInfoBeans.get(position).title);
        holder.ticket_history_item_ticket_number.setText(ticketInfoBeans.get(position).ticketNumber + "");
        holder.ticket_history_item_scedule_time.setText((CommanUtil.transhms(ticketInfoBeans.get(position).startDate, "yyyy.MM.dd HH:mm")));
        holder.ticket_history_item_scedule_address.setText(ticketInfoBeans.get(position).cityName);
        holder.ticket_history_item_scedule_price.setText(((int)ticketInfoBeans.get(position).amount)+"");

        return convertView;
    }
//        ImageLoader.getInstance().displayImage(ticketInfoBeans.get(position).portraitUrlSmall
//                , holder.ticket_history_item_head
//                , ImageLoaderOptions.headOptions);
    //        holder.scedule_address.setText(ticketInfoBeans.get(position).address);


}
