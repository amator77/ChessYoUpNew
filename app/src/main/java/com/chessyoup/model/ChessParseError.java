package com.chessyoup.model;

public class ChessParseError extends Exception {
    private static final long serialVersionUID = -6051856171275301175L;

    public PositionImpl pos;
    public int resourceId = -1;

    public ChessParseError(String msg) {
        super(msg);
        pos = null;
    }
    public ChessParseError(String msg, PositionImpl pos) {
        super(msg);
        this.pos = pos;
    }

    public ChessParseError(int resourceId) {
        super("");
        pos = null;
        this.resourceId = resourceId;
    }

    public ChessParseError(int resourceId, PositionImpl pos) {
        super("");
        this.pos = pos;
        this.resourceId = resourceId;
    }
}
