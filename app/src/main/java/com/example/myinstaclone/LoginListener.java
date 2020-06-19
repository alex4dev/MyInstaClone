package com.example.myinstaclone;

import com.google.firebase.auth.FirebaseUser;

public interface LoginListener {
    void onLoginSuccess(FirebaseUser result);
    void onLoginFailed();
}
