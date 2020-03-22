package com.example.instagramclone;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.instagramclone.Utils.BottomNavigationViewHelper;
import com.example.instagramclone.Utils.SquareImageView;
import com.example.instagramclone.Utils.UniversalImageLoader;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class ViewPostFragment extends Fragment {
    private static final String TAG = "ViewPostFragment";

    public ViewPostFragment() {
        super();
        setArguments(new Bundle());
    }

    private BottomNavigationViewEx bottomNavigationView;
    private Context mContext;
    private String imgUrl;
    private int mActivityNumber = 0;

    //widgets
    private SquareImageView mPostImage;
    TextView mBackLabel, mCaption, mUsername, mTimeStamp;
    ImageView mBackArrow, mEllipses, mHearRed, mHeartWhite, mProfileImage;
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_post,container, false);

        mContext = getActivity();
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


        try {
            imgUrl = getUrlFromBundle();
            UniversalImageLoader.setImage(imgUrl, mPostImage,null, "");
            mActivityNumber = getActivityNumFromBundle();

        }catch (NullPointerException e) {
            Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage());
        }

        setupBottomNavigationView();
        return view;
    }

    /**
     * retrieve the imgUrl form the incoming bundle from profileActivity interface
     * @return
     */
    private String getUrlFromBundle() {
        Log.d(TAG, "getUrlFromBundle: arguments " + getArguments());
        Bundle bundle = this.getArguments();
        if (bundle !=null) {
            return bundle.getString(getString(R.string.image_url));
        } else {
            return null;
        }
    }

    /**
     * retrieve the activity number form the incoming bundle from profileActivity interface
     * @return
     */
    private int getActivityNumFromBundle() {
        Log.d(TAG, "getUrlFromBundle: arguments " + getArguments());
        Bundle bundle = this.getArguments();
        if (bundle !=null) {
            return bundle.getInt(getString(R.string.activity_number));
        } else {
            return 0;
        }
    }


    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewHelper.setupBottomNavigationView( bottomNavigationView);
        BottomNavigationViewHelper.enableNavigation(mContext,getActivity(), bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(mActivityNumber);

        menuItem.setChecked(true);
    }
}
