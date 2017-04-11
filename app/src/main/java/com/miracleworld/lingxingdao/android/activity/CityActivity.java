package com.miracleworld.lingxingdao.android.activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.miracleworld.lingxingdao.android.App;
import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.adapter.CityActivityAdapter;
import com.miracleworld.lingxingdao.android.base.BaseActivity;
import com.miracleworld.lingxingdao.android.bean.CityBean;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by donghaifeng on 2015/12/24.
 */
public class CityActivity extends BaseActivity {

    //删选数据库的条件
    private int provenceId;
    private ArrayList<CityBean> citys;


    @Override
    protected void initView() {
        //返回标题
        findViewById(R.id.activity_city_iv_goback).setOnClickListener(this);
        //市列表
        ListView activity_city_lv= (ListView) findViewById(R.id.activity_city_lv);

        getPageValue();
        relationDb();
        CityActivityAdapter adapter=new CityActivityAdapter(this,citys);
        activity_city_lv.setAdapter(adapter);
        activity_city_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent();
                intent.putExtra("cityName", citys.get(position).cityName);
                intent.putExtra("cityId", citys.get(position).cityId);
                CityActivity.this.setResult(300,intent);
                CityActivity.this.finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
            }
        });
    }

    private void getPageValue() {
        Bundle bundle=getIntent().getExtras();
        provenceId=bundle.getInt("provenceId");
    }

    private void relationDb() {
        File databaseFilename=new File(this.getFilesDir().getPath(), App.DB_NAME);
        Log.e("jxf", "文件是否存在" + databaseFilename.exists());
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
        citys=new ArrayList<CityBean>();
        Cursor cs=db.query("city", new String[]{"id","name"}, "province_id=?",new String[]{String.valueOf(provenceId)}, null, null, null);
        //db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy)
        CityBean cityBean=null;
        if(cs!=null&&cs.getCount()>0){
            while(cs.moveToNext()){
                cityBean=new CityBean();
                cityBean.cityId=cs.getInt(0);
                cityBean.cityName=cs.getString(1);

                citys.add(cityBean);
                Log.e("jxf", "验证是否取出long值" +"cityBean.cityId"+ cityBean.cityId+"cityBean.cityName"+cityBean.cityName);
            }
        }
        cs.close();
        db.close();
        Log.e("jxf", "集合数量" + citys.size());

    }

    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_city);

    }

    @Override
    protected void onClickEvent(View view) {
        switch (view.getId()){
            case R.id.activity_city_iv_goback:
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                break;
        }

    }


}
