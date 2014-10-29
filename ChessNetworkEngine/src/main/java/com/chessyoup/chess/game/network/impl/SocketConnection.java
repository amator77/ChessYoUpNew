package com.chessyoup.chess.game.network.impl;

import com.chessyoup.chess.game.Player;
import com.chessyoup.chess.game.network.Connection;
import com.chessyoup.chess.game.network.exceptions.NetworkException;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by leo on 29.10.2014.
 */
public class SocketConnection implements Connection {

    private String ip;

    private int port;

    private Socket socket;

    private List<ConnectionListener> listeners;

    public SocketConnection(String ip,int port){
        this.ip = ip;
        this.port = port;
        this.listeners = new ArrayList<ConnectionListener>();
    }

    @Override
    public boolean isConnected() {
        return socket != null && this.socket.isConnected();
    }

    @Override
    public void open() throws NetworkException {
        if( !isConnected() ){
            try {
                this.socket = new Socket();
                this.socket.connect(new InetSocketAddress(InetAddress.getByName("ip"),port));
                this.fireOnConnectionEvent();
            } catch (IOException e) {
                throw new NetworkException("Exception on connecting to "+ip+":"+port,e);
            }
        }
    }

    @Override
    public void close() throws NetworkException{
        if( isConnected() ){
            try {
                this.socket.close();
            } catch (IOException e) {
                throw new NetworkException("Exception on disconnecting from "+ip+":"+port,e);
            }
        }
    }

    @Override
    public void sendMessage(Player playerId, byte[] data) throws NetworkException {
        if( isConnected() ){
            try {
                this.socket.getOutputStream().write(data);
            } catch (IOException e) {
                this.close();
                this.fireOnDisconnectionEvent();
                throw new NetworkException("Exception on writing to socket!",e);
            }
        }
    }

    public void addConnectionListener(ConnectionListener listener){

        if( !this.listeners.contains(listener)) {
            this.listeners.add(listener);
        }
    }

    public void removeConnectionListener(ConnectionListener listener){

        if( this.listeners.contains(listener)) {
            this.listeners.remove(listener);
        }
    }

    private void fireOnConnectionEvent(){
        for(ConnectionListener listener : listeners){
            listener.onConnected();
        }
    }

    private void fireOnDisconnectionEvent(){
        for(ConnectionListener listener : listeners){
            listener.onDisconnected();
        }
    }
}
