package com.example.instagramclone.Share;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.instagramclone.R;
import com.example.instagramclone.Utils.FileSearch;

import java.io.File;
import java.util.ArrayList;

public class GalleryFragment extends Fragment {
    private static final String TAG= "GalleryFragment";

    //widgets
    private GridView gridView;
    private ImageView galleryImageView, shareClose;
    private ProgressBar mProgressBar;
    private Spinner directorySpinner;

    //vars
    private ArrayList<String> directories;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery,container,false);
        galleryImageView = view.findViewById(R.id.galleryImageView);
        mProgressBar = view.findViewById(R.id.progressBar);
        gridView = view.findViewById(R.id.gridView);
        directorySpinner = view.findViewById(R.id.spinnerDirectory);
        mProgressBar.setVisibility(View.GONE);
        directories = new ArrayList<>();

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
        

        init();

        return view;

    }

    private void init() {

        String CAMERA = getActivity().getExternalFilesDir(Environment.DIRECTORY_DCIM).getPath();
        String PICTURES = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).getPath();

        if (FileSearch.getDirectoryPaths(PICTURES) != null) {
            directories = FileSearch.getDirectoryPaths(PICTURES);
        }
        directories.add(CAMERA);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item);
        
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        directorySpinner.setAdapter(adapter);
        
        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //check for other folders inside "/storage/emulated/0/pictures"
    }
}
