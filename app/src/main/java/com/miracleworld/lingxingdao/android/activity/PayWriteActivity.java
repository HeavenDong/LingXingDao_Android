package com.miracleworld.lingxingdao.android.activity;

import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.RequestParams;
import com.miracleworld.lingxingdao.android.App;
import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.activity.pay.TicketForPayActivity;
import com.miracleworld.lingxingdao.android.adapter.PayWriteAdapter;
import com.miracleworld.lingxingdao.android.base.BaseActivity;
import com.miracleworld.lingxingdao.android.bean.BuyTicketInfo;
import com.miracleworld.lingxingdao.android.http.NetClient;
import com.miracleworld.lingxingdao.android.http.NetResponseHandler;
import com.miracleworld.lingxingdao.android.http.Url;
import com.miracleworld.lingxingdao.android.options.ImageLoaderOptions;
import com.miracleworld.lingxingdao.android.utils.CommanUtil;
import com.miracleworld.lingxingdao.android.utils.SharedPreUtils;
import com.miracleworld.lingxingdao.android.view.DefinedSingleToast;
import com.miracleworld.lingxingdao.android.view.myroundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by donghaifeng on 2016/2/18.
 */

public class PayWriteActivity extends BaseActivity{

    private int scheduleId;
    private String portraitUrlSmall;
    private String title;
    private String teacherName;
    private String provinceName;
    private String cityName;
    private String address;
    private long startTime;
    private long endTime;
    private double price;
    private double againPrice;
    private double sitInPrice;
    private double askPrice;
    private String priceType;
//    private int isAgain;

    //控件
    //头部加号的容器
    private RelativeLayout paywrite_head_jia_group;
    //悬浮的加号的容器
    private RelativeLayout paywrite_jia_tip_group;
    //列表
    private ListView paywrite_lv;
    //底部全部价格
    private TextView paywrite_sum_prices;
    //底部的总张数
    private TextView paywrite_sum_pager;

    //列表数据集合
    private ArrayList<BuyTicketInfo> buyTicketInfos;
    private PayWriteAdapter adapter;

    private  double sumPrices;
    //确认提交按钮
    private TextView paywrite_submit_pay;
    private String tableName="customer";

    @Override
    protected void initView() {
        //维持数据库的40条数据
        //获取数据库的集合
        Cursor cursor= App.getSqlManager().seleteAll(tableName,new String[]{"id","name","mobile","identitycard","agent"},"id desc");
        ArrayList<BuyTicketInfo> temp=new ArrayList<BuyTicketInfo>();
        if (cursor!=null&&cursor.getCount()>0){
            Log.e("jxf", "customer数据库有数据");
            while(cursor.moveToNext()) {
                BuyTicketInfo bean=new BuyTicketInfo();
                bean.id=cursor.getInt(0);
                bean.name=cursor.getString(1);
                bean.mobile=cursor.getString(2);
                bean.identityCard=cursor.getString(3);
                bean.agent=cursor.getString(4);
                temp.add(bean);
            }
            cursor.close();
            if (temp.size()>40){
                int id=temp.get(39).id;
                App.getSqlManager().deleteAllWhere(tableName, "id<?", new String[]{""+id});
//                for (int i=40;i<temp.size();i=40){
//                    temp.remove(i);
//                }
//                Log.e("jxf","customer页从数据库取数据：有数据：集合数量"+temp.size());
            }
        }else{
            Log.e("jxf", "customer数据库没有数据");
            cursor.close();
        }
        App.getSqlManager().close();

        //老师头布局以及悬浮头
        View head=View.inflate(this, R.layout.activity_paywrite_head_teacher, null);
        RoundedImageView paywrite_teacher_header= (RoundedImageView) head.findViewById(R.id.paywrite_head_teacher_header);
        TextView paywrite_head_teachername= (TextView) head.findViewById(R.id.paywrite_head_teachername);
        TextView paywrite_head_schedulename= (TextView) head.findViewById(R.id.paywrite_head_schedulename);
        TextView paywrite_head_scheduletime= (TextView) head.findViewById(R.id.paywrite_head_scheduletime);
        TextView paywrite_head_schedule_address= (TextView) head.findViewById(R.id.paywrite_head_schedule_address);
        //新学员
        TextView paywrite_head_schedule_newprice= (TextView) head.findViewById(R.id.paywrite_head_schedule_newprice);
        //复训学员
        LinearLayout paywrite_head_againprice_group= (LinearLayout)head.findViewById(R.id.paywrite_head_againprice_group);
        TextView paywrite_head_schedule_oldprice= (TextView) head.findViewById(R.id.paywrite_head_schedule_oldprice);
        //旁听学员
        LinearLayout paywrite_head_sitinprice_group= (LinearLayout)head.findViewById(R.id.paywrite_head_sitinprice_group);
        TextView paywrite_head_schedule_sitinprice= (TextView) head.findViewById(R.id.paywrite_head_schedule_sitinprice);
        //提问学员
        LinearLayout paywrite_head_askprice_group= (LinearLayout)head.findViewById(R.id.paywrite_head_askprice_group);
        TextView paywrite_head_schedule_askprice= (TextView) head.findViewById(R.id.paywrite_head_schedule_askprice);

        View headTip=View.inflate(this, R.layout.activity_paywrite_head_tip, null);
        paywrite_head_jia_group= (RelativeLayout) headTip.findViewById(R.id.paywrite_head_jia_group);
        ImageView paywrite_head_jia= (ImageView) headTip.findViewById(R.id.paywrite_head_jia);
        paywrite_head_jia.setOnClickListener(this);

        getBundle();
        //给头部控件赋值
        ImageLoader.getInstance().displayImage(portraitUrlSmall, paywrite_teacher_header, ImageLoaderOptions.headOptions);
        paywrite_head_teachername.setText(teacherName);
        paywrite_head_schedulename.setText(title);
        paywrite_head_scheduletime.setText(CommanUtil.transhms(startTime, "yyyy.MM.dd")+"-"+CommanUtil.transhms(endTime,"yyyy.MM.dd"));
        paywrite_head_schedule_address.setText(provinceName+cityName+"("+address+")");
        paywrite_head_schedule_newprice.setText(""+((int)price));
        //String转String数组  0是 新学员  1是 复训  2 旁听 3 是 提问
        if (priceType.equals("")){
            //表示只有 新学员的价格：原本就被隐藏了
//            holder.fragment_schedule_againprice_group.setVisibility(View.GONE);

        }else{
            String[] typeString=priceType.split(",");
            Log.e("jxf","打印数组的长度"+typeString.length);
            for (int i=0;i<typeString.length;i++) {
                //表示有复训
                if (typeString[i].equals("1")) {
                    paywrite_head_againprice_group.setVisibility(View.VISIBLE);
                    paywrite_head_schedule_oldprice.setText("" + ((int)againPrice));
                }
                //表示有旁听
                else if (typeString[i].equals("2")){
                    paywrite_head_sitinprice_group.setVisibility(View.VISIBLE);
                    paywrite_head_schedule_sitinprice.setText("" + ((int)sitInPrice));
                }
                //表示有提问
                else if (typeString[i].equals("3")){
                    paywrite_head_askprice_group.setVisibility(View.VISIBLE);
                    paywrite_head_schedule_askprice.setText("" + ((int)askPrice));
                }
            }
        }
        //isAgain 0 否  1是
//        if (isAgain==0){
//            paywrite_head_againprice_group.setVisibility(View.GONE);
//        }
//        else{
//            paywrite_head_againprice_group.setVisibility(View.VISIBLE);
//            paywrite_head_schedule_oldprice.setText(""+((int)againPrice));
//        }
        //初始化除头部外其他控件
        //返回键
        RelativeLayout paywrite_left= (RelativeLayout) findViewById(R.id.paywrite_left);
        paywrite_left.setOnClickListener(this);
        //悬浮框的控件
        paywrite_jia_tip_group= (RelativeLayout) findViewById(R.id.paywrite_jia_tip_group);
        //悬浮加号
        ImageView paywrite_jia_tip_iv= (ImageView) findViewById(R.id.paywrite_jia_tip_iv);
        paywrite_jia_tip_iv.setOnClickListener(this);
        //listview
        paywrite_lv= (ListView) findViewById(R.id.paywrite_lv);
        paywrite_lv.addHeaderView(head);
        paywrite_lv.addHeaderView(headTip);
        paywrite_lv.setOverScrollMode(View.OVER_SCROLL_NEVER);
        //给适配器结合赋值：初始进来是没有值的
        buyTicketInfos=new ArrayList<BuyTicketInfo>();
        adapter=new PayWriteAdapter(this,this,buyTicketInfos,priceType);
        paywrite_lv.setAdapter(adapter);
        paywrite_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position<2){
                    Log.e("jxf","头部的点击事件不做任何的处理");
                }
                else{
                    Intent intent = new Intent(PayWriteActivity.this, AddCustomerActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putInt("position", position - 2);
                    bundle.putInt("id", buyTicketInfos.get(position - 2).id);
                    bundle.putString("name", buyTicketInfos.get(position - 2).name);
                    bundle.putString("mobile", buyTicketInfos.get(position - 2).mobile);
                    bundle.putString("identityCard", buyTicketInfos.get(position - 2).identityCard);
                    bundle.putString("agent", buyTicketInfos.get(position - 2).agent);
                    intent.putExtras(bundle);
                    startActivityForResult(intent, 540);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }
            }
        });
        paywrite_lv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem >= 1) {
                    paywrite_jia_tip_group.setVisibility(View.VISIBLE);
                } else {

                    paywrite_jia_tip_group.setVisibility(View.GONE);
                }
            }
        });
        //底部全部价格
        paywrite_sum_prices= (TextView) findViewById(R.id.paywrite_sum_prices);
        //底部的总张数
        paywrite_sum_pager= (TextView) findViewById(R.id.paywrite_sum_pager);
        changeSum();
        //底部提交支付  paywrite_submit_pay
        paywrite_submit_pay= (TextView) findViewById(R.id.paywrite_submit_pay);
        paywrite_submit_pay.setOnClickListener(this);
    }

    private void getBundle() {
        Bundle bundle=getIntent().getExtras();
        scheduleId=bundle.getInt("scheduleId");
        portraitUrlSmall = bundle.getString("portraitUrlSmall");
        title=bundle.getString("title");
        teacherName=bundle.getString("teacherName");
        provinceName=bundle.getString("provinceName");
        cityName=bundle.getString("cityName");
        address=bundle.getString("address");
        startTime = bundle.getLong("startTime");
        endTime=bundle.getLong("endTime");
        price = bundle.getDouble("price");
        againPrice=bundle.getDouble("againPrice");
        sitInPrice=bundle.getDouble("sitInPrice");
        askPrice=bundle.getDouble("askPrice");
        priceType=bundle.getString("priceType");
//        isAgain=bundle.getInt("isAgain");
    }

    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_paywrite);
    }

    @Override
    protected void onClickEvent(View view) {
        switch (view.getId()){
            case R.id.paywrite_left:
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                break;
            //头部的加号
            case R.id.paywrite_head_jia:
                Intent intent=new Intent(this,CustomerActivity.class);
                intent.putExtra("list", (Serializable) buyTicketInfos);
                startActivityForResult(intent,960);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            //悬浮的加号
            case R.id.paywrite_jia_tip_iv:
                Intent intent1=new Intent(this,CustomerActivity.class);
                intent1.putExtra("list", (Serializable) buyTicketInfos);
                startActivityForResult(intent1,960);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            //底部提交支付按钮
            case R.id.paywrite_submit_pay:
                if (buyTicketInfos.size()==0){
                    DefinedSingleToast.showToast(this,getResources().getString(R.string.sorry_no));
                    break;
                }else if (buyTicketInfos.size()>20){
                    DefinedSingleToast.showToast(this,getResources().getString(R.string.sorry_20));
                    break;
                }else {
                    JSONArray array=new JSONArray();
                    for (int i=0;i<buyTicketInfos.size();i++){
                        JSONObject jo = new JSONObject();
                        String name=buyTicketInfos.get(i).name;
                        String mobile=buyTicketInfos.get(i).mobile;
                        String identityCard=buyTicketInfos.get(i).identityCard;
                        String agent=null;
                        if (buyTicketInfos.get(i).agent.equals("暂无")){
                            agent="";
                        }else{
                            agent=buyTicketInfos.get(i).agent;
                        }

                        int buyType=buyTicketInfos.get(i).identify;
                        double price1 = 0;
                        if (buyType==0){
                            price1=price;
                        }
                        else if (buyType==1){
                            price1=againPrice;
                        }
                        else if (buyType==2){
                            price1=sitInPrice;
                        }
                        else if (buyType==3){
                            price1=askPrice;
                        }

                        try {
                            jo.put("name",name);
                            jo.put("mobile",mobile);
                            jo.put("identityCard",identityCard);
                            jo.put("price",price1);
                            jo.put("agent",agent);
                            jo.put("buyType",buyType);
                            array.put(jo);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    Log.e("jxf","打印jasonarray"+array.toString());
                    paywrite_submit_pay.setClickable(false);
                    //提交服务器
                    submitAndGetOrder(array.toString());
                }
                break;
        }
    }


//    //默认价格是新学员价格  1:新学员  2：复训学员
//    @Override
//    protected void onClickEvent(View view) {
//        switch (view.getId()){
//            //返回键
//            case R.id.paywrite_left:
//                finish();
//                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
//                break;
//            //新学员
//            case R.id.paywrite_newstudent_tv:
//                if (identity==1){
//                    Log.e("jxf","本身就是新学员：点击不做任何操作");
//                }
//                else{
//                    paywrite_newstudent_tv.setBackgroundResource(R.drawable.oldstudent);
//                    paywrite_newstudent_tv.setTextColor(getResources().getColor(R.color.white));
//                    paywrite_oldstudent_tv.setBackgroundResource(R.drawable.newstudent);
//                    paywrite_oldstudent_tv.setTextColor(getResources().getColor(R.color.schedule_student_color));
//                }
//                identity=1;
//                paywrite_page_price_old.setText(((int)price)+"");
//                paywrite_page_price_new.setText(((int)price)+"");
//                paywrite_sum_prices.setText(((int)price*pageNum)+"");
//                break;
//            //复训学员
//            case R.id.paywrite_oldstudent_tv:
//                if (identity==2){
//                    Log.e("jxf","本身就是复训学员：点击不做任何操作");
//                }
//                else{
//                    paywrite_newstudent_tv.setBackgroundResource(R.drawable.newstudent);
//                    paywrite_newstudent_tv.setTextColor(getResources().getColor(R.color.schedule_student_color));
//                    paywrite_oldstudent_tv.setBackgroundResource(R.drawable.oldstudent);
//                    paywrite_oldstudent_tv.setTextColor(getResources().getColor(R.color.white));
//                }
//                identity=2;
//                paywrite_page_price_old.setText(((int)againPrice)+"");
//                paywrite_page_price_new.setText(((int)againPrice)+"");
//                paywrite_sum_prices.setText(((int)againPrice*pageNum)+"");
//            break;
//
//            //中间窗口
//            //减号
//            case R.id.paywrite_jian_hao_old:
//                if (pageNum==1){
//                    break;
//                }
//                else{
//                    pageNum=pageNum-1;
//                    paywrite_page_number_old.setText(pageNum+"");
//                    paywrite_page_number_new.setText(pageNum+"");
//                    if (identity==1){
//                        paywrite_sum_prices.setText(((int)price*pageNum)+"");
//                    }
//                    if (identity==2){
//                        paywrite_sum_prices.setText(((int)againPrice*pageNum)+"");
//                    }
//                    if (pageNum==1){
//                        paywrite_jian_hao_old.setImageResource(R.drawable.jian_1);
//                        paywrite_jian_hao_new.setImageResource(R.drawable.jian_1);
//                    }
//                    if (pageNum!=1){
//                        paywrite_jian_hao_old.setImageResource(R.drawable.jian);
//                        paywrite_jian_hao_new.setImageResource(R.drawable.jian);
//                    }
//                    if (pageNum==5){
//                        paywrite_jia_hao_old.setImageResource(R.drawable.jia_1);
//                        paywrite_jia_hao_new.setImageResource(R.drawable.jia_1);
//                    }
//                    if (pageNum!=5){
//                        paywrite_jia_hao_old.setImageResource(R.drawable.jia);
//                        paywrite_jia_hao_new.setImageResource(R.drawable.jia);
//                    }
//                    for (int i=pageNum;i<linearLayouts.length;i++){
//                        linearLayouts[i].setVisibility(View.GONE);
//                    }
//                }
//                break;
//            //加号
//            case R.id.paywrite_jia_hao_old:
//                if (pageNum==5){
//                    break;
//                }
//                else{
//                    pageNum=pageNum+1;
//                    paywrite_page_number_old.setText(pageNum+"");
//                    paywrite_page_number_new.setText(pageNum+"");
//                    if (identity==1){
//                        paywrite_sum_prices.setText(((int)price*pageNum)+"");
//                    }
//                    if (identity==2){
//                        paywrite_sum_prices.setText(((int)againPrice*pageNum)+"");
//                    }
//                    if (pageNum==1){
//                        paywrite_jian_hao_old.setImageResource(R.drawable.jian_1);
//                        paywrite_jian_hao_new.setImageResource(R.drawable.jian_1);
//                    }
//                    if (pageNum!=1){
//                        paywrite_jian_hao_old.setImageResource(R.drawable.jian);
//                        paywrite_jian_hao_new.setImageResource(R.drawable.jian);
//                    }
//                    if (pageNum==5){
//                        paywrite_jia_hao_old.setImageResource(R.drawable.jia_1);
//                        paywrite_jia_hao_new.setImageResource(R.drawable.jia_1);
//                    }
//                    if (pageNum!=5){
//                        paywrite_jia_hao_old.setImageResource(R.drawable.jia);
//                        paywrite_jia_hao_new.setImageResource(R.drawable.jia);
//                    }
//                    for (int i=0;i<pageNum;i++){
//                        linearLayouts[i].setVisibility(View.VISIBLE);
//                    }
//                }
//
//                break;
//
//            //悬浮
//            //减号
//            case R.id.paywrite_jian_hao_new:
//                if (pageNum==1){
//                    break;
//                }
//                else{
//                    pageNum=pageNum-1;
//                    paywrite_page_number_old.setText(pageNum+"");
//                    paywrite_page_number_new.setText(pageNum+"");
//                    if (identity==1){
//                        paywrite_sum_prices.setText(((int)price*pageNum)+"");
//                    }
//                    if (identity==2){
//                        paywrite_sum_prices.setText(((int) againPrice * pageNum) + "");
//                    }
//                    if (pageNum==1){
//                        paywrite_jian_hao_old.setImageResource(R.drawable.jian_1);
//                        paywrite_jian_hao_new.setImageResource(R.drawable.jian_1);
//                    }
//                    if (pageNum!=1){
//                        paywrite_jian_hao_old.setImageResource(R.drawable.jian);
//                        paywrite_jian_hao_new.setImageResource(R.drawable.jian);
//                    }
//                    if (pageNum==5){
//                        paywrite_jia_hao_old.setImageResource(R.drawable.jia_1);
//                        paywrite_jia_hao_new.setImageResource(R.drawable.jia_1);
//                    }
//                    if (pageNum!=5){
//                        paywrite_jia_hao_old.setImageResource(R.drawable.jia);
//                        paywrite_jia_hao_new.setImageResource(R.drawable.jia);
//                    }
//                    for (int i=pageNum;i<linearLayouts.length;i++){
//                        linearLayouts[i].setVisibility(View.GONE);
//                    }
//
//                }
//                break;
//            //加号
//            case R.id.paywrite_jia_hao_new:
//                if (pageNum==5){
//                    break;
//                }
//                else{
//                    pageNum=pageNum+1;
//                    paywrite_page_number_old.setText(pageNum+"");
//                    paywrite_page_number_new.setText(pageNum+"");
//                    if (identity==1){
//                        paywrite_sum_prices.setText(((int)price*pageNum)+"");
//                    }
//                    if (identity==2){
//                        paywrite_sum_prices.setText(((int)againPrice*pageNum)+"");
//                    }
//                    if (pageNum==1){
//                        paywrite_jian_hao_old.setImageResource(R.drawable.jian_1);
//                        paywrite_jian_hao_new.setImageResource(R.drawable.jian_1);
//                    }
//                    if (pageNum!=1){
//                        paywrite_jian_hao_old.setImageResource(R.drawable.jian);
//                        paywrite_jian_hao_new.setImageResource(R.drawable.jian);
//                    }
//                    if (pageNum==5){
//                        paywrite_jia_hao_old.setImageResource(R.drawable.jia_1);
//                        paywrite_jia_hao_new.setImageResource(R.drawable.jia_1);
//                    }
//                    if (pageNum!=5){
//                        paywrite_jia_hao_old.setImageResource(R.drawable.jia);
//                        paywrite_jia_hao_new.setImageResource(R.drawable.jia);
//                    }
//                    for (int i=0;i<pageNum;i++){
//                        linearLayouts[i].setVisibility(View.VISIBLE);
//                    }
//                }
//                break;
//
//            //提交支付
//            //  EditTextnames   EditTextphones   EditTextidcards
//            case R.id.paywrite_submit_pay:
//                for (int i=0;i<pageNum;i++){
//                    String name=EditTextnames[i].getText().toString().trim();
//                    String phone=EditTextphones[i].getText().toString().trim();
//                    String idcard=EditTextidcards[i].getText().toString().trim();
//                    //校验姓名
//                    if (name.equals("")){
//                        DefinedSingleToast.showToast(PayWriteActivity.this,getResources().getString(R.string.name_must_have));
//                        isBreak=false;
//                        break;
//                    }
//                    else{
//                        if (!CommanUtil.getStrLength(name)){
//                            DefinedSingleToast.showToast(PayWriteActivity.this,getResources().getString(R.string.name_must_right));
//                            isBreak=false;
//                            break;
//                        }
//                    }
//                    //校验电话号码
//                    if (phone.equals("")){
//                        DefinedSingleToast.showToast(PayWriteActivity.this,getResources().getString(R.string.phone_must_have));
//                        isBreak=false;
//                        break;
//                    }
//                    else{
//                        if (!CommanUtil.isMobilePhone(phone)){
//                            DefinedSingleToast.showToast(PayWriteActivity.this,getResources().getString(R.string.phone_must_right));
//                            isBreak=false;
//                            break;
//                        }
//                    }
//                    //校验身份证号
//                    if (idcard.equals("")){
//                        DefinedSingleToast.showToast(PayWriteActivity.this,getResources().getString(R.string.idcard_must_have));
//                        isBreak=false;
//                        break;
//                    }
//                    else{
//                        if (!CommanUtil.personIdValidation(idcard)){
//                            DefinedSingleToast.showToast(PayWriteActivity.this,getResources().getString(R.string.idcard_must_right));
//                            isBreak=false;
//                            break;
//                        }
//                    }
//                    isBreak=true;
//                }
//
//                if (isBreak){
//                    JSONArray array=new JSONArray();
//                    for (int i=0;i<pageNum;i++){
//                        JSONObject jo = new JSONObject();
//                        String name=EditTextnames[i].getText().toString().trim();
//                        String mobile=EditTextphones[i].getText().toString().trim();
//                        String identityCard=EditTextidcards[i].getText().toString().trim();
//                        double price1 = 0;
//                        if (identity==1){
//                            price1=price;
//                        }
//                        else if (identity==2){
//                            price1=againPrice;
//                        }
//                        try {
//                            jo.put("name",name);
//                            jo.put("mobile",mobile);
//                            jo.put("identityCard",identityCard);
//                            jo.put("price",price1);
//                            array.put(jo);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                    //确定总价格
//                    if (identity==1){
//                        sumPrice=price*pageNum;
//                    }
//                    else if (identity==2){
//                        sumPrice=againPrice*pageNum;
//                    }
//                    Log.e("jxf","打印jasonarray"+array.toString());
//                    paywrite_submit_pay.setClickable(false);
//                    //提交服务器
//                    submitAndGetOrder(array.toString());
//                }
//
//                break;
//        }
//    }

    private void submitAndGetOrder(String json) {
        ConnectivityManager mConnectivity = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info == null || !mConnectivity.getBackgroundDataSetting()) {
            Log.e("jxf", "支付填写没网");
            paywrite_submit_pay.setClickable(true);
            DefinedSingleToast.showToast(this, getResources().getString(R.string.toast_net));
        }
        else {
            Log.e("jxf", "有网开始支付填写并请求订单");
            RequestParams params = new RequestParams();
            params.put("userId", SharedPreUtils.get(this, "user_id", 0));
            params.put("courseId",scheduleId);
            params.put("amount",sumPrices);
            params.put("strData",json);
            Log.e("jxf", "支付填写订单上传参数" + params);
            NetClient.headPost(PayWriteActivity.this, Url.PAY_WRITE, params, new NetResponseHandler() {
                @Override
                public void onResponse(String json) {
                    Log.e("jxf", "返回json" + json);
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        String status = jsonObject.optString("status");
                        String errorCode = jsonObject.optString("errorCode");
                        //请求成功
                        if (status.equals("1")) {
                            JSONObject jsonObject2 = jsonObject.optJSONObject("datas");
                            //订单号
                            String ordersn = jsonObject2.getString("ordersn");
                            int orderId = jsonObject2.getInt("id");
                            sumPrices = jsonObject2.getDouble("amount");
                            Log.e("jxf", "服务器返回订单号=" + ordersn + "服务器返回订单orderId=" + orderId + "amount价钱=" + sumPrices);
                            paywrite_submit_pay.setClickable(true);
                            //携带数据跳转
                            Intent intent = new Intent(PayWriteActivity.this, TicketForPayActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putInt("scheduleId", scheduleId);
                            bundle.putString("portraitUrlSmall", portraitUrlSmall);
                            bundle.putString("title", title);
                            bundle.putString("teacherName", teacherName);
                            bundle.putString("provinceName", provinceName);
                            bundle.putString("cityName", cityName);
                            bundle.putString("address", address);
                            bundle.putLong("startTime", startTime);
                            bundle.putLong("endTime", endTime);
                            bundle.putDouble("sumPrice", sumPrices);
                            bundle.putString("ordersn", ordersn);
                            bundle.putInt("orderId", orderId);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        }
                        //请求失败
                        else {
                            Log.e("jxf", "服务器订单号失败");
                            if (errorCode.equals(31000)){
                                DefinedSingleToast.showToast(PayWriteActivity.this,"余票不足");
                                paywrite_submit_pay.setClickable(true);
                            }else{
                                DefinedSingleToast.showToast(PayWriteActivity.this, getResources().getString(R.string.toast_net));
                                paywrite_submit_pay.setClickable(true);
                            }


                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("jxf", "异常了");
                        DefinedSingleToast.showToast(PayWriteActivity.this, getResources().getString(R.string.toast_net));
                        paywrite_submit_pay.setClickable(true);
                    }

                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    Log.e("jxf", "服务器生成订单号失败onfail" + throwable.toString());
                    DefinedSingleToast.showToast(PayWriteActivity.this, getResources().getString(R.string.toast_net));
                    paywrite_submit_pay.setClickable(true);
                }
            });
        }

    }


//    @Override
//    public void onWindowFocusChanged(boolean hasFocus) {
//        super.onWindowFocusChanged(hasFocus);
//        if (hasFocus){
////            //悬浮距Top的高低
////            int height=CommanUtil.dp2px(this,43);
//            //中间视图距离上边的位置
//            int heightmiddle=paywrite_head_jia_group.getTop();
//            //参与比较的距离
////            compareheight=heightmiddle-height;
//            compareheight=heightmiddle;
//        }
//    }

//    @Override
//    public void onScroll(int scrollY) {
//        if (scrollY>=compareheight){
//            tip_new.setVisibility(View.VISIBLE);
//        }
//        else{
//            tip_new.setVisibility(View.GONE);
//        }
//    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }

    public void remove(int position){
        buyTicketInfos.remove(position);
        adapter.notifyDataSetChanged();
        changeSum();
    }

    public void changeItemIdentify(int position,int identify){
        buyTicketInfos.get(position).identify=identify;
        adapter.notifyDataSetChanged();
        changeSum();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==960){
            if (resultCode==961){
                buyTicketInfos.removeAll(buyTicketInfos);
                buyTicketInfos.addAll((ArrayList<BuyTicketInfo>) data.getSerializableExtra("list"));
                adapter.notifyDataSetChanged();
                changeSum();
            }
        }
        else if (requestCode==540){
            //修改了 要修改内存中集合
            if (resultCode==541){
                Bundle bundle=data.getBundleExtra("reback");
                int position=bundle.getInt("position");
                String name=bundle.getString("name");
                String mobile=bundle.getString("mobile");
                String identityCard=bundle.getString("identityCard");
                String agent=bundle.getString("agent");
                buyTicketInfos.get(position).name=name;
                buyTicketInfos.get(position).mobile=mobile;
                buyTicketInfos.get(position).identityCard=identityCard;
                buyTicketInfos.get(position).agent=agent;
                adapter.notifyDataSetChanged();
            }
        }
    }


    private void changeSum(){
        sumPrices = 0;
        if (buyTicketInfos.size()>0){
            for (int i=0;i<buyTicketInfos.size();i++){
                //新学员
                if (buyTicketInfos.get(i).identify==0){
                    sumPrices=sumPrices+price;
                }
                //复训学员
                else if (buyTicketInfos.get(i).identify==1){
                    sumPrices=sumPrices+againPrice;
                }
                else if (buyTicketInfos.get(i).identify==2){
                    sumPrices=sumPrices+sitInPrice;
                }
                else if (buyTicketInfos.get(i).identify==3){
                    sumPrices=sumPrices+askPrice;
                }
            }
        }
        paywrite_sum_prices.setText(""+((int)sumPrices));
        paywrite_sum_pager.setText("("+(buyTicketInfos.size())+"张)");
    }
}
