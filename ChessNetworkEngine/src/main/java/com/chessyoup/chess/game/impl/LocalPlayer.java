package com.chessyoup.chess.game.impl;

import com.chessyoup.chess.model.Move;

/**
 * Created by leo on 27.10.2014.
 */
public class LocalPlayer extends PlayerImpl {

    public LocalPlayer(String playerId) {
        super(playerId);
    }

    @Override
    public void moveMade(Move move, String gameId) {
        this.fireMoveEvent(move,gameId);
    }
}
