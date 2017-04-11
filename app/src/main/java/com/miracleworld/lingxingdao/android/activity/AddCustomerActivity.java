package com.miracleworld.lingxingdao.android.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miracleworld.lingxingdao.android.App;
import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.base.BaseActivity;
import com.miracleworld.lingxingdao.android.utils.CommanUtil;
import com.miracleworld.lingxingdao.android.view.DefinedSingleToast;

/**
 * Created by donghaifeng on 2016/2/27.
 */
public class AddCustomerActivity extends BaseActivity{

    //姓名
    private EditText add_username;
    //姓名
    private EditText add_mobile;
    //姓名
    private EditText add_card;
    //姓名
    private EditText add_agent;
    //判断完全符合的boolean  true表示 是断开了 不用修改数据库
    private boolean isBreak;
    private String tableName="customer";

    //商业传参的参数
    private boolean isBundle;
    private int bundleposition;
    private int bundleid;
    private String bundlename;
    private String bundlemobile;
    private String bundleidentityCard;
    private String bundleagent;

    @Override
    protected void initView() {
        //标题
        TextView customer_my_title_tv= (TextView) findViewById(R.id.customer_my_title_tv);
        //返回键
        RelativeLayout opento_addcustomer_title_left= (RelativeLayout) findViewById(R.id.opento_addcustomer_title_left);
        opento_addcustomer_title_left.setOnClickListener(this);
        //确认键
        RelativeLayout sure_add= (RelativeLayout) findViewById(R.id.sure_add);
        sure_add.setOnClickListener(this);
        //姓名
        add_username= (EditText) findViewById(R.id.add_username);
        //姓名
        add_mobile= (EditText) findViewById(R.id.add_mobile);
        //姓名
        add_card= (EditText) findViewById(R.id.add_card);
        //姓名
        add_agent= (EditText) findViewById(R.id.add_agent);
        isBundle=false;
        Bundle bundle=getIntent().getExtras();
        if (bundle!=null){
            customer_my_title_tv.setText(getResources().getString(R.string.edit_new_customer));
            isBundle=true;
            bundleposition=bundle.getInt("position");
            bundleid=bundle.getInt("id");
            bundlename=bundle.getString("name");
            bundlemobile=bundle.getString("mobile");
            bundleidentityCard=bundle.getString("identityCard");
            bundleagent=bundle.getString("agent");
            if (bundleagent.equals("暂无")){
                bundleagent="";
            }
            add_username.setText(bundlename);
            add_mobile.setText(bundlemobile);
            add_card.setText(bundleidentityCard);
            add_agent.setText(bundleagent);
        }
        else{
            customer_my_title_tv.setText(getResources().getString(R.string.add_new_customer));
        }
    }

    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_add_customer_open_newlayout);
    }

    @Override
    protected void onClickEvent(View view) {
        switch (view.getId()){
            case R.id.opento_addcustomer_title_left:
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                break;
            case R.id.sure_add:
                isBreak=false;
                Log.e("jxf","点击确认了");
                //与数据库字段一致
                String name=add_username.getText().toString().trim();
                Log.e("jxf","name"+name);
                String mobile=add_mobile.getText().toString().trim();
                Log.e("jxf","mobile"+mobile);
                String identitycard=add_card.getText().toString().trim();
                Log.e("jxf","identitycard"+identitycard);
                String agent=add_agent.getText().toString().trim();
                Log.e("jxf","agent"+agent);

                //校验姓名
                Log.e("jxf","校验姓名");
                if (name.equals("")){
                    DefinedSingleToast.showToast(this, getResources().getString(R.string.name_must_have));
                    isBreak=true;
                }
                else{
                    if (!CommanUtil.getStrLength(name)){
                        DefinedSingleToast.showToast(this,getResources().getString(R.string.name_must_right));
                        isBreak=true;
                    }
                }
                //校验电话号码
                Log.e("jxf","校验电话号码");
                if (mobile.equals("")){
                    DefinedSingleToast.showToast(this,getResources().getString(R.string.phone_must_have));
                    isBreak=true;
                }
                else{
                    if (!CommanUtil.isMobilePhone(mobile)){
                        DefinedSingleToast.showToast(this,getResources().getString(R.string.phone_must_right));
                        isBreak=true;
                    }
                }
                //校验身份证号
                Log.e("jxf","校验身份证号");
                if (identitycard.equals("")){
                    DefinedSingleToast.showToast(this,getResources().getString(R.string.idcard_must_have));
                    isBreak=true;
                }
                else{
                    if (!CommanUtil.personIdValidation(identitycard)){
                        DefinedSingleToast.showToast(this,getResources().getString(R.string.idcard_must_right));
                        isBreak=true;
                    }
                }
                //校验代理人
                Log.e("jxf","校验代理人");
                if (agent.equals("")){
                    agent="暂无";
                }
                else{
                    if (!CommanUtil.getStrLength(agent)) {
                        DefinedSingleToast.showToast(this,getResources().getString(R.string.agent_must_right));
                        isBreak=true;
                    }
                }
                if (!isBreak){
                    if (isBundle){
                        //修改数据库
                        Log.e("jxf","修改数据库");
                        ContentValues values=new ContentValues();
                        values.put("name",name);
                        values.put("mobile", mobile);
                        values.put("identitycard", identitycard);
                        values.put("agent", agent);
                        App.getSqlManager().updateOne(tableName, values, "id=?", new String[]{"" + bundleid});
                        App.getSqlManager().close();
                        Intent intent=new Intent();
                        Bundle bundle=new Bundle();
                        bundle.putInt("position", bundleposition);
                        bundle.putString("name",name);
                        bundle.putString("mobile",mobile);
                        bundle.putString("identityCard",identitycard);
                        bundle.putString("agent",agent);
                        intent.putExtra("reback",bundle);
                        this.setResult(541, intent);
                        AddCustomerActivity.this.finish();
                        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    }else{
                        Log.e("jxf","插入数据库");
                        //操作数据库
                        ContentValues values=new ContentValues();
                        values.put("name",name);
                        values.put("mobile",mobile);
                        values.put("identitycard",identitycard);
                        values.put("agent", agent);
                        App.getSqlManager().insert(tableName, values);
                        App.getSqlManager().close();
                        Intent intent=new Intent();
                        this.setResult(581, intent);
                        Log.e("jxf","页面结束");
                        AddCustomerActivity.this.finish();
                        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }
}
