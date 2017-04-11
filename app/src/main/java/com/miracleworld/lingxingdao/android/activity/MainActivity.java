package com.miracleworld.lingxingdao.android.activity;


import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.miracleworld.lingxingdao.android.App;
import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.activity.my.MyPersonEditActivity;
import com.miracleworld.lingxingdao.android.activity.pay.HistoryForPayActivity;
import com.miracleworld.lingxingdao.android.activity.personal.MyPersonalDataActivity;
import com.miracleworld.lingxingdao.android.adapter.MainActivityRightlvAdapter;
import com.miracleworld.lingxingdao.android.base.BaseActivity;
import com.miracleworld.lingxingdao.android.bean.Category;
import com.miracleworld.lingxingdao.android.fragment.ConnectFragment;
import com.miracleworld.lingxingdao.android.fragment.ContentFragment;
import com.miracleworld.lingxingdao.android.fragment.FirstFragment;
import com.miracleworld.lingxingdao.android.fragment.HomeFragment;
import com.miracleworld.lingxingdao.android.fragment.MallFragment;
import com.miracleworld.lingxingdao.android.fragment.ScheduleFragment;
import com.miracleworld.lingxingdao.android.http.NetClient;
import com.miracleworld.lingxingdao.android.http.NetResponseHandler;
import com.miracleworld.lingxingdao.android.http.Url;
import com.miracleworld.lingxingdao.android.options.ImageLoaderOptions;
import com.miracleworld.lingxingdao.android.utils.SharedPreUtils;
import com.miracleworld.lingxingdao.android.view.DefinedSingleToast;
import com.miracleworld.lingxingdao.android.view.TempSingleToast;
import com.miracleworld.lingxingdao.android.view.myroundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by donghaifeng on 2015/12/16
 */
public class MainActivity extends BaseActivity {
    //备注：打开关闭的动画！：左右两侧findviewbyid：提供内容碎片刷新的功能
    //打开个人设置中心：退出登录的请求码
    public final static int REQUEST_CODE=901;
    private ImageView[] imageViews;
    private TextView[] textViews;
    private LinearLayout[] layouts;
    //正常图片的资源id
    private int imageNormal[] = new int[]{
            R.drawable.home_tab_gray,R.drawable.content_tab_gray,R.drawable.connect_tab_gray,R.drawable.schedule_tab_gray,R.drawable.mall_tab_gray
    };
    //点击以后图片
    private int imagePressed[] = new int[]{
            R.drawable.home_tab_normal, R.drawable.content_tab_narmal, R.drawable.connect_tab_normal, R.drawable.schedule_tab_normal,R.drawable.mall_tab_normal
    };
    //底部字体的id
    private int textIds[] = new int[]{
            R.id.main_bottom_home_txt,R.id.main_bottom_content_txt, R.id.main_bottom_connect_txt, R.id.main_bottom_schedule_txt, R.id.main_bottom_mall_txt
    };
    //底部图片的id
    private int imageIds[] = new int[]{
            R.id.main_bottom_home_img,R.id.main_bottom_content_img, R.id.main_bottom_connect_img, R.id.main_bottom_schedule_img, R.id.main_bottom_mall_img
    };

    private Fragment[] fragments;
    private int grey, deeppowder;
    private int curTabPosition = 0;
    private int index = 0; // 当前Fragment的index
    private FragmentManager fragmentManager;

    //双击退出
    private long lastPress;
    private long backPressThreshold=2000;
    //头部标题名称
    private String[] titles;


    //代码所需全局变量
    private RoundedImageView main_iv_title_left;
    private TextView tv_title;
    private ImageView main_iv_title_right;
    private DrawerLayout drawer_layout;
    private LinearLayout main_left;
    private LinearLayout main_right;

    //右边数据的集合
    private ArrayList<Category> categorys;
    private MainActivityRightlvAdapter adapter;
    //右边的控件
    private ListView main_right_lv_category;

    //左边控件
    private RoundedImageView main_drawer_left_head;
    private TextView main_drawer_left_name;
    private TextView main_drawer_left_username;
    private TextView main_drawer_left_phone;
    // TODO: 2016/3/8 标记左侧有没有新收件 
    private ImageView is_have_newinfo;
    private int right_position_pre=0;

    //右侧列表的刷新操作fragment对象
    private Fragment contentFragment;
    @Override
    protected void initView() {
        //一进入就请求右侧列表：
        //右侧控件
        //右侧listview:要有条目点击事件：要有adapter
        //右边数据的集合
        categorys=new ArrayList<Category>();
        main_right_lv_category= (ListView) findViewById(R.id.main_right_lv_category);
        main_right_lv_category.setOverScrollMode(View.OVER_SCROLL_NEVER);
        //右边的侧滑界面列表
        adapter=new MainActivityRightlvAdapter(this,categorys);
        main_right_lv_category.setAdapter(adapter);
        String list= (String) SharedPreUtils.get(getApplicationContext(), "list", "");
        Log.e("jxf", "主页面流程打印" + list);
        //解析本地取出的
        takeoutCategory(list);
        //判断网络情况
        ConnectivityManager mConnectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info == null || !mConnectivity.getBackgroundDataSetting()) {
            Log.e("jxf", "启动页请求分类列表：没网");
        }
        else {
            Log.e("jxf", "启动页请求分类列表：有网");
            requestCategory();
        }
        //new String[]{"灵性岛","内容","链接","研讨会","商城"};
        //头部标题
        titles= this.getResources().getStringArray(R.array.main_titles);
        main_iv_title_left= (RoundedImageView) findViewById(R.id.main_iv_title_left);
        tv_title= (TextView) findViewById(R.id.tv_title);
        main_iv_title_right= (ImageView) findViewById(R.id.main_iv_title_right);
        //左边点击事件
        //左侧个人头像
        main_drawer_left_head= (RoundedImageView) findViewById(R.id.main_drawer_left_head);
        //名字
        main_drawer_left_name= (TextView) findViewById(R.id.main_drawer_left_name);
        //用户名
        main_drawer_left_username= (TextView) findViewById(R.id.main_drawer_left_username);
        //电话号码
        main_drawer_left_phone= (TextView) findViewById(R.id.main_drawer_left_phone);
        //左侧的收件箱是否有新信息
        is_have_newinfo= (ImageView) findViewById(R.id.is_have_newinfo);
        //左侧赋值,本地获取个人信息
        changeLeftPersonInfo();

        //个人信息
        LinearLayout main_drawer_left_persondata= (LinearLayout) findViewById(R.id.main_drawer_left_persondata);
        main_drawer_left_persondata.setOnClickListener(this);
        //个人信息设置
        LinearLayout main_drawer_left_persondata_setting= (LinearLayout) findViewById(R.id.main_drawer_left_persondata_setting);
        main_drawer_left_persondata_setting.setOnClickListener(this);
        //支付历史
        LinearLayout main_drawer_left_pay_history= (LinearLayout) findViewById(R.id.main_drawer_left_pay_history);
        main_drawer_left_pay_history.setOnClickListener(this);
        //订阅管理
        LinearLayout main_drawer_left_subscription_manager= (LinearLayout) findViewById(R.id.main_drawer_left_subscription_manager);
        main_drawer_left_subscription_manager.setOnClickListener(this);
        LinearLayout main_drawer_left_inbox= (LinearLayout) findViewById(R.id.main_drawer_left_inbox);
        main_drawer_left_inbox.setOnClickListener(this);
        //左侧返回：关闭侧滑
        ImageView main_left_goback= (ImageView) findViewById(R.id.main_left_goback);
        main_left_goback.setOnClickListener(this);

        //得到DrawerLayout
        drawer_layout= (DrawerLayout) findViewById(R.id.drawer_layout);
        //关闭滑动
        drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        drawer_layout.setDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                Log.e("jxf", "抽屉关闭了一个，将滑动解锁");
                drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                Log.e("jxf", "抽屉关闭了一个，将滑动锁定");
                drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                if (newState == DrawerLayout.STATE_DRAGGING) {
                    Log.e("jxf", "滑动状态时，将滑动锁定");
                    drawer_layout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                }

            }
        });
        //得到左右两边的布局
        main_left= (LinearLayout) findViewById(R.id.main_left);
        main_right= (LinearLayout) findViewById(R.id.main_right);
        //界面显示
        //右侧点击事件
        main_right_lv_category.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //获取点击的集合数据
                Log.e("jxf", "点击了右侧列表的" + categorys.get(position).id);
                Log.e("jxf","之前点击的条目"+right_position_pre);
                //刷新fragment了
                if (categorys.get(position).id==0){
                    ((ContentFragment)contentFragment).refreshIsCheck(false);
                }
                else{
                    ((ContentFragment)contentFragment).refreshIsCheck(true);
                }
                //不等的时候才刷新内容列表和右侧列表
                if (right_position_pre!=categorys.get(position).id){
                    ((ContentFragment)contentFragment).refreshContent(categorys.get(position).id);
                    //点击的时候切换位置记录
                    right_position_pre=position;
                    //遍历改变颜色
                    for (int i = 0; i < categorys.size(); i++) {
                        if (i == position) {
                            categorys.get(i).isClick = true;
                        } else {
                            categorys.get(i).isClick = false;
                        }
                    }
                    //切换字体颜色
                    adapter.notifyDataSetChanged();
                    Log.e("jxf", "main右侧列表刷新");
                }
                //关闭右侧列表
                drawer_layout.closeDrawer(main_right);
            }
        });

        //底部导航
        // 文字默认的颜色
        grey = getResources().getColor(R.color.bottom_font_color_unclick);
        // 文字被选中时的颜色
        deeppowder = getResources().getColor(R.color.bottom_font_color_click);

        layouts = new LinearLayout[5];
        //home
        layouts[0] = (LinearLayout) findViewById(R.id.main_bottom_home_ll);

        //内容
        layouts[1] = (LinearLayout) findViewById(R.id.main_bottom_content_ll);
        //链接
        layouts[2] = (LinearLayout) findViewById(R.id.main_bottom_connect_ll);
        //研讨会
        layouts[3] = (LinearLayout) findViewById(R.id.main_bottom_schedule_ll);
        //商城
        layouts[4] = (LinearLayout) findViewById(R.id.main_bottom_mall_ll);
        //遍历添加点击事件
        for (int i = 0; i < 5; i++) {
            layouts[i].setOnClickListener(this);
        }

        imageViews = new ImageView[5];

        for (int i = 0; i < 5; i++) {
            imageViews[i] = (ImageView) findViewById(imageIds[i]);
        }

        textViews = new TextView[5];
        for (int i = 0; i < 5; i++) {
            textViews[i] = (TextView) findViewById(textIds[i]);
        }

        //添加fragment
        //找到父控件
        FrameLayout main_content_frame= (FrameLayout) findViewById(R.id.main_content_frame);
        main_content_frame.requestDisallowInterceptTouchEvent(true);
        Fragment firstFragment=new FirstFragment();
        contentFragment=new ContentFragment();
        Fragment connectFragment=new ConnectFragment();
        Fragment scheduleFragment=new ScheduleFragment();
        Fragment mallFragment=new MallFragment();
        fragments = new Fragment[]{firstFragment,contentFragment, connectFragment, scheduleFragment, mallFragment};
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.main_content_frame, firstFragment)
//                .add(R.id.main_content_frame, contentFragment)
//                .add(R.id.main_content_frame, connectFragment)
//                .add(R.id.main_content_frame, scheduleFragment)
//                .add(R.id.main_content_frame, mallFragment)
                //.hide(contentFragment).hide(connectFragment).hide(scheduleFragment).hide(mallFragment)
                .show(firstFragment).commit();
        selectTab(curTabPosition);
        selectTitle(curTabPosition);
        drawer_layout.removeView(main_right);
    }

    /*
    *修改左侧个人信息
    * */
    private void changeLeftPersonInfo() {
        String usersmallavatar=(String)SharedPreUtils.get(this, "user_small_avatar", "");
        String userName = (String)SharedPreUtils.get(this, "user_name", "");
        String usernickname = (String)SharedPreUtils.get(this, "user_nick_name", "");
        String usermobile = (String)SharedPreUtils.get(this, "user_mobile", "");
        ImageLoader.getInstance().displayImage(usersmallavatar, main_drawer_left_head, ImageLoaderOptions.headOptions);
        ImageLoader.getInstance().displayImage(usersmallavatar, main_iv_title_left, ImageLoaderOptions.smallheadOptions);
        if (userName.equals("")){
            Log.e("jxf","修改左侧的user_name未设置");
            main_drawer_left_username.setText(getResources().getString(R.string.not_set));
        }
        else{
            Log.e("jxf","修改左侧的user_name设置新的");
            main_drawer_left_username.setText(userName);
        }
        Log.e("jxf","修改左侧的user_nick_name");
        main_drawer_left_name.setText(usernickname);

        main_drawer_left_phone.setText(usermobile);
    }

    private void selectTitle(int position) {
        tv_title.setText(titles[position]);
        switch (position){
            //首页：左
            case 0:
                main_iv_title_left.setVisibility(View.VISIBLE);
                main_iv_title_left.setClickable(true);
                main_iv_title_right.setVisibility(View.INVISIBLE);
                main_iv_title_right.setClickable(false);
                main_iv_title_left.setOnClickListener(this);
                break;
            //内容：左右
            case 1:
                main_iv_title_left.setVisibility(View.VISIBLE);
                main_iv_title_left.setClickable(true);
                main_iv_title_right.setVisibility(View.VISIBLE);
                main_iv_title_right.setClickable(true);
                main_iv_title_left.setOnClickListener(this);
                main_iv_title_right.setOnClickListener(this);
                break;
            //链接：无
            case 2:
                main_iv_title_left.setVisibility(View.INVISIBLE);
                main_iv_title_left.setClickable(false);
                main_iv_title_right.setVisibility(View.INVISIBLE);
                main_iv_title_right.setClickable(false);

                break;

            //研讨会：左
            case 3:
                main_iv_title_left.setVisibility(View.VISIBLE);
                main_iv_title_left.setClickable(true);
                main_iv_title_right.setVisibility(View.INVISIBLE);
                main_iv_title_right.setClickable(false);
                main_iv_title_left.setOnClickListener(this);
                break;
            //商城：无
            case 4:
                main_iv_title_left.setVisibility(View.INVISIBLE);
                main_iv_title_left.setClickable(false);
                main_iv_title_right.setVisibility(View.INVISIBLE);
                main_iv_title_right.setClickable(false);
                break;


        }
    }

    private void selectTab(int position) {
        for (int i = 0; i < 5; i++) {
            if (i == position) {
                textViews[i].setTextColor(deeppowder);
                imageViews[i].setBackgroundResource(imagePressed[i]);
            } else {
                textViews[i].setTextColor(grey);
                imageViews[i].setBackgroundResource(imageNormal[i]);
            }
        }
    }

    @Override
    public void setContentLayout() {
        setContentView(R.layout.main_activity);
    }

    @Override
    protected void onClickEvent(View view) {
        switch (view.getId()) {
            //首页
            case R.id.main_bottom_home_ll:
                TempSingleToast.cancleToast();
                curTabPosition = 0;
                selectTitle(curTabPosition);
                selectTab(curTabPosition);
                selsectFragment();
                Log.e("jxf", "右边移除");
                drawer_layout.removeView(main_right);
                break;


            //内容
            case R.id.main_bottom_content_ll:
                TempSingleToast.cancleToast();
                curTabPosition =1;
                selectTitle(curTabPosition);
                selectTab(curTabPosition);
                selsectFragment();
                Log.e("jxf", "右边先移除，再添加");
                drawer_layout.removeView(main_right);
                drawer_layout.addView(main_right);
                //点击出去，回来选中第一个的方法：
                backToAll();
//                main_right_lv_category.setSelection(0);
//                adapter.notifyDataSetChanged();
                break;
            //链接
            case R.id.main_bottom_connect_ll:
                TempSingleToast.showToast(MainActivity.this, "更多精彩，敬请期待！");
//                Toast.makeText(MainActivity.this, "更多精彩，敬请期待！", Toast.LENGTH_SHORT).show();
//                curTabPosition = 2;
//                selectTitle(curTabPosition);
//                selectTab(curTabPosition);
//                selsectFragment();
//                Log.e("jxf", "右边先移除，再添加");
//                drawer_layout.removeView(main_right);
//                drawer_layout.addView(main_right);
                //以下方法不好用：使用先移除再添加来替代
//                Log.e("jxf", "判断右边是否存在" + (main_right.getVisibility() == View.GONE));
//                if(main_right.getVisibility()==View.GONE){
//                    Log.e("jxf","右边不存在，添加");
//                    drawer_layout.addView(main_right);
//                }
                break;
            //研讨会
            case R.id.main_bottom_schedule_ll:
                TempSingleToast.cancleToast();
                curTabPosition = 3;
                selectTitle(curTabPosition);
                selectTab(curTabPosition);
                selsectFragment();
                Log.e("jxf", "右边移除");
                drawer_layout.removeView(main_right);
                break;
            //商城
            case R.id.main_bottom_mall_ll:
                TempSingleToast.showToast(MainActivity.this, "更多精彩，敬请期待！");

//                curTabPosition = 4;
//                selectTitle(curTabPosition);
//                selectTab(curTabPosition);
//                selsectFragment();
//                Log.e("jxf", "右边先移除，再添加");
//                drawer_layout.removeView(main_right);
//                drawer_layout.addView(main_right);


                break;
            //标题左边打开左抽屉
            case R.id.main_iv_title_left:
                Log.e("jxf","点击左边，打开左边");
                Log.e("jxf","左侧的打开之前请求网络拿取是否有新收件");
                // TODO: 2016/3/8 失败 网络断开都要放行的
//                requestIsHaveNew();
                drawer_layout.openDrawer(Gravity.LEFT);
                break;
            //标题右边打开右抽屉
            case R.id.main_iv_title_right:
                Log.e("jxf","点击右边，打开右边");
                drawer_layout.openDrawer(Gravity.RIGHT);

                break;
            //跳转个人信息
            case R.id.main_drawer_left_persondata:
                drawer_layout.closeDrawer(main_left);
                Intent intentpersondata=new Intent(this, MyPersonalDataActivity.class);
                startActivity(intentpersondata);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

                break;
            //跳转个人信息设置
            case R.id.main_drawer_left_persondata_setting:
                drawer_layout.closeDrawer(main_left);
                Intent intentsetting=new Intent(this, MyPersonEditActivity.class);
                startActivityForResult(intentsetting, REQUEST_CODE);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

                break;
            //跳转支付历史
            case R.id.main_drawer_left_pay_history:
                drawer_layout.closeDrawer(main_left);
                Intent intentpayhistory=new Intent(this, HistoryForPayActivity.class);
                startActivity(intentpayhistory);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

                break;
            //跳转订阅管理
            case R.id.main_drawer_left_subscription_manager:
                drawer_layout.closeDrawer(main_left);
                Intent intentsubscriptionmanager=new Intent(this, ManageSubscribeActivity.class);
                startActivity(intentsubscriptionmanager);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            //跳转 收件箱
            // TODO: 2016/3/8 跳转修改 
            case R.id.main_drawer_left_inbox:
                drawer_layout.closeDrawer(main_left);
                Intent intentinbox=new Intent(this, ManageSubscribeActivity.class);
                startActivity(intentinbox);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            //左侧返回：关闭左侧抽屉
            case R.id.main_left_goback:
                drawer_layout.closeDrawer(main_left);
                break;
        }
    }

    private void backToAll() {
        ((ContentFragment)contentFragment).refreshIsCheck(false);
        if (right_position_pre!=0){
            //首先刷新右侧列表：在refresh以下content的内容
            for (int i = 0; i < categorys.size(); i++) {
                if (i == 0) {
                    categorys.get(i).isClick = true;
                } else {
                    categorys.get(i).isClick = false;
                }

            }

            //切换字体颜色
            adapter.notifyDataSetChanged();
            ((ContentFragment)contentFragment).refreshContent(0);
            right_position_pre=0;
        }

    }

    /**
     * 切换fragment
     * ***/
    private void selsectFragment() {
        if (index != curTabPosition) {
            Log.e("jxf", "切换fragment");
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.hide(fragments[index]);
            if (!fragments[curTabPosition].isAdded()) {
                transaction.add(R.id.main_content_frame, fragments[curTabPosition]);
            }
            transaction.show(fragments[curTabPosition]).commit();
        }
        index = curTabPosition;
        DefinedSingleToast.cancleToast();
    }



    /*
    *
    * 进入main请求右侧列表操作
    * ***/
    private void requestCategory(){
        NetClient.headGet(this, Url.MAIN_CATEGORY, null, new NetResponseHandler() {
            @Override
            public void onResponse(String json) {
                try {
                    JSONObject JSONObjectAll = new JSONObject(json);
                    String status=JSONObjectAll.optString("status");
                    if (status.equals("1")){
                        SharedPreUtils.put(getApplicationContext(), "list", json);
                        Log.e("jxf", "main界面category存储完毕");
//                        //肯定不为空
//                        String categoryString=(String)SharedPreUtils.get(getApplicationContext(),"list","");
//                        Log.e("jxf", "main界面打印category" + categoryString);
                        //取出
                        takeoutCategory(json);
                    }
//                    else{
//                        //也从本地取：两种情况  本地为空；本地不为空有数据
//                        String categoryString=(String)SharedPreUtils.get(getApplicationContext(),"list","");
//                        //不为空
//                        if (!(categoryString.equals(""))){
//                            //取出
//                            takeoutCategory(categoryString);
//                        }else{
//                            Log.e("jxf","右侧列表没有取出");
//                            return;
//
//                        }
//
//                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("jxf","main页取右侧列表请求网络有异常了"+e.toString());
                }

            }
            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                Log.e("jxf","main页取右侧列表请求网络onfail"+throwable.toString());
//                //也要从本地取 两种情况  本地为空；本地不为空有数据
//                String categoryString=(String)SharedPreUtils.get(getApplicationContext(),"list","");
//                //不为空
//                if (!(categoryString.equals(""))){
//                    //取出
//                    takeoutCategory(categoryString);
//
//                }else{
//                    Log.e("jxf","右侧列表没有取出");
//                    return;
//                }
            }
        });
    }



    /*
    * 解析列表本地json字符串的
    *
    * */
    private void takeoutCategory(String json){
        //本地无数据
        if (json.equals("")){
            Log.e("jxf","本地文件中没有存储列表的list");
            return;
        }
        //本地有数据+网络请求成功
        else{
            try {
                categorys.clear();
                JSONObject JSONObjectAll=new JSONObject(json);
                JSONArray jSONArray=JSONObjectAll.optJSONArray("datas");
                Category categoryshort=new Category();
                categoryshort.id=0;
                categoryshort.name=getResources().getString(R.string.main_drawer_right_title_all);
                categoryshort.sort=0;
                categoryshort.isClick=true;
                categorys.add(categoryshort);
                int length=jSONArray.length();
                for (int i = 0; i < length; i++){
                    JSONObject obj = jSONArray.optJSONObject(i);
                    Category category=new Category();
                    category.id=obj.optInt("id");
                    category.name=obj.optString("name");
                    category.sort=obj.optInt("sort");
                    category.isClick=false;
                    categorys.add(category);
                }
                Log.e("jxf","列表main页面第二次请求的最终数量"+categorys.size());
                adapter.notifyDataSetChanged();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    //双击返回

    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        Toast pressBackToast = Toast.makeText(getApplicationContext(), getResources().getString(R.string.main_again_exit), Toast.LENGTH_SHORT);

        if (Math.abs(currentTime - lastPress) > backPressThreshold) {
            pressBackToast.show();
            // 记录上一次按back键的时间
            lastPress = currentTime;
        } else {
            pressBackToast.cancel();
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE&&resultCode==1103){
            MainActivity.this.finish();
        }
        if (requestCode==REQUEST_CODE&&resultCode==1101){
            Log.e("jxf","个人设置中心修改左侧");
            changeLeftPersonInfo();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.getSqlManager().close();
    }
}
