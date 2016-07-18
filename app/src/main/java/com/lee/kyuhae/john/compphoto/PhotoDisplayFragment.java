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

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

/**
 * A placeholder fragment containing a simple view.
 */
public class PhotoDisplayFragment extends Fragment {
    private static final String TAG = "PhotoDisplayFragment";
    private View view;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.photodisplay_fragment_layout, container, false);
        mContext = view.getContext();

        // Mat img1 = loadImageFromResource(R.raw.cathedral_001);
        // displayImage(img1, R.id.image_view1);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        String filePath = (String) getArguments().get(Constants.PHOTO_PATH_ARG);
        Mat imgMat = Imgcodecs.imread(filePath);
        displayImage(imgMat, R.id.image_view1);
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

    private void displayImage(Mat mat, int viewId) {
        Bitmap bitmap = Bitmap
                .createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(mat, bitmap);
        final ImageView view = (ImageView) this.view.findViewById(viewId);
        view.setImageBitmap(bitmap);
    }
}
