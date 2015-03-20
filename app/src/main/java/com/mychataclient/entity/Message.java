package com.mychataclient.entity;

import com.mychataclient.enums.MessageType;

/**
 * Created by ciprian.mare on 3/20/2015.
 */
public class Message {

    private MessageType messageType;
    private User user;
    private String message;

    public Message(MessageType messageType, User user, String message) {
        this.messageType = messageType;
        this.user = user;
        this.message = message;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
