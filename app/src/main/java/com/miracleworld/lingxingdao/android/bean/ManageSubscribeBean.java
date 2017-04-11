package com.miracleworld.lingxingdao.android.bean;

import java.io.Serializable;

/**
 * 订阅管理Bean
 */
public class ManageSubscribeBean implements Serializable {
    public int teacherId;           //老师id
    public int orderId;          //订单号ID
    public int channelId;        //频道id
    public String teacherName;      //老师昵称
    public String channelName;      //订阅频道channelName
    public String portraitUrlBig;   //老师大图
    public String portraitUrlSmall; //老师小图
    public String ordersn ;         //订单号
    public String type ;            //支付类型
    public String period ;          //周期
    public String des ;          //频道的描述
    public long createTime;         //上次订阅支付时间
    public long endTime;          //结束时间
    public double amount;           //价格

    public ManageSubscribeBean() {
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
