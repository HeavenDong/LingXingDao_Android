package com.miracleworld.lingxingdao.android.activity.pay;


import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.adapter.MyFragmentPagerAdapter;
import com.miracleworld.lingxingdao.android.base.BaseActivity;
import com.miracleworld.lingxingdao.android.fragment.HistoryForChannelFragment;
import com.miracleworld.lingxingdao.android.fragment.HistoryForTicketFragment;
import java.util.ArrayList;

/**
 *支付历史
 */
public class HistoryForPayActivity extends BaseActivity{
    private static final String TAG = "haifeng";
    private RelativeLayout history_channel_title;
    private RelativeLayout history_ticket_title;
    private TextView channel_title;
    private TextView ticket_title;
    private View channl_coarse_line_left;
    private View ticket_coarse_line_right;
    private ViewPager history_viewpager;
    private ArrayList<Fragment> fragmentList;
    @Override
    protected void initView() {
      /*标题返回键*/
      findViewById(R.id.pay_history_title_left).setOnClickListener(this);
      /*小标题*/
        //频道点击区域
        history_channel_title = (RelativeLayout) findViewById(R.id.history_channel_title);
        //字体
        channel_title= (TextView) findViewById(R.id.channel_title);
        //粗线
        channl_coarse_line_left=findViewById(R.id.channl_coarse_line_left);
        //购票点击区域
        history_ticket_title= (RelativeLayout) findViewById(R.id.history_ticket_title);
        //字体
        ticket_title = (TextView) findViewById(R.id.ticket_title);
        //粗线
        ticket_coarse_line_right=findViewById(R.id.ticket_coarse_line_right);
        //颜色 color/subscripte_grey_color
        //设置点击事件
        history_channel_title.setOnClickListener(this);
        history_ticket_title.setOnClickListener(this);
        //viewpager
        history_viewpager = (ViewPager) findViewById(R.id.history_viewpager);
        history_viewpager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        history_viewpager.setOffscreenPageLimit(1);
        //viewpager数据
        fragmentList = new ArrayList<Fragment>();
        HistoryForChannelFragment channelFragment=new HistoryForChannelFragment();
        HistoryForTicketFragment ticketFragment=new HistoryForTicketFragment();
        Log.e("jxf", "viewpager数据集合添加频道fragment");
        fragmentList.add(channelFragment);
        Log.e("jxf", "viewpager数据集合添加购票fragment");
        fragmentList.add(ticketFragment);
        /*给ViewPager设置适配器*/
        Log.e("jxf", "将数据给适配器来setadapter");
        history_viewpager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList));

        //初始化
        //history_viewpager.setCurrentItem(0,true); 肯能不需要
        channel_title.setTextColor(getResources().getColor(R.color.main_color));
        channl_coarse_line_left.setVisibility(View.VISIBLE);
        ticket_title.setTextColor(getResources().getColor(R.color.subscripte_grey_color));
        ticket_coarse_line_right.setVisibility(View.INVISIBLE);
        //滑动监听
        history_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        //更改控件和下划线
                        channel_title.setTextColor(getResources().getColor(R.color.main_color));
                        channl_coarse_line_left.setVisibility(View.VISIBLE);
                        ticket_title.setTextColor(getResources().getColor(R.color.subscripte_grey_color));
                        ticket_coarse_line_right.setVisibility(View.INVISIBLE);
                        break;
                    case 1:
                        //更改控件和下划线
                        channel_title.setTextColor(getResources().getColor(R.color.subscripte_grey_color));
                        channl_coarse_line_left.setVisibility(View.INVISIBLE);
                        ticket_title.setTextColor(getResources().getColor(R.color.main_color));
                        ticket_coarse_line_right.setVisibility(View.VISIBLE);
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
    @Override
    public void setContentLayout() {
       setContentView(R.layout.pay_history);
    }
    @Override
    protected void onClickEvent(View view) {
       switch (view.getId()){
           case R.id.pay_history_title_left:
               finish();
               overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
               break;
           case R.id.history_channel_title:
               //更改控件状态
               channel_title.setTextColor(getResources().getColor(R.color.main_color));
               channl_coarse_line_left.setVisibility(View.VISIBLE);
               ticket_title.setTextColor(getResources().getColor(R.color.subscripte_grey_color));
               ticket_coarse_line_right.setVisibility(View.INVISIBLE);
               history_viewpager.setCurrentItem(0,true);
               break;
           case R.id.history_ticket_title:
               //更改控件状态
               channel_title.setTextColor(getResources().getColor(R.color.subscripte_grey_color));
               channl_coarse_line_left.setVisibility(View.INVISIBLE);
               ticket_title.setTextColor(getResources().getColor(R.color.main_color));
               ticket_coarse_line_right.setVisibility(View.VISIBLE);
               history_viewpager.setCurrentItem(1,true);
               break;
       }
    }
}
