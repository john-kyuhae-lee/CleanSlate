package com.lee.kyuhae.john.compphoto.algorithm;

import com.lee.kyuhae.john.compphoto.algorithm.histogram.MLEMinimizer;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.Arrays;


/**
 * Created by john.lee on 7/22/16.
 */

public class ImageProcessor {
    private static final String TAG = "ImageProcessor";
    private Mat compImage, labelImage, blendedImage, tBlendImage, dataTermImage, interTermImage;
    private int width, height;
    private short[] labels;
    private MLEMinimizer mleMinimizer;

    // This may require a new class corresponding to stackViewer.h and stackViewer.cpp
    private Mat[] imageStack;

    private void colorLabelImage() {
        int index = 0;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++, index++) {
                RGBVector rgbVector = LabelColor.list.get(labels[index]);
                int[] color = {rgbVector.getR(), rgbVector.getG(), rgbVector.getB()};
                labelImage.put(row, col, color);
            }
        }
    }

    public void intializeComposite(Mat image) {
        this.width = image.width();
        this.height = image.height();
        this.compImage = image;

        // Info: CV_8U may not be the right type... We'll see.
        // CV_8U is for unsigned int 0-255.
        this.blendedImage = Mat.ones(height, width, CvType.CV_8U);
        this.tBlendImage = Mat.ones(height, width, CvType.CV_8U);
        this.labelImage = Mat.ones(height, width, CvType.CV_8U);
        this.dataTermImage = Mat.ones(height, width, CvType.CV_8U);
        this.interTermImage = Mat.ones(height, width, CvType.CV_8U);
        this.labels = new short[width * height];
        Arrays.fill(labels, (short) 0);
        colorLabelImage();
    }

    public void initializeStack(Mat[] images) {
        this.imageStack = images;
        this.mleMinimizer = new MLEMinimizer(images, labels);
    }

    public void compute() {

    }
}
