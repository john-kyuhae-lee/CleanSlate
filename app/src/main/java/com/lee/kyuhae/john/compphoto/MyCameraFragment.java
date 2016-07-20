package com.lee.kyuhae.john.compphoto;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lee.kyuhae.john.compphoto.camera2basic.AutoFitTextureView;
import com.lee.kyuhae.john.compphoto.camera2basic.Camera2BasicFragment;
import com.lee.kyuhae.john.compphoto.event.CaptureCompletionEvent;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * Created by john.lee on 7/16/16.
 */

public class MyCameraFragment extends Camera2BasicFragment {
    private static final String TAG = "MyCameraFragment";

    private static final String FILE_NAME_APPENDER = "MyPic_";

    // For the purpose of creating clean slate, or background reconstruction,
    // A multiple pictures between each shots are required.
    private static final int NUM_PICTURES_REQUIRED = 5;

    // This could be a user input.
    private static final int WAIT_TIME_BETWEEN_SHOTS_SECS = 1;

    // Waiting for the current capture progress to be over.
    private static final int WAIT_TIME_DEFAULT_MILLIS = 100;

    // We need to make sure that we do not request a capture while
    // there is already one in progress.
    private static final int IDLE = 0;
    private static final int CAPTURE_IN_PROGRESS = 1;
    private final AtomicInteger currentStatus = new AtomicInteger(IDLE);

    // Stores paths for completed pictures.
    private ArrayList<String> completedPictures;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        return inflater.inflate(R.layout.mycamera_fragment, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mTextureView = (AutoFitTextureView) view.findViewById(R.id.texture);
    }

    @Override
    protected void notifyCaptureCompletion() {
        Log.d(TAG, "Received a notification for a capture completion. " +
                "Unlocking the cameraResourceLock.");
        currentStatus.set(IDLE);
        completedPictures.add(mFile.getPath());
    }

    /**
     * Triggered when a user clicks on 'Take Picture' button.
     */
    @Override
    protected void takePicture() {
        completedPictures = new ArrayList<>();
        long waitTimeMillis = TimeUnit.SECONDS.toMillis(WAIT_TIME_BETWEEN_SHOTS_SECS);
        for (int i = 0; i < NUM_PICTURES_REQUIRED; i++) {
            // If it is not the first shot, then sleep for a wait time.
            if (i != 0 && waitTimeMillis != 0) {
                Log.d(TAG, "A user requested a wait time between a shot for " + waitTimeMillis);
                try {
                    Thread.sleep(waitTimeMillis);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Interrupted while waiting for the next shot.", e);
                }
            }

            Log.d(TAG, "Trying a lock in order to request a capture.");
            while (!currentStatus.compareAndSet(IDLE, CAPTURE_IN_PROGRESS)) {
                try {
                    Thread.sleep(WAIT_TIME_DEFAULT_MILLIS);
                } catch (InterruptedException e) {
                    Log.e(TAG, "Interrupted while waiting for the current capture to complete.", e);
                }
            }

            Log.d(TAG, "Acquired a lock. Proceeding to request a capture.");
            String fileName = FILE_NAME_APPENDER + System.currentTimeMillis();
            createFile(fileName);
            lockFocus();
        }

        requestFollowOnAction();
    }

    private void requestFollowOnAction() {
        Log.d(TAG, "Received notification completion");
        MainActivity.eventBus.post(new CaptureCompletionEvent(completedPictures));
    }
}
