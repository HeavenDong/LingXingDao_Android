package com.miracleworld.lingxingdao.android.bean;

import java.io.Serializable;

/**
 * Created by donghaifeng on 2016/3/8.
 */
public class NewContentMusic implements Serializable {
    public int id;//资源Id
    public String title;//标题
    public String categoryName;//资源分类名称
    public String pictureUrlSmall;//截图小 碟片的
    public String pictureUrlMiddle;//截图中
    public String pictureUrlBig;//截图大
    public String url;//路径：音乐路径
    public String isCost;//是否收费(0：免费，1：收费)'
    public int type;//资源类别（视频或音频）',
    public int sort;//排序
    public long createTime; //创建时间
    public int subsCount;//点赞数
    public double score;//评分
    public String teacherName;//老师名字
    public String userPortrait;//老师头像：老师的：注意字段和之前的不一样了
}
