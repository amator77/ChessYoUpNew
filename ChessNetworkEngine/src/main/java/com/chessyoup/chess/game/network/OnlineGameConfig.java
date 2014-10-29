package com.chessyoup.chess.game.network;

import com.chessyoup.chess.game.Player;
import com.chessyoup.chess.game.TimeCtrl;
import com.chessyoup.chess.model.Color;

/**
 * Created by leo on 29.10.2014.
 */
public class OnlineGameConfig {

    Connection connection;

    String gameId;

    Player localPlayer;

    Player remotePlayer;

    Player owner;

    Color ownerColor;

    TimeCtrl timeCtrl;

    public OnlineGameConfig setConnection(Connection connection){
        this.connection = connection;
        return this;
    }

    public OnlineGameConfig setGameId(String gameId){
        this.gameId = gameId;
        return this;
    }

    public OnlineGameConfig setOwner(Player owner){
        this.owner = owner;
        return this;
    }

    public OnlineGameConfig setLocalPlayer(Player localPlayer){
        this.localPlayer = localPlayer;
        return this;
    }

    public OnlineGameConfig setRemotePlayer(Player remotePlayer){
        this.remotePlayer = remotePlayer;
        return this;
    }

    public OnlineGameConfig setOwnerColor(Color ownerColor){
        this.ownerColor = ownerColor;
        return this;
    }

    public OnlineGameConfig setTimeCtrl(TimeCtrl timeCtrl){
        this.timeCtrl = timeCtrl;
        return this;
    }


}
