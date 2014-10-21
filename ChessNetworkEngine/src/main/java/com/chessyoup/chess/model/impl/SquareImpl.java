package com.chessyoup.chess.model.impl;

import com.chessyoup.chess.model.Color;
import com.chessyoup.chess.model.Square;

/**
 * Created by leo on 21.10.2014.
 */
public class SquareImpl implements Square {
    private int index;

    private char file;
    private int rank;

    public SquareImpl(char file, int rank) {
        this.file = file;
        this.rank = rank;
        this.index = Util.getSquareRealIndex(file,rank-1);
    }

    public Color getColor() {
        return darkSquare(getX(index),getY(index)) ? Color.BLACK : Color.WHITE;
    }

    public char getFile() {
        return this.file;
    }

    public int getRank() {
        return this.rank;
    }

    @Override
    public String toString() {
        return "SquareImpl{" +
                "index=" + index +
                ", file=" + file +
                ", rank=" + rank +
                ", color=" + getColor() +
                '}';
    }

    /**
     * Return index in squares[] vector corresponding to (x,y).
     */
    public final static int getSquare(int x, int y) {
        return y * 8 + x;
    }

    /**
     * Return x position (file) corresponding to a square.
     */
    public final static int getX(int square) {
        return square & 7;
    }

    /**
     * Return y position (rank) corresponding to a square.
     */
    public final static int getY(int square) {
        return square >> 3;
    }

    /**
     * Return true if (x,y) math coordinate ( a1 is 0 ,0 , h8 is 7,7 ) is a dark square.
     */
    public final static boolean darkSquare(int x, int y) {
        return (x & 1) == (y & 1);
    }
}
