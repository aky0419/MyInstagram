package com.example.instagramclone.Utils;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.instagramclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
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
        this.mContext = mContext;

        if (mAuth.getCurrentUser() !=null) {
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    public void registerNewEmail(final String email, final String password, final String username) {
        final CollectionReference usersRef = db.collection("users");
        Query query = usersRef.whereEqualTo("username",username);
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
                        createUserWithEmailAndPassword(email, password, username + random3Digits );
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
                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
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
                            mAuth.signOut();
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


    public void addNewUser(String email, String name) {
        String currentUserId = mAuth.getCurrentUser().getUid();
        Map<String, Object> user = new HashMap<>();
        //email,phone_number,user_id,username
        user.put("email", email);
        user.put("phone_number", "");
        user.put("user_id", currentUserId);
        user.put("username", name);
   db.collection("users").document(currentUserId)
           .set(user);



    }
}
