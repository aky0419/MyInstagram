package com.example.instagramclone.Profile;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.instagramclone.Models.Photo;
import com.example.instagramclone.R;
import com.example.instagramclone.Utils.BottomNavigationViewHelper;
import com.example.instagramclone.Utils.ViewCommentsFragment;
import com.example.instagramclone.Utils.ViewPostFragment;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;


public class ProfileActivity extends AppCompatActivity implements
        ProfileFragment.OnGridImageSelectedListener, ViewPostFragment.OnCommentThreadSelectedListener {
    private static final String TAG = "ProfileActivity";

    @Override
    public void onGridImageSelected(Photo photo, int activityNumber) {
        ViewPostFragment viewPostFragment = new ViewPostFragment();
        Bundle arg = new Bundle();
        arg.putParcelable(getString(R.string.photo),photo);
        arg.putInt(getString(R.string.activity_number), activityNumber);
        viewPostFragment.setArguments(arg);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, viewPostFragment);
        transaction.addToBackStack(getString(R.string.view_post_fragment));
        transaction.commit();

    }

    @Override
    public void onCommentThreadSelectedListener(Photo photo) {
        ViewCommentsFragment viewCommentsFragment = new ViewCommentsFragment();
        Bundle arg = new Bundle();
        arg.putParcelable(getString(R.string.photo), photo);
        viewCommentsFragment.setArguments(arg);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, viewCommentsFragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();

    }
    private Context mContext = ProfileActivity.this;
    private static final int ACTIVITY_NUM = 4;
    private ImageView profileMenu;
    private ProgressBar mProgressBar;
    private ImageView mProfilePhoto;
    private static final int NUM_GIRD_COLUMNS = 3;



    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        Log.d(TAG, "onCreate: started.");
        init();
//        setupActivityWidgets();
//
//
//        setupBottomNavigationView();
//        setupToolbar();
//        setProfileImage();
//        temGridSetup();
    }

    private void init() {
        Log.d(TAG, "init: inflating" + R.string.profile_fragment);
        ProfileFragment fragment = new ProfileFragment();
        FragmentTransaction transaction = ProfileActivity.this.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container,fragment);
        transaction.addToBackStack(getString(R.string.profile_fragment));
        transaction.commit();

    }

//    private void temGridSetup() {
//        ArrayList<String> imgURLs = new ArrayList<>();
//        imgURLs.add("https://s3.amazonaws.com/user-content.stoplight.io/8987/1541019969018");
//        imgURLs.add("https://www.bravotv.com/sites/bravo/files/whats-for-sale-with-a-view-show-hero.jpg");
//        imgURLs.add("https://jooinn.com/images/view-10.jpg");
//        imgURLs.add("https://images.unsplash.com/photo-1463725876303-ff840e2aa8d5?ixlib=rb-1.2.1&ixid=eyJhcHBfaWQiOjEyMDd9&w=1000&q=80");
//        imgURLs.add("https://www.royalcaribbean.com/content/dam/royal/ports-and-destinations/destinations/southern-caribbean/assets/caribbean-bay-aerial-view-hero.jpg");
//        imgURLs.add("https://home.bt.com/images/the-20-best-views-in-the-uk-revealed-136417214455702601-170411144310.jpg");
//        imgURLs.add("https://home.bt.com/images/travel-back-in-time-with-google-street-view-136396071608202601-150211172511.jpg");
//        imgURLs.add("https://cdn.wallpapersafari.com/40/25/zMDnPd.jpg");
//        imgURLs.add("https://cdn.sandals.com/sandals/v12/images/resorts/sbr/home/main-slider/silent-evening-pool-beach-view.jpg");
//        imgURLs.add("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQi7UrYSwePMzME1YI38n-k2pmg9zrdLM-xd90v9PiM4KV90vEb0w&s");
//        imgURLs.add("https://www.breathlessresorts.com/content/dam/amr/photography/breathless/brepc/BREPC_Aerial_2017_1.jpg");
//        imgURLs.add("https://cache.marriott.com/marriottassets/marriott/BOBXR/bobxr-exterior-aerialview-1580-hor-wide.jpg?interpolation=progressive-bilinear&downsize=1440px:*");
//
//
//
//        setupImageGrid(imgURLs);
//    }
//    private void setupImageGrid(ArrayList<String> imgURLs) {
//        GridView mGridView = findViewById(R.id.gridView);
//        int gridWidth = getResources().getDisplayMetrics().widthPixels;
//        int imageWidth = gridWidth/NUM_GIRD_COLUMNS;
//        mGridView.setColumnWidth(imageWidth);
//
//        GridImageAdapter adapter = new GridImageAdapter(mContext,R.layout.layout_grid_imageview, "", imgURLs);
//        mGridView.setAdapter(adapter);
//
//    }
//
//    private void setProfileImage() {
//        Log.d(TAG, "setProfileImage: setting profile photo.");
//        String imgURL = "s3.amazonaws.com/user-content.stoplight.io/8987/1541019969018";
//        UniversalImageLoader.setImage(imgURL,mProfilePhoto,mProgressBar,"https://");
//
//    }
//
//    private void setupActivityWidgets() {
//        mProgressBar = findViewById(R.id.profileProgressBar);
//        mProgressBar.setVisibility(View.GONE);
//        mProfilePhoto = findViewById(R.id.profile_photo);
//    }
//
//    private void setupToolbar() {
//        Toolbar toolbar = findViewById(R.id.profileToolBar);
//        setSupportActionBar(toolbar);
//        profileMenu = findViewById(R.id.profileMenu);
//        profileMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d(TAG, "onClick: navigating to account settings");
//                Intent intent = new Intent(mContext, AccountSettingActivity.class);
//
//                startActivity(intent);
//            }
//        });
//
//
//    }
//
//
//
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext,this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }



}
