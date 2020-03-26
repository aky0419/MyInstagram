package com.example.instagramclone.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.instagramclone.Models.Like;
import com.example.instagramclone.Models.Photo;
import com.example.instagramclone.Profile.ProfileActivity;
import com.example.instagramclone.R;
import com.example.instagramclone.Utils.BottomNavigationViewHelper;
import com.example.instagramclone.Utils.SquareImageView;
import com.example.instagramclone.Utils.StringManipulation;
import com.example.instagramclone.Utils.UniversalImageLoader;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.w3c.dom.Document;

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

public class ViewPostFragment extends Fragment {
    private static final String TAG = "ViewPostFragment";

    public interface OnCommentThreadSelectedListener{
        void onCommentThreadSelectedListener (Photo photo);

    }
    OnCommentThreadSelectedListener mOnCommentThreadSelectedListener;

    public ViewPostFragment() {
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
    private int mActivityNumber = 0;
    private String username;
    private String mProfileUrl;
    private GestureDetector gestureDetector;
    private Heart heart;
    private Boolean mLikedByCurrentUser = false;
    private List<String> likes;
    private StringBuilder sb;

    //widgets
    private SquareImageView mPostImage;
    TextView mBackLabel, mCaption, mUsername, mTimeStamp, mLikes;
    ImageView mBackArrow, mEllipses, mHearRed, mHeartWhite, mProfileImage, mComment;
    private BottomNavigationViewEx bottomNavigationView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_post, container, false);

        likes = new ArrayList<>();
        mContext = getActivity();
        mLikes = view.findViewById(R.id.image_likes);
        mPostImage = view.findViewById(R.id.post_image);
        bottomNavigationView = view.findViewById(R.id.bottomNavViewBar);
        mBackArrow = view.findViewById(R.id.backArrow);
        mBackLabel = view.findViewById(R.id.tvBackLabel);
        mEllipses = view.findViewById(R.id.profileMenu);
        mCaption = view.findViewById(R.id.image_caption);
        mUsername = view.findViewById(R.id.username);
        mTimeStamp = view.findViewById(R.id.image_time_posted);
        mHearRed = view.findViewById(R.id.image_heart_red);
        mHeartWhite = view.findViewById(R.id.image_heart);
        mProfileImage = view.findViewById(R.id.profile_photo);
        mComment = view.findViewById(R.id.image_speechBubble);


        gestureDetector = new GestureDetector(getActivity(), new GestureListener());
        heart = new Heart(mHeartWhite, mHearRed);

        setupFirebaseAuth();
        try {
            photo = getPhotoFromBundle();
            UniversalImageLoader.setImage(photo.getImage_path(), mPostImage, null, "");
            mActivityNumber = getActivityNumFromBundle();
            getLikesString();

        } catch (NullPointerException e) {
            Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage());
        }


        setupBottomNavigationView();
        getPhotoDetails();


        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            mOnCommentThreadSelectedListener = (OnCommentThreadSelectedListener) getActivity();


        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException " + e.getMessage() );
        }
    }

    private void getLikesString() {
        final List<String> likesString = new ArrayList<>();
        CollectionReference photoRef = db.collection(getString(R.string.dbname_photos));
        photoRef.document(photo.getPhoto_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                likes = (List<String>) documentSnapshot.get(getString(R.string.field_likes));
                if (likes == null || likes.isEmpty()) {
                    mLikedByCurrentUser = false;
                    mLikes.setText("");
                } else {
                    mLikedByCurrentUser = false;
                    for (int i = 0; i < likes.size(); i++) {
                        if (likes.get(i).equals(mAuth.getUid())) {
                            mLikedByCurrentUser = true;
                        }

                        CollectionReference usersRef = db.collection(getString(R.string.dbname_uers));
                        usersRef.document(likes.get(i)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {

                                likesString.add(documentSnapshot.get("username").toString());
                                sb = new StringBuilder();
                                sb.append("Liked by ");
                                if (likesString.size() == 1) {
                                    sb.append(likesString.get(0));

                                } else if (likesString.size() == 2) {
                                    sb.append(likesString.get(0));
                                    sb.append(" and ");
                                    sb.append(likesString.get(1));

                                } else if (likesString.size() == 3) {
                                    sb.append(likesString.get(0));
                                    sb.append(", ");
                                    sb.append(likesString.get(1));
                                    sb.append(" and ");
                                    sb.append(likesString.get(2));

                                } else if (likesString.size() > 3) {
                                    int n = likesString.size() - 3;
                                    sb.append(likesString.get(0));
                                    sb.append(", ");
                                    sb.append(likesString.get(1));
                                    sb.append(", ");
                                    sb.append(likesString.get(2));
                                    sb.append(" and ");
                                    sb.append(n);
                                    sb.append(" others");

                                }
                                mLikes.setText(sb.toString());

                            }
                        });
                    }

                }
                setupWidgets();
            }
        });
    }


    public class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            final DocumentReference photoDocRef = db.collection(getString(R.string.dbcollection_photos)).document(photo.getPhoto_id());

            photoDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    likes = (List<String>) documentSnapshot.get(getString(R.string.field_likes));
                    if (likes == null) {
                        //add new like
                        addNewLike();
                        heart.toggleLike();

                    } else {
                        if (likes.contains(mAuth.getUid())) {
                            int idx = likes.indexOf(mAuth.getUid());
                            likes.remove(idx);
                            Map<String, Object> updates = new HashMap<>();
                            updates.put(getString(R.string.field_likes), likes);
                            photoDocRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    heart.toggleLike();

                                    getLikesString();

                                }
                            });
                        } else {
                            heart.toggleLike();
                            addNewLike();
                        }

                    }
                }
            });

            Log.d(TAG, "onDoubleTap: double tap detected.");

            return true;
        }
    }


    private void addNewLike() {
        Log.d(TAG, "addNewLike: adding new like");
        final Like like = new Like();
        like.setUser_id(mAuth.getCurrentUser().getUid());
        final DocumentReference photoDocRef = db.collection(getString(R.string.dbcollection_photos)).document(photo.getPhoto_id());
        photoDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                likes = (List<String>) documentSnapshot.get(getString(R.string.field_likes));
                likes.add(like.getUser_id());
                Map<String, Object> updates = new HashMap<>();
                updates.put(getString(R.string.field_likes), likes);
                photoDocRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        getLikesString();

                    }
                });
            }
        });
    }


    @SuppressLint("ClickableViewAccessibility")
    private void setupWidgets() {
        String timeStampDiff = getTimeStampDifference();
        if (!timeStampDiff.equals("0")) {
            mTimeStamp.setText(timeStampDiff + " DAYS AGO");
        } else {
            mTimeStamp.setText("TODAY");
        }
        UniversalImageLoader.setImage(mProfileUrl, mProfileImage, null, "");
        mBackLabel.setText(username);
        mCaption.setText(photo.getCaption());

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back");
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        mComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back");
                mOnCommentThreadSelectedListener.onCommentThreadSelectedListener(photo);


            }
        });


        if (mLikedByCurrentUser) {
            mHeartWhite.setVisibility(View.GONE);
            mHearRed.setVisibility(View.VISIBLE);
            mHearRed.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: red heart touch detected.");
                    return gestureDetector.onTouchEvent(event);
                }
            });


        } else {
            mHearRed.setVisibility(View.GONE);
            mHeartWhite.setVisibility(View.VISIBLE);
            mHeartWhite.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: white heart touch detected.");
                    return gestureDetector.onTouchEvent(event);
                }
            });
        }

    }

    private void getPhotoDetails() {
        CollectionReference userAccountSettingRef = db.collection(getString(R.string.dbname_user_account_settings));
        DocumentReference document = userAccountSettingRef.document(mAuth.getUid());
        document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                mProfileUrl = documentSnapshot.get("profile_photo").toString();
                username = documentSnapshot.get("username").toString();


            }
        });

    }

    /**
     * Returns a string representing the number of days ago the post was made
     */
    private String getTimeStampDifference() {
        String difference = "";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd 'T' HH:mm 'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("US/Pacific"));
        Date today = calendar.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimeStamp = photo.getDate_created();
        try {
            timestamp = sdf.parse(photoTimeStamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24)));
        } catch (ParseException e) {
            Log.e(TAG, "getTimeStampDifference: ParseException" + e.getMessage());
            difference = "0";
        }
        return difference;

    }

    /**
     * retrieve the imgUrl form the incoming bundle from profileActivity interface
     *
     * @return
     */
    private Photo getPhotoFromBundle() {
        Log.d(TAG, "getUrlFromBundle: arguments " + getArguments());
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getParcelable(getString(R.string.photo));
        } else {
            return null;
        }
    }

    /**
     * retrieve the activity number form the incoming bundle from profileActivity interface
     *
     * @return
     */
    private int getActivityNumFromBundle() {
        Log.d(TAG, "getUrlFromBundle: arguments " + getArguments());
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getInt(getString(R.string.activity_number));
        } else {
            return 0;
        }
    }


    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(mContext, getActivity(), bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(mActivityNumber);

        menuItem.setChecked(true);
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
