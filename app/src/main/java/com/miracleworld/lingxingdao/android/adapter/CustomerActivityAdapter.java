package com.miracleworld.lingxingdao.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.activity.AddCustomerActivity;
import com.miracleworld.lingxingdao.android.activity.CustomerActivity;
import com.miracleworld.lingxingdao.android.bean.BuyTicketInfo;

import java.util.ArrayList;

/**
 * Created by donghaifeng on 2016/2/27.
 */
public class CustomerActivityAdapter extends BaseAdapter {
    private CustomerActivity mActivity;
    private Context context;
    private ArrayList<BuyTicketInfo> sqlCustomers;

    public CustomerActivityAdapter(CustomerActivity mActivity,Context context,ArrayList<BuyTicketInfo> sqlCustomers){
        this.mActivity=mActivity;
        this.context=context;
        this.sqlCustomers=sqlCustomers;
    }

    @Override
    public int getCount() {
        return sqlCustomers.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    class CustomerViewHolder{
        RelativeLayout box_select_group;
        CheckBox box_select;
        TextView add_name;
        TextView add_mobile;
        TextView add_card;
        TextView add_agent;
        ImageView change_add;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        CustomerViewHolder holder;
        if (convertView==null){
            convertView=View.inflate(context, R.layout.layout_listitem_add_customer,null);
            holder=new CustomerViewHolder();
            holder.box_select_group= (RelativeLayout) convertView.findViewById(R.id.box_select_group);
            holder.box_select= (CheckBox) convertView.findViewById(R.id.box_select);
            holder.add_name= (TextView) convertView.findViewById(R.id.add_name);
            holder.add_mobile= (TextView) convertView.findViewById(R.id.add_mobile);
            holder.add_card= (TextView) convertView.findViewById(R.id.add_card);
            holder.add_agent= (TextView) convertView.findViewById(R.id.add_agent);
            holder.change_add= (ImageView) convertView.findViewById(R.id.change_add);
            convertView.setTag(holder);
        }
        else{
            holder= (CustomerViewHolder) convertView.getTag();
        }
        //赋值
        holder.box_select.setChecked(sqlCustomers.get(position).isCheck);
        holder.add_name.setText(sqlCustomers.get(position).name);
        holder.add_mobile.setText(sqlCustomers.get(position).mobile);
        holder.add_card.setText(sqlCustomers.get(position).identityCard);
        holder.add_agent.setText(sqlCustomers.get(position).agent);
        holder.box_select_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.changeIsCheck(position, !sqlCustomers.get(position).isCheck);
            }
        });

        holder.change_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddCustomerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("position", position);
                bundle.putInt("id", sqlCustomers.get(position).id);
                bundle.putString("name", sqlCustomers.get(position).name);
                bundle.putString("mobile", sqlCustomers.get(position).mobile);
                bundle.putString("identityCard", sqlCustomers.get(position).identityCard);
                bundle.putString("agent", sqlCustomers.get(position).agent);
                intent.putExtras(bundle);
                mActivity.startActivityForResult(intent, 560);
                mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });

        return convertView;
    }
}
