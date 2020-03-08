package com.example.instagramclone.Share;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import com.example.instagramclone.Profile.AccountSettingActivity;
import com.example.instagramclone.R;
import com.example.instagramclone.Utils.FileSearch;
import com.example.instagramclone.Utils.GridImageAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {
    private static final String TAG= "GalleryFragment";
    private static final int NUM_GRID_COLUMNS = 3;
    private static final String mAppend = "file:/";


    //widgets
    private GridView gridView;
    private ImageView galleryImageView, shareClose;
    private ProgressBar mProgressBar;
    private Spinner directorySpinner;
    TextView nextScreen;

    //vars
    private ArrayList<String> directories;
    private String mSelectedImage;

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

        nextScreen = view.findViewById(R.id.tvNext);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to the final share screen.");

                if (isRootTask()) {
                    Intent intent = new Intent(getActivity(),NextActivity.class);
                    intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                    startActivity(intent);
                }
                else {
                    Intent intent = new Intent(getActivity(), AccountSettingActivity.class);
                    intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                    intent.putExtra(getString(R.string.return_to_fragment),getString(R.string.edit_profile_fragment));
                    startActivity(intent);
                    getActivity().finish();
                }

            }
        });
        

        init();

        return view;

    }

    private Boolean isRootTask() {
        if (((ShareActivity)getActivity()).getTask() == 0) {
            return true;
        }
        else {
            return false;
        }
    }

    private void init() {

        String CAMERA = Environment.getExternalStorageDirectory().getPath() + "/DCIM/Camera";
        String PICTURES = Environment.getExternalStorageDirectory().getPath() + "/Download";
        //check for other folders inside "/storage/emulated/0/pictures"
//        int permissionCheck1 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
//        int permissionCheck2 = ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        if (permissionCheck1 != PackageManager.PERMISSION_GRANTED || permissionCheck2 != PackageManager.PERMISSION_GRANTED) {
//           /* ActivityCompat.requestPermissions(this,
//                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
//                    124);*/
//        }
//        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
//        ContentResolver resolver = getActivity().getContentResolver();
//        Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, MediaStore.Images.Media._ID);
//        int count = cursor.getCount();
//        for (int i = 0; i < count; i++) {
//            cursor.moveToPosition(i);
//            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
//            //Store the path of the image
//            directories.add(cursor.getString(dataColumnIndex));
//            Log.i("PATH", cursor.getString(dataColumnIndex));
//        }

        if (FileSearch.getDirectoryPaths(PICTURES) != null){
            directories. add(PICTURES);
        }



        directories.add(CAMERA);

        ArrayList<String> directoryNames = new ArrayList<>();
        for (int i = 0; i < directories.size(); i++){
            directoryNames.add(directories.get(i).replace(Environment.getExternalStorageDirectory().getPath(),""));
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,directoryNames);
        
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directorySpinner.setAdapter(adapter);
        
        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            setupGridView(directories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }
    private void setupGridView(String selectedDirectory) {
        Log.d(TAG, "setupGridView: directory chosen " + selectedDirectory );

//        final ArrayList<String> imgURLs = new ArrayList<>();
//        final String[] columns = { MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID };
//        ContentResolver resolver = getActivity().getContentResolver();
//        Cursor cursor = resolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, MediaStore.Images.Media._ID);
//        int count = cursor.getCount();
//        for (int i = 0; i < count; i++) {
//            cursor.moveToPosition(i);
//            int dataColumnIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
//            //Store the path of the image
//            imgURLs.add(cursor.getString(dataColumnIndex));
//            Log.i("PATH", imgURLs.get(i));
//        }
        final ArrayList<String> imgURLs = FileSearch.getFilePaths(selectedDirectory);
        //set the grid column width
        final int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        //use grid adapter to adapt images to gridView
        GridImageAdapter adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, mAppend, imgURLs);
        gridView.setAdapter(adapter);

        //set the first image to be displayed when the activity fragment view is inflated
        setImage(imgURLs.get(0), galleryImageView);
        mSelectedImage =  imgURLs.get(0);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setImage(imgURLs.get(position),galleryImageView);
                mSelectedImage =  imgURLs.get(position);
            }
        });
    }

    private void setImage(String imgURL, ImageView image) {
        Log.d(TAG, "setImage: setting image.");
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(mAppend + imgURL, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                mProgressBar.setVisibility(View.VISIBLE);

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }
}
