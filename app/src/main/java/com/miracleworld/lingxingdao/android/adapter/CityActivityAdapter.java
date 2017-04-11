package com.miracleworld.lingxingdao.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.bean.CityBean;

import java.util.ArrayList;

/**
 * Created by donghaifeng on 2015/12/24.
 */
public class CityActivityAdapter extends BaseAdapter {
    private Context context;
    //需要集合 list
    private ArrayList<CityBean> citys;
    public CityActivityAdapter(Context context,ArrayList<CityBean> citys){
        this.context=context;
        this.citys=citys;
    }

    @Override
    public int getCount() {
        return citys.size();
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
        TextView city_lv_item_tv;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            convertView=View.inflate(context, R.layout.activity_city_lv_item,null);
            holder=new ViewHolder();

            holder.city_lv_item_tv= (TextView) convertView.findViewById(R.id.city_lv_item_tv);
            convertView.setTag(holder);
        }
        else {
            holder= (ViewHolder) convertView.getTag();
        }

        //以下赋值
        holder.city_lv_item_tv.setText(citys.get(position).cityName);

        return convertView;
    }
}
