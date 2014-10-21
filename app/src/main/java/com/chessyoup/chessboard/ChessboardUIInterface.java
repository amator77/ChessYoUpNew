package com.chessyoup.chessboard;

import java.util.ArrayList;

import com.chessyoup.model.MoveImpl;
import com.chessyoup.model.PositionImpl;

public interface ChessboardUIInterface {
    
    public void setPosition(PositionImpl pos, String variantInfo, ArrayList<MoveImpl> variantMoves);

    public void setSelection(int sq);

    /** Set the status text. */
    public void setStatus(ChessboardStatus status);

    /** Update the list of moves. */
    public void moveListUpdated();

    /** Ask what to promote a pawn to. Should call reportPromotePiece() when done. */
    public void requestPromotePiece();

    /** Run code on the GUI thread. */
    public void runOnUIThread(Runnable runnable);

    /** Report that user attempted to make an invalid move. */
    public void reportInvalidMove(MoveImpl m);

    /** Report remaining thinking time to GUI. */
    public void setRemainingTime(int wTime, int bTime, int nextUpdate);

    /** Report a move made that is a candidate for GUI animation. */
    public void setAnimMove(PositionImpl sourcePos, MoveImpl move, boolean forward);

    /** Get the default player name. */
    public String whitePlayerName();
    
    /** Get the default player name. */
    public String blackPlayerName();

    /** Return true if only main-line moves are to be kept. */
    public boolean discardVariations();
    
	public void localMoveMade(MoveImpl m);
}
