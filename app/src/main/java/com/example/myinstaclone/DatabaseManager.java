package com.example.myinstaclone;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

interface DatabaseListener {

    static enum DataType {USERS, FEEDS}

    void onQueryUsersResult(List<UserModel> result);

    void onQueryFeedsResult(List<FeedModel> result);
}

public class DatabaseManager {
    private static final String TAG = "DatabaseManager";

    private static final DatabaseManager ourInstance = new DatabaseManager();

    public static DatabaseManager getInstance() {
        return ourInstance;
    }

    private FirebaseFirestore db;
    private ListenerRegistration registration;

    private static final List<DatabaseListener> _listeners = new CopyOnWriteArrayList<DatabaseListener>();


    public static void addListener(final DatabaseListener listener) {
        if (null != listener) {
            _listeners.add(listener);
        }
    }

    public static void removeListener(final DatabaseListener listener) {
        if (null != listener) {
            _listeners.remove(listener);
        }
    }

    /**
     * Called when there is a network change.
     */
    private static void notifyUserResult(List<UserModel> result) {
        for (DatabaseListener listener : _listeners) {
            listener.onQueryUsersResult(result);
        }
    }

    private static void notifyFeedResult(List<FeedModel> result) {
        for (DatabaseListener listener : _listeners) {
            listener.onQueryFeedsResult(result);
        }
    }

    private DatabaseManager() {
        db = FirebaseFirestore.getInstance();

    }

    public void subscribe(){
        registration = db.collection("feeds")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {

                        Log.d(TAG, "on feed event");
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        for (DocumentChange dc : value.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:
                                    Log.d(TAG, "New feed: " + dc.getDocument().getData());
                                    break;
                                case MODIFIED:
                                    Log.d(TAG, "Modified feed: " + dc.getDocument().getData());
                                    break;
                                case REMOVED:
                                    Log.d(TAG, "Removed feed: " + dc.getDocument().getData());
                                    break;
                            }
                        }



                        List<FeedModel> feedsList = new ArrayList<FeedModel>();
                        for (DocumentSnapshot document : value) {
                            if (document != null) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                FeedModel feed = document.toObject(FeedModel.class);
                                feedsList.add(feed);

                            }
                        }


                        notifyFeedResult(feedsList);


                    }
                });
    }

    public void unsubscribe(){
        if(registration != null){
            registration.remove();
        }
    }

    public void createUser(final String userId, String first, String last, String imageName) {
        Map<String, Object> user = new HashMap<>();
        user.put("fist", first);
        user.put("last", last);
        user.put("imageName", imageName);

        Log.i(TAG, "start create user, id: " + userId);
        db.collection("users")
                .document(userId).set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "user successfully added id: " + userId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding user", e);
                    }
                });
    }

    public void createFeed(String userId, Date date, String description, String imageName, String location) {
        Map<String, Object> feed = new HashMap<>();
        feed.put("author", userId);
        feed.put("date", date);
        feed.put("description", description);
        feed.put("imageName", imageName);
        feed.put("location", location);

        Log.i(TAG, "start create feed, author: " + userId);
        db.collection("feeds")
                .add(feed)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "Feed DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    public void getFeeds() {
        Log.i(TAG, "start request feeds");
        try {
            db.collection("feeds")
                    .orderBy("date", Query.Direction.DESCENDING)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            Log.d(TAG, "on feeds request Complete");

                            if (task.isSuccessful()) {
                                List<FeedModel> feedsList = new ArrayList<FeedModel>();
                                for (DocumentSnapshot document : task.getResult()) {
                                    if (document != null) {
                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                        FeedModel feed = document.toObject(FeedModel.class);
                                        feedsList.add(feed);

                                    }
                                }
                                // notifyFeedResult(feedsList);
                            } else {
                                Log.w(TAG, "Error getting documents.", task.getException());
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void getUserById(String userId) {
        Log.i(TAG, "start request user by id: " + userId);


        DocumentReference userRef = db.collection("users")
                .document(userId);

        userRef.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            List<UserModel> users = new ArrayList<UserModel>();
                            if (document.exists()) {
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                UserModel user = document.toObject(UserModel.class);
                                user.setId(document.getId());
                                users.add(user);
                            } else {
                                Log.d(TAG, "No such document");
                            }
                            notifyUserResult(users);
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });

    }

    public void updateUserInfos(final String userId, Map<String, Object> updateInfos){
        Log.d(TAG, "upadate user infos: " + userId);

        if(updateInfos.containsKey("imageName")){

        }
        db.collection("users")
                .document(userId).set(updateInfos)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "user successfully updated id: " + userId);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error update user", e);
                    }
                });
    }
}
