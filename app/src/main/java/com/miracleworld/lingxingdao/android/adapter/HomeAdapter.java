package com.miracleworld.lingxingdao.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.bean.HomeNewest;
import com.miracleworld.lingxingdao.android.options.ImageLoaderOptions;
import com.miracleworld.lingxingdao.android.utils.CommanUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;

/**
 * Created by donghaifeng on 2015/12/18.
 */
public class HomeAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<HomeNewest> homeNewests;
    //备注：需要集合 list
    public HomeAdapter(Context context,ArrayList<HomeNewest> homeNewests){
        this.context=context;
        this.homeNewests=homeNewests;
    }
    @Override
    public int getCount() {
        return homeNewests.size();
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
        //条目图片
        ImageView iv_fragment_news_item;
        //条目标题
        TextView title_fragment_news_item;
        //条目时间
        TextView time_fragment_news_item;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            convertView=View.inflate(context, R.layout.fragment_news_lv_item,null);
            holder=new ViewHolder();
            holder.iv_fragment_news_item= (ImageView) convertView.findViewById(R.id.iv_fragment_news_item);
            holder.title_fragment_news_item= (TextView) convertView.findViewById(R.id.title_fragment_news_item);
            holder.time_fragment_news_item= (TextView) convertView.findViewById(R.id.time_fragment_news_item);
            convertView.setTag(holder);
        }
        else {
            holder= (ViewHolder) convertView.getTag();
        }
        //以下赋值
        ImageLoader.getInstance().displayImage(homeNewests.get(position).icon,holder.iv_fragment_news_item, ImageLoaderOptions.newsoptions);
        //被点击了 发灰
        if (homeNewests.get(position).isCheck){
            //灰色content_introduction_font_color
            holder.title_fragment_news_item.setTextColor(context.getResources().getColor(R.color.content_introduction_font_color));
        }
        else{
            //home_news_title_font_color黑色
            holder.title_fragment_news_item.setTextColor(context.getResources().getColor(R.color.home_news_title_font_color));
        }
        holder.title_fragment_news_item.setText(homeNewests.get(position).title);
        holder.time_fragment_news_item.setText(CommanUtil.transhms(homeNewests.get(position).creatTime, "MM-dd HH:mm"));

        return convertView;
    }
}
