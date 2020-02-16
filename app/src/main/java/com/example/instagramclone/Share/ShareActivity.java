package com.example.instagramclone.Share;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.instagramclone.R;
import com.example.instagramclone.Utils.BottomNavigationViewHelper;
import com.example.instagramclone.Utils.Permissions;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

public class ShareActivity extends AppCompatActivity {
    private static final String TAG = "ShareActivity";
    private Context mContext = ShareActivity.this;


    //constants
    private static final int VERIFY_PERMISSIONS_REQUEST =1;
    private static final int ACTIVITY_NUM = 3;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Log.d(TAG, "onCreate: started.");
        
        if(checkPermissionsArray(Permissions.PERMISSIONS)){

        } else {
            verifyPermissions(Permissions.PERMISSIONS);
        }

        setupBottomNavigationView();
    }

    private void verifyPermissions(String[] permissions) {
        Log.d(TAG, "verifyPermissions: verifying permissions.");
        ActivityCompat.requestPermissions(
                ShareActivity.this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }

    /**
     * check array of permissions
     * @param permissions
     * @return
     */
    private boolean checkPermissionsArray(String[] permissions) {

        Log.d(TAG, "checkPermissionsArray: checking permissions array");
        for (int i=0; i< permissions.length; i++) {
            String check = permissions[i];
            if (!checkPermissions(check)) {
                return false;
            }
        }
        return true;
    }

    /**
     * check single permission is it has been verified
     * @param permission
     * @return
     */
    private boolean checkPermissions(String permission) {
        Log.d(TAG, "checkPermissions: checking permission" + permission);
        int checkSelfPermission = ActivityCompat.checkSelfPermission(mContext, permission);
        if (checkSelfPermission != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermissions: \n permission was not granted for " + permission);
            return false;
        }
        else {
            Log.d(TAG, "checkPermissions: \n permission was granted for " + permission);
            return true;
        }
    }


    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext,bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}
