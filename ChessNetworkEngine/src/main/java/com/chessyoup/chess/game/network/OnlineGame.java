package com.chessyoup.chess.game.network;

import com.chessyoup.chess.game.Player;
import com.chessyoup.chess.game.impl.GameImpl;
import com.chessyoup.chess.model.Chessboard;
import com.chessyoup.chess.model.Color;
import com.chessyoup.chess.model.Factory;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by leo on 29.10.2014.
 */
public class OnlineGame extends GameImpl implements Connection.ConnectionListener {

    private static final Logger LOG = Logger.getLogger(OnlineGame.class.toString());

    private OnlineGameConfig config;

    public OnlineGame(OnlineGameConfig config) {
        super(config.gameId, Factory.getFactory().createChessboard());
        this.config = config;
        config.connection.addConnectionListener(this);
    }

    public OnlineGameConfig getConfig(){
        return this.config;
    }

    @Override
    public void onMessage(Player player, byte[] message) {
        LOG.log(Level.FINE," onMessage from "+player+" , size :"+message.length);
    }

    @Override
    public void onJoin(Player player) {
        LOG.log(Level.FINE," onJoin "+player);
        this.config.remotePlayer = player;
    }

    @Override
    public void onLeft(Player player) {
        LOG.log(Level.FINE," onLeft "+player);

        if( getState() == STATE.IN_PROGRESS){
            //TODO
        }
    }

    @Override
    public void onConnected() {
        LOG.log(Level.FINE," onConnected ");
    }

    @Override
    public void onDisconnected() {
        LOG.log(Level.FINE," onDisconnected ");
    }

    public void gameReady(){
        Player whitePlayer = config.ownerColor == Color.WHITE ? config.owner : config.remotePlayer ;
        Player blackPlayer = config.ownerColor == Color.BLACK ? config.owner : config.remotePlayer ;
        this.setup(whitePlayer,blackPlayer,config.timeCtrl );
    }
}
