package com.miracleworld.lingxingdao.android.bean;

import java.io.Serializable;

/**
 * Created by donghaifeng on 2016/2/26.
 */
public class BuyTicketInfo implements Serializable{
    //主键
    public int id;
    public String name;
    public String mobile;
    public String identityCard;
    //代理人一项：默认是""
    public String agent;
//    //条目是否支持复训的boolean false 不支持 true支持
//    public boolean itemIsSupportAgain;
    //0是 新学员  1是 复训  2 旁听 3 是 提问
    public int identify;
    //标记是否是选中了状态
    public boolean isCheck;
}
