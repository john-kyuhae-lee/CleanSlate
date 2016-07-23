package com.lee.kyuhae.john.compphoto.algorithm;

import android.util.Log;

import com.lee.kyuhae.john.compphoto.algorithm.histogram.MLEMinimizer;

import org.opencv.core.CvType;
import org.opencv.core.Mat;

import java.util.Arrays;


/**
 * Created by john.lee on 7/22/16.
 */

public class ImageProcessor {
    private static final String TAG = "ImageProcessor";
    private static final double PENALTY_VALUE_CONVERSION_COEFFICIENT = 100.0d;

    private final Mat labelImage, dataPenaltyImage, interactionPenaltyImage;
    private final int width, height;
    private final short[] labels;
    private final MLEMinimizer mleMinimizer;
    private final Mat[] images;

    private Mat compositeImage;

    public ImageProcessor(Mat[] images) {
        this.width = images[0].width();
        this.height = images[0].height();
        this.images = images;

        // Info: CV_8U may not be the right type... We'll see.
        // CV_8U is for unsigned int 0-255.
        this.labelImage = Mat.ones(height, width, CvType.CV_8U);
        this.dataPenaltyImage = Mat.ones(height, width, CvType.CV_8U);
        this.interactionPenaltyImage = Mat.ones(height, width, CvType.CV_8U);
        this.labels = new short[width * height];
        Arrays.fill(labels, (short) 0);
        colorLabelImage();

        this.mleMinimizer = new MLEMinimizer(images, labels);
    }

    public void compute() {
        // This alters labels array.
        Log.d(TAG, "Starting MLE Minimizer computation.");
        this.mleMinimizer.compute();
        Log.d(TAG, "Completed MLE Minimizer computation.");

        // Info: This is optional
        // re-coloring of the label images after minimizer computation has ran.
        colorLabelImage();

        // Info: This is optional
        // This creates intermediate penalty data visualization.
        // createPenaltyVisualization();

        // Now, build a composite based on updated labels.
        this.compositeImage = createComposite();
    }

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

    private void createPenaltyVisualization() {
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Coordinate cPoint = new Coordinate(col, row);
                int dataPenalty = (int) (PENALTY_VALUE_CONVERSION_COEFFICIENT *
                        mleMinimizer.getCurrentDataPenalty(cPoint));
                int[] dpVals = {dataPenalty, dataPenalty, dataPenalty};
                dataPenaltyImage.put(row, col, dpVals);

                int interactionPenalty = (int) (PENALTY_VALUE_CONVERSION_COEFFICIENT
                        * mleMinimizer.getCurrentMaxInteractionPenalty(cPoint));
                int[] ipVals = {interactionPenalty, interactionPenalty, interactionPenalty};
                interactionPenaltyImage.put(row, col, ipVals);
            }
        }
    }

    private Mat createComposite() {
        Mat composite = Mat.ones(height, width, CvType.CV_8U);
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                Coordinate cPoint = new Coordinate(col, row);
                int imageIdx = labels[cPoint.getOneDimensionalIndex(width)];
                double[] rgbVals = images[imageIdx].get(row, col);
                composite.put(row, col, rgbVals);
            }
        }

        return composite;
    }
}
