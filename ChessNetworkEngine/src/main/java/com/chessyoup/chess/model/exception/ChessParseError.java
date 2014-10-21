package com.chessyoup.chess.model.exception;

import com.chessyoup.chess.model.impl.PositionImpl;

/**
 * Created by leo on 17.10.2014.
 */
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
