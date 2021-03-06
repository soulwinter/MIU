package com.example.myapplication.bean;

import com.chad.library.adapter.base.entity.MultiItemEntity;

import static com.example.myapplication.bean.CommentEntity.TYPE_COMMENT_PARENT;

import java.util.List;

public class FirstLevelBean implements MultiItemEntity{

    private List<SecondLevelBean> secondLevelBeans;

    //一级评论id
    private Integer id;
    //一级评论头像
    private String headImg;
    //一级评论的用户名
    private String userName;
    //一级评论的用户id
    private Integer userId;
    //评论内容
    private String content;
    //创建时间
    private long createTime;
    //点赞数量
    private int likeCount;
    //是否点赞了  0没有 1点赞
    private int isLike;
    //当前评论的总条数（包括这条一级评论）ps:处于未使用状态
    private long totalCount;
    //当前一级评论的位置（下标）
    private int position;
    //当前一级评论所在的位置(下标)
    private int positionCount;
    // 被回复的comment的数据库id
    private Integer recommentWho;

    public Integer getRecommentWho() {
        return recommentWho;
    }

    public void setRecommentWho(Integer recommentWho) {
        this.recommentWho = recommentWho;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public int getIsLike() {
        return isLike;
    }

    public void setIsLike(int isLike) {
        this.isLike = isLike;
    }

    public List<SecondLevelBean> getSecondLevelBeans() {
        return secondLevelBeans;
    }

    public void setSecondLevelBeans(List<SecondLevelBean> secondLevelBeans) {
        this.secondLevelBeans = secondLevelBeans;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPositionCount() {
        return positionCount;
    }

    public void setPositionCount(int positionCount) {
        this.positionCount = positionCount;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"secondLevelBeans\":")
                .append(secondLevelBeans);
        sb.append(",\"id\":\"")
                .append(id).append('\"');
        sb.append(",\"headImg\":\"")
                .append(headImg).append('\"');
        sb.append(",\"userName\":\"")
                .append(userName).append('\"');
        sb.append(",\"userId\":\"")
                .append(userId).append('\"');
        sb.append(",\"content\":\"")
                .append(content).append('\"');
        sb.append(",\"createTime\":")
                .append(createTime);
        sb.append(",\"likeCount\":")
                .append(likeCount);
        sb.append(",\"isLike\":")
                .append(isLike);
        sb.append(",\"totalCount\":")
                .append(totalCount);
        sb.append(",\"position\":")
                .append(position);
        sb.append(",\"positionCount\":")
                .append(positionCount);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int getItemType() {
        return TYPE_COMMENT_PARENT;
    }
}
