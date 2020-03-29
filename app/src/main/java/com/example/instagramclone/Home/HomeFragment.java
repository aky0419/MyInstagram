package com.example.instagramclone.Home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.instagramclone.Models.Photo;
import com.example.instagramclone.R;
import com.example.instagramclone.Utils.MainfeedListAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

public class HomeFragment extends Fragment {
    private static final String TAG= "HomeFragment";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    ListView mListView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home,container,false);

        mListView = view.findViewById(R.id.listView);
       setupFirebaseAuth();



        return view;

    }

    //------------------------------------Firebase----------------------------------------------


    //Set up firebase authentication object
    public void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        CollectionReference followRef = db.collection(getString(R.string.dbname_follow));
        final Query query = followRef.whereEqualTo(getString(R.string.field_follower), mAuth.getUid());
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<String> ids = new ArrayList<>();
                ids.add(mAuth.getCurrentUser().getUid());
                for (DocumentSnapshot d : queryDocumentSnapshots.getDocuments()) {
                    ids.add(d.get(getString(R.string.field_following)).toString());
                }

                CollectionReference photoRef = db.collection(getString(R.string.dbname_photos));
                HashSet<String> uniqueIds = new HashSet<>(ids);
                Query query1 = photoRef.whereIn(getString(R.string.field_user_id), Arrays.asList(uniqueIds.toArray()));
                // TODO: change date_created to timestamp query1.orderBy("timestamp");
                query1.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<Photo> photos = queryDocumentSnapshots.toObjects(Photo.class);
                        MainfeedListAdapter adapter = new MainfeedListAdapter(getActivity(), R.layout.layout_mainfeed_listview, photos);
                        mListView.setAdapter(adapter);

                    }
                });
            }

        });


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //FirebaseUser currentUser = mAuth.getCurrentUser();
      //  setupFirebaseAuth();
        // check if user is logged in
        // checkCurrentUser(currentUser);

//        if (currentUser == null) {
//            mAuth.signInWithEmailAndPassword("kenchan52016@yahoo.com","123456")
//                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                        @Override
//                        public void onComplete(@NonNull Task<AuthResult> task) {
//                            if (task.isSuccessful()) {
//                                // Sign in success, update UI with the signed-in user's information
//                                Log.d(TAG, "createUserWithEmail:success");
//
//                            } else {
//                                // If sign in fails, display a message to the user.
//                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
//
//
//                            }
//                        }
//                    });
//
//
//        }

    }
}
