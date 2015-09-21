package com.kevin_yang.fuzz_quiz;

import android.app.DialogFragment;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by kevinyang on 9/18/15.
 */
public class ImageFragment extends DialogFragment {

    public static final String EXTRA_IMAGE_DECODED =
            "com.kevin_yang.fuzz_quiz.image_path";
    private ImageView mImageView;

    public static ImageFragment newInstance(byte[] img) {
        Bundle args = new Bundle();
        args.putSerializable(EXTRA_IMAGE_DECODED, img);
        ImageFragment fragment = new ImageFragment();
        fragment.setArguments(args);
        fragment.setStyle(DialogFragment.STYLE_NO_TITLE, 0);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup parent, Bundle savedInstanceState) {
        // Decode, resize the image passed down from FuzzQuizFragment
        byte[] decodedImage = (byte[]) getArguments().getSerializable(EXTRA_IMAGE_DECODED);
        BitmapDrawable image = PictureUtils.getFullScaledDrawable(getActivity(), decodedImage);
        // Set the image to the ImageView in the layout
        View v = inflater.inflate(R.layout.fragment_image, parent, false);
        mImageView = (ImageView) v.findViewById(R.id.item_image);
        mImageView.setImageDrawable(image);
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        PictureUtils.cleanImageView(mImageView);
    }
}

