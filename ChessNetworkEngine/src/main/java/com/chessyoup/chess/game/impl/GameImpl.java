package com.chessyoup.chess.game.impl;

import com.chessyoup.chess.game.Game;
import com.chessyoup.chess.game.Clock;
import com.chessyoup.chess.game.Service;
import com.chessyoup.chess.game.Player;
import com.chessyoup.chess.game.TimeCtrl;
import com.chessyoup.chess.game.ui.UI;
import com.chessyoup.chess.model.Chessboard;
import com.chessyoup.chess.model.Color;
import com.chessyoup.chess.model.Move;
import com.chessyoup.chess.model.Result;
import com.chessyoup.chess.model.exception.IllegalMoveException;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GameImpl implements Game, Player.PlayerListener {

    private static final Logger LOG = Logger.getLogger(GameImpl.class.getName());

    public String gameId;

    public Chessboard chessboard;

    public Player whitePlayer;

    public Player blackPlayer;

    public OFFER lastOffer;

    public STATE state;

    private List<GameListener> listeners;

    public GameImpl(String gameId, Chessboard chessboard) {
        this.gameId = gameId;
        this.chessboard = chessboard;
        this.whitePlayer = null;
        this.blackPlayer = null;
        this.lastOffer = null;
        this.listeners = new ArrayList<GameListener>();
        this.state = STATE.UNKNOWN;
    }

    @Override
    public String getId() {
        return this.gameId;
    }

    @Override
    public Chessboard getChessboard() {
        return this.chessboard;
    }

    @Override
    public Player getWhitePlayer() {
        return this.whitePlayer;
    }

    @Override
    public Player getBlackPlayer() {
        return this.blackPlayer;
    }

    @Override
    public Result getResult() {
        return this.chessboard.getMovesTree().getSelectedNode().getResult();
    }

    @Override
    public OFFER getLastOffer() {
        return this.lastOffer;
    }

    @Override
    public STATE getState() {
        return this.state;
    }

    @Override
    public void setup(Player whitePlayer, Player blackPlayer, TimeCtrl timeControll) {

        if (this.whitePlayer != null) {
            this.whitePlayer.removeListener(this);
        }

        if (this.blackPlayer != null) {
            this.blackPlayer.removeListener(this);
        }

        this.whitePlayer = whitePlayer;
        this.whitePlayer.addListener(this);
        this.blackPlayer = blackPlayer;
        this.blackPlayer.addListener(this);
        this.chessboard.updateClockTime(Color.WHITE,timeControll.getTime(),timeControll.getIncrement());
        this.chessboard.updateClockTime(Color.BLACK,timeControll.getTime(),timeControll.getIncrement());
        this.chessboard.reset();
    }

    @Override
    public Service getService() {
        return null;
    }

    @Override
    public void onMove(Player source,String gameId,Move move,long moveTime) {
        LOG.log(Level.FINE,"Player :"+source.getId()+" move :"+move.toString() +" , move time :"+moveTime+" , gameId :"+gameId);

        if (this.gameId == gameId) {

            try {
                this.chessboard.doMove(move,moveTime);
            } catch (IllegalMoveException e) {
                // TODO handle this exception
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onOffer(Player source,OFFER offer, String gameId) {
        if (this.gameId == gameId) {
            this.lastOffer = offer;

            // TODO handle this
        }
    }

    @Override
    public void onResign(Player source,String gameId) {
        if (this.gameId == gameId) {
            this.handleResign(source);
        }
    }

    @Override
    public void onExit(Player source,String gameId) {
        if (this.gameId == gameId) {
            this.handleResign(source);
        }
    }

    @Override
    public void onFlag(Player source,String gameId) {
        if (this.gameId == gameId) {
            this.handleResign(source,Result.REASON.FLAG);
        }
    }

    @Override
    public void onChat(Player source,String gameId, String message) {
        if (this.gameId == gameId) {
            // TODO send this message to UI layer
        }
    }

    private void handleResign(Player source,Result.REASON reason){
        Color sideToMove = this.chessboard.getPosition().getActiveColor();
        this.chessboard.setResult(new Result(sideToMove == Color.WHITE ? Result.VALUE.BLACK_WIN : Result.VALUE.BLACK_WIN, reason));
    }

    private void handleResign(Player source){
        this.handleResign(source,Result.REASON.RESIGN);
    }
}
