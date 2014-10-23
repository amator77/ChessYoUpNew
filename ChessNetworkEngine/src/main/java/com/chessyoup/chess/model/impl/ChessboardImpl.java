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

    private MODE mode;

    public ChessboardImpl() {
        this.tree = new TreeImpl(new NodeImpl());

        try {
            this.position = new PositionImpl(TextIO.readFEN(TextIO.startPosFEN));
        } catch (ChessParseError chessParseError) {
            chessParseError.printStackTrace();
        }

        this.listeners = new ArrayList<ChessboardListener>();
        this.mode = MODE.PLAY;
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
    public MODE getMode() {
        return null;
    }

    @Override
    public void setMode(MODE mode) {
        this.mode = mode;
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

        if( this.mode == MODE.PLAY) {
            UndoInfo ui = new UndoInfo();
            this.position.makeMove(Util.convertMove(move), ui);
            this.tree.appendNode(new NodeImpl(this.tree.getSelectedNode(), move , ui));

            if (!silent) {
                fireChangeEvent();
            }
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
        if( this.mode == MODE.ANALYSIS) {


//            this.position.unMakeMove();
        }
    }

    @Override
    public void goForward() {

        if( this.mode == MODE.ANALYSIS) {
            Node selectedNode = this.tree.getSelectedNode();

            if(selectedNode !=  null && selectedNode.getChilds().size() > 0 ){
                for(Node child : selectedNode.getChilds()){
                    if( child.isMain() ){
                        tree.setSelectedNode(child);
                        this.position.makeMove(Util.convertMove(child.getMove()), ((NodeImpl) child).getUi());
                        this.fireChangeEvent();
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void goBack() {
        if( this.mode == MODE.ANALYSIS) {
            Node selectedNode = this.tree.getSelectedNode();

            if(selectedNode !=  null && selectedNode.getParent() != null ){
                NodeImpl parent = (NodeImpl)selectedNode.getParent();
                tree.setSelectedNode(parent);

                if( selectedNode.getParent().getMove() != null) {
                    this.position.unMakeMove(Util.convertMove(parent.getMove()), parent.getUi());
                    this.fireChangeEvent();
                }
            }
        }
    }

    private void fireChangeEvent(){
        for(ChessboardListener listener : listeners){
            listener.onChange(this);
        }
    }

    public static void main(String[] args) throws IllegalMoveException {
        final ChessboardImpl impl =  new ChessboardImpl();

        impl.addChessboardListener(new ChessboardListener() {
            @Override
            public void onChange(Chessboard source) {
                System.out.println( "On change event");
                System.out.println( TextIO.asciiBoard(impl.getPosition()));
                System.out.println( impl.getMovesTree().toString());
                System.out.println("color to move"+ impl.getPosition().getActiveColor() +" ,move nr :"+impl.getPosition().getFullMoveNumber());
            }

            @Override
            public void onResult(Chessboard source) {

            }
        });

        impl.doMove( TextIO.stringToMove(impl.getPosition(),"e2e4") );
        impl.doMove( TextIO.stringToMove(impl.getPosition(), "e7e5") );
        impl.doMove( TextIO.stringToMove(impl.getPosition(), "g1f3") );
        impl.doMove( TextIO.stringToMove(impl.getPosition(), "b8c6") );
        impl.doMove( TextIO.stringToMove(impl.getPosition(), "f1b5") );
        impl.doMove( TextIO.stringToMove(impl.getPosition(), "g8e7") );

        System.out.println(".....................");
        System.out.println(".....................");
        System.out.println(".....................");
        System.out.println(".....................");

//        impl.setMode(MODE.ANALYSIS);
//        impl.goBack();
//        impl.goBack();
//        impl.goBack();
//        impl.goBack();
//        impl.goBack();
//        impl.goBack();
//
//        System.out.println(".....................");
//        System.out.println(".....................");
//        System.out.println(".....................");
//        System.out.println(".....................");
//        impl.goForward();

        impl.reset();
    }
}
