package com.chessyoup.chess.model;

import com.chessyoup.chess.model.exception.IllegalMoveException;

/**
 * Created by leo on 17.10.2014.
 */
public interface Chessboard {

    public enum MODE { PLAY , ANALYSIS }

    public interface ChessboardListener {

        public void onChange(Chessboard source);

        public void onResult(Chessboard source);
    }

    /**
     *
     * @return
     */
    public MODE getMode();

    /**
     * Update clock time for the color.
     * @param color - color to update
     * @param time - time in milliseconds
     */
    public void updateClockTime(Color color , long time , long increment);

    /**
     * Get remaining time for the color.
     * @param color
     * @return
     */
    public long getClockTime(Color color);

    /**
     * Set chessboard mode.
     * @param mode
     */
    public void setMode(MODE mode);

    /**
     * Get the chess tree for this chessboard.
     * @return
     */
    public Tree getMovesTree();

    /**
     *
     * @return
     */
    public Square getSquare(char file , int rank);

    /**
     * Get current position on the chessboard
     * @return
     */
    public Position getPosition();

    /**
     * Set this result for the selected node in tree/
     */
    public void setResult(Result result);

    /**
     * Apply this move on current position.
     * This will generate an event.
     * @param move - the move to apply
     * @throws IllegalMoveException - if this is an illegal move.
     */

    /**
     * Apply this move on current position , update clocks , and generate position change event
     * @param move - the move
     * @param moveTime - the move time. This will be extracted from side to move time.
     * @throws IllegalMoveException - if this move is illegal.
     */
    public void doMove(Move move , long moveTime) throws IllegalMoveException;

    /**
     * Apply this move on current position.
     * No event will be generated by this.
     * @param move
     * @param silent
     * @throws IllegalMoveException
     */


    /**
     * Apply this move on current position , update clocks , and generate position change event
     * @param move - the move
     * @param moveTime - the move time. This will be extracted from side to move time.
     * @param silent - if true , this will no generate an event
     * @throws IllegalMoveException
     */
    public void doMove(Move move,long moveTime,boolean silent) throws IllegalMoveException;

    /**
     * Undo last move.
     * This will generate an event.
     */
    public void undoMove();

    /**
     * Reset this chessboard to initial chess position using
     * FEN : rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1
     */
    public void reset();

    /**
     * Navigate to this Node in ANALYSIS mode.
     * This will trigger an event.
     * @param node - tree node to navigate , null for the start position ( root node )
     */
    public void goToNode(Node node);

    /**
     * Navigate 1 node forward from current node in ANALYSIS mode.
     * This will trigger an event.
     */
    public void goForward();

    /**
     * Navigate 1 node backward from current node in ANALYSIS mode.
     * This will trigger an event.
     */
    public void goBack();

    public void addChessboardListener(ChessboardListener listener);

    public void removeChessboardListener(ChessboardListener listener);
}
