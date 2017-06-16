package com.marceme.marcefirebasechat.model;

import com.google.firebase.database.Exclude;


public class User {

    private String displayName;
    private String email;
    private String connection;
    private String mystatus;
    private String photoURL;
    private String defaultPhotoURL = "http://www.rd.com/wp-content/uploads/sites/2/2016/02/06-train-cat-shake-hands.jpg";
    private long createdAt;

//    private LinkedList<String> friends;

    private String mRecipientId;

    public User() {
    }


    public User(String displayName, String email, String photoURL, String connection, long createdAt) {


        this.displayName = displayName;
        this.email = email;
        this.photoURL = photoURL == null ? defaultPhotoURL : photoURL;
        this.connection = connection;
        this.createdAt = createdAt;
    }



    public String createUniqueChatRef(long createdAtCurrentUser, String currentUserEmail){
        String uniqueChatRef="";
        if(createdAtCurrentUser > getCreatedAt()){
            uniqueChatRef = cleanEmailAddress(currentUserEmail)+"-"+cleanEmailAddress(getUserEmail());
        }else {

            uniqueChatRef=cleanEmailAddress(getUserEmail())+"-"+cleanEmailAddress(currentUserEmail);
        }
        return uniqueChatRef;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getPhotoURL() {
        return photoURL;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    private String cleanEmailAddress(String email){
        //replace dot with comma since firebase does not allow dot
        return email.replace(".","-");
    }

    public String getStatus() {
        return mystatus;
    }


    public void setStatus(String mystatus) {
        this.mystatus = mystatus;
    }

    private String getUserEmail() {
        //Log.e("user email  ", userEmail);
        return email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public String getConnection() {
        return connection;
    }

    @Exclude
    public String getRecipientId() {
        return mRecipientId;
    }

    public void setRecipientId(String recipientId) {
        this.mRecipientId = recipientId;
    }
}
