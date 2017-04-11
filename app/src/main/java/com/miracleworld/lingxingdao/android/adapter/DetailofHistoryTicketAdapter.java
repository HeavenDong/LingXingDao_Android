package com.miracleworld.lingxingdao.android.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.bean.DetailofHictoryTicketBean;

import java.util.ArrayList;

/**
 * Created by donghaifeng on 2016/2/25.
 */
public class DetailofHistoryTicketAdapter extends BaseAdapter{

    private Context context;
    private ArrayList<DetailofHictoryTicketBean> detailofHictoryTicketBeans;
    public DetailofHistoryTicketAdapter(Context context,ArrayList<DetailofHictoryTicketBean> detailofHictoryTicketBeans) {
        this.context=context;
        this.detailofHictoryTicketBeans=detailofHictoryTicketBeans;
    }

    @Override
    public int getCount() {
        return detailofHictoryTicketBeans.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    class ViewHolderDetail{
        //标题灰
        TextView ticket_unit;
        //学员类型
        TextView student_type;
        //价格
        TextView price;
        //姓名
        TextView student_name;
        //手机号
        TextView moble;
        //身份证
        TextView card;
        //代理人
        TextView agent;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderDetail holder;
        if (convertView==null){
            convertView= View.inflate(context, R.layout.item_list_ticket_pay_history_detail, null);
            holder=new ViewHolderDetail();
            holder.ticket_unit= (TextView) convertView.findViewById(R.id.ticket_unit);
            holder.student_type= (TextView) convertView.findViewById(R.id.student_type);
            holder.price= (TextView) convertView.findViewById(R.id.price);
            holder.student_name= (TextView) convertView.findViewById(R.id.student_name);
            holder.moble= (TextView) convertView.findViewById(R.id.moble);
            holder.card= (TextView) convertView.findViewById(R.id.card);
            holder.agent=(TextView) convertView.findViewById(R.id.agent);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolderDetail) convertView.getTag();
        }

        //String转String数组  0是 新学员  1是 复训  2 旁听 3 是 提问
        if (detailofHictoryTicketBeans.get(position).buyType==0){
            holder.student_type.setText(context.getResources().getString(R.string.new_student));
        }else if (detailofHictoryTicketBeans.get(position).buyType==1){
            holder.student_type.setText(context.getResources().getString(R.string.old_student));
        }
        else if (detailofHictoryTicketBeans.get(position).buyType==2){
            holder.student_type.setText(context.getResources().getString(R.string.sitin_student));
        }
        else if (detailofHictoryTicketBeans.get(position).buyType==3){
            holder.student_type.setText(context.getResources().getString(R.string.ask_student));
        }
        //赋值
        holder.ticket_unit.setText(""+(position+1));
        holder.price.setText(""+((int)detailofHictoryTicketBeans.get(position).price));
        holder.student_name.setText(detailofHictoryTicketBeans.get(position).name);
        holder.moble.setText(detailofHictoryTicketBeans.get(position).mobile);
        holder.card.setText(detailofHictoryTicketBeans.get(position).identityCard);
        if (detailofHictoryTicketBeans.get(position).agent.equals("")){
            holder.agent.setText("暂无");
        }else{
            holder.agent.setText(detailofHictoryTicketBeans.get(position).agent);
        }

        return convertView;
    }
}
