package com.example.myapplication.entity;



import java.io.Serializable;


public class MessageDTO implements Serializable {
    String userId;
    String channel;
    String content;
    String type;


    @Override
    public String toString() {
        return "MessageDTO{" +
                "userId='" + userId + '\'' +
                ", channel='" + channel + '\'' +
                ", content='" + content + '\'' +
                ", type='" + type + '\'' +
                '}';
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
