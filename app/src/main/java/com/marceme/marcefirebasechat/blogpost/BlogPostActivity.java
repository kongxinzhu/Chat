package com.marceme.marcefirebasechat.blogpost;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.marceme.marcefirebasechat.R;

public class BlogPostActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blog_post);
    }

    public void goToPhoto(View view) {
        Intent photo = new Intent(this, PhotoActivity.class);
        startActivity(photo);
    }
}
