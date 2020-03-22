package com.example.instagramclone.Profile;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.example.instagramclone.Models.Photo;
import com.example.instagramclone.R;
import com.example.instagramclone.Utils.BottomNavigationViewHelper;

import com.example.instagramclone.Utils.GridImageAdapter;
import com.example.instagramclone.Utils.StringManipulation;
import com.example.instagramclone.Utils.UniversalImageLoader;
import com.example.instagramclone.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";

    public interface OnGridImageSelectedListener{
        void onGridImageSelected(String imgUrl, int activityNumber);
    }
    OnGridImageSelectedListener onGridImageSelectedListener;

    private TextView mPosts, mFollowers, mFollowing, mDisplayName, mUsername, mWebsite, mDescription, editProfile;
    private CircleImageView mProfilePhoto;
    private Toolbar toolbar;
    private BottomNavigationView bottomNavigationView;
    private ImageView profileMenu;
    private ProgressBar mProgressBar;
    private GridView gridView;

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
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        mPosts = view.findViewById(R.id.tvPosts);
        mFollowers = view.findViewById(R.id.tvFollowers);
        mFollowing = view.findViewById(R.id.tvFollowing);
        mDisplayName = view.findViewById(R.id.display_name);
        mUsername = view.findViewById(R.id.profileName);
        mWebsite = view.findViewById(R.id.display_website);
        mDescription = view.findViewById(R.id.display_description);
        editProfile =view.findViewById(R.id.textEditProfile);
        editProfile.setOnClickListener(new View.OnClickListener() {
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

        setupFirebaseAuth();
        setupBottomNavigationView();
        setupToolbar();
        setupGridView();


        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        try {
            onGridImageSelectedListener = (OnGridImageSelectedListener) getActivity();
        }catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ClassCastException " + e.getMessage());
        }
        super.onAttach(context);
    }



    private void setupGridView() {
        Log.d(TAG, "setupGridView: Setting up image grid.");

        final ArrayList<String> imgUrl = new ArrayList<>();
        final CollectionReference photosRef = db.collection("photos");
        Query query = photosRef.whereEqualTo("user_id", mAuth.getUid());
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                for (int i=0; i<documents.size(); i++) {
                    imgUrl.add(documents.get(i).get("image_path").toString());
                }
                int gridWidth = getResources().getDisplayMetrics().widthPixels;
                int imageWidth = gridWidth/NUM_GIRD_COLUMNS;
                gridView.setColumnWidth(imageWidth);

                GridImageAdapter adapter = new GridImageAdapter(mContext,R.layout.layout_grid_imageview, "", imgUrl);
                gridView.setAdapter(adapter);
                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        onGridImageSelectedListener.onGridImageSelected(imgUrl.get(position), ACTIVITY_NUM );
                    }
                });
            }
        });
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
        BottomNavigationViewHelper.enableNavigation(mContext,getActivity(), (BottomNavigationViewEx) bottomNavigationView);
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

        db.collection("user_account_settings")
                .document(mAuth.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                Map<String, Object> doc = documentSnapshot.getData();
                Log.d(TAG, "onSuccess: setting widgets with data retrieving from firebase database: "+ doc.get("profile_photo"));

                UniversalImageLoader.setImage(doc.get("profile_photo").toString(),mProfilePhoto,null,"");

                mDescription.setText(doc.get("description").toString());

                String username = StringManipulation.expandUsername(doc.get("username").toString());
                mUsername.setText(username);

                mWebsite.setText(doc.get("website").toString());
                mFollowers.setText(doc.get("followers").toString());
                mFollowing.setText(doc.get("following").toString());
                mPosts.setText(doc.get("posts").toString());
                mDisplayName.setText(doc.get("display_name").toString());

                mProgressBar.setVisibility(View.GONE);


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
