package com.chessyoup.chess.model.impl;

import com.chessyoup.chess.model.Color;
import com.chessyoup.chess.model.Factory;
import com.chessyoup.chess.model.Move;
import com.chessyoup.chess.model.Piece;
import com.chessyoup.chess.model.Square;

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

    public static int convertPiece(Piece piece) {
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
            case NONE:
                return PieceImpl.EMPTY;
        }

        return PieceImpl.EMPTY;
    }

    public static  Piece convertPiece(int piece) {

        switch (piece) {
            case PieceImpl.WBISHOP:
                return Factory.getFactory().getPiece(Color.WHITE, Piece.Type.BISHOP);
            case PieceImpl.BBISHOP:
                return Factory.getFactory().getPiece(Color.BLACK, Piece.Type.BISHOP);
            case PieceImpl.WROOK:
                return Factory.getFactory().getPiece(Color.WHITE, Piece.Type.ROOK);
            case PieceImpl.BROOK:
                return Factory.getFactory().getPiece(Color.BLACK, Piece.Type.ROOK);
            case PieceImpl.WKNIGHT:
                return Factory.getFactory().getPiece(Color.WHITE, Piece.Type.KNIGHT);
            case PieceImpl.BKNIGHT:
                return Factory.getFactory().getPiece(Color.BLACK, Piece.Type.KNIGHT);
            case PieceImpl.WKING:
                return Factory.getFactory().getPiece(Color.WHITE, Piece.Type.KING);
            case PieceImpl.BKING:
                return Factory.getFactory().getPiece(Color.BLACK, Piece.Type.KING);
            case PieceImpl.WQUEEN:
                return Factory.getFactory().getPiece(Color.WHITE, Piece.Type.QUEEN);
            case PieceImpl.BQUEEN:
                return Factory.getFactory().getPiece(Color.BLACK, Piece.Type.QUEEN);
            case PieceImpl.WPAWN:
                return Factory.getFactory().getPiece(Color.WHITE, Piece.Type.PAWN);
            case PieceImpl.BPAWN:
                return Factory.getFactory().getPiece(Color.BLACK, Piece.Type.PAWN);
        }

        return Factory.getFactory().getPiece(Color.WHITE, Piece.Type.NONE);
    }

    public static MoveImpl convertMove(Move move){
        int from = getSquareRealIndex(move.getSource().getFile(),move.getSource().getRank());
        int to = getSquareRealIndex(move.getDestination().getFile(),move.getDestination().getRank());
        int promotionPiece = convertPiece(move.getPromotionPiece());
        return  new MoveImpl(from,to,promotionPiece);
    }

    public static Square getSquare(int index) {
        char f =  (char)(SquareImpl.getX(index)+97);
        int r =  SquareImpl.getY(index);
        return Factory.getFactory().getSquare(f,r);
    }

    public static int getSquareRealIndex(char file , int rank){

        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x < 8; x++) {
                int index = PositionImpl.getSquare(x, y);
                char f =  (char)(SquareImpl.getX(index)+97);
                int r =  SquareImpl.getY(index);

                if( (f == file) && (r == rank) ){
                    return index;
                }
            }
        }

        return -1;
    }
}
