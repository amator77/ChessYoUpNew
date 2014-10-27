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


public class GameImpl implements Game, Player.PlayerListener {

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
        return this.getChessboard();
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
        this.chessboard.reset();
    }

    @Override
    public Service getService() {
        return null;
    }

    @Override
    public void onMove(Move move, String gameId) {
        if (this.gameId == gameId) {

            try {
                // TODO get move time from clock
                this.chessboard.doMove(move,1000);
            } catch (IllegalMoveException e) {
                // TODO handle this exception
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onOffer(OFFER offer, String gameId) {
        if (this.gameId == gameId) {
            this.lastOffer = offer;

            // TODO handle this
        }
    }

    @Override
    public void onResign(String gameId) {
        if (this.gameId == gameId) {
            this.handleResign();
        }
    }

    @Override
    public void onExit(String gameId) {
        if (this.gameId == gameId) {
            this.handleResign();
        }
    }

    @Override
    public void onFlag(String gameId) {
        if (this.gameId == gameId) {
            this.handleResign(Result.REASON.FLAG);
        }
    }

    @Override
    public void onChat(String gameId, String message) {
        if (this.gameId == gameId) {
            // TODO send this message to UI layer
        }
    }

    private void handleResign(Result.REASON reason){
        Color sideToMove = this.chessboard.getPosition().getActiveColor();
        this.chessboard.setResult(new Result(sideToMove == Color.WHITE ? Result.VALUE.BLACK_WIN : Result.VALUE.BLACK_WIN, reason));
    }

    private void handleResign(){
        this.handleResign(Result.REASON.RESIGN);
    }
}
