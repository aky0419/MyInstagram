package com.example.instagramclone.Utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.instagramclone.Home.HomeActivity;
import com.example.instagramclone.Models.Photo;
import com.example.instagramclone.Profile.AccountSettingActivity;
import com.example.instagramclone.Profile.EditProfileFragment;
import com.example.instagramclone.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.LogDescriptor;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

public class FirebaseMethods  {
    private static final String TAG = "FirebaseMethods";

    private double mPhotoUploadProgress = 0;


    //Firebase
    private FirebaseAuth mAuth;
    private Context mContext;
    private String userID;
    private FirebaseFirestore db;
    private StorageReference mStorageRef;


    public FirebaseMethods(Context mContext) {
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseFirestore.setLoggingEnabled(true);
        this.mContext = mContext;

        if (mAuth.getCurrentUser() !=null) {
            userID = mAuth.getCurrentUser().getUid();
        }
    }


    public void uploadImageToStorage(final String photoType, final String imageURL, final String caption, final Bitmap bitmap) {
        // StorageReference storageRef = storage.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        // Create a reference to 'images/mountains.jpg'


        final String user_id = mAuth.getCurrentUser().getUid();
        getImageCount(new Callback() {
            @Override
            public void onSuccess(Object obj) {

                if(photoType.equals(mContext.getString(R.string.new_photo))) {
                int count = (int) obj;
                final StorageReference ImagesRef = mStorageRef.child("photos/users/" + "/" + user_id + "/photo" + (count+1));
                // Get the data from an ImageView as bytes
                    Bitmap bm = bitmap;
                    if (bm == null) {
                        bm = ImageManager.getBitmap(imageURL);
                    }

                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                byte[] data = baos.toByteArray();

                UploadTask uploadTask = ImagesRef.putBytes(data);
                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Log.d(TAG, "onFailure: Fail to upload.");
                        Toast.makeText(mContext,"photo upload failed", Toast.LENGTH_SHORT).show();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        // ...
                        Task<Uri> filebaseUrlTask = ImagesRef.getDownloadUrl();
                        filebaseUrlTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Log.d(TAG, "onSuccess: Successfully uploaded " + uri);
                                Toast.makeText(mContext,"photo upload success", Toast.LENGTH_SHORT).show();

                                // add the new photo to 'photos' node
                                String url = uri.toString();
                                addPhotoToDatabase(caption, url);

                                //navigate to the main feed so the user can see their photo
                                Intent intent = new Intent(mContext, HomeActivity.class);
                                mContext.startActivity(intent);
                            }
                        });
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                        double progress = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        if (progress - 15 > mPhotoUploadProgress) {
                            Toast.makeText(mContext,"photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                            mPhotoUploadProgress = progress;

                        }
                        Log.d(TAG, "onProgress: upload progress" + progress + "% done");

                    }
                });
            }

                else if (photoType.equals(mContext.getString(R.string.profile_photo))) {
                    Log.d(TAG, "onSuccess: uploading new PROFILE photo");

                    ((AccountSettingActivity)mContext).setViewPager(
                            ((AccountSettingActivity)mContext).pagerAdapter.getFragmentNumber(mContext.getString(R.string.edit_profile_fragment))
                    );

                    final StorageReference ImagesRef = mStorageRef.child("photos/users/" + "/" + user_id + "/profile_photo");
                    // Get the data from an ImageView as bytes
                    Bitmap bm = bitmap;
                    if (bm == null) {
                        bm = ImageManager.getBitmap(imageURL); //????????????????????????????????
                    }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();

                    UploadTask uploadTask = ImagesRef.putBytes(data);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                            Log.d(TAG, "onFailure: Fail to upload.");
                            Toast.makeText(mContext,"photo upload failed", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                            // ...
                            Task<Uri> filebaseUrlTask = ImagesRef.getDownloadUrl();
                            filebaseUrlTask.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    Log.d(TAG, "onSuccess: Successfully uploaded " + uri);
                                    Toast.makeText(mContext,"photo upload success", Toast.LENGTH_SHORT).show();

                                    // insert into 'user_account_setting' node
                                    String url = uri.toString();
                                    setProfilePhoto(url);


                                    //navigate to the main feed so the user can see their photo
//                                    Intent intent = new Intent(mContext, EditProfileFragment.class);
//                                    mContext.startActivity(intent);
                                }
                            });
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {

                            double progress = (100 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            if (progress - 15 > mPhotoUploadProgress) {
                                Toast.makeText(mContext,"photo upload progress: " + String.format("%.0f", progress) + "%", Toast.LENGTH_SHORT).show();
                                mPhotoUploadProgress = progress;

                            }
                            Log.d(TAG, "onProgress: upload progress" + progress + "% done");

                        }
                    });


                }
            }
        });


    }

    private void setProfilePhoto(String url) {
        Log.d(TAG, "setProfilePhoto: setting new profile image: " + url);
        Map<String, Object> user_account_settings = new HashMap<>();
        user_account_settings.put(mContext.getString(R.string.profile_photo), url);
        db.collection(mContext.getString(R.string.dbname_user_account_settings)).document(mAuth.getUid()).update(user_account_settings);

    }

    private void addPhotoToDatabase (String caption, String url) {
        Log.d(TAG, "addPhotoToDatabase: adding photo to database.");


        String tags = StringManipulation.getTags(caption);


        String newPhotoKey = StringManipulation.generateRandomChars();
        Photo photo = new Photo();
        photo.setCaption(caption);
        photo.setImage_path(url);
        photo.setDate_created(getTimestamp());
        photo.setTags(tags);
        photo.setUser_id(mAuth.getCurrentUser().getUid());
        photo.setPhoto_id(newPhotoKey);

        //insert into database
        db.collection(mContext.getString(R.string.dbname_photos)).document(photo.getPhoto_id()).set(photo);
    }

    private String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("America/Monterrey"));
        return sdf.format(new Date());
    }

    public  void getImageCount(final Callback cb) {
    final CollectionReference usersRef = db.collection("photos");
        Query query = usersRef.whereEqualTo("user_id",mAuth.getCurrentUser().getUid());
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()) {
                     int imageCount = task.getResult().size();
                     cb.onSuccess(imageCount);
                }
            }
        });

}


    public interface Callback {
         void onSuccess(Object obj);
 }

    public void updateUsername(String username) {
        Log.d(TAG, "updateUsername: updating username to: " + username);

        Map<String, Object> user = new HashMap<>();
        user.put("username", StringManipulation.condenseUsername(username));
        db.collection("users").document(mAuth.getUid()).update(user);


        Map<String, Object> user_account_settings = new HashMap<>();
        user_account_settings.put("username", StringManipulation.condenseUsername(username));
        db.collection("user_account_settings").document(mAuth.getUid()).update(user_account_settings);
    }

    public void updateEmail(String email) {
        Log.d(TAG, "updateUsername: updating username to: " + email);

        Map<String, Object> user = new HashMap<>();
        user.put("email", StringManipulation.condenseUsername(email));
        db.collection("users").document(mAuth.getUid()).update(user);

    }




    public void registerNewEmail(final String email, final String password, final String username) {
                final CollectionReference usersRef = db.collection("users");
                Query query = usersRef.whereEqualTo("username", StringManipulation.condenseUsername(username));
                query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            QuerySnapshot result = task.getResult();
                            if (result.size() > 0) {
                                Log.d(TAG, "has existed user");
                                Random random = new Random();
                                // random 3 digits from 100 ~ 999
                                int random3Digits = random.nextInt(900) + 100;
                                createUserWithEmailAndPassword(email, password, username + random3Digits);

                            } else {
                                Log.d(TAG, "we can create an user now");
                                createUserWithEmailAndPassword(email, password, username);

                            }

                        } else {
                            Log.d(TAG, "task failed");
                        }


                    }
                });
            }

            private void createUserWithEmailAndPassword(final String email, String password, final String username) {
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Sign in success, update UI with the signed-in user's information
                                    Log.d(TAG, "onComplete: Authstate changed: " + userID);
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    userID = mAuth.getCurrentUser().getUid();
                                    Toast.makeText(mContext, R.string.auth_success,
                                            Toast.LENGTH_SHORT).show();
                                    addNewUser(email, username);
                                    addNewUserAccountSetting("", "", "", username, "");
                                    sendVerificationEmail();
                                    mAuth.signOut();
                                    //   mAuth.signOut();
                                } else {
                                    // If sign in fails, display a message to the user.
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(mContext, R.string.auth_failed,
                                            Toast.LENGTH_SHORT).show();

                                    //updateUI(null);
                                }

                                // ...
                            }
                        });


            }

            public void sendVerificationEmail() {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {

                            } else {
                                Toast.makeText(mContext, "could't send verification email.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }

            /**
             * add information to the users nodes
             * add information to the user_account_settings node
             */


            public void addNewUser(String email, String username) {

                String currentUserId = mAuth.getCurrentUser().getUid();

                Map<String, Object> user = new HashMap<>();
                //email,phone_number,user_id,username
                user.put("email", email);
                user.put("phone_number", "");
                user.put("user_id", currentUserId);
                user.put("username", StringManipulation.condenseUsername(username));
                db.collection("users").document(currentUserId).set(user)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "addNewUser onComplete: ");
                            }
                        });


            }

            public void addNewUserAccountSetting(String description, String display_name, String profile_photo, String username, String website) {
                String currentUserId = mAuth.getCurrentUser().getUid();
                Map<String, Object> user_account_setting = new HashMap<>();
                user_account_setting.put("description", description);
                user_account_setting.put("display_name", display_name);
                user_account_setting.put("followers", 0);
                user_account_setting.put("following", 0);
                user_account_setting.put("posts", 0);
                user_account_setting.put("profile_photo", profile_photo);
                user_account_setting.put("username", StringManipulation.condenseUsername(username));
                user_account_setting.put("website", website);
                db.collection("user_account_settings").document(currentUserId).set(user_account_setting)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Log.d(TAG, "addNewUserAccountSetting onComplete: ");
                            }
                        });

            }


        }