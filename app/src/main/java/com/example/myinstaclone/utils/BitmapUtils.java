package com.example.myinstaclone.utils;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.PixelCopy;
import android.view.Window;
import android.widget.ImageView;

public class BitmapUtils {

  /*  public static Bitmap captureView(ImageView view, Window window, BitmapDrawListener bitmapCallback) {
        Bitmap dbitmap = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Above Android O, use PixelCopy
            dbitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            int[] location = new int[2];
            view.getLocationInWindow(location);
            PixelCopy.request(window,
                    new Rect(location[0], location[1], location[0] + view.getWidth(), location[1] + view.getHeight()),
                    dbitmap,
                    (int result) -> {
                        if (result == PixelCopy.SUCCESS) {
                            bitmapCallback.onBitmapReady(dbitmap);
                        }
                    }, bitmapCallback);
        } else {
            try {
                // Get the data from an ImageView as bytes
                view.setDrawingCacheEnabled(true);
                view.buildDrawingCache();
                view.setDrawingCacheEnabled(false);
                dbitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        bitmapCallback.onBitmapReady(dbitmap);
    }*/
}
