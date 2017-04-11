package com.miracleworld.lingxingdao.android.bean;

import java.io.Serializable;

/**
 * Created by donghaifeng on 2016/2/25.
 */
public class TicketInfoBean implements Serializable {
    public int id;
    public String ordersn;//订单号
    public String teacherName;
    public long startDate;
    public long endDate;
    public String title;
    public double amount;
    public int ticketNumber;
    public String ordersTicketDetailProtocolList;
    public String portraitUrlSmall;
    public String portraitUrlBig;
    public String cityName;
    /*缺图片，地址*/
}
