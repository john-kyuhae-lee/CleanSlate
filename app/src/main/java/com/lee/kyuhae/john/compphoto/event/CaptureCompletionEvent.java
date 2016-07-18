package com.lee.kyuhae.john.compphoto.event;

import lombok.Getter;

/**
 * Boilerplate for event definition. This is used from MyCameraFragment to notify
 * that the capture is completed.
 * Created by john.lee on 7/17/16.
 */
public class CaptureCompletionEvent {
    @Getter private final String photoPath;

    public CaptureCompletionEvent(String photoPath) {
        this.photoPath = photoPath;
    }
}
