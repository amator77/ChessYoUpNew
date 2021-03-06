package com.chessyoup.chess.model.impl;

import com.chessyoup.chess.model.Factory;
import com.chessyoup.chess.model.Move;
import com.chessyoup.chess.model.Piece;
import com.chessyoup.chess.model.Square;

/**
 * Created by leo on 17.10.2014.
 */
public class MoveImpl implements Move {

    /** From square, 0-63. */
    public int from;

    /** To square, 0-63. */
    public int to;

    /** Promotion piece. */
    public int promoteTo;

    /** Create a move object. */
    public MoveImpl(int from, int to, int promoteTo) {
        this.from = from;
        this.to = to;
        this.promoteTo = promoteTo;
    }

    public MoveImpl(MoveImpl m) {
        this.from = m.from;
        this.to = m.to;
        this.promoteTo = m.promoteTo;
    }

    @Override
    public boolean equals(Object o) {
        if ((o == null) || (o.getClass() != this.getClass()))
            return false;
        MoveImpl other = (MoveImpl)o;
        if (from != other.from)
            return false;
        if (to != other.to)
            return false;
        if (promoteTo != other.promoteTo)
            return false;
        return true;
    }
    @Override
    public int hashCode() {
        return (from * 64 + to) * 16 + promoteTo;
    }

    /** Useful for debugging. */
    public final String toString() {
        return TextIO.moveToUCIString(this);
    }

    @Override
    public Square getSource() {
        return Util.getSquare(from);
    }

    @Override
    public Square getDestination() {
        return Util.getSquare(to);
    }

    @Override
    public Piece getPromotionPiece() {
        return Util.convertPiece(promoteTo);
    }

    @Override
    public String toUCIString() {
        return TextIO.moveToUCIString(this);
    }
}
