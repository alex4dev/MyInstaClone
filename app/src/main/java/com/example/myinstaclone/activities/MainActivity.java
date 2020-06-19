package com.example.myinstaclone.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myinstaclone.ApplicationContext;
import com.example.myinstaclone.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ApplicationContext.getInstance().init(getApplicationContext());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }



}
