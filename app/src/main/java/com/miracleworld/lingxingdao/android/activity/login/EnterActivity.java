package com.miracleworld.lingxingdao.android.activity.login;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import com.miracleworld.lingxingdao.android.App;
import com.miracleworld.lingxingdao.android.R;
import com.miracleworld.lingxingdao.android.activity.MainActivity;
import com.miracleworld.lingxingdao.android.base.BaseActivity;
import com.miracleworld.lingxingdao.android.http.Url;
import com.miracleworld.lingxingdao.android.utils.CommanUtil;
import com.miracleworld.lingxingdao.android.utils.SharedPreUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


/**启动页*/

public class EnterActivity extends BaseActivity {
    private Handler handler=   new Handler();
    @Override
    protected void initView() {
        String text0=(String)SharedPreUtils.get(getApplicationContext(),"list","");
        Log.e("jxf","welcome页请求分类列表之前：取本地list分类列表"+text0);
        ConnectivityManager mConnectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if (info == null || !mConnectivity.getBackgroundDataSetting()) {
            Log.e("jxf", "启动页请求分类列表：没网");
        }
        else{
            Log.e("jxf", "启动页请求分类列表：有网");
            //错误日志上传服务器
            //首先判断文件在不在：在的才去上传：上传字符串：用post提交方式
            if (fileIsExists(App.getContext().getFilesDir().getAbsolutePath()+"/errorlog.txt")){
                Log.e("jxf","errorlog存在");
                File file =new File(App.getContext().getFilesDir().getAbsolutePath()+"/errorlog.txt");
                String contents="";
                try {
                    InputStream stream=new FileInputStream(file);
                    BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(stream));
                    String line;
                    while (( line = bufferedReader.readLine()) != null) {
                        contents += line + "\n";
                    }
                    stream.close();
                    final String logStr=contents;
                    final Thread log=new Thread(){
                        @Override
                        public void run() {
                            //上传给服务器
                            HttpClient client = new DefaultHttpClient();
                            HttpPost post = new HttpPost(Url.LOG_COMMIT);
                            try {
                                List<NameValuePair> parameters = new ArrayList<NameValuePair>();
                                BasicNameValuePair bnvp = new BasicNameValuePair("logStr", logStr);
                                parameters.add(bnvp);
                                UrlEncodedFormEntity entity = new UrlEncodedFormEntity(parameters, "utf-8");
                                //为post请求设置实体
                                post.setEntity(entity);
                                HttpResponse response = client.execute(post);
                                if(response.getStatusLine().getStatusCode() == 200){
                                    Log.e("jxf","错误日志上传成功");
                                    File fileold =new File(App.getContext().getFilesDir().getAbsolutePath()+"/errorlog.txt");
                                    fileold.delete();
                                    Log.e("jxf","日志文件删除");
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    };
                    log.start();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            Thread t = new Thread(){
                @Override
                public void run() {
                    BasicHttpParams httpParams = new BasicHttpParams();
                    HttpConnectionParams.setConnectionTimeout(httpParams, 5000);
                    HttpClient client = new DefaultHttpClient(httpParams);
                    HttpGet get = new HttpGet(Url.MAIN_CATEGORY);
                    try {
                        HttpResponse response = client.execute(get);
                        StatusLine line = response.getStatusLine();
                        int code = line.getStatusCode();
                        if(code == 200){
                            HttpEntity entity = response.getEntity();
                            InputStream is = entity.getContent();
                            String text = CommanUtil.getTextFromStream(is);
                            JSONObject jSONObjectAll=new JSONObject(text);
                            String status=jSONObjectAll.optString("status");
                            if (status.equals("1")){
                                SharedPreUtils.put(getApplicationContext(),"list",text);
                                String text1=(String)SharedPreUtils.get(getApplicationContext(),"list","");
                                Log.e("jxf", "welcome页面httpclient得到网络的流+category存储完毕==打印category"+text1);
                                Log.e("jxf","welcome页面请求成功");
                            }
                        }
                        //链接失败
                        else{
                            //没有网络的网络超时：200以外直接走catch了
                            Log.e("jxf","启动页面httpclient请求数据本地缓存category:200以外：category此次本地没有保存、替换！");
                        }
                    } catch (Exception e) {
                        Log.e("jxf","启动页面httpclient请求数据本地缓存category异常了："+e.toString());
                        e.printStackTrace();
                    }
                }
            };
            t.start();
        }

        long loginTime = (long)SharedPreUtils.get(App.getContext(), "login_time",(long)1);
        final int userId= (int)SharedPreUtils.get(App.getContext(), "user_id", 0);


        final long standard=System.currentTimeMillis()-loginTime;
        handler.postDelayed(new Runnable() {

            public void run() {
                if (userId==0) {
                    Intent intent = new Intent(EnterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                    /**30天未进入app*/
                } else if(standard>(long)30*24*60*60*1000){
                    Intent intent = new Intent(EnterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    SharedPreUtils.put(App.getContext(), "login_time",System.currentTimeMillis());
                    Intent intent = new Intent(EnterActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);

    }

    @Override
    public void setContentLayout() {
        setContentView(R.layout.enter_activity);
    }

    @Override
    protected void onClickEvent(View view) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        /**如果参数为null的话，会将所有的Callbacks和Messages全部清除掉,避免内存泄露*/
        handler.removeCallbacksAndMessages(null);
    }

    public boolean fileIsExists(String strFile)
    {
        try
        {
            File f=new File(strFile);
            if(!f.exists())
            {
                Log.e("jxf","文件不存在");
                return false;
            }
            Log.e("jxf","文件存在");

        }
        catch (Exception e)
        {
            Log.e("jxf","文件不存在");
            return false;
        }

        return true;
    }

}
