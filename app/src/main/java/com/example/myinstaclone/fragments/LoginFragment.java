package com.example.myinstaclone.fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.method.KeyListener;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myinstaclone.ApplicationContext;
import com.example.myinstaclone.LoginListener;
import com.example.myinstaclone.LoginManager;
import com.example.myinstaclone.R;
import com.example.myinstaclone.activities.MainActivity;
import com.google.firebase.auth.FirebaseUser;

/**
 * A simple {@link Fragment} subclass.
 */
public class LoginFragment extends Fragment implements KeyListener, View.OnClickListener {

    EditText emailEdit;
    EditText passwordEdit;
    Button loginButton;
    Button signupButton;

    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View root = inflater.inflate(R.layout.fragment_login, container, false);

        ScrollView scrollView = (ScrollView) root.findViewById(R.id.loginScrollView);
        ViewGroup mainContainer = scrollView.findViewById(R.id.loginMainContainer);
        ViewGroup subContainer = mainContainer.findViewById(R.id.loginSubContainer);

        // Inflate the layout for this fragment
        emailEdit = subContainer.findViewById(R.id.emailEdit);
        passwordEdit = subContainer.findViewById(R.id.passwordEdit);
        loginButton = subContainer.findViewById(R.id.loginButton);
        signupButton = subContainer.findViewById(R.id.signUpButton);


        loginButton.setOnClickListener(this);
        signupButton.setOnClickListener(this);
        LoginManager.addListener(new LoginListener() {
            @Override
            public void onLoginSuccess(FirebaseUser result) {
                // Intent intent = new Intent(ApplicationContext.get(), FeedsActivity.class);
                // startActivity(intent);
                Navigation.findNavController(root).navigate(R.id.action_global_homeFragment);

            }

            @Override
            public void onLoginFailed() {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ApplicationContext.get(), "Authentication failed, invalid email or password",
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

        return root;
    }

    @Override
    public int getInputType() {
        return 0;
    }

    @Override
    public boolean onKeyDown(View view, Editable text, int keyCode, KeyEvent event) {
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

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.loginButton){
            LoginManager.getInstance().doLogin(emailEdit.getText().toString(), passwordEdit.getText().toString());
        }else{
            LoginManager.getInstance().doSignUp(emailEdit.getText().toString(), passwordEdit.getText().toString());
        }

    }
}
