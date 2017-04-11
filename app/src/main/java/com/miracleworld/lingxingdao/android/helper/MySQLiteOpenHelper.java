package com.miracleworld.lingxingdao.android.helper;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by donghaifeng on 2016/1/2.
 */
public class MySQLiteOpenHelper  extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "list.db";
    private static final int DATABASE_VERSION = 2;
    public MySQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //注意：openhelper的调用时创建或者打开一个数据库：如果是第一次才会执行oncreat方法创建表 其他的时候不会执行  只有当版本号不一致的时候才会调用 onupgrade方法
    //注意 oncreat或者onupgrade的方法执行会传进一个默认的给你创建或者修改表结构
//    将现有表重命名为临时表；
//
//    创建新表；
//
//    将临时表的数据导入新表（注意处理修改的列）；
//
//    删除临时表。
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("jxf","执行oncreat方法");
        db.execSQL("CREATE TABLE 'schedule' ('id1' integer PRIMARY KEY AUTOINCREMENT ,'id' bigint(11),'title' varchar(100),'teacher_name' varchar(100),'teacher_img_small' varchar(256),'teacher_img_big' varchar(256),'start_time' datetime,'end_time' datetime,'province_name' varchar(100),'city_name' varchar(100),'address' varchar(256),'price' bigint(11),'again_price' bigint(11),'sitin_price' bigint(11),'ask_price' bigint(11),'url' varchar(256),'color' varchar(256),'price_type' varchar(256))");
        db.execSQL("CREATE TABLE 'home' ('id1' integer PRIMARY KEY AUTOINCREMENT ,'id' bigint(11),'title' varchar(100),'icon' varchar(256),'creat_time' datetime,'url' varchar(256),'update_time' datetime)");
        db.execSQL("CREATE TABLE 'contents' ('id1' integer PRIMARY KEY AUTOINCREMENT ,'teacher_id' bigint(11),'portraiturl_big' varchar(256),'portraiturl_small' varchar(256),'nickname' varchar(256),'introduce' varchar(256),'catgory_name' varchar(256),'catgory_id' varchar(256),'remark' text,'pricerange' text,'sort' bigint(11))");
        db.execSQL("CREATE TABLE 'channel' ('id1' integer PRIMARY KEY AUTOINCREMENT ,'id' bigint(11),'channelname' text,'price' bigint(11),'categoryname' varchar(256),'iconurl' varchar(256),'des' text,'teacher_id' bigint(11))");
        db.execSQL("CREATE TABLE 'channel2' ('id1' integer PRIMARY KEY AUTOINCREMENT ,'id' bigint(11),'channelname' text,'price' bigint(11),'categoryname' varchar(256),'iconurl' varchar(256),'des' text,'teacher_id' bigint(11),'categoryid' bigint(11))");
        db.execSQL("CREATE TABLE 'customer' ('id' integer PRIMARY KEY AUTOINCREMENT,'name' varchar(256),'mobile' varchar(100),'identitycard' varchar(100),'agent' varchar(256))");
    }

    //注意小版本的时候可以做数据库的历史存储 创建原来的表：键临时表中的数据存储进新建的原来的空数据库 做表 迁移 在删除临时的数据库  大版本直接删除数据库
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("jxf","执行onUpgrade方法");
//版本1的数据库
//        db.execSQL("CREATE TABLE 'schedule' ('id1' integer PRIMARY KEY AUTOINCREMENT ,'id' bigint(11),'title' varchar(100),'teacher_name' varchar(100),'teacher_img_small' varchar(256),'teacher_img_big' varchar(256),'start_time' datetime,'end_time' datetime,'province_name' varchar(100),'city_name' varchar(100),'address' varchar(256))");
//        db.execSQL("CREATE TABLE 'home' ('id1' integer PRIMARY KEY AUTOINCREMENT ,'id' bigint(11),'title' varchar(100),'icon' varchar(256),'creat_time' datetime,'url' varchar(256),'update_time' datetime)");
//        db.execSQL("CREATE TABLE 'contents' ('id1' integer PRIMARY KEY AUTOINCREMENT ,'teacher_id' bigint(11),'portraiturl_big' varchar(256),'portraiturl_small' varchar(256),'nickname' varchar(256),'introduce' varchar(256),'catgory_name' varchar(256),'catgory_id' varchar(256),'remark' text,'pricerange' text,'sort' bigint(11))");
//        db.execSQL("CREATE TABLE 'channel' ('id1' integer PRIMARY KEY AUTOINCREMENT ,'id' bigint(11),'channelname' text,'price' bigint(11),'categoryname' varchar(256),'iconurl' varchar(256),'des' text,'teacher_id' bigint(11))");

        if (oldVersion==1){
            db.execSQL("Drop TABLE 'schedule'");
            db.execSQL("Drop TABLE 'home'");
            db.execSQL("Drop TABLE 'contents'");
            db.execSQL("Drop TABLE 'channel'");

            //版本升级要做的操作 需要创建的新表  版本2的数据库
            db.execSQL("CREATE TABLE 'schedule' ('id1' integer PRIMARY KEY AUTOINCREMENT ,'id' bigint(11),'title' varchar(100),'teacher_name' varchar(100),'teacher_img_small' varchar(256),'teacher_img_big' varchar(256),'start_time' datetime,'end_time' datetime,'province_name' varchar(100),'city_name' varchar(100),'address' varchar(256),'price' bigint(11),'again_price' bigint(11),'sitin_price' bigint(11),'ask_price' bigint(11),'url' varchar(256),'color' varchar(256),'price_type' varchar(256))");
            db.execSQL("CREATE TABLE 'home' ('id1' integer PRIMARY KEY AUTOINCREMENT ,'id' bigint(11),'title' varchar(100),'icon' varchar(256),'creat_time' datetime,'url' varchar(256),'update_time' datetime)");
            db.execSQL("CREATE TABLE 'contents' ('id1' integer PRIMARY KEY AUTOINCREMENT ,'teacher_id' bigint(11),'portraiturl_big' varchar(256),'portraiturl_small' varchar(256),'nickname' varchar(256),'introduce' varchar(256),'catgory_name' varchar(256),'catgory_id' varchar(256),'remark' text,'pricerange' text,'sort' bigint(11))");
            db.execSQL("CREATE TABLE 'channel' ('id1' integer PRIMARY KEY AUTOINCREMENT ,'id' bigint(11),'channelname' text,'price' bigint(11),'categoryname' varchar(256),'iconurl' varchar(256),'des' text,'teacher_id' bigint(11))");
            db.execSQL("CREATE TABLE 'channel2' ('id1' integer PRIMARY KEY AUTOINCREMENT ,'id' bigint(11),'channelname' text,'price' bigint(11),'categoryname' varchar(256),'iconurl' varchar(256),'des' text,'teacher_id' bigint(11),'categoryid' bigint(11))");
            db.execSQL("CREATE TABLE 'customer' ('id' integer PRIMARY KEY AUTOINCREMENT,'name' varchar(256),'mobile' varchar(100),'identitycard' varchar(100),'agent' varchar(256))");

        }

    }
}
