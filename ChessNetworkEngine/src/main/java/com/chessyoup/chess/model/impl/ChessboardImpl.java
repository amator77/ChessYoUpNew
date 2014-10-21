package com.chessyoup.chess.model.impl;

import com.chessyoup.chess.model.Chessboard;
import com.chessyoup.chess.model.Factory;
import com.chessyoup.chess.model.Move;
import com.chessyoup.chess.model.Node;
import com.chessyoup.chess.model.Square;
import com.chessyoup.chess.model.Tree;
import com.chessyoup.chess.model.exception.ChessParseError;
import com.chessyoup.chess.model.exception.IllegalMoveException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leo on 17.10.2014.
 */
public class ChessboardImpl implements Chessboard {

    private Tree tree;

    private PositionImpl position;

    private List<ChessboardListener> listeners;

    public ChessboardImpl() {
        this.tree = new TreeImpl(new NodeImpl());

        try {
            this.position = new PositionImpl(TextIO.readFEN(TextIO.startPosFEN));
        } catch (ChessParseError chessParseError) {
            chessParseError.printStackTrace();
        }

        this.listeners = new ArrayList<ChessboardListener>();
    }

    public void addChessboardListener(ChessboardListener listener){
        if( !this.listeners.contains(listener)){
            this.listeners.add(listener);
        }
    }

    public void removeChessboardListener(ChessboardListener listener){
        if( this.listeners.contains(listener)){
            this.listeners.remove(listener);
        }
    }

    @Override
    public Tree getMovesTree() {
        return this.tree;
    }

    /**
     * @return
     */
    public Square getSquare(char file, int rank) {
        return Factory.getFactory().getSquare(file, rank);
    }

    @Override
    public PositionImpl getPosition() {
        return this.position;
    }

    @Override
    public void doMove(Move move) throws IllegalMoveException {
        this.doMove(move,false);
    }

    @Override
    public void doMove(Move move, boolean silent) throws IllegalMoveException {

        this.position.makeMove(Util.convertMove(move), new UndoInfo());

        if( !silent ){
            fireChangeEvent();
        }
    }

    @Override
    public void undoMove() {
        //TODO
    }

    @Override
    public void reset() {
        this.tree = new TreeImpl(new NodeImpl());

        try {
            this.position = new PositionImpl(TextIO.readFEN(TextIO.startPosFEN));
        } catch (ChessParseError chessParseError) {
            chessParseError.printStackTrace();
        }

        fireChangeEvent();
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

    private void fireChangeEvent(){
        for(ChessboardListener listener : listeners){
            listener.onChange(this);
        }
    }

    public static void main(String[] args) throws IllegalMoveException {
        ChessboardImpl impl =  new ChessboardImpl();
        System.out.println( TextIO.asciiBoard(impl.getPosition()));
        System.out.println("color to move"+ impl.getPosition().getActiveColor() +" ,move nr :"+impl.getPosition().getFullMoveNumber());
        impl.doMove( TextIO.stringToMove(impl.getPosition(),"e2e4") );
        System.out.println( TextIO.asciiBoard(impl.getPosition()));
        System.out.println("color to move"+ impl.getPosition().getActiveColor() +" ,move nr :"+impl.getPosition().getFullMoveNumber());
        impl.doMove( TextIO.stringToMove(impl.getPosition(), "e7e5") );
        System.out.println( TextIO.asciiBoard(impl.getPosition()));
        System.out.println("color to move"+ impl.getPosition().getActiveColor() +" ,move nr :"+impl.getPosition().getFullMoveNumber());
    }
}
