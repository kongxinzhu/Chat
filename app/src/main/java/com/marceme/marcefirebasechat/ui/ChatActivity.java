package com.marceme.marcefirebasechat.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marceme.marcefirebasechat.FireChatHelper.ExtraIntent;
import com.marceme.marcefirebasechat.R;
import com.marceme.marcefirebasechat.adapter.MessageChatAdapter;
import com.marceme.marcefirebasechat.model.ChatMessage;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class ChatActivity extends Activity {

    private static final String TAG = ChatActivity.class.getSimpleName();

    @BindView(R.id.recycler_view_chat) RecyclerView mChatRecyclerView;
    @BindView(R.id.edit_text_message) EditText mUserMessageChatText;


    private String mRecipientId;
    private String mCurrentUserId;

    private MessageChatAdapter messageChatAdapter;
    private DatabaseReference messageChatDatabase;
    private ChildEventListener messageChatListener;


    private static final int RC_STORAGE_PERMS = 102;
    private static final int RC_TAKE_PICTURE = 101;
    private Uri mDownloadUrl;
    private ProgressDialog mProgressDialog;
    private static final int RC_SELECT_PICTURE = 103;
    private StorageReference mStorageRef;
    private Uri mFileUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        bindButterKnife();
        setDatabaseInstance();
        setUsersId();
        setChatRecyclerView();
        mStorageRef = FirebaseStorage.getInstance().getReference();
    }

    private void bindButterKnife() {
        ButterKnife.bind(this);
    }
    private void setDatabaseInstance() {
        String chatRef = getIntent().getStringExtra(ExtraIntent.EXTRA_CHAT_REF);
        messageChatDatabase = FirebaseDatabase.getInstance().getReference().child(chatRef);
    }

    private void setUsersId() {
        mRecipientId = getIntent().getStringExtra(ExtraIntent.EXTRA_RECIPIENT_ID);
        mCurrentUserId = getIntent().getStringExtra(ExtraIntent.EXTRA_CURRENT_USER_ID);
    }

    private void setChatRecyclerView() {
        mChatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mChatRecyclerView.setHasFixedSize(true);
        messageChatAdapter = new MessageChatAdapter(this, new ArrayList<ChatMessage>());
        mChatRecyclerView.setAdapter(messageChatAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();

        messageChatListener = messageChatDatabase.limitToFirst(20).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildKey) {

                if(dataSnapshot.exists()){
                    ChatMessage newMessage = dataSnapshot.getValue(ChatMessage.class);
                    if(newMessage.getSender().equals(mCurrentUserId)){
                        if (newMessage.getImageURL().isEmpty()) {
                            newMessage.setRecipientOrSenderStatus(MessageChatAdapter.SENDER_TEXT);
                        } else if(newMessage.getMessage().isEmpty()){
                            newMessage.setRecipientOrSenderStatus(MessageChatAdapter.SENDER_IMAGE);
                        } else {
                            newMessage.setRecipientOrSenderStatus(MessageChatAdapter.SENDER_BOTH);
                        }
                    } else if(newMessage.getImageURL().isEmpty()) {
                        newMessage.setRecipientOrSenderStatus(MessageChatAdapter.RECIPIENT_TEXT);
                    } else if(newMessage.getMessage().isEmpty()){
                        newMessage.setRecipientOrSenderStatus(MessageChatAdapter.RECIPIENT_IMAGE);
                    } else {
                        newMessage.setRecipientOrSenderStatus(MessageChatAdapter.RECIPIENT_BOTH);
                    }
                    messageChatAdapter.refillAdapter(newMessage);
                    mChatRecyclerView.scrollToPosition(messageChatAdapter.getItemCount()-1);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

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


    @Override
    protected void onStop() {
        super.onStop();

        if(messageChatListener != null) {
            messageChatDatabase.removeEventListener(messageChatListener);
        }
        messageChatAdapter.cleanUp();

    }

    @OnClick(R.id.btn_send_message)
    public void btnSendMsgListener(View sendButton){
        String message = mUserMessageChatText.getText().toString().trim();
        String senderMessage = message == null ? new String() : message;
        String senderImageURL = new String();
        if(mDownloadUrl != null) {
            senderImageURL = mDownloadUrl.toString();
        }

        if(!senderMessage.isEmpty() || !senderImageURL.isEmpty()){

            ChatMessage newMessage = new ChatMessage(senderMessage,senderImageURL,mCurrentUserId,mRecipientId);
            messageChatDatabase.push().setValue(newMessage);
            mUserMessageChatText.setText("");
        }
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);
        if (requestCode == RC_TAKE_PICTURE) {
            if (resultCode == RESULT_OK) {
                if (mFileUri != null) {
                    uploadFromUri(mFileUri);
                } else {
                    Log.w(TAG, "File URI is null");
                }
            } else {
                Toast.makeText(this, "Taking picture failed.", Toast.LENGTH_SHORT).show();
            }
        }
        else if(requestCode == RC_SELECT_PICTURE)
        {
            if (resultCode == RESULT_OK) {
                if (mFileUri != null) {
                    Uri uri = data.getData();
                    uploadFromUri(uri);
                } else {
                    Log.w(TAG, "File URI is null");
                }
            } else {
                Toast.makeText(this, "Taking picture failed.", Toast.LENGTH_SHORT).show();
            }
        }
    }


    public void editUpload(View view) {
        selectImage();
    }

    @AfterPermissionGranted(RC_STORAGE_PERMS)
    private void launchCamera() {
        Log.d(TAG, "launchCamera");

        // Check that we have permission to read images from external storage.
        String perm = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (!EasyPermissions.hasPermissions(this, perm)) {
            EasyPermissions.requestPermissions(this, "This sample reads images from your camera to demonstrate uploading",
                    RC_STORAGE_PERMS, perm);
            return;
        }

        // Choose file storage location, must be listed in res/xml/file_paths.xml
        File dir = new File(Environment.getExternalStorageDirectory() + "/photos");
        File file = new File(dir, UUID.randomUUID().toString() + ".jpg");
        try {
            // Create directory if it does not exist.
            if (!dir.exists()) {
                dir.mkdir();
            }
            boolean created = file.createNewFile();
            Log.d(TAG, "file.createNewFile:" + file.getAbsolutePath() + ":" + created);
        } catch (IOException e) {
            Log.e(TAG, "file.createNewFile" + file.getAbsolutePath() + ":FAILED", e);
        }

        // Create content:// URI for file, required since Android N
        // See: https://developer.android.com/reference/android/support/v4/content/FileProvider.html
        mFileUri = FileProvider.getUriForFile(this,
                "com.marceme.marcefirebasechat.fileprovider", file);

        // Create and launch the intent
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);

        startActivityForResult(takePictureIntent, RC_TAKE_PICTURE);
    }

    @AfterPermissionGranted(RC_STORAGE_PERMS)
    private void launchGallery() {
        Log.d(TAG, "launchCamera");

        // Check that we have permission to read images from external storage.
        String perm = android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
        if (!EasyPermissions.hasPermissions(this, perm)) {
            EasyPermissions.requestPermissions(this, "This sample reads images from your camera to demonstrate uploading",
                    RC_STORAGE_PERMS, perm);
            return;
        }

        // Choose file storage location, must be listed in res/xml/file_paths.xml
        File dir = new File(Environment.getExternalStorageDirectory() + "/photos");
        File file = new File(dir, UUID.randomUUID().toString() + ".jpg");
        try {
            // Create directory if it does not exist.
            if (!dir.exists()) {
                dir.mkdir();
            }
            boolean created = file.createNewFile();
            Log.d(TAG, "file.createNewFile:" + file.getAbsolutePath() + ":" + created);
        } catch (IOException e) {
            Log.e(TAG, "file.createNewFile" + file.getAbsolutePath() + ":FAILED", e);
        }

        // Create content:// URI for file, required since Android N
        // See: https://developer.android.com/reference/android/support/v4/content/FileProvider.html
        mFileUri = FileProvider.getUriForFile(this,
                "com.marceme.marcefirebasechat.fileprovider", file);

        // Create and launch the intent
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.setType("image/*");

//        Intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Uploading your picture");
        startActivityForResult(pickPhoto, RC_SELECT_PICTURE);
    }



    private void selectImage() {

        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setTitle("Add Photo by : ");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo"))
                {
                    launchCamera();
                }
                else if (options[item].equals("Choose from Gallery"))
                {
                    launchGallery();

                }
                else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }



    private void uploadFromUri(Uri fileUri) {
        Toast.makeText(getApplicationContext(), fileUri.toString(), Toast.LENGTH_SHORT).show();
        String m_path = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM).getAbsolutePath();
        Log.d(TAG, "uploadFromUri:src:" + fileUri.toString());

        // [START get_child_ref]
        // Get a reference to store file at photos/<FILENAME>.jpg
        final StorageReference photoRef = mStorageRef.child("photos").child(fileUri.getLastPathSegment());

        //final StorageReference photoRefs =m_path;
        // [END get_child_ref]

        // Upload file to Firebase Storage
        // [START_EXCLUDE]
        showProgressDialog();
        // [END_EXCLUDE]
        Log.d(TAG, "uploadFromUri:dst:" + photoRef.getPath());


        photoRef.putFile(fileUri)
                .addOnSuccessListener(this, new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Upload succeeded
                        Log.d(TAG, "uploadFromUri:onSuccess");

                        // Get the public download URL
                        Uri uri = taskSnapshot.getDownloadUrl();
                        String messagePhotoURL = uri.toString();

                        ChatMessage newMessage = new ChatMessage(new String(), messagePhotoURL,
                                mCurrentUserId, mRecipientId);
                        messageChatDatabase.push().setValue(newMessage);
                        mUserMessageChatText.setText("");

//                        Picasso.with(ChatActivity.this).load(mUserNewPhotoURL).into(mUserPhoto);
                        // [START_EXCLUDE]
                        hideProgressDialog();
//                        updateUI(mAuth.getCurrentUser());
                        // [END_EXCLUDE]
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Upload failed
                        Log.w(TAG, "uploadFromUri:onFailure", exception);

                        mDownloadUrl = null;

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        Toast.makeText(ChatActivity.this, "Error: upload failed",
                                Toast.LENGTH_SHORT).show();
//                        updateUI(mAuth.getCurrentUser());
                        // [END_EXCLUDE]
                    }
                });
    }

    private void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("Loading...");
            mProgressDialog.setIndeterminate(true);
        }

        mProgressDialog.show();
    }


    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}

