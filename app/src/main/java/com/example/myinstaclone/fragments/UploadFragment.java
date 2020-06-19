package com.example.myinstaclone.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.myinstaclone.R;
import com.example.myinstaclone.StorageListener;
import com.example.myinstaclone.StorageManager;
import com.example.myinstaclone.viewmodels.UploadViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;


public class UploadFragment extends Fragment implements StorageListener, View.OnClickListener {
    private static final String TAG = "UploadFragment";

    ProgressBar progressBar;
    String source;
    ImageView imageView;

    Bitmap bitmap;
    private UploadViewModel mViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_upload, container, false);
        imageView = root.findViewById(R.id.imagePreview);

        FloatingActionButton fab = root.findViewById(R.id.fab);
        fab.setOnClickListener(this);
        progressBar = root.findViewById(R.id.progressBar);
        progressBar.setVisibility(ProgressBar.INVISIBLE);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.showPreview();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(UploadViewModel.class);
        // TODO: Use the ViewModel
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        StorageManager.addListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        StorageManager.removeListener(this);
    }


    private void showPreview() {
        Bundle args = this.getArguments();
        String path = args.getString("imagePath");

        Log.i(TAG, "showPreview with uri:" + path);

        Uri imageUri = Uri.parse(path);
        try {
            /*if (Build.VERSION.SDK_INT < 28) {
                this.bitmap = MediaStore.Images.Media.getBitmap(
                        getActivity().getContentResolver(),
                        imageUri
                );
                imageView.setImageBitmap(this.bitmap);

            } else {
                ImageDecoder.Source source = ImageDecoder.createSource(getActivity().getContentResolver(), imageUri);
                this.bitmap = ImageDecoder.decodeBitmap(source);
                imageView.setImageBitmap(this.bitmap);
            }
*/
            Glide.with(getContext())
                    .load(new File(imageUri.getPath())) // Uri of the picture
                    .disallowHardwareConfig()
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUploadSuccess(Uri downloadUri) {
        StorageManager.removeListener(this);
        this.popToHome();
    }

    @Override
    public void onUploadProgress(Double progress) {
        progressBar.setProgress(progress.intValue());
    }

    @Override
    public void onUploadFailed() {
        Toast.makeText(getContext(), "Upload photo failed", Toast.LENGTH_SHORT);
    }

    private void popToHome() {
        View root = getView();
        Navigation.findNavController(root).navigate(R.id.action_uploadFragment_to_homeFragment);
    }

    @Override
    public void onClick(View v) {
        try {
            // Get the data from an ImageView as bytes
            imageView.setDrawingCacheEnabled(true);
            imageView.buildDrawingCache();
            imageView.setDrawingCacheEnabled(false);
            this.bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        StorageManager.getInstance().uploadPhoto(this.bitmap);
        progressBar.setVisibility(v.VISIBLE);
    }


}
