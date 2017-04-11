package com.miracleworld.lingxingdao.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.bean.PayHistoryBean;
import com.miracleworld.lingxingdao.android.options.ImageLoaderOptions;
import com.miracleworld.lingxingdao.android.utils.CommanUtil;
import com.miracleworld.lingxingdao.android.view.myroundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * 频道支付历史的适配器
 */
public class ChannelPayHistoryAdapter extends BaseAdapter{
    private  Context context;
    private  List<PayHistoryBean> payHistoryList;

    public ChannelPayHistoryAdapter(Context context, List<PayHistoryBean> payHistoryList) {
        this.context=context;
        this.payHistoryList=payHistoryList;
    }



    @Override
    public int getCount() {
        return payHistoryList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PayHistoryHolder holder;
        if (convertView==null){
          holder=  new PayHistoryHolder();
            convertView= View.inflate(context,R.layout.pay_history_list_item, null);
            holder.pay_code= (TextView) convertView.findViewById(R.id.pay_code);
            holder.pay_history_item_head= (RoundedImageView) convertView.findViewById(R.id.pay_history_item_head);
            holder.subscribe_lecturer= (TextView) convertView.findViewById(R.id.subscribe_lecturer);
            holder.subscribe_channel= (TextView) convertView.findViewById(R.id.subscribe_channel);
            holder.subscribe_week= (TextView) convertView.findViewById(R.id.subscribe_week);
            holder.subscribe_time= (TextView) convertView.findViewById(R.id.subscribe_time);
            holder.subscribe_way= (TextView) convertView.findViewById(R.id.subscribe_way);
            holder.subscribe_price= (TextView) convertView.findViewById(R.id.subscribe_price);
            convertView.setTag(holder);
        }else {
            holder = (PayHistoryHolder) convertView.getTag();
        }
        /**填写数据*/
        ImageLoader.getInstance().displayImage(payHistoryList.get(position).portraitUrlSmall
                , holder.pay_history_item_head
                , ImageLoaderOptions.headOptions);

        holder.pay_code.setText(payHistoryList.get(position).ordersn);
        holder.subscribe_lecturer.setText(payHistoryList.get(position).teacherName);
        holder.subscribe_channel.setText(payHistoryList.get(position).channelName);
        holder.subscribe_week.setText(payHistoryList.get(position).period);
        holder.subscribe_time.setText(CommanUtil.transhms(payHistoryList.get(position).createTime, "yyyy.MM.dd")+"-"+CommanUtil.transhms(Long.parseLong(payHistoryList.get(position).endTime),"yyyy.MM.dd"));
        holder.subscribe_way.setText(payHistoryList.get(position).type);
        holder.subscribe_price.setText(""+(int)payHistoryList.get(position).amount);
        return convertView;
    }

    private class PayHistoryHolder {
        TextView pay_code,subscribe_lecturer,subscribe_channel,subscribe_week,subscribe_time,subscribe_way,subscribe_price;
        RoundedImageView pay_history_item_head;
    }


}
