package com.kevin_yang.fuzz_quiz;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;
import android.widget.ImageView;

public class PictureUtils {

    /*GET source image in full size*/
    public static BitmapDrawable getFullScaledDrawable(Activity a, byte[] decodedImg) {
        // get Screen size
        Display display = a.getWindowManager().getDefaultDisplay();
        float destWidth = display.getWidth();
        float destHeight = display.getHeight();

        Bitmap src = BitmapFactory.decodeByteArray(decodedImg, 0, decodedImg.length);
        float srcWidth = src.getWidth();
        float srcHeight = src.getHeight();

        int inSampleSize = 1;
        if (srcWidth > srcHeight) {
            inSampleSize = Math.round(destHeight / srcHeight);
        } else {
            inSampleSize = Math.round(destWidth / srcWidth);
        }

        src = Bitmap.createScaledBitmap(src, (int) (srcWidth*inSampleSize), (int) (srcHeight*inSampleSize), true);
        return new BitmapDrawable(a.getResources(), src);
    }


    public static void cleanImageView(ImageView imageView) {
        if (!(imageView.getDrawable() instanceof BitmapDrawable))
            return;

        // Clean up the view's image for the sake of memory
        BitmapDrawable b = (BitmapDrawable)imageView.getDrawable();
        b.getBitmap().recycle();
        imageView.setImageDrawable(null);
    }
}