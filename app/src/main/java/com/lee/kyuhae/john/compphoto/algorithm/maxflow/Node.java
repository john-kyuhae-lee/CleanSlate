package com.lee.kyuhae.john.compphoto.algorithm.maxflow;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Created by john.lee on 7/20/16.
 */
@Data
@Accessors(chain = true)
public class Node {
    private Arc first;
    private Arc parent;
    private Node next;

    private int timestamp;
    private int distance;
    private boolean sink;

    private float residualCapacity;
}
