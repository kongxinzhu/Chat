package com.marceme.marcefirebasechat.model;

/**
 * Created by Jerry on 2017/6/15.
 */

public class BlogMessage {


    private String sender;
    private String description;
    private String photoURL;
    private String imageURL;
    private int mRecipientOrSenderStatus;
    //private String recipient;

//    private int mRecipientOrSenderStatus;
    public BlogMessage() {
    }


    public BlogMessage(String sender, String description, String photoURL, String imageURL) {
        this.sender = sender;
        this.description = description;
        this.photoURL = photoURL;
        this.imageURL = imageURL;
    }


//   public void setRecipientOrSenderStatus(int recipientOrSenderStatus) {
//      this.mRecipientOrSenderStatus = recipientOrSenderStatus;
//   }

    public String getDescription() {
        return description;
    }

    public String getPhotoURL(){
        return photoURL;
    }

    public String getImageURL(){ return imageURL;}

    public String getSender(){
        return sender;
    }

    public int getRecipientOrSenderStatus() {
        return mRecipientOrSenderStatus;
    }
    public void setRecipientOrSenderStatus(int status) {
        this.mRecipientOrSenderStatus = status;
    }

//    @Exclude
//    public int getRecipientOrSenderStatus() {
//        return mRecipientOrSenderStatus;
//    }

}

