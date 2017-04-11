package com.miracleworld.lingxingdao.android.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.activity.ManageSubscribeActivity;
import com.miracleworld.lingxingdao.android.activity.pay.SubscribeForPayActivity;
import com.miracleworld.lingxingdao.android.bean.ManageSubscribeBean;
import com.miracleworld.lingxingdao.android.options.ImageLoaderOptions;
import com.miracleworld.lingxingdao.android.utils.CommanUtil;
import com.miracleworld.lingxingdao.android.view.myroundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 *  订阅管理的适配器
 */
public class ManageSubscribeAdapter extends BaseAdapter{
    private ManageSubscribeActivity context;
    private  List<ManageSubscribeBean> manageSubscribeBeanListList;
    private int index=-1;
    public ManageSubscribeAdapter(ManageSubscribeActivity context, List<ManageSubscribeBean> manageSubscribeBeanListList) {
        this.context=context;
        this.manageSubscribeBeanListList=manageSubscribeBeanListList;
    }
    @Override
    public int getCount() {
        return manageSubscribeBeanListList.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        PayHistoryHolder holder;
        if (convertView==null){
            convertView= View.inflate(context, R.layout.manage_subscribe_list_item, null);
            holder=  new PayHistoryHolder();
            holder.manage_subscribe_item_head= (RoundedImageView) convertView.findViewById(R.id.manage_subscribe_item_head);
            holder.subscribe_lecturer_name= (TextView) convertView.findViewById(R.id.subscribe_lecturer_name);
            holder.channel_name= (TextView) convertView.findViewById(R.id.channel_name);

            holder.subscribe_pay_time= (TextView) convertView.findViewById(R.id.subscribe_pay_time);
            holder.subscribe_finish_time= (TextView) convertView.findViewById(R.id.subscribe_finish_time);
            holder.resubscribe_btn=(LinearLayout)convertView.findViewById(R.id.resubscribe_btn);
            convertView.setTag(holder);
        }else {
            holder = (PayHistoryHolder) convertView.getTag();
        }
        ImageLoader.getInstance().displayImage(manageSubscribeBeanListList.get(position).portraitUrlSmall
                , holder.manage_subscribe_item_head
                , ImageLoaderOptions.headOptions);
        /**填写数据*/
        holder.subscribe_lecturer_name.setText(manageSubscribeBeanListList.get(position).teacherName);
        holder.channel_name.setText(manageSubscribeBeanListList.get(position).channelName);
        holder.subscribe_pay_time.setText(CommanUtil.transhms(manageSubscribeBeanListList.get(position).createTime, "yyyy.MM.dd"));
        holder.subscribe_finish_time.setText(CommanUtil.transhms(manageSubscribeBeanListList.get(position).endTime,"yyyy.MM.dd"));

        int i=context.getResult();
        if(i==1&&index==position){
            index=-1;
            Intent intent = new Intent(context, SubscribeForPayActivity.class);
            Bundle bundle=new Bundle();
            bundle.putString("lecturer_head", manageSubscribeBeanListList.get(position).portraitUrlSmall);
            bundle.putString("lecturer_name", manageSubscribeBeanListList.get(position).teacherName);
            bundle.putInt("teacherId", manageSubscribeBeanListList.get(position).teacherId);
            bundle.putDouble("Price", manageSubscribeBeanListList.get(position).amount);
            bundle.putInt("source",1);
            /**频道名*/
            bundle.putInt("channel_Id", manageSubscribeBeanListList.get(position).channelId);
            bundle.putString("channel_name", manageSubscribeBeanListList.get(position).channelName);
            bundle.putString("startTime",context.getStartTime());
            bundle.putString("endTime", context.getEndTime());
            intent.putExtras(bundle);
            context.startActivityForResult(intent, 1010);
            context.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }
        holder.resubscribe_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                index=position;
                context.getServerTime(manageSubscribeBeanListList.get(position).channelId);
            }
        });
        return convertView;
    }

    private class PayHistoryHolder {
        TextView subscribe_lecturer_name,channel_name,subscribe_pay_time,subscribe_finish_time;
        LinearLayout resubscribe_btn;
        RoundedImageView manage_subscribe_item_head;
    }

//    /**转换时间：yyyy.MM.dd*/
//    private String transymd(long progress){
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
//        String ms = formatter.format(progress);
//        return ms;
//    }

}
