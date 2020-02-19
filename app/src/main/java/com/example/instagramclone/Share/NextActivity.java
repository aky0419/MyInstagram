package com.example.instagramclone.Share;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.instagramclone.R;

import java.util.zip.Inflater;

public class NextActivity extends AppCompatActivity {
    private static final String TAG = "NextActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

        Log.d(TAG, "onCreate: got the chosen image." + getIntent().getStringExtra(getString(R.string.selected_image)));
    }
}
