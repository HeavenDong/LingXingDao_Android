package com.miracleworld.lingxingdao.android.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.options.ImageLoaderOptions;
import com.miracleworld.lingxingdao.android.view.DefinedSingleToast;
import com.miracleworld.lingxingdao.android.view.clipview.ClipImageView;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;


/**
 * 用于缩放裁剪的自定义activity
 */
public class CropActivity extends Activity implements View.OnClickListener {

    private ClipImageView imageView;
    public static final int RESULT_OK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        Log.e("jxf", "ImageLoader清理缓存");
        ImageLoader.getInstance().clearMemoryCache();


        imageView = (ClipImageView) findViewById(R.id.src_pic);
        //打开的得到图片的URi:利用bitmap
        Intent intent = getIntent();
        final String uri = intent.getStringExtra("uri");


        ImageLoader.getInstance().displayImage(uri, imageView, ImageLoaderOptions.optionsCrop);

        findViewById(R.id.bt_cancle).setOnClickListener(this);
        findViewById(R.id.bt_complete).setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_cancle:
                finish();
                break;
            case R.id.bt_complete:
                Bitmap bitmap = null;
                try {
                    bitmap = imageView.clip();
                    Log.e("jxf","bitmap大小打印"+(bitmap.getRowBytes() * bitmap.getHeight()));
                } catch (OutOfMemoryError e) {
                    Log.e("jxf","切图类--切图完成时，报OOM异常");
                    e.printStackTrace();
                    DefinedSingleToast.showToast(CropActivity.this, CropActivity.this.getResources().getString(R.string.oom_catch));
                    //扑捉到内存错误的时候，要及时重新打开本界面
                    //startActivity(new Intent(CropActivity.this, WishAddActivity.class));
                }

                //String CACHE_DIR = "/CacheDir/aijingxi";// 本地缓存目录
                File imagePath = new File(getCacheDir(), "" + Calendar.getInstance().getTimeInMillis()+".jpg");// /sdcard/aijingxi/cropImage
                File parentFile = imagePath.getParentFile();
                if (!parentFile.exists()) {// 判断上级目录是否存在，不存在就需要创建
                    parentFile.mkdirs();
                }

                // 把Bitmap对象持久化到本地

                OutputStream os = null;
                try {
                    os = new FileOutputStream(imagePath);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    os.flush();
                    os.close();
                } catch (FileNotFoundException e) {
                    Log.e("jxf","切图类--bitmap图片压缩时，报FileNotFoundException异常");
                    e.printStackTrace();
                } catch (IOException e) {
                    Log.e("jxf","切图类--bitmap图片压缩时，报IOException异常");
                    e.printStackTrace();
                }

                //释放
                if (bitmap != null && !bitmap.isRecycled()) {
                    // 回收并且置为null
                    bitmap.recycle();
                    bitmap = null;
                    Log.e("jxf", "bitmap回收");
                }
                System.gc();

                Intent intent = new Intent();
                intent.putExtra("Cropuri", imagePath.getAbsolutePath());
                CropActivity.this.setResult(RESULT_OK, intent);
                finish();
                break;
        }
    }
}