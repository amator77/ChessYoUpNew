package com.chessyoup.chess.game.network.impl;

import com.chessyoup.chess.game.Player;
import com.chessyoup.chess.game.network.Connection;
import com.chessyoup.chess.game.network.exceptions.NetworkException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by leo on 29.10.2014.
 */
public class SocketConnection implements Connection , Runnable {

    private String ip;

    private int port;

    private Socket socket;

    private List<ConnectionListener> listeners;

    private Thread readThread;

    private Player remotePlayer;

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
                this.socket.connect(new InetSocketAddress(InetAddress.getByName(ip),port));
                this.readThread = new Thread(this);
                this.readThread.start();
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

    public Player getRemotePlayer() {
        return remotePlayer;
    }

    public void setRemotePlayer(Player remotePlayer) {
        this.remotePlayer = remotePlayer;
    }

    @Override
    public void run() {

        if(isConnected()){

            try {

                InputStream stream = this.socket.getInputStream();
                byte[] buffer = new byte[1024];

                while(isConnected()){
                    Logger.getLogger(SocketConnection.class.toString()).log(Level.INFO,"Waiting for data...");
                    int read = stream.read(buffer);
                    Logger.getLogger(SocketConnection.class.toString()).log(Level.INFO,"new data from "+remotePlayer.getId()+" , size :"+read);
                    byte[] message = new byte[read];
                    System.arraycopy(buffer,0,message,0,read);

                    if( new String(message).equals("join")){
                        for( ConnectionListener listener : listeners){
                            listener.onRemoteConnected(this.remotePlayer);
                        }
                    }
                    else{
                        for( ConnectionListener listener : listeners){
                            listener.onMessage(remotePlayer,message);
                        }
                    }
                }

            } catch (IOException e) {
                Logger.getLogger(SocketConnection.class.toString()).log(Level.WARNING,"Socket exception.Closing the read thread.",e);
            }

        }

        Logger.getLogger(SocketConnection.class.toString()).log(Level.FINE,"Client thread is closing.");
    }
}
