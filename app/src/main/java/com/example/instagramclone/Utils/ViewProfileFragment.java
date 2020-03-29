package com.example.instagramclone.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.instagramclone.Models.Comment;
import com.example.instagramclone.Models.Photo;
import com.example.instagramclone.Models.User;
import com.example.instagramclone.Profile.AccountSettingActivity;
import com.example.instagramclone.Profile.ProfileActivity;
import com.example.instagramclone.Profile.ProfileFragment;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class ViewProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";

    public interface OnGridImageSelectedListener {
        void onGridImageSelected(Photo photo, int activityNumber);
    }

    ProfileFragment.OnGridImageSelectedListener onGridImageSelectedListener;

    private TextView mPosts, mFollowers, mFollowing, mDisplayName, mUsername, mWebsite, mDescription, mEditProfile, follow, unfollow, following;
    private CircleImageView mProfilePhoto;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private ImageView profileMenu;
    private ProgressBar mProgressBar;
    private GridView gridView;

    private User mUser;

    private Context mContext;
    private static final int ACTIVITY_NUM = 3;


    private static final int NUM_GIRD_COLUMNS = 3;

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReference;
    private FirebaseFirestore db;

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_profile, container, false);

        mPosts = view.findViewById(R.id.tvPosts);
        mFollowers = view.findViewById(R.id.tvFollowers);
        mFollowing = view.findViewById(R.id.tvFollowing);
        mDisplayName = view.findViewById(R.id.display_name);
        mUsername = view.findViewById(R.id.profileName);
        mWebsite = view.findViewById(R.id.display_website);
        mDescription = view.findViewById(R.id.display_description);
        unfollow = view.findViewById(R.id.unfollow);
        following = view.findViewById(R.id.following);
        follow = view.findViewById(R.id.follow);
        mEditProfile = view.findViewById(R.id.textEditProfile);

        setupFirebaseAuth();



        try {
            mUser = getUserFromBundle();
            init();
        }catch (NullPointerException e) {
            Log.e(TAG, "onCreateView: NullPointerException " + e.getMessage() );
            Toast.makeText(mContext, "something went wrong", Toast.LENGTH_SHORT).show();
            getActivity().getSupportFragmentManager().popBackStack();
        }

        mEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to" +mContext.getString(R.string.edit_profile_fragment));
                Intent intent = new Intent(mContext, AccountSettingActivity.class);
                intent.putExtra(getString(R.string.calling_activity), getString(R.string.profile_activity));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }
        });


        gridView = view.findViewById(R.id.gridView);
        mProgressBar = view.findViewById(R.id.profileProgressBar);
        mProfilePhoto = view.findViewById(R.id.profile_photo);
        toolbar = view.findViewById(R.id.profileToolBar);
        bottomNavigationView = view.findViewById(R.id.bottomNavViewBar);
        profileMenu = view.findViewById(R.id.profileMenu);
        mContext = getActivity();

        Log.d(TAG, "onCreateView: started.");

        setCurrentUserProfile();

        isFollowing();
        setupBottomNavigationView();
        setupToolbar();
        //setupGridView();

        follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: now following " + mUser.getUsername());

                    Map<String, Object> mFollow = new HashMap<>();
                    mFollow.put(getString(R.string.field_follower), mAuth.getCurrentUser().getUid());
                    mFollow.put(getString(R.string.field_following), mUser.getUser_id());
                    db.collection(getString(R.string.dbname_follow)).add(mFollow).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                            if (task.isSuccessful()) {
                                setFollowing();
                            }
                        }
                    });


            }
        });

        unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: now unfollowing " + mUser.getUsername());
                final CollectionReference followRef = db.collection(getString(R.string.dbname_follow));
                Query query = followRef.whereEqualTo(getString(R.string.field_following), mUser.getUser_id())
                        .whereEqualTo(getString(R.string.field_follower), mAuth.getCurrentUser().getUid());
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            for (DocumentSnapshot d : documents) {
                                d.getReference().delete();
                            }
                            setUnfollowing();
                        }
                    }
                });

            }
        });

        return view;
    }
    
    private void isFollowing() {
        Log.d(TAG, "isFollowing: checking if following this user.");
        setFollowing();

        final CollectionReference followRef = db.collection(getString(R.string.dbname_follow));
        Query query = followRef.whereEqualTo(getString(R.string.field_following), mUser.getUser_id())
                .whereEqualTo(getString(R.string.field_follower), mAuth.getCurrentUser().getUid());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: found user");
                    List<DocumentSnapshot> documents = task.getResult().getDocuments();
                    if (documents.size() !=0) {
                        setFollowing();
                    } else {
                        setUnfollowing();
                    }
                }
            }
        });

    }

    private void setFollowing() {
        Log.d(TAG, "setFollowing: updating UI for following this user");
        follow.setVisibility(View.GONE);
        unfollow.setVisibility(View.VISIBLE);
    }

    private void setUnfollowing() {
        Log.d(TAG, "setFollowing: updating UI for unfollowing this user");
        follow.setVisibility(View.VISIBLE);
        unfollow.setVisibility(View.GONE);
    }

    private void setCurrentUserProfile() {
        Log.d(TAG, "setFollowing: updating UI for current user");
        if (mUser.getUser_id().equals(mAuth.getCurrentUser().getUid())) {
            mEditProfile.setVisibility(View.VISIBLE);
            follow.setVisibility(View.GONE);
            unfollow.setVisibility(View.GONE);
        }
    }

    private void init() {
        // 1) get profile details; set profile widgets
        db.collection(getString(R.string.dbname_user_account_settings)).document(mUser.getUser_id()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    Map<String, Object> data = task.getResult().getData();
                    mPosts.setText(data.get(getString(R.string.field_posts)).toString());
                    mFollowers.setText(data.get(getString(R.string.field_followers)).toString());
                    mFollowing.setText(data.get(getString(R.string.field_following)).toString());
                    mDisplayName.setText(data.get(getString(R.string.field_display_name)).toString());
                    mDescription.setText(data.get(getString(R.string.field_description)).toString());
                    mUsername.setText(data.get(getString(R.string.field_username)).toString());
                    mWebsite.setText(data.get(getString(R.string.field_website)).toString());

                    String profileImageUrl = data.get(getString(R.string.field_profile_photo)).toString();
                    ImageLoader imageLoader = ImageLoader.getInstance();
                    imageLoader.displayImage(profileImageUrl,mProfilePhoto);

                    mProgressBar.setVisibility(View.GONE);
                }
            }
        });
        // 2) get user's profile photos
        final ArrayList<Photo> photos = new ArrayList<>();
        final ArrayList<String> imageUrls = new ArrayList<>();
        CollectionReference photoRef = db.collection(getString(R.string.dbname_photos));
        Query query = photoRef.whereEqualTo(getString(R.string.field_user_id),mUser.getUser_id());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
           if (task.isSuccessful()) {
               QuerySnapshot result = task.getResult();
               for (DocumentSnapshot d : result) {
                   photos.add(d.toObject(Photo.class));
                   final Photo p = photos.get(photos.size()-1);
                   d.getReference().collection("comments").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                       @Override
                       public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                           p.setComments(queryDocumentSnapshots.toObjects(Comment.class));
                       }
                   });
                   imageUrls.add(d.get(getString(R.string.field_image_path)).toString());
               }
               //setup GridView
               GridImageAdapter adapter = new GridImageAdapter(mContext, R.layout.layout_grid_imageview,"",imageUrls);
               gridView.setAdapter(adapter);
               gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                   @Override
                   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                           onGridImageSelectedListener.onGridImageSelected(photos.get(position), ACTIVITY_NUM);
                   }
               });
           }
            }
        });
    }
    private User getUserFromBundle() {
        Log.d(TAG, "getUserFromBundle: argument: " + getArguments());
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            return bundle.getParcelable(getString(R.string.intent_user));
        } else {
            return null;
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        try {
            onGridImageSelectedListener = (ProfileFragment.OnGridImageSelectedListener) getActivity();
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException " + e.getMessage());
        }
        super.onAttach(context);
    }




    private void setupToolbar() {
        ((ProfileActivity) getActivity()).setSupportActionBar(toolbar);
        profileMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to account settings");
                Intent intent = new Intent(mContext, AccountSettingActivity.class);


                startActivity(intent);
            }
        });


    }


    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.setupBottomNavigationView((BottomNavigationViewEx) bottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(mContext, getActivity(), (BottomNavigationViewEx) bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);

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
        /*
         * Retrieves the account settings for the user currently logged in
         * Database: user_account_settings node
         */

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
    }

        @Override
        public void onStart () {
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

