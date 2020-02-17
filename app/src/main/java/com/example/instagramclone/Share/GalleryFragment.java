package com.example.instagramclone.Share;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.instagramclone.R;

public class GalleryFragment extends Fragment {
    private static final String TAG= "GalleryFragment";

    //widgets
    private GridView gridView;
    private ImageView galleryImageView, shareClose;
    private ProgressBar mProgressBar;
    private Spinner directorySpinner;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery,container,false);
        galleryImageView = view.findViewById(R.id.galleryImageView);
        mProgressBar = view.findViewById(R.id.progressBar);
        gridView = view.findViewById(R.id.gridView);
        directorySpinner = view.findViewById(R.id.spinnerDirectory);
        mProgressBar.setVisibility(View.GONE);
        Log.d(TAG, "onCreateView: started.");


        shareClose = view.findViewById(R.id.ivCloseShare);

        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing gallery fragment.");
                getActivity().finish();
            }
        });

        TextView nextScreen = view.findViewById(R.id.tvNext);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to the final share screen.");

            }
        });
        

        
        return view;

    }
}
