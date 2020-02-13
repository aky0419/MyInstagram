package com.example.instagramclone.Utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.instagramclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FirebaseMethods  {
    private static final String TAG = "FirebaseMethods";


    //Firebase
    private FirebaseAuth mAuth;
    private Context mContext;
    private String userID;
    private FirebaseFirestore db;


    public FirebaseMethods(Context mContext) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseFirestore.setLoggingEnabled(true);
        this.mContext = mContext;

        if (mAuth.getCurrentUser() !=null) {
            userID = mAuth.getCurrentUser().getUid();
        }
    }



    public void updateUsername(String username) {
        Log.d(TAG, "updateUsername: updating username to: " + username);

        Map<String, Object> user = new HashMap<>();
        user.put("username", StringManipulation.condenseUsername(username));
        db.collection("users").document(mAuth.getUid()).update(user);


        Map<String, Object> user_account_settings = new HashMap<>();
        user_account_settings.put("username", StringManipulation.condenseUsername(username));
        db.collection("user_account_settings").document(mAuth.getUid()).update(user_account_settings);
    }
            public void registerNewEmail(final String email, final String password, final String username) {
                final CollectionReference usersRef = db.collection("users");
                Query query = usersRef.whereEqualTo("username", StringManipulation.condenseUsername(username));
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot result = task.getResult();
                            if (result.size() > 0) {
                                Log.d(TAG, "has existed user");
                                Random random = new Random();
                                // random 3 digits from 100 ~ 999
                                int random3Digits = random.nextInt(900) + 100;
                                createUserWithEmailAndPassword(email, password, username + random3Digits);

                            } else {
                                Log.d(TAG, "we can create an user now");
                                createUserWithEmailAndPassword(email, password, username);

                            }

                        } else {
                            Log.d(TAG, "task failed");
                        }


                    }
                });
            }

            private void createUserWithEmailAndPassword(final String email, String password, final String username) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "onComplete: Authstate changed: " + userID);
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    userID = mAuth.getCurrentUser().getUid();
                                    Toast.makeText(mContext, R.string.auth_success,
                                            Toast.LENGTH_SHORT).show();
                                    addNewUser(email, username);
                                    addNewUserAccountSetting("", "", "", username, "");
                                    sendVerificationEmail();
                                    mAuth.signOut();
                                    //   mAuth.signOut();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(mContext, R.string.auth_failed,
                                            Toast.LENGTH_SHORT).show();

                                    //updateUI(null);
                                }

                                // ...
                            }
                        });


            }

            public void sendVerificationEmail() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                            } else {
                                Toast.makeText(mContext, "could't send verification email.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            /**
             * add information to the users nodes
             * add information to the user_account_settings node
             */


            public void addNewUser(String email, String username) {

                String currentUserId = mAuth.getCurrentUser().getUid();

                Map<String, Object> user = new HashMap<>();
                //email,phone_number,user_id,username
                user.put("email", email);
                user.put("phone_number", "");
                user.put("user_id", currentUserId);
                user.put("username", StringManipulation.condenseUsername(username));
                db.collection("users").document(currentUserId).set(user)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "addNewUser onComplete: ");
                            }
                        });


            }

            public void addNewUserAccountSetting(String description, String display_name, String profile_photo, String username, String website) {
                String currentUserId = mAuth.getCurrentUser().getUid();
                Map<String, Object> user_account_setting = new HashMap<>();
                user_account_setting.put("description", description);
                user_account_setting.put("display_name", display_name);
                user_account_setting.put("followers", 0);
                user_account_setting.put("following", 0);
                user_account_setting.put("posts", 0);
                user_account_setting.put("profile_photo", profile_photo);
                user_account_setting.put("username", StringManipulation.condenseUsername(username));
                user_account_setting.put("website", website);
                db.collection("user_account_settings").document(currentUserId).set(user_account_setting)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "addNewUserAccountSetting onComplete: ");
                            }
                        });

            }


        }