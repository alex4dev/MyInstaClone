package com.example.myinstaclone;


import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

interface StorageListener {
    void onUploadSuccess(Uri downloadUri);
    void onUploadProgress(Double progress);
    void onUploadFailed();
}

public class StorageManager extends ThreadPoolExecutor {
    private static final String TAG = "StorageManager";

    private static final StorageManager ourInstance = new StorageManager();

    public static StorageManager getInstance() {
        return ourInstance;
    }

    private FirebaseStorage storage;

    private StorageManager() {
        super(5, 5,
                50, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        storage = FirebaseStorage.getInstance();
    }


    private static List<StorageListener> _listeners = new CopyOnWriteArrayList<StorageListener>();

    static void addListener(final StorageListener listener) {
        if (null != listener) {
            _listeners.add(listener);
        }
    }

    static void removeListener(final StorageListener listener) {
        if (null != listener) {
            _listeners.remove(listener);
        }
    }

    /**
     * Called when a network connection is available.
     */
    static void notifyUploadSuccess(Uri downloadUri) {
        for (StorageListener listener : _listeners) {
            listener.onUploadSuccess(downloadUri);
        }
    }

    /**
     * Called when event progress trigger
     */
    static void notifyUploadProgress(Double progress) {
        for (StorageListener listener : _listeners) {
            listener.onUploadProgress(progress);
        }
    }

    /**
     * Called when a network connection becomes unavailable.
     */
    static void notifyUploadFailed() {
        for (StorageListener listener : _listeners) {
            listener.onUploadFailed();
        }
    }

    private String generateId() {
        Date currentTime = Calendar.getInstance().getTime();
        String userId = LoginManager.getInstance().getCurrentUser().getUid();
        return userId + "_" + currentTime.toString().replaceAll("\\s", "");
    }

    public void uploadPhoto(Bitmap photoBitmap) {
        if (photoBitmap == null) {
            return;
        }

        Log.i(TAG, "start upload photo");

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();

        // Create a reference to 'images/*'
        StorageReference photoRef = storageRef.child("places/" + generateId());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photoBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = photoRef.putBytes(data);
        uploadTask
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        Log.d(TAG, "Upload is " + progress + "% done");
                        notifyUploadProgress(progress);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        Log.e(TAG, "upload photo failed: " + exception.getMessage());
                        notifyUploadFailed();
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Log.i(TAG, "upload photo success");
                        Log.d(TAG, "uri: " + taskSnapshot.getUploadSessionUri().toString());
                        Log.d(TAG, "uri path: " + taskSnapshot.getUploadSessionUri().getPath());
                        Log.d(TAG, "uri param name: " + taskSnapshot.getUploadSessionUri().getQueryParameter("name"));
                        final String imageName = taskSnapshot.getUploadSessionUri().getQueryParameter("name");


                        String localuserId = LoginManager.getInstance().getCurrentUser().getUid();

                        DatabaseManager.getInstance().createFeed(localuserId, Calendar.getInstance().getTime(), "", imageName, "ici");

                        notifyUploadSuccess(taskSnapshot.getUploadSessionUri());
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                        // ...
                    }
        });
    }


}