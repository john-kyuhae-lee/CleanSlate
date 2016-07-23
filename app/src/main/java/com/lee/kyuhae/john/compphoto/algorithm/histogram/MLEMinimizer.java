package com.lee.kyuhae.john.compphoto.algorithm.histogram;

import android.util.Log;

import com.lee.kyuhae.john.compphoto.algorithm.Coordinate;

import org.opencv.core.Mat;

/**
 * Maximum Likelyhood Estimation (MLE) Energy Minimizer
 *
 * Created by john.lee on 7/19/16.
 */
public class MLEMinimizer extends EnergyMinimizer {
    private static final String TAG = "MLEMinimizer";

    /**
     * Max Histogram Cut related Constants.
     */
    private static final double INFINITE_CAPACITY = 1000000;
    private static final int MAX_ITERATION = 100;
    private static final float MAX_PROBABILITY = 1.0f;
    private static final int NUM_CHANNEL = 3;

    /**
     * Constants for Interaction Penalty calculation.
     */
    private static final float INTERACTION_PENALTY_COEFFICIENT = 0.6f;
    private static final float POTTS_INTERACTION_ENERGY_CONSTANT = 0.000001f;
    private static final float REGULAR_INTERACTION_ENERGY_CONSTANT = 0.1f;

    private final Histogram histogram;

    public MLEMinimizer(Mat[] images, short[] labels) {
        this(images, labels, EXPANSION_FLAG_DEFAULT);
    }

    public MLEMinimizer(Mat[] images, short[] labels, boolean expansionFlag) {
        super(images, labels, expansionFlag);

        this.histogram = new Histogram(images);
        Log.d(TAG, "Starting to compute histogram for the given images.");
        histogram.compute();
        Log.d(TAG, "Completed histogram computation.");
    }

    private double getDataCost(Coordinate point, short d) {
        if (histogram == null) {
            Log.d(TAG, "Calling getDataCost when histogram has not set.");
            throw new IllegalStateException("Histogram has not set!");
        }

        Histogram.Pixel histogramPixel = histogram.getPixel(point.getCol(), point.getRow());
        double[] rgbValues = images[d].get(point.getRow(), point.getCol());
        double probability = histogramPixel.getProbability(rgbValues);

        if (probability < 0 || probability > MAX_PROBABILITY) {
            Log.d(TAG, "Probability is " + probability + ", Your codes seem to be broken. " +
                    "Have fun debugging :)");
            throw new IllegalStateException("Probability is not within the limit.");
        }

        // Info: If you want to do a min histogram, return the probability as is.
        return MAX_PROBABILITY - probability;
    }

    double BVZDataPenalty(Coordinate point, short d) {
        // Warn: Make sure 'transform' doesn't apply to my case.
        // If it does apply, needs to implement _displace() function.
        if (point.greaterThanOrEqualTo(ZERO_COORDINATE)
                && point.smallerThan(coordinateLimit)) {
            return getDataCost(point, d);
        } else {
            return INFINITE_CAPACITY;
        }
    }

    double BVZInteractionPenalty(
            Coordinate cPoint, Coordinate nPoint, short cLabel, short nLabel) {
        // Warn: For max histogram, I think C_NORMAL type is used - this could be source of trouble.
        if (cLabel >= images.length || nLabel >= images.length) {
            String message = "Received curPointLabel: " + cLabel + ", "
                    + "neighborPointLabel: " + nLabel
                    + ", images.length: " + images.length
                    + "\n labelValue should be less than image length.";
            Log.d(TAG, message);
            throw new IllegalStateException(message);
        }

        // No penalty case.
        if (cLabel == nLabel) {
            return 0.0d;
        }

        // C_NORMAL type interaction penalty calculation.
        // 1. Calculate the difference at point Coordinate.
        float a = 0, M;
        double[] cPointRGB = images[cLabel]
                .get(cPoint.getRow(), cPoint.getCol());
        double[] nPointRGB = images[nLabel]
                .get(cPoint.getRow(), cPoint.getCol());
        for (int c = 0; c < NUM_CHANNEL; c++) {
            int k = (int) cPointRGB[c] - (int) nPointRGB[c];
            a += k * k;
        }
        M = a * a;

        // 2. Calculate the difference at neighborPoint Coordinate.
        a = 0;
        cPointRGB = images[cLabel]
                .get(nPoint.getRow(), nPoint.getCol());
        nPointRGB = images[nLabel]
                .get(nPoint.getRow(), nPoint.getCol());
        for (int c = 0; c < NUM_CHANNEL; c++) {
            int k = (int) cPointRGB[c] - (int) nPointRGB[c];
            a += k * k;
        }
        M += a * a;
        M /= INTERACTION_PENALTY_COEFFICIENT;
        if (M > INFINITE_CAPACITY) {
            M = (float) INFINITE_CAPACITY;
        }

        // HistogramCut version of Interaction Penalty adjustment.
        return POTTS_INTERACTION_ENERGY_CONSTANT + (REGULAR_INTERACTION_ENERGY_CONSTANT * M);
    }

    public void compute() {
        double energy, energyOld;
        int stepCounter = 0;

        energy = BVZComputeEnergy();
        Log.d(TAG, "Starting energy: " + energy);
        for (int i = 0; i < MAX_ITERATION; i++) {
            for (short step = 0; step < images.length && stepCounter < images.length; step++) {
                energyOld = energy;
                energy = BVZExpand(step, energyOld);

                if (energyOld == energy) {
                    stepCounter++;
                } else {
                    stepCounter = 0;
                }

                Log.d(TAG, "i: " + i + ", step: " + step + ", stepCounter: " + stepCounter
                        + "energy: " + energy + ", energyOld: " + energyOld);

                // TODO: Put an event-driven stop functionality.
                // This could take awhile, and it seems this is what the original authors did.
            }
        }
    }

    public double getCurrentDataPenalty(Coordinate cPoint) {
        if ( cPoint.greaterThanOrEqualTo(ZERO_COORDINATE) &&
                cPoint.smallerThan(coordinateLimit) ) {
            return BVZDataPenalty(cPoint, labels[cPoint.getOneDimensionalIndex(width)]);
        } else {
            String message = "Received coordinate outside the range: " + cPoint.toString();
            Log.d(TAG, message);
            throw new IllegalArgumentException(message);
        }
    }

    public double getCurrentMaxInteractionPenalty(Coordinate cPoint) {
        if ( cPoint.greaterThanOrEqualTo(ZERO_COORDINATE) &&
                cPoint.smallerThan(coordinateLimit) ) {
            double maxPenalty = Double.MIN_VALUE;
            int col = cPoint.getCol();
            int row = cPoint.getRow();
            Coordinate nPoint;
            if (col > 0) {
                nPoint = new Coordinate(col - 1, row);
                maxPenalty = Math.max(maxPenalty, BVZInteractionPenalty(cPoint, nPoint,
                        labels[cPoint.getOneDimensionalIndex(width)],
                        labels[nPoint.getOneDimensionalIndex(width)]));
            }

            if (col < width - 1) {
                nPoint = new Coordinate(col + 1, row);
                maxPenalty = Math.max(maxPenalty, BVZInteractionPenalty(cPoint, nPoint,
                        labels[cPoint.getOneDimensionalIndex(width)],
                        labels[nPoint.getOneDimensionalIndex(width)]));
            }

            if (row > 0) {
                nPoint = new Coordinate(col, row - 1);
                maxPenalty = Math.max(maxPenalty, BVZInteractionPenalty(cPoint, nPoint,
                        labels[cPoint.getOneDimensionalIndex(width)],
                        labels[nPoint.getOneDimensionalIndex(width)]));
            }

            if (row < height - 1) {
                nPoint = new Coordinate(col, row + 1);
                maxPenalty = Math.max(maxPenalty, BVZInteractionPenalty(cPoint, nPoint,
                        labels[cPoint.getOneDimensionalIndex(width)],
                        labels[nPoint.getOneDimensionalIndex(width)]));
            }

            return maxPenalty;
        } else {
            String message = "Received coordinate outside the range: " + cPoint.toString();
            Log.d(TAG, message);
            throw new IllegalArgumentException(message);
        }
    }
}
