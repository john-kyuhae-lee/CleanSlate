package com.lee.kyuhae.john.compphoto.algorithm.maxflow;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

/**
 * Created by john.lee on 7/20/16.
 */
public class Graph {
    public static final int SOURCE = 0;
    public static final int SINK = 1;

    @Getter private final List<Node> nodeList = new ArrayList<>();
    private final List<Arc> arcList = new ArrayList<>();

    public Node addNode() {
        Node i = new Node();
        i.setFirst(null);
        i.setResidualCapacity(0);

        nodeList.add(i);

        return i;
    }

    public void addEdge(Node from, Node to, double capacity, double reverseCapacity) {
        Arc a = new Arc();
        Arc aRev = new Arc();

        arcList.add(a);
        arcList.add(aRev);

        a.setSister(aRev);
        aRev.setSister(a);
        a.setNext(from.getFirst());
        from.setFirst(a);
        aRev.setNext(to.getFirst());
        to.setFirst(aRev);
        a.setHead(to);
        aRev.setHead(from);
        a.setResidualCapacity(capacity);
        aRev.setResidualCapacity(reverseCapacity);
    }

    public int whatSegment(Node i) {
        if (i.getParent() != null && !i.isSink()) {
            return SOURCE;
        }
        return SINK;
    }
}
