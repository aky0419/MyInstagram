package com.example.instagramclone.Utils;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.instagramclone.Models.Comment;
import com.example.instagramclone.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class commentListAdapter extends ArrayAdapter<Comment> {
    private static final String TAG = "commentListAdapter";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mReference;
    private FirebaseFirestore db;

    private LayoutInflater mInflater;
    private int layoutResource;
    private Context mContext;
    private String mProfileUrl;
    private String mUsername;

    public commentListAdapter(@NonNull Context context, int resource, @NonNull List<Comment> objects) {
        super(context, resource, objects);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mContext = context;
        layoutResource = resource;
    }

    private static class ViewHolder {
        TextView comment, username, timeStamp, reply, likes;
        CircleImageView profileImage;
        ImageView like;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final ViewHolder holder;
        if (convertView==null) {
            convertView = mInflater.inflate(layoutResource, parent,false);
            holder = new ViewHolder();
            holder.comment = convertView.findViewById(R.id.comment);
            holder.username = convertView.findViewById(R.id.comment_username);
            holder.timeStamp = convertView.findViewById(R.id.comment_time_posted);
            holder.reply = convertView.findViewById(R.id.comment_reply);
            holder.likes = convertView.findViewById(R.id.comment_likes);
            holder.profileImage = convertView.findViewById(R.id.comment_profile_photo);
            holder.like = convertView.findViewById(R.id.comment_like);

            convertView.setTag(holder); //store view in memory

        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        //set the comment
        holder.comment.setText(getItem(position).getComment());

        //set the timestamp difference
        String timeStampDifference = getTimeStampDifference(getItem(position));
        if (! timeStampDifference.equals("0")) {
            holder.timeStamp.setText(timeStampDifference + " d");
        } else {
            holder.timeStamp.setText("today");
        }

        //set the  username and profile image
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        db = FirebaseFirestore.getInstance();
            CollectionReference userAccountSettingRef = db.collection(mContext.getString(R.string.dbname_user_account_settings));
            DocumentReference document = userAccountSettingRef.document(getItem(position).getUser_id());
            document.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    mProfileUrl = documentSnapshot.get("profile_photo").toString();
                    mUsername = documentSnapshot.get("username").toString();
                    holder.username.setText(mUsername);
                    UniversalImageLoader.setImage(mProfileUrl, holder.profileImage,null, "");
                }
            });





        return convertView;
    }

    private String getTimeStampDifference(Comment comment) {
        String difference = " ";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("US/Pacific"));
        Calendar c = Calendar.getInstance();
        Date today = c.getTime();
        sdf.format(today);
        Date timeStamp;
        String photoTimeStamp = comment.getDate_created();
        try {
            timeStamp = sdf.parse(photoTimeStamp);
            difference = String.valueOf(Math.round(((today.getTime() - timeStamp.getTime())/1000/60/60/24)));

        } catch (ParseException e) {
            Log.e(TAG, "getTimeStampDifference: ParseException "+ e.getMessage());
            difference = "0";
        }
        return difference;
    }
}
