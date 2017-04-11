package com.miracleworld.lingxingdao.android.options;

import com.miracleworld.lingxingdao.android.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

/**
 * Created by donghaifeng on 2016/1/8.
 */
public class ImageLoaderOptions {
    public static DisplayImageOptions options= new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.img_down)
            .showImageForEmptyUri(R.drawable.img_down)
            .showImageOnFail(R.drawable.img_down).cacheInMemory(true)
            .cacheOnDisk(true).considerExifParams(true)
            .displayer(new SimpleBitmapDisplayer())//是否图片加载好后渐入的动画时间
            .build();
        public static DisplayImageOptions newsoptions= new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.news_down)
                .showImageForEmptyUri(R.drawable.news_down)
                .showImageOnFail(R.drawable.news_down).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())//是否图片加载好后渐入的动画时间
                .build();
    public static DisplayImageOptions headOptions=new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.head_loading)
            .showImageForEmptyUri(R.drawable.head_loading)
            .showImageOnFail(R.drawable.head_loading).cacheInMemory(true)
            .cacheOnDisk(true).considerExifParams(true)
            .displayer(new SimpleBitmapDisplayer())//是否图片加载好后渐入的动画时间
            .build();
        public static DisplayImageOptions smallheadOptions=new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.person_tab)
                .showImageForEmptyUri(R.drawable.person_tab)
                .showImageOnFail(R.drawable.person_tab).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .displayer(new SimpleBitmapDisplayer())//是否图片加载好后渐入的动画时间
                .build();
    public static DisplayImageOptions playOption=new DisplayImageOptions.Builder()
            .showImageOnLoading(R.drawable.img_default)
            .showImageForEmptyUri(R.drawable.img_default)
            .showImageOnFail(R.drawable.img_default).cacheInMemory(true)
            .cacheOnDisk(true).considerExifParams(true)
            .displayer(new SimpleBitmapDisplayer())//是否图片加载好后渐入的动画时间
            .build();
    public static DisplayImageOptions optionsCrop= new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisk(true).considerExifParams(true).build();
        public  static DisplayImageOptions playRoundOption=new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.img_default)
                .showImageForEmptyUri(R.drawable.img_default)
                .showImageOnFail(R.drawable.img_default).cacheInMemory(true)
                .cacheOnDisk(true).considerExifParams(true)
                .displayer(new RoundedBitmapDisplayer(5))//是否图片加载好后渐入的动画时间
                .build();
}
