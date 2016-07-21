package com.lee.kyuhae.john.compphoto.algorithm.maxflow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john.lee on 7/20/16.
 */
public class Graph {
    final List<Node> nodeList = new ArrayList<>();
    private final List<Arc> arcList = new ArrayList<>();
    List<NodePointer> nodePointerList;
    Node[] queueFirst = new Node[2];
    Node[] queueLast = new Node[2];
    NodePointer orphanFirst = null;
    NodePointer orphanLast = null;
    int timestamp = 0;
    double flow = 0.0;

    public Node addNode() {
        Node i = new Node();
        i.setFirst(null);
        i.setResidualCapacity(0);

        nodeList.add(i);

        return i;
    }

    public void addEdge(Node from, Node to, float capacity, float reverseCapacity) {
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

    public void setTweights(Node i, float sourceCapacity, float sinkCapacity) {
        flow += sourceCapacity < sinkCapacity ? sourceCapacity : sinkCapacity;
        i.setResidualCapacity(sourceCapacity - sinkCapacity);
    }

    public void addTweights(Node i, float sourceCapacity, float sinkCapacity) {
        float delta = i.getResidualCapacity();
        if (delta > 0) {
            sourceCapacity += delta;
        } else {
            sinkCapacity -= delta;
        }

        setTweights(i, sourceCapacity, sinkCapacity);
    }

}
