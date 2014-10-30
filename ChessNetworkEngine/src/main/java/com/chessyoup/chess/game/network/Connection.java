package com.chessyoup.chess.game.network;

import com.chessyoup.chess.game.Player;
import com.chessyoup.chess.game.network.exceptions.NetworkException;

/**
 * Created by leo on 29.10.2014.
 */
public interface Connection {


    public interface ConnectionListener{

        public void onMessage(Player player,byte[] message);

        public void onRemoteConnected(Player player);

        public void onRemoteDisconnected(Player player);

        public void onConnected();

        public void onDisconnected();
    }

    public boolean isConnected();

    public void open() throws NetworkException;

    public void close() throws NetworkException;

    public void sendMessage(Player player , byte[] data) throws NetworkException;

    public void addConnectionListener(ConnectionListener listener);

    public void removeConnectionListener(ConnectionListener listener);
}
