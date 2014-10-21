package com.chessyoup.chess.model;

import com.chessyoup.chess.model.impl.MoveImpl;
import com.chessyoup.chess.model.impl.PieceImpl;
import com.chessyoup.chess.model.impl.PositionImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leo on 17.10.2014.
 */
public class Factory {

    public static final Factory instance = new Factory();

    public static final Factory getFactory(){
        return Factory.instance;
    }

    public Square squares[];

    private Factory(){
        this.squares = new Square[64];

        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x < 8; x++) {
                int index = PositionImpl.getSquare(x, y);
                this.squares[index] = new Square(index);
            }
        }
    }

    public Piece getPiece(Color color , Piece.PieceType type ){
        return new PieceImpl(color,type);
    }

    public Square getSquare(char file , int rank){
        return this.squares[Square.getSquare((104 - file),rank-1)];
    }

    public Square getSquare(int index){
        return this.squares[index];
    }

    public Move createMove(Square source,Square destination,Piece promotionPiece){
        return new MoveImpl(source.getIndex(),destination.getIndex(),convertPiece(promotionPiece));
    }

    public Move createMove(Square source,Square destination){
        return createMove(source, destination, null);
    }

    public Position getStartPosition(){
        return null;
    }


    public int convertPiece(Piece piece){
        switch (piece.getType()){
            case BISHOP: return piece.getColor() == Color.WHITE ? PieceImpl.WBISHOP : PieceImpl.BBISHOP;
            case ROOK: return piece.getColor() == Color.WHITE ? PieceImpl.WROOK : PieceImpl.BROOK;
            case KNIGHT: return piece.getColor() == Color.WHITE ? PieceImpl.WKNIGHT : PieceImpl.BKNIGHT;
            case KING: return piece.getColor() == Color.WHITE ? PieceImpl.WKING : PieceImpl.BKING;
            case QUEEN: return piece.getColor() == Color.WHITE ? PieceImpl.WQUEEN : PieceImpl.BQUEEN;
            case PAWN: return piece.getColor() == Color.WHITE ? PieceImpl.WPAWN : PieceImpl.BPAWN;
        }

        return PieceImpl.EMPTY;
    }

    public Piece convertPiece(int piece){
        switch (piece){
            case PieceImpl.WBISHOP : return getPiece(Color.WHITE, Piece.PieceType.BISHOP);
            case PieceImpl.BBISHOP : return getPiece(Color.BLACK, Piece.PieceType.BISHOP);
            case PieceImpl.WROOK : return getPiece(Color.WHITE, Piece.PieceType.ROOK);
            case PieceImpl.BROOK : return getPiece(Color.BLACK, Piece.PieceType.ROOK);
            case PieceImpl.WKNIGHT : return getPiece(Color.WHITE, Piece.PieceType.KNIGHT);
            case PieceImpl.BKNIGHT : return getPiece(Color.BLACK, Piece.PieceType.KNIGHT);
            case PieceImpl.WKING : return getPiece(Color.WHITE, Piece.PieceType.KING);
            case PieceImpl.BKING : return getPiece(Color.BLACK, Piece.PieceType.KING);
            case PieceImpl.WQUEEN : return getPiece(Color.WHITE, Piece.PieceType.QUEEN);
            case PieceImpl.BQUEEN : return getPiece(Color.BLACK, Piece.PieceType.QUEEN);
        }

        return null;
    }
}

