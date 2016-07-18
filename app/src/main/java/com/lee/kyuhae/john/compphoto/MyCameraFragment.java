package com.lee.kyuhae.john.compphoto;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lee.kyuhae.john.compphoto.camera2basic.AutoFitTextureView;
import com.lee.kyuhae.john.compphoto.camera2basic.Camera2BasicFragment;
import com.lee.kyuhae.john.compphoto.event.CaptureCompletionEvent;


/**
 * Created by john.lee on 7/16/16.
 */

public class MyCameraFragment extends Camera2BasicFragment {
    private static final String TAG = "MyCameraFragment";

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
        Log.d(TAG, "Received notification completion");
        MainActivity.eventBus.post(new CaptureCompletionEvent(mFile.getPath()));
    }
}
