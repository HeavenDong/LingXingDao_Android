package com.miracleworld.lingxingdao.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.bean.Category;

import java.util.ArrayList;

/**
 * Created by donghaifeng on 2015/12/23.
 */

public class MainActivityRightlvAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<Category> categorys;
    public MainActivityRightlvAdapter(Context context,ArrayList<Category> categorys){
        this.context=context;
        this.categorys=categorys;
    }

    @Override
    public int getCount() {
        return categorys.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    class  ViewHolder{
        TextView teachercategory_lv_item_tv;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            convertView=View.inflate(context, R.layout.main_right_teachercategory_lv_item,null);
            holder=new ViewHolder();
            holder.teachercategory_lv_item_tv= (TextView) convertView.findViewById(R.id.teachercategory_lv_item_tv);
            convertView.setTag(holder);
        }
        else {
            holder= (ViewHolder) convertView.getTag();
        }

        //以下赋值
        holder.teachercategory_lv_item_tv.setText(categorys.get(position).name);
        if (categorys.get(position).isClick){
            holder.teachercategory_lv_item_tv.setTextColor(context.getResources().getColor(R.color.content_drawerlayout_category_font_color_click));
        }
        else{
            holder.teachercategory_lv_item_tv.setTextColor(context.getResources().getColor(R.color.content_drawerlayout_category_font_color_unclick));
        }



        return convertView;
    }
}
