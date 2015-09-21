package com.kevin_yang.fuzz_quiz;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

/**
 * Created by kevinyang on 9/17/15.
 */
public class Item {
    private String mId;
    private String mType;
    private String mDate;
    private String mData;
    private Bitmap mImage;

    public Item (String id, String type, String date, String data) {
        mId = id;
        mType = type;
        mDate = date;
        mData = data;
    }

    public String getType() {
        return mType;
    }

    public String getDate() {
        return mDate;
    }

    public String getId() {
        return mId;
    }

    public String getData() {
        return mData;
    }

    public void setImage(Bitmap image) {
        mImage = image;
    }

    public Bitmap getImage() {
        return mImage;
    }

}
