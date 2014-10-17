package com.chessyoup.chess.game.network.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.chessyoup.chess.game.Game;
import com.chessyoup.chess.game.Player;
import com.chessyoup.chess.game.impl.ChessGameImpl;
import com.chessyoup.chess.game.network.NetworkGame;
import com.chessyoup.chess.game.network.Transport;
import com.chessyoup.chess.game.network.Transport.TransportListener;
import com.chessyoup.chess.game.network.messages.IMessage;
import com.chessyoup.chess.model.Chessboard;

public class NetworkGameImpl extends ChessGameImpl implements NetworkGame,
		TransportListener {

	private static final Logger LOG = Logger.getLogger(NetworkGameImpl.class
			.getName());

	private Player localPlayer;

	private Player remotePlayer;

	private Transport transport;

	public List<NetworkGameListener> listeners;

	public NetworkGameImpl(String gameId, Player localPlayer,
			Transport transport) {
		super(gameId,null);
		this.localPlayer = localPlayer;
		this.transport = transport;
		this.transport.addTransportListener(this);
		this.listeners = new ArrayList<NetworkGame.NetworkGameListener>();
	}

	@Override
	public Player getLocalPlayer() {
		return this.localPlayer;
	}

	@Override
	public Player getRemotePlayer() {
		return this.remotePlayer;
	}

	@Override
	public Game getChessGame() {
		return this;
	}

	@Override
	public Transport getTransport() {
		return this.transport;
	}

	@Override
	public void addNetworkGameListener(NetworkGameListener listener) {

		if (!this.listeners.contains(listener)) {
			this.listeners.add(listener);
		}
	}

	@Override
	public void removeNetworkGameListener(NetworkGameListener listener) {
		if (this.listeners.contains(listener)) {
			this.listeners.remove(listener);
		}
	}
	
	@Override
	public boolean isRemotePlayerConnected() {
		return this.remotePlayer != null;
	}
	
	@Override
	public void onMessageReceived(IMessage message) {
		LOG.info("onMessageReceived :: " + message.toString());

		switch (message.getCommand()) {
		case CONNECT :
			this.handleConnectMessage(message);
			break;
//		case MOVE:
//			this.handleMoveMessage(message);
//			break;
//		case CHAT:
//			this.handleChatMessage(message);
//			break;
//		case DISCONNECT :
//			this.handleDisconnectMessage(message);			
//			break;
		default:
			break;
		}
	}
	
	private void handleConnectMessage(IMessage message) {
		this.remotePlayer = getPlayer(message.getPayload());
		
		for(NetworkGameListener listener : listeners){
			listener.onRemotePlayerConnected();
		}
	}
	
	private Player getPlayer(Map<String, String> data) {
		// TODO Auto-generated method stub
		return null;
	}

	private void handleChatMessage(IMessage message) {
		
		for(NetworkGameListener listener : listeners){
			listener.onChatReceived(message.toString());
		}
	}

    @Override
    public Chessboard getChessboard() {
        return null;
    }

//	private void handleMoveMessage(MoveMessage message) {
//		
//		for(NetworkGameListener listener : listeners){
//			listener.onMoveRecevied(message.getMove());
//		}
//	}
//	
//	private void handleDisconnectMessage(DisconnectMessage message) {
//		
//		this.remotePlayer = null;
//		
//		for(NetworkGameListener listener : listeners){
//			listener.onRemotePlayerDisconect();
//		}		
//	}		
}
