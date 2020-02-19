package com.example.instagramclone.Utils;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;
import java.util.ArrayList;

public class FileSearch {

    public static ArrayList<String> getDirectoryPaths(String directory) {

        /**
         * Search a directory and return a list of all **directories** contained inside
         */
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();
        for (int i=0; i< listfiles.length; i++) {
            if (listfiles[i].isDirectory()) {
                pathArray.add(listfiles[i].getAbsolutePath());
            }
        }

        return pathArray;
    }


    /**
     * Search a directory and return a list of all **files** contained inside
     */
    public static ArrayList<String> getFilePaths(String directory) {
        ArrayList<String> pathArray = new ArrayList<>();
        File file = new File(directory);
        File[] listfiles = file.listFiles();
        for (int i=0; i< listfiles.length; i++) {
            if (listfiles[i].isFile()) {
                pathArray.add(listfiles[i].getAbsolutePath());
            }
        }

        return pathArray;
    }


}
