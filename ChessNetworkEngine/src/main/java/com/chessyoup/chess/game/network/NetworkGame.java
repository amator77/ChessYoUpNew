package com.chessyoup.chess.game.network;

import com.chessyoup.chess.game.Game;
import com.chessyoup.chess.game.Player;
import com.chessyoup.chess.model.Move;

public interface NetworkGame {
	
	public interface NetworkGameListener {
		
		public void onRemotePlayerConnected();
		
		public void onRemotePlayerDisconect();
		
		public void onReadyRecevied();

		public void onMoveRecevied(Move move);

		public void onOfferReceived();

		public void onChatReceived(String chatMessage);

		public void onResignRecevied();

		public void onFlagRecevied();

		public void onExitRecevied();
	}
	
	public Player getLocalPlayer();
	
	public Player getRemotePlayer();
	
	public boolean isRemotePlayerConnected();
	
	public Game getChessGame();
	
	public Transport getTransport();
	
	public void addNetworkGameListener(NetworkGameListener listener);
	
	public void removeNetworkGameListener(NetworkGameListener listener);
}
