package com.miracleworld.lingxingdao.android.utils;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import com.miracleworld.lingxingdao.android.App;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by donghaifeng on 2015/11/16.
 */
public class CommanUtil {


    private static final char[] DIGITS_LOWER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * Used to build output as Hex
     */
    private static final char[] DIGITS_UPPER =
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};


    /**
     *   MD5加密
     */
    public static String md5Hex(String planText, boolean toLowerCase) {
        try {
            byte[] data = MessageDigest.getInstance("MD5").digest(planText.getBytes("UTF-8"));
            final int l = data.length;
            final char[] out = new char[l << 1];

            char[] toDigits = toLowerCase ? DIGITS_LOWER : DIGITS_UPPER;
            // two characters form the hex value.
            for (int i = 0, j = 0; i < l; i++) {
                out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
                out[j++] = toDigits[0x0F & data[i]];
            }
            return new String(out);
        } catch (UnsupportedEncodingException | NoSuchAlgorithmException ignored) {

        }
        return null;
    }

    /**
     * 验证输入代金卡号码---正则验证
     */
    public static boolean isGiftCade(String str) {
        String regex = "^[A-Z0-9]{16}$";
        return match(regex, str);
    }
    /**
     * 验证输入手机号码---正则验证
     */
    public static boolean isMobilePhone(String str) {
        String regex = "^(((1[3,4,5,6,7,8][0-9]{1})|170)+\\d{8})$";
        return match(regex, str);
    }
    /**
     * 验证输入密码---正则验证
     */
    public static boolean isPSW(String str) {
        String regex = "^[0-9A-Za-z]{6,20}$";
        return match(regex, str);
    }
    /**
     * 用户名---正则验证
     */
    public static boolean isUserName(String str) {
        String regex = "^[A-Za-z]+[-_0-9A-Za-z]{5,11}$";// ^[-_a-zA-Z0-9]+$
        return match(regex, str);
    }
    /**
     * 验证输入代理人---正则验证
     */
    public static boolean isAgent(String str) {
        String regex = "^[\u4E00-\u9FA5A-Za-z0-9_]{0,100}$";
        return match(regex, str);
    }
    private static boolean match(String regex, String str) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.matches();
    }

    /**
     * 检查当前网络是否可用
     * return  boolean
     */
    public static boolean isNetworkAvailable()
    {
        Context context = App.getContext();
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager == null)
        {
            return false;
        }
        else
        {
            // 获取NetworkInfo对象
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

            if (networkInfo != null && networkInfo.length > 0)
            {
                for (int i = 0; i < networkInfo.length; i++)
                {
                    System.out.println(i + "===状态===" + networkInfo[i].getState());
                    System.out.println(i + "===类型===" + networkInfo[i].getTypeName());
                    // 判断当前网络状态是否为连接状态
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
                }
            }
        }
        return false;
    }
//    /**
//     * 获取磁盘缓存路径
//     * @param name 文件夹名称
//     * @return 缓存路径
//     */
//    public static File getDiskCacheDir(Context context, String name) {
//        if (Environment.MEDIA_MOUNTED.equals(Environment
//                .getExternalStorageState())
//                || !Environment.isExternalStorageRemovable()) {
//            File file = new File(context.getExternalCacheDir(), name);
//            if (!file.exists())
//                file.mkdirs();
//            return file;
//        } else {
//            File file = new File(context.getCacheDir(), name);
//            if (!file.exists())
//                file.mkdirs();
//            return file;
//        }
//    }
//    /**
//     * 保存图片到系统相册
//     * @param image
//     */
//    public static Uri saveImageToDICM(Context context, File image) {
//        Uri uri = null;
//        // 保存到相册
//        try {
//            String uriString = MediaStore.Images.Media.insertImage(
//                    context.getContentResolver(), image.getAbsolutePath(),
//                    image.getName(), image.getName());
//
//            uri = Uri.parse(uriString);
////            // 通知系统相册刷新
////            context.sendBroadcast(new Intent(
////                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(image)));
//        } catch (FileNotFoundException e) {
//            Log.e("jxf","捕捉异常FileNotFoundException");
//
//        }
//        return uri;
//    }



    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     *
     * **/
    public static String getTextFromStream(InputStream is) {
        byte[] b = new byte[1024];
        int len;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            while((len = is.read(b)) != -1){
                bos.write(b, 0, len);
            }

            String text = new String(bos.toByteArray());
            return text;
        } catch (Exception e) {

            e.printStackTrace();
        }

        return null;

    }
   /**获取屏幕的宽度 */
    public static int getScreenWidth(Context context) {
        WindowManager manager = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        return display.getWidth();
    }
    //转化时间
    public static String transhms(long progress,String rule){
        SimpleDateFormat formatter = new SimpleDateFormat(rule);//初始化Formatter的转换格式。

        String ms = formatter.format(progress);
        return ms;
    }

    //身份证验证
    /**
     * 18位或者15位身份证验证 18位的最后一位可以是字母X
     *
     * @param text
     * @return
     */
    public static boolean personIdValidation(String text) {
        boolean flag = false;
        String regx = "[0-9]{17}X";
        String reg1 = "[0-9]{15}";
        String regex = "[0-9]{18}";
        flag = text.matches(regx) || text.matches(reg1) || text.matches(regex);
        return flag;
    }
    /**字符小于100
     * return boolean*/
    public static boolean  getStrLength(String value) {
        int valueLength = 0;
        String chinese = "[\u0391-\uFFE5]";
        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
        for (int i = 0; i < value.length(); i++) {
            /* 获取一个字符 */
            String temp = value.substring(i, i + 1);
            /* 判断是否为中文字符 */
            if (temp.matches(chinese)) {
                valueLength += 2;
            } else {
                valueLength += 1;
            }
        }
        if (valueLength<=100){
            return true;
        }else {
            return false;
        }
    }


//    public static boolean  getStrLength(String value) {
//        int valueLength = 0;
//        String chinese = "[\u0391-\uFFE5]";
//        String notNumber="[^0-9]";
//        /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
//        for (int i = 0; i < value.length(); i++) {
//            /* 获取一个字符 */
//            String temp = value.substring(i, i + 1);
//            /* 判断是否为中文字符 */
//            if (temp.matches(notNumber)){
//                if (temp.matches(chinese)) {
//                    valueLength += 2;
//                } else {
//                    valueLength += 1;
//                }
//            }else {
//                return false;
//            }
//
//
//        }
//        if (valueLength<=50){
//            return true;
//        }else {
//            return false;
//        }
//    }

}
