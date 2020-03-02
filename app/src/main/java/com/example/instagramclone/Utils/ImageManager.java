package com.example.instagramclone.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ImageManager {

    private static final String TAG = "ImageManager";

    public static Bitmap getBitmap(String imageUrl) {
        File imageFile = new File (imageUrl);
        FileInputStream fileInputStream = null;
        Bitmap bitmap = null;

        try{
            fileInputStream = new FileInputStream(imageFile);
            bitmap = BitmapFactory.decodeStream(fileInputStream);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "getBitmap: FileNotFoundException: " +e.getMessage());
        } finally {
            try {
                fileInputStream.close();
            } catch (IOException e) {
                Log.d(TAG, "getBitmap: FileNotFoundException: " +e.getMessage());
            }
        }
        return bitmap;
    }

    public static Bitmap getBitmapImageView(ImageView imageView){
        imageView.setDrawingCacheEnabled(true);
        imageView.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        return bitmap;
    }

}
