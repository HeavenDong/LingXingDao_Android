package com.miracleworld.lingxingdao.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.bean.NewContentMusic;
import com.miracleworld.lingxingdao.android.view.myroundedimageview.RoundedImageView;

import java.util.ArrayList;

/**
 * Created by donghaifeng on 2016/3/8.
 */
public class NewContentAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<NewContentMusic> newContentMusics;

    public  NewContentAdapter(Context context,ArrayList<NewContentMusic> newContentMusics){
        this.context=context;
        this.newContentMusics=newContentMusics;
    }

    @Override
    public int getCount() {
        return newContentMusics.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    class NewContentHolder{
        RoundedImageView img_head_item;
        TextView item_title_fragment_newcontent;
        TextView item_channl_clazz_fragment_newcontent;
        TextView item_time_fragment_newcontent;
        TextView item_channlname_fragment_newcontent;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        NewContentHolder holder;
        if(convertView==null) {
            convertView = View.inflate(context, R.layout.fragment_newcontent_item, null);
            holder=new NewContentHolder();
            holder.img_head_item= (RoundedImageView) convertView.findViewById(R.id.img_head_item);
            holder.item_title_fragment_newcontent= (TextView) convertView.findViewById(R.id.item_title_fragment_newcontent);
            holder.item_channl_clazz_fragment_newcontent= (TextView) convertView.findViewById(R.id.item_channl_clazz_fragment_newcontent);
            holder.item_time_fragment_newcontent= (TextView) convertView.findViewById(R.id.item_time_fragment_newcontent);
            holder.item_channlname_fragment_newcontent= (TextView) convertView.findViewById(R.id.item_channlname_fragment_newcontent);
            convertView.setTag(holder);
        }
        else {
            holder= (NewContentHolder) convertView.getTag();
        }
        //以下赋值

        return convertView;
    }
}
