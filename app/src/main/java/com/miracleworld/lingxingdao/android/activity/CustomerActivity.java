package com.miracleworld.lingxingdao.android.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miracleworld.lingxingdao.android.App;
import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.adapter.CustomerActivityAdapter;
import com.miracleworld.lingxingdao.android.base.BaseActivity;
import com.miracleworld.lingxingdao.android.bean.BuyTicketInfo;
import com.miracleworld.lingxingdao.android.view.DefinedSingleToast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by donghaifeng on 2016/2/27.
 */
public class CustomerActivity extends BaseActivity {

    private ArrayList<BuyTicketInfo> transInCustomers;
    private ArrayList<BuyTicketInfo> sqlCustomers;
    private ArrayList<BuyTicketInfo> rebackCustomers;
    private ArrayList<BuyTicketInfo> tempBackUpSql;
    private String tableName="customer";
    private CustomerActivityAdapter adapter;

    @Override
    protected void initView() {
        //集合初始化
        transInCustomers= (ArrayList<BuyTicketInfo>) getIntent().getSerializableExtra("list");
        //为了还原临时保存的 数据库集合
        tempBackUpSql=new ArrayList<BuyTicketInfo>();
        sqlCustomers=new ArrayList<BuyTicketInfo>();
        rebackCustomers=new ArrayList<BuyTicketInfo>();

        //获取数据库的集合
        Cursor cursor= App.getSqlManager().seleteAll(tableName,new String[]{"id","name","mobile","identitycard","agent"},"id desc");
        if (cursor!=null&&cursor.getCount()>0){
            Log.e("jxf", "customer数据库有数据");
            while(cursor.moveToNext()) {
                BuyTicketInfo bean=new BuyTicketInfo();
                BuyTicketInfo bean1=new BuyTicketInfo();
                bean.id=cursor.getInt(0);
                bean1.id=cursor.getInt(0);
                bean.name=cursor.getString(1);
                bean1.name=cursor.getString(1);
                bean.mobile=cursor.getString(2);
                bean1.mobile=cursor.getString(2);
                bean.identityCard=cursor.getString(3);
                bean1.identityCard=cursor.getString(3);
                bean.agent=cursor.getString(4);
                bean1.agent=cursor.getString(4);
                //默认的勾选框是不勾选的
                bean.isCheck=false;
                //0是 新学员  1是 复训  2 旁听 3 是 提问
                bean.identify=0;
                sqlCustomers.add(bean);
                tempBackUpSql.add(bean1);
            }
            cursor.close();
//            if (sqlCustomers.size()>40){
//                int id=sqlCustomers.get(39).id;
//                App.getSqlManager().deleteOne(tableName, "id<?", new String[]{""+id});
//                for (int i=40;i<sqlCustomers.size();i=40){
//                    sqlCustomers.remove(i);
//                }
//                Log.e("jxf","customer页从数据库取数据：有数据：集合数量"+sqlCustomers.size());
//            }
        }else{
            Log.e("jxf", "customer数据库没有数据");
            cursor.close();
        }
        App.getSqlManager().close();

        //操作两个集合 打钩
        //数据库没有数据:前面的也不可能有
        if (sqlCustomers.size()==0){
            //维持空数据：直接刷空界面
        }
        //数据库有数据
        else{
            //前面的界面没有数据
            if (transInCustomers.size()==0){
                //维持数据库数据ischeck不变 刷界面
            }
            //前面的有数据 for循环嵌套
            else{
                //遍历传进来的数据集合
                for (int i=0;i<transInCustomers.size();i++){
                    for (int z=0;z<sqlCustomers.size();z++){
                        //id是数据库中的主键 维持唯一
                        if (transInCustomers.get(i).id==sqlCustomers.get(z).id){
                            sqlCustomers.get(z).isCheck=true;
                            sqlCustomers.get(z).identify=transInCustomers.get(i).identify;
                        }

                    }
                }
            }
        }
        //传进来的集合和数据库集合 内存状态 ischeck 整理完毕
        adapter=new CustomerActivityAdapter(this,this,sqlCustomers);
        //查找控件
        //返回键
        RelativeLayout back_butt_add_customer= (RelativeLayout) findViewById(R.id.back_butt_add_customer);
        back_butt_add_customer.setOnClickListener(this);
        //确认按钮
        TextView sure_add_customer= (TextView) findViewById(R.id.sure_add_customer);
        sure_add_customer.setOnClickListener(this);
        //添加联系人按钮
        TextView butt_add_customer= (TextView) findViewById(R.id.butt_add_customer);
        butt_add_customer.setOnClickListener(this);
        ListView add_customer_lv= (ListView) findViewById(R.id.add_customer_lv);
        add_customer_lv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        add_customer_lv.setAdapter(adapter);
        //以上界面的展示完成了
    }

    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_add_customer_layout);
    }

    @Override
    protected void onClickEvent(View view) {
        switch (view.getId()){
            //返回键
            case R.id.back_butt_add_customer:
                backUpSql();
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                break;
            //确认按钮
            case R.id.sure_add_customer:
                rebackCustomers.removeAll(rebackCustomers);

                //返回内存中保存集合的数据reback  set结果码；还有判断数量20 要判断是不是0
                for (int i=0;i<sqlCustomers.size();i++){
                    if (sqlCustomers.get(i).isCheck){
                        rebackCustomers.add(sqlCustomers.get(i));
                    }
                }
                if (rebackCustomers.size()==0){
                    DefinedSingleToast.showToast(this,getResources().getString(R.string.sorry_no));
                }
                else if (rebackCustomers.size()>20){
                    DefinedSingleToast.showToast(this,getResources().getString(R.string.sorry_20));
                }
                else{
                    Intent intent=new Intent();
                    intent.putExtra("list", (Serializable) rebackCustomers);
                    this.setResult(961, intent);
                    CustomerActivity.this.finish();
                    overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                }
                break;
            case R.id.butt_add_customer:
                Intent intent=new Intent(CustomerActivity.this,AddCustomerActivity.class);
                startActivityForResult(intent,580);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
        }
    }

    @Override
    public void onBackPressed() {
        backUpSql();
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }


    public void changeIsCheck(int position,boolean isCheck){
        sqlCustomers.get(position).isCheck=isCheck;
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==560){
            Log.e("jxf","得到响应requestCode==560");
            if (resultCode==541){
                Bundle bundle=data.getBundleExtra("reback");
                int position=bundle.getInt("position");
                String name=bundle.getString("name");
                String mobile=bundle.getString("mobile");
                String identityCard=bundle.getString("identityCard");
                String agent=bundle.getString("agent");
                sqlCustomers.get(position).name=name;
                sqlCustomers.get(position).mobile=mobile;
                sqlCustomers.get(position).identityCard=identityCard;
                sqlCustomers.get(position).agent=agent;
                //身份和ischeck维持不变
                adapter.notifyDataSetChanged();
            }
        }
        if (requestCode==580){
            if (resultCode==581){
                Cursor cursor= App.getSqlManager().seleteOne(tableName, new String[]{"id", "name", "mobile", "identitycard", "agent"}, "id desc", "" + 1);
                int i=0;
                while(cursor.moveToNext()) {
                    Log.e("jxf","打印 添加完返回 取cursor的次数"+(i+1));
                    BuyTicketInfo bean=new BuyTicketInfo();
                    bean.id=cursor.getInt(0);
                    bean.name=cursor.getString(1);
                    bean.mobile=cursor.getString(2);
                    bean.identityCard=cursor.getString(3);
                    bean.agent=cursor.getString(4);
                    //默认的勾选框是勾选的
                    bean.isCheck=true;
                    //1 默认新学员
                    bean.identify=0;
                    sqlCustomers.add(0,bean);
                }
                cursor.close();
                App.getSqlManager().close();
                adapter.notifyDataSetChanged();
            }
        }
    }

    private void backUpSql(){
        if (tempBackUpSql.size()==0){
            Log.e("jxf", "备份数据库size=0删除所有保持为0");
            App.getSqlManager().deleteAll(tableName);
        }else{
            Log.e("jxf", "备份数据库size>0更新操作");
            for (int i=0;i<tempBackUpSql.size();i++){
                ContentValues values=new ContentValues();
                values.put("name",tempBackUpSql.get(i).name);
                Log.e("jxf","打印名字"+tempBackUpSql.get(i).name);
                values.put("mobile", tempBackUpSql.get(i).mobile);
                values.put("identitycard", tempBackUpSql.get(i).identityCard);
                values.put("agent", tempBackUpSql.get(i).agent);
                Log.e("jxf","打印当前的id"+tempBackUpSql.get(i).id);
                App.getSqlManager().updateOne(tableName, values, "id=?", new String[]{"" + tempBackUpSql.get(i).id});
                App.getSqlManager().close();
            }
            App.getSqlManager().deleteAllWhere(tableName, "id>?", new String[]{"" + tempBackUpSql.get(0).id});
        }
        Log.e("jxf", "备份数据库完毕");
        App.getSqlManager().close();
    }

}
