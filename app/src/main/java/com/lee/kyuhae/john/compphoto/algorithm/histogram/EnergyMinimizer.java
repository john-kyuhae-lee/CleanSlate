package com.lee.kyuhae.john.compphoto.algorithm.histogram;

import android.util.Log;

import com.lee.kyuhae.john.compphoto.algorithm.Coordinate;
import com.lee.kyuhae.john.compphoto.algorithm.maxflow.Graph;
import com.lee.kyuhae.john.compphoto.algorithm.maxflow.MaxFlowFinder;
import com.lee.kyuhae.john.compphoto.algorithm.maxflow.Node;

import org.opencv.core.Mat;

/**
 * Created by john.lee on 7/22/16.
 */

abstract class EnergyMinimizer {
    private static final String TAG = "EnergyMinimizer";

    private static final Node ACTIVE_NODE = new Node();
    private static final Node NON_PRESENT_NODE = new Node();

    private static final double INTERACTION_PENALTY_EXPANSION_THRESHOLD = 0.0001d;
    static final boolean EXPANSION_FLAG_DEFAULT = false;

    /**
     * Constants for BVZ algorithm -- general ones that are used in a multiple BVZ functions.
     */
    static final Coordinate ZERO_COORDINATE = new Coordinate(0, 0);
    private static final Coordinate[] NEIGHBORS = {new Coordinate(1, 0), new Coordinate(0, -1)};

    /**
     * BVZ termA expansion sink-source configuration variables.
     */
    private boolean alphaSink;
    private int termA;
    private int termB;

    final Mat[] images;
    private final int height;
    private final int width;
    private final short[] labels;
    final Coordinate coordinateLimit;

    EnergyMinimizer(Mat[] images, short[] labels) {
        this(images, labels, EXPANSION_FLAG_DEFAULT);
    }

    EnergyMinimizer(Mat[] images, short[] labels, boolean expansionFlag) {
        this.images = images;
        this.labels = labels;
        this.height = images[0].height();
        this.width = images[0].width();
        this.coordinateLimit = new Coordinate(width, height);

        setAlphaSink(expansionFlag);
    }

    private void setAlphaSink(boolean flag) {
        alphaSink = flag;
        if (alphaSink) {
            termA = Graph.SOURCE;
            termB = Graph.SINK;
        } else {
            termA = Graph.SINK;
            termB = Graph.SOURCE;
        }
    }

    abstract double BVZDataPenalty(Coordinate point, short d);

    abstract double BVZInteractionPenalty(
           Coordinate cPoint, Coordinate nPoint, short cLabel, short nLabel);

    private boolean isNode(Node n) {
        return n != null && n != ACTIVE_NODE && n != NON_PRESENT_NODE;
    }

    double BVZComputeEnergy() {
        double energy = 0.0;
        for (int row = 0; row < images[0].height(); row++) {
            for (int col = 0; col < images[0].width(); col++) {
                Coordinate cPoint = new Coordinate(col, row);

                // Instead of increment index each iterations,
                // using index calculation from the Coordinate.
                // TODO: If something's off, this is changed from the original code.
                short cLabel = labels[cPoint.getOneDimensionalIndex(width)];
                energy += BVZDataPenalty(cPoint, cLabel);

                for (Coordinate adjPoint : NEIGHBORS) {
                    Coordinate nPoint =
                            new Coordinate(cPoint.getCol() + adjPoint.getCol(),
                                    cPoint.getRow() + adjPoint.getRow());

                    if (nPoint.greaterThanOrEqualTo(ZERO_COORDINATE) &&
                            nPoint.smallerThan(coordinateLimit)) {
                        short nLabel = labels[nPoint.getOneDimensionalIndex(width)];
                        energy += BVZInteractionPenalty(cPoint, nPoint, cLabel, nLabel);
                    }
                }
            }
        }
        return energy;
    }

    double BVZExpand(short a, double energyOld) {
        Graph g = new Graph();
        double energy = 0.0d;

        Node[] nodeArray = new Node[width * height];
        double[] penaltyArray = new double[width * height];

        // Initializing -- Start of the graph building.
        Coordinate cPoint = new Coordinate(0, 0);
        for (cPoint.setRow(0); cPoint.getRow() < height; cPoint.incrementRow()) {
            // Warn: Original code uses index and increment it by 1 each loop.
            for (cPoint.setCol(0); cPoint.getCol() < width; cPoint.incrementColumn()) {
                short cLabel = labels[cPoint.getOneDimensionalIndex(width)];

                if (a == cLabel) {
                    nodeArray[cPoint.getOneDimensionalIndex(width)] = ACTIVE_NODE;
                    energy += BVZDataPenalty(cPoint, cLabel);
                    continue;
                }

                nodeArray[cPoint.getOneDimensionalIndex(width)] = g.addNode();
                double delta = BVZDataPenalty(cPoint, cLabel);
                penaltyArray[cPoint.getOneDimensionalIndex(width)] =
                        BVZDataPenalty(cPoint, a) - delta;
                energy += delta;
            }
        }

        for (cPoint.setRow(0); cPoint.getRow() < height; cPoint.incrementRow()) {
            // Warn: Original code uses index and increment it by 1 each loop.
            for (cPoint.setCol(0); cPoint.getCol() < width; cPoint.incrementColumn()) {
                int cIndex = cPoint.getOneDimensionalIndex(width);
                short cLabel = labels[cIndex];
                Node cNode = nodeArray[cIndex];

                // Adding interaction
                for (Coordinate adjPoint : NEIGHBORS) {
                    Coordinate nPoint = new Coordinate(
                            cPoint.getCol() + adjPoint.getCol(),
                            cPoint.getRow() + adjPoint.getRow());

                    boolean skip = !(nPoint.greaterThanOrEqualTo(ZERO_COORDINATE)
                            && nPoint.smallerThan(coordinateLimit));
                    if (skip) {
                        continue;
                    }

                    int nIndex = nPoint.getOneDimensionalIndex(width);
                    short nLabel = labels[nIndex];
                    Node nNode = nodeArray[nIndex];

                    if (isNode(cNode) && isNode(nNode)) {
                        double penalty00 = BVZInteractionPenalty(cPoint, nPoint, cLabel, nLabel);
                        double penalty0A = BVZInteractionPenalty(cPoint, nPoint, cLabel, a);
                        double penaltyA0 = BVZInteractionPenalty(cPoint, nPoint, a, nLabel);

                        double delta = penalty00 < penalty0A ? penalty00 : penalty0A;
                        if (delta > 0) {
                            penaltyArray[cPoint.getOneDimensionalIndex(width)] -= delta;
                            energy += delta;
                            penalty00 -= delta;
                            penalty0A -= delta;
                        }

                        delta = penalty00 < penaltyA0 ? penalty00 : penaltyA0;
                        if (delta > 0) {
                            penaltyArray[nPoint.getOneDimensionalIndex(width)] -= delta;
                            energy += delta;
                            penalty00 -= delta;
                            penaltyA0 -= delta;
                        }

                        if (penalty00 > INTERACTION_PENALTY_EXPANSION_THRESHOLD) {
                            Log.d(TAG, "penalty00 is over the threshold. It is non-metric: "
                                    + penalty00);
                        }

                        if (alphaSink) {
                            g.addEdge(cNode, nNode, penalty0A, penaltyA0);
                        } else {
                            g.addEdge(cNode, nNode, penaltyA0, penalty0A);
                        }
                    } else if (isNode(cNode) && !isNode(nNode)) {
                        // Case where nNode does not exist.
                        double delta = BVZInteractionPenalty(cPoint, nPoint, cLabel, a);
                        penaltyArray[cPoint.getOneDimensionalIndex(width)] -= delta;
                        energy += delta;
                    } else if (!isNode(cNode) && isNode(nNode)) {
                        // Case where nNode does not exist.
                        double delta = BVZInteractionPenalty(cPoint, nPoint, a, nLabel);
                        penaltyArray[nPoint.getOneDimensionalIndex(width)] -= delta;
                        energy += delta;
                    }
                }
            }
        }
        /* -- end of the graph building. ready to call MaxFlowFinder */


        MaxFlowFinder maxFlowFinder = new MaxFlowFinder(g);

        /* Adding source and sink edges */
        for (cPoint.setRow(0); cPoint.getRow() < height; cPoint.incrementRow()) {
            for (cPoint.setCol(0); cPoint.getCol() < width; cPoint.incrementColumn()) {
                Node cNode = nodeArray[cPoint.getOneDimensionalIndex(width)];
                if (isNode(cNode)) {
                    double delta = penaltyArray[cPoint.getOneDimensionalIndex(width)];
                    if (alphaSink) {
                        if (delta > 0) {
                            maxFlowFinder.setTweights(cNode, delta, 0);
                        } else {
                            maxFlowFinder.setTweights(cNode, 0, -delta);
                            energy += delta;
                        }
                    } else {
                        if (delta > 0) {
                            maxFlowFinder.setTweights(cNode, 0, delta);
                        } else {
                            maxFlowFinder.setTweights(cNode, -delta, 0);
                            energy += delta;
                        }
                    }
                }
            }
        }

        energy += maxFlowFinder.findMaxFlow();

        if (energy < energyOld) {
            for (cPoint.setRow(0); cPoint.getRow() < height; cPoint.incrementRow()) {
                for (cPoint.setCol(0); cPoint.getCol() < width; cPoint.incrementColumn()) {
                    Node cNode = nodeArray[cPoint.getOneDimensionalIndex(width)];

                    if (isNode(cNode) && g.whatSegment(cNode) == termB) {
                        labels[cPoint.getOneDimensionalIndex(width)] = a;
                    }
                }
            }

            return energy;
        }

        return energyOld;
    }
}
