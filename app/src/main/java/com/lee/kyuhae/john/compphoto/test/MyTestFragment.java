package com.lee.kyuhae.john.compphoto.test;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lee.kyuhae.john.compphoto.R;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by john.lee on 7/19/16.
 */

public class MyTestFragment extends Fragment {
    private static final String TAG = "MyTestFragment";
    private View view;
    private Context mContext;
    private final AtomicInteger imageViewId = new AtomicInteger(0);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.photodisplay_fragment_layout, container, false);
        mContext = view.getContext();

        Mat img1 = loadImageFromResource(R.raw.cathedral_001);
        if (img1 == null) {
            Log.d(TAG, "image not found!");
            return view;
        }
        double[] channels = img1.get(0,0);
        for (int i = 0; i < channels.length; i++) {
            Log.d(TAG, "sample image pixel(0,0) " + i + " : " + channels[i]);
        }
        addImage(img1);
        addText("first image");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private Mat loadImageFromResource(int resourceId) {
        Mat mat;
        try {
            mat = Utils.loadResource(mContext, resourceId);
        } catch (Exception e) {
            Log.e(TAG, "Error while reading resource.", e);
            return null;
        }
        return mat;
    }

    private void addImage(Mat mat) {
        Bitmap bitmap = Bitmap
                .createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);

        // Add the image to the layout.
        int curId = imageViewId.getAndIncrement();
        RelativeLayout imageContainer = (RelativeLayout) view.findViewById(R.id.image_container);
        final ImageView imageView = new ImageView(getContext());
        imageView.setId(curId);
        imageView.setImageBitmap(bitmap);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        if (curId > 0) {
            params.addRule(RelativeLayout.BELOW, curId - 1);
        }

        imageContainer.addView(imageView, params);
    }

    private void addText(String text) {
        RelativeLayout imageContainer = (RelativeLayout) view.findViewById(R.id.image_container);
        int curId = imageViewId.getAndIncrement();
        final TextView textView = new TextView(getContext());
        textView.setText(text);
        textView.setId(curId);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        if (curId > 0) {
            params.addRule(RelativeLayout.BELOW, curId - 1);
        }

        imageContainer.addView(textView, params);
    }

}
