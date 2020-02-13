package com.example.instagramclone.Profile;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.instagramclone.R;
import com.example.instagramclone.Utils.StringManipulation;
import com.example.instagramclone.Utils.UniversalImageLoader;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment {

    private static final String TAG = "EditProfileFragment";


    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    //EditProfile Fragment Widgets
    private EditText mDisplayName, mUsername, mWebsite, mDescription, mEmail, mPhoneNumber;
    private TextView mChangeProfilePhoto;
    private CircleImageView mProfilePhoto;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);

        mProfilePhoto = view.findViewById(R.id.profile_photo);
        mDisplayName =view.findViewById(R.id.display_name);
        mUsername = view.findViewById(R.id.username);
        mWebsite = view.findViewById(R.id.website);
        mDescription = view.findViewById(R.id.description);
        mEmail = view.findViewById(R.id.email);
        mPhoneNumber = view.findViewById(R.id.phoneNumber);
        mChangeProfilePhoto = view.findViewById(R.id.changeProfilePhoto);
        mChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        setupFirebaseAuth();
//        setProfileImage();

        //back arrow for navigating back to "ProfileActivity"
        ImageView backArrow = view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to ProfileActivity");
                getActivity().finish();
            }
        });

        return view;
    }


//    private void setProfileImage() {
//        Log.d(TAG, "setProfileImage: setting profile image.");
//        String imageURL = "s3.amazonaws.com/user-content.stoplight.io/8987/1541019969018";
//        UniversalImageLoader.setImage(imageURL, mProfilePhoto, null, "https://");
//    }


//------------------------------------Firebase----------------------------------------------


    //Set up firebase authentication object
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        db = FirebaseFirestore.getInstance();
        /*
         * Retrieves the account settings for the user currently logged in
         * Database: user_account_settings node
         */

        db.collection("user_account_settings")
                .document(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Map<String, Object> doc = documentSnapshot.getData();
                Log.d(TAG, "onSuccess: setting widgets with data retrieving from firebase database: " + doc.get("user_account_settings"));

                UniversalImageLoader.setImage(doc.get("profile_photo").toString(), mProfilePhoto, null, "");

                mDescription.setText(doc.get("description").toString());

                String username = StringManipulation.expandUsername(doc.get("username").toString());
                mUsername.setText(username);
                mWebsite.setText(doc.get("website").toString());
                mDisplayName.setText(doc.get("display_name").toString());



            }
        });

        db.collection("users")
                .document(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Map<String, Object> doc = documentSnapshot.getData();
                Log.d(TAG, "onSuccess: setting widgets with data retrieving from firebase database: " + doc.get("users"));

                mEmail.setText(doc.get("email").toString());
                mPhoneNumber.setText(doc.get("phone_number").toString());




            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
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
//
//        }
    }
}