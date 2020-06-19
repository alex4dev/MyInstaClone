package com.example.myinstaclone.fragments;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myinstaclone.LoginManager;
import com.example.myinstaclone.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class StartupFragment extends Fragment {
    private static final String TAG = "StartupFragment";


    public StartupFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_startup, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

               if(LoginManager.getInstance().getCurrentUser() != null){
                   Log.i(TAG, "User already connected - navigate to home");
                   Navigation.findNavController(view).navigate(R.id.action_global_homeFragment);
               }else{
                   Log.i(TAG, "User not connected - navigate to login");
                   Navigation.findNavController(view).navigate(R.id.action_startupFragment_to_loginFragment);
               }
    }
}
