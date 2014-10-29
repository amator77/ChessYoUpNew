package com.chessyoup.chess.game.impl;

import com.chessyoup.chess.Util;
import com.chessyoup.chess.game.Game;
import com.chessyoup.chess.game.Player;
import com.chessyoup.chess.game.Rating;
import com.chessyoup.chess.game.Service;
import com.chessyoup.chess.game.TimeCtrl;
import com.chessyoup.chess.model.Chessboard;
import com.chessyoup.chess.model.Color;
import com.chessyoup.chess.model.Move;
import com.chessyoup.chess.model.Result;
import com.chessyoup.chess.model.exception.IllegalMoveException;
import com.chessyoup.chess.model.impl.PositionImpl;
import com.chessyoup.chess.model.impl.TextIO;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


public class GameImpl implements Game, Player.PlayerListener ,Chessboard.ChessboardListener {

    private static final Logger LOG = Logger.getLogger(GameImpl.class.getName());

    public String gameId;

    public Chessboard chessboard;

    public Player whitePlayer;

    public Player blackPlayer;

    public OFFER lastOffer;

    public STATE state;

    private List<GameListener> listeners;

    private TimeCtrl timeCtrl;

    public GameImpl(String gameId, Chessboard chessboard) {
        this.gameId = gameId;
        this.chessboard = chessboard;
        this.whitePlayer = null;
        this.blackPlayer = null;
        this.lastOffer = null;
        this.listeners = new ArrayList<GameListener>();
        this.state = STATE.UNKNOWN;
        this.chessboard.addChessboardListener(this);
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
        this.timeCtrl = timeControll;
        this.chessboard.updateClockTime(Color.WHITE, timeControll.getTime(), timeControll.getIncrement());
        this.chessboard.updateClockTime(Color.BLACK, timeControll.getTime(), timeControll.getIncrement());
        this.chessboard.reset();
        this.setState(STATE.READY);
    }

    @Override
    public Service getService() {
        return null;
    }

    @Override
    public void onMove(Player source, String gameId, Move move, long moveTime) {
        LOG.log(Level.FINE, "Player :" + source.getId() + " move :" + move.toString() + " , move time :" + moveTime + " , gameId :" + gameId);

        if (this.gameId == gameId) {

            try {
                this.chessboard.doMove(move, moveTime);

                if( this.state == STATE.UNKNOWN){
                    this.setState(STATE.IN_PROGRESS);
                }
            } catch (IllegalMoveException e) {
                // TODO handle this exception
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onOffer(Player source, OFFER offer, String gameId) {
        if (this.gameId == gameId) {
            this.lastOffer = offer;

            // TODO handle this
        }
    }

    @Override
    public void onResign(Player source, String gameId) {
        if (this.gameId == gameId) {
            this.handleResign(source);
        }
    }

    @Override
    public void onExit(Player source, String gameId) {
        if (this.gameId == gameId) {
            this.handleResign(source);
        }
    }

    @Override
    public void onFlag(Player source, String gameId) {
        if (this.gameId == gameId) {
            this.handleResign(source, Result.REASON.FLAG);
        }
    }

    @Override
    public void onChat(Player source, String gameId, String message) {
        if (this.gameId == gameId) {
            // TODO send this message to UI layer
        }
    }

    private void handleResign(Player source) {
        this.handleResign(source, Result.REASON.RESIGN);
    }

    private void handleResign(Player source, Result.REASON reason) {
        Color sideToMove = this.chessboard.getPosition().getActiveColor();
        this.chessboard.setResult(new Result(sideToMove == Color.WHITE ? Result.VALUE.BLACK_WIN : Result.VALUE.BLACK_WIN, reason));
    }

    @Override
    public void onChange(Chessboard source) {
        LOG.log(Level.FINE,"chessboard onChange event");
        print();
    }

    @Override
    public void onResult(Chessboard source) {
        LOG.log(Level.FINE,"chessboard onResult event");
        Rating.TYPE type = Util.getGameType(timeCtrl);

        switch (getResult().getValue())
        {
            case WHITE_WIN:
            {
                Util.updateRatingsOnResult(this.whitePlayer,this.blackPlayer, type);
            }
            break;
            case BLACK_WIN:
            {
                Util.updateRatingsOnResult(this.blackPlayer,this.whitePlayer,type);
            }
            break;
            case DRAW:
            {
                Util.updateRatingsOnDraw(this.whitePlayer, this.blackPlayer, type);
            }
            break;
        }

        this.setState(STATE.FINISHED);
        print();
    }

    @Override
    public void addGameListener(GameListener listener){
        if(!this.listeners.contains(listener)){
            this.listeners.add(listener);
        }
    }

    @Override
    public void removeGameListener(GameListener listener){
        if(this.listeners.contains(listener)){
            this.listeners.remove(listener);
        }
    }

    protected void setState(STATE newState){
        STATE oldState = state;
        this.state = newState;
        this.fireStateChangeEvent(this,oldState,newState);
    }

    protected void fireStateChangeEvent(Game source, STATE oldState,STATE newState){
        for(GameListener listener : listeners){
            listener.onStateChange(this,oldState,newState);
        }
    }

    private void print(){
        System.out.println(this.whitePlayer);
        System.out.println(this.blackPlayer);
        System.out.println(TextIO.asciiBoard((PositionImpl) this.chessboard.getPosition()));
        System.out.println(this.getChessboard().getMovesTree().toString());
        System.out.println("Side move: " + this.getChessboard().getPosition().getActiveColor() + " ,move nr :" + this.getChessboard().getPosition().getFullMoveNumber());
        System.out.println("White time: "+this.getChessboard().getClockTime(Color.WHITE));
        System.out.println("Black time: "+this.getChessboard().getClockTime(Color.BLACK));
        System.out.println("Result :"+getResult());
    }
}
