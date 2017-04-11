package com.miracleworld.lingxingdao.android.activity.my;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;

import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import com.loopj.android.http.RequestParams;
import com.miracleworld.lingxingdao.android.activity.CropActivity;
import com.miracleworld.lingxingdao.android.activity.ProvenceActivity;
import com.miracleworld.lingxingdao.android.activity.ScraleActivity;
import com.miracleworld.lingxingdao.android.activity.login.LoginActivity;
import com.miracleworld.lingxingdao.android.base.BaseActivity;
import com.miracleworld.lingxingdao.android.http.NetClient;
import com.miracleworld.lingxingdao.android.http.NetResponseHandler;
import com.miracleworld.lingxingdao.android.http.Url;
import com.miracleworld.lingxingdao.android.options.ImageLoaderOptions;
import com.miracleworld.lingxingdao.android.utils.CommanUtil;
import com.miracleworld.lingxingdao.android.utils.SelectPicPopupWindow;
import com.miracleworld.lingxingdao.android.utils.SharedPreUtils;
import com.miracleworld.lingxingdao.android.view.DefinedSingleToast;
import com.miracleworld.lingxingdao.android.view.myroundedimageview.RoundedImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.miracleworld.lingxingdao.android.App;
import com.miracleworld.lingxingdao.android.R;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;

/**
 * 个人信息编辑页面
 */
public class MyPersonEditActivity extends BaseActivity {
    private static final int REQUEST_CODE_IMAGE_CAPTURE = 2;
    private static final int REQUEST_CODE_CHOOSE_IMAGE = 3;
    private static final int CROP_REQUEST_CODE = 5;
    private TextView my_username,my_nickname, myPersonAddressEdit,my_person_phone;
    private RoundedImageView myPersonAvatarImg;
    private ImageView myname_arraw;
    private File outPutPhoto;
    private Uri photoUri;
    private File outPutPhotos;
    private File imagePath;
    //自定义的弹出框类
    SelectPicPopupWindow menuWindow;
    private Uri uri;
    private ImageView user_change_iv;
    private RotateAnimation rotateAnimation;

    private String userName;
    private  String nickname;
    private String  province;
    private String  city;
    //private String portraitUrlBig;

    @Override
    protected void initView() {
        /**正在加载图片*/
        user_change_iv= (ImageView)findViewById(R.id.user_change_iv);
        /**旋转动画*/
        rotateAnimation = (RotateAnimation) AnimationUtils.loadAnimation(this, R.anim.rotate_refresh_drawable_default);
        myPersonAvatarImg = (RoundedImageView) findViewById(R.id.my_avatar_img);
        my_username = (TextView) findViewById(R.id.my_username);
        myname_arraw = (ImageView) findViewById(R.id.myname_arraw);
        my_nickname = (TextView) findViewById(R.id.my_nickname);
        my_person_phone = (TextView) findViewById(R.id.my_person_phone);
        myPersonAddressEdit = (TextView) findViewById(R.id.my_person_address);
        ButtonSetOnclickListner();
        getUserInfo();
    }

    private void ButtonSetOnclickListner() {
        findViewById(R.id.my_avatar_img).setOnClickListener(this);
        findViewById(R.id.my_person_avatar).setOnClickListener(this);
        findViewById(R.id.my_person_username).setOnClickListener(this);
        findViewById(R.id.my_person_nickname).setOnClickListener(this);
        findViewById(R.id.my_person_editaddress).setOnClickListener(this);
        findViewById(R.id.my_backlogin).setOnClickListener(this);
        findViewById(R.id.my_title_left).setOnClickListener(this);
    }
    @Override
    public void setContentLayout() {
        setContentView(R.layout.my_person_edit);

    }
    private void getUserInfo() {

        String mobile = SharedPreUtils.get(App.getContext(),  "user_mobile","未填写").toString();
        userName = SharedPreUtils.get(App.getContext(), "user_name", "未填写").toString();
        nickname = SharedPreUtils.get(App.getContext(), "user_nick_name", "未填写").toString();
        String portraitUrlSmall = SharedPreUtils.get(App.getContext(), "user_small_avatar", "").toString();
        String  isUpdate = SharedPreUtils.get(App.getContext(), "isUpdate", "0").toString();
        String  country = SharedPreUtils.get(App.getContext(), "country", "").toString();
        province = SharedPreUtils.get(App.getContext(), "province", "").toString();
        city = SharedPreUtils.get(App.getContext(), "city", "").toString();
        int userId= (int)SharedPreUtils.get(App.getContext(), "user_id", 0);
        if (!userName.equals("")) {
            my_username.setText(userName);
        }
        if (!nickname.equals("")) {
            my_nickname.setText(nickname);
        }
        if (!portraitUrlSmall.equals("")) {
            ImageLoader.getInstance().displayImage(portraitUrlSmall, myPersonAvatarImg, ImageLoaderOptions.headOptions);
        }
        if (!mobile.equals("")) {
            my_person_phone.setText(mobile);
        }
        if (province.equals("")&&country.equals("")){
            myPersonAddressEdit.setText(getResources().getString(R.string.my_text13));
        }else{
            myPersonAddressEdit.setText(province + "-" + city);
        }
        /**这里限制用户名只能修改一次*/
        if (!isUpdate.equals("0")) {
            myname_arraw.setVisibility(View.GONE);
            findViewById(R.id.my_person_username).setClickable(false);
        }
    }

    @Override
    protected void onClickEvent(View view) {
        switch (view.getId()) {
            /**点击看大图*/
            case R.id.my_avatar_img:
                 String portraitUrlSmall = SharedPreUtils.get(App.getContext(), "user_small_avatar", "").toString();
                Intent intent1=new Intent(MyPersonEditActivity.this, ScraleActivity.class);
                intent1.putExtra("imgSmallUrl",portraitUrlSmall);
                intent1.putExtra("different","2");
                startActivity(intent1);
                overridePendingTransition(R.anim.righttop_small_to_big, R.anim.unchange);
                break;
            /**编辑头像*/
            case R.id.my_person_avatar:
                menuWindow = new SelectPicPopupWindow(this, itemsOnClick, R.layout.my_addpic_pop);
                menuWindow.showAtLocation(this.findViewById(R.id.my_rl_main_edit), Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在
                break;
            /**编辑用户名*/
            case R.id.my_person_username:
                Intent intent = new Intent(MyPersonEditActivity.this, MyUserNameEditActivity.class);
                startActivityForResult(intent, 504);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;

            /**编辑昵称*/
            case R.id.my_person_nickname:
                Intent intent3 = new Intent(MyPersonEditActivity.this, MyNickNameEditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("oldeName",my_nickname.getText().toString());
                intent3.putExtras(bundle);
                startActivityForResult(intent3, 501);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            /**编辑地址*/
            case R.id.my_person_editaddress:
                Intent intent2 = new Intent(MyPersonEditActivity.this, ProvenceActivity.class);
                startActivityForResult(intent2, 503);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            /**退出登录*/
            case R.id.my_backlogin:
                final AlertDialog dialog=  new AlertDialog.Builder(MyPersonEditActivity.this).create();
                dialog.show();
                dialog.setCancelable(true);
                Window window=dialog.getWindow();
                View dialogView=View .inflate(MyPersonEditActivity.this, R.layout.my_finish_dialog, null);
                window.setContentView(dialogView);

                dialogView.findViewById(R.id.finish_dialog_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialogView.findViewById(R.id.finish_dialog_sure).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
//                      SharedPreUtils.clear(App.getContext());
                        SharedPreUtils.remove(App.getContext(), "user_id");
                        SharedPreUtils.remove(App.getContext(), "isUpdate");
                        dialog.dismiss();
                        Intent intent = new Intent(MyPersonEditActivity.this, LoginActivity.class);
                        startActivity(intent);
                        setResult(1103);
                        finish();
                    }
                });


                break;
            /**返回*/
            case R.id.my_title_left:
                finish();
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            /**拍照*/
            case REQUEST_CODE_IMAGE_CAPTURE:
                if (resultCode == RESULT_OK) {
//                    uri =saveImageToDICM(getApplicationContext(),
//                            outPutPhoto);
//                    if (uri == null) {
//                        return;
//                    }
//                    Log.e("jxf","打印保存照片地址uri"+uri.toString());
                    Log.e("jxf","打印保存照片地址path"+outPutPhoto.getAbsolutePath());
                    int degree=getBitmapDegree(outPutPhoto.getAbsolutePath());
                    Log.e("jxf","打印旋转尺寸"+degree);
                    //使用大图做尺寸压缩
                    BitmapFactory.Options options = new BitmapFactory.Options();
//                    // 从解码器中获取原始图片的宽高，这样避免了直接申请内存空间
//                    options.inJustDecodeBounds = true;
                    // Calculate inSampleSize
                    options.inSampleSize = 5;
//                    // 压缩完后便可以将inJustDecodeBounds设置为false了。
//                    options.inJustDecodeBounds = false;
                    options.inPreferredConfig = Bitmap.Config.RGB_565;
                    //做尺寸压缩
                    Log.e("jxf","做尺寸压缩");
                    Bitmap bitmap = BitmapFactory.decodeFile(outPutPhoto.getAbsolutePath(), options);
                    Log.e("jxf","bitmap大小打印"+(bitmap.getRowBytes() * bitmap.getHeight()));
                    // 根据旋转角度，生成旋转矩阵
                    Matrix matrix = new Matrix();
                    matrix.postRotate(degree);
                    Log.e("jxf", "做角度变换了");
                    Bitmap returnBm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                    Log.e("jxf","returnBm大小打印"+(returnBm.getRowBytes() * returnBm.getHeight()));
                    //压缩进原来的文件中
                    File imagePath = new File(getCacheDir(), "" + Calendar.getInstance().getTimeInMillis());// /sdcard/aijingxi/cropImage
                    File parentFile = imagePath.getParentFile();
                    if (!parentFile.exists()) {// 判断上级目录是否存在，不存在就需要创建
                        parentFile.mkdirs();
                    }
                    // 把Bitmap对象持久化到本地
                    OutputStream os = null;
                    try {
                        os = new FileOutputStream(imagePath);
                        returnBm.compress(Bitmap.CompressFormat.JPEG, 100, os);
                        Log.e("jxf", "做质量压缩压缩进硬盘");
                        os.flush();
                        os.close();
                    } catch (FileNotFoundException e) {
                        Log.e("jxf","切图类1--bitmap图片压缩时，报FileNotFoundException异常");
                        e.printStackTrace();
                    } catch (IOException e) {
                        Log.e("jxf","切图类1--bitmap图片压缩时，报IOException异常");
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
                    //释放
                    if (returnBm != null && !returnBm.isRecycled()) {
                        // 回收并且置为null
                        returnBm.recycle();
                        returnBm = null;
                        Log.e("jxf", "returnBm回收");
                    }
                    System.gc();
                    uri =saveImageToDICM(getApplicationContext(),
                            imagePath);
                    Intent intent = new Intent(this, CropActivity.class);
                    intent.putExtra("uri", uri.toString());
                    Log.e("jxf","传递的uri=="+ uri.toString());
                    startActivityForResult(intent, CROP_REQUEST_CODE);
                }
                break;
            /**本地相册*/
            case REQUEST_CODE_CHOOSE_IMAGE:
                if (resultCode == RESULT_OK) {
                    Uri uri;
                    uri = data.getData();
                    if (data == null) {
                        return;
                    }
                    photoUri = uri;
                    Intent intent = new Intent(this, CropActivity.class);
                    intent.putExtra("uri", uri.toString());
                    startActivityForResult(intent, CROP_REQUEST_CODE);
                }
                break;
            /**切图*/
            case CROP_REQUEST_CODE:
                if (data == null) {
                    return;
                }
                String imageCropuri = data.getStringExtra("Cropuri");
                Log.e("jxf","打印切图返回的imageCropuri"+imageCropuri);
                if (imageCropuri != null) {
//                    ImageLoader.getInstance().displayImage("file://" + imageCropuri, myPersonAvatarImg, ImageLoaderOptions.headOptions);

                    outPutPhotos = new File(imageCropuri);
                    //请求网络
                    if(CommanUtil.isNetworkAvailable()) {
                        changUserHeadPhoto();
                    }else {
                        DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.toast_net));
                    }
                }
                break;
            /**编辑用户名*/
            case 504:
                if (resultCode == 544) {
                    userName=data.getExtras().getString("userName");
                    RequestParams params = new RequestParams();
                    params.put("userName",userName);
                    changUserInfo(params, 1);
                }
                /**编辑昵称*/
            case 501:
                if (resultCode == 541) {
                    nickname=data.getExtras().getString("nickName");
                    RequestParams params = new RequestParams();
                    params.put("nickname", nickname);
                    changUserInfo(params,2);
                }
                break;
            /**编辑地址*/
            case 503:
                if (resultCode == 543) {
                    province = data.getStringExtra("provenceName");
                    city = data.getStringExtra("cityName");
                    int countryId=data.getIntExtra("countryId",0);
                    int provenceId=data.getIntExtra("provenceId", 0);
                    int cityId=data.getIntExtra("cityId", 0);

                    RequestParams params = new RequestParams();
                    params.put("country", countryId);
                    params.put("province", provenceId);
                    params.put("city", cityId);
                    changUserInfo(params,3);

                }
                break;
        }


    }
    /**修改用户信息*/
    private void changUserInfo(RequestParams params, final int type) {
        if(CommanUtil.isNetworkAvailable()) {
            // 开始动画
            user_change_iv.setAnimation(rotateAnimation);
            user_change_iv.setVisibility(View.VISIBLE);
            params.put("userId", (int) SharedPreUtils.get(App.getContext(), "user_id", 0));
            NetClient.headPost(this, Url.CHANGE_USERINFO_URL, params, new NetResponseHandler() {
                @Override
                public void onResponse(String json) {
                    //动画关闭
                    user_change_iv.clearAnimation();
                    user_change_iv.setVisibility(View.GONE);
                    try {
                        JSONObject jsonObject = new JSONObject(json);
                        int status = jsonObject.getInt("status");
                        if (status == 1) {
                            setResult(1101);
                            if (type==1){
                                /**修改用户名*/
                                my_username.setText(userName);
                                SharedPreUtils.put(App.getContext(), "user_name",userName);
                                SharedPreUtils.put(App.getContext(), "isUpdate", "1");
                                myname_arraw.setVisibility(View.GONE);
                                findViewById(R.id.my_person_username).setClickable(false);

                            }
                            if (type==2) {
                                /**修改名字*/
                                my_nickname.setText(nickname);
                                SharedPreUtils.put(App.getContext(), "user_nick_name", nickname);
                            }
                            if (type==3) {
                                /**修改地区*/
                                myPersonAddressEdit.setText(province + "-" + city);
                                SharedPreUtils.put(App.getContext(), "city", city);
                                SharedPreUtils.put(App.getContext(), "province", province);
                            }

                        } else {
                            setResult(1102);
                            DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.forget_toast5));
                        }
                    } catch (JSONException e) {
                        Log.e("jxf","个人中心-修改用户信息，解析服务返回JSONObject时异常");
                        setResult(1102);
                        DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.forget_toast5));
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    //动画关闭
                    user_change_iv.clearAnimation();
                    user_change_iv.setVisibility(View.GONE);
                    DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.forget_toast5));
                    setResult(1102);
                }
            });

        }else {
            setResult(1102);
            DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.toast_net));
        }
    }


    /**修改用户头像*/
    private void changUserHeadPhoto() {
        if(CommanUtil.isNetworkAvailable()) {
            // 开始动画
            user_change_iv.setAnimation(rotateAnimation);
            user_change_iv.setVisibility(View.VISIBLE);
            RequestParams params = new RequestParams();
            params.put("enctype", "multipart/form-data");
            params.put("id",(int) SharedPreUtils.get(App.getContext(), "user_id", 0));
            Log.e("jxf","");
            try {
                params.put("headImg",  outPutPhotos.getName(),outPutPhotos);
            } catch (FileNotFoundException e) {
                Log.e("jxf","个人中心-修改用户头像，找不到文件图片时报FileNotFoundException异常");
                e.printStackTrace();
            }
            NetClient.headPost(this,Url.CHANGE_HEAD_URL, params, new NetResponseHandler() {
                @Override
                public void onResponse(String json) {
                    //动画关闭
                    user_change_iv.clearAnimation();
                    user_change_iv.setVisibility(View.GONE);
                    JSONObject jSONObject = null;
                    try {
                        jSONObject = new JSONObject(json);
                        String status = jSONObject.optString("status");
                        String errorCode = jSONObject.optString("errorCode");
                        if (status.equals("1")) {
                            String datas = jSONObject.optString("datas");
                            JSONObject jsonObject2 = new JSONObject(datas);
                            setResult(1101);
                            String ImgUrl= jsonObject2.optString("url");
                            ImageLoader.getInstance().displayImage(ImgUrl, myPersonAvatarImg, ImageLoaderOptions.headOptions);
                            SharedPreUtils.put(App.getContext(), "user_small_avatar", ImgUrl);
                            SharedPreUtils.put(App.getContext(), "user_big_avatar",ImgUrl);

                        }else {
                            setResult(1102);
                            if (errorCode.equals("90001")) {
                                DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.regist_text24));
                            }
                        }
                    } catch (JSONException e) {
                        Log.e("jxf","个人中心-修改用户头像，解析服务返回JSONObject时异常");
                        setResult(1102);
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(int i, Header[] headers, byte[] bytes, Throwable throwable) {
                    setResult(1102);
                    //动画关闭
                    user_change_iv.clearAnimation();
                    user_change_iv.setVisibility(View.GONE);
                    DefinedSingleToast.showToast(App.getContext(), getResources().getString(R.string.forget_toast5));
                }
            });
        }else {
            setResult(1102);
            DefinedSingleToast.showToast(App.getInstance(), getResources().getString(R.string.toast_net));
        }
    }

    public Uri getOutputPhoto() {
        if (photoUri == null || outPutPhoto == null) {
            String fileName = ""+Calendar.getInstance().getTimeInMillis()+".JPG";
            outPutPhoto = new File(getDiskCacheDir(this, "data"),
                    fileName);
            photoUri = Uri.fromFile(outPutPhoto);
        }
        return photoUri;
    }

    //为弹出窗口实现监听类
    private View.OnClickListener itemsOnClick =  new View.OnClickListener() {

        public void onClick(View v) {

            switch (v.getId()) {
                /**开启相机*/
                case R.id.btn_take_photo:
                    //防止内存溢出
                    Log.e("jxf", "ImageLoader清理内存缓存");
                    ImageLoader.getInstance().clearMemoryCache();
                    Intent intent5 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    intent5.putExtra(MediaStore.EXTRA_OUTPUT, getOutputPhoto());
                    startActivityForResult(intent5, REQUEST_CODE_IMAGE_CAPTURE);
                    menuWindow.dismiss();
                    break;
                /**开启相册*/
                case R.id.btn_pick_photo:
                    imagePath = null;
                    Intent intent4 = new Intent(Intent.ACTION_PICK);
                    intent4.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            "image/*");
                    startActivityForResult(intent4, REQUEST_CODE_CHOOSE_IMAGE);
                    menuWindow.dismiss();
                    break;
                /**关闭PopupWindow*/
                case R.id.cancel_addhead:
                    menuWindow.dismiss();
                    break;
                default:
                    break;
            }


        }
    };

    private int getBitmapDegree(String path) {
        int degree = 0;
        try {
            // 从指定路径下读取图片，并获取其EXIF信息
            ExifInterface exifInterface = new ExifInterface(path);
            // 获取图片的旋转信息
            int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            Log.e("jxf","orientation"+orientation);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    degree = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    degree = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    degree = 270;
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return degree;
    }

    /**
     * 获取磁盘缓存路径
     * @param name 文件夹名称
     * @return 缓存路径
     */
    private   File getDiskCacheDir(Context context, String name) {
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            File file = new File(context.getExternalCacheDir(), name);
            if (!file.exists())
                file.mkdirs();
            return file;
        } else {
            File file = new File(context.getCacheDir(), name);
            if (!file.exists())
                file.mkdirs();
            return file;
        }
    }


    /**
     * 保存图片到系统相册
     * @param image
     */
    private Uri saveImageToDICM(Context context, File image) {
        Uri uri = null;
        // 保存到相册
        try {
            String uriString = MediaStore.Images.Media.insertImage(
                    context.getContentResolver(), image.getAbsolutePath(),
                    image.getName(), image.getName());

            uri = Uri.parse(uriString);
//            // 通知系统相册刷新
//            context.sendBroadcast(new Intent(
//                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(image)));
        } catch (FileNotFoundException e) {
            Log.e("jxf","捕捉异常FileNotFoundException");

        }
        return uri;
    }
}
