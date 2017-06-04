package com.marceme.marcefirebasechat.profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.marceme.marcefirebasechat.FireChatHelper.ChatHelper;
import com.marceme.marcefirebasechat.R;
import com.marceme.marcefirebasechat.login.LogInActivity;
import com.marceme.marcefirebasechat.model.User;
import com.marceme.marcefirebasechat.ui.MainActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MyProfileActivity extends Activity {
    private static final String TAG = MyProfileActivity.class.getSimpleName();


//    @BindView(R.id.profile_displayname) EditText mUserDisplayname;
    private EditText mUserDisplayname;
    private EditText mUserStatus;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mUserRefDatabase;
    private FirebaseUser mUser;
    private ChildEventListener mChildEventListener;
    private DatabaseReference mUserRow;
    private String mCurrentUserUid;
    private String mCurrentUserDisplayname;
//    private String mCurrentUserUid;
//    private String mCurrentUserUid;
//    private String mCurrentUserUid;
//    private String mCurrentUserUid;

    private AlertDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);




        bindButterKnife();
        setAuthInstance();
        setUsersDatabase();
        setAuthListener();
        setUser();
        setUserData(mUser);
        getCurrentUserJava();
        mUserDisplayname = (EditText) findViewById(R.id.profile_displayname);
    }



    private void getCurrentUserJava(){
//        DatabaseReference mUseDisplayname = mUserRefDatabase.child(mCurrentUserUid).child("displayName");
//        mUseDisplayname.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                String dn = dataSnapshot.getValue(String.class);
//                mUserDisplayname.setText(dn);
//
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
//
        mUserRow = mUserRefDatabase.child(mCurrentUserUid);
        mUserRow.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getValue(User.class);
                mUserDisplayname.setText(mUser.getDisplayName());

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
//        showProgressBarForUsers();
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
        updateProfileInDB();
        dismissAlertDialog();
        goToMainActivity();

    }

    private void showAlertDialog(String message, boolean isCancelable){
        dialog = ChatHelper.buildAlertDialog(getString(R.string.login_error_title), message,isCancelable,MyProfileActivity.this);
        dialog.show();
    }

    private void setmUserRow() {
        mUserRow = mUserRefDatabase.child(mCurrentUserUid);
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


    }

    private void goToMainActivity() {
        Intent intent = new Intent(MyProfileActivity.this, MainActivity.class);
        startActivity(intent);
    }
}
