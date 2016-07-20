package com.lee.kyuhae.john.compphoto;

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

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A placeholder fragment containing a simple view.
 */
public class PhotoDisplayFragment extends Fragment {
    private static final String TAG = "PhotoDisplayFragment";
    private View view;
    private Context mContext;
    private final AtomicInteger imageViewId = new AtomicInteger(0);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.photodisplay_fragment_layout, container, false);
        mContext = view.getContext();

        // Mat img1 = loadImageFromResource(R.raw.cathedral_001);
        // addImage(img1, R.id.image_view1);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        ArrayList<String> pictures = getArguments().getStringArrayList(Constants.PHOTO_PATH_ARG);

        if (pictures == null) {
            Log.d(TAG, "Received an empty array for pictures.");
            return;
        }

        Log.d(TAG, "Received " + pictures.size() + " images :[" + pictures.toString() + "]");
        addText("Images...");
        for (int i = 0; i < pictures.size(); i++) {
            Mat imgMat = Imgcodecs.imread(pictures.get(i));
            addImage(imgMat);
            addText("image " + i);
        }
    }

    private Mat loadImageFromResource(int resourceId) {
        Mat mat;
        try {
            mat = Utils.loadResource(mContext, resourceId, 0);
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
