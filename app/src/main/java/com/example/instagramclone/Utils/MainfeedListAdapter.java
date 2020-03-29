package com.example.instagramclone.Utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.instagramclone.Home.HomeActivity;
import com.example.instagramclone.Models.Comment;
import com.example.instagramclone.Models.Like;
import com.example.instagramclone.Models.Photo;
import com.example.instagramclone.Models.User;
import com.example.instagramclone.Profile.ProfileActivity;
import com.example.instagramclone.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.zip.Inflater;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainfeedListAdapter extends ArrayAdapter<Photo> {

    private static final String TAG = "MainfeedListAdapter";

    private LayoutInflater mInflater;
    private int mLayoutResource;
    private List<Photo> mPhotos;
    private Context mContext;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    private String currentUsername;

    public MainfeedListAdapter(@NonNull Context context, int resource, @NonNull List<Photo> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContext = context;
        mLayoutResource = resource;
        mPhotos = objects;
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

    }

    private static class ViewHolder {
        CircleImageView mProfileImage;
        TextView username, timeDelta, caption, likes, comments;
        SquareImageView image;
        ImageView heartRed, heartWhite, comment;


        User user = new User();
        StringBuilder users;
        String mLikesString;
        boolean likedByCurrentUser;
        Heart heart;
        GestureDetector detector;
        Photo photo;
        List<String> mLikes;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(mLayoutResource, parent, false);
            holder.mProfileImage = convertView.findViewById(R.id.profile_photo);
            holder.username = convertView.findViewById(R.id.tvBackLabel);
            holder.timeDelta = convertView.findViewById(R.id.image_time_posted);
            holder.caption = convertView.findViewById(R.id.image_caption);
            holder.likes = convertView.findViewById(R.id.image_likes);
            holder.comments = convertView.findViewById(R.id.image_comments);
            holder.heartRed = convertView.findViewById(R.id.image_heart_red);
            holder.heartWhite = convertView.findViewById(R.id.image_heart);
            holder.comment = convertView.findViewById(R.id.image_speechBubble);
            holder.image = convertView.findViewById(R.id.post_image);

            holder.heart = new Heart(holder.heartWhite, holder.heartRed);
            holder.photo = getItem(position);
            holder.detector = new GestureDetector(mContext, new GestureListener(holder));
            holder.mLikes = new ArrayList<>();
            holder.mLikesString = "";
            holder.likedByCurrentUser = false;
            holder.users = new StringBuilder();

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        //get the current users username

//        getCurrentUsername();

        //get likes string
        getLikesString(holder);

        //set the comments
//        if (getItem(position).getComments() != null && getItem(position).getComments().size()>0) {
//            holder.comments.setText("View all " + getItem(position)..getComments().size() + " comments");
//        } else {
//            holder.comments.setText("");
//        }
//        holder.comments.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((HomeActivity)mContext).onCommentThreadSelected(getItem(position));
//            }
//        });

        //set the time it was posted
        String timeStampDifference = getTimeStampDifference(getItem(position));
        if (timeStampDifference.equals("0")) {
            holder.timeDelta.setText("Today");
        } else {
            holder.timeDelta.setText(timeStampDifference + " DAYS AGO");
        }

        //set the profile image
        ImageLoader imageLoader = ImageLoader.getInstance();
        imageLoader.displayImage(getItem(position).getImage_path(), holder.image);

        //set the profile image
        db.collection(mContext.getString(R.string.dbname_user_account_settings)).document(getItem(position).getUser_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(TAG, "onSuccess: found user: " + documentSnapshot.get(mContext.getString(R.string.field_username)).toString());
                String imageUrl = documentSnapshot.get(mContext.getString(R.string.field_profile_photo)).toString();
                String username = documentSnapshot.get(mContext.getString(R.string.field_username)).toString();

                final ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.displayImage(imageUrl, holder.mProfileImage);
                holder.mProfileImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ProfileActivity.class);
                        intent.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home_activity));
                        intent.putExtra(mContext.getString(R.string.intent_user), holder.user);
                        mContext.startActivity(intent);

                    }
                });

                holder.username.setText(username);
                holder.username.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(mContext, ProfileActivity.class);
                        intent.putExtra(mContext.getString(R.string.calling_activity), mContext.getString(R.string.home_activity));
                        intent.putExtra(mContext.getString(R.string.intent_user), holder.user);
                        mContext.startActivity(intent);

                    }
                });
            }
        });

        

        //get the user object
        db.collection(mContext.getString(R.string.dbname_users)).document(getItem(position).getUser_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(TAG, "onSuccess: get the user " + documentSnapshot.get(mContext.getString(R.string.field_user_id)).toString());
                holder.user = documentSnapshot.toObject(User.class);
                    }
                });

        return convertView;
    }
    
//    private void getCurrentUsername() {
//        Log.d(TAG, "getCurrentUsername: retrieving username from firebase");
//        final DocumentReference user_id_ref = db.collection(mContext.getString(R.string.dbname_user_account_settings)).document(mAuth.getCurrentUser().getUid());
//        user_id_ref.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//            if (documentSnapshot.exists()) {
//                currentUsername = documentSnapshot.get(mContext.getString(R.string.field_user_id)).toString();
//            }
//            }
//        });
//    }


    public class GestureListener extends GestureDetector.SimpleOnGestureListener {

        ViewHolder mHolder;
        public GestureListener(ViewHolder holder) {
            mHolder = holder;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            final DocumentReference photoDocRef = db.collection(mContext.getString(R.string.dbcollection_photos)).document(mHolder.photo.getPhoto_id());

            photoDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {

                    List<String> likesFromDB =  (List<String>) documentSnapshot.get(mContext.getString(R.string.field_likes));
                    if (likesFromDB == null) {
                        //add new like
                        addNewLike(mHolder);
                        mHolder.heart.toggleLike();

                    } else {
                        mHolder.mLikes = likesFromDB;
                        if ( mHolder.mLikes.contains(mAuth.getUid())) {
                            int idx = mHolder.mLikes.indexOf(mAuth.getUid());
                            mHolder.mLikes.remove(idx);
                            Map<String, Object> updates = new HashMap<>();
                            updates.put(mContext.getString(R.string.field_likes), mHolder.mLikes);
                            photoDocRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    mHolder.heart.toggleLike();

                                    getLikesString(mHolder);

                                }
                            });
                        } else {
                            mHolder.heart.toggleLike();
                            addNewLike(mHolder);
                        }

                    }
                }
            });

            Log.d(TAG, "onDoubleTap: double tap detected.");

            return true;
        }
    }

    private void getLikesString(final ViewHolder holder) {
        Log.d(TAG, "getLikesString: getting likes string");

        try {

        final List<String> likesString = new ArrayList<>();
        CollectionReference photoRef = db.collection(mContext.getString(R.string.dbname_photos));
        photoRef.document(holder.photo.getPhoto_id()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                documentSnapshot.getReference().collection("comments").get().addOnSuccessListener()
                List<String> likesFromDB = (List<String>) documentSnapshot.get(mContext.getString(R.string.field_likes));
                if (likesFromDB == null || likesFromDB.isEmpty()) {
                    holder.likedByCurrentUser = false;
                    holder.likes.setText("");
                } else {
                    holder.mLikes = (List<String>) documentSnapshot.get(mContext.getString(R.string.field_likes));
                    holder.likedByCurrentUser = false;
                    for (int i = 0; i < holder.mLikes.size(); i++) {
                        if (holder.mLikes.get(i).equals(mAuth.getUid())) {
                            holder.likedByCurrentUser = true;
                        }

                        CollectionReference usersRef = db.collection(mContext.getString(R.string.dbname_uers));
                        usersRef.document(holder.mLikes.get(i)).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                holder.users = new StringBuilder();
                                likesString.add(documentSnapshot.get("username").toString());
                                holder.users.append("Liked by ");
                                if (likesString.size() == 1) {
                                    holder.users.append(likesString.get(0));

                                } else if (likesString.size() == 2) {
                                    holder.users.append(likesString.get(0));
                                    holder.users.append(" and ");
                                    holder.users.append(likesString.get(1));

                                } else if (likesString.size() == 3) {
                                    holder.users.append(likesString.get(0));
                                    holder.users.append(", ");
                                    holder.users.append(likesString.get(1));
                                    holder.users.append(" and ");
                                    holder.users.append(likesString.get(2));

                                } else if (likesString.size() > 3) {
                                    int n = likesString.size() - 3;
                                    holder.users.append(likesString.get(0));
                                    holder.users.append(", ");
                                    holder.users.append(likesString.get(1));
                                    holder.users.append(", ");
                                    holder.users.append(likesString.get(2));
                                    holder.users.append(" and ");
                                    holder.users.append(n);
                                    holder.users.append(" others");

                                }
                                holder.likes.setText(holder.users.toString());

                            }
                        });
                    }

                }
                setupWidgets(holder);
            }
        });
    }

        catch (NullPointerException e) {
            Log.e(TAG, "getLikesString: NullPointerException: " + e.getMessage());
            holder.likedByCurrentUser = false;
            holder.likes.setText("");
            //setup likes string

        }
    }

    private void addNewLike(final ViewHolder holder) {
        Log.d(TAG, "addNewLike: adding new like");
        final Like like = new Like();
        like.setUser_id(mAuth.getCurrentUser().getUid());
        final DocumentReference photoDocRef = db.collection(mContext.getString(R.string.dbcollection_photos)).document(holder.photo.getPhoto_id());
        photoDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                if (documentSnapshot.get(mContext.getString(R.string.field_likes)) == null) {
                    holder.mLikes.add(like.getUser_id());
                    Map<String, Object> updates = new HashMap<>();
                    updates.put(mContext.getString(R.string.field_likes), holder.mLikes);
                    photoDocRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            getLikesString(holder);

                        }
                    });

                } else {
                    holder.mLikes = (List<String>) documentSnapshot.get(mContext.getString(R.string.field_likes));
                    holder.mLikes.add(like.getUser_id());
                    Map<String, Object> updates = new HashMap<>();
                    updates.put(mContext.getString(R.string.field_likes),  holder.mLikes);
                    photoDocRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            getLikesString(holder);

                        }
                    });
                }
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupWidgets(final ViewHolder holder) {
        if (holder.likedByCurrentUser) {
            holder.heartWhite.setVisibility(View.GONE);
            holder.heartRed.setVisibility(View.VISIBLE);
            holder.heartRed.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: red heart touch detected.");
                    return holder.detector.onTouchEvent(event);
                }
            });


        } else {
            holder.heartRed.setVisibility(View.GONE);
            holder.heartWhite.setVisibility(View.VISIBLE);
            holder.heartWhite.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    Log.d(TAG, "onTouch: white heart touch detected.");
                    return  holder.detector.onTouchEvent(event);
                }
            });
        }
    }

    /**
     * Returns a string representing the number of days ago the post was made
     */
    private String getTimeStampDifference(Photo photo) {
        String difference = "";
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd'T'HH:mm:ss'Z' ", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("US/Pacific"));
        Date today = calendar.getTime();
        sdf.format(today);
        Date timestamp;
        final String photoTimeStamp = photo.getDate_created();
        try {
            timestamp = sdf.parse(photoTimeStamp);
            difference = String.valueOf(Math.round(((today.getTime() - timestamp.getTime()) / 1000 / 60 / 60 / 24)));
        } catch (ParseException e) {
            Log.e(TAG, "getTimeStampDifference: ParseException" + e.getMessage());
            difference = "0";
        }
        return difference;

    }
}
