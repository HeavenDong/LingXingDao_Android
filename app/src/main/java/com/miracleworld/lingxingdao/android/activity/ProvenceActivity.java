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
import com.miracleworld.lingxingdao.android.adapter.ProvenceActivityAdapter;
import com.miracleworld.lingxingdao.android.base.BaseActivity;
import com.miracleworld.lingxingdao.android.bean.ProvenceBean;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by donghaifeng on 2015/12/23.
 */
public class ProvenceActivity extends BaseActivity{

    //保存省的集合
    private ArrayList<ProvenceBean> provences;
    //保存跳转需要传递的值
    private int provenceId;
    private String provenceName;
    private int countryId;


    @Override
    protected void initView() {
        //标题返回键
        findViewById(R.id.activity_provence_iv_goback).setOnClickListener(this);
        //省列表
        ListView activity_provence_lv= (ListView) findViewById(R.id.activity_provence_lv);
        relationDb();
        ProvenceActivityAdapter adapter=new ProvenceActivityAdapter(this,provences);
        activity_provence_lv.setAdapter(adapter);
        activity_provence_lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                provenceId = provences.get(position).provenceId;
                provenceName = provences.get(position).provenceName;
                countryId = provences.get(position).countryId;


                //携带省的id进行跳转Startactivityforresult
                Intent intent = new Intent(ProvenceActivity.this, CityActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("provenceId", provenceId);
                intent.putExtras(bundle);
                startActivityForResult(intent, 200);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

            }
        });
    }

    //关联本地数据库
    private void relationDb() {
        File databaseFilename=new File(this.getFilesDir().getPath(),App.DB_NAME);
        Log.e("jxf","文件是否存在"+databaseFilename.exists());
        SQLiteDatabase db = SQLiteDatabase.openOrCreateDatabase(databaseFilename, null);
        provences=new ArrayList<ProvenceBean>();
        Cursor cs=db.query("province", new String[]{"id","name","country_id"}, null,null, null, null, null);
        //db.query(table, columns, selection, selectionArgs, groupBy, having, orderBy)
        ProvenceBean provenceBean=null;
        if(cs!=null&&cs.getCount()>0){
            while(cs.moveToNext()){
                provenceBean=new ProvenceBean();
                provenceBean.provenceId=cs.getInt(0);
                provenceBean.provenceName=cs.getString(1);
                provenceBean.countryId=cs.getInt(2);
                provences.add(provenceBean);
                Log.e("jxf", "验证是否取出long值" +"provenceBean.provenceId"+ provenceBean.provenceId+"provenceBean.countryId"+provenceBean.countryId);
            }
        }
        cs.close();
        db.close();
        Log.e("jxf", "集合数量" + provences.size());


    }

    @Override
    public void setContentLayout() {
        setContentView(R.layout.activity_provence);
    }

    @Override
    protected void onClickEvent(View view) {
        switch (view.getId()){
            //标题返回键
            case R.id.activity_provence_iv_goback:
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==200&&resultCode==300){
            String cityName=data.getStringExtra("cityName");
            int cityId=data.getIntExtra("cityId", 0);
            Log.e("jxf","cityName"+cityName);
            Log.e("jxf","cityId"+cityId);
/*            //543
            String userAddress=provenceName+"-"+cityName;*/

            Intent intent = new Intent();
            intent.putExtra("provenceName", provenceName);
            intent.putExtra("cityName", cityName);
            intent.putExtra("countryId", countryId);
            intent.putExtra("provenceId", provenceId);
            intent.putExtra("cityId", cityId);
            ProvenceActivity.this.setResult(543, intent);
            ProvenceActivity.this.finish();
            overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);


        }

    }

}

