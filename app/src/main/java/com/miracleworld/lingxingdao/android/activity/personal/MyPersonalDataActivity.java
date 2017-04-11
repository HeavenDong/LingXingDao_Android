package com.miracleworld.lingxingdao.android.activity.personal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.loopj.android.http.RequestParams;
import com.miracleworld.lingxingdao.android.App;
import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.activity.ScraleActivity;
import com.miracleworld.lingxingdao.android.activity.SubscriptionActivity;
import com.miracleworld.lingxingdao.android.adapter.MyPersonalGridAdapter;
import com.miracleworld.lingxingdao.android.base.BaseActivity;
import com.miracleworld.lingxingdao.android.bean.MyPersonalGridBean;
import com.miracleworld.lingxingdao.android.http.NetClient;
import com.miracleworld.lingxingdao.android.http.NetResponseHandler;
import com.miracleworld.lingxingdao.android.http.Url;
import com.miracleworld.lingxingdao.android.options.ImageLoaderOptions;
import com.miracleworld.lingxingdao.android.utils.CommanUtil;
import com.miracleworld.lingxingdao.android.utils.SharedPreUtils;
import com.miracleworld.lingxingdao.android.view.DefinedSingleToast;
import com.miracleworld.lingxingdao.android.view.myroundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * 个人资料
 */
public class MyPersonalDataActivity extends BaseActivity{
    private static final String TAG = "haifeng";
    private GridView personal_grid;
    private TextView personal_name;
    private ImageView personal_loading_iv;
    private RotateAnimation rotateAnimation;
    private RoundedImageView personal_head_img;
    private RelativeLayout personal_net_message,personal_not_message;
    private List<MyPersonalGridBean> gridList;
    private MyPersonalGridAdapter adapter;
    private  String portraitUrlBig;
    private String portraitUrlSmall;
    @Override
    protected void initView() {
        /**查无数据通知*/
        personal_not_message= (RelativeLayout)findViewById(R.id.personal_not_message);
        personal_not_message.setVisibility(View.GONE);
        /**网络不好通知*/
        personal_net_message= (RelativeLayout)findViewById(R.id.personal_net_message);
        personal_net_message.setVisibility(View.GONE);
        personal_grid= (GridView) findViewById(R.id.personal_grid);
        personal_name= (TextView) findViewById(R.id.personal_name);
        personal_head_img= (RoundedImageView) findViewById(R.id.personal_head_img);
        personal_head_img.setOnClickListener(this);
        personal_loading_iv= (ImageView)findViewById(R.id.personal_loading_iv);
        rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.rotate_refresh_drawable_default);
        findViewById(R.id.personal_title_left).setOnClickListener(this);
        initData();
    }

    private void initData() {
        String nickname = SharedPreUtils.get(App.getContext(), "user_nick_name","").toString();
        portraitUrlSmall = SharedPreUtils.get(App.getContext(), "user_small_avatar", "").toString();
        portraitUrlBig = SharedPreUtils.get(App.getContext(), "user_big_avatar", "").toString();
        Log.e(TAG, "本地头像portraitUrlSmall" + portraitUrlSmall+ "---　本地头像portraitUrlBig　　＋"+portraitUrlBig);
        if (!nickname.equals("")) {
            personal_name.setText(nickname);
        }else {
            personal_name.setText("未填写");
        }
        if (!portraitUrlSmall.equals("")) {
            ImageLoader.getInstance().displayImage(portraitUrlSmall, personal_head_img, ImageLoaderOptions.headOptions);
        }
        gridList= new ArrayList<MyPersonalGridBean>();
        adapter = new MyPersonalGridAdapter(this, gridList);
        personal_grid.setAdapter(adapter);
        personal_grid.setSelector(R.color.white);
        /**点击跳转已订阅页面*/
        personal_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                RequestParams params = new RequestParams();
                params.put("userId", (int) SharedPreUtils.get(App.getContext(), "user_id", 0));
                params.put("teacherId", gridList.get(position).teacherId);
                loadFindTeacherData(params);
            }
        });
        loadData();
    }

    @Override
    public void setContentLayout() {
        setContentView(R.layout.my_personal_data);

    }
    @Override
    protected void onClickEvent(View view) {
        switch (view.getId()){
            case R.id.personal_title_left:
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                break;
            case R.id.personal_head_img:
                Intent intent1=new Intent(MyPersonalDataActivity.this, ScraleActivity.class);
                intent1.putExtra("imgSmallUrl",portraitUrlSmall);
                intent1.putExtra("different","1");
                startActivity(intent1);
                overridePendingTransition(R.anim.small_to_big, R.anim.unchange);
                break;
        }
    }
    private void loadData() {
        if(CommanUtil.isNetworkAvailable()) {
        // 开始动画
        personal_loading_iv.setVisibility(View.VISIBLE);
        personal_loading_iv.setAnimation(rotateAnimation);

        RequestParams params = new RequestParams();
        params.put("userId", (int) SharedPreUtils.get(App.getContext(), "user_id", 0));
        Log.e(TAG, "个人资料上传服务器" + params);
        NetClient.headGet(this, Url.LOOK_USERINFO_URL, params, new NetResponseHandler() {
            @Override
            public void onResponse(String json) {
                Log.e(TAG, "服务器返回" + json);
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    String status = jsonObject.optString("status");
                    String errorCode = jsonObject.getString("errorCode");
                    if (status != null && status.equals("1")) {
                        JSONArray jsonArray = jsonObject.optJSONArray("datas");
                        if (jsonArray != null) {
                            ArrayList<MyPersonalGridBean> temp = new ArrayList<MyPersonalGridBean>();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.optJSONObject(i);
                                MyPersonalGridBean gridBean = new MyPersonalGridBean();
                                gridBean.teacherId = obj.getInt("teacherId");
                                gridBean.portraitUrlSmall = obj.getString("portraitUrlSmall");
                                gridBean.portraitUrlBig = obj.getString("portraitUrlBig");
                                gridBean.teacherName = obj.getString("teacherName");
                                temp.add(gridBean);
                            }
                            gridList.addAll(temp);
                            adapter.notifyDataSetChanged();
                            //动画关闭
                            personal_loading_iv.clearAnimation();
                            personal_loading_iv.setVisibility(View.INVISIBLE);
                        }
                    } else {

                        /**0=成功，
                         2=查无数据,
                         11007=用户id为空，
                         90001=系统异常*/
                        //动画关闭
                        personal_loading_iv.clearAnimation();
                        personal_loading_iv.setVisibility(View.INVISIBLE);
                        if (errorCode.equals("2")) {
                            personal_not_message.setVisibility(View.VISIBLE);
                        }
                        if (errorCode.equals("11007")) {
                            DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.regist_text26));
                        }
                        if (errorCode.equals("90001")) {
                            DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.regist_text24));
                        }
                    }
                } catch (JSONException e) {
                    Log.e("jxf","个人资料，解析服务返回JSONObject时异常");
                    DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.regist_text24));
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                //动画关闭
                personal_loading_iv.clearAnimation();
                personal_loading_iv.setVisibility(View.INVISIBLE);
                //让网络不好的页面出来
                personal_net_message.setVisibility(View.VISIBLE);
                personal_net_message.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        personal_net_message.setVisibility(View.GONE);

                        loadData();

                    }
                });

            }
        });
        }else {
            //让网络不好的页面出来
            personal_net_message.setVisibility(View.VISIBLE);
            personal_net_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    personal_net_message.setVisibility(View.GONE);

                    loadData();

                }
            });
        }

    }
    /**内容详情--头部老师信息接口
     * @param params
     */
    private void loadFindTeacherData(RequestParams params) {
        if(CommanUtil.isNetworkAvailable()) {
            Log.e(TAG, "内容详情--头部老师信息接口上传服务器" + params);
            NetClient.headGet(this,Url.CONTENT_DETAIL_HEAD_URL, params, new NetResponseHandler() {
                @Override
                public void onResponse(String json) {
                    Log.e(TAG, "内容详情--头部老师信息接口服务器返回" + json);
                    try {
                        JSONObject jsonObject =  new JSONObject(json);
                        String status= jsonObject.optString("status");
                        String errorCode  = jsonObject.getString("errorCode");
                        if (status.equals("1")) {
                            String datas = jsonObject.getString("datas");
                            JSONObject  jsonObject2 = new JSONObject(datas);
                            Intent intent=new Intent(MyPersonalDataActivity.this,SubscriptionActivity.class);
                            Bundle bundle=new Bundle();
                            bundle.putInt("teacherId", jsonObject2.getInt("teacherId"));
                            bundle.putString("portraitUrlSmall", jsonObject2.getString("portraitUrlSmall"));
                            bundle.putString("portraitUrlBig",jsonObject2.getString("portraitUrlBig"));
                            bundle.putString("nickname", jsonObject2.getString("nickname"));
                            bundle.putString("catgoryName", jsonObject2.getString("catgoryName"));
                            bundle.putString("catgoryId", jsonObject2.getString("catgoryId"));
                            bundle.putString("remark", jsonObject2.getString("remark"));

                            bundle.putString("pricerange",jsonObject2.getString("priceRange"));

                            intent.putExtras(bundle);
                            startActivity(intent);
                            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                        }else {
                            if (errorCode.equals("2")) {
                                DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text27));
                            }
                            if (errorCode.equals("90001")) {
                                DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text24));
                            }
                        }
                    } catch (JSONException e) {
                        DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text24));
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.toast_net));
                }
            });
        }else {
            DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.toast_net));
        }

    }
}
