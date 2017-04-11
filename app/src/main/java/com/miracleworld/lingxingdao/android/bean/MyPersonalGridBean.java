package com.miracleworld.lingxingdao.android.bean;

import java.io.Serializable;

/**
 * 个人资料__gridviewBean
 */
public class MyPersonalGridBean implements Serializable {
    public int  teacherId ;//讲师id
    public String teacherName;//讲师姓名
    public String portraitUrlSmall; //小头像
    public String portraitUrlBig; // 大头像
    public String introduce;
    public double price;
    public String catgoryName;
    public String catgoryId;
    public String remark;
    public int isReserve;

    public MyPersonalGridBean() {
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
