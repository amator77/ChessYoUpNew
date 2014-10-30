package com.chessyoup.chess.game.impl;

import com.chessyoup.chess.game.Clock;
import com.chessyoup.chess.game.Player;
import com.chessyoup.chess.game.ui.UI;
import com.chessyoup.service.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by leo on 30.10.2014.
 */
public class ServiceImpl implements Service {

    private Map<String, Player> players;

    public ServiceImpl() {
        players = new HashMap<String, Player>();
    }

    @Override
    public Clock getClock() {
        return null;
    }

    @Override
    public UI getUI() {
        return null;
    }

    @Override
    public Player findPlayerById(String playerId) {
        return players.get(playerId);
    }

    @Override
    public void registerPlayer(Player player) {
        if (!this.players.containsKey(player.getId())) {
            this.players.put(player.getId(), player);
        }
    }

    @Override
    public void removePlayer(Player player) {
        if (this.players.containsKey(player.getId())) {
            this.players.remove(player.getId());
        }
    }

    @Override
    public void handleException(Exception e) {

    }
}
