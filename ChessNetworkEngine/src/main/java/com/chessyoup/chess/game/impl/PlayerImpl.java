package com.chessyoup.chess.game.impl;

import com.chessyoup.chess.game.Player;
import com.chessyoup.chess.game.Rating;
import com.chessyoup.chess.model.Move;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by leo on 27.10.2014.
 */
public abstract class PlayerImpl implements Player {

    private String id;

    private Rating bullet, blitz, standard;

    private List<PlayerListener> listeners;

    public PlayerImpl(String playerId) {
        this.id = playerId;
        this.blitz = new Rating(Rating.TYPE.BLITZ);
        this.bullet = new Rating(Rating.TYPE.BULLET);
        this.standard = new Rating(Rating.TYPE.STANDARD);
        this.listeners = new ArrayList<PlayerListener>();
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public Rating getRating(Rating.TYPE type) {

        switch (type) {
            case BULLET:
                return bullet;
            case BLITZ:
                return blitz;
            case STANDARD:
                return standard;
        }

        return blitz;
    }

    @Override
    public void updateRating(Rating.TYPE type, double newRating) {

        //TODO add the rest of the values

        switch (type) {
            case BULLET:
                this.bullet.setValue(newRating);
                break;
            case BLITZ:
                this.blitz.setValue(newRating);
                break;
            case STANDARD:
                this.standard.setValue(newRating);
                break;
        }
    }

    @Override
    public void addListener(PlayerListener listener) {
        if( !this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    @Override
    public void removeListener(PlayerListener listener) {
        if( this.listeners.contains(listener)) {
            this.listeners.remove(listener);
        }
    }

    protected void fireMoveEvent(Move move,String gameId){
        for(PlayerListener listener : this.listeners){
            listener.onMove(move,gameId);
        }
    }

    /**
     * External layers ( UI or Transport ) should call this on move made by the player.
     * @param move - the move
     * @param gameId - the game id
     */
    public abstract void moveMade(Move move,String gameId);
}
