package com.miracleworld.lingxingdao.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.bean.ChannelCategoryHorizontalBean;

import java.util.ArrayList;

/**
 * Created by donghaifeng on 2015/12/23.
 */
public class ChannelHorizontrallvAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<ChannelCategoryHorizontalBean> channelCategoryHorizontals;

    public ChannelHorizontrallvAdapter(Context context, ArrayList<ChannelCategoryHorizontalBean> channelCategoryHorizontals){
        this.context=context;
        this.channelCategoryHorizontals=channelCategoryHorizontals;
    }

    @Override
    public int getCount() {
        return channelCategoryHorizontals.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    //继续沿用subscription的横向列表的布局
    class ViewHolder{
        //字
        TextView subscription_horizontral_lv_item_tv;
        //线
        ImageView subscription_horizontral_lv_item_iv;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            convertView=View.inflate(context, R.layout.subscription_horizontral_lv_item,null);
            holder=new ViewHolder();
            holder.subscription_horizontral_lv_item_tv= (TextView) convertView.findViewById(R.id.subscription_horizontral_lv_item_tv);
            holder.subscription_horizontral_lv_item_iv= (ImageView) convertView.findViewById(R.id.subscription_horizontral_lv_item_iv);
            convertView.setTag(holder);
        }else{
            holder= (ViewHolder) convertView.getTag();
        }
        if(channelCategoryHorizontals.get(position).isCheck){
            holder.subscription_horizontral_lv_item_tv.setTextColor(context.getResources().getColor(R.color.content_drawerlayout_category_font_color_click));
            holder.subscription_horizontral_lv_item_tv.setText(channelCategoryHorizontals.get(position).detailCategory);
            holder.subscription_horizontral_lv_item_iv.setImageResource(R.drawable.content_classify);
        }
        else{
            holder.subscription_horizontral_lv_item_tv.setTextColor(context.getResources().getColor(R.color.content_teacher_introduce_title_font_color));
            holder.subscription_horizontral_lv_item_tv.setText(channelCategoryHorizontals.get(position).detailCategory);
            holder.subscription_horizontral_lv_item_iv.setImageResource(R.drawable.content_classify_transport);
        }
        return convertView;
    }
}
