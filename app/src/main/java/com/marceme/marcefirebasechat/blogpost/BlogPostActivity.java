package com.marceme.marcefirebasechat.blogpost;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.appindexing.Action;
import com.google.firebase.appindexing.FirebaseUserActions;
import com.google.firebase.appindexing.builders.Actions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.marceme.marcefirebasechat.FireChatHelper.ChatHelper;
import com.marceme.marcefirebasechat.R;
import com.marceme.marcefirebasechat.adapter.BlogPostAdapter;
import com.marceme.marcefirebasechat.model.BlogMessage;

import java.util.ArrayList;

public class BlogPostActivity extends Activity {

    private DatabaseReference messageChatDatabaseRef;
    private String recipientId;
    private String senderId;
    //private FirebaseRecyclerAdapter<Message, MessageViewHolder> mFirebaseAdapter;
    private LinearLayoutManager mLinearLayoutManager;
    private static final String TAG = "BlogPostActivity";
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private EditText mMessageEditText;

    private RecyclerView previousMessages;
    private BlogPostAdapter messageListAdapter;
    private ChildEventListener messageChatListener;
    private ChatHelper chatHelper;
    private FirebaseUser mFirebaseUser;
    private String mPhotoUrl;

    private static final int REQUEST_INVITE = 1;
    private static final int REQUEST_IMAGE = 2;
    private static final String LOADING_IMAGE_URL = "https://www.google.com/images/spin-32.gif";

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
//    public Action getIndexApiAction() {
//        return Actions.newView("Chat", "http://[ENTER-YOUR-URL-HERE]");
//    }

//    @Override
//    public void onStart() {
//        super.onStart();
//
//        // ATTENTION: This was auto-generated to implement the App Indexing API.
//        // See https://g.co/AppIndexing/AndroidStudio for more information.
//        FirebaseUserActions.getInstance().start(getIndexApiAction());
//    }

    public Action getIndexApiAction() {
        return Actions.newView("Chat", "http://[ENTER-YOUR-URL-HERE]");
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        FirebaseUserActions.getInstance().start(getIndexApiAction());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_post);

        //recipientId = getIntent().getStringExtra(Utils.CONTACT_ID);
        //senderId = getIntent().getStringExtra(Utils.CURRENT_USER_ID);
        mFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mFirebaseUser.getPhotoUrl() != null) {
            mPhotoUrl = mFirebaseUser.getPhotoUrl().toString();
        }

        messageListAdapter = new BlogPostAdapter(new ArrayList<BlogMessage>(), getApplicationContext());
        previousMessages = (RecyclerView) findViewById(R.id.recycler_view_chat);
        previousMessages.setLayoutManager(new LinearLayoutManager(this));
        previousMessages.setHasFixedSize(true);
        previousMessages.setAdapter(messageListAdapter);
        messageChatDatabaseRef = FirebaseDatabase.getInstance().getReference("BlogPosts");


        messageChatListener = messageChatDatabaseRef.limitToFirst(20).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildKey) {

                if(dataSnapshot.exists()){
                    BlogMessage newMessage = dataSnapshot.getValue(BlogMessage.class);
                    if(newMessage.getSender().equals(senderId)){
                        if (newMessage.getImageURL() == null) {
                            newMessage.setRecipientOrSenderStatus(BlogPostAdapter.RECIPIENT_TEXT);
                        } else {
                            newMessage.setRecipientOrSenderStatus(BlogPostAdapter.RECIPIENT_IMAGE);
                        }

                    }else{
                        if (newMessage.getImageURL() == null) {
                            newMessage.setRecipientOrSenderStatus(BlogPostAdapter.RECIPIENT_TEXT);
                        } else {
                            newMessage.setRecipientOrSenderStatus(BlogPostAdapter.RECIPIENT_IMAGE);
                        }
                    }
                    messageListAdapter.refillAdapter(newMessage);
                    previousMessages.scrollToPosition(messageListAdapter.getItemCount()-1);
                }


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
//                if(dataSnapshot.exists()) {
//                    String userUid = dataSnapshot.getKey();
//                    if(!userUid.equals(senderId)) {
//
//                        BlogMessage blogpost = dataSnapshot.getValue(BlogMessage.class);
//
//                        int index = mUsersKeyList.indexOf(userUid);
//                        if(index > -1) {
//                            mUsersChatAdapter.changeUser(index, user);
//                        }
//                    }
//
//                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }



    public void goToPhoto(View view) {
        Intent photo = new Intent(this, PhotoActivity.class);
        startActivity(photo);
    }


    @Override
    protected void onStop() {
        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
// See https://g.co/AppIndexing/AndroidStudio for more information.
        FirebaseUserActions.getInstance().end(getIndexApiAction());

        if (messageChatListener != null) {
            messageChatDatabaseRef.removeEventListener(messageChatListener);
        }
        messageListAdapter.cleanUp();

    }

//    @Override
//    protected void onStop() {
//        super.onStop();// ATTENTION: This was auto-generated to implement the App Indexing API.
//// See https://g.co/AppIndexing/AndroidStudio for more information.
//        FirebaseUserActions.getInstance().end(getIndexApiAction());
//
//        if (messageChatListener != null) {
//            messageChatDatabaseRef.removeEventListener(messageChatListener);
//        }
//        messageListAdapter.cleanUp();
//
//    }
}
