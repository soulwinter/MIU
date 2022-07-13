package com.example.myapplication.entity;



import java.io.Serializable;
import java.util.Date;


public class ChatHisMessageDTO implements Serializable {
    String channelId;
    User user;
    MessageDTO message;
    String type;
    Date createTime;


    @Override
    public String toString() {
        return "ChatHisMessageDTO{" +
                "channelId='" + channelId + '\'' +
                ", user=" + user +
                ", message=" + message +
                ", type='" + type + '\'' +
                ", createTime=" + createTime +
                '}';
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public MessageDTO getMessage() {
        return message;
    }

    public void setMessage(MessageDTO message) {
        this.message = message;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
