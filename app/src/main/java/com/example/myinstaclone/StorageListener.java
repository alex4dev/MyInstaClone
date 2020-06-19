package com.example.myinstaclone;

import android.net.Uri;

public interface StorageListener {
    void onUploadSuccess(Uri downloadUri);
    void onUploadProgress(Double progress);
    void onUploadFailed();
}
