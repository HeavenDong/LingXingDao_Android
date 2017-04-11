package com.miracleworld.lingxingdao.android.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.nfc.FormatException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.activity.PayWriteActivity;
import com.miracleworld.lingxingdao.android.bean.Schedule;
import com.miracleworld.lingxingdao.android.options.ImageLoaderOptions;
import com.miracleworld.lingxingdao.android.utils.CommanUtil;
import com.miracleworld.lingxingdao.android.view.myroundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by donghaifeng on 2015/12/18.
 */
public class ScheduleAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Schedule> schedules;
    private Activity activity;
    public ScheduleAdapter(Context context,ArrayList<Schedule> schedules,Activity activity){
        this.context=context;
        this.schedules=schedules;
        this.activity=activity;
    }
    @Override
    public int getCount() {
        return schedules.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    class ViewHolder{
        //条目圆角头像图片
        RoundedImageView fragment_schedule_lv_item_iv_head;
        //条目研讨会老师名字
        TextView fragment_schedule_lv_item_tv_name;
        //条目研讨会时间
        TextView fragment_schedule_lv_item_tv_time;
        //条目研讨会内容
        TextView fragment_schedule_lv_item_tv_content;
        //条目研讨会地址
        TextView fragment_schedule_lv_item_tv_address;
        //正常价钱
        TextView fragment_schedule_lv_item_tv_price;
        //复训容器
        LinearLayout fragment_schedule_againprice_group;
        //复训价钱
        TextView fragment_schedule_againprice;
        //购票键
        TextView channel_lv_item_butt;
        //背景色的颜色
        RelativeLayout background_color_rl;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            convertView=View.inflate(context, R.layout.fragment_schedule_lv_item,null);
            holder=new ViewHolder();
            holder.fragment_schedule_lv_item_iv_head= (RoundedImageView) convertView.findViewById(R.id.fragment_schedule_lv_item_iv_head);
            holder.fragment_schedule_lv_item_tv_name= (TextView) convertView.findViewById(R.id.fragment_schedule_lv_item_tv_name);
            holder.fragment_schedule_lv_item_tv_time= (TextView) convertView.findViewById(R.id.fragment_schedule_lv_item_tv_time);
            holder.fragment_schedule_lv_item_tv_content= (TextView) convertView.findViewById(R.id.fragment_schedule_lv_item_tv_content);
            holder.fragment_schedule_lv_item_tv_address= (TextView) convertView.findViewById(R.id.fragment_schedule_lv_item_tv_address);
            holder.fragment_schedule_lv_item_tv_price= (TextView) convertView.findViewById(R.id.fragment_schedule_lv_item_tv_price);
            holder.fragment_schedule_againprice_group= (LinearLayout) convertView.findViewById(R.id.fragment_schedule_againprice_group);
            holder.fragment_schedule_againprice= (TextView) convertView.findViewById(R.id.fragment_schedule_againprice);
            holder.channel_lv_item_butt= (TextView) convertView.findViewById(R.id.channel_lv_item_butt);
            holder.background_color_rl= (RelativeLayout) convertView.findViewById(R.id.background_color_rl);
            convertView.setTag(holder);
        }
        else {
            holder= (ViewHolder) convertView.getTag();
        }
        //以下赋值
        ImageLoader.getInstance().displayImage(schedules.get(position).portraitUrlSmall, holder.fragment_schedule_lv_item_iv_head, ImageLoaderOptions.headOptions);
        holder.fragment_schedule_lv_item_tv_name.setText(schedules.get(position).teacherName);
        String color=schedules.get(position).color;
        if (color.equals("")){
            Log.e("jxf","颜色没有值");
            holder.background_color_rl.setBackgroundColor(Color.parseColor("#65C4C9"));
        }
        else{
            holder.background_color_rl.setBackgroundColor(Color.parseColor(color));
        }
        holder.fragment_schedule_lv_item_tv_time.setText(CommanUtil.transhms(schedules.get(position).startTime, "yyyy.MM.dd")+"-"+CommanUtil.transhms(schedules.get(position).endTime,"yyyy.MM.dd"));
        holder.fragment_schedule_lv_item_tv_content.setText(schedules.get(position).title);
        holder.fragment_schedule_lv_item_tv_address.setText(schedules.get(position).provinceName+schedules.get(position).cityName+"("+schedules.get(position).address+")");
        holder.fragment_schedule_lv_item_tv_price.setText(""+((int)(schedules.get(position).price)));
        //判断有没有复训价格  默认没有
        boolean haveAgain=false;
        //解析String 为String 数组
        if (schedules.get(position).priceType.equals("")){
            haveAgain=false;
        }else{
            String[] typeString=schedules.get(position).priceType.split(",");
            Log.e("jxf","打印数组的长度"+typeString.length);
            for (int i=0;i<typeString.length;i++) {
                //表示有复训
                if (typeString[i].equals("1")) {
                    haveAgain=true;
                }
                //不用执行else
            }
        }

        if (haveAgain){
            holder.fragment_schedule_againprice_group.setVisibility(View.VISIBLE);
            holder.fragment_schedule_againprice.setText("" + ((int) (schedules.get(position).againPrice)));
        } else{
            holder.fragment_schedule_againprice_group.setVisibility(View.GONE);

        }
//        if (schedules.get(position).isAgain==0){
//            holder.fragment_schedule_againprice_group.setVisibility(View.GONE);
//
//        } else{
//            holder.fragment_schedule_againprice_group.setVisibility(View.VISIBLE);
//
//        }
//        holder.fragment_schedule_againprice.setText(""+((int)(schedules.get(position).againPrice)));
        holder.channel_lv_item_butt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, PayWriteActivity.class);
                Bundle bundle=new Bundle();
                bundle.putInt("scheduleId", schedules.get(position).id);
                bundle.putString("portraitUrlSmall", schedules.get(position).portraitUrlSmall);
                bundle.putString("title", schedules.get(position).title);
                bundle.putString("teacherName", schedules.get(position).teacherName);
                bundle.putString("provinceName",schedules.get(position).provinceName);
                bundle.putString("cityName",schedules.get(position).cityName);
                bundle.putString("address", schedules.get(position).address);
                bundle.putLong("startTime", schedules.get(position).startTime);
                bundle.putLong("endTime", schedules.get(position).endTime);
                bundle.putDouble("price", schedules.get(position).price);
                bundle.putDouble("againPrice", schedules.get(position).againPrice);
                bundle.putDouble("sitInPrice", schedules.get(position).sitInPrice);
                bundle.putDouble("askPrice", schedules.get(position).askPrice);
                bundle.putString("priceType", schedules.get(position).priceType);
//                bundle.putInt("isAgain", schedules.get(position).isAgain);
                intent.putExtras(bundle);
                context.startActivity(intent);
                activity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        return convertView;
    }
}
