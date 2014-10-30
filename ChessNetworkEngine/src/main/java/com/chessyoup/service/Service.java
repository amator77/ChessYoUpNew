package com.chessyoup.service;

import com.chessyoup.chess.game.Clock;
import com.chessyoup.chess.game.Player;
import com.chessyoup.chess.game.ui.UI;

/**
 * Created by leo on 28.10.2014.
 */
public interface Service {

    public Clock getClock();

    public UI getUI();

    public Player findPlayerById(String playerId);

    public void registerPlayer(Player player);

    public void removePlayer(Player player);

    public void handleException(Exception e);
}
