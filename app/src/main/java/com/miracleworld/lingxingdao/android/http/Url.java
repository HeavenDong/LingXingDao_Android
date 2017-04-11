package com.miracleworld.lingxingdao.android.http;

/**
 * Created by donghaifeng on 2015/12/30.
 */
public class Url {
    //mainurl   http://139.196.173.207:8080
//    public static final String MAINURL="http://192.168.0.100:8080/api";
//    public static final String MAINURL="http://192.168.0.221:9999/api";
    public static final String MAINURL="http://139.196.173.207:8080/api";
//    public static final String MAINURL="http://192.168.0.249:9999/api";
    //main_category:get请求的分类列表：缺少全体的 id=0
    public static final String MAIN_CATEGORY=MAINURL+"/content/catList";
    //研讨会：schedule
    public static final String SCHEDULE=MAINURL+"/course/courseList";
    //主页新闻列表：
    public static final String HOME=MAINURL+"/news/newsList";
    //内容列表
    public static final String CONTENT=MAINURL+"/user/findTeacherList";
    //内容详情
    public static final String CONTENT_DETAIL=MAINURL+"/resource/findResourceList";
    //登录入口
    public static final String LOGIN_URL = MAINURL+ "/user/login";
    // 注册
    public static final String REGIST_URL = MAINURL+ "/user/register";
    //注册验证码
    public static final String REGIST_MSGCODE_URL =MAINURL+ "/securityCode/registerSendCode";
    //修改密码
    public static final String FORGET_URL =MAINURL+ "/user/updatePassword";
    //修改密码验证码
    public static final String FORGET_MSGCODE_URL =MAINURL+ "/securityCode/updatePwdSendCode";
    //修改用户信息
    public static final String CHANGE_USERINFO_URL =MAINURL+ "/user/editUserById";
    //个人中心---上传头像
    public static final String CHANGE_HEAD_URL = MAINURL+ "/user/updateHeadImg";
    //个人中心---个人资料
    public static final String LOOK_USERINFO_URL = MAINURL+ "/orders/reserveTeacherById";
    //内容详情--头部老师信息接口
    public static final String CONTENT_DETAIL_HEAD_URL = MAINURL+ "/user/findTeacherById";
    //个人中心---订阅管理
    public static final String LOOK_MANAGSUB_URL = MAINURL+ "/orders/reserveManage";
    //个人中心---频道支付历史
    public static final String LOOK_HISTORYPAY_URL = MAINURL+ "/orders/payHistory";
    //个人中心---购票支付历史
    public static final String LOOK_TICKET_HISTORYPAY_URL = MAINURL+ "/ordersTicket/findOrdersTicketList";
    //支付--生成订单
    public static final String GET_PAY_ORDERS_URL = MAINURL+ "/orders/saveOrders";
    //支付--微信
    public static final String WEIXIN_PAY_URL = MAINURL+ "/pay/getWxPrepayId";
    //支付--代金卡获取短信
    public static final String GIFTCAR_PAY_SENDCODE_URL =MAINURL+ "/securityCode/giftCardPaySendCode";
    //支付--代金卡支付
    public static final String DAIJINKA_PAY_URL =MAINURL+ "/giftCard/payGiftCard";
    //订阅管理支付结果回调给服务器
    public static final String ORDER_CALL_BACK =MAINURL+ "/orders/updateOrderStatus";
    //音频列表接口
    public static final String MUSIC_LIST =MAINURL+ "/resource/channelResourceList";
    //频道列表
    public static final String CHANNEL_LIST =MAINURL+ "/channel/findChannelList";
    //支付界面显示时间的接口
    public static final String PAY_STARTANDEND_TIME =MAINURL+ "/orders/orderLastTime";
    //付费频道的接口
    public static final String PAY_CHANNEL =MAINURL+ "/channel/findChannelByResourceId";
    //频道详情界面 请求音频列表的
    public static final String CHANNELDETAIL_LIST =MAINURL+ "/resource/findResourceByChannelId";
    //研讨会支付填写界面同时生成预支付订单
    public static final String PAY_WRITE =MAINURL+ "/ordersTicket/saveOrdersTicket";
    //购票订单保存
    public static final String PAY_TICKETI_CALL_BACK =MAINURL+ "/ordersTicket/updateOrdersTicket";
    //错误日志上传
    public static final String LOG_COMMIT =MAINURL+ "/log/uploadAndroidLog";
    //阿里回掉接口
    public static final String ALI_CALLBACK =MAINURL+ "/aliPay/alPaySuccess";
    //新内容列表
    public static final String NEW_CONTENT =MAINURL+ "/resource/findAllResource";

}
