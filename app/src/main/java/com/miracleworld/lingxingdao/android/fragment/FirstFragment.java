package com.miracleworld.lingxingdao.android.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.base.BaseFragment;
import com.miracleworld.lingxingdao.android.view.DefinedSingleToast;

/**
 * Created by donghaifeng on 2016/3/8.
 */
public class FirstFragment extends BaseFragment {


    private int curTabPosition = 0;
    private int index = 0; // 当前Fragment的index
    private TextView first_fragment_news_title;
    private TextView first_fragment_content_title;
    private TextView first_fragment_news_line;
    private TextView first_fragment_content_line;
    private FragmentManager fragmentManager;
    private Fragment[] fragments;

    @Override
    protected void initView(View view, Bundle bundle) {
        //点击的区域
        RelativeLayout first_fragment_news= (RelativeLayout) view.findViewById(R.id.first_fragment_news);
        first_fragment_news.setOnClickListener(this);
        RelativeLayout first_fragment_content= (RelativeLayout) view.findViewById(R.id.first_fragment_content);
        first_fragment_content.setOnClickListener(this);
        //显示标题的地方
        first_fragment_news_title= (TextView) view.findViewById(R.id.first_fragment_news_title);
        first_fragment_content_title= (TextView) view.findViewById(R.id.first_fragment_content_title);
        //标题下方的线
        first_fragment_news_line= (TextView) view.findViewById(R.id.first_fragment_news_line);
        first_fragment_content_line= (TextView) view.findViewById(R.id.first_fragment_content_line);
        //容器
        FrameLayout first_fragment_fragmentgroup= (FrameLayout) view.findViewById(R.id.first_fragment_fragmentgroup);
        first_fragment_fragmentgroup.requestDisallowInterceptTouchEvent(true);
        //初始化
        first_fragment_news_title.setTextColor(getResources().getColor(R.color.main_color));
        first_fragment_news_line.setBackgroundColor(getResources().getColor(R.color.main_color));
        first_fragment_content_title.setTextColor(getResources().getColor(R.color.content_teacher_font_color));
        first_fragment_content_line.setBackgroundColor(getResources().getColor(R.color.light_grey));
        //初始化fragment
        Fragment homeFragemnt=new HomeFragment();
        Fragment newContentFragment=new NewContentFragment();
        fragments = new Fragment[]{homeFragemnt,newContentFragment};
        fragmentManager=getChildFragmentManager();
        fragmentManager.beginTransaction()
                .add(R.id.first_fragment_fragmentgroup, homeFragemnt)
                .show(homeFragemnt).commit();
    }

    @Override
    protected int setLayout() {
        return R.layout.first_layout_fragment;
    }

    @Override
    protected void onClickEvent(View view) {
        switch (view.getId()){
            //点击次标题的新闻
            case R.id.first_fragment_news:
                curTabPosition=0;
                //切换次级标题的显示
                first_fragment_news_title.setTextColor(getResources().getColor(R.color.main_color));
                first_fragment_news_line.setBackgroundColor(getResources().getColor(R.color.main_color));
                first_fragment_content_title.setTextColor(getResources().getColor(R.color.content_teacher_font_color));
                first_fragment_content_line.setBackgroundColor(getResources().getColor(R.color.light_grey));
                //切换fragment
                selectChildFragemnt();
                break;
            //点击次标题的新内容
            case R.id.first_fragment_content:
                curTabPosition=1;
                //切换次级标题的显示
                first_fragment_news_title.setTextColor(getResources().getColor(R.color.content_teacher_font_color));
                first_fragment_news_line.setBackgroundColor(getResources().getColor(R.color.light_grey));
                first_fragment_content_title.setTextColor(getResources().getColor(R.color.main_color));
                first_fragment_content_line.setBackgroundColor(getResources().getColor(R.color.main_color));
                //切换fragment
                selectChildFragemnt();
                break;
        }
    }

    private void selectChildFragemnt() {
        if (index != curTabPosition) {
            Log.e("jxf", "切换fragment");
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.hide(fragments[index]);
            if (!fragments[curTabPosition].isAdded()) {
                transaction.add(R.id.first_fragment_fragmentgroup, fragments[curTabPosition]);
            }
            transaction.show(fragments[curTabPosition]).commit();
        }
        index = curTabPosition;
        DefinedSingleToast.cancleToast();
    }


}
