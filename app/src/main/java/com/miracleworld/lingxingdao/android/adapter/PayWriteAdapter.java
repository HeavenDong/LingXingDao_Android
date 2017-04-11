package com.miracleworld.lingxingdao.android.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.activity.PayWriteActivity;
import com.miracleworld.lingxingdao.android.bean.BuyTicketInfo;
import com.miracleworld.lingxingdao.android.view.DefinedSingleToast;

import java.util.ArrayList;

/**
 * Created by donghaifeng on 2016/2/26.
 */
public class  PayWriteAdapter extends BaseAdapter {

    private PayWriteActivity mActivity;
    private Context context;
    private ArrayList<BuyTicketInfo> buyTicketInfos;
    private String  priceType;
    //0不支持 1支持
    public PayWriteAdapter(PayWriteActivity mActivity,Context context,ArrayList<BuyTicketInfo> buyTicketInfos,String  priceType){
        this.mActivity=mActivity;
        this.context=context;
        this.buyTicketInfos=buyTicketInfos;
        this.priceType=priceType;
    }
    @Override
    public int getCount() {
        return buyTicketInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    class PayWriteViewHolder{
        //删除点击区域
        ImageView delect_customer;
        TextView customer_name;
        TextView write_mobile;
        TextView write_card;
        TextView write_agent;
        TextView type_students;
        //切换复训点击区域
        RelativeLayout change_write;
        ImageView blue_arrow;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        PayWriteViewHolder holder;
        if (convertView==null){
            convertView=View.inflate(context, R.layout.layout_listitem_paywrite,null);
            holder=new PayWriteViewHolder();
            holder.delect_customer= (ImageView) convertView.findViewById(R.id.delect_customer);
            holder.customer_name= (TextView) convertView.findViewById(R.id.customer_name);
            holder.write_mobile= (TextView) convertView.findViewById(R.id.write_mobile);
            holder.write_card= (TextView) convertView.findViewById(R.id.write_card);
            holder.write_agent= (TextView) convertView.findViewById(R.id.write_agent);
            holder.type_students= (TextView) convertView.findViewById(R.id.type_students);
            holder.change_write= (RelativeLayout) convertView.findViewById(R.id.change_write);
            holder.blue_arrow= (ImageView) convertView.findViewById(R.id.blue_arrow);
            convertView.setTag(holder);
        }
        else{
            holder= (PayWriteViewHolder) convertView.getTag();
        }
        //赋值
        holder.customer_name.setText(buyTicketInfos.get(position).name);
        holder.write_mobile.setText(buyTicketInfos.get(position).mobile);
        holder.write_card.setText(buyTicketInfos.get(position).identityCard);
        holder.write_agent.setText(buyTicketInfos.get(position).agent);
        //1是新学员  2是复训学员
        if (buyTicketInfos.get(position).identify==0){
            holder.type_students.setText(context.getResources().getString(R.string.new_student));
        }else if (buyTicketInfos.get(position).identify==1){
            holder.type_students.setText(context.getResources().getString(R.string.old_student));
        }else if (buyTicketInfos.get(position).identify==2){
            holder.type_students.setText(context.getResources().getString(R.string.sitin_student));
        }else if (buyTicketInfos.get(position).identify==3){
            holder.type_students.setText(context.getResources().getString(R.string.ask_student));
        }
        //点击相关
        holder.delect_customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.remove(position);
            }
        });
        //isagain 0 不支持 1支持
        //0是 新学员  1是 复训  2 旁听 3 是 提问
        if (priceType.equals("")){
            holder.blue_arrow.setVisibility(View.GONE);

        }else{
            holder.blue_arrow.setVisibility(View.VISIBLE);
        }
        holder.change_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (priceType.equals("")){
                    Log.e("jxf","不支持点击是不做任何的操作");
                }else{
                    Log.e("jxf", "弹出单选框");
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("请选择学员身份：");
                    //与对象中的对应起来 1是新学员 2是复选学员  identify 1shi xin   2shi fuxun
                    String[] typeString=priceType.split(",");
                    Log.e("jxf","打印数组的长度"+typeString.length);
                    final String[] items = new String[1+typeString.length];
                    items[0]="新学员";
                    for (int i=0;i<typeString.length;i++){
                        if (typeString[i].equals("1")){
                            items[i+1]="复训学员";
                        }
                        else if (typeString[i].equals("2")){
                            items[i+1]="旁听学员";
                        }
                        else if (typeString[i].equals("3")){
                            items[i+1]="提问学员";
                        }
                    }
                    builder.setSingleChoiceItems(items,-1, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //which也是从0开始
                            DefinedSingleToast.showToast(context, "您选择了" + items[which]);
                            int identify=0;
                            if (items[which].equals("新学员")){
                                identify=0;
                            }
                            else if (items[which].equals("复训学员")){
                                identify=1;
                            }
                            else if (items[which].equals("旁听学员")){
                                identify=2;
                            }
                            else if (items[which].equals("提问学员")){
                                identify=3;
                            }
                            if (identify==buyTicketInfos.get(position).identify){
                                Log.e("jxf","身份没有发生改变");
                            }
                            else{
                                mActivity.changeItemIdentify(position,identify);
                            }
                            dialog.dismiss();
                        }
                    });

                    builder.show();
                }
            }
        });

        return convertView;
    }
}

