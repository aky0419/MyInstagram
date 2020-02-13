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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.instagramclone.R;
import com.example.instagramclone.Utils.FirebaseMethods;
import com.example.instagramclone.Utils.StringManipulation;
import com.example.instagramclone.Utils.UniversalImageLoader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileFragment extends Fragment {

    private static final String TAG = "EditProfileFragment";


    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseMethods mFirebaseMethods;

    //EditProfile Fragment Widgets
    private EditText mDisplayName, mUsername, mWebsite, mDescription, mEmail, mPhoneNumber;
    private TextView mChangeProfilePhoto;
    private CircleImageView mProfilePhoto;
    private ImageView saveChanges;
   private  HashMap<String, String> initialProfileField;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);
        initialProfileField = new HashMap<>();
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


        saveChanges = view.findViewById(R.id.saveChanges);
        saveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProfileSettings();
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
        mFirebaseMethods = new FirebaseMethods(getActivity());
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

                String email = doc.get("email").toString();
                initialProfileField.put("username", username);
                initialProfileField.put("email", email);


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
    /**
     * Retrieves the data contained in the widgets and submit it to the database
     * Before doing so, it checks to make sure the username chosen is unique
     */

    private void saveProfileSettings(){

        final String displayName = mDisplayName.getText().toString();
        final String username = mUsername.getText().toString();
        final String website = mWebsite.getText().toString();
        final String description = mDescription.getText().toString();
        final String email = mEmail.getText().toString();
        final String phoneNumber = mPhoneNumber.getText().toString();



        Map<String, Object> user = new HashMap<>();
        user.put("email", email);
        user.put("phone_number", phoneNumber);
        // case1: if the user made a change to their username
        if (!username.equals(this.initialProfileField.get("username"))) {
            checkIfUsernameExists(username);
        }
        // case2: if the user made a change to their email
        if (!username.equals(this.initialProfileField.get("email"))) {

            //step1) Reauthentication
            //
            //step2) check if the email already is registered
            //step3) change the email

        }



        db.collection("users").document(mAuth.getUid()).update(user);


        Map<String, Object> user_account_settings = new HashMap<>();
        user_account_settings.put("description", description);
        user_account_settings.put("display_name", displayName);
        user_account_settings.put("website", website);
        db.collection("user_account_settings").document(mAuth.getUid()).update(user_account_settings);

    }
    /**
     *check if username already exists in the database
     */


    private void checkIfUsernameExists(final String username) {
        Log.d(TAG, "checkIfUsernameExists: " + username + " already exists.");

        final CollectionReference usersRef = db.collection("users");
        Query query = usersRef.whereEqualTo("username", StringManipulation.condenseUsername(username));
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    QuerySnapshot result = task.getResult();
                    if (result.size() > 0) {
                        Log.d(TAG, "has existed user");
                        Toast.makeText(getActivity(),"That username already exists.",Toast.LENGTH_SHORT).show();


                    }   else {
                        mFirebaseMethods.updateUsername(username);

                        Toast.makeText(getActivity(),"saved username.",Toast.LENGTH_SHORT).show();


                    }
                } else {
                    Log.d(TAG, "task failed");
                }


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