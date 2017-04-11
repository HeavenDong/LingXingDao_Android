package com.miracleworld.lingxingdao.android.activity;

import android.content.Intent;
import android.view.View;
import android.widget.ImageView;

import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.base.BaseActivity;
import com.miracleworld.lingxingdao.android.options.ImageLoaderOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by donghaifeng on 2016/1/11.
 */
public class ScraleActivity extends BaseActivity {
    private String different;
    @Override
    protected void initView() {
        ImageView All_cover_iv= (ImageView) findViewById(R.id.all_cover_iv);
        All_cover_iv.setOnClickListener(this);
        Intent intent=getIntent();
        String imgSmallUrl=intent.getStringExtra("imgSmallUrl");
        different=intent.getStringExtra("different");
        ImageLoader.getInstance().displayImage(imgSmallUrl,All_cover_iv, ImageLoaderOptions.headOptions);
    }

    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_scrale);
    }

    @Override
    protected void onClickEvent(View view) {
        switch (view.getId()){
            case R.id.all_cover_iv:
                if (different.equals("1")){
                    finish();
                    //关闭动画从右到左
                    overridePendingTransition(R.anim.unchange, R.anim.big_to_small);
                }
                if (different.equals("2")){
                    finish();
                    //关闭动画从右到左
                    overridePendingTransition(R.anim.unchange, R.anim.righttop_big_to_small);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        if (different.equals("1")){
            finish();
            //关闭动画从右到左
            overridePendingTransition(R.anim.unchange, R.anim.big_to_small);
        }
        if (different.equals("2")){
            finish();
            //关闭动画从右到左
            overridePendingTransition(R.anim.unchange, R.anim.righttop_big_to_small);
        }
    }
}
