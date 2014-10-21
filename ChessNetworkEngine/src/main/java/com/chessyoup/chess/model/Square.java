package com.chessyoup.chess.model;

public class Square {

    private int index;

    public Square(char file, int rank) {
        int x = 7 - (104 - file);
        int y = 7 - (rank-1);
        this.index = getSquare(x,y);
    }

    public Square(int index) {
        this.index = index;
    }

    public Color getColor() {
        int x = 7-getY(index);
        int y = getX(index);
        return darkSquare(x,y) ? Color.BLACK : Color.WHITE;
    }

    public char getFile() {
        return (char)(getX(this.index)+97);
    }

    public int getRank() {
        return getY(this.index);
    }

    public int getIndex() {
        return this.index;
    }

    @Override
    public String toString() {
        return "Square{" +
                "index=" + index +
                ", color=" + getColor() +
                ", file=" + getFile() +
                ", rank=" + getRank() +
                ", x=" + getX(this.index) +
                ", y=" + getY(this.index) +
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
