package com.miracleworld.lingxingdao.android.daoManager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.miracleworld.lingxingdao.android.App;
import com.miracleworld.lingxingdao.android.helper.MySQLiteOpenHelper;

/**
 * Created by donghaifeng on 2016/1/2.
 */
public class SQLHelperManager {
    private static SQLHelperManager instance;
    private Context mContext;
    private MySQLiteOpenHelper mySQLiteOpenHelper;
    private SQLiteDatabase db;
    //单例模式
    private SQLHelperManager(Context context){
        mContext=context;
        mySQLiteOpenHelper=new MySQLiteOpenHelper(mContext);
        db=mySQLiteOpenHelper.getWritableDatabase();
    }
    //使用单例，不加锁
    public static SQLHelperManager getInstance() {
        if(instance == null){
            Log.e("jxf","数据库管理类单例不存在：open时创建");
            instance = new SQLHelperManager(App.getContext());
        }else{
            Log.e("jxf","数据库管理类单例已经存在：open时不创建：直接拿来用");
        }
        return instance;
    }
    //打开数据库
    private void open(){
        if (db==null||!db.isOpen()){
            db=mySQLiteOpenHelper.getWritableDatabase();
            Log.e("jxf","db之前没有或者关闭掉了：getWritableDatabase()打开数据库");
        }
        else{
            Log.e("jxf","db之间就已经存在：没有被关闭");
            return;
        }
    }
    //关闭数据库
    public void close(){
        if (db.isOpen()){
            db.close();
            Log.e("jxf", "db存在才关闭db对象");
        }
    }

    //添加数据库的方法：抽出表名
    public void insert(String tableName, ContentValues values) {
        open();
        db.insert(tableName, null, values);
        Log.e("jxf", "表：" + tableName + "添加数据");
    }
    //删除全部数据
    public  void deleteAll(String tableName){
        open();
        db.delete(tableName, null, null);
        Log.e("jxf", "表：" + tableName + "删除了全部数据");
    }

    //获取单条的
    public  Cursor seleteOne(String tableName,String[] columns, String orderBy,String limit){
        open();
        Cursor cursor=db.query(tableName,columns,null,null,null,null,orderBy,limit);
        Log.e("jxf", "表：" + tableName + "获取全部数据：得到唯一的cursor");
        return cursor;
    }

    //获取集合并利用SQL排序排序
    public  Cursor seleteAll(String tableName,String[] columns, String orderBy){
        open();
        Cursor cursor=db.query(tableName,columns,null,null,null,null,orderBy);
        Log.e("jxf", "表：" + tableName + "获取全部数据：得到cursor");
        return cursor;
    }
    //维持20条的删除单部分
    public  void deleteOne(String tableName,String where,String[] wheres){
        open();
        db.delete(tableName, where, wheres);
        Log.e("jxf", "表：" + tableName + "删除了超过20条的数据");
    }
    //更新一条数据
    public  void updateOne(String tableName,ContentValues values,String where,String[] wheres){
        open();
        db.update(tableName, values, where, wheres);
        Log.e("jxf", "表：" + tableName + "更新单条数据");
    }

    public Cursor seleteAllWhere(String tableName, String[] columns,String where, String[] wheres,String orderBy) {
        open();
        Cursor cursor=db.query(tableName,columns,where,wheres,null,null,orderBy);
        Log.e("jxf", "表：" + tableName + "获取"+where+"全部数据：得到cursor");
        return cursor;
    }

    public void deleteAllWhere(String tableName,String where,String[] wheres) {
        open();
        db.delete(tableName, where, wheres);
        Log.e("jxf", "表：" + tableName + "删除了"+where+"全部数据");
    }
}
