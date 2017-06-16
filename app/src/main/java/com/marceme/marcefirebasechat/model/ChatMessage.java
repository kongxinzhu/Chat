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

    public ChatMessage(String message, String imageURL, String sender, String recipient) {
        this.message = message;
        this.imageURL = imageURL;
        this.recipient = recipient;
        this.sender = sender;
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
