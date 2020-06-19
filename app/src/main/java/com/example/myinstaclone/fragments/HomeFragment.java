package com.example.myinstaclone.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myinstaclone.DatabaseListener;
import com.example.myinstaclone.DatabaseManager;
import com.example.myinstaclone.FeedModel;
import com.example.myinstaclone.FeedViewAdapter;
import com.example.myinstaclone.R;
import com.example.myinstaclone.UserModel;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class HomeFragment extends Fragment implements DatabaseListener {
    private static final String TAG = "HomeFragment";

    private static final int REQUEST_GALLERY_IMAGE = 1;


    private RecyclerView.Adapter mAdapter;

    private ArrayList<FeedModel> feedsList = new ArrayList<FeedModel>();


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public HomeFragment() {
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(TAG, "onCreateView");

        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Set the adapter
        Context context = view.getContext();
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.feedsRecyclerView);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mAdapter = new FeedViewAdapter(context, feedsList);
        recyclerView.setAdapter(mAdapter);
        ((FeedViewAdapter) mAdapter).setClickListener(new FeedViewAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {


            }
        });

        FloatingActionMenu menu = (FloatingActionMenu) view.findViewById(R.id.menuButtons);
        FloatingActionButton cameraButton = (FloatingActionButton) menu.findViewById(R.id.cameraButton);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "camera button clicked");
                navigateToCamera();
            }
        });
        FloatingActionButton pickerButton = (FloatingActionButton) menu.findViewById(R.id.pickerButton);
        pickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "picker button clicked");
                showPicker();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_GALLERY_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            navigateToUpload(selectedImageUri.toString());
        }

    }

    @Override
    public void onStart() {
        Log.i(TAG, "onStart");
        super.onStart();
        DatabaseManager.addListener(this);
        DatabaseManager.getInstance().getFeeds();
    }

    @Override
    public void onStop() {
        Log.i(TAG, "onStop");
        super.onStop();
        DatabaseManager.removeListener(this);
    }

    @Override
    public void onQueryUsersResult(List<UserModel> result) {

    }

    @Override
    public void onQueryFeedsResult(List<FeedModel> result) {
        Log.d(TAG, "onQueryFeedsResult");

        if (result != null && result.size() > 0) {
            Log.d(TAG, "add result size: " + result.size());
            feedsList.clear();
            feedsList.addAll(0, result);
            Collections.sort(feedsList, new Comparator<FeedModel>() {
                @Override
                public int compare(FeedModel r1, FeedModel r2) {
                    return r2.getDate().compareTo(r1.getDate());
                }
            });
            mAdapter.notifyDataSetChanged();
        }
    }

    private void showPicker() {
        Log.i(TAG, "showPicker");
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        this.startActivityForResult(intent, REQUEST_GALLERY_IMAGE);
    }

    private void navigateToCamera() {
        View root = getView();
        Navigation.findNavController(root).navigate(R.id.action_homeFragment_to_cameraSurfaceFragment);
    }

    private void navigateToUpload(String arg) {
        View root = getView();
        Navigation.findNavController(root).navigate(CameraFragmentDirections.actionGlobalUploadFragment(arg));
    }

}
