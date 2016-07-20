package com.lee.kyuhae.john.compphoto.event;

import java.util.ArrayList;

import lombok.Getter;

/**
 * Boilerplate for event definition. This is used from MyCameraFragment to notify
 * that the capture is completed.
 * Created by john.lee on 7/17/16.
 */
public class CaptureCompletionEvent {
    // Paths to pictures.
    @Getter private final ArrayList<String> pictures;

    public CaptureCompletionEvent(ArrayList<String> pictures) {
        this.pictures = pictures;
    }
}
