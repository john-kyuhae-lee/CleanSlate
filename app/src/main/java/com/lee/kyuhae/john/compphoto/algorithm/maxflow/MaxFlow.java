package com.lee.kyuhae.john.compphoto.algorithm.maxflow;

import java.util.ArrayList;

/**
 * Created by john.lee on 7/20/16.
 */

public class MaxFlow extends Graph {
    private static final Arc TERMINAL = new Arc();
    private static final Arc ORPHAN = new Arc();
    private static final int INFINITE_DISTANCE = 1000000000;

    public void setActive(Node i) {
        if (i.getNext() == null) {
            if (queueLast[1] != null) {
                queueFirst[1].setNext(i);
            } else {
                queueLast[1] = i;
                i.setNext(i);
            }
        }
    }

    public Node nextActive() {
        Node i = queueFirst[0];

        while (true) {
            if (i == null) {
                queueFirst[0] = queueFirst[1];
                i = queueFirst[1];
                queueFirst[1] = null;
                queueLast[1] = null;
                if (i == null) {
                    return null;
                }
            }

            // Remove from the active list.
            if (i.getNext() == i) {
                queueFirst[0] = null;
                queueLast[0] = null;
            } else {
                i.setNext(null);
            }

            // A node in the list is active iff it has a parent.
            if (i.getParent() != null) {
                return i;
            }
        }
    }

    public void init() {
        queueFirst[0] = null;
        queueFirst[1] = null;
        queueLast[0] = null;
        queueLast[0] = null;
        orphanFirst = null;

        for (Node node : nodeList) {
            node.setNext(null);
            node.setTimestamp(0);
            if (node.getResidualCapacity() > 0) {
                // this node is connected to the source.
                node.setSink(false);
                node.setParent(TERMINAL);
                setActive(node);
                node.setDistance(1);
            } else if (node.getResidualCapacity() < 0) {
                // this node is connected to the sink
                node.setSink(true);
                node.setParent(TERMINAL);
                setActive(node);
                node.setDistance(1);
            } else {
                node.setParent(null);
            }
        }

        timestamp = 0;
    }

    public void augment(Arc middleArc) {
        Node i;
        Arc a;
        float bottleneck;
        NodePointer np;

	    /* 1. Finding bottleneck capacity */
        /* 1a - the source tree */
        bottleneck = middleArc.getResidualCapacity();
        for (i = middleArc.getSister().getHead(); ; i = a.getHead()) {
            a = i.getParent();
            if (a == TERMINAL) {
                break;
            }

            if (bottleneck > a.getSister().getResidualCapacity()) {
                bottleneck = a.getSister().getResidualCapacity();
            }
        }

        if (bottleneck > i.getResidualCapacity()) {
            bottleneck = i.getResidualCapacity();
        }

	    /* 1b - the sink tree */
        for (i = middleArc.getHead(); ; i = a.getHead()) {
            a = i.getParent();
            if (a == TERMINAL) {
                break;
            }

            if (bottleneck > a.getResidualCapacity()) {
                bottleneck = a.getResidualCapacity();
            }
        }

        if (bottleneck > i.getResidualCapacity()) {
            bottleneck = -(i.getResidualCapacity());
        }

	    /* 2. Augmenting */
        /* 2a - the source tree */
        float sisterRC = middleArc.getSister().getResidualCapacity() + bottleneck;
        middleArc.getSister().setResidualCapacity(sisterRC);
        float middleArcRC = middleArc.getResidualCapacity() - bottleneck;
        middleArc.setResidualCapacity(middleArcRC);

        for (i = middleArc.getSister().getHead(); ; i = a.getHead()) {
            a = i.getParent();
            if (a == TERMINAL) {
                break;
            }

            middleArcRC = a.getResidualCapacity() + bottleneck;
            a.setResidualCapacity(middleArcRC);
            sisterRC = a.getSister().getResidualCapacity() - bottleneck;
            a.getSister().setResidualCapacity(sisterRC);

            // TODO: Make sure I am getting this !floatVal cpp evaluation translated correctly.
            if (a.getSister().getResidualCapacity() < 0) {
                /* add i to the adoption list */
                i.setParent(ORPHAN);
                np = new NodePointer();
                nodePointerList.add(np);
                np.setPointer(i);
                np.setNext(orphanFirst);
                orphanFirst = np;
            }
        }

        float updatedRC = i.getResidualCapacity() - bottleneck;
        i.setResidualCapacity(updatedRC);

        if (i.getResidualCapacity() < 0) {
             /* add i to the adoption list */
            i.setParent(ORPHAN);
            np = new NodePointer();
            nodePointerList.add(np);
            np.setPointer(i);
            np.setNext(orphanFirst);
            orphanFirst = np;
        }

	    /* 2b - the sink tree */
        for (i = middleArc.getHead(); ; i = a.getHead()) {
            a = i.getParent();
            if (a == TERMINAL) {
                break;
            }
            sisterRC = a.getSister().getResidualCapacity() + bottleneck;
            a.getSister().setResidualCapacity(sisterRC);
            updatedRC = a.getResidualCapacity() - bottleneck;
            a.setResidualCapacity(updatedRC);

            if (a.getResidualCapacity() < 0) {
                /* add i to the adoption list */
                i.setParent(ORPHAN);
                np = new NodePointer();
                nodePointerList.add(np);
                np.setPointer(i);
                np.setNext(orphanFirst);
                orphanFirst = np;
            }
        }

        updatedRC = i.getResidualCapacity() + bottleneck;
        i.setResidualCapacity(updatedRC);

        if (i.getResidualCapacity() < 0) {
    		/* add i to the adoption list */
            i.setParent(ORPHAN);
            np = new NodePointer();
            nodePointerList.add(np);
            np.setPointer(i);
            np.setNext(orphanFirst);
            orphanFirst = np;
        }

        flow += bottleneck;
    }

    public void processSourceOrphan(Node i) {
        Node j;
        Arc a0, a0Min = null, a;
        NodePointer np;
        int d, dMin = INFINITE_DISTANCE;

        /* trying to find a new parent */
        for (a0 = i.getFirst(); a0 != null ; a0 = a0.getNext()) {
            if (a0.getSister().getResidualCapacity() >= 0) {
                j = a0.getHead();
                a = j.getParent();
                if (!j.isSink() && a != null) {
                    /* checking the origin of j */
                    d = 0;
                    while (true) {
                        if (j.getTimestamp() == timestamp) {
                            d += j.getDistance();
                            break;
                        }
                        a = j.getParent();
                        d++;

                        if (a == TERMINAL) {
                            j.setTimestamp(timestamp);
                            j.setDistance(1);
                            break;
                        }

                        if (a == ORPHAN) {
                            d = INFINITE_DISTANCE;
                            break;
                        }

                        j = a.getHead();
                    }

                    /* j originates from the source - done */
                    if (d < INFINITE_DISTANCE) {
                        if (d < dMin) {
                            a0Min = a0;
                            dMin = d;
                        }

                        /* set marks along the path */
                        for (j = a0.getHead(); j.getTimestamp() != timestamp; j = j.getParent().getHead()) {
                            j.setTimestamp(timestamp);
                            j.setDistance(d--);
                        }
                    }
                }
            }

            i.setParent(a0Min);
            if (i.getParent() != null) {
                i.setTimestamp(timestamp);
                i.setDistance(dMin + 1);
            } else {
                /* no parent is found */
                i.setTimestamp(0);

                /* process neighbors */
                for (a0 = i.getFirst(); a0 != null ; a0 = a0.getNext()) {
                    j = a0.getHead();
                    a = j.getParent();
                    if (!j.isSink() && a != null) {
                        if (a0.getSister().getResidualCapacity() >= 0) {
                            setActive(j);
                        }

                        if (a != TERMINAL && a != ORPHAN && a.getHead() == i) {
                            /* add j to the adoption list */
                            j.setParent(ORPHAN);
                            np = new NodePointer();
                            nodePointerList.add(np);
                            np.setPointer(j);
                            if (orphanLast != null) {
                                orphanLast.setNext(np);
                            } else {
                                orphanFirst = np;
                            }
                            orphanLast = np;
                            np.setNext(null);
                        }
                    }
                }
            }
        }
    }

    public void processSinkOrphan(Node i) {
        Node j;
        Arc a0, a0Min = null, a;
        NodePointer np;
        int d, dMin = INFINITE_DISTANCE;

        /* trying to find a new parent */
        for ( a0 = i.getFirst(); a0 != null; a0 = a0.getNext() ) {
            if (a0.getResidualCapacity() >= 0) {
                j = a0.getHead();
                a = j.getParent();
                if (j.isSink() && a != null) {
                    /* checking the origin of j */
                    d = 0;
                    while ( true ) {
                        if (j.getTimestamp() == timestamp) {
                            d += j.getDistance();
                            break;
                        }

                        a = j.getParent();
                        d++;
                        if (a == TERMINAL) {
                            j.setTimestamp(timestamp);
                            j.setDistance(1);
                            break;
                        }

                        if (a == ORPHAN) {
                            d = INFINITE_DISTANCE;
                            break;
                        }

                        j = a.getHead();
                    }

                    /* j originates from the sink - done */
                    if (d < INFINITE_DISTANCE) {
                        if (d < dMin) {
                            a0Min = a0;
                            dMin = d;
                        }

                        /* set marks along the path */
                        for (j = a0.getHead(); j.getTimestamp() != timestamp; j=j.getParent().getHead()) {
                            j.setTimestamp(timestamp);
                            j.setDistance(d--);
                        }
                    }
                }
            }

            i.setParent(a0Min);
            if (i.getParent() != null) {
                i.setTimestamp(timestamp);
                i.setDistance(dMin + 1);
            } else {
                /* no parent is found */
                i.setTimestamp(0);

                /* process neighbors */
                for (a0=i.getFirst(); a0 != null; a0=a0.getNext()) {
                    j = a0.getHead();
                    a = j.getParent();
                    if (j.isSink() && a != null) {
                        if (a0.getResidualCapacity() >= 0) {
                            setActive(j);
                        }

                        if ( a != TERMINAL && a != ORPHAN && a.getHead() == i) {
                            /* add j to the adoption list */
                            j.setParent(ORPHAN);
                            np = new NodePointer();
                            nodePointerList.add(np);
                            np.setPointer(j);
                            if (orphanLast != null) {
                                orphanLast.setNext(np);
                            } else {
                                orphanFirst = np;
                            }
                            orphanLast = np;
                            np.setNext(null);

                        }
                    }
                }
            }
        }
    }

    public double maxflow() {
        Node i, j, cur = null;
        Arc a;
        NodePointer np, npNext;

        init();
        nodePointerList = new ArrayList<>();

        while ( true ) {
            i = cur;
            if ( i != null ) {
                /* remove active flag */
                i.setNext(null);
                if (i.getParent() != null) {
                    i = null;
                }
            }

            if (i == null) {
                i = nextActive();
                if ( i == null ) {
                    break;
                }
            }

            /* growth */
            if (!i.isSink()) {
                /* grow source tree */
                for (a=i.getFirst(); a != null; a=a.getNext()) {
                    if (a.getResidualCapacity() >= 0) {
                        j = a.getHead();
                        if (j.getParent() == null) {
                            j.setSink(false);
                            j.setParent(a.getSister());
                            j.setTimestamp(i.getTimestamp());
                            j.setDistance(i.getDistance() + 1);
                            setActive(j);
                        } else if (j.isSink()) {
                            break;
                        } else if (j.getTimestamp() <= i.getTimestamp()
                                && j.getDistance() > i.getDistance()) {
                            /* heuristic - trying to make the distance from j to the source shorter */
                            j.setParent(a.getSister());
                            j.setTimestamp(i.getTimestamp());
                            j.setDistance(i.getDistance() + 1);
                        }
                    }
                }
            } else {
                /* grow sink tree */
                for (a=i.getFirst(); a != null; a=a.getNext()) {
                    if (a.getSister().getResidualCapacity() >= 0) {
                        j = a.getHead();
                        if (j.getParent() == null) {
                            j.setSink(true);
                            j.setParent(a.getSister());
                            j.setTimestamp(i.getTimestamp());
                            j.setDistance(i.getDistance() + 1);
                            setActive(j);
                        } else if (!j.isSink()) {
                            a = a.getSister();
                            break;
                        } else if (j.getTimestamp() <= i.getTimestamp()
                                && j.getDistance() > i.getDistance()) {
                            /* heuristic - trying to make the distance from the j to the sink shorter */
                            j.setParent(a.getSister());
                            j.setTimestamp(i.getTimestamp());
                            j.setDistance(i.getDistance() + 1);
                        }
                    }
                }
            }

            timestamp++;

            if (a != null) {
                /* set active flag */
                i.setNext(i);
                cur = i;

                /* augmentation */
                augment(a);
                /* augmentation end */

                /* adoption */
                while ( (np = orphanFirst) != null ) {
                    npNext = np.getNext();
                    np.setNext(null);

                    while ( (np = orphanFirst) != null ) {
                        orphanFirst = np.getNext();
                        i = np.getPointer();
                        nodePointerList.remove(np);
                        if (orphanFirst == null) {
                            orphanLast = null;
                        }

                        if (i.isSink()) {
                            processSinkOrphan(i);
                        } else {
                            processSourceOrphan(i);
                        }
                    }
                    orphanFirst = npNext;
                }
                /* adoption end */
            } else {
                cur = null;
            }
        }

        nodePointerList = null;
        return flow;
    }
}