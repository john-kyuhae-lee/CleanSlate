package com.lee.kyuhae.john.compphoto.algorithm;

import lombok.Data;

/**
 * Created by john.lee on 7/19/16.
 */
@Data
public class Coordinate {
    private final int col, row;

    public Coordinate(int col, int row) {
        this.col = col;
        this.row = row;
    }

    public boolean greaterThanOrEqualTo(Coordinate other) {
        return this.col >= other.getCol() && this.row >= other.getRow();
    }

    public boolean smallerThan(Coordinate other) {
        return this.col < other.getCol() && this.row < other.getRow();
    }
}
