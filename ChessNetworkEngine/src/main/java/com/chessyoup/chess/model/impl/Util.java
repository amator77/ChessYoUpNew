package com.chessyoup.chess.model.impl;

import com.chessyoup.chess.model.Color;
import com.chessyoup.chess.model.Factory;
import com.chessyoup.chess.model.Piece;

/**
 * Created by leo on 20.10.2014.
 */
public class Util {

    /**
     * Return index in squares[] vector corresponding to (x,y).
     */
    public static int getSquareIndex(int x, int y) {
        return y * 8 + x;
    }

    public int convertPiece(Piece piece) {
        switch (piece.getType()) {
            case BISHOP:
                return piece.getColor() == Color.WHITE ? PieceImpl.WBISHOP : PieceImpl.BBISHOP;
            case ROOK:
                return piece.getColor() == Color.WHITE ? PieceImpl.WROOK : PieceImpl.BROOK;
            case KNIGHT:
                return piece.getColor() == Color.WHITE ? PieceImpl.WKNIGHT : PieceImpl.BKNIGHT;
            case KING:
                return piece.getColor() == Color.WHITE ? PieceImpl.WKING : PieceImpl.BKING;
            case QUEEN:
                return piece.getColor() == Color.WHITE ? PieceImpl.WQUEEN : PieceImpl.BQUEEN;
            case PAWN:
                return piece.getColor() == Color.WHITE ? PieceImpl.WPAWN : PieceImpl.BPAWN;
        }

        return PieceImpl.EMPTY;
    }

    public static  Piece convertPiece(int piece) {

        System.out.println(piece);

        switch (piece) {
            case PieceImpl.WBISHOP:
                return Factory.getFactory().getPiece(Color.WHITE, Piece.PieceType.BISHOP);
            case PieceImpl.BBISHOP:
                return Factory.getFactory().getPiece(Color.BLACK, Piece.PieceType.BISHOP);
            case PieceImpl.WROOK:
                return Factory.getFactory().getPiece(Color.WHITE, Piece.PieceType.ROOK);
            case PieceImpl.BROOK:
                return Factory.getFactory().getPiece(Color.BLACK, Piece.PieceType.ROOK);
            case PieceImpl.WKNIGHT:
                return Factory.getFactory().getPiece(Color.WHITE, Piece.PieceType.KNIGHT);
            case PieceImpl.BKNIGHT:
                return Factory.getFactory().getPiece(Color.BLACK, Piece.PieceType.KNIGHT);
            case PieceImpl.WKING:
                return Factory.getFactory().getPiece(Color.WHITE, Piece.PieceType.KING);
            case PieceImpl.BKING:
                return Factory.getFactory().getPiece(Color.BLACK, Piece.PieceType.KING);
            case PieceImpl.WQUEEN:
                return Factory.getFactory().getPiece(Color.WHITE, Piece.PieceType.QUEEN);
            case PieceImpl.BQUEEN:
                return Factory.getFactory().getPiece(Color.BLACK, Piece.PieceType.QUEEN);
            case PieceImpl.WPAWN:
                return Factory.getFactory().getPiece(Color.WHITE, Piece.PieceType.PAWN);
            case PieceImpl.BPAWN:
                return Factory.getFactory().getPiece(Color.BLACK, Piece.PieceType.PAWN);
        }

        return null;
    }
}
