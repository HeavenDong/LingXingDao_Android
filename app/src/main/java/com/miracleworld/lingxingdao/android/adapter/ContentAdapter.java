package com.miracleworld.lingxingdao.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.bean.Content;
import com.miracleworld.lingxingdao.android.options.ImageLoaderOptions;
import com.miracleworld.lingxingdao.android.view.myroundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by donghaifeng on 2015/12/18.
 */
public class ContentAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Content> contents;
    public ContentAdapter(Context context,ArrayList<Content> contents){
        this.context=context;
        this.contents=contents;
    }
    @Override
    public int getCount() {
        return contents.size();
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
        //条目老师圆形头像
        RoundedImageView fragment_content_lv_item_iv_head;
        //条目研讨会老师名字
        TextView fragment_content_lv_item_tv_name;
        //条目老师课程价钱
        TextView fragment_content_lv_item_tv_price;
        //条目老师一句话介绍
        TextView fragment_content_lv_item_tv_introduce;
        //条目分类容器:不使用容器 使用字符串加空格添加
        TextView fragment_content_lv_item_tv_categorys;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            convertView=View.inflate(context, R.layout.fragment_content_lv_item,null);
            holder=new ViewHolder();
            holder.fragment_content_lv_item_iv_head= (RoundedImageView) convertView.findViewById(R.id.fragment_content_lv_item_iv_head);
            holder.fragment_content_lv_item_tv_name= (TextView) convertView.findViewById(R.id.fragment_content_lv_item_tv_name);
            holder.fragment_content_lv_item_tv_price= (TextView) convertView.findViewById(R.id.fragment_content_lv_item_tv_price);
            holder.fragment_content_lv_item_tv_introduce= (TextView) convertView.findViewById(R.id.fragment_content_lv_item_tv_introduce);
            holder.fragment_content_lv_item_tv_categorys= (TextView) convertView.findViewById(R.id.fragment_content_lv_item_tv_categorys);
            convertView.setTag(holder);
        }
        else{
            holder= (ViewHolder) convertView.getTag();
        }
        //以下赋值
        //被点击：
        if (contents.get(position).isCheck){
            convertView.setBackgroundColor(context.getResources().getColor(R.color.isclick_color));
        }
        //没被点击：白色
        else{
            convertView.setBackgroundColor(context.getResources().getColor(R.color.white));
        }
        ImageLoader.getInstance().displayImage(contents.get(position).portraitUrlSmall, holder.fragment_content_lv_item_iv_head, ImageLoaderOptions.headOptions);
        holder.fragment_content_lv_item_tv_name.setText(contents.get(position).nickname);
        holder.fragment_content_lv_item_tv_price.setText((contents.get(position).pricerange)+context.getResources().getString(R.string.subscription_everymouth));
        holder.fragment_content_lv_item_tv_introduce.setText(contents.get(position).introduce);
        String categorys=contents.get(position).catgoryName.replace(",","   ");
        holder.fragment_content_lv_item_tv_categorys.setText(categorys);
        return convertView;
    }
}
