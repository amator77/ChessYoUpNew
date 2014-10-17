package com.chessyoup.chess.model;

import com.chessyoup.chess.model.impl.MoveImpl;
import com.chessyoup.chess.model.impl.PieceImpl;
import com.chessyoup.chess.model.impl.SquareImpl;

/**
 * Created by leo on 17.10.2014.
 */
public class Factory {

    public static final Factory instance = new Factory();

    public static final Factory getFactory(){
        return Factory.instance;
    }

    private Factory(){
        //TODO init caches
    }

    public Piece getPiece(Color color , Piece.PieceType type ){
        return new PieceImpl(color,type);
    }

    public Square getSquare(Color color , char file , int rank){
        return new SquareImpl(color,file,rank);
    }

    public Move getMove(Square source,Square destination,Piece.PieceType promotionPiece){
        return new MoveImpl(source,destination,promotionPiece);
    }

    public Move getMove(Square source,Square destination){
        return new MoveImpl(source,destination);
    }

    public Position getStartPosition(){
        return null;
    }
}

