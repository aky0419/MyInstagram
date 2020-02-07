package com.example.instagramclone.login;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.instagramclone.R;
import com.example.instagramclone.Utils.FirebaseMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    private Context mContext;
    private TextView loadingPleaseWait;
    private EditText mEmail, mUserName, mPassword;
    private ProgressBar mProgressBar;
    private Button btnRegister;
    private FirebaseMethods firebaseMethods;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Log.d(TAG, "onCreate: started.");

        initWidgets();
        setupFirebaseAuth();


        firebaseMethods = new FirebaseMethods(mContext);
        init();


    }

    private void init() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = mEmail.getText().toString();
                String userName = mUserName.getText().toString();
                String password = mPassword.getText().toString();
                if (checkInputs(email, userName, password)) {
                    mProgressBar.setVisibility(View.VISIBLE);
                    loadingPleaseWait.setVisibility(View.VISIBLE);
                    firebaseMethods.registerNewEmail(email,password,userName);
                    finish();

                }

            }
        });

    }

    private boolean checkInputs(String email, String userName, String password) {
        Log.d(TAG, "checkInputs: checking inputs for all values");
        if (email.equals("") || userName.equals("")|| password.equals("")) {
            Toast.makeText(mContext, "Please Fill out all Fields",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    //initiating activity widgets
    private void initWidgets (){
        Log.d(TAG, "initWidgets: Initializing Widgets.");
        mProgressBar = findViewById(R.id.ProgressBar);
        mEmail = findViewById(R.id.input_email);
        mUserName = findViewById(R.id.input_username);
        mPassword = findViewById(R.id.input_password);
        mContext = RegisterActivity.this;
        loadingPleaseWait = findViewById(R.id.loadingPleaseWait);
        btnRegister = findViewById(R.id.button_register);
        mProgressBar.setVisibility(View.GONE);
        loadingPleaseWait.setVisibility(View.GONE);
    }

    private boolean isStringNull(String string) {
        Log.d(TAG, "isStringNull: checking string if null.");
        if (string.equals("")) {
            return true;
        } else {
            return false;
        }

    }
        //------------------------------------Firebase----------------------------------------------
    //Set up firebase authentication object
    public void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

    }



    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

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
