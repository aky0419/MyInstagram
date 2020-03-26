package com.example.instagramclone.Utils;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.instagramclone.Models.Comment;
import com.example.instagramclone.Models.Photo;
import com.example.instagramclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        comments = new ArrayList<>();

        setupFirebaseAuth();





        try {
            photo = getPhotoFromBundle();

            getCommentsFromDatabase();

        } catch (NullPointerException e) {
            Log.e(TAG, "onCreateView: NullPointerException " + e.getMessage() );
        }


        return view;
    }

    private void closeKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view!= null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }

    private void submitNewComment(String newComment) {
        Log.d(TAG, "submitNewComment: adding a new comment: " + newComment);
        final Comment comment = new Comment();
        comment.setComment(newComment);
        comment.setDate_created(getTimeStamp());
        comment.setTimestamp(new Timestamp(new Date()));
        comment.setUser_id(mAuth.getCurrentUser().getUid());
        Map<String, Object> photoComment = new HashMap<>();
        photoComment.put(getString(R.string.dbname_user_id), comment.getUser_id());
        photoComment.put(getString(R.string.dbname_comment), comment.getComment());
        photoComment.put(getString(R.string.dbname_date_created), comment.getDate_created());
        photoComment.put("timestamp", comment.getTimestamp());
        db.collection(getString(R.string.dbname_photos)).document(photo.getPhoto_id()).collection(getString(R.string.comments)).add(photoComment).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {

                Log.d(TAG, "onComplete: added " + task.isSuccessful());
            }
        });

        
    }

    private String getTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("US/Pacific"));
        return sdf.format(new Date());

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
    private void getCommentsFromDatabase(){
        CollectionReference photoRef = db.collection(getString(R.string.dbname_photos));
        CollectionReference commentRef = photoRef.document(photo.getPhoto_id()).collection(getString(R.string.dbcollection_comments));

        commentRef.orderBy("timestamp").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@javax.annotation.Nullable QuerySnapshot queryDocumentSnapshots, @javax.annotation.Nullable FirebaseFirestoreException e) {
                comments.clear();
                Comment firstComment = new Comment();
                firstComment.setUser_id(photo.getUser_id());
                firstComment.setDate_created(photo.getDate_created());
                firstComment.setComment(photo.getCaption());
                comments.add(firstComment);

                for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {
                    Comment comment = new Comment();
                    comment.setComment(document.get(getString(R.string.dbname_comment)).toString());
                    comment.setUser_id(document.get(getString(R.string.dbname_user_id)).toString());
                    comment.setDate_created(document.get(getString(R.string.dbname_date_created)).toString());
                    comments.add(comment);
                }
                photo.setComments(comments);
                CommentListAdapter commentListAdapter = new CommentListAdapter(getActivity(), R.layout.layout_center_comments,comments);
                mListView.setAdapter(commentListAdapter);

                mBackArrow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                });
                mCheckMark.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!mComment.getText().toString().equals("")) {
                            submitNewComment(mComment.getText().toString());

                            mComment.setText("");
                            closeKeyboard();
                        } else {
                            Toast.makeText(getActivity(), "you can't post a blank comment", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

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
