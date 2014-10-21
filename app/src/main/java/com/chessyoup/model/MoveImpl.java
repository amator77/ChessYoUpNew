package com.chessyoup.model;


public class MoveImpl {
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
}
