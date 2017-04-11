package com.miracleworld.lingxingdao.android;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

import com.miracleworld.lingxingdao.android.daoManager.SQLHelperManager;
import com.miracleworld.lingxingdao.android.utils.CommanUtil;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;

/**
 * Created by donghaifeng on 2015/12/16
 * implements Thread.UncaughtExceptionHandler
 */
public class App extends Application{
    //db文件保存在data/data/报名/  下
    public static String DB_NAME = "myDb";
    private static Context mContext;
    private static App instance;
    public static App getInstance() {
        return instance;
    }
    public static Context getContext() {
        return mContext;
    }
    private static SQLHelperManager sqlManager;
    public static SQLHelperManager getSqlManager() {
        return sqlManager;
    }
    public static String httpUserAgent;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = getApplicationContext();
        //获得单例的数据库管理
        sqlManager=SQLHelperManager.getInstance();
        //防止内存泄露立马关闭数据库
        sqlManager.close();
        instance = this;
        initLocationDb();
        setImageLoaderConfiguration();
        //头部信息的另一种获取方式
        getHttpHead();
//        Thread.currentThread().setUncaughtExceptionHandler(this);
    }
    //头部信息方法
    private void getHttpHead(){
        String pkName = mContext.getPackageName();
        String versionName=null;
        int versionCode=0;
        try {
            versionName = mContext.getPackageManager().getPackageInfo(pkName,0).versionName;
            versionCode = mContext.getPackageManager().getPackageInfo(pkName,0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        WebView mwebView = new WebView(mContext);
        WebSettings set = mwebView.getSettings();
        String userinfo=set.getUserAgentString();
        String encoding=System.getProperty("file.encoding");
        String language=System.getProperty("user.language");
        String region=System.getProperty("user.region");
        String company=android.os.Build.MANUFACTURER;
        httpUserAgent="pkName:"+pkName+"versionName:"+versionName+"versionCode"+versionCode+"language:"+language+"region"+region+"encoding"+encoding+"company"+company+"browser"+userinfo;
        Log.e("jxf", "app请求携带头部信息useragent" + httpUserAgent);
    }
    //初始本地数据库
    private void initLocationDb() {
        File saveFile=new File(mContext.getFilesDir().getPath(),DB_NAME);
        if (!saveFile.exists()) {
            Log.e("jxf","raw数据库本地保存：文件不存在");
            try {
                saveFile.createNewFile();
                Log.e("jxf", "raw数据库本地保存：文件不存在：创建");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("jxf", "raw数据库本地保存：文件不存在：创建时异常");
            }
            InputStream is = mContext.getResources().openRawResource(R.raw.lingxing);
            FileOutputStream fos = null;
            try {
                boolean zaibuzai=saveFile.exists();
                Log.e("jxf","raw数据库本地保存：初始已存在活文件不存在创建完：再次判断"+zaibuzai);
                fos = new FileOutputStream(saveFile);
                byte[] buffer = new byte[8192];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                    Log.e("jxf", "raw数据库缓存本地开始");
                }
                fos.close();
                is.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e("jxf", "raw数据库本地保存：文件不存在：创建时：异常了");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("jxf", "raw数据库本地保存：文件不存在：创建时：流异常了");
            }
        }
    }
    /**
     * 配置ImageLoaderConfiguration
     */
    private void setImageLoaderConfiguration() {
//        //设置缓存文件夹位置
//        File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), "imageloader/Cache");
//
//        ImageLoaderConfiguration config = new ImageLoaderConfiguration
//                .Builder(getApplicationContext())
//                .memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽
//                        //.discCacheExtraOptions(480, 800, Bitmap.CompressFormat.JPEG, 75, null) // Can slow ImageLoader, use it carefully (Better don't use it)/设置缓存的详细信息，最好不要设置这个
//                .threadPoolSize(3)//线程池内加载的数量
//                .threadPriority(Thread.NORM_PRIORITY - 2)//配置下载图片的线程优先级
//                .denyCacheImageMultipleSizesInMemory()//不会在内存中缓存多个大小的图片
//                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) //???????你可以通过自己的内存缓存实现
//                .memoryCacheSize(2 * 1024 * 1024)//这个可能有问题：内存大小默认是：app可用内存的1/8
//                        //.discCacheSize(50 * 1024 * 1024)//这个可以解开
//                .discCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密 ：为了保证图片名称唯一
//                .tasksProcessingOrder(QueueProcessingType.LIFO)
//                .discCacheFileCount(100) //缓存的文件数量
//                .discCache(new UnlimitedDiskCache(cacheDir))//自定义缓存路径
//                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
//                .imageDownloader(new BaseImageDownloader(getApplicationContext(), 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
//                .writeDebugLogs() // Remove for release app
//                .build();//开始构建
//        // Initialize ImageLoader with configuration.
//        ImageLoader.getInstance().init(config);//全局初始化此配置

        //功能性试验
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
        .threadPriority(Thread.NORM_PRIORITY - 2)//配置下载图片的线程优先级
        .denyCacheImageMultipleSizesInMemory()//不会在内存中缓存多个大小的图片
        .diskCacheFileNameGenerator(new Md5FileNameGenerator())//为了保证图片名称唯一
        .diskCacheSize(50 * 1024 * 1024) // 50 MiB
        //内存缓存大小默认是：app可用内存的1/8
        .tasksProcessingOrder(QueueProcessingType.LIFO)
        .writeDebugLogs()// Remove for release app
        .build();
        // Initialize ImageLoader with configuration.
        ImageLoader.getInstance().init(config);
    }

//    @Override
//    public void uncaughtException(Thread thread, Throwable ex) {
//        try {
//            PrintStream printStream=new PrintStream(mContext.getFilesDir().getAbsolutePath()+"/errorlog.txt");
//            String currentTime= CommanUtil.transhms(System.currentTimeMillis(), "yyyy-MM-dd HH:mm:ss");
//            printStream.println("TIME:"+currentTime);
//            printStream.println("PHONEINFOR:"+httpUserAgent);
//            printStream.println("以下是错误信息===========");
//            ex.printStackTrace(printStream);
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            Log.e("jxf","捕获全局错误日志生成文件异常了");
//        }
//        android.os.Process.killProcess(android.os.Process.myPid());
//    }
//    //转换时间：月日时分MM-dd HH:mm
//    private  String transhms(long progress){
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");//初始化Formatter的转换格式。
//        String ms = formatter.format(progress);
//        return ms;
//    }
}