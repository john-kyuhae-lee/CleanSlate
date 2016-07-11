package com.lee.kyuhae.john.compphoto;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment
        implements CameraBridgeViewBase.CvCameraViewListener2 {
    private static final String TAG = "MainActivityFragment";
    // private CameraBridgeViewBase mOpenCvCameraView;
    private View view;
    private Context mContext;

    static {
        // If you use opencv 2.4, System.loadLibrary("opencv_java")
        System.loadLibrary("opencv_java3");
    }

    public MainActivityFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_main, container, false);
        mContext = view.getContext();
//        mOpenCvCameraView = (CameraBridgeViewBase) view.findViewById(R.id.HelloOpenCvView);
//        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
//        mOpenCvCameraView.setCvCameraViewListener(this);

        BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(mContext) {
            @Override
            public void onManagerConnected(int status) {
                switch (status) {
                    case LoaderCallbackInterface.SUCCESS: {
                        Log.i(TAG, "OpenCV loaded successfully");
                        // mOpenCvCameraView.enableView();
                    }
                    break;
                    default: {
                        super.onManagerConnected(status);
                    }
                    break;
                }
            }
        };

        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, view.getContext(), mLoaderCallback);

        Mat img1 = loadImage(R.raw.cathedral_001);
        Mat img2 = loadImage(R.raw.cathedral_002);

        displayImage(img1, R.id.image_view1);
        displayImage(img2, R.id.image_view2);

        assert img1 != null;
        Mat subtractedMat = new Mat(img1.rows(), img1.cols(), img1.type());
        Core.subtract(img1, img2, subtractedMat);
        displayImage(subtractedMat, R.id.image_view3);
        return view;
    }

    private Mat loadImage(int resourceId) {
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

    @Override
    public void onPause() {
        super.onPause();
//        if (mOpenCvCameraView != null)
//            mOpenCvCameraView.disableView();
    }

    public void onDestroy() {
        super.onDestroy();
//        if (mOpenCvCameraView != null)
//            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        return inputFrame.rgba();
    }
}
