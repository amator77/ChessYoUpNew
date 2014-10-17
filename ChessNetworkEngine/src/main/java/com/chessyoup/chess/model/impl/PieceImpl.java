package com.chessyoup.chess.model.impl;

import com.chessyoup.chess.model.Color;
import com.chessyoup.chess.model.Piece;

/**
 * Created by leo on 17.10.2014.
 */
public class PieceImpl implements Piece {

    private Color color;

    private PieceType type;

    public PieceImpl(Color color , PieceType type){
        this.color = color;
        this.type = type;
    }

    @Override
    public Color getColor() {
        return this.color;
    }

    @Override
    public PieceType getType() {
        return this.type;
    }
}
