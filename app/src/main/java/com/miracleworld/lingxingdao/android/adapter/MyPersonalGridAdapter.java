package com.miracleworld.lingxingdao.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.bean.MyPersonalGridBean;
import com.miracleworld.lingxingdao.android.options.ImageLoaderOptions;
import com.miracleworld.lingxingdao.android.view.myroundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

/**
 * 个人资料 GridView 适配器
 */
public class MyPersonalGridAdapter extends BaseAdapter {
    private  List<MyPersonalGridBean> gridList;
    private Context context;

    public MyPersonalGridAdapter(Context context, List<MyPersonalGridBean> gridList) {
        this.context=context;
        this.gridList=gridList;
    }

    

    @Override
    public int getCount() {
        return gridList.size();
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
        PersonalViewHold holder;
        if (convertView==null){
            holder=new PersonalViewHold();
            convertView= View.inflate(context,R.layout.my_personal_grid_item,null);
            holder.personal_grid_item_head= (RoundedImageView) convertView.findViewById(R.id.personal_grid_item_head);
            holder.personal_grid_item_name= (TextView) convertView.findViewById(R.id.personal_grid_item_name);
            convertView.setTag(holder);

        }else {
            holder= (PersonalViewHold) convertView.getTag();
        }
        ImageLoader.getInstance().displayImage(gridList.get(position).portraitUrlSmall,holder.personal_grid_item_head, ImageLoaderOptions.headOptions);
        holder.personal_grid_item_name.setText(gridList.get(position).teacherName);
        return convertView;
    }

    private class PersonalViewHold {
        RoundedImageView personal_grid_item_head;
        TextView personal_grid_item_name;
    }
}
