package com.example.myinstaclone;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

interface LoginListener {
    void onLoginSuccess(FirebaseUser result);
    void onLoginFailed();
}

public class LoginManager extends ThreadPoolExecutor {
    private static final String TAG = "LoginManager";

    private static final LoginManager ourInstance = new LoginManager();

    public static LoginManager getInstance() {
        return ourInstance;
    }

    private FirebaseAuth mAuth;

    private UserInfo currentUser;

    private LoginManager() {
        super(5, 5,
                50, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
    }


    private static List<LoginListener> _listeners = new CopyOnWriteArrayList<LoginListener>();

    static void addListener(final LoginListener listener) {
        if (null != listener) {
            _listeners.add(listener);
        }
    }

    static void removeListener(final LoginListener listener) {
        if (null != listener) {
            _listeners.remove(listener);
        }
    }

    /**
     * Called when a network connection is available.
     */
    static void notifyLoginSuccess(FirebaseUser user) {
        for (LoginListener listener : _listeners) {
            listener.onLoginSuccess(user);
        }
    }

    /**
     * Called when a network connection becomes unavailable.
     */
    static void notifyLoginFailed() {
        for (LoginListener listener : _listeners) {
            listener.onLoginFailed();
        }
    }




    public UserInfo getCurrentUser() {
        return currentUser;
    }

    public void doLogin(String email, String password) {
        Log.i(TAG, "Start login - email:" + email + " password: "+ password);
        if (email == null || email == "" || password == null || password == "") {
            Log.i(MainActivity.class.toString(), "sign up failed");
            notifyLoginFailed();
            return;
        }

        final FirebaseUser currentFBUser = mAuth.getCurrentUser();
        if (currentFBUser != null) {
            Log.w(TAG, "user already connected " + currentUser.getEmail());
            notifyLoginFailed();
            return;
        }

        if(mAuth == null){
            Log.w(TAG, "login failed - maAuth is null");
            return;
        }

        try{
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                Log.d(TAG, "signInWithEmail:success email:" + user.getEmail());
                                currentUser = user;
                                notifyLoginSuccess(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                notifyLoginFailed();                            // ...
                            }

                            // ...
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void doSignUp(String email, String password) {
        Log.i(MainActivity.class.toString(), "Start sign up");
        if (email == null || email == "" || password == null || password == "") {
            Log.i(MainActivity.class.toString(), "sign up failed");
            notifyLoginFailed();
            return;
        }


        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(this.getClass().toString(), "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            currentUser = user;
                            // ask database create user
                            DatabaseManager.getInstance().createUser(user.getUid(), StringUtils.splitEmail(user.getEmail())[0],"", "https://robohash.org/" + user.getUid());
                            notifyLoginSuccess(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(this.getClass().toString(), "createUserWithEmail:failure", task.getException());
                            Toast.makeText(ApplicationContext.get(), "Authentication failed - account already exist",
                                    Toast.LENGTH_SHORT).show();
                            notifyLoginFailed();
                        }

                        // ...
                    }
                });
    }


}
