package com.chessyoup.chess.model;

import com.chessyoup.chess.model.impl.ChessboardImpl;
import com.chessyoup.chess.model.impl.PieceImpl;
import com.chessyoup.chess.model.impl.SquareImpl;
import com.chessyoup.chess.model.impl.Util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by leo on 17.10.2014.
 */
public class Factory {

    public static final Factory instance = new Factory();

    public static final Factory getFactory(){
        return Factory.instance;
    }

    Map<String,Square> squares;

    private Factory(){
        this.squares = new HashMap<String, Square>();
    }

    public Piece getPiece(Color color , Piece.Type type ){
        return new PieceImpl(color,type);
    }

    public Square getSquare(char file , int rank){
        String key = file+""+rank;
        Square s = squares.get(key);

        if( s == null ){
            s = new SquareImpl(file,rank);
            squares.put(key,s);
        }

        return s;
    }

    public Chessboard createChessboard(){
        return new ChessboardImpl();
    }

    public Move uciStringToMove(String uciString){
        return Util.UCIstringToMove(uciString);
    }
}

