package com.example.myinstaclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.security.Key;

public class MainActivity extends AppCompatActivity implements KeyListener {

    EditText emailEdit;
    EditText passwordEdit;
    Button loginButton;
    Button signupButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ApplicationContext.getInstance().init(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(LoginManager.getInstance().getCurrentUser() != null){
            Intent intent = new Intent(this, FeedsActivity.class);
            startActivity(intent);
        }


        emailEdit = findViewById(R.id.emailEdit);
        passwordEdit = findViewById(R.id.passwordEdit);
        loginButton = findViewById(R.id.loginButton);
        signupButton = findViewById(R.id.signUpButton);


        LoginManager.addListener(new LoginListener() {
            @Override
            public void onLoginSuccess(FirebaseUser result) {
                Intent intent = new Intent(ApplicationContext.get(), FeedsActivity.class);
                startActivity(intent);
            }

            @Override
            public void onLoginFailed() {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Authentication failed, invalid email or password",
                                Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        // emailEdit.setKeyListener((KeyListener) this);
        passwordEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    // do your stuff here
                    Log.d(MainActivity.class.toString(), "onEditorAction: Done");
                    LoginManager.getInstance().doLogin(emailEdit.getText().toString(), passwordEdit.getText().toString());
                }
                return false;
            }
        });
    }

    @Override
    public int getInputType() {
        return 0;
    }

    @Override
    public boolean onKeyDown(View view, Editable text, int keyCode, KeyEvent event) {

        Log.d(this.getLocalClassName(), "onKeyDown: "+ String.valueOf(keyCode));
        // If the event is a key-down event on the "enter" button
        if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                (keyCode == KeyEvent.KEYCODE_ENTER)) {

            // Perform action on key press
            Toast.makeText(this, emailEdit.getText(), Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;

    }

    @Override
    public boolean onKeyUp(View view, Editable text, int keyCode, KeyEvent event) {
        return false;
    }

    @Override
    public boolean onKeyOther(View view, Editable text, KeyEvent event) {
        return false;
    }

    @Override
    public void clearMetaKeyState(View view, Editable content, int states) {

    }

    public void loginButton_triggered(View view){
        LoginManager.getInstance().doLogin(emailEdit.getText().toString(), passwordEdit.getText().toString());
    }

    public void signupButton_triggered(View view){
        LoginManager.getInstance().doSignUp(emailEdit.getText().toString(), passwordEdit.getText().toString());
    }






}
