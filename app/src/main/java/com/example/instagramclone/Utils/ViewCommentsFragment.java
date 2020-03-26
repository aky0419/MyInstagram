package com.example.instagramclone.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.instagramclone.Models.Comment;
import com.example.instagramclone.Models.Like;
import com.example.instagramclone.Models.Photo;
import com.example.instagramclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ViewCommentsFragment extends Fragment {
    private static final String TAG = "ViewCommentsFragment";

    public ViewCommentsFragment() {
        super();
        setArguments(new Bundle());
    }



    //firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReference;
    private FirebaseFirestore db;


    //vars

    private Context mContext;
    private Photo photo;
    private List<Comment> comments;


    //widgets
    private ImageView mBackArrow, mCheckMark;
    private EditText mComment;
    private ListView mListView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_comments, container, false);
        mContext = getActivity();
        mBackArrow = view.findViewById(R.id.backArrow);
        mCheckMark = view.findViewById(R.id.ivPostComment);
        mComment = view.findViewById(R.id.comment);
        mListView = view.findViewById(R.id.listView);





        try {
            photo = getPhotoFromBundle();
            comments = new ArrayList<>();
            Comment firstComment = new Comment();
            firstComment.setComment(photo.getCaption());
            firstComment.setUser_id(photo.getUser_id());
            firstComment.setDate_created(photo.getDate_created());
            comments.add(firstComment);
            CommentListAdapter commentListAdapter = new CommentListAdapter(getActivity(), R.layout.layout_center_comments,comments);
            mListView.setAdapter(commentListAdapter);

        } catch (NullPointerException e) {
            Log.e(TAG, "onCreateView: NullPointerException " + e.getMessage() );
        }


        return view;
    }

    private Photo getPhotoFromBundle() {
        Log.d(TAG, "getPhotoFromBundle: arguments " + getArguments());
        Bundle arg = this.getArguments();
        if (arg != null) {
        return arg.getParcelable(getString(R.string.photo));
        } else {
            return null;
        }
    }


    //------------------------------------Firebase----------------------------------------------


    //Set up firebase authentication object
    public void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        db = FirebaseFirestore.getInstance();

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
