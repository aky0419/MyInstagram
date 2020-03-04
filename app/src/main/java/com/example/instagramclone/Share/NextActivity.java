package com.example.instagramclone.Share;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.instagramclone.R;
import com.example.instagramclone.Utils.FirebaseMethods;
import com.example.instagramclone.Utils.StringManipulation;
import com.example.instagramclone.Utils.UniversalImageLoader;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Map;
import java.util.Random;
import java.util.zip.Inflater;

public class NextActivity extends AppCompatActivity {
    private static final String TAG = "NextActivity";
    private static final String mAppend = "file:/";
    private int imageCount = 0;
    String imgURL;
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReference;
    private FirebaseFirestore db;
    private FirebaseMethods firebaseMethods;
    private FirebaseDatabase database;
    private DatabaseReference myRef;


    //widges + var
    ImageView imageShare;
    EditText description;
    String caption;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        imageShare = findViewById(R.id.imageShare);
        firebaseMethods = new FirebaseMethods(NextActivity.this);
        description = findViewById(R.id.description);



        //setupFirebaseAuth();

        ImageView backArrow = findViewById(R.id.ivBackArrow);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the activity.");
                finish();
            }
        });

        TextView share = findViewById(R.id.tvShare);

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to the final share screen.");
               //upload the image to firebase
                sharePhoto();
                Toast.makeText(NextActivity.this, "Attempting to upload new photo",Toast.LENGTH_SHORT).show();

                caption = description.getText().toString();
                String photoType = "new_photo";
                firebaseMethods.uploadImageToStorage(photoType, imageShare, caption);
            }
        });

        setupImage();

    }

    private void sharePhoto() {
        /*
        Step 1)
        Create a data model for Photos

        Step 2)
        Add properties to the Photo Objects (Caption, date, imageUrl, photo_id, tags, user_id)

        Step 3)
        Count the number of photos that the user already have

        step 4)
        a) Upload the photo to Firebase Storage
        b) insert into 'photo' node
        c) insert into 'user_photo' node
         */
firebaseMethods.getImageCount(new FirebaseMethods.Callback() {
    @Override
    public void onSuccess(Object obj) {
        int imageCount = (int) obj;
        Log.d(TAG, "onSuccess: image count: " + imageCount );

    }
});

    }

        private void setupImage() {
        Intent intent = getIntent();
        imgURL = intent.getStringExtra(getString(R.string.selected_image));
        UniversalImageLoader.setImage(imgURL, imageShare, null, "");

        }

//------------------------------------Firebase----------------------------------------------


    //Set up firebase authentication object
    public void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        db = FirebaseFirestore.getInstance();



        /*
         * Retrieves the account settings for the user currently logged in
         * Database: user_account_settings node
         */



    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //FirebaseUser currentUser = mAuth.getCurrentUser();
        setupFirebaseAuth();
        // check if user is logged in
        // checkCurrentUser(currentUser);

//        if (currentUser == null) {
//            mAuth.signInWithEmailAndPassword("kenchan52016@yahoo.com","123456")
//                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (task.isSuccessful()) {
//                                // Sign in success, update UI with the signed-in user's information
//                                Log.d(TAG, "createUserWithEmail:success");
//
//                            } else {
//                                // If sign in fails, display a message to the user.
//                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
//
//
//                            }
//                        }
//                    });
//
//
//        }

    }
}


