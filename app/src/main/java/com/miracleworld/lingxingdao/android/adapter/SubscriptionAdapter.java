package com.miracleworld.lingxingdao.android.adapter;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.bean.MusicLesson;
import com.miracleworld.lingxingdao.android.options.ImageLoaderOptions;
import com.miracleworld.lingxingdao.android.utils.CommanUtil;
import com.miracleworld.lingxingdao.android.view.myroundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by donghaifeng on 2016/1/23.
 */
public class SubscriptionAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<MusicLesson> lessons;
    public SubscriptionAdapter(Context context,ArrayList<MusicLesson> lessons){
        this.context=context;
        this.lessons=lessons;
    }
    @Override
    public int getCount() {
        return lessons.size();
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
        RoundedImageView audio_photo;
        TextView lesson_content;
        TextView lesson_category;
        TextView lesson_creattime;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            convertView=View.inflate(context, R.layout.activity_subscription_lv_item,null);
            holder=new ViewHolder();
            holder.audio_photo= (RoundedImageView) convertView.findViewById(R.id.audio_photo);
            holder.lesson_content= (TextView) convertView.findViewById(R.id.lesson_content);
            holder.lesson_category= (TextView) convertView.findViewById(R.id.lesson_category);
            holder.lesson_creattime= (TextView) convertView.findViewById(R.id.lesson_creattime);
            convertView.setTag(holder);
        }
        else {
            holder= (ViewHolder) convertView.getTag();
        }

        //以下赋值
        if (lessons.get(position).isCheck){
            convertView.setBackgroundColor(context.getResources().getColor(R.color.isclick_color));
        }
        else{
            convertView.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
        ImageLoader.getInstance().displayImage(lessons.get(position).pictureUrlSmall,holder.audio_photo, ImageLoaderOptions.playOption);
        holder.lesson_content.setText(lessons.get(position).title);
        holder.lesson_category.setText(lessons.get(position).categoryName);
        Log.e("jxf","列表条目时间"+lessons.get(position));
        holder.lesson_creattime.setText(CommanUtil.transhms(lessons.get(position).creatTime, "MM-dd HH:mm:ss"));
        return convertView;
    }
}
