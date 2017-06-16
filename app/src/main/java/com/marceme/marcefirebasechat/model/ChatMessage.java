package com.marceme.marcefirebasechat.model;

import com.google.firebase.database.Exclude;

public class ChatMessage {

    private String message;
    private String sender;
    private String recipient;
    private String imageURL;

    private int mRecipientOrSenderStatus;

    public ChatMessage() {
    }

    public ChatMessage(String message, String sender, String recipient, String imageURL) {
        this.message = message;
        this.recipient = recipient;
        this.sender = sender;
        this.imageURL = imageURL;
    }


    public void setRecipientOrSenderStatus(int recipientOrSenderStatus) {
        this.mRecipientOrSenderStatus = recipientOrSenderStatus;
    }


    public String getImageURL() {
        return imageURL;
    }

    public String getMessage() {
        return message;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getSender() {
        return sender;
    }

    @Exclude
    public int getRecipientOrSenderStatus() {
        return mRecipientOrSenderStatus;
    }


}
