package com.chessyoup.chess.game;

import com.chessyoup.chess.model.Chessboard;
import com.chessyoup.chess.model.Result;

public interface Game {

    public enum STATE {
        UNKNOWN , READY, IN_PROGRESS, FINISHED , ABORTED
    }

    public enum OFFER {
        DRAW, ABORT, REMATCH
    }

    public interface GameListener{
        public void onStateChange(Game source,STATE oldState,STATE newState);
    }

    /**
     * Game unique id.
     * @return
     */
    public String getId();

    /**
     * The chessboard chessboard
      * @return
     */
    public Chessboard getChessboard();

    /**
     * The chessboard white player
     * @return
     */
    public Player getWhitePlayer();

    /**
     * The chessboard black player
     * @return
     */
    public Player getBlackPlayer();

    /**
     * Get game result.
     * @return - the result
     */
    public Result getResult();

    /**
     * Last chessboard OFFER from the player to move.
     * @return
     */
    public OFFER getLastOffer();

    /**
     * Get the current state of the chessboard.
     * Initial state of the chessboard after creation is UNKNOWN.
     * @return
     */
    public STATE getState();

    /**
     * Setup things for an new chessboard.The state will be UNKNOWN.
     * The chessboard will be on initial position, etc.
     * @param whitePlayer - the white player
     * @param blackPlayer - the blakp layer
     * @param timeControll - time control for this chessboard.
     */
    public void setup(Player whitePlayer,Player blackPlayer,TimeCtrl timeControll);

    /**
     *
     * @param listener
     */
    public void addGameListener(GameListener listener);

    /**
     *
     * @param listener
     */
    public void removeGameListener(GameListener listener);
}
