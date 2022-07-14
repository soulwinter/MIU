package com.example.myapplication.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class CommentOfTag implements Serializable {

    private Integer id;

    private Integer userId;

    private String userName;

    private Integer tagId;

    private String comment;

    private Integer recommentWho;

    private String replyUsername;

    private String photoPath;

    private Date createTime;

    private String username;

    private Integer likes;

    private List<CommentOfTag> childList;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getReplyUsername() {
        return replyUsername;
    }

    public void setReplyUsername(String replyUsername) {
        this.replyUsername = replyUsername;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public List<CommentOfTag> getChildList() {
        return childList;
    }

    public void setChildList(List<CommentOfTag> childList) {
        this.childList = childList;
    }

    public CommentOfTag() {
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment == null ? null : comment.trim();
    }

    public Integer getRecommentWho() {
        return recommentWho;
    }

    public void setRecommentWho(Integer recommentWho) {
        this.recommentWho = recommentWho;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath == null ? null : photoPath.trim();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}