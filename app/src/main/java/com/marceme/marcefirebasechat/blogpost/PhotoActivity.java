package com.marceme.marcefirebasechat.blogpost;

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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.marceme.marcefirebasechat.FireChatHelper.ChatHelper;
import com.marceme.marcefirebasechat.R;
import com.marceme.marcefirebasechat.login.LogInActivity;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import butterknife.ButterKnife;
import butterknife.OnClick;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class PhotoActivity extends Activity implements EasyPermissions.PermissionCallbacks {
    private static final String TAG = PhotoActivity.class.getSimpleName();

    private EditText mUserDisplayname;
    private EditText mUserStatus;
    private ImageView mUserPhoto;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mUserRefDatabase;
    private FirebaseUser mUser;
    private String mCurrentUserUid;
    private String mUserStatusContent;
    private String mUserPhotoDisplaynameContent;
    private String mUserPhotoURL;
    private final String NO_CHANGE = "0";
    private String mUserNewPhotoURL = "0";
    private AlertDialog dialog;

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
        setContentView(R.layout.activity_photo);

        bindButterKnife();
        setAuthInstance();
        setUsersDatabase();
        setAuthListener();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        //setUser();
        //setUserData(mUser);
        //mUserDisplayname = (EditText) findViewById(R.id.profile_displayname);
        //mUserStatus = (EditText) findViewById(R.id.profile_status);
        mUserPhoto = (ImageView) findViewById(R.id.profile_image);
        //getCurrentUserJava();


    }



    private void getCurrentUserJava(){
        DatabaseReference mUserDisplaynameReference = mUserRefDatabase.child(mCurrentUserUid).child("displayName");
        mUserDisplaynameReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mUserPhotoDisplaynameContent = dataSnapshot.getValue(String.class);
                mUserDisplayname.setText(mUserPhotoDisplaynameContent);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference mUserStatusReference = mUserRefDatabase.child(mCurrentUserUid).child("status");
        mUserStatusReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mUserStatusContent = dataSnapshot.getValue(String.class);
                mUserStatus.setText(mUserStatusContent);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        DatabaseReference mUserPhotoReference = mUserRefDatabase.child(mCurrentUserUid).child("photoURL");
        mUserPhotoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                mUserPhotoURL = dataSnapshot.getValue(String.class);
                Picasso.with(PhotoActivity.this)
                        .load(mUserPhotoURL)
                        .into(mUserPhoto);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


    private void setUser() {
        mUser = mAuth.getCurrentUser();
    }

    private void setUsersDatabase() {
        mUserRefDatabase = FirebaseDatabase.getInstance().getReference().child("users");
    }

    private void bindButterKnife() {
        ButterKnife.bind(this);
    }

    private void setAuthInstance() {
        mAuth = FirebaseAuth.getInstance();
    }

    private void setAuthListener() {
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user != null) {
                    setUserData(user);
                } else {
                    // User is signed out
                    goToLogin();
                }
            }
        };
    }

    private void setUserData(FirebaseUser user) {
        mCurrentUserUid = user.getUid();
    }

    private void goToLogin() {
        Intent intent = new Intent(this, LogInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // LoginActivity is a New Task
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); // The old task when coming back to this activity should be cleared so we cannot come back to it.
        startActivity(intent);
    }

    @OnClick(R.id.btn_profile_cancel)
    public void cancelClickListener(Button button) {
        finish();
    }

    @OnClick(R.id.btn_profile_save)
    public void saveClickListener(Button button) {
        showAlertDialog("Updating your profile...",false);
        //updateProfileInDB();
        //dismissAlertDialog();
        goToBlogPost();
    }

    private void showAlertDialog(String message, boolean isCancelable){
        dialog = ChatHelper.buildAlertDialog(getString(R.string.login_error_title), message,isCancelable,PhotoActivity.this);
        dialog.show();
    }


    private void dismissAlertDialog() {
        dialog.dismiss();
    }

    private void updateProfileInDB(){
        mUserStatus = (EditText) findViewById(R.id.profile_status);
        mUserDisplayname = (EditText) findViewById(R.id.profile_displayname);
        String mStatus = mUserStatus.getText().toString();
        String mDisplayname = mUserDisplayname.getText().toString();
        mUserRefDatabase.child(mCurrentUserUid).child("status").setValue(mStatus);
        mUserRefDatabase.child(mCurrentUserUid).child("displayName").setValue(mDisplayname);
        if(!mUserNewPhotoURL.equals(NO_CHANGE)) {
            mUserRefDatabase.child(mCurrentUserUid).child("photoURL").setValue(mUserNewPhotoURL);
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
//        Intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Uploading your picture");
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

        AlertDialog.Builder builder = new AlertDialog.Builder(PhotoActivity.this);
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
                        mDownloadUrl = taskSnapshot.getDownloadUrl();
                        mUserNewPhotoURL = mDownloadUrl.toString();
                        Picasso.with(PhotoActivity.this).load(mUserNewPhotoURL).into(mUserPhoto);
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
                        Toast.makeText(PhotoActivity.this, "Error: upload failed",
                                Toast.LENGTH_SHORT).show();
//                        updateUI(mAuth.getCurrentUser());
                        // [END_EXCLUDE]
                    }
                });
    }

    public void goToBlogPost() {
        Intent blogPost = new Intent(this, BlogPostActivity.class);
        startActivity(blogPost);
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

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
    }
}
