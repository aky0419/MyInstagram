package com.example.instagramclone.Share;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.instagramclone.R;
import com.example.instagramclone.Utils.StringManipulation;
import com.example.instagramclone.Utils.UniversalImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;
import java.util.zip.Inflater;

public class NextActivity extends AppCompatActivity {
    private static final String TAG = "NextActivity";
    private static final String mAppend = "file:/";
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReference;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);

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
            }
        });


        setupImage();
    }
        private void setupImage() {
        Intent intent = getIntent();
        ImageView image = findViewById(R.id.imageShare);
        String imgURL = intent.getStringExtra(getString(R.string.selected_image));
        UniversalImageLoader.setImage(imgURL, image, null, "");

        }

////------------------------------------Firebase----------------------------------------------
//
//
//    //Set up firebase authentication object
//    public void setupFirebaseAuth() {
//        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
//        // Initialize Firebase Auth
//        mAuth = FirebaseAuth.getInstance();
//        mFirebaseDatabase = FirebaseDatabase.getInstance();
//        db = FirebaseFirestore.getInstance();
//        /*
//         * Retrieves the account settings for the user currently logged in
//         * Database: user_account_settings node
//         */
//
//        db.collection("user_account_settings")
//                .document(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//
//                Map<String, Object> doc = documentSnapshot.getData();
//                Log.d(TAG, "onSuccess: setting widgets with data retrieving from firebase database: "+ doc.get("profile_photo"));
//
//                UniversalImageLoader.setImage(doc.get("profile_photo").toString(),mProfilePhoto,null,"");
//
//                mDescription.setText(doc.get("description").toString());
//
//                String username = StringManipulation.expandUsername(doc.get("username").toString());
//                mUsername.setText(username);
//
//                mWebsite.setText(doc.get("website").toString());
//                mFollowers.setText(doc.get("followers").toString());
//                mFollowing.setText(doc.get("following").toString());
//                mPosts.setText(doc.get("posts").toString());
//                mDisplayName.setText(doc.get("display_name").toString());
//
//                mProgressBar.setVisibility(View.GONE);
//
//
//            }
//        });
//
//
//
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        //FirebaseUser currentUser = mAuth.getCurrentUser();
//        setupFirebaseAuth();
//        // check if user is logged in
//        // checkCurrentUser(currentUser);
//
////        if (currentUser == null) {
////            mAuth.signInWithEmailAndPassword("kenchan52016@yahoo.com","123456")
////                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
////                        @Override
////                        public void onComplete(@NonNull Task<AuthResult> task) {
////                            if (task.isSuccessful()) {
////                                // Sign in success, update UI with the signed-in user's information
////                                Log.d(TAG, "createUserWithEmail:success");
////
////                            } else {
////                                // If sign in fails, display a message to the user.
////                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
////
////
////                            }
////                        }
////                    });
////
////
////        }
//
//    }
}


