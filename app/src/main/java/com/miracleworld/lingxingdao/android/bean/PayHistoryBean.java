package com.miracleworld.lingxingdao.android.bean;

/**
 * 支付历史Bean
 */
public class PayHistoryBean {

    public int teacherId;           //老师id
    public String teacherName;      //老师昵称
    public String channelName;      //订阅频道
    public String portraitUrlBig;   //老师大图
    public String portraitUrlSmall; //老师小图
    public String ordersn;          //订单号
    public int orderId;          //订单号ID
    public String type;             //支付类型
    public String period;             //订阅周期
    public String endTime;          //结束时间
    public long createTime;         //上次订阅支付时间
    public double amount;              //支付金额
}
