package com.lee.kyuhae.john.compphoto.algorithm;

import android.util.Log;

import org.opencv.core.Mat;

import java.util.Arrays;

import lombok.Getter;

/**
 * Histogram -- This class is translated from the corresponding original work
 * (tmpfuse/histogram.h, tmpfuse/histogram.cpp). Acknowledging that it is java representation
 * of author's original work. Link to their artifact can be found in Readme file of the project.
 * Created by john.lee on 7/19/16.
 */
public class Histogram {
    public class Channel {
        private static final String TAG = "Histogram.Channel";
        private static final int MIN = 0;
        private static final int MAX = 256;
        private static final int NUM_BINS = 20;
        private static final float BIN_SIZE = ( MAX - MIN ) / (float) NUM_BINS;

        private int totalNumDataPoint = 0;
        private int peakBinIdx = -1;
        private int[] histogram;
        @Getter
        private double variance;

        public Channel() {
            this.histogram = new int[NUM_BINS];
            Arrays.fill(histogram, 0);
        }

        public void addValue(int val) {
            if (val < MIN || val > MAX) {
                Log.e(TAG, "A given data with value " + val + ".");
                throw new IllegalArgumentException("Data should be between " + MIN + " and " + MAX
                        + ". Given " + val + ".");
            }

            int binIdx = (int) ( val / BIN_SIZE );
            histogram[binIdx]++;
            totalNumDataPoint++;

            if (histogram[binIdx] > histogram[peakBinIdx]) {
                peakBinIdx = binIdx;
            }

            computeVariance();
        }

        private void computeVariance() {
            double mean = 0.0;
            for (int i = 0; i < NUM_BINS; i++) {
                mean += histogram[i] * ((i + 1) * BIN_SIZE);
            }
            mean /= totalNumDataPoint;

            variance = 0.0;
            for (int i = 0; i < NUM_BINS; i++) {
                variance += histogram[i] *
                        (((i + 1) * BIN_SIZE - mean) * (i * BIN_SIZE - mean));
            }
        }

        public double getProbability(int val) {
            int binIdx = (int) (val / BIN_SIZE);
            return histogram[binIdx] / totalNumDataPoint;
        }
    }

    public class Pixel {
        private final Channel rChannel = new Channel();
        private final Channel gChannel = new Channel();
        private final Channel bChannel = new Channel();

        public void addValues(double[] values) {
            addValues((int) values[0], (int) values[1], (int) values[2]);
        }

        public void addValues(int r, int g, int b) {
            this.rChannel.addValue(r);
            this.gChannel.addValue(g);
            this.bChannel.addValue(b);
        }

        public double getProbabiilty(int r, int g, int b) {
            return rChannel.getProbability(r)
                    * gChannel.getProbability(g)
                    * bChannel.getProbability(b);
        }
    }

    private static final int MIN_REQUIRED_NUM_IMAGES = 2;
    private final Pixel[] pixels;
    private final int width, height;

    public Histogram(final Mat[] images) {
        // Check that at least MIN_REQUIRED_NUM_IMAGES are given.
        if (images.length < MIN_REQUIRED_NUM_IMAGES) {
            throw new IllegalArgumentException("Mininum of " + MIN_REQUIRED_NUM_IMAGES +
                    " images required. Given " + images.length + " images.");
        }

        // Assuming that all images are of the same height and same weights.
        width = images[0].width();
        height = images[0].height();
        this.pixels = new Pixel[width * height];

        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                for (Mat image : images) {
                    // openCV expects row first.
                    double[] pixelValues = image.get(row, col);
                    int pixelLocation = row * width + col;
                    pixels[pixelLocation] = new Pixel();
                    pixels[pixelLocation].addValues(pixelValues);
                }
            }
        }
    }

    public Pixel getPixel(int col, int row) {
        int pixelLocation = row * width + col;
        return pixels[pixelLocation];
    }
}
