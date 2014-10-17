package com.chessyoup.chess.model.impl;

import com.chessyoup.chess.model.Chessboard;
import com.chessyoup.chess.model.Color;
import com.chessyoup.chess.model.Factory;
import com.chessyoup.chess.model.Move;
import com.chessyoup.chess.model.Node;
import com.chessyoup.chess.model.Position;
import com.chessyoup.chess.model.Square;
import com.chessyoup.chess.model.Tree;
import com.chessyoup.chess.model.exception.IllegalMoveException;

/**
 * Created by leo on 17.10.2014.
 */
public class ChessboardImpl implements Chessboard {

    private Tree tree;

    private static final Square[][] squares = new Square[8][8];

    static {
        int c = 1;
        Color squareColor = Color.BLACK;

        for (int f = 97; f <= 104; f++) {
            for (int r = 1; r <= 8; r++) {
                squares[7 - (104 - f)][8 - r] = Factory.getFactory().getSquare(squareColor, (char) f, r);
                squareColor =  (squareColor == Color.BLACK) ? Color.WHITE : Color.BLACK;

                if( c++ % 8 == 0  ){
                    squareColor =  (squareColor == Color.BLACK) ? Color.WHITE : Color.BLACK;
                }
            }
        }
    }

    public ChessboardImpl() {
        this.tree = new TreeImpl(new NodeImpl(Factory.getFactory().getStartPosition()));
    }

    @Override
    public Tree getTree() {
        return this.tree;
    }

    @Override
    public Square[][] getSquares() {
        return ChessboardImpl.squares;
    }

    @Override
    public Position getPosition() {
        return this.tree.getSelectedNode().getPosition();
    }

    @Override
    public void doMove(Move move) throws IllegalMoveException {
        //TODO
    }

    @Override
    public void doMove(Move move, boolean silent) throws IllegalMoveException {
        //TODO
    }

    @Override
    public void undoMove() {
        //TODO
    }

    @Override
    public void reset() {
        //TODO
    }

    @Override
    public void goToNode(Node node) {
        //TODO
    }

    @Override
    public void goForward() {
        //TODO
    }

    @Override
    public void goBack() {
        //TODO
    }

    public static void main(String[] args) {
        new ChessboardImpl();
    }
}
