package com.chessyoup.chess.model.impl;

import com.chessyoup.chess.model.Color;
import com.chessyoup.chess.model.Square;

/**
 * Created by leo on 17.10.2014.
 */
public class SquareImpl implements Square {

    private Color color;
    private char file;
    private int rank;

    public SquareImpl(Color color, char file , int rank){
        this.color = color;
        this.file = file;
        this.rank = rank;
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    @Override
    public char getFile() {
        return this.file;
    }

    @Override
    public int getRank() {
        return this.rank;
    }

    @Override
    public String toString() {
        return "SquareImpl{" +
                "color=" + color +
                ", file=" + file +
                ", rank=" + rank +
                '}';
    }
}
